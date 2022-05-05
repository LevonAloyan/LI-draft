package com.gagsn.db.dto;

import lombok.Data;

@Data
public class JournalUpdateDto {
    private int id;
    private String oldUrl;
    private String newUrl;
}
