package ses.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
import jakarta.validation.Valid;
import ses.entities.Batch;
import ses.entities.BatchRepo;
import ses.entities.CourseRepo;

//@RestController
public class BatchController {

	@Autowired
	CourseRepo courseRepo;

	@Autowired
	BatchRepo batchRepo;

	// 2
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

	// 6
	@GetMapping("/runningbatches")
	@Operation(summary = "Get All Currently Running Batches", description = "List of all the Batches that are currently running")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Batches Successfully"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object getRunningBatches() {
		LocalDate today = LocalDate.now();
		List<Batch> batches = batchRepo.findAllRunningBatches(today);
		try {
			if (batches.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Running Batches!");
			}
			return batches;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	// 7
	@GetMapping("/completedbatches")
	@Operation(summary = "Get All Completed Batches", description = "List Of All The Batches That Are Completed")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Batches Successfully"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object getCompletedBatches() {
		LocalDate today = LocalDate.now();
		List<Batch> batches = batchRepo.findAllCompletedBatches(today);
		try {
			if (batches.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No completed Batches exists");
			}
			return batches;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	// 11
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
			if (batches.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No batches are there in this " + courseCode);
			}
			return batches;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	// 13
	@GetMapping("/batchesbetween")
	@Operation(summary = "Get All Batches Between Two Dates", description = "List Of All Batches That Started Between Given Two Dates")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Batches Successfully"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object getBatchesBetweenDates(@RequestParam("startDate") LocalDate startDate,
			@RequestParam("endDate") LocalDate endDate) {
		List<Batch> batches = batchRepo.findBatchesBetweenDates(startDate, endDate);
		try {
			if (batches.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No batches are there between the specified dates");
			}
			return batches;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}
}
