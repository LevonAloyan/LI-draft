package com.gagsn.finder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ElasticDao<T> {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    public List<T> findByQuery(NativeSearchQuery query, Class<T> clazz, String index) {
        SearchHits<T> searchHits = elasticsearchTemplate.search(query, clazz, IndexCoordinates.of(index));
        log.info("Found {} elements for search in the index {} by query \n {}", searchHits.getTotalHits(), index, query.getQuery());

        return searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
