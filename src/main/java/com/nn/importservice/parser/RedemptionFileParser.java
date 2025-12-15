package com.nn.importservice.parser;

import com.nn.importservice.dto.RedemptionRecord;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.fixed.FixedWidthFields;
import com.univocity.parsers.fixed.FixedWidthParser;
import com.univocity.parsers.fixed.FixedWidthParserSettings;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class RedemptionFileParser implements FileParser<RedemptionRecord> {

    @Override
    public List<RedemptionRecord> parse(Path file) throws FileParsingException {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            FixedWidthFields fieldLengths = new FixedWidthFields();
            fieldLengths.addField("company", 1);
            fieldLengths.addField("chdrnum", 8);
            fieldLengths.addField("survalue", 15);
            fieldLengths.addField("jobUser", 20);
            fieldLengths.addField("jobName", 10);
            fieldLengths.addField("jobTimestamp", 16);
            
            FixedWidthParserSettings settings = new FixedWidthParserSettings(fieldLengths);
            settings.setSkipEmptyLines(true);
            settings.setIgnoreTrailingWhitespaces(false);
            settings.setIgnoreLeadingWhitespaces(false);
            settings.getFormat().setLineSeparator("\r\n");
            
            BeanListProcessor<RedemptionRecord> rowProcessor = new BeanListProcessor<>(RedemptionRecord.class);
            settings.setProcessor(rowProcessor);
            
            FixedWidthParser parser = new FixedWidthParser(settings);
            parser.parse(reader);
            
            return rowProcessor.getBeans();
        } catch (Exception e) {
            throw new FileParsingException("Failed to parse file: " + file, e);
        }
    }
}
