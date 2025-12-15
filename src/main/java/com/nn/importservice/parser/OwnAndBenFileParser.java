package com.nn.importservice.parser;

import com.nn.importservice.dto.OwnAndBenRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class OwnAndBenFileParser implements FileParser<OwnAndBenRecord> {

    private static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;
    private static final int EXPECTED_FIELD_COUNT = 8;

    @Override
    public List<OwnAndBenRecord> parse(Path file) throws FileParsingException {
        List<OwnAndBenRecord> records = new ArrayList<>();
        
        try (Reader reader = Files.newBufferedReader(file, ISO_8859_1);
             CSVParser csvParser = new CSVParser(reader, 
                     CSVFormat.DEFAULT.builder()
                             .setDelimiter('|')
                             .setIgnoreEmptyLines(true)
                             .setTrim(true)
                             .setTrailingDelimiter(true)
                             .get())) {
            
            for (CSVRecord csvRecord : csvParser) {
                try {
                    if (csvRecord.size() < EXPECTED_FIELD_COUNT) {
                        throw new FileParsingException(
                                "Invalid number of fields at line " + csvRecord.getRecordNumber() + 
                                ": expected " + EXPECTED_FIELD_COUNT + ", got " + csvRecord.size());
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

    private OwnAndBenRecord createRecord(CSVRecord csvRecord) {
        String chdrnum = getField(csvRecord, 0);
        String cownnum = getField(csvRecord, 1);
        String ownerName = getField(csvRecord, 2);
        String lifcNum = getField(csvRecord, 3);
        String lifcName = getField(csvRecord, 4);
        String aracde = getField(csvRecord, 5);
        String agntnum = getField(csvRecord, 6);
        String mailAddress = getField(csvRecord, 7);

        return new OwnAndBenRecord(
                chdrnum, cownnum, ownerName, lifcNum,
                lifcName, aracde, agntnum, mailAddress
        );
    }

    private String getField(CSVRecord record, int index) {
        if (index >= record.size()) {
            return null;
        }
        String value = record.get(index).trim();
        return value.isEmpty() ? null : value;
    }
}
