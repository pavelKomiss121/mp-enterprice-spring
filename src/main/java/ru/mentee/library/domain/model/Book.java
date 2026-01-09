/* @MENTEE_POWER (C)2025 */
package ru.mentee.library.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(unique = true, nullable = false, length = 20)
    private String isbn;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(nullable = false)
    private Boolean available = true;
}
