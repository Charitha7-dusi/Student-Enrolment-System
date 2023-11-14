package ses.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import ses.entities.BatchRepo;
import ses.entities.CourseRepo;
import ses.entities.Payment;
import ses.entities.PaymentRepo;
import ses.entities.Student;
import ses.entities.StudentPaymentDTO;
import ses.entities.StudentRepo;

//@RestController
public class StudentController {
	@Autowired
	StudentRepo studentRepo;

	@Autowired
	PaymentRepo paymentRepo;

	@Autowired
	CourseRepo courseRepo;

	@Autowired
	BatchRepo batchRepo;

	// 3
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/addnewstudentandpayment")
	@Operation(summary = "Add Student and Payment", description = "While adding new student,add payment details corresponding to that student")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "New Student And corresponding Payment is Added Successfully"),
			@ApiResponse(responseCode = "400", description = "Student is Already exists"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })
	@Transactional
	public Object addNewStudentPayment(@Valid @RequestBody StudentPaymentDTO studentpayment) {
		try {
			Student s = new Student();
			s.setName(studentpayment.getName());
			s.setEmail(studentpayment.getEmail());
			s.setMobile(studentpayment.getMobile());
			s.setBatch_id(studentpayment.getBatch_id());
			s.setDoj(studentpayment.getDoj());
			List<Student> students = studentRepo.findAll();
			boolean exist = students.contains(s);
			if (exist)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student Already Present");
			studentRepo.save(s);
			Payment p = new Payment();
			p.setStudentid(s.getId());
			p.setAmount(studentpayment.getAmount());
			p.setPaydate(studentpayment.getPaydate());
			p.setPaymode(studentpayment.getPaymode());
			paymentRepo.save(p);
			return studentpayment;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/updatestudentandpayment/{id}")
	@Operation(summary = "Update a Student and Payment By Student Id", description = "While updating an existing Student,update Payment details if requires")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Updated Student And Payment Successfully"),
			@ApiResponse(responseCode = "400", description = "It's a Bad Request."
					+ "Please double-check and make sure all the details are entered correctly"),
			@ApiResponse(responseCode = "404", description = "Student Id Not Found"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })
	@Transactional
	public Object updateStudentAndPayment(@PathVariable("id") Integer id,
			@Valid @RequestBody StudentPaymentDTO studentpayment) {
		try {
			var student = studentRepo.findById(id);
			if (student.isPresent()) {
				Student s = student.get();
				s.setName(studentpayment.getName());
				s.setEmail(studentpayment.getEmail());
				s.setMobile(studentpayment.getMobile());
				s.setBatch_id(studentpayment.getBatch_id());
				s.setDoj(studentpayment.getDoj());
				studentRepo.save(s);
				var payment = paymentRepo.findByStudentid(id);
				if (payment.isPresent()) {
					Payment p = payment.get();
					p.setAmount(studentpayment.getAmount());
					p.setPaydate(studentpayment.getPaydate());
					p.setPaymode(studentpayment.getPaymode());
					paymentRepo.save(p);
				}
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student Id Not Found!");
			}
			return studentpayment;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/students/delete/{id}")
	@Operation(summary = "Delete a Student and Payment By Student Id", description = "While deleting an existing Student,deleting corresponding Payment details of that Student")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Deleted Student By Student Id Successful"),
			@ApiResponse(responseCode = "404", description = "Student Id not found"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public void deleteStudent(@PathVariable("id") int id) {
		var student = studentRepo.findById(id);
		try {
			if (student.isPresent())
				studentRepo.deleteById(id);
			else
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student Id Not Found!");
		} catch (ResponseStatusException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An issue occurred on the server while processing the request");
		}
	}

	// 8
	@GetMapping("/studentsofrunningbatches")
	@Operation(summary = "Get All Running Batches Students", description = "List Of All The Students Who Are In Current Running Batches ")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieved All Students Of Running Batches Successfully"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object getStudentsOfRunningBatches() {
		LocalDate currentDate = LocalDate.now();
		List<Student> students = studentRepo.findStudentsOfRunningBatches(currentDate);
		try {
			if (students.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No students are there in current running batches");
			}
			return students;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	// 9
	@GetMapping("/studentsbycourse/{code}")
	@Operation(summary = "Get All Students For a Particular Course", description = "List Of All The Students By The Given CourseCode")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieved all students for a particular course successfully"),
			@ApiResponse(responseCode = "404", description = "CourseCode Not Found"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object getStudentsByCourse(@PathVariable String code) {
		var course_code = courseRepo.findById(code);
		try {
			if (course_code.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No course code is present");
			}
			List<Student> students = studentRepo.findStudentsByCourse(code);
			if (students.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No students are there in specific course");
			}
			return students;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	// 10
	@GetMapping("/studentsbybatch/{batchid}")
	@Operation(summary = "Get All Students Of A Particular Batch", description = "List Of All The Students By The Given Batch Id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retreived All Students For The Given Batch Successfully"),
			@ApiResponse(responseCode = "404", description = "BatchId Not Found"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

//		public List<Student> getStudentsByBatch(@PathVariable int batchid) {
//			List<Student> students = studentRepo.findStudentsByBatch(batchid);
//			try {
//				if (students.isEmpty()) {
//					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No students are there in specific batch ");
//				}
//				return students;
//			} catch (Exception ex) {
//				return ex.getMessage();
//			}
	//
//		}
	public Object getStudentsByBatch(@PathVariable int batchid,
			@RequestParam(name = "pageSize", required = false, defaultValue = "2") int pageSize, Pageable pageable) {
		Sort sort = Sort.by("id").descending(); // Sort by batch ID in descending order
		var batch_id = batchRepo.findById(batchid);
		try {
			if (batch_id.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No batch id is present");
			}
			Page<Student> students = studentRepo.findStudentsByBatch(batchid,
					PageRequest.of(pageable.getPageNumber(), pageSize, sort));

			if (students.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No students for the given Batch id!");
			}

			return students;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	// 12
	@GetMapping("/students/search")
	@Operation(summary = "Get All Students On The Given Partial Name", description = "List Of All Students By Given Partial Name")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Students Successfully"),
			@ApiResponse(responseCode = "404", description = "No Students were found with that partial name"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object searchStudentsByPartialName(@RequestParam("name") String name) {
		List<Student> students = studentRepo.findByNameContaining(name);
		try {
			if (students.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No students found with that partial name");
			}
			return students;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}
}
