package integraion_test.tests;

import com.example.amigoscode_spring_boot_demo.DemoSpringBootDemoApplication;
import integraion_test.helper.ApiAction;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static integraion_test.helper.ApiAction.RequestTypes.POST;
import static integraion_test.helper.Constants.STUDENT_PATH;

@SpringBootTest(
        classes = DemoSpringBootDemoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class UpdateStudentIT {

    public static ApiAction send(int studentId, String name, String email) {
        var request = new ApiAction.HttpRequest(POST, STUDENT_PATH + "/" + studentId);
        request.setQueryParams(
                Map.of("name", name, "email", email)
        );

        return new ApiAction(request)
                .send();
    }
}
