package nl.novi.boekenbeheer.service;

import nl.novi.boekenbeheer.dto.request.BookRequest;
import nl.novi.boekenbeheer.dto.response.AuthorResponse;
import nl.novi.boekenbeheer.dto.response.BookResponse;
import nl.novi.boekenbeheer.entity.Author;
import nl.novi.boekenbeheer.entity.Book;
import nl.novi.boekenbeheer.exception.DuplicateRecordException;
import nl.novi.boekenbeheer.exception.RecordNotFoundException;
import nl.novi.boekenbeheer.mapper.BookMapper;
import nl.novi.boekenbeheer.repository.AuthorRepository;
import nl.novi.boekenbeheer.repository.BookRepository;
import nl.novi.boekenbeheer.util.FileStorageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private FileStorageUtil fileStorageUtil;

    @InjectMocks
    private BookService bookService;

    private Author author;
    private Book book;
    private BookRequest bookRequest;
    private BookResponse bookResponse;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setFirstName("J.K.");
        author.setLastName("Rowling");

        book = new Book();
        book.setId(1L);
        book.setIsbn("978-0-7475-3269-9");
        book.setTitle("Harry Potter");
        book.setPublisher("Bloomsbury");
        book.setPublicationYear(1997);
        book.setCategory("Fantasy");
        book.setAuthor(author);

        bookRequest = new BookRequest(
                "978-0-7475-3269-9",
                "Harry Potter",
                "Bloomsbury",
                1997,
                "Fantasy",
                "Beschrijving",
                null,
                1L
        );

        AuthorResponse authorResponse = new AuthorResponse(1L, "J.K.", "Rowling");
        bookResponse = new BookResponse(
                1L,
                "978-0-7475-3269-9",
                "Harry Potter",
                "Bloomsbury",
                1997,
                "Fantasy",
                "Beschrijving",
                null,
                authorResponse
        );
    }

    // getAllBooks

    @Test
    void getAllBooks_returnsAllBooks() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        // Act
        List<BookResponse> result = bookService.getAllBooks();

        // Assert
        assertEquals(1, result.size());
        verify(bookRepository).findAll();
    }

    // getBookById

    @Test
    void getBookById_existingId_returnsBookResponse() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        // Act
        BookResponse result = bookService.getBookById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getBookById_unknownId_throwsRecordNotFoundException() {
        // Arrange
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class,
                () -> bookService.getBookById(99L));
    }

    // getBooksByCategory

    @Test
    void getBooksByCategory_returnsMatchingBooks() {
        // Arrange
        when(bookRepository.findByCategory("Fantasy")).thenReturn(List.of(book));
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        // Act
        List<BookResponse> result = bookService.getBooksByCategory("Fantasy");

        // Assert
        assertEquals(1, result.size());
        verify(bookRepository).findByCategory("Fantasy");
    }

    // createBook

    @Test
    void createBook_newIsbn_createsBook() {
        // Arrange
        when(bookRepository.findByIsbn(bookRequest.isbn())).thenReturn(Optional.empty());
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookMapper.toEntity(bookRequest, author)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        // Act
        BookResponse result = bookService.createBook(bookRequest);

        // Assert
        assertNotNull(result);
        verify(bookRepository).save(book);
    }

    @Test
    void createBook_duplicateIsbn_throwsDuplicateRecordException() {
        // Arrange
        when(bookRepository.findByIsbn(bookRequest.isbn())).thenReturn(Optional.of(book));

        // Act & Assert
        assertThrows(DuplicateRecordException.class,
                () -> bookService.createBook(bookRequest));

        verify(bookRepository, never()).save(any());
    }

    @Test
    void createBook_authorNotFound_throwsRecordNotFoundException() {
        // Arrange
        when(bookRepository.findByIsbn(bookRequest.isbn())).thenReturn(Optional.empty());
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class,
                () -> bookService.createBook(bookRequest));

        verify(bookRepository, never()).save(any());
    }

    // updateBook

    @Test
    void updateBook_existingBook_updatesAndReturns() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        // Act
        BookResponse result = bookService.updateBook(1L, bookRequest);

        // Assert
        assertNotNull(result);
        verify(bookRepository).save(book);
    }

    @Test
    void updateBook_unknownId_throwsRecordNotFoundException() {
        // Arrange
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class,
                () -> bookService.updateBook(99L, bookRequest));

        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateBook_authorNotFound_throwsRecordNotFoundException() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class,
                () -> bookService.updateBook(1L, bookRequest));

        verify(bookRepository, never()).save(any());
    }

    // deleteBook

    @Test
    void deleteBook_existingId_deletesBook() {
        // Arrange
        when(bookRepository.existsById(1L)).thenReturn(true);

        // Act
        bookService.deleteBook(1L);

        // Assert
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_unknownId_throwsRecordNotFoundException() {
        // Arrange
        when(bookRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(RecordNotFoundException.class,
                () -> bookService.deleteBook(99L));

        verify(bookRepository, never()).deleteById(any());
    }

    // uploadCover

    @Test
    void uploadCover_existingBook_savesFilePath() {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(fileStorageUtil.slaBestandOp(mockFile)).thenReturn("uuid.jpg");
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toResponse(book)).thenReturn(bookResponse);

        // Act
        BookResponse result = bookService.uploadCover(1L, mockFile);

        // Assert
        assertNotNull(result);
        assertEquals("uuid.jpg", book.getCoverImagePath());
        verify(bookRepository).save(book);
    }

    @Test
    void uploadCover_unknownId_throwsRecordNotFoundException() {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class,
                () -> bookService.uploadCover(99L, mockFile));

        verify(bookRepository, never()).save(any());
    }

    // downloadCover

    @Test
    void downloadCover_existingCover_returnsResource() {
        // Arrange
        book.setCoverImagePath("uuid.jpg");
        Resource mockResource = mock(Resource.class);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(fileStorageUtil.laadBestand("uuid.jpg")).thenReturn(mockResource);

        // Act
        Resource result = bookService.downloadCover(1L);

        // Assert
        assertNotNull(result);
        verify(fileStorageUtil).laadBestand("uuid.jpg");
    }

    @Test
    void downloadCover_noCover_throwsRecordNotFoundException() {
        // Arrange
        book.setCoverImagePath(null);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act & Assert
        assertThrows(RecordNotFoundException.class,
                () -> bookService.downloadCover(1L));

        verify(fileStorageUtil, never()).laadBestand(any());
    }

    @Test
    void downloadCover_bookNotFound_throwsRecordNotFoundException() {
        // Arrange
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecordNotFoundException.class,
                () -> bookService.downloadCover(99L));

        verify(fileStorageUtil, never()).laadBestand(any());
    }
}