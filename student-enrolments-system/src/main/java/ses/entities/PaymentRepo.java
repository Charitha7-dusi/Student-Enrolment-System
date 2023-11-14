package ses.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.validation.Valid;

public interface PaymentRepo extends JpaRepository<Payment, Integer> {

//14
	@Query("SELECT p FROM Payment p WHERE p.student.batch_id = :batchid")
	List<Payment> findPaymentsByBatchId(@Param("batchid") Integer batchid);

//15
	@Query("SELECT p FROM Payment p WHERE p.paydate BETWEEN :startDate AND :endDate")
	List<Payment> findPaymentsBetweenDates(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

//16
	List<Payment> findByPaymode(Character mode);

	Optional<Payment> findByStudentid(@Valid Integer id);

}
