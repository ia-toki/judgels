package judgels.fs.aws;

import static java.util.stream.Collectors.joining;

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
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;
import judgels.fs.FileSystem;

public class AwsFileSystem implements FileSystem {
    private final AmazonS3 s3;
    private final String cloudFrontBaseUrl;
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
    public void uploadPublicFile(InputStream file, List<String> destDirPath, String destFilename) {
        List<String> destFilePath = ImmutableList.<String>builder()
                .addAll(destDirPath)
                .add(destFilename)
                .build();
        String destFilePathString = destFilePath.stream().collect(joining(File.separator));

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
    public String getPublicFileUrl(List<String> filePath) {
        List<String> cloudFrontFilePath = ImmutableList.<String>builder()
                .add(cloudFrontBaseUrl)
                .addAll(filePath)
                .build();
        return cloudFrontFilePath.stream().collect(joining(File.separator));
    }
}
