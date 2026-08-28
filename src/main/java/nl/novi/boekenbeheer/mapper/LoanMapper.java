package nl.novi.boekenbeheer.mapper;

import nl.novi.boekenbeheer.dto.request.LoanRequest;
import nl.novi.boekenbeheer.dto.response.LoanResponse;
import nl.novi.boekenbeheer.entity.BookCopy;
import nl.novi.boekenbeheer.entity.Customer;
import nl.novi.boekenbeheer.entity.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    public Loan toEntity(LoanRequest request, Customer customer, BookCopy bookCopy) {
        Loan loan = new Loan();
        loan.setLoanDate(request.loanDate());
        loan.setDueDate(request.dueDate());
        loan.setCustomer(customer);
        loan.setBookCopy(bookCopy);
        return loan;
    }

    public LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getBookCopy().getId(),
                loan.getBookCopy().getBook().getTitle(),
                loan.getBookCopy().getBarcode(),
                loan.getCustomer().getId(),
                loan.getCustomer().getFirstName() + " " + loan.getCustomer().getLastName()
        );
    }
}