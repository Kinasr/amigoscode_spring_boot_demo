package com.example.amigoscode_spring_boot_demo.student;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static java.time.Month.JULY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;
    private StudentService underTest;

    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

    @Nested
    class GetStudents {
        @Test
        void getAllStudentsShouldInvokeFindAll() {
            // when
            underTest.getStudents();

            // then
            verify(studentRepository).findAll();
        }
    }

    @Nested
    class AddNewStudent {
        @Test
        void shouldCreateNewStudent() {
            // given
            var student = new Student(
                    "Mohamed",
                    "mohamed@email.com",
                    LocalDate.of(2001, JULY, 5)
            );
            given(studentRepository.save(any())).willReturn(new Student(
                    10L,
                    student.getName(),
                    student.getEmail(),
                    student.getDateOfBirth()
            ));

            // when
            underTest.addNewStudent(student);

            // then
            ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);
            verify(studentRepository).save(studentArgumentCaptor.capture());

            var capturedStudent = studentArgumentCaptor.getValue();

            assertThat(capturedStudent).isEqualTo(student);
        }

        @Test
        void createNewStudentWithNameLengthThirtyShouldBeOk() {
            // given
            var student = new Student(
                    "A" + StringUtils.repeat("a", 29),
                    "ahmed@email.com",
                    LocalDate.of(2001, JULY, 5)
            );
            given(studentRepository.save(any())).willReturn(new Student(
                    10L,
                    student.getName(),
                    student.getEmail(),
                    student.getDateOfBirth()
            ));

            // when
            underTest.addNewStudent(student);

            // then
            ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);
            verify(studentRepository).save(studentArgumentCaptor.capture());

            var capturedStudent = studentArgumentCaptor.getValue();

            assertThat(capturedStudent).isEqualTo(student);
        }

        @Test
        void createNewStudentWithNameGreaterThanThirtyShouldReturnTooLongName() {
            // given
            var student = new Student(
                    StringUtils.repeat("a", 31),
                    "ahmed@email.com",
                    LocalDate.of(2001, JULY, 5)
            );

            // when
            var result = underTest.addNewStudent(student);

            // then
            assertThat(result.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().containsKey("error-message")).isTrue();
            assertThat(result.getBody().get("error-message")).isEqualTo("Student Name should be less than 30");
            verify(studentRepository, never()).save(any());
        }

        @Test
        void createNewStudentWithNullNameShouldBeBadRequest() {
            // given
            var student = new Student(
                    null,
                    "ahmed@email.com",
                    LocalDate.of(2001, JULY, 5)
            );

            // when
            var result = underTest.addNewStudent(student);

            // then
            assertThat(result.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().containsKey("error-message")).isTrue();
            assertThat(result.getBody().get("error-message")).isEqualTo("Student name can't be null");
            verify(studentRepository, never()).save(any());
        }

        @ParameterizedTest(name = "{index} -> Creating a new Student with name: [{arguments}], should be bad request")
        @EmptySource
        @ValueSource(strings = {"ali", "A!i", "A1i", "A!1", "!Ali", "1Ali"})
        void createNewStudentWithInvalidNameShouldBeBadRequest(String name) {
            // given
            var student = new Student(
                    name,
                    "ahmed@email.com",
                    LocalDate.of(2001, JULY, 5)
            );

            // when
            var result = underTest.addNewStudent(student);

            // then
            assertThat(result.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().containsKey("error-message")).isTrue();
            assertThat(result.getBody().get("error-message"))
                    .isEqualTo("Student Name should start with capital letter and contains letters only");
            verify(studentRepository, never()).save(any());
        }

        @Test
        void createNewStudentWithNullEmailShouldBeBadRequest() {
            // given
            var student = new Student(
                    "Ahmed",
                    null,
                    LocalDate.of(2001, JULY, 5)
            );

            // when
            var result = underTest.addNewStudent(student);

            // then
            assertThat(result.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().containsKey("error-message")).isTrue();
            assertThat(result.getBody().get("error-message")).isEqualTo("Student email can't be null");
            verify(studentRepository, never()).save(any());
        }

        @ParameterizedTest(name = "{index} -> Creating a new Student with email: [{arguments}], should be bad request")
        @EmptySource
        @ValueSource(strings = {"ahmedemail.com", "@email.com", "ahmedemailcom", "ahmed@emailcom", "@.ahmedemailcom",
        "ahmedemailcom@.", "ahmed@@email.com", "ahmed@email..com", "a@hemd@email.com", "ahmed@email.net"})
        void createNewStudentWithInvalidEmailShouldBeBadRequest(String email) {
            // given
            var student = new Student(
                    "Ahmed",
                    email,
                    LocalDate.of(2001, JULY, 5)
            );

            // when
            var result = underTest.addNewStudent(student);

            // then
            assertThat(result.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().containsKey("error-message")).isTrue();
            assertThat(result.getBody().get("error-message"))
                    .isEqualTo("Student email should follow the following format [alphanumeric]@[alphanumeric].com");
            verify(studentRepository, never()).save(any());
        }

        @Test
        void createNewStudentWithExistedEmailShouldBeConflict() {
            // given
            var student = new Student(
                    "Ahmed",
                    "ahemd@email.com",
                    LocalDate.of(2001, JULY, 5)
            );
            given(studentRepository.findStudentByEmail(anyString()))
                    .willReturn(Optional.of(student));

            // when
            var result = underTest.addNewStudent(student);

            // then
            assertThat(result.getStatusCode()).isEqualTo(CONFLICT);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().containsKey("error-message")).isTrue();
            assertThat(result.getBody().get("error-message"))
                    .isEqualTo("This Email is Taken");
            verify(studentRepository, never()).save(any());
        }
