package ses.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Table(name = "batches")
@Entity
public class Batch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "batchid")
	private int batchid;

	@Column(name = "coursecode")
	@NotBlank(message = "Course code is required")
	@Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Course code should only contain letters and numbers")
	@Size(max = 6, message = "Course code can have a maximum of 6 characters")
	private String code;

	@Column(name = "startdate")
	@NotNull
	private LocalDate start;

	@Column(name = "enddate")
	@NotNull
	private LocalDate end;

	@Column(name = "timings")
	@NotBlank(message = "Timings is required")
	private String timings;

	@Column(name = "duration")
	@Positive(message = "Duration cannot be negative")
	private int duration;

	@Column(name = "fee")
	@Positive(message = "Fee cannot be negative adn zero")
	private int fee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "coursecode", insertable = false, updatable = false)
	private Course course;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "batch")
	@JsonIgnore
	private List<Student> students = new ArrayList<Student>();

	public int getBatchid() {
		return batchid;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	public void setBatchid(int batchid) {
		this.batchid = batchid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public LocalDate getStart() {
		return start;
	}

	public void setStart(LocalDate start) {
		this.start = start;
	}

	public LocalDate getEnd() {
		return end;
	}

	public void setEnd(LocalDate end) {
		this.end = end;
	}

	public String getTimings() {
		return timings;
	}

	public void setTimings(String timings) {
		this.timings = timings;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getFee() {
		return fee;
	}

	public void setFee(int fee) {
		this.fee = fee;
	}

	@Override
	public int hashCode() {
		return Objects.hash(start, timings);
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Batch) {
			Batch other = (Batch) obj;
			return Objects.equals(start, other.start) && Objects.equals(timings, other.timings);
		} else
			return false;
	}

	@Override
	public String toString() {
		return "Batch [batchid=" + batchid + ", code=" + code + ", start=" + start + ", end=" + end + ", timings="
				+ timings + ", duration=" + duration + ", fee=" + fee + "]";
	}

}
