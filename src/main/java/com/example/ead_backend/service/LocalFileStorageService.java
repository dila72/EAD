package com.example.ead_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Local file storage service for handling image uploads
 * Stores images in the local file system instead of cloud storage
 */
@Slf4j
@Service
public class LocalFileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Upload an image to local storage
     * 
     * @param file the image file to upload
     * @return Map containing the file URL and file ID
     */
    public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
        return uploadImage(file, "");
    }

    /**
     * Upload an image to local storage with custom subfolder
     * 
     * @param file      the image file to upload
     * @param subfolder the subfolder name (e.g., "customers", "vehicles")
     * @return Map containing the file URL and file ID
     */
    public Map<String, Object> uploadImage(MultipartFile file, String subfolder) throws IOException {
        try {
            if (file.isEmpty()) {
                throw new IOException("Failed to store empty file");
            }

            log.info("Uploading image to local storage: {}", file.getOriginalFilename());

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, subfolder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path destinationPath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            // Build URL path
            String urlPath = subfolder.isEmpty() 
                ? "/api/files/" + uniqueFilename
                : "/api/files/" + subfolder + "/" + uniqueFilename;
            
            String fileUrl = baseUrl + urlPath;

            log.info("Image uploaded successfully. File ID: {}", uniqueFilename);

            // Return result similar to Cloudinary format
            Map<String, Object> result = new HashMap<>();
            result.put("secure_url", fileUrl);
            result.put("public_id", subfolder.isEmpty() ? uniqueFilename : subfolder + "/" + uniqueFilename);
            result.put("url", fileUrl);

            return result;

        } catch (IOException e) {
            log.error("Failed to upload image to local storage: {}", e.getMessage(), e);
            throw new IOException("Failed to upload image: " + e.getMessage());
        }
    }

    /**
     * Delete an image from local storage
     * 
     * @param publicId the public ID (file path) of the image to delete
     */
    public void deleteImage(String publicId) throws IOException {
        if (publicId == null || publicId.isEmpty()) {
            log.warn("Attempted to delete image with null or empty public ID");
            return;
        }

        try {
            log.info("Deleting image from local storage: {}", publicId);
            
            Path filePath = Paths.get(uploadDir, publicId);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Image deleted successfully: {}", publicId);
            } else {
                log.warn("Image file not found for deletion: {}", publicId);
            }
        } catch (IOException e) {
            log.error("Failed to delete image from local storage: {}", e.getMessage(), e);
            throw new IOException("Failed to delete image: " + e.getMessage());
        }
    }

    /**
     * Update an image (delete old one and upload new one)
     * 
     * @param newFile    the new image file
     * @param oldPublicId the public ID of the old image to delete
     * @return Map containing the new file URL and file ID
     */
    public Map<String, Object> updateImage(MultipartFile newFile, String oldPublicId) throws IOException {
        // Delete old image if exists
        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            try {
                deleteImage(oldPublicId);
            } catch (IOException e) {
                log.warn("Failed to delete old image during update: {}", e.getMessage());
                // Continue with upload even if delete fails
            }
        }

        // Extract subfolder from oldPublicId if present
        String subfolder = "";
        if (oldPublicId != null && oldPublicId.contains("/")) {
            subfolder = oldPublicId.substring(0, oldPublicId.lastIndexOf("/"));
        }

        // Upload new image
        return uploadImage(newFile, subfolder);
    }

    /**
     * Get the physical file path for a public ID
     * 
     * @param publicId the public ID (file path)
     * @return Path object
     */
    public Path getFilePath(String publicId) {
        return Paths.get(uploadDir, publicId);
    }
}
