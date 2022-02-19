package integraion_test.helper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSenderOptions;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class ApiAction {
    private final HttpRequest httpRequest;
    private Response response;

    public ApiAction(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public ApiAction send() {
        var url = Constants.BASE_URL + httpRequest.getServiceName();

        var request = RestAssured.given();

        request
                .headers(httpRequest.headers().orElse(Map.of()))
                .contentType(httpRequest.contentType().orElse(ContentType.JSON))
                .formParams(httpRequest.formParams().orElse(Map.of()))
                .queryParams(httpRequest.queryParams().orElse(Map.of()))
                .body(httpRequest.body().orElse(""))
                .cookies(httpRequest.cookies().orElse(Map.of()));

        MyLogger.info(this.getClass().getSimpleName(), "Sending request to --> " + url);
        MyLogger.info(this.getClass().getSimpleName(), "Request --> " + httpRequest);

        response = httpRequest
                .getType()
                .send()
                .apply(request, url);

        MyLogger.info(this.getClass().getSimpleName(), response.asPrettyString());
        return this;
    }

    public ApiAction assertStatusCode(HttpStatus status) {
        response.then().statusCode(status.value());
        return this;
    }

    public Response extractResponse() {
        return response.then().extract().response();
    }

//    public ApiAction assertResponseTime(long expected) {
//        var actual = response.time();
//        return this;
//    }

    public @Data
    static
    class HttpRequest {
        private final RequestTypes type;
        private final String serviceName;
        private Map<String, Object> headers = null;
        private ContentType contentType = null;
        private Map<String, Object> formParams = null;
        private Map<String, Object> queryParams = null;
        private String body = null;
        private Map<String, String> cookies = null;

        public HttpRequest(RequestTypes type, String serviceName) {
            this.type = type;
            this.serviceName = serviceName;
        }

        public Optional<Map<String, Object>> headers() {
            return Optional.ofNullable(headers);
        }

        public Optional<ContentType> contentType() {
            return Optional.ofNullable(contentType);
        }

        public Optional<Map<String, Object>> formParams() {
            return Optional.ofNullable(formParams);
        }

        public Optional<Map<String, Object>> queryParams() {
            return Optional.ofNullable(queryParams);
        }

        public Optional<String> body() {
            return Optional.ofNullable(body);
        }

        public Optional<Map<String, String>> cookies() {
            return Optional.ofNullable(cookies);
        }
    }

    public enum RequestTypes {
        GET(RequestSenderOptions::get), POST(RequestSenderOptions::post), PUT(RequestSenderOptions::put),
        PATCH(RequestSenderOptions::patch), DELETE(RequestSenderOptions::delete);

        private final BiFunction<RequestSpecification, String, Response> function;

        RequestTypes(BiFunction<RequestSpecification, String, Response> function) {
            this.function = function;
        }

        public BiFunction<RequestSpecification, String, Response> send() {
            return function;
        }
    }
}
