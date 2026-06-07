package tw.edu.fju.miniclinic.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;

@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Object> {

    @Override
    public Object convertToDatabaseColumn(LocalDate date) {
        return date == null ? null : date.toString();
    }

    @Override
    public LocalDate convertToEntityAttribute(Object dbData) {
        if (dbData == null) return null;
        
        if (dbData instanceof java.sql.Date) {
            return ((java.sql.Date) dbData).toLocalDate();
        }
        if (dbData instanceof LocalDate) {
            return (LocalDate) dbData;
        }
        
        String str = dbData.toString();
        return LocalDate.parse(str.length() > 10 ? str.substring(0, 10) : str);
    }
}
