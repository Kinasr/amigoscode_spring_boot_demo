package com.example.amigoscode_spring_boot_demo.student;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Entity
@Table
public @Getter @Setter @ToString
class Student {
    @Id
    @SequenceGenerator(
            name = "student_sequence",
            sequenceName = "student_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_sequence"
    )
    private Long id;
    private String name;
    private String email;
    private LocalDate dateOfBirth;
    @Transient
    private Integer age;

    public Student() {
    }

    public Student(String name, String email, LocalDate dateOfBirth) {
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    public Student(Long id, String name, String email, LocalDate dateOfBirth) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    public int getAge() {
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

    public Optional<String> name() {
        return Optional.ofNullable(this.name);
    }

    public Optional<String> email() {
        return Optional.ofNullable(this.email);
    }

    public Optional<LocalDate> dateOfBirth() {
        return Optional.ofNullable(dateOfBirth);
    }
}
