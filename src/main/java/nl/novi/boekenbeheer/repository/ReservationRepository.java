package nl.novi.boekenbeheer.repository;

import nl.novi.boekenbeheer.entity.Reservation;
import nl.novi.boekenbeheer.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerId(Long customerId);
    List<Reservation> findByBookId(Long bookId);
    List<Reservation> findByStatus(ReservationStatus status);
}