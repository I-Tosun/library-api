package nl.novi.boekenbeheer.repository;

import nl.novi.boekenbeheer.entity.LibraryCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryCardRepository extends JpaRepository<LibraryCard, Long> {
    Optional<LibraryCard> findByCardNumber(String cardNumber);
    Optional<LibraryCard> findByCustomerId(Long customerId);
}
