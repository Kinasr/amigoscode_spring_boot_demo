package integraion_test.tests;

import com.example.amigoscode_spring_boot_demo.DemoSpringBootDemoApplication;
import integraion_test.helper.ApiAction;
import integraion_test.helper.ApiAction.HttpRequest;
import org.springframework.boot.test.context.SpringBootTest;

import static integraion_test.helper.ApiAction.RequestTypes.DELETE;
import static integraion_test.helper.Constants.STUDENT_PATH;

@SpringBootTest(
        classes = DemoSpringBootDemoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class DeleteStudentIT {

    public static ApiAction send(int studentId) {
        return new ApiAction(
          new HttpRequest(DELETE, STUDENT_PATH + "/" + studentId)
        ).send();
    }
}
