package com.nn.importservice.parser;

import com.nn.importservice.dto.OwnAndBenRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OwnAndBenFileParserTest {

    private static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

    @TempDir
    Path tempDir;

    private OwnAndBenFileParser parser;

    @BeforeEach
    void setUp() {
        parser = new OwnAndBenFileParser();
    }

    @Test
    void testParse_WithValidFile_ReturnsRecords() {
        Path testFile = Path.of("src/test/resources/testfiles/own_and_ben_01.txt");

        List<OwnAndBenRecord> records = parser.parse(testFile);

        assertFalse(records.isEmpty());
        assertTrue(records.size() >= 2);
        
        // Verify first record
        OwnAndBenRecord first = records.getFirst();
        assertEquals("86000019", first.chdrnum());
        assertEquals("76000018", first.cownnum());
        assertNotNull(first.ownerName());
        assertTrue(first.ownerName().contains("Szegedi"));
        assertEquals("76000018", first.lifcNum());
        assertNotNull(first.lifcName());
        assertTrue(first.lifcName().contains("Szegedi"));
        assertEquals("00X", first.aracde());
        assertEquals("11111", first.agntnum());
        assertNotNull(first.mailAddress());
        assertTrue(first.mailAddress().contains("Budapest"));
        
        // Verify second record
        OwnAndBenRecord second = records.get(1);
        assertEquals("86000029", second.chdrnum());
        assertEquals("76000027", second.cownnum());
        assertNotNull(second.ownerName());
        assertTrue(second.ownerName().contains("Katalin"));
        assertEquals("76000027", second.lifcNum());
        assertNotNull(second.lifcName());
        assertEquals("00X", second.aracde());
        assertEquals("11111", second.agntnum());
        assertNotNull(second.mailAddress());
    }

    @Test
    void testParse_WithEmptyFile_ReturnsEmptyList() throws IOException {
        Path testFile = tempDir.resolve("own_and_ben_empty.txt");
        Files.writeString(testFile, "", ISO_8859_1);

        List<OwnAndBenRecord> records = parser.parse(testFile);

        assertTrue(records.isEmpty());
    }

    @Test
    void testParse_WithInvalidFormat_ThrowsException() throws IOException {
        Path testFile = tempDir.resolve("own_and_ben_invalid.txt");
        Files.writeString(testFile, "86000019|76000018|Name", ISO_8859_1);

        FileParsingException exception = assertThrows(FileParsingException.class, 
                () -> parser.parse(testFile));
        
        assertTrue(exception.getMessage().contains("Invalid number of fields") || 
                   exception.getMessage().contains("Failed to parse line"));
    }
}
