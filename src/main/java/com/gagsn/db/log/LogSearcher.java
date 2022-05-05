package com.gagsn.db.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogSearcher {

    private final LogParser logParser;

    public List<Document> search() {
        List<Document> documents = new ArrayList<>();
        try {
            List<String> lines = logParser.parse("file/karaf-20201115.log");

            documents = lines.stream()
                    .filter(line -> line.contains("Maximum failed count: 2 limited for FE document update service was limited for draftId:"))
                    .map(line -> {
                        String draftId = StringUtils.substringBetween(line, "Maximum failed count: 2 limited for FE document update service was limited for draftId: ", " finalId: ");
                        String finalId = line.substring(line.indexOf("finalId: ")).substring(9);
                        Document document = new Document();
                        document.setDraftId(draftId);
                        document.setFinalId(finalId);
                        return document;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return documents;
    }
}
