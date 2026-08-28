package nl.novi.boekenbeheer.service;

import nl.novi.boekenbeheer.dto.request.AuthorRequest;
import nl.novi.boekenbeheer.dto.response.AuthorResponse;
import nl.novi.boekenbeheer.entity.Author;
import nl.novi.boekenbeheer.exception.DuplicateRecordException;
import nl.novi.boekenbeheer.exception.RecordNotFoundException;
import nl.novi.boekenbeheer.mapper.AuthorMapper;
import nl.novi.boekenbeheer.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(authorMapper::toResponse)
                .toList();
    }

    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Auteur met id " + id + " niet gevonden"));
        return authorMapper.toResponse(author);
    }

    public AuthorResponse createAuthor(AuthorRequest request) {
        boolean exists = authorRepository.findByLastName(request.lastName())
                .stream()
                .anyMatch(a -> a.getFirstName().equalsIgnoreCase(request.firstName()));
        if (exists) {
            throw new DuplicateRecordException("Auteur " + request.firstName() + " " + request.lastName() + " bestaat al");
        }
        Author author = authorMapper.toEntity(request);
        Author saved = authorRepository.save(author);
        return authorMapper.toResponse(saved);
    }

    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Author with id " + id + " not found"));
        author.setFirstName(request.firstName());
        author.setLastName(request.lastName());
        Author saved = authorRepository.save(author);
        return authorMapper.toResponse(saved);
    }

    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new RecordNotFoundException("Author with id " + id + " not found");
        }
        authorRepository.deleteById(id);
    }
}