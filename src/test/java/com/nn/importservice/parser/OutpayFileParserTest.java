package com.nn.importservice.parser;

import com.nn.importservice.dto.OutpayRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutpayFileParserTest {

    private static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

    @TempDir
    Path tempDir;

    private OutpayFileParser parser;

    @BeforeEach
    void setUp() {
        parser = new OutpayFileParser();
    }

    @Test
    void testParse_WithValidFile_ReturnsRecords() {
        Path testFile = Path.of("src/test/resources/testfiles/outpay_01.txt");

        List<OutpayRecord> records = parser.parse(testFile);

        assertFalse(records.isEmpty());
        assertTrue(records.size() >= 2);
        
        // Verify first record
        OutpayRecord first = records.get(0);
        assertEquals("20930093", first.clntnum());
        assertEquals("70027344", first.chdrnum());
        assertEquals("CUP", first.letterType());
        assertEquals("20200210", first.printDate());
        assertEquals("OUTPAY", first.dataId());
        assertNotNull(first.clntName());
        assertTrue(first.clntName().contains("Lajos"));
        assertNotNull(first.clntAddress());
        assertTrue(first.clntAddress().contains("Budapest"));
        assertNull(first.regDate());
        assertEquals(new BigDecimal("100.00"), first.benPercent());
        assertEquals("OW", first.role1());
        assertNull(first.role2());
        assertEquals("20930093", first.cownNum());
        assertNotNull(first.cownName());
        
        // Verify second record
        OutpayRecord second = records.get(1);
        assertEquals("22702221", second.clntnum());
        assertEquals("70176982", second.chdrnum());
        assertEquals("CUP", second.letterType());
        assertEquals("20200210", second.printDate());
        assertEquals("OUTPAY", second.dataId());
        assertNotNull(second.clntName());
        assertNotNull(second.clntAddress());
        assertTrue(second.clntAddress().contains("Gyula"));
    }

    @Test
    void testParse_WithEmptyFile_ReturnsEmptyList() throws IOException {
        Path testFile = tempDir.resolve("outpay_empty.txt");
        Files.writeString(testFile, "", ISO_8859_1);

        List<OutpayRecord> records = parser.parse(testFile);

        assertTrue(records.isEmpty());
    }

    @Test
    void testParse_WithInvalidDelimiter_ThrowsException() throws IOException {
        Path testFile = tempDir.resolve("outpay_wrong_delimiter.txt");
        Files.writeString(testFile, "20930093,70027344,CUP,20200210,OUTPAY", ISO_8859_1);

        FileParsingException exception = assertThrows(FileParsingException.class, 
                () -> parser.parse(testFile));
        
        assertTrue(exception.getMessage().contains("Invalid number of fields") || 
                   exception.getMessage().contains("Failed to parse line"));
    }
}
