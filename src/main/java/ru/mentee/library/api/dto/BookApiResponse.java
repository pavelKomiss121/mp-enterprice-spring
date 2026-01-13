/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookApiResponse {
    private String title;
    private List<AuthorInfo> authors;
    private List<PublisherInfo> publishers;

    @JsonProperty("publish_date")
    private String publishDate;

    @JsonProperty("number_of_pages")
    private Integer numberOfPages;

    // identifiers - это объект, где ключи - типы идентификаторов, значения - массивы строк
    private Map<String, List<String>> identifiers;

    private String description;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorInfo {
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PublisherInfo {
        private String name;
    }
}
