package com.example.ead_backend;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableJpaRepositories // to enable the JPA Repositories to make the database connection work
@EnableWebMvc // to enable the Spring MVC to make the REST API work
public class EadBackendApplication {

	@Bean // the control of creating the object is given to the Spring Container
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	public static void main(String[] args) {
		SpringApplication.run(EadBackendApplication.class, args);
	}

}
