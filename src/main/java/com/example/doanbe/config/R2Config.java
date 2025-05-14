package com.example.doanbe.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class R2Config {

    @Value("${access-key-id}")
    private String accessKeyId;

    @Value("${secret-access-key}")
    private String secretAccessKey;

    @Value("${endpoint}")
    private String endpoint;

    @Value("${region}")
    private String region;


    @Value("${bucket-name}")
    private String bucketName;

    @Bean
    public AmazonS3 r2Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);

        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignerOverride("AWSS3V4SignerType");

        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfig)
                .build();
    }
}
