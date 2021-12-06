package integration_tests;

import static io.restassured.RestAssured.given;
import static java.time.Month.JANUARY;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.example.amigoscode_spring_boot_demo.DemoSpringBootDemoApplication;
import com.example.amigoscode_spring_boot_demo.student.Student;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(
    classes = DemoSpringBootDemoApplication.class,
    webEnvironment = WebEnvironment.DEFINED_PORT
)
public class StudentITest {

  private static final String STUDENT_PATH = "http://localhost:8081/api/v1/student";

  @Test
  void createNewStudentShouldBeOk(){
    Student student = new Student(
        "Ahmed2",
        "ahemd2@email.com",
        LocalDate.of(1999, JANUARY, 5)
    );

    RequestSpecification requestSpecification = given().contentType("application/json").body(student);
    Response response = requestSpecification.post(STUDENT_PATH);
    assertThat(response.then().extract().statusCode()).isEqualTo(SC_CREATED);
    response.then().assertThat().body("name", equalTo(student.getName()));
    response.then().assertThat().body("email", equalTo(student.getEmail()));
    response.then().assertThat().body("dateOfBirth", equalTo(student.getDateOfBirth().toString()));
  }
}
