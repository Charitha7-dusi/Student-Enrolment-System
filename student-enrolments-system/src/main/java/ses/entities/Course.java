package ses.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Table(name = "courses")
@Entity
public class Course {

	@Id
	@Column(name = "coursecode")
	@Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Course code should only contain letters and numbers")
	@NotBlank(message = "Course code is required")
	@Size(max = 6, message = "Course code can have a maximum of 6 characters")
	private String code;

	@Column(name = "name")
	@NotBlank(message = "Course Name is required")
	private String name;

	@Column(name = "duration")
	@Positive(message = "Duration cannot be negative")
	private int duration;

	@Column(name = "fee")
	@Positive(message = "Fees Cannot be Negative and Zero")
	private int fee;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
	@JsonIgnore
	private List<Batch> batches = new ArrayList<Batch>();

	public List<Batch> getBatches() {
		return batches;
	}

	public void setBatches(List<Batch> batches) {
		this.batches = batches;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return Objects.hash(code, name);
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Course) {
			Course other = (Course) obj;
			return Objects.equals(code, other.code) && Objects.equals(name, other.name);
		} else
			return false;
	}

	@Override
	public String toString() {
		return "Course [code=" + code + ", name=" + name + ", duration=" + duration + ", fee=" + fee + "]";
	}

}