//        @Test
//        void shouldThrowAnExceptionWhenAddingStudentWithExistedEmail() {
//            // given
//            var student = new Student(
//                    "Ahmed",
//                    "ahmed@email.com",
//                    LocalDate.of(2001, JULY, 5)
//            );
//
//            // when
//            given(studentRepository.findStudentByEmail(anyString()))
//                    .willReturn(Optional.of(student));
//
//            // then
//            assertThatThrownBy(() -> underTest.addNewStudent(student))
//                    .isInstanceOf(IllegalStateException.class)
//                    .hasMessageContaining("This Email is Taken");
//            verify(studentRepository, never()).save(any());
//        }

        @Test
        void createNewStudentWithNullDateOfBirthShouldBeBadRequest() {
            // given
            var student = new Student(
                    "Ahmed",
                    "ahmed@email.com",
                    null
            );

            // when
            var result = underTest.addNewStudent(student);

            // then
            assertThat(result.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().containsKey("error-message")).isTrue();
            assertThat(result.getBody().get("error-message")).isEqualTo("Student date of birth can't be null");
            verify(studentRepository, never()).save(any());
        }

        @ParameterizedTest(name = "{index} -> Creating a new Student with date of birth: [{arguments}]," +
                " should be bad request")
        @ValueSource(ints = {-1, 0, 1000, 1799, 2004, 2020, 2050, 50000, 999999999})
        void createNewStudentWithInvalidDateOfBirthShouldBeBadRequest(int year) {
            // given
            var now = LocalDate.now();
            // todo: will be wrong if we in leap year but we try to access this date in a common year
            var student = new Student(
                    "Ahmed",
                    "ahmed@email.com",
                    LocalDate.of(year, now.getMonth(), now.getDayOfMonth())
            );

            // when
            var result = underTest.addNewStudent(student);

            // then
            assertThat(result.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().containsKey("error-message")).isTrue();
            assertThat(result.getBody().get("error-message"))
                    .isEqualTo("Student date of birth should between 1800 and age should be > 18");
            verify(studentRepository, never()).save(any());
        }
    }

    @Test
    @Disabled
    void deleteStudent() {
    }

    @Test
    @Disabled
    void updateStudent() {
    }
}