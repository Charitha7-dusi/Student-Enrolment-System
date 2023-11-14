package ses.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import ses.entities.BatchRepo;
import ses.entities.Payment;
import ses.entities.PaymentRepo;

//@RestController
public class PaymentController {

	@Autowired
	PaymentRepo paymentRepo;

	@Autowired
	BatchRepo batchRepo;

//	4
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

	// 14
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
			if (payments.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No payments are there in specified batch id");
			}
			return payments;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	// 15
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

	// 16
	@GetMapping("/payments/bymode/{mode}")
	@Operation(summary = "Get All Payments Of A Particular Payment Mode", description = "List Of All Payments By Given PayMode")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved All Payments Successful"),
			@ApiResponse(responseCode = "404", description = "Invalid Payment Mode"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request") })

	public Object getPaymentsByMode(@PathVariable("mode") Character mode) {
		var payment = paymentRepo.findByPaymode(mode);
		try {
			if (payment.isEmpty())
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment Mode Not Found!");
			return payment;
		} catch (ResponseStatusException ex) {
			return ex.getMessage();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

}
