package com.knowvault.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path uploadDir = Paths.get("uploads");

    public String storeFile(MultipartFile file) throws IOException {

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String originalName = file.getOriginalFilename();
        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String storedFileName = UUID.randomUUID() + extension;
        Path targetPath = uploadDir.resolve(storedFileName);

        file.transferTo(targetPath);

        return storedFileName;
    }

    public Resource loadFileAsResource(String filePath) throws IOException {
        Path path = Paths.get(filePath).normalize();
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("File not found");
        }

        return resource;
    }

    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath).normalize();
        Files.deleteIfExists(path);
    }
}
