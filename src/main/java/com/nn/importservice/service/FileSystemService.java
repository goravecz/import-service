package com.nn.importservice.service;

import com.nn.importservice.config.FileSystemProperties;
import com.nn.importservice.exception.FileSystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class FileSystemService {

    private final FileSystemProperties properties;

    public FileSystemService(FileSystemProperties properties) {
        this.properties = properties;
    }

    /**
     * Lists files in the import folder matching the given prefix pattern
     *
     * @param prefix the prefix pattern to match files against
     * @return list of file paths matching the pattern
     */
    public List<Path> listFilesByPrefix(String prefix) {
        Path importFolder = Paths.get(properties.importFolder());
        
        if (!Files.exists(importFolder)) {
            log.warn("Import folder does not exist: {}", importFolder);
            return List.of();
        }

        if (!Files.isDirectory(importFolder)) {
            throw new FileSystemException("Import folder is not a directory: " + importFolder);
        }

        try (Stream<Path> stream = Files.walk(importFolder, 1)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith(prefix))
                    .toList();
        } catch (IOException e) {
            throw new FileSystemException("Failed to list files with prefix: " + prefix, e);
        }
    }

    /**
     * Deletes a file after successful processing
     *
     * @param file the file to delete
     */
    public void deleteFile(Path file) {
        try {
            Files.deleteIfExists(file);
            log.info("Deleted file: {}", file.getFileName());
        } catch (IOException e) {
            throw new FileSystemException("Failed to delete file: " + file, e);
        }
    }

    /**
     * Moves a file to the error folder when processing fails
     *
     * @param file the file to move
     */
    public void moveFileToError(Path file) {
        Path errorFolder = Paths.get(properties.errorFolder());
        
        try {
            createDirectoriesIfNeeded(errorFolder);
            Path target = errorFolder.resolve(file.getFileName());
            Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Moved file to error folder: {}", file.getFileName());
        } catch (IOException e) {
            throw new FileSystemException("Failed to move file to error folder: " + file, e);
        }
    }

    private void createDirectoriesIfNeeded(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created directory: {}", path);
            }
        } catch (IOException e) {
            throw new FileSystemException("Failed to create directories: " + path, e);
        }
    }
}
