package ses.entities;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Table(name = "payments")
@Entity
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payid")
	private int pay_id;

	@Column(name = "Id")
	@NotNull(message = "Student Id is required")
	private int studentid;

	@Column(name = "amount")
	@Positive(message = "Amount cannot be negative")
	private int amount;

	@Column(name = "paydate")
	@NotNull(message = "Payment date is required")
	private LocalDate paydate;

	@Column(name = "paymode")
	private char paymode;

	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "Id", referencedColumnName = "Id", insertable = false, updatable = false)
	private Student student;

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public int getPay_id() {
		return pay_id;
	}

	public void setPay_id(int pay_id) {
		this.pay_id = pay_id;
	}

	public int getStudentid() {
		return studentid;
	}

	public void setStudentid(int id) {
		this.studentid = id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public LocalDate getPaydate() {
		return paydate;
	}

	public void setPaydate(LocalDate paydate) {
		this.paydate = paydate;
	}

	public char getPaymode() {
		return paymode;
	}

	public void setPaymode(char paymode) {
		this.paymode = paymode;
	}

	@Override
	public int hashCode() {
		return Objects.hash(studentid);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Payment) {
			Payment other = (Payment) obj;
			return Objects.equals(studentid, other.studentid);
		} else
			return false;
	}

	@Override
	public String toString() {
		return "Payment [pay_id=" + pay_id + ", studentid=" + studentid + ", amount=" + amount + ", paydate=" + paydate
				+ ", paymode=" + paymode + "]";
	}

}
