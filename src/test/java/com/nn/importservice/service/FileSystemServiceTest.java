package com.nn.importservice.service;

import com.nn.importservice.config.FileSystemProperties;
import com.nn.importservice.exception.FileSystemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemServiceTest {

    @TempDir
    Path tempDir;

    private FileSystemService fileSystemService;
    private Path importFolder;
    private Path errorFolder;

    @BeforeEach
    void setUp() throws IOException {
        importFolder = tempDir.resolve("import");
        errorFolder = tempDir.resolve("error");
        Files.createDirectories(importFolder);

        FileSystemProperties properties = new FileSystemProperties(
                importFolder.toString(),
                errorFolder.toString()
        );
        fileSystemService = new FileSystemService(properties);
    }

    @Test
    void testListFilesByPrefix_WithMatchingFiles() throws IOException {
        Files.createFile(importFolder.resolve("redemption_001.txt"));
        Files.createFile(importFolder.resolve("redemption_002.txt"));
        Files.createFile(importFolder.resolve("outpay_001.txt"));

        List<Path> result = fileSystemService.listFilesByPrefix("redemption_");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getFileName().toString().startsWith("redemption_")));
    }

    @Test
    void testListFilesByPrefix_WithNoMatchingFiles() throws IOException {
        Files.createFile(importFolder.resolve("outpay_001.txt"));
        Files.createFile(importFolder.resolve("outpay_002.txt"));

        List<Path> result = fileSystemService.listFilesByPrefix("redemption_");

        assertTrue(result.isEmpty());
    }

    @Test
    void testListFilesByPrefix_WhenImportFolderDoesNotExist() {
        FileSystemProperties properties = new FileSystemProperties(
                "/non/existent/path",
                errorFolder.toString()
        );
        FileSystemService service = new FileSystemService(properties);

        List<Path> result = service.listFilesByPrefix("redemption_");

        assertTrue(result.isEmpty());
    }

    @Test
    void testListFilesByPrefix_ThrowsExceptionOnIOError() throws IOException {
        // Create a file instead of a directory to force IOException
        Path fileInsteadOfDir = tempDir.resolve("not_a_directory.txt");
        Files.createFile(fileInsteadOfDir);

        FileSystemProperties properties = new FileSystemProperties(
                fileInsteadOfDir.toString(),
                errorFolder.toString()
        );
        FileSystemService service = new FileSystemService(properties);

        assertThrows(FileSystemException.class, () -> service.listFilesByPrefix("test_"));
    }

    @Test
    void testDeleteFile_Successfully() throws IOException {
        Path file = Files.createFile(importFolder.resolve("test.txt"));
        assertTrue(Files.exists(file));

        fileSystemService.deleteFile(file);

        assertFalse(Files.exists(file));
    }

    @Test
    void testDeleteFile_WhenFileDoesNotExist() {
        Path nonExistentFile = importFolder.resolve("nonexistent.txt");

        assertDoesNotThrow(() -> fileSystemService.deleteFile(nonExistentFile));
    }

    @Test
    void testMoveFileToError_Successfully() throws IOException {
        Path file = Files.createFile(importFolder.resolve("error_file.txt"));
        Files.writeString(file, "test content");

        fileSystemService.moveFileToError(file);

        assertFalse(Files.exists(file));
        assertTrue(Files.exists(errorFolder.resolve("error_file.txt")));
        assertEquals("test content", Files.readString(errorFolder.resolve("error_file.txt")));
    }

    @Test
    void testMoveFileToError_CreatesErrorFolderIfNotExists() throws IOException {
        Path file = Files.createFile(importFolder.resolve("test.txt"));

        fileSystemService.moveFileToError(file);

        assertTrue(Files.exists(errorFolder));
        assertTrue(Files.exists(errorFolder.resolve("test.txt")));
    }

    @Test
    void testMoveFileToError_ReplacesExistingFile() throws IOException {
        Files.createDirectories(errorFolder);
        Path existingFile = Files.createFile(errorFolder.resolve("test.txt"));
        Files.writeString(existingFile, "old content");

        Path newFile = Files.createFile(importFolder.resolve("test.txt"));
        Files.writeString(newFile, "new content");

        fileSystemService.moveFileToError(newFile);

        assertEquals("new content", Files.readString(errorFolder.resolve("test.txt")));
    }

    @Test
    void testListFilesByPrefix_OwnAndBenFiles() throws IOException {
        Files.createFile(importFolder.resolve("own_and_ben_001.txt"));
        Files.createFile(importFolder.resolve("own_and_ben_002.txt"));
        Files.createFile(importFolder.resolve("own_and_ben_003.txt"));
        Files.createFile(importFolder.resolve("redemption_001.txt"));

        List<Path> result = fileSystemService.listFilesByPrefix("own_and_ben_");

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(p -> p.getFileName().toString().startsWith("own_and_ben_")));
    }

    @Test
    void testListFilesByPrefix_WithEmptyFolder() throws IOException {
        List<Path> result = fileSystemService.listFilesByPrefix("any_prefix_");

        assertTrue(result.isEmpty());
    }

}
