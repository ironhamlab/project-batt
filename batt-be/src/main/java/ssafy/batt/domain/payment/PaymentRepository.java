package ssafy.batt.domain.payment;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.batt.domain.booking.Booking;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBooking(Booking booking);
    
    Optional<Payment> findByPaymentKey(String paymentKey);
}
