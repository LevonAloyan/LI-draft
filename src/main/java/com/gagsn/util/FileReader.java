package com.gagsn.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileReader {

    public File readFile(String name) {
        ClassLoader classLoader = FileReader.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(name)).getFile());
        log.info("Read file from resources with file name: {}", name);
        return file;
    }
}
