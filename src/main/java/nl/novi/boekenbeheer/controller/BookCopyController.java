package nl.novi.boekenbeheer.controller;

import jakarta.validation.Valid;
import nl.novi.boekenbeheer.dto.request.BookCopyRequest;
import nl.novi.boekenbeheer.dto.response.BookCopyResponse;
import nl.novi.boekenbeheer.service.BookCopyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-copies")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @GetMapping
    public ResponseEntity<List<BookCopyResponse>> getAllBookCopies() {
        return ResponseEntity.ok(bookCopyService.getAllBookCopies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookCopyResponse> getBookCopyById(@PathVariable Long id) {
        return ResponseEntity.ok(bookCopyService.getBookCopyById(id));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BookCopyResponse>> getBookCopiesByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookCopyService.getBookCopiesByBookId(bookId));
    }

    @PostMapping
    public ResponseEntity<BookCopyResponse> createBookCopy(@Valid @RequestBody BookCopyRequest request) {
        BookCopyResponse response = bookCopyService.createBookCopy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookCopy(@PathVariable Long id) {
        bookCopyService.deleteBookCopy(id);
        return ResponseEntity.noContent().build();
    }
}