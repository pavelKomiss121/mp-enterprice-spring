/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.api.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mentee.library.api.dto.BookApiResponse;

@FeignClient(name = "open-library-client", url = "${openlibrary.api.url}")
public interface OpenLibraryClient {

    @GetMapping("/api/books")
    Map<String, BookApiResponse> getBookByIsbn(
            @RequestParam("bibkeys") String bibkeys,
            @RequestParam("format") String format,
            @RequestParam("jscmd") String jscmd);
}
