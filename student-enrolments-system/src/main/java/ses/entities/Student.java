package ses.entities;

import java.time.LocalDate;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

@Table(name = "students")
@Entity
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	private int id;

	@Column(name = "name")
	@NotBlank(message = "Student Name is required")
	private String name;

	@Column(name = "email")
	@Pattern(regexp = "^[A-Za-z0-9]+@[A-Za-z]+\\.[A-Za-z]{2,}+$", message = "Invalid email format")
	private String email;

	@Column(name = "mobile")
	@NotBlank(message = "Mobile number is required")
	@Pattern(regexp = "^\\d{10}$", message = "Mobile should only contain 10 numbers")
	private String mobile;

	@Column(name = "batchid")
	@NotNull(message = "Batch ID is required")
	private int batch_id;

	@Column(name = "doj")
	@PastOrPresent(message = "Date of joining should be in the past or present")
	private LocalDate doj;
	@NotNull(message = "Batch ID is required")

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Batchid", insertable = false, updatable = false)
	@JsonIgnore
	private Batch batch;

	@OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private Payment payment;

	public Batch getBatch() {
		return batch;
	}

	public void setBatch(Batch batch) {
		this.batch = batch;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(int batch_id) {
		this.batch_id = batch_id;
	}

	public LocalDate getDoj() {
		return doj;
	}

	public void setDoj(LocalDate doj) {
		this.doj = doj;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email);
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Student) {
			Student other = (Student) obj;
			return Objects.equals(email, other.email);
		} else
			return false;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", email=" + email + ", mobile=" + mobile + ", batch_id="
				+ batch_id + ", doj=" + doj + "]";
	}

}
