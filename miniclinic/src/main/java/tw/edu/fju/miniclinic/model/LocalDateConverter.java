package tw.edu.fju.miniclinic.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;

@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(LocalDate date) {
        return date == null ? null : date.toString();  // 存入資料庫：LocalDate → "2026-05-01"
    }

    @Override
    public LocalDate convertToEntityAttribute(String s) {
        return s == null ? null : LocalDate.parse(s);  // 從資料庫讀出："2026-05-01" → LocalDate
    }
}
