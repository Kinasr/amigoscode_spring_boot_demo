package com.example.amigoscode_spring_boot_demo.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static java.time.Month.JULY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class StudentRepositoryTest {
    @Autowired
    private StudentRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldGetStudentByEmail() {
        // given
        var student = new Student(
                "Ahmed",
                "ahmed@email.com",
                LocalDate.of(2001, JULY, 5)
        );
        underTest.save(student);

        // when
        var expected = underTest.findStudentByEmail(student.getEmail());

        // then
        assertThat(expected.isPresent()).isTrue();
        assertThat(expected.get().getName()).isEqualTo(student.getName());
    }

    @Test
    void shouldGetEmptyWhenStudentIsNotExist() {
        // given
        var student = new Student(
                "Ahmed",
                "ahmed@email.com",
                LocalDate.of(2001, JULY, 5)
        );

        // when
        var expected = underTest.findStudentByEmail(student.getEmail());

        // then
        assertThat(expected.isPresent()).isFalse();
    }
}