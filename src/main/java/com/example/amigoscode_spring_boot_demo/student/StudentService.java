package com.example.amigoscode_spring_boot_demo.student;

import com.example.amigoscode_spring_boot_demo.model.ResponseRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiPredicate;

import static org.springframework.http.HttpStatus.*;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final BiPredicate<String, String> isValidNewNameOrEmail = (o, n) ->
            n != null && n.length() > 0 && !Objects.equals(o, n);

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public ResponseEntity<?> getStudents() {
        var students = studentRepository.findAll();
        return students.isEmpty() ?
                new ResponseEntity<>(Map.of("data", ""), NO_CONTENT) :
                new ResponseEntity<>(Map.of("data", students), OK);
    }

    public ResponseEntity<Map<String, Object>> addNewStudent(Student student) {
        var oStudent = Optional.ofNullable(student);
        var response = new ResponseRecord();


        oStudent.ifPresentOrElse(
                s -> {
                    s.name().ifPresentOrElse(
                            n -> {
                                if (n.length() > 30) response
                                        .setData(Map.of("error-message", "Student Name should be less than 30"))
                                        .setStatus(BAD_REQUEST);
                                else if (!n.matches("^[A-Z][a-zA-Z]+")) response
                                        .setData(Map.of("error-message",
                                                "Student Name should start with capital letter " +
                                                        "and contains letters only"))
                                        .setStatus(BAD_REQUEST);
                            },
                            () -> response
                                    .setData(Map.of("error-message", "Student name can't be null"))
                                    .setStatus(BAD_REQUEST)
                    );
                    s.email().ifPresentOrElse(
                            e -> {
                                if (!e.matches("^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.com")) response
                                        .setData(Map.of("error-message", "Student email should " +
                                                "follow the following format [alphanumeric]@[alphanumeric].com"))
                                        .setStatus(BAD_REQUEST);
                            },
                            () -> response
                                    .setData(Map.of("error-message", "Student email can't be null"))
                                    .setStatus(BAD_REQUEST)
                    );
                    s.dateOfBirth().ifPresentOrElse(
                            b -> {
                                if (b.getYear() < 1800 || s.getAge() < 18) response
                                        .setData(Map.of("error-message",
                                                "Student date of birth should between 1800 and age should be > 18"))
                                        .setStatus(BAD_REQUEST);
                            },
                            () -> response
                                    .setData(Map.of("error-message", "Student date of birth can't be null"))
                                    .setStatus(BAD_REQUEST)
                    );
                },
                () -> response
                        .setData(Map.of("error-message", "Invalid request"))
                        .setStatus(BAD_REQUEST)
        );

        if (oStudent.isPresent() && oStudent.get().email().isPresent()) {
            var studentOptional = studentRepository
                    .findStudentByEmail(student.getEmail());
            if (studentOptional.isPresent())
                response.setData(Map.of("error-message", "This Email is Taken"))
                        .setStatus(CONFLICT);
        }

        if (oStudent.isPresent() && response.getData() == null) {
            var savedStudent = studentRepository.save(student);
            response.setData(Map.of("data", savedStudent)).setStatus(OK);
        }

        return new ResponseEntity<>(
                response.getData(),
                response.getStatus()
        );
    }

    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId))
            throw new IllegalStateException("The Student with Id: " + studentId + " is not exist");

        studentRepository.deleteById(studentId);
    }

    @Transactional
    public void updateStudent(Long studentId, String name, String email) {
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalStateException("The Student with Id: " + studentId + " is not exist"));

        if (isValidNewNameOrEmail.test(student.getName(), name))
            student.setName(name);

        if (isValidNewNameOrEmail.test(student.getEmail(), email)) {
            if (studentRepository.findStudentByEmail(email).isPresent())
                throw new IllegalStateException("This Email is Taken");

            student.setEmail(email);
        }
    }
}
