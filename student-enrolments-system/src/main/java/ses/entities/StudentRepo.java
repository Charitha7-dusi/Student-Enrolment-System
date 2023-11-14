package ses.entities;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepo extends JpaRepository<Student, Integer> {

//8
	@Query("SELECT s FROM Student s JOIN Batch b ON s.batch = b WHERE b.start <= :currentDate AND b.end >= :currentDate")
	List<Student> findStudentsOfRunningBatches(@Param("currentDate") LocalDate currentDate);

//9
	@Query("SELECT s FROM Student s WHERE s.batch.code = :code")
	List<Student> findStudentsByCourse(@Param("code") String courseCode);

// 10
	@Query("SELECT s FROM Student s WHERE s.batch_id = :batchid")
	Page<Student> findStudentsByBatch(@Param("batchid") int batchid,Pageable pageable);

//12
	List<Student> findByNameContaining(String string);
}
