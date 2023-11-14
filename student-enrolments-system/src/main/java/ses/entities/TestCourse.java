package ses.entities;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//@SpringBootApplication
public class TestCourse implements CommandLineRunner {
	
	@Autowired
	private CourseRepo courseRepo;
	
	public void displayCourses() {
		for(var c : courseRepo.findAll()) {
			System.out.println(c.toString());
		}
	}
	
	public static void main(String[] args) {
		SpringApplication.run(TestCourse.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		displayCourses();
		
	}
}
