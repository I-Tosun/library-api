package nl.novi.boekenbeheer.repository;

import nl.novi.boekenbeheer.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomerId(Long customerId);
    List<Loan> findByBookCopyId(Long bookCopyId);
}