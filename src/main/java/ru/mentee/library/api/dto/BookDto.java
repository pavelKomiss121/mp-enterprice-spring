/* @MENTEE_POWER (C)2025 */
package ru.mentee.library.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;
    private String title;
    private String author;
    private String isbn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishedDate;

    private Boolean available;
}
