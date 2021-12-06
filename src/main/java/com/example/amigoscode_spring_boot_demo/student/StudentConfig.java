package com.example.amigoscode_spring_boot_demo.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

import static java.time.Month.JANUARY;
import static java.time.Month.MARCH;

@Configuration
public class StudentConfig {
    @Bean
    CommandLineRunner commandLineRunner(StudentRepository repository) {
        return args -> repository.saveAll(
                List.of(
                        new Student(
                                "Ahmed",
                                "ahemd@email.com",
                                LocalDate.of(1999, JANUARY, 5)
                        ),
                        new Student(
                                "Ali",
                                "ali@email.com",
                                LocalDate.of(2000, MARCH, 3)
                        )
                )
        );
    }
}
