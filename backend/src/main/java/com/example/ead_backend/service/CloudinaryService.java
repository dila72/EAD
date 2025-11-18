package com.example.ead_backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Cloudinary Service
 * DISABLED - Using local file storage instead
 * This service is kept for reference but is disabled via ConditionalOnProperty
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "cloudinary.enabled", havingValue = "true", matchIfMissing = false)
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Upload an image to Cloudinary
     *
     * @param file the image file to upload
     * @return Map containing the upload result with url and public_id
     * @throws IOException if upload fails
     */
    public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
        return uploadImage(file, "vehicles");
    }

    /**
     * Upload an image to Cloudinary with custom subfolder
     *
     * @param file the image file to upload
     * @param subfolder the subfolder name (e.g., "vehicles", "customers")
     * @return Map containing the upload result with url and public_id
     * @throws IOException if upload fails
     */
    public Map<String, Object> uploadImage(MultipartFile file, String subfolder) throws IOException {
        try {
            log.info("Uploading image to Cloudinary: {}", file.getOriginalFilename());
            
            // Generate a unique public ID for the image
            String publicId = subfolder + "/" + UUID.randomUUID().toString();
            
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "folder", "ead-automobile/" + subfolder,
                            "resource_type", "image",
                            "transformation", ObjectUtils.asMap(
                                    "width", 800,
                                    "height", 600,
                                    "crop", "limit",
                                    "quality", "auto"
                            )
                    )
            );
            
            log.info("Image uploaded successfully. Public ID: {}", uploadResult.get("public_id"));
            return uploadResult;
        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary: {}", e.getMessage(), e);
            throw new IOException("Failed to upload image: " + e.getMessage());
        }
    }

    /**
     * Delete an image from Cloudinary
     *
     * @param publicId the public ID of the image to delete
     * @throws IOException if deletion fails
     */
    public void deleteImage(String publicId) throws IOException {
        try {
            if (publicId != null && !publicId.isEmpty()) {
                log.info("Deleting image from Cloudinary: {}", publicId);
                Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Image deleted successfully. Result: {}", result.get("result"));
            }
        } catch (IOException e) {
            log.error("Failed to delete image from Cloudinary: {}", e.getMessage(), e);
            throw new IOException("Failed to delete image: " + e.getMessage());
        }
    }

    /**
     * Update an image (delete old one and upload new one)
     *
     * @param file the new image file
     * @param oldPublicId the public ID of the old image to delete
     * @return Map containing the upload result
     * @throws IOException if operation fails
     */
    public Map<String, Object> updateImage(MultipartFile file, String oldPublicId) throws IOException {
        // Delete old image if exists
        if (oldPublicId != null && !oldPublicId.isEmpty()) {
            deleteImage(oldPublicId);
        }
        
        // Upload new image
        return uploadImage(file);
    }
}
