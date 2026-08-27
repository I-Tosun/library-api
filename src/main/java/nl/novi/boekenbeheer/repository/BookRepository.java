package nl.novi.boekenbeheer.repository;

import nl.novi.boekenbeheer.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByGenre(String genre);
    List<Book> findByAuthorId(Long authorId);
}