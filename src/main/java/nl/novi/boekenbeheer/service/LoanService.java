package nl.novi.boekenbeheer.service;

import nl.novi.boekenbeheer.dto.request.LoanRequest;
import nl.novi.boekenbeheer.dto.response.LoanResponse;
import nl.novi.boekenbeheer.entity.BookCopy;
import nl.novi.boekenbeheer.entity.Customer;
import nl.novi.boekenbeheer.entity.Loan;
import nl.novi.boekenbeheer.enums.BookCopyStatus;
import nl.novi.boekenbeheer.exception.BadRequestException;
import nl.novi.boekenbeheer.exception.RecordNotFoundException;
import nl.novi.boekenbeheer.mapper.LoanMapper;
import nl.novi.boekenbeheer.repository.BookCopyRepository;
import nl.novi.boekenbeheer.repository.CustomerRepository;
import nl.novi.boekenbeheer.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Service voor het beheren van uitleningen. */
@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookCopyRepository bookCopyRepository;
    private final CustomerRepository customerRepository;
    private final LoanMapper loanMapper;

    public LoanService(LoanRepository loanRepository,
                       BookCopyRepository bookCopyRepository,
                       CustomerRepository customerRepository,
                       LoanMapper loanMapper) {
        this.loanRepository = loanRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.customerRepository = customerRepository;
        this.loanMapper = loanMapper;
    }

    // Geeft alle leningen terug als lijst van LoanResponse DTO's
    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    // Geeft alle leningen van een specifieke klant terug
    public List<LoanResponse> getLoansByCustomerId(Long customerId) {
        return loanRepository.findByCustomerId(customerId)
                .stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    // Geeft één lening terug op basis van ID, gooit 404 als niet gevonden
    public LoanResponse getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Lening met id " + id + " niet gevonden"));
        return loanMapper.toResponse(loan);
    }
    // Maakt een nieuwe lening aan en controleert beschikbaarheid via @Transactional.
    @Transactional
    public LoanResponse createLoan(LoanRequest request) {
        // Exemplaar ophalen en beschikbaarheid controleren
        BookCopy bookCopy = bookCopyRepository.findById(request.bookCopyId())
                .orElseThrow(() -> new RecordNotFoundException("Exemplaar met id " + request.bookCopyId() + " niet gevonden"));

        // Businessregel: exemplaar moet de status AVAILABLE hebben
        if (bookCopy.getStatus() != BookCopyStatus.AVAILABLE) {
            throw new BadRequestException("Exemplaar met id " + request.bookCopyId() + " is niet beschikbaar");
        }

         // Klant ophalen
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new RecordNotFoundException("Klant met id " + request.customerId() + " niet gevonden"));

        // Status van het exemplaar bijwerken naar LOANED
        bookCopy.setStatus(BookCopyStatus.LOANED);
        bookCopyRepository.save(bookCopy);

        // Lening aanmaken en opslaan
        Loan loan = loanMapper.toEntity(request, customer, bookCopy);
        Loan saved = loanRepository.save(loan);
        return loanMapper.toResponse(saved);
    }
    // Verwerkt de retournering en zet het exemplaar terug naar AVAILABLE via @Transactional.
    @Transactional
    public LoanResponse returnLoan(Long id) {
        // Lening ophalen
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Lening met id " + id + " niet gevonden"));

        // Businessregel: lening mag niet al ingeleverd zijn
        if (loan.getReturnDate() != null) {
            throw new BadRequestException("Lening met id " + id + " is al ingeleverd");
        }

        // Retourdatum instellen op vandaag
        loan.setReturnDate(LocalDate.now());

        // Status van het exemplaar terugzetten naar AVAILABLE
        // Geen extra findById nodig: exemplaar is al beschikbaar via de lening
        BookCopy bookCopy = loan.getBookCopy();
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopyRepository.save(bookCopy);
        // Lening opslaan met retourdatum
        Loan saved = loanRepository.save(loan);
        return loanMapper.toResponse(saved);
    }

    // Verwijdert een lening op basis van ID, gooit 404 als niet gevonden
    public void deleteLoan(Long id) {
        if (!loanRepository.existsById(id)) {
            throw new RecordNotFoundException("Lening met id " + id + " niet gevonden");
        }
        loanRepository.deleteById(id);
    }
}