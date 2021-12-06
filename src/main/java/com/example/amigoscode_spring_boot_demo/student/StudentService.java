package com.example.amigoscode_spring_boot_demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final BiPredicate<String, String> isValidNewNameOrEmail = (o, n) ->
            n != null && n.length() > 0 && !Objects.equals(o, n);

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public void addNewStudent(Student student) {
        var studentOptional = studentRepository
                .findStudentByEmail(student.getEmail());
        if (studentOptional.isPresent())
            throw new IllegalStateException("This Email is Taken");

        studentRepository.save(student);
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
