/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.converter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import ru.mentee.learning.api.dto.CourseDto;
import ru.mentee.learning.api.dto.CourseListResponse;

@Slf4j
public class CsvMessageConverter extends AbstractHttpMessageConverter<CourseListResponse> {

    public CsvMessageConverter() {
        super(MediaType.parseMediaType("text/csv"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return CourseListResponse.class.isAssignableFrom(clazz);
    }

    @Override
    protected CourseListResponse readInternal(
            Class<? extends CourseListResponse> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException("CSV reading not supported");
    }

    @Override
    protected void writeInternal(CourseListResponse response, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        log.debug("Writing CourseListResponse as CSV");

        try (OutputStreamWriter writer =
                new OutputStreamWriter(outputMessage.getBody(), StandardCharsets.UTF_8)) {
            // Заголовок CSV
            writer.write("id,title,description,category,level,duration,price\n");

            // Строки данных
            for (CourseDto course : response.getCourses()) {
                writer.write(
                        String.format(
                                "%s,%s,%s,%s,%s,%d,%s\n",
                                escapeCsv(course.getId()),
                                escapeCsv(course.getTitle()),
                                escapeCsv(course.getDescription()),
                                escapeCsv(course.getCategory()),
                                course.getLevel(),
                                course.getDuration(),
                                course.getPrice()));
            }

            writer.flush();
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // Если содержит запятую, кавычки или перенос строки - заключаем в кавычки
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
