package com.gagsn.finder;

import com.gagsn.db.dto.FileDto;
import com.gagsn.db.repository.FileRepository;
import com.gagsn.util.FileReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticIndexer {

    public static final String SYMPHONY_FILE_LOCATION = "file/drafts-final.log.txt";

    private final FileReader fileReader;
    private final FileRepository stringRepository;

    public void indexFile() throws IOException {
        String symphonyXml = read();
        FileDto fileDto = new FileDto();
        fileDto.setContent(symphonyXml);

        stringRepository.save(fileDto);
    }

    public void indexFile(String content) throws IOException {
        FileDto fileDto = new FileDto();
        fileDto.setContent(content);
        stringRepository.save(fileDto);
    }

    private String read() throws IOException {
        File file = fileReader.readFile(SYMPHONY_FILE_LOCATION);
        return FileUtils.readFileToString(file, "UTF-8");
    }
}
