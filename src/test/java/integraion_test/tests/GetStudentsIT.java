package integraion_test.tests;

import com.example.amigoscode_spring_boot_demo.DemoSpringBootDemoApplication;
import integraion_test.helper.ApiAction;
import integraion_test.helper.ApiAction.HttpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;

import static integraion_test.helper.ApiAction.RequestTypes.GET;
import static integraion_test.helper.Constants.STUDENT_PATH;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(
        classes = DemoSpringBootDemoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class GetStudentsIT {
    @Test
    void getStudentsShouldBeOk() {
        // given
        var timestamp = new Timestamp(System.currentTimeMillis()).getTime();
        var name = "Test";
        var email = "test" + timestamp + "@email.com";
        var dateOfBirth = "2000-05-05";
        RegisterNewStudentIT.send(name, email, dateOfBirth);

        // when
        var response = send()
                .assertStatusCode(OK)
                .extractResponse();

        // then
        assertThat(response.jsonPath().getString("data")).isNotNull();
    }

    @Test
    void getStudentsWhileNoRecordsInDBShouldBeOk() {
        DeleteStudentIT.send(1);
        DeleteStudentIT.send(2);

        send()
                .assertStatusCode(NO_CONTENT);
    }

    public static ApiAction send() {
        return new ApiAction(
                new HttpRequest(GET, STUDENT_PATH)
        ).send();
    }
}
