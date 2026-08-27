package nl.novi.boekenbeheer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private int publicationYear;

    @Column
    private String ebookFilePath;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @OneToMany(mappedBy = "book", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<BookCopy> bookCopies = new ArrayList<>();
}