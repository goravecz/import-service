package com.nn.importservice.parser;

import java.nio.file.Path;
import java.util.List;

public interface FileParser<T> {
    
    List<T> parse(Path file) throws FileParsingException;
}
