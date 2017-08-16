package com.github.davidmoten.aws.maven;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.maven.plugin.logging.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;

final class S3FileDeployer {

    private final Log log;

    S3FileDeployer(Log log) {
        this.log = log;
    }

    public void deploy(AwsKeyPair keyPair, String region, File file, final String bucketName, final String objectName,
            Proxy proxy) {

        if (file == null) {
            throw new RuntimeException("must specify inputDirectory parameter in configuration");
        }

        final AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(keyPair.key, keyPair.secret));

        ClientConfiguration cc = Util.createConfiguration(proxy);

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(credentials).withClientConfiguration(cc)
                .withRegion(region).build();

        PutObjectRequest req = new PutObjectRequest(bucketName, objectName, file);

        s3.putObject(req);

        log.info("deployed " + file.getName() + " to s3 " + bucketName + ":" + objectName);

    }

}
