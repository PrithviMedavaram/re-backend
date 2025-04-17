package com.example.demo.service;

import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.contentSources.B2FileContentSource;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2Bucket;
import com.backblaze.b2.client.structures.B2FileVersion;
import com.backblaze.b2.client.structures.B2UploadFileRequest; // Use the request builder
import com.example.demo.model.UploadFileResponse;
import com.example.demo.property.BackblazeB2Properties;
import org.slf4j.Logger; // Add logging
import org.slf4j.LoggerFactory; // Add logging
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct; // Or javax.annotation if not using Jakarta EE 9+

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects; // For null checks

@Service
public class BackblazeB2Service {

    private static final Logger log = LoggerFactory.getLogger(BackblazeB2Service.class);

    private final B2StorageClient b2StorageClient;
    private final BackblazeB2Properties properties; // Inject properties
    private String bucketId; // Store the bucket ID, not the name

    // Inject both the client and properties via constructor
    public BackblazeB2Service(B2StorageClient b2StorageClient, BackblazeB2Properties properties) {
        this.b2StorageClient = b2StorageClient;
        this.properties = properties;
    }

    // Find the bucket ID after the bean is created and properties are set
    @PostConstruct
    private void initializeBucketId() {
        String bucketName = properties.getBucketName();
        if (bucketName == null || bucketName.isBlank()) {
            log.error("Backblaze B2 bucket name is not configured in properties.");
            throw new IllegalStateException("Backblaze B2 bucket name is not configured.");
        }
        log.info("Looking up B2 Bucket ID for bucket name: {}", bucketName);
        try {
            // Use getBucketOrNullByName for clarity, handle null case explicitly
            B2Bucket bucket = b2StorageClient.getBucketOrNullByName(bucketName);
            if (bucket == null) {
                log.error("Backblaze B2 bucket not found: {}", bucketName);
                throw new IllegalStateException("Configured Backblaze B2 bucket not found: " + bucketName);
            }
            this.bucketId = bucket.getBucketId();
            log.info("Found B2 Bucket ID: {} for bucket name: {}", this.bucketId, bucketName);
        } catch (B2Exception e) {
            log.error("Failed to get Backblaze B2 bucket ID for name '{}': {}", bucketName, e.getMessage(), e);
            // Wrap in a runtime exception to prevent application startup if B2 is misconfigured or unreachable
            throw new RuntimeException("Failed to initialize Backblaze B2 service: Could not get bucket ID for " + bucketName, e);
        }
    }


    public List<B2Bucket> listBuckets() {
        try {
            // Consider potential issues if the underlying collection is null or lazy.
            // This simple approach might be sufficient for most cases.
            return b2StorageClient.buckets().stream().toList();
        } catch (B2Exception e) {
            log.error("Failed to list buckets from Backblaze B2: {}", e.getMessage(), e);
            // Wrap B2Exception in a runtime exception for simplicity, or handle it more specifically
            throw new RuntimeException("Failed to list buckets from Backblaze B2", e);
        }
    }

    /**
     * Uploads a file to the configured Backblaze B2 bucket.
     *
     * @param file The MultipartFile to upload.
     * @return UploadFileResponse containing details of the uploaded file.
     * @throws B2Exception If there's an error communicating with the B2 API.
     * @throws IOException If there's an error reading the file input stream.
     */
    public UploadFileResponse uploadFile(MultipartFile file) throws B2Exception, IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file.");
        }
        // Ensure service was initialized correctly
        if (this.bucketId == null) {
            log.error("Backblaze B2 Service bucket ID is null. Initialization likely failed.");
            throw new IllegalStateException("Backblaze B2 Service is not properly initialized (bucket ID is null). Check configuration and logs.");
        }

        // Use original filename, provide default if null
        String originalFileName = Objects.requireNonNullElse(file.getOriginalFilename(), "untitled");
        // Use provided content type, default to octet-stream if null
        String contentType = Objects.requireNonNullElse(file.getContentType(), "application/octet-stream");
        long contentLength = file.getSize();

        log.info("Attempting to upload file '{}' ({} bytes, type: {}) to bucket ID '{}'",
                originalFileName, contentLength, contentType, this.bucketId);

        // Use try-with-resources to ensure the InputStream is closed
        try (InputStream inputStream = file.getInputStream()) {
            // Create a content source from the InputStream
            // Convert MultipartFile to a temporary File
            File tempFile = File.createTempFile("upload-", "-" + originalFileName);
            file.transferTo(tempFile);

            // Create a content source from the File
            B2FileContentSource contentSource = B2FileContentSource.builder(tempFile)
                    .build();


            // Build the upload request
            B2UploadFileRequest request = B2UploadFileRequest.builder(
                            this.bucketId,       // The ID of the bucket
                            originalFileName,    // The name of the B2 file
                            contentType,         // The MIME type
                            contentSource)       // The content source
                    // Add optional settings if needed:
                    // .setCustomField("key", "value")
                    // .setServerSideEncryption(B2FileSse.SSE_B2_AES)
                    .build();

            log.debug("Uploading file with B2 request details..."); // Avoid logging potentially large request object directly
            B2FileVersion uploadedFile = b2StorageClient.uploadSmallFile(request);
            log.info("Successfully uploaded file '{}' as B2 file ID '{}'", originalFileName, uploadedFile.getFileId());

            // Return details from the B2FileVersion object
            return new UploadFileResponse(
                    uploadedFile.getFileId(),
                    uploadedFile.getFileName(),
                    uploadedFile.getFileInfo() // Use the length reported by B2
            );
        } catch (B2Exception e) {
            log.error("Backblaze B2 API error during upload of '{}': {}", originalFileName, e.getMessage(), e);
            // Re-throw specific B2Exception for the controller to handle
            throw e;
        } catch (IOException e) {
            log.error("IO error during upload preparation or streaming for file '{}': {}", originalFileName, e.getMessage(), e);
            // Re-throw IOException for the controller to handle
            throw e;
        } catch (Exception e) {
            // Catch unexpected errors
            log.error("Unexpected error during upload of file '{}': {}", originalFileName, e.getMessage(), e);
            // Wrap unexpected exceptions
            throw new RuntimeException("Unexpected error uploading file " + originalFileName, e);
        }
    }
}