package pt.com.test.consumer;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import pt.com.model.EmployeeModel;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static pt.com.util.Constants.*;
import static pt.com.util.Constants.TEST_CONSUMER;
import static pt.com.util.Support.getAvailablePort;

public class UserTest {

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2(TEST_PROVIDER, LOCALHOST,
            getAvailablePort(), this);

    EmployeeModel employeeModel = new EmployeeModel();

    private DslPart body = new PactDslJsonBody()
            .numberType("id", 1)
            .booleanType("currentEmployee", true)
            .stringType("name", "Andre V");

    @Pact(consumer = TEST_CONSUMER)
    public RequestResponsePact createPact(PactDslWithProvider builder) {

        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, APPLICATION_JSON);

        return builder
                .given(EMPLOYEE_1_EXIST)
                .uponReceiving(GET_REQUEST)
                .path(EMPLOYEES_1)
                .method(GET)
                .willRespondWith()
                .status(STATUS_CODE_OK)
                .headers(headers)
                .body(body)
                .toPact();
    }

    @Test
    @PactVerification()
    public void checkIfEmployee1Exists() {
        setupData();

        Response responseGet = callGetApi();
        verifyGetApi(responseGet);

    }

    private void setupData() {
        employeeModel.setId(1L);
        employeeModel.setCurrentEmployee(true);
        employeeModel.setName(NAME);
    }

    private Response callGetApi() {
        RestAssured.baseURI = mockProvider.getUrl();
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.request(Method.GET, EMPLOYEES_1);
        return response;
    }

    private void verifyGetApi(Response response) {
        assertThat(response.getStatusCode()).isEqualTo(STATUS_CODE_OK);
        assertThat(response.getHeaders().get(CONTENT_TYPE).toString().contains(APPLICATION_JSON)).isTrue();
        assertThat(response.asString().equals(employeeModel.toString()));
    }

}
