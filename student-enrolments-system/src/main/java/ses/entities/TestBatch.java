package ses.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class TestBatch implements CommandLineRunner{
	@Autowired
	private BatchRepo batchRepo;
	
	public void displayBatches() {
		for(var b : batchRepo.findAll()) {
			System.out.println(b.toString());
		}
	}
	
	public static void main(String[] args) {
		SpringApplication.run(TestBatch.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		displayBatches();
		
	}
}
