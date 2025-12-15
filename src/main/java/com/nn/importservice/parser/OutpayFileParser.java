package com.nn.importservice.parser;

import com.nn.importservice.dto.OutpayRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class OutpayFileParser implements FileParser<OutpayRecord> {

    private static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;
    private static final int EXPECTED_FIELD_COUNT = 14;

    @Override
    public List<OutpayRecord> parse(Path file) throws FileParsingException {
        List<OutpayRecord> records = new ArrayList<>();
        
        try (Reader reader = Files.newBufferedReader(file, ISO_8859_1);
             CSVParser csvParser = new CSVParser(reader, 
                     CSVFormat.DEFAULT.builder()
                             .setDelimiter(';')
                             .setIgnoreEmptyLines(true)
                             .setTrim(true)
                             .get())) {
            
            for (CSVRecord csvRecord : csvParser) {
                try {
                    if (csvRecord.size() < EXPECTED_FIELD_COUNT - 1) {
                        throw new FileParsingException(
                                "Invalid number of fields at line " + csvRecord.getRecordNumber() + 
                                ": expected at least " + (EXPECTED_FIELD_COUNT - 1) + ", got " + csvRecord.size());
                    }
                    records.add(createRecord(csvRecord));
                } catch (Exception e) {
                    throw new FileParsingException(
                            "Failed to parse line " + csvRecord.getRecordNumber() + ": " + csvRecord, e);
                }
            }
            
            return records;
        } catch (IOException e) {
            throw new FileParsingException("Failed to read file: " + file, e);
        }
    }

    private OutpayRecord createRecord(CSVRecord csvRecord) {
        String clntnum = getField(csvRecord, 0);
        String chdrnum = getField(csvRecord, 1);
        String letterType = getField(csvRecord, 2);
        String printDate = getField(csvRecord, 3);
        String dataId = getField(csvRecord, 4);
        String clntName = getField(csvRecord, 5);
        String clntAddress = getField(csvRecord, 6);
        String regDate = getFieldOrNull(csvRecord, 7);
        String benPercentStr = getField(csvRecord, 8);
        String role1 = getField(csvRecord, 9);
        String role2 = getFieldOrNull(csvRecord, 10);
        String cownNum = getField(csvRecord, 11);
        String cownName = getField(csvRecord, 12);

        BigDecimal benPercent = null;
        if (benPercentStr != null && !benPercentStr.isEmpty()) {
            try {
                benPercent = new BigDecimal(benPercentStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid benefit percent: " + benPercentStr, e);
            }
        }

        return new OutpayRecord(
                clntnum, chdrnum, letterType, printDate, dataId,
                clntName, clntAddress, regDate, benPercent,
                role1, role2, cownNum, cownName
        );
    }

    private String getField(CSVRecord record, int index) {
        if (index >= record.size()) {
            return null;
        }
        String value = record.get(index).trim();
        return value.isEmpty() ? null : value;
    }

    private String getFieldOrNull(CSVRecord record, int index) {
        String value = getField(record, index);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }
}
