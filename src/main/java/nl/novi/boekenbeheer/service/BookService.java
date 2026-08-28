package nl.novi.boekenbeheer.service;

import nl.novi.boekenbeheer.dto.request.BookRequest;
import nl.novi.boekenbeheer.dto.response.BookResponse;
import nl.novi.boekenbeheer.entity.Author;
import nl.novi.boekenbeheer.entity.Book;
import nl.novi.boekenbeheer.exception.DuplicateRecordException;
import nl.novi.boekenbeheer.exception.RecordNotFoundException;
import nl.novi.boekenbeheer.mapper.BookMapper;
import nl.novi.boekenbeheer.repository.AuthorRepository;
import nl.novi.boekenbeheer.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookMapper = bookMapper;
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Boek met id " + id + " niet gevonden"));
        return bookMapper.toResponse(book);
    }

    public List<BookResponse> getBooksByCategory(String category) {
        return bookRepository.findByCategory(category)
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    public BookResponse createBook(BookRequest request) {
        if (bookRepository.findByIsbn(request.isbn()).isPresent()) {
            throw new DuplicateRecordException("Boek met ISBN " + request.isbn() + " bestaat al");
        }
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new RecordNotFoundException("Auteur met id " + request.authorId() + " niet gevonden"));
        Book book = bookMapper.toEntity(request, author);
        Book saved = bookRepository.save(book);
        return bookMapper.toResponse(saved);
    }

    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Boek met id " + id + " niet gevonden"));
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new RecordNotFoundException("Auteur met id " + request.authorId() + " niet gevonden"));
        book.setIsbn(request.isbn());
        book.setTitle(request.title());
        book.setPublisher(request.publisher());
        book.setPublicationYear(request.publicationYear());
        book.setCategory(request.category());
        book.setDescription(request.description());
        book.setCoverImagePath(request.coverImagePath());
        book.setAuthor(author);
        Book saved = bookRepository.save(book);
        return bookMapper.toResponse(saved);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RecordNotFoundException("Boek met id " + id + " niet gevonden");
        }
        bookRepository.deleteById(id);
    }
}