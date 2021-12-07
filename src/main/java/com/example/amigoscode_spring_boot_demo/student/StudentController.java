package com.example.amigoscode_spring_boot_demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/student")
public class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Getting all students in the database.
     * @return list of students
     */
    @GetMapping
    public ResponseEntity<?> getStudents() {
        return new ResponseEntity<>(studentService.getStudents(), HttpStatus.OK);
    }

    /**
     * Adding new Student under the following conditions
     * - name: should start with capital letter, contains only letters, and be 30 characters at max.
     * - email: should follow the following pattern [alphanumeric]@[alphanumeric].com
     * - date of birth: should be > 1800 and the age should be > 18
     * @param student The record that will be added
     */
    @PostMapping
    public ResponseEntity<?> registerNewStudent(@RequestBody Student student) {
        return studentService.addNewStudent(student);
    }

    /**
     * Deleting a student with his id.
     * @param studentId to be deleted
     */
    @DeleteMapping(path = "{studentId}")
    public void deleteStudent(@PathVariable("studentId") Long studentId) {
        studentService.deleteStudent(studentId);
    }

    /**
     * Updating Student Name, Email, or both using his id.
     * @param studentId to be updated.
     * @param name new student name. "optional"
     * @param email new student email. "optional"
     */
    @PutMapping(path = "{studentId}")
    public void updateStudent(@PathVariable("studentId") Long studentId,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) String email) {
        studentService.updateStudent(studentId, name, email);
    }
}
