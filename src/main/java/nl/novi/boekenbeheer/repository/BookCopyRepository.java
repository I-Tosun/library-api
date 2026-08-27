package nl.novi.boekenbeheer.repository;

import nl.novi.boekenbeheer.entity.BookCopy;
import nl.novi.boekenbeheer.enums.BookCopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    Optional<BookCopy> findByCopyCode(String copyCode);
    List<BookCopy> findByBookId(Long bookId);
    List<BookCopy> findByBookIdAndStatus(Long bookId, BookCopyStatus status);
}