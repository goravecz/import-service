package com.nn.importservice.parser;

import com.nn.importservice.dto.RedemptionRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RedemptionFileParserTest {

    @TempDir
    Path tempDir;

    private RedemptionFileParser parser;

    @BeforeEach
    void setUp() {
        parser = new RedemptionFileParser();
    }

    @Test
    void testParse_WithValidFile_ReturnsRecords() {
        Path testFile = Path.of("src/test/resources/testfiles/redemption_01.txt");

        List<RedemptionRecord> records = parser.parse(testFile);

        assertFalse(records.isEmpty());
        assertEquals(23, records.size());
        
        RedemptionRecord first = records.getFirst();
        assertEquals("1", first.getCompany());
        assertEquals("30003195", first.getChdrnum());
        assertEquals(new BigDecimal("3450978.00"), first.getSurvalue());
        assertEquals("K5003MT   WEEKEND1", first.getJobUser());
        assertEquals("2020-02-15", first.getJobName());
    }

    @Test
    void testParse_WithEmptyFile_ReturnsEmptyList() throws IOException {
        Path testFile = tempDir.resolve("redemption_empty.txt");
        Files.writeString(testFile, "");

        List<RedemptionRecord> records = parser.parse(testFile);

        assertTrue(records.isEmpty());
    }

}
