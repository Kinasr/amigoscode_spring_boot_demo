package integraion_test.tests;

import com.example.amigoscode_spring_boot_demo.DemoSpringBootDemoApplication;
import integraion_test.helper.ApiAction;
import integraion_test.helper.ApiAction.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;

import static integraion_test.helper.ApiAction.RequestTypes.POST;
import static integraion_test.helper.Constants.STUDENT_PATH;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(
        classes = DemoSpringBootDemoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class RegisterNewStudentIT {
    @Test
    void createNewStudentWithValidDataShouldBeOk() {
        // given
        var timestamp = new Timestamp(System.currentTimeMillis()).getTime();
        var name = "Mark";
        var email = "mark" + timestamp + "@email.com";
        var dateOfBirth = "2000-05-05";
        var age = Period.between(LocalDate.parse(dateOfBirth), LocalDate.now()).getYears();

        // when
        var response = send(name, email, dateOfBirth)
                .assertStatusCode(CREATED)
                .extractResponse();

        // then
        assertThat(response.jsonPath().getInt("data.id")).isNotNull();
        assertThat(response.jsonPath().getString("data.name")).isEqualTo(name);
        assertThat(response.jsonPath().getString("data.email")).isEqualTo(email);
        assertThat(response.jsonPath().getString("data.dateOfBirth")).isEqualTo(dateOfBirth);
        assertThat(response.jsonPath().getInt("data.age")).isEqualTo(age);
    }

    @Nested
    class StudentNameIT {
        @Test
        void createNewStudentWithNullNameShouldBeBadRequest() {
            // given
            // when
            var timestamp = new Timestamp(System.currentTimeMillis()).getTime();
            var response = send(null, "mark"+ timestamp +"@email.com", "2000-05-05")
                    .assertStatusCode(BAD_REQUEST)
                    .extractResponse();
            // then
            assertThat(response.jsonPath().getString("error-message"))
                    .isEqualTo("Student name can't be null");
        }

        @Test
        void createNewStudentWithNameGreaterThanThirty() {
            // given
            // when
            var timestamp = new Timestamp(System.currentTimeMillis()).getTime();
            var response = send("A" + StringUtils.repeat("a", 30),
                    "mark"+ timestamp +"@email.com", "2000-05-05")
                    .assertStatusCode(BAD_REQUEST)
                    .extractResponse();
            // then
            assertThat(response.jsonPath().getString("error-message"))
                    .isEqualTo("Student Name should be less than 30");
        }

        @ParameterizedTest(name = "{index} -> Creating a new Student with name: [{arguments}], should be bad request")
        @EmptySource
        @ValueSource(strings = {"ali", "A!i", "A1i", "A!1", "!Ali", "1Ali"})
        void createNewStudentWithInvalidNameShouldBeBadRequest(String name) {
            // given
            // when
            var timestamp = new Timestamp(System.currentTimeMillis()).getTime();
            var response = send(name, "mark" + timestamp + "@email.com", "2000-05-05")
                    .assertStatusCode(BAD_REQUEST)
                    .extractResponse();
            // then
            assertThat(response.jsonPath().getString("error-message"))
                    .isEqualTo("Student Name should start with capital letter and contains letters only");
        }
    }

    @Nested
    class StudentEmailIT {
        @Test
        void createNewStudentWithNullEmailShouldBeBadRequest() {
            // given
            // when
            var response = send("Mark", null, "2000-05-05")
                    .assertStatusCode(BAD_REQUEST)
                    .extractResponse();
            // then
            assertThat(response.jsonPath().getString("error-message"))
                    .isEqualTo("Student email can't be null");
        }

        @ParameterizedTest(name = "{index} -> Creating a new Student with email: [{arguments}], should be bad request")
        @EmptySource
        @ValueSource(strings = {"ahmedemail.com", "@email.com", "ahmedemailcom", "ahmed@emailcom", "@.ahmedemailcom",
                "ahmedemailcom@.", "ahmed@@email.com", "ahmed@email..com", "a@hemd@email.com", "ahmed@email.net"})
        void createNewStudentWithInvalidEmailShouldBeBadRequest(String email) {
            // given
            // when
            var response = send("Mark", email, "2000-05-05")
                    .assertStatusCode(BAD_REQUEST)
                    .extractResponse();
            // then
            assertThat(response.jsonPath().getString("error-message"))
                    .isEqualTo("Student email should follow the following format [alphanumeric]@[alphanumeric].com");
        }

        @Test
        void createNewStudentWithExistedEmailShouldBeConflict() {
            // given
            var timestamp = new Timestamp(System.currentTimeMillis()).getTime();
            var name = "Test";
            var email = "test" + timestamp + "@email.com";
            var dateOfBirth = "2000-05-05";
            send(name, email, dateOfBirth);

            // when
            var response = send(name, email, dateOfBirth)
                    .assertStatusCode(CONFLICT)
                    .extractResponse();

            // then
            assertThat(response.jsonPath().getString("error-message"))
                    .isEqualTo("This Email is Taken");
        }
    }

    @Nested
    class StudentDateOfBirthIT {
        @Test
        void createNewStudentWithNullDateOfBirthShouldBeBadRequest() {
            // given
            // when
            var timestamp = new Timestamp(System.currentTimeMillis()).getTime();
            var response = send("Mark", "mark"+ timestamp +"@email.com", null)
                    .assertStatusCode(BAD_REQUEST)
                    .extractResponse();

            // then
            assertThat(response.jsonPath().getString("error-message"))
                    .isEqualTo("Student date of birth can't be null");
        }

        @ParameterizedTest(name = "{index} -> Creating a new Student with date of birth: [{arguments}]," +
                " should be bad request")
        @ValueSource(ints = {-1, 0, 1000, 1799, 2004, 2020, 2050, 50000, 999999999})
        void createNewStudentWithInvalidDateOfBirthShouldBeBadRequest(int year) {
            // given
            // when
            var now = LocalDate.now();
            var response = send("Mark", "mark@email.com",
                    LocalDate.of(year, now.getMonth(), now.getDayOfMonth()).toString())
                    .assertStatusCode(BAD_REQUEST)
                    .extractResponse();
            // then
            assertThat(response.jsonPath().getString("error-message"))
                    .isEqualTo("Student date of birth should between 1800 and age should be > 18");
        }
    }

    public static ApiAction send(String name, String email, String dateOfBirth) {
        var request = new HttpRequest(POST, STUDENT_PATH);
        var body = name == null ? "" : "\"name\": \"" + name + "\",\n";

        body += email == null ? "" : "\"email\": \"" + email + "\"";
        body += email != null && dateOfBirth != null ? ",\n" : "";
        body += dateOfBirth == null ? "" : "\"dateOfBirth\": \"" + dateOfBirth + "\"\n";
        request.setBody("{\n" + body + "}");

        return new ApiAction(request)
                .send();
    }
}
