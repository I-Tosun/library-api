package nl.novi.boekenbeheer.service;

import nl.novi.boekenbeheer.dto.request.LibraryCardRequest;
import nl.novi.boekenbeheer.dto.response.LibraryCardResponse;
import nl.novi.boekenbeheer.entity.Customer;
import nl.novi.boekenbeheer.entity.LibraryCard;
import nl.novi.boekenbeheer.exception.DuplicateRecordException;
import nl.novi.boekenbeheer.exception.RecordNotFoundException;
import nl.novi.boekenbeheer.mapper.LibraryCardMapper;
import nl.novi.boekenbeheer.repository.CustomerRepository;
import nl.novi.boekenbeheer.repository.LibraryCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibraryCardService {

    private final LibraryCardRepository libraryCardRepository;
    private final CustomerRepository customerRepository;
    private final LibraryCardMapper libraryCardMapper;

    public LibraryCardService(LibraryCardRepository libraryCardRepository,
                              CustomerRepository customerRepository,
                              LibraryCardMapper libraryCardMapper) {
        this.libraryCardRepository = libraryCardRepository;
        this.customerRepository = customerRepository;
        this.libraryCardMapper = libraryCardMapper;
    }

    public List<LibraryCardResponse> getAllLibraryCards() {
        return libraryCardRepository.findAll()
                .stream()
                .map(libraryCardMapper::toResponse)
                .toList();
    }

    public LibraryCardResponse getLibraryCardById(Long id) {
        LibraryCard libraryCard = libraryCardRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("bibliotheekpas met id " + id + " niet gevonden"));
        return libraryCardMapper.toResponse(libraryCard);
    }

    public LibraryCardResponse createLibraryCard(LibraryCardRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new RecordNotFoundException("Klant met id " + request.customerId() + " niet gevonden"));
        if (customer.getLibraryCard() != null) {
            throw new DuplicateRecordException("Klant met id " + request.customerId() + " heeft al een bibliotheekpas");
        }
        LibraryCard libraryCard = libraryCardMapper.toEntity(request, customer);
        LibraryCard saved = libraryCardRepository.save(libraryCard);
        return libraryCardMapper.toResponse(saved);
    }

    public LibraryCardResponse updateLibraryCard(Long id, LibraryCardRequest request) {
        LibraryCard libraryCard = libraryCardRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("LibraryCard with id " + id + " not found"));
        libraryCard.setCardNumber(request.cardNumber());
        libraryCard.setIssueDate(request.issueDate());
        libraryCard.setExpirationDate(request.expirationDate());
        LibraryCard saved = libraryCardRepository.save(libraryCard);
        return libraryCardMapper.toResponse(saved);
    }

    public void deleteLibraryCard(Long id) {
        if (!libraryCardRepository.existsById(id)) {
            throw new RecordNotFoundException("LibraryCard with id " + id + " not found");
        }
        libraryCardRepository.deleteById(id);
    }
}