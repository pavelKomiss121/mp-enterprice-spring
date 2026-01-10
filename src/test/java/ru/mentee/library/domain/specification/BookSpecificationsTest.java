/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.domain.specification;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mentee.library.config.JpaAuditConfig;
import ru.mentee.library.domain.model.Author;
import ru.mentee.library.domain.model.Book;
import ru.mentee.library.domain.model.BookStatus;
import ru.mentee.library.domain.model.Category;
import ru.mentee.library.domain.repository.BookRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(JpaAuditConfig.class)
class BookSpecificationsTest {

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

    @Autowired private BookRepository bookRepository;

    @Autowired private TestEntityManager entityManager;

    private Author stephenKing;
    private Author robertMartin;
    private Author joshuaBloch;
    private Category fiction;
    private Category programming;
    private Book book1; // "The Stand" by Stephen King, Fiction, 1978, 1152 pages
    private Book book2; // "Clean Code" by Robert Martin, Programming, 2008, 464 pages
    private Book book3; // "Effective Java" by Joshua Bloch, Programming, 2017, 416 pages

    @BeforeEach
    void setUp() {
        // Очистка
        entityManager.getEntityManager().createQuery("DELETE FROM Loan").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Book").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Author").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Category").executeUpdate();
        entityManager.flush();
        entityManager.clear();

        // Создание категорий
        fiction = Category.builder().name("Fiction").description("Fictional stories").build();
        programming =
                Category.builder().name("Programming").description("Programming books").build();
        entityManager.persist(fiction);
        entityManager.persist(programming);

        // Создание авторов
        stephenKing =
                Author.builder()
                        .firstName("Stephen")
                        .lastName("King")
                        .birthDate(LocalDate.of(1947, 9, 21))
                        .biography("Master of horror")
                        .build();
        robertMartin =
                Author.builder()
                        .firstName("Robert")
                        .lastName("Martin")
                        .birthDate(LocalDate.of(1952, 12, 5))
                        .biography("Uncle Bob")
                        .build();
        joshuaBloch =
                Author.builder()
                        .firstName("Joshua")
                        .lastName("Bloch")
                        .birthDate(LocalDate.of(1961, 8, 28))
                        .biography("Java expert")
                        .build();
        entityManager.persist(stephenKing);
        entityManager.persist(robertMartin);
        entityManager.persist(joshuaBloch);

        // Создание книг
        book1 =
                Book.builder()
                        .isbn("978-0-307-74365-0")
                        .title("The Stand")
                        .description("Post-apocalyptic horror novel")
                        .publicationYear(1978)
                        .pages(1152)
                        .category(fiction)
                        .status(BookStatus.AVAILABLE)
                        .authors(Set.of(stephenKing))
                        .build();

        book2 =
                Book.builder()
                        .isbn("978-0-13-235088-4")
                        .title("Clean Code")
                        .description("A Handbook of Agile Software Craftsmanship")
                        .publicationYear(2008)
                        .pages(464)
                        .category(programming)
                        .status(BookStatus.AVAILABLE)
                        .authors(Set.of(robertMartin))
                        .build();

        book3 =
                Book.builder()
                        .isbn("978-0-13-468599-1")
                        .title("Effective Java")
                        .description("Best practices for Java programming")
                        .publicationYear(2017)
                        .pages(416)
                        .category(programming)
                        .status(BookStatus.LOANED)
                        .authors(Set.of(joshuaBloch))
                        .build();

        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.persist(book3);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Should найти книгу по части названия")
    void shouldFindBookByPartialTitle() {
        // given
        Specification<Book> spec = BookSpecifications.titleContains("Clean");

        // when
        List<Book> books = bookRepository.findAll(spec);

        // then
        assertThat(books).hasSize(1).extracting(Book::getTitle).containsExactly("Clean Code");
    }

    @Test
    @DisplayName("Should найти книгу по части названия (регистронезависимо)")
    void shouldFindBookByPartialTitleCaseInsensitive() {
        // given
        Specification<Book> spec = BookSpecifications.titleContains("clean");

        // when
        List<Book> books = bookRepository.findAll(spec);

        // then
        assertThat(books).hasSize(1).extracting(Book::getTitle).containsExactly("Clean Code");
    }

    @Test
    @DisplayName("Should найти книги по имени автора")
    void shouldFindBooksByAuthorFirstName() {
        // given
        Specification<Book> spec = BookSpecifications.authorNameContains("Stephen");

        // when
        List<Book> books = bookRepository.findAll(spec);

        // then
        assertThat(books).hasSize(1).extracting(Book::getTitle).containsExactly("The Stand");
    }

