package ses.entities;

import java.time.LocalDate;

public class StudentPaymentDTO {

	private String name;
	private String email;
	private String mobile;
	private int batch_id;
	private LocalDate doj;
	private int amount;
	private LocalDate paydate;
	private char paymode;

	public StudentPaymentDTO(String name, String email, String mobile, int batch_id, LocalDate doj, int amount,
			LocalDate paydate, char paymode) {
		super();
		this.name = name;
		this.email = email;
		this.mobile = mobile;
		this.batch_id = batch_id;
		this.doj = doj;
		this.amount = amount;
		this.paydate = paydate;
		this.paymode = paymode;
	}

	public StudentPaymentDTO() {
		super();
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

}
