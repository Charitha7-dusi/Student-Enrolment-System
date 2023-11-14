package ses.entities;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BatchRepo extends JpaRepository<Batch, Integer> {

// 6
	@Query("SELECT b FROM Batch b WHERE :today BETWEEN b.start AND b.end")
	List<Batch> findAllRunningBatches(@Param("today") LocalDate today);

// 7
	@Query("SELECT b FROM Batch b WHERE b.end < :today")
	List<Batch> findAllCompletedBatches(@Param("today") LocalDate today);

//11
	@Query("SELECT b FROM Batch b WHERE b.code = :courseCode")
	List<Batch> findBatchesOfCourse(@Param("courseCode") String courseCode);

// 13
	@Query("SELECT b FROM Batch b WHERE b.start BETWEEN :startDate AND :endDate")
	List<Batch> findBatchesBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