    @Test
    @DisplayName("Should найти книги по фамилии автора")
    void shouldFindBooksByAuthorLastName() {
        // given
        Specification<Book> spec = BookSpecifications.authorNameContains("Martin");

        // when
        List<Book> books = bookRepository.findAll(spec);

        // then
        assertThat(books).hasSize(1).extracting(Book::getTitle).containsExactly("Clean Code");
    }

    @Test
    @DisplayName("Should найти книги по категории")
    void shouldFindBooksByCategory() {
        // given
        Specification<Book> spec = BookSpecifications.inCategory(programming.getId());

        // when
        List<Book> books = bookRepository.findAll(spec);

        // then
        assertThat(books)
                .hasSize(2)
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Clean Code", "Effective Java");
    }

    @Test
    @DisplayName("Should найти книги по автору и категории")
    void shouldFindBooksByAuthorAndCategory() {
        // given
        Specification<Book> spec =
                Specification.where(BookSpecifications.authorNameContains("Martin"))
                        .and(BookSpecifications.inCategory(programming.getId()));

        // when
        List<Book> books = bookRepository.findAll(spec);

        // then
        assertThat(books).hasSize(1).extracting(Book::getTitle).containsExactly("Clean Code");
    }

    @Test
    @DisplayName("Should найти книги в диапазоне лет")
    void shouldFindBooksInYearRange() {
        // given
        Specification<Book> spec = BookSpecifications.publishedBetween(2000, 2010);

        // when
        List<Book> books = bookRepository.findAll(spec);

        // then
        assertThat(books).hasSize(1).extracting(Book::getTitle).containsExactly("Clean Code");
    }

    @Test
    @DisplayName("Should найти книги от определённого года")
    void shouldFindBooksFromYear() {
        // given
        Specification<Book> spec = BookSpecifications.publishedBetween(2010, null);

        // when
        List<Book> books = bookRepository.findAll(spec);

        // then
        assertThat(books).hasSize(1).extracting(Book::getTitle).containsExactly("Effective Java");
    }

    @Test
    @DisplayName("Should найти книги до определённого года")
    void shouldFindBooksUntilYear() {
        // given
        Specification<Book> spec = BookSpecifications.publishedBetween(null, 2000);

        // when
        List<Book> books = bookRepository.findAll(spec);

        // then
        assertThat(books).hasSize(1).extracting(Book::getTitle).containsExactly("The Stand");
    }

    @Test
    @DisplayName("Should найти книги по диапазону страниц")
    void shouldFindBooksByPagesRange() {
        // given
        Specification<Book> spec = BookSpecifications.pagesBetween(400, 500);

        // when
        List<Book> books = bookRepository.findAll(spec);

        // then
        assertThat(books)
                .hasSize(2)
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Clean Code", "Effective Java");
    }

    @Test
    @DisplayName("Should найти только доступные книги")
    void shouldFindOnlyAvailableBooks() {
        // given
        Specification<Book> spec = BookSpecifications.availableOnly(true);

        // when
        List<Book> books = bookRepository.findAll(spec);

        // then
        assertThat(books)
                .hasSize(2)
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("The Stand", "Clean Code");
    }

    @Test
    @DisplayName("Should корректно работать с пустыми критериями")
    void shouldWorkWithEmptyCriteria() {
        // given
        Specification<Book> spec = Specification.where(null);

        // when
        List<Book> books = bookRepository.findAll(spec);

        // then
        assertThat(books).hasSize(3);
    }

    @Test
    @DisplayName("Should комбинировать все фильтры")
    void shouldCombineAllFilters() {
        // given
        Specification<Book> spec =
                Specification.where(BookSpecifications.authorNameContains("Martin"))
                        .and(BookSpecifications.inCategory(programming.getId()))
                        .and(BookSpecifications.publishedBetween(2000, 2010))
                        .and(BookSpecifications.pagesBetween(400, 500))
                        .and(BookSpecifications.availableOnly(true));

        // when
        List<Book> books = bookRepository.findAll(spec);

        // then
        assertThat(books).hasSize(1).extracting(Book::getTitle).containsExactly("Clean Code");
    }

    @Test
    @DisplayName("Should вернуть пустой список если критерии не совпадают")
    void shouldReturnEmptyListWhenNoMatch() {
        // given
        Specification<Book> spec = BookSpecifications.titleContains("NonExistentBook");

        // when
        List<Book> books = bookRepository.findAll(spec);

        // then
        assertThat(books).isEmpty();
    }
}
