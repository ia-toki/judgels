package judgels.fs.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.SetObjectAclRequest;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import judgels.fs.FileSystem;

public final class AwsFileSystem implements FileSystem {
    private final AmazonS3 s3;
    private final Optional<String> cloudFrontBaseUrl;
    private final String bucketName;

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
    public void uploadPublicFile(InputStream file, Path destDirPath, String destFilename) {
        Path destFilePath = destDirPath.resolve(destFilename);
        String destFilePathString = destFilePath.toString();

        ObjectMetadata objectMetadata = new ObjectMetadata();

        String contentType = URLConnection.guessContentTypeFromName(destFilename);
        if (contentType != null) {
            objectMetadata.setContentType(contentType);
            if (contentType.startsWith("image/")) {
                objectMetadata.setCacheControl("no-transform,public,max-age=300,s-maxage=900");
            }
        }

        s3.putObject(new PutObjectRequest(bucketName, destFilePathString, file, objectMetadata));
        s3.setObjectAcl(new SetObjectAclRequest(bucketName, destFilePathString, CannedAccessControlList.PublicRead));
    }

    @Override
    public String getPublicFileUrl(Path filePath) {
        String baseUrl = cloudFrontBaseUrl.orElseThrow(
                () -> new IllegalStateException("cloudFrontBaseUrl was required"));

        return Paths.get(URI.create(baseUrl)).resolve(filePath).toString();
    }
}
