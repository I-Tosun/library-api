package nl.novi.boekenbeheer.service;

import nl.novi.boekenbeheer.dto.request.LoanRequest;
import nl.novi.boekenbeheer.dto.response.LoanResponse;
import nl.novi.boekenbeheer.entity.Book;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanService loanService;

    private BookCopy bookCopy;
    private Customer customer;
    private Loan loan;
    private LoanRequest loanRequest;
    private LoanResponse loanResponse;

    @BeforeEach
    void setUp() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Harry Potter");

        bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setBarcode("BC-001");
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopy.setBook(book);

        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Test");
        customer.setLastName("Klant");

        loan = new Loan();
        loan.setId(1L);
        loan.setLoanDate(LocalDate.of(2026, 9, 1));
        loan.setDueDate(LocalDate.of(2026, 9, 15));
        loan.setReturnDate(null);
        loan.setBookCopy(bookCopy);
        loan.setCustomer(customer);

        loanRequest = new LoanRequest(1L, 1L,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 15));

        loanResponse = new LoanResponse(1L,
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 15),
                null, 1L, "Harry Potter", "BC-001", 1L, "Test Klant");
    }

    // getAllLoans

    @Test
    void getAllLoans_returnsAllLoans() {
        // Arrange
        when(loanRepository.findAll()).thenReturn(List.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        // Act
        List<LoanResponse> result = loanService.getAllLoans();

        // Assert
        assertEquals(1, result.size());
        verify(loanRepository).findAll();
    }

    // getLoansByCustomerId

    @Test
    void getLoansByCustomerId_returnsLoansForCustomer() {
        // Arrange
        when(loanRepository.findByCustomerId(1L)).thenReturn(List.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        // Act
        List<LoanResponse> result = loanService.getLoansByCustomerId(1L);

        // Assert
        assertEquals(1, result.size());
        verify(loanRepository).findByCustomerId(1L);
    }

    // getLoanById

    @Test
    void getLoanById_existingId_returnsLoanResponse() {
        // Arrange
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        // Act
        LoanResponse result = loanService.getLoanById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getLoanById_unknownId_throwsRecordNotFoundException() {
        // Arrange
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class,
                () -> loanService.getLoanById(99L));
    }

    // createLoan

    @Test
    void createLoan_availableCopyAndExistingCustomer_createsLoan() {
        // Arrange
        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(bookCopy));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(loanMapper.toEntity(loanRequest, customer, bookCopy)).thenReturn(loan);
        when(loanRepository.save(loan)).thenReturn(loan);
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        // Act
        LoanResponse result = loanService.createLoan(loanRequest);

        // Assert
        assertNotNull(result);
        assertEquals(BookCopyStatus.LOANED, bookCopy.getStatus());
        verify(bookCopyRepository).save(bookCopy);
        verify(loanRepository).save(loan);
    }

    @Test
    void createLoan_bookCopyNotFound_throwsRecordNotFoundException() {
        // Arrange
        when(bookCopyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class,
                () -> loanService.createLoan(loanRequest));

        verify(loanRepository, never()).save(any());
    }

    @Test //  extra verify checks toegevoegd
    void createLoan_bookCopyNotAvailable_throwsBadRequestException() {
        // Arrange
        bookCopy.setStatus(BookCopyStatus.LOANED);
        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(bookCopy));

        // Act & Assert
        assertThrows(BadRequestException.class,
                () -> loanService.createLoan(loanRequest));

        verify(customerRepository, never()).findById(any());
        verify(bookCopyRepository, never()).save(any());
        verify(loanRepository, never()).save(any());
    }

    @Test
    void createLoan_customerNotFound_throwsRecordNotFoundException() {
        // Arrange
        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(bookCopy));
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class,
                () -> loanService.createLoan(loanRequest));

        verify(loanRepository, never()).save(any());
    }

    // returnLoan

    @Test
    void returnLoan_activeLoan_setsReturnDateAndAvailable() {
        // Arrange
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);
        when(loanMapper.toResponse(loan)).thenReturn(loanResponse);

        // Act
        LoanResponse result = loanService.returnLoan(1L);

        // Assert
        assertNotNull(loan.getReturnDate());
        assertEquals(BookCopyStatus.AVAILABLE, bookCopy.getStatus());
        verify(bookCopyRepository).save(bookCopy);
        verify(loanRepository).save(loan);
    }

    @Test
    void returnLoan_unknownId_throwsRecordNotFoundException() {
        // Arrange
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class,
                () -> loanService.returnLoan(99L));
    }

    @Test
    void returnLoan_alreadyReturned_throwsBadRequestException() {
        // Arrange
        loan.setReturnDate(LocalDate.of(2026, 9, 10));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        // Act & Assert
        assertThrows(BadRequestException.class,
                () -> loanService.returnLoan(1L));

        verify(loanRepository, never()).save(any());
    }

    // deleteLoan

    @Test
    void deleteLoan_existingId_deletesLoan() {
        // Arrange
        when(loanRepository.existsById(1L)).thenReturn(true);

        // Act
        loanService.deleteLoan(1L);

        // Assert
        verify(loanRepository).deleteById(1L);
    }

    @Test
    void deleteLoan_unknownId_throwsRecordNotFoundException() {
        // Arrange
        when(loanRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(RecordNotFoundException.class,
                () -> loanService.deleteLoan(99L));

        verify(loanRepository, never()).deleteById(any());
    }
}