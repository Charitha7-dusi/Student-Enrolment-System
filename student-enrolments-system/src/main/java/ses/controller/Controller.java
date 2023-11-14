package ses.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import ses.entities.Batch;
import ses.entities.BatchRepo;
import ses.entities.Course;
import ses.entities.CourseRepo;
import ses.entities.Payment;
import ses.entities.PaymentRepo;
import ses.entities.Student;
import ses.entities.StudentPaymentDTO;
import ses.entities.StudentRepo;

@RestController
public class Controller {

	@Autowired
	StudentRepo studentRepo;

	@Autowired
	PaymentRepo paymentRepo;

	@Autowired
	CourseRepo courseRepo;

	@Autowired
	BatchRepo batchRepo;

// 1
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/courses/add")
	@Operation(summary = "Add a new Course", description = "Adding a new course details")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "New Course Added Successfully"),
			@ApiResponse(responseCode = "400", description = "Course Code is Already exists"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request ") })

	public Object addNewCourse(@Valid @RequestBody Course course) {
//			var course = courseRepo.findById(course.getCode());
//			if (course.isPresent()) {
//				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CourseCode Already Present");
//			}
		try {
			List<Course> existingcourses = courseRepo.findAll();
			boolean exist = existingcourses.contains(course);
			if (exist)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course Already Present");
			courseRepo.save(course);
			return course;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/courses/update/{code}")
	@Operation(summary = "Update a Course By CourseCode", description = "Given a CourseCode it Updates the existing course by the given CourseCode")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Updated Course Successfully"),
			@ApiResponse(responseCode = "400", description = "It's a Bad Request."
					+ "Please double-check and make sure all the details are entered correctly"),
			@ApiResponse(responseCode = "404", description = "CourseCode Not Found"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object updateCourse(@PathVariable("code") String code, @Valid @RequestBody Course updatedCourse) {
		var course = courseRepo.findById(code);
		try {
			if (course.isPresent()) {
				Course existingCourse = course.get();
				existingCourse.setName(updatedCourse.getName());
				existingCourse.setDuration(updatedCourse.getDuration());
				existingCourse.setFee(updatedCourse.getFee());
				return courseRepo.save(existingCourse);
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course with code " + code + " not found");
			}
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}

	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/courses/delete/{code}")
	@Operation(summary = "Delete a Course By CourseCode", description = "Given a CourseCode it deletes the existing Course by the given CourseCde")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Deleted Course Successfully"),
			@ApiResponse(responseCode = "404", description = "Given CourseCode not found"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public void deleteCourse(@PathVariable("code") String code) {
		var course = courseRepo.findById(code);
		try {
			if (course.isPresent())
				courseRepo.deleteById(code);
			else
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course Code Not Found!");
		} catch (ResponseStatusException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An issue occurred on the server while processing the request");
		}
	}

//2
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/batches/add")
	@Operation(summary = "Add a new Batch", description = "Adding a new batch details")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "New Batch Added Successfully"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object addNewBatch(@Valid @RequestBody Batch batch) {
		try {
			List<Batch> existingbatches = batchRepo.findAll();
			boolean exist = existingbatches.contains(batch);
			if (exist)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "batch Already Present");
			batchRepo.save(batch);
			return batch;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/batches/update/{batchid}")
	@Operation(summary = "Update a Batch By Batch Id", description = "Updating an existing batch by the given Batch Id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Batch Updated Successfully"),
			@ApiResponse(responseCode = "400", description = "It's a Bad Request."
					+ "Please double-check and make sure all the details are entered correctly"),
			@ApiResponse(responseCode = "404", description = "BatchId Not Found"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object updateBatch(@PathVariable("batchid") int batchid, @Valid @RequestBody Batch updatedBatch) {
		var batch = batchRepo.findById(batchid);
		try {
			if (batch.isPresent()) {
				Batch existingBatch = batch.get();
				existingBatch.setCode(updatedBatch.getCode());
				existingBatch.setStart(updatedBatch.getStart());
				existingBatch.setEnd(updatedBatch.getEnd());
				existingBatch.setTimings(updatedBatch.getTimings());
				existingBatch.setDuration(updatedBatch.getDuration());
				existingBatch.setFee(updatedBatch.getFee());
				return batchRepo.save(existingBatch);
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch with id " + batch + " not found");
			}
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/batches/delete/{batchid}")
	@Operation(summary = "Delete a Batch By Batch Id", description = "Deleting an existing batch by the given Batch Id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Deleted Batch Successfully"),
			@ApiResponse(responseCode = "404", description = "BatchId not found"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public void deleteBatch(@PathVariable("batchid") int batchid) {
		var batch = batchRepo.findById(batchid);
		try {
			if (batch.isPresent())
				batchRepo.deleteById(batchid);
			else
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch Id Not Found!");
		} catch (ResponseStatusException ex) {
			throw ex;
		} catch (DataAccessException ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An issue occurred on the server while processing the request");
		}
	}

//3
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

//4
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/payments/update/{payid}")
	@Operation(summary = "Update a Payment By Pay Id", description = "Updating an existing Payment by the given Pay Id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Payment Updated Successful"),
			@ApiResponse(responseCode = "400", description = "It's a Bad Request."
					+ "Please double-check and make sure all the details are entered correctly"),
			@ApiResponse(responseCode = "404", description = "Payment Id Not Found"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object updatePayment(@PathVariable("payid") int payid, @RequestBody Payment updatedPayment) {
		var payment = paymentRepo.findById(payid);
		try {
			if (payment.isPresent()) {
				Payment existingpayment = payment.get();
				existingpayment.setAmount(updatedPayment.getAmount());
				existingpayment.setPaydate(updatedPayment.getPaydate());
				existingpayment.setPaymode(updatedPayment.getPaymode());
				return paymentRepo.save(existingpayment);
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment with id " + payid + " not found");
			}
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

// 5
	@GetMapping("/courses")
	@CrossOrigin
	@Operation(summary = "Get All Courses")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Courses Successfully"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public List<Course> getAllCourses() {
		try {
			return courseRepo.findAll();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
	}

//6
	@GetMapping("/runningbatches")
	@Operation(summary = "Get All Currently Running Batches", description = "List of all the Batches that are currently running")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Batches Successfully"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })
	public List<Batch> getRunningBatches() {
		LocalDate today = LocalDate.now();
		List<Batch> batches = batchRepo.findAllRunningBatches(today);
		return batches;
	}

// 7
	@GetMapping("/completedbatches")
	@Operation(summary = "Get All Completed Batches", description = "List Of All The Batches That Are Completed")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Batches Successfully"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object getCompletedBatches() {
		LocalDate today = LocalDate.now();
		List<Batch> batches = batchRepo.findAllCompletedBatches(today);
		return batches;
	}

//8
	@GetMapping("/studentsofrunningbatches")
	@Operation(summary = "Get All Running Batches Students", description = "List Of All The Students Who Are In Current Running Batches ")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retrieved All Students Of Running Batches Successfully"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })
	public List<Student> getStudentsOfRunningBatches() {
		LocalDate currentDate = LocalDate.now();
		List<Student> students = studentRepo.findStudentsOfRunningBatches(currentDate);
		return students;
	}

//9
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
			return students;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

//10
	@GetMapping("/studentsbybatch/{batchid}")
	@Operation(summary = "Get All Students Of A Particular Batch", description = "List Of All The Students By The Given Batch Id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Retreived All Students For The Given Batch Successfully"),
			@ApiResponse(responseCode = "404", description = "BatchId Not Found"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

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
			return students;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

//11
	@GetMapping("/batchesofcourse/{courseCode}")
	@Operation(summary = "Get All Batches Of A Particular Course", description = "List Of All Batches By The Given Course Code")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Batches Successfully"),
			@ApiResponse(responseCode = "404", description = "CourseCode Not Found"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object getAllBatchesForCourse(@PathVariable String courseCode) {
		var course_code = courseRepo.findById(courseCode);
		try {
			if (course_code.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No course code is present");
			}
			List<Batch> batches = batchRepo.findBatchesOfCourse(courseCode);
			return batches;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

//12
	@GetMapping("/students/search")
	@Operation(summary = "Get All Students On The Given Partial Name", description = "List Of All Students By Given Partial Name")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Students Successfully"),
			@ApiResponse(responseCode = "404", description = "No Students were found with that partial name"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object searchStudentsByPartialName(@RequestParam("name") String name) {
		List<Student> students = studentRepo.findByNameContaining(name);
		return students;
	}

//13
	@GetMapping("/batchesbetween")
	@Operation(summary = "Get All Batches Between Two Dates", description = "List Of All Batches That Started Between Given Two Dates")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Batches Successfully"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object getBatchesBetweenDates(@RequestParam("startDate") LocalDate startDate,
			@RequestParam("endDate") LocalDate endDate) {
		List<Batch> batches = batchRepo.findBatchesBetweenDates(startDate, endDate);
		return batches;
	}

//14
	@GetMapping("/payments/{batchid}")
	@Operation(summary = "Get All Payments For A Particular Batch", description = "List Of All Payments By a Given Batch Id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Payments Successfully"),
			@ApiResponse(responseCode = "404", description = "BatchId Not Found"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object getPaymentsForBatch(@PathVariable Integer batchid) {
		var batch_id = batchRepo.findById(batchid);
		try {
			if (batch_id.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No batch id is present");
			}
			List<Payment> payments = paymentRepo.findPaymentsByBatchId(batchid);
			return payments;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

//15
	@GetMapping("/paymentsbetween")
	@Operation(summary = "Get All Payments Between Two Dates", description = "List Of All Payments That Are Made Between Given Two Dates")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Payments Successfully"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object getPaymentsBetweenDates(@RequestParam("startDate") LocalDate startDate,
			@RequestParam("endDate") LocalDate endDate) {
		List<Payment> payments = paymentRepo.findPaymentsBetweenDates(startDate, endDate);
		try {
			if (payments.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No payments are there in specified dates");
			}
			return payments;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

//16
	@GetMapping("/payments/bymode/{mode}")
	@Operation(summary = "Get All Payments Of A Particular Payment Mode", description = "List Of All Payments By Given PayMode")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Payments Successful"),
			@ApiResponse(responseCode = "404", description = "Invalid Payment Mode"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object getPaymentsByMode(@PathVariable("mode") Character mode) {
		var payment = paymentRepo.findByPaymode(mode);
		return payment;
	}

////Other Get requests
//	@GetMapping("/courses/bycode/{code}")
//	@Operation(summary = "Get batches by the given given id!", description = "Given a batch id it retrieves details of batch")
//	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Batches founded by given batch id"),
//			@ApiResponse(responseCode = "404", description = "Batch id not found") })
//	public Object getOneCourse(@PathVariable("code") String code) {
//		try {
//			var course = courseRepo.findById(code);
//			if (course.isPresent()) {
//				return course.get();
//			} else {
//				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coursecode Not Found!");
//			}
//		} catch (ResponseStatusException ex) {
//			return ex.getMessage();
//		} catch (Exception ex) {
//			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
//		}
//
//	}
//
//	@GetMapping("/batches")
//	@Operation(summary = "Get All batches")
//	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All batches Successfully"),
//			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })
//	public List<Batch> getAllBatches() {
//		return batchRepo.findAll();
//	}
//
//	@GetMapping("/batches/byid/{id}")
//	@Operation(summary = "Get batches by the given given id!", description = "Given a batch id it retrieves details of batch")
//	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Batches founded by given batch id"),
//			@ApiResponse(responseCode = "404", description = "Batch id not found") })
//	public Object getOneBatch(@PathVariable("id") int id) {
//		var batch = batchRepo.findById(id);
//		try {
//			if (batch.isPresent())
//				return batch.get();
//			else
//				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch Id Not Found!");
//		} catch (ResponseStatusException ex) {
//			return ex.getMessage();
//		} catch (DataAccessException ex) {
//			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
//					"An issue occurred on the server while processing the request");
//		}
//	}
//
//	@GetMapping("/students")
//	@Operation(summary = "Get all Students")
//	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Students Successfully"),
//			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })
//	public Object getAllStudents() {
//		try {
//			return studentRepo.findAll();
//		} catch (ResponseStatusException ex) {
//			return ex.getMessage();
//		} catch (DataAccessException ex) {
//			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
//					"An issue occurred on the server while processing the request");
//		}
//	}
//
//	@GetMapping("/payments")
//	@Operation(summary = "Get All Payments")
//	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All payments Successfully"),
//			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })
//	public Object getAllPayments() {
//		try {
//			return paymentRepo.findAll();
//		} catch (ResponseStatusException ex) {
//			return ex.getMessage();
//		} catch (DataAccessException ex) {
//			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
//					"An issue occurred on the server while processing the request");
//		}
//	}
//
//	@GetMapping("/payments/byid/{payid}")
//	@Operation(summary = "Get payments by the given payment id!", description = "Given a payment id it retrieves details of payment")
//	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "payment founded by given payment id"),
//			@ApiResponse(responseCode = "404", description = "payment id not found") })
//	public Payment getOnePayment(@PathVariable("payid") int payid) {
//		var batch = paymentRepo.findById(payid);
//		try {
//			if (batch.isPresent())
//				return batch.get();
//			else
//				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment Id Not Found!");
//		} catch (DataAccessException ex) {
//			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
//					"An issue occurred on the server while processing the request");
//		}
//	}

}
