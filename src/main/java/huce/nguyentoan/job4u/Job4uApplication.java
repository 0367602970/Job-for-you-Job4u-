package huce.nguyentoan.job4u;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Job4uApplication {

	public static void main(String[] args) {
		SpringApplication.run(Job4uApplication.class, args);
	}

}
