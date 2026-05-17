package edu.cit.estrera.wearisit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EstreraApplication {

	public static void main(String[] args) {
		SpringApplication.run(EstreraApplication.class, args);
	}

}
