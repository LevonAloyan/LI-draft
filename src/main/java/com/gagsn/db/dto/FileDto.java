package com.gagsn.db.dto;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "file")
public class FileDto {
    private String id;
    private String content;
}
