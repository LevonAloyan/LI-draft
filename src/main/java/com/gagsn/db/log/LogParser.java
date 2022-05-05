package com.gagsn.db.log;

import com.gagsn.util.FileReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogParser {

    private final FileReader fileReader;

    public List<String> parse(String logFilePath) throws IOException {
        return Files.readAllLines(fileReader.readFile(logFilePath).toPath());
    }

}
