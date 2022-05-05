package com.gagsn.db.service;

import com.gagsn.db.dto.JournalUpdateDto;
import com.gagsn.util.FileReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelImporter {

    public static final String FILE_LOCATION = "excel/migration_journals.xlsx";

    private final FileReader fileReader;

    public List<JournalUpdateDto> importJournals() {
        File file = fileReader.readFile(FILE_LOCATION);
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(file);
        } catch (Exception e) {
            log.error("XSSFWorkbook can not read file: {}", FILE_LOCATION, e);
        }
        XSSFSheet worksheet = workbook.getSheetAt(0);

        List<JournalUpdateDto> journals = new ArrayList<>();

        for (int index = 0; index < worksheet.getPhysicalNumberOfRows() - 1; index++) {
            if (index > 0) {
                JournalUpdateDto journalUpdateDto = new JournalUpdateDto();
                XSSFRow row = worksheet.getRow(index);
                try {

                    journalUpdateDto.setId((int) row.getCell(0).getNumericCellValue());
                } catch (NullPointerException e) {
                    System.out.println(row);
                    System.out.println(index);
                    throw new RuntimeException();
                }
                journalUpdateDto.setOldUrl(row.getCell(1).getStringCellValue());
                journalUpdateDto.setNewUrl(row.getCell(2).getStringCellValue());
                journals.add(journalUpdateDto);
            }
        }
        return journals;
    }

}
