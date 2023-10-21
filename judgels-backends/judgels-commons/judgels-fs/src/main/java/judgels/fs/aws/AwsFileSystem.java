package judgels.fs.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;
import judgels.fs.NaturalFilenameComparator;

public final class AwsFileSystem implements FileSystem {
    private final AmazonS3 s3;
    private final Optional<String> cloudFrontBaseUrl;
    private final String bucketName;

    private final Cache<String, String> privateFileUrlCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofHours(5))
            .build();

    public AwsFileSystem(AwsConfiguration config, AwsFsConfiguration fsConfig) {
        AWSCredentials creds = new BasicAWSCredentials(config.getAccessKey(), config.getSecretKey());
        this.s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(config.getS3BucketRegionId())
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .build();

        this.cloudFrontBaseUrl = config.getCloudFrontBaseUrl();

        this.bucketName = fsConfig.getS3BucketName();
        if (!s3.doesBucketExistV2(bucketName)) {
            s3.createBucket(new CreateBucketRequest(bucketName, Region.fromValue(config.getS3BucketRegionId())));
        }
    }

    @Override
    public void createDirectory(Path dirPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean directoryExists(Path dirPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createFile(Path filePath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeFile(Path filePath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getFile(Path filePath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void uploadPublicFile(Path filePath, InputStream content) {
        String destFilePathString = filePath.toString();

        ObjectMetadata objectMetadata = new ObjectMetadata();

        String contentType = URLConnection.guessContentTypeFromName(destFilePathString);
        if (contentType != null) {
            objectMetadata.setContentType(contentType);
            if (contentType.startsWith("image/")) {
                objectMetadata.setCacheControl("no-transform,public,max-age=300,s-maxage=900");
            }
        }

        s3.putObject(bucketName, destFilePathString, content, objectMetadata);
    }

    @Override
    public String getPublicFileUrl(Path filePath) {
        if (cloudFrontBaseUrl.isPresent()) {
            return cloudFrontBaseUrl.get() + Paths.get("/").resolve(filePath).toString();
        }
        return "https://" + bucketName + ".s3.amazonaws.com/" + filePath.toString();
    }

    @Override
    public void uploadPrivateFile(Path filePath, InputStream content) {
        uploadPublicFile(filePath, content);

        String destFilePathString = filePath.toString();
        s3.setObjectAcl(bucketName, destFilePathString, CannedAccessControlList.PublicRead);
    }

    @Override
    public String getPrivateFileUrl(Path filePath) {
        String key = filePath.toString();
        return privateFileUrlCache.get(key, $ -> {
            GeneratePresignedUrlRequest presignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key);
            presignedUrlRequest.setExpiration(Date.from(Instant.now().plus(Duration.ofHours(4))));
            return s3.generatePresignedUrl(presignedUrlRequest).toString();
        });
    }

    @Override
    public void uploadZippedFiles(Path dirPath, InputStream content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FileInfo> listDirectoriesInDirectory(Path dirPath) {
        String prefix = dirPath.toString();
        if (!prefix.isEmpty()) {
            prefix += File.separator;
        }

        ListObjectsV2Result result = s3.listObjectsV2(bucketName, prefix);

        Set<String> seenDirectoryNames = Sets.newHashSet();
        ImmutableList.Builder<FileInfo> fileInfos = ImmutableList.builder();
        for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
            String key = objectSummary.getKey().substring(prefix.length());
            if (key.endsWith(File.separator) || !key.contains(File.separator)) {
                continue;
            }
            key = key.substring(0, key.lastIndexOf(File.separator));
            if (key.contains(File.separator) || seenDirectoryNames.contains(key)) {
                continue;
            }

            seenDirectoryNames.add(key);
            fileInfos.add(new FileInfo.Builder()
                    .name(key)
                    .size(objectSummary.getSize())
                    .lastModifiedTime(objectSummary.getLastModified().toInstant())
                    .build());
        }
        return fileInfos.build();
    }

    @Override
    public List<FileInfo> listFilesInDirectory(Path dirPath) {
        String prefix = dirPath.toString();
        if (!prefix.isEmpty()) {
            prefix += File.separator;
        }

        ListObjectsV2Result result = s3.listObjectsV2(bucketName, prefix);

        List<FileInfo> fileInfos = Lists.newArrayList();
        for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
            String key = objectSummary.getKey().substring(prefix.length());
            if (key.contains(File.separator)) {
                continue;
            }

            fileInfos.add(new FileInfo.Builder()
                    .name(key)
                    .size(objectSummary.getSize())
                    .lastModifiedTime(objectSummary.getLastModified().toInstant())
                    .build());
        }

        Comparator<String> comparator = new NaturalFilenameComparator();
        fileInfos.sort((FileInfo f1, FileInfo f2) -> comparator.compare(f1.getName(), f2.getName()));
        return ImmutableList.copyOf(fileInfos);
    }

    @Override
    public void writeByteArrayToFile(Path filePath, byte[] content) {
        String key = filePath.toString();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(URLConnection.guessContentTypeFromName(key));

        s3.putObject(bucketName, key, new ByteArrayInputStream(content), objectMetadata);
    }

    @Override
    public byte[] readByteArrayFromFile(Path filePath) {
        try (S3Object object = s3.getObject(bucketName, filePath.toString())) {
            return ByteStreams.toByteArray(object.getObjectContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
