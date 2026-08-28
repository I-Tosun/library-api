package nl.novi.boekenbeheer.repository;

import nl.novi.boekenbeheer.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByLastName(String lastName);
}