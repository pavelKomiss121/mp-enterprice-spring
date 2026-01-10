/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи критериев поиска книг.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchCriteria {

    /** Часть названия книги (частичное совпадение) */
    private String title;

    /** Имя или фамилия автора (частичное совпадение) */
    private String author;

    /** ID категории */
    private Long categoryId;

    /** Год публикации, от (включительно) */
    private Integer fromYear;

    /** Год публикации, до (включительно) */
    private Integer toYear;

    /** Минимальное количество страниц */
    private Integer minPages;

    /** Максимальное количество страниц */
    private Integer maxPages;

    /** Только доступные для выдачи книги (true = только AVAILABLE) */
    private Boolean available;
}
