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

    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    public List<LoanResponse> getLoansByCustomerId(Long customerId) {
        return loanRepository.findByCustomerId(customerId)
                .stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    public LoanResponse getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Lening met id " + id + " niet gevonden"));
        return loanMapper.toResponse(loan);
    }

    @Transactional
    public LoanResponse createLoan(LoanRequest request) {
        BookCopy bookCopy = bookCopyRepository.findById(request.bookCopyId())
                .orElseThrow(() -> new RecordNotFoundException("Exemplaar met id " + request.bookCopyId() + " niet gevonden"));

        if (bookCopy.getStatus() != BookCopyStatus.AVAILABLE) {
            throw new BadRequestException("Exemplaar met id " + request.bookCopyId() + " is niet beschikbaar");
        }

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new RecordNotFoundException("Klant met id " + request.customerId() + " niet gevonden"));

        bookCopy.setStatus(BookCopyStatus.LOANED);
        bookCopyRepository.save(bookCopy);

        Loan loan = loanMapper.toEntity(request, customer, bookCopy);
        Loan saved = loanRepository.save(loan);
        return loanMapper.toResponse(saved);
    }

    @Transactional
    public LoanResponse returnLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Lening met id " + id + " niet gevonden"));

        if (loan.getReturnDate() != null) {
            throw new BadRequestException("Lening met id " + id + " is al ingeleverd");
        }

        loan.setReturnDate(LocalDate.now());

        BookCopy bookCopy = loan.getBookCopy();
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopyRepository.save(bookCopy);

        Loan saved = loanRepository.save(loan);
        return loanMapper.toResponse(saved);
    }

    public void deleteLoan(Long id) {
        if (!loanRepository.existsById(id)) {
            throw new RecordNotFoundException("Lening met id " + id + " niet gevonden");
        }
        loanRepository.deleteById(id);
    }
}