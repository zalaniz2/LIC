package LIC.UC04v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class Uc04v1Application {
	public static void main(String[] args) {
		SpringApplication.run(Uc04v1Application.class, args);
	}

}

