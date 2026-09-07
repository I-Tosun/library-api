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
import nl.novi.boekenbeheer.util.FileStorageUtil;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;
    private final FileStorageUtil fileStorageUtil;

    public BookService(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            BookMapper bookMapper,
            FileStorageUtil fileStorageUtil) {

        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookMapper = bookMapper;
        this.fileStorageUtil = fileStorageUtil;
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RecordNotFoundException(
                                "Boek met id " + id + " niet gevonden"));

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
            throw new DuplicateRecordException(
                    "Boek met ISBN " + request.isbn() + " bestaat al");
        }

        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() ->
                        new RecordNotFoundException(
                                "Auteur met id " + request.authorId() + " niet gevonden"));

        Book book = bookMapper.toEntity(request, author);

        Book saved = bookRepository.save(book);

        return bookMapper.toResponse(saved);
    }

    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RecordNotFoundException(
                                "Boek met id " + id + " niet gevonden"));

        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() ->
                        new RecordNotFoundException(
                                "Auteur met id " + request.authorId() + " niet gevonden"));

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

    public BookResponse uploadCover(Long id, MultipartFile bestand) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RecordNotFoundException(
                                "Boek met id " + id + " niet gevonden"));

        String bestandsnaam = fileStorageUtil.slaBestandOp(bestand);

        book.setCoverImagePath(bestandsnaam);

        Book saved = bookRepository.save(book);

        return bookMapper.toResponse(saved);
    }

    public Resource downloadCover(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RecordNotFoundException(
                                "Boek met id " + id + " niet gevonden"));

        if (book.getCoverImagePath() == null ||
                book.getCoverImagePath().isBlank()) {

            throw new RecordNotFoundException(
                    "Geen cover beschikbaar voor boek met id " + id);
        }

        return fileStorageUtil.laadBestand(book.getCoverImagePath());
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RecordNotFoundException(
                    "Boek met id " + id + " niet gevonden");
        }

        bookRepository.deleteById(id);
    }
}