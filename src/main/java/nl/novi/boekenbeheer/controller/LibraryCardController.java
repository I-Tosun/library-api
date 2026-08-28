package nl.novi.boekenbeheer.controller;

import jakarta.validation.Valid;
import nl.novi.boekenbeheer.dto.request.LibraryCardRequest;
import nl.novi.boekenbeheer.dto.response.LibraryCardResponse;
import nl.novi.boekenbeheer.service.LibraryCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library-cards")
public class LibraryCardController {

    private final LibraryCardService libraryCardService;

    public LibraryCardController(LibraryCardService libraryCardService) {
        this.libraryCardService = libraryCardService;
    }

    @GetMapping
    public ResponseEntity<List<LibraryCardResponse>> getAllLibraryCards() {
        return ResponseEntity.ok(libraryCardService.getAllLibraryCards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibraryCardResponse> getLibraryCardById(@PathVariable Long id) {
        return ResponseEntity.ok(libraryCardService.getLibraryCardById(id));
    }

    @PostMapping
    public ResponseEntity<LibraryCardResponse> createLibraryCard(@Valid @RequestBody LibraryCardRequest request) {
        LibraryCardResponse response = libraryCardService.createLibraryCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibraryCardResponse> updateLibraryCard(@PathVariable Long id,
                                                                 @Valid @RequestBody LibraryCardRequest request) {
        return ResponseEntity.ok(libraryCardService.updateLibraryCard(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibraryCard(@PathVariable Long id) {
        libraryCardService.deleteLibraryCard(id);
        return ResponseEntity.noContent().build();
    }
}