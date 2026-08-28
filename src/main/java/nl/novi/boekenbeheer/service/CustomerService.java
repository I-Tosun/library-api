package nl.novi.boekenbeheer.service;

import nl.novi.boekenbeheer.dto.request.CustomerRequest;
import nl.novi.boekenbeheer.dto.response.CustomerResponse;
import nl.novi.boekenbeheer.entity.Customer;
import nl.novi.boekenbeheer.exception.DuplicateRecordException;
import nl.novi.boekenbeheer.exception.RecordNotFoundException;
import nl.novi.boekenbeheer.mapper.CustomerMapper;
import nl.novi.boekenbeheer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Klant met id " + id + " niet gevonden"));
        return customerMapper.toResponse(customer);
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateRecordException("Klant met email " + request.email() + " bestaat al");
        }
        Customer customer = customerMapper.toEntity(request);
        Customer saved = customerRepository.save(customer);
        return customerMapper.toResponse(saved);
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Klant met id " + id + " niet gevonden"));
        if (!customer.getEmail().equals(request.email()) &&
                customerRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateRecordException("Klant met email " + request.email() + " bestaat al");
        }
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());
        Customer saved = customerRepository.save(customer);
        return customerMapper.toResponse(saved);
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RecordNotFoundException("Klant met id " + id + " niet gevonden");
        }
        customerRepository.deleteById(id);
    }
}