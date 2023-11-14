package ses.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import ses.entities.Course;
import ses.entities.CourseRepo;

//@RestController
public class CourseController {

	@Autowired
	CourseRepo courseRepo;

	// 1
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/courses/add")
	@Operation(summary = "Add a new Course", description = "Adding a new course details")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "New Course Added Successfully"),
			@ApiResponse(responseCode = "400", description = "Course Code is Already exists"),
			@ApiResponse(responseCode = "500", description = "An issue occurred on the server while processing a request ") })

	public Object addNewCourse(@Valid @RequestBody Course course) {
//				var course = courseRepo.findById(course.getCode());
//				if (course.isPresent()) {
//					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CourseCode Already Present");
//				}
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

}
