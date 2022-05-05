package com.gagsn.finder;

import com.gagsn.db.dto.FileDto;
import com.gagsn.util.FileReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class Searcher {

    private final FileReader fileReader;
    private final ElasticIndexer indexer;
    private final ElasticsearchRestTemplate elasticsearchTemplate;

    public static final String SOLR_FILE_LOCATION = "file/solr.txt";

    public void search() throws IOException {
        indexer.indexFile();

        List<String> ids = read();
        List<String> result = new ArrayList<>();

        for (String id : ids) {
            QueryBuilder query = QueryBuilders.matchQuery("content", id);
            NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(query);
            SearchHits<FileDto> searchHits = elasticsearchTemplate.search(nativeSearchQuery, FileDto.class, IndexCoordinates.of("file"));
            if (!searchHits.getSearchHits().isEmpty()) {
                List<String> collect = searchHits.getSearchHits()
                        .stream()
                        .map(SearchHit::getContent)
                        .map(FileDto::getId)
                        .collect(Collectors.toList());
                result.addAll(collect);
                System.out.println("Found ids: " + result.size() + " Count: " + collect.size() +  ", ID: " + collect.get(0));
            }
        }

        System.out.println("All id's founded count " + result.size() + "Provided id's count: " + ids.size());
    }

    private List<String> read() throws IOException {
        File file = fileReader.readFile(SOLR_FILE_LOCATION);
        List<String> ids = FileUtils.readLines(file, "UTF-8");
        System.out.println(ids);

        return ids;
    }
}
