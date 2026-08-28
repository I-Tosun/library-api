package nl.novi.boekenbeheer.service;

import nl.novi.boekenbeheer.dto.request.BookCopyRequest;
import nl.novi.boekenbeheer.dto.response.BookCopyResponse;
import nl.novi.boekenbeheer.entity.Book;
import nl.novi.boekenbeheer.entity.BookCopy;
import nl.novi.boekenbeheer.enums.BookCopyStatus;
import nl.novi.boekenbeheer.exception.DuplicateRecordException;
import nl.novi.boekenbeheer.exception.RecordNotFoundException;
import nl.novi.boekenbeheer.mapper.BookCopyMapper;
import nl.novi.boekenbeheer.repository.BookCopyRepository;
import nl.novi.boekenbeheer.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;
    private final BookCopyMapper bookCopyMapper;

    public BookCopyService(BookCopyRepository bookCopyRepository, BookRepository bookRepository, BookCopyMapper bookCopyMapper) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
        this.bookCopyMapper = bookCopyMapper;
    }

    public List<BookCopyResponse> getAllBookCopies() {
        return bookCopyRepository.findAll()
                .stream()
                .map(bookCopyMapper::toResponse)
                .toList();
    }

    public BookCopyResponse getBookCopyById(Long id) {
        BookCopy bookCopy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Exemplaar met id " + id + " niet gevonden"));
        return bookCopyMapper.toResponse(bookCopy);
    }

    public List<BookCopyResponse> getBookCopiesByBookId(Long bookId) {
        return bookCopyRepository.findByBookId(bookId)
                .stream()
                .map(bookCopyMapper::toResponse)
                .toList();
    }

    public BookCopyResponse createBookCopy(BookCopyRequest request) {
        if (bookCopyRepository.findByBarcode(request.barcode()).isPresent()) {
            throw new DuplicateRecordException("Exemplaar met barcode " + request.barcode() + " bestaat al");
        }
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new RecordNotFoundException("Boek met id " + request.bookId() + " niet gevonden"));
        BookCopy bookCopy = bookCopyMapper.toEntity(request, book);
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        BookCopy saved = bookCopyRepository.save(bookCopy);
        return bookCopyMapper.toResponse(saved);
    }

    public void deleteBookCopy(Long id) {
        if (!bookCopyRepository.existsById(id)) {
            throw new RecordNotFoundException("Exemplaar met id " + id + " niet gevonden");
        }
        bookCopyRepository.deleteById(id);
    }
}