/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mentee.library.api.dto.BookDto;
import ru.mentee.library.api.dto.BookSearchCriteria;
import ru.mentee.library.domain.model.Author;
import ru.mentee.library.domain.model.Book;
import ru.mentee.library.domain.model.BookStatus;
import ru.mentee.library.domain.model.Category;
import ru.mentee.library.domain.repository.AuthorRepository;
import ru.mentee.library.domain.repository.BookRepository;
import ru.mentee.library.domain.repository.CategoryRepository;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles("library")
@Transactional
class BookSearchServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.enabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired private BookSearchService bookSearchService;
    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private CategoryRepository categoryRepository;

    private Category fiction;
    private Category programming;
    private Author stephenKing;
    private Author robertMartin;
    private Author joshuaBloch;

    @BeforeEach
    void setUp() {
        // Очистка
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        categoryRepository.deleteAll();

        // Создание тестовых данных
        fiction = categoryRepository.save(Category.builder().name("Fiction").build());
        programming = categoryRepository.save(Category.builder().name("Programming").build());

        stephenKing =
                authorRepository.save(
                        Author.builder()
                                .firstName("Stephen")
                                .lastName("King")
                                .birthDate(LocalDate.of(1947, 9, 21))
                                .build());
        robertMartin =
                authorRepository.save(
                        Author.builder()
                                .firstName("Robert")
                                .lastName("Martin")
                                .birthDate(LocalDate.of(1952, 12, 5))
                                .build());
        joshuaBloch =
                authorRepository.save(
                        Author.builder()
                                .firstName("Joshua")
                                .lastName("Bloch")
                                .birthDate(LocalDate.of(1961, 8, 28))
                                .build());

        // Книги
        bookRepository.save(
                Book.builder()
                        .isbn("978-1")
                        .title("The Stand")
                        .publicationYear(1978)
                        .pages(1152)
                        .category(fiction)
                        .status(BookStatus.AVAILABLE)
                        .authors(Set.of(stephenKing))
                        .build());

        bookRepository.save(
                Book.builder()
                        .isbn("978-2")
                        .title("Clean Code")
                        .publicationYear(2008)
                        .pages(464)
                        .category(programming)
                        .status(BookStatus.AVAILABLE)
                        .authors(Set.of(robertMartin))
                        .build());

        bookRepository.save(
                Book.builder()
                        .isbn("978-3")
                        .title("Effective Java")
                        .publicationYear(2017)
                        .pages(416)
                        .category(programming)
                        .status(BookStatus.LOANED)
                        .authors(Set.of(joshuaBloch))
                        .build());
    }

    @Test
    @DisplayName("Should найти книги по названию")
    void shouldFindBooksByTitle() {
        // given
        BookSearchCriteria criteria = BookSearchCriteria.builder().title("Clean").build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<BookDto> result = bookSearchService.searchBooks(criteria, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    @DisplayName("Should найти книги по автору")
    void shouldFindBooksByAuthor() {
        // given
        BookSearchCriteria criteria = BookSearchCriteria.builder().author("King").build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<BookDto> result = bookSearchService.searchBooks(criteria, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("The Stand");
    }

    @Test
    @DisplayName("Should найти книги по категории")
    void shouldFindBooksByCategory() {
        // given
        BookSearchCriteria criteria =
                BookSearchCriteria.builder().categoryId(programming.getId()).build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<BookDto> result = bookSearchService.searchBooks(criteria, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(BookDto::getTitle)
                .containsExactlyInAnyOrder("Clean Code", "Effective Java");
    }

    @Test
    @DisplayName("Should найти книги по диапазону лет")
    void shouldFindBooksByYearRange() {
        // given
        BookSearchCriteria criteria =
                BookSearchCriteria.builder().fromYear(2000).toYear(2010).build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<BookDto> result = bookSearchService.searchBooks(criteria, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    @DisplayName("Should найти только доступные книги")
    void shouldFindOnlyAvailableBooks() {
        // given
        BookSearchCriteria criteria = BookSearchCriteria.builder().available(true).build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<BookDto> result = bookSearchService.searchBooks(criteria, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(BookDto::getTitle)
                .containsExactlyInAnyOrder("The Stand", "Clean Code");
    }

    @Test
    @DisplayName("Should комбинировать несколько фильтров")
    void shouldCombineMultipleFilters() {
        // given
        BookSearchCriteria criteria =
                BookSearchCriteria.builder()
                        .categoryId(programming.getId())
                        .fromYear(2000)
                        .available(true)
                        .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<BookDto> result = bookSearchService.searchBooks(criteria, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    @DisplayName("Should поддерживать пагинацию")
    void shouldSupportPagination() {
        // given
        BookSearchCriteria criteria = BookSearchCriteria.builder().build();
        Pageable pageable = PageRequest.of(0, 2);

        // when
        Page<BookDto> result = bookSearchService.searchBooks(criteria, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Should поддерживать сортировку")
    void shouldSupportSorting() {
        // given
        BookSearchCriteria criteria = BookSearchCriteria.builder().build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("publicationYear").ascending());

        // when
        Page<BookDto> result = bookSearchService.searchBooks(criteria, pageable);

        // then
        assertThat(result.getContent())
                .extracting(BookDto::getTitle)
                .containsExactly("The Stand", "Clean Code", "Effective Java");
    }

    @Test
    @DisplayName("Should вернуть пустую страницу если ничего не найдено")
    void shouldReturnEmptyPageWhenNoMatch() {
        // given
        BookSearchCriteria criteria = BookSearchCriteria.builder().title("NonExistent").build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<BookDto> result = bookSearchService.searchBooks(criteria, pageable);

        // then
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }
}
