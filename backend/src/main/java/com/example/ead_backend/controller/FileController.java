package com.example.ead_backend.controller;

import com.example.ead_backend.service.LocalFileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Controller for serving uploaded files
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FileController {

    private final LocalFileStorageService fileStorageService;

    /**
     * Serve an uploaded file
     * GET /api/files/{filename}
     * GET /api/files/{subfolder}/{filename}
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        return serveFileFromPath(filename);
    }

    @GetMapping("/{subfolder}/{filename:.+}")
    public ResponseEntity<Resource> serveFileWithSubfolder(
            @PathVariable String subfolder,
            @PathVariable String filename) {
        return serveFileFromPath(subfolder + "/" + filename);
    }

    private ResponseEntity<Resource> serveFileFromPath(String publicId) {
        try {
            Path filePath = fileStorageService.getFilePath(publicId);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Determine content type
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                log.debug("Serving file: {} with content type: {}", publicId, contentType);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                log.warn("File not found or not readable: {}", publicId);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            log.error("Malformed URL for file: {}", publicId, e);
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            log.error("Error reading file: {}", publicId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
