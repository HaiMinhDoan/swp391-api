package com.devmam.taraacademyapi.service.impl.utils;

import com.devmam.taraacademyapi.service.MinioService;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinioServiceImpl implements MinioService {

    private static final Logger logger = LoggerFactory.getLogger(MinioServiceImpl.class);

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private String minioBucketName;
    @Autowired
    private String minioEndpoint;

    @Override
    public String upload(MultipartFile file) throws Exception {
        return upload(file, file.getOriginalFilename());
    }

    @Override
    public String upload(MultipartFile file, String objectName) throws Exception {
        try (InputStream is = file.getInputStream()) {
            ObjectWriteResponse res = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            logger.info("Uploaded to MinIO: {} -> etag:{}", objectName, res.etag());
            return objectName;
        }
    }

    @Override
    public byte[] download(String objectName) throws Exception {
        try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(minioBucketName)
                .object(objectName)
                .build())) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            IOUtils.copy(stream, buffer);
            return buffer.toByteArray();
        }
    }

    @Override
    public boolean delete(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioBucketName)
                    .object(objectName)
                    .build());
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete object {}: {}", objectName, e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> list(String prefix) throws Exception {
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(minioBucketName)
                .prefix(prefix == null ? "" : prefix)
                .recursive(true)
                .build());
        List<String> keys = new ArrayList<>();
        for (Result<Item> result : results) {
            Item item = result.get();
            keys.add(item.objectName());
        }
        return keys;
    }

    @Override
    public boolean exists(String objectName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(minioBucketName)
                    .object(objectName)
                    .build());
            return true;
        } catch (ErrorResponseException e) {
            return false;
        } catch (Exception e) {
            logger.error("Error checking object existence {}: {}", objectName, e.getMessage());
            return false;
        }
    }

    @Override
    public String getPublicUrl(String objectName) {
        return minioEndpoint.endsWith("/")
                ? minioEndpoint + minioBucketName + "/" + objectName
                : minioEndpoint + "/" + minioBucketName + "/" + objectName;
    }

    @Override
    public String generatePresignedUploadUrl(String objectName, int expirySeconds) throws Exception {
        return minioClient.getPresignedObjectUrl(
                io.minio.GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(minioBucketName)
                        .object(objectName)
                        .expiry(expirySeconds)
                        .build()
        );
    }
}


