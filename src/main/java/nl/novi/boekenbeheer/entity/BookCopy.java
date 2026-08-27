package nl.novi.boekenbeheer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.novi.boekenbeheer.enums.BookCopyStatus;

import java.time.LocalDate;

@Entity
@Table(name = "book_copies")
@Getter
@Setter
@NoArgsConstructor
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String copyCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookCopyStatus status = BookCopyStatus.AVAILABLE;

    @Column(nullable = false)
    private LocalDate acquisitionDate;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}