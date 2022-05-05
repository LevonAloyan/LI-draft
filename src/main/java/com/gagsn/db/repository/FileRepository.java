package com.gagsn.db.repository;

import com.gagsn.db.dto.FileDto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends ElasticsearchRepository<FileDto, Long> {
}
