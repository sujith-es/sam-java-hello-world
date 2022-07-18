package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Collections;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static final int STATUS_CODE_NO_CONTENT = 204;
    static final int STATUS_CODE_CREATED = 201;

    static final int STATUS_CODE_SERVER_ERROR = 500;
    private final DynamoDbEnhancedClient dbClient;
    private final TableSchema<Employee> employeeTableSchema;

    public App() {

        dbClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(DynamoDbClient.builder()
                        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                        .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                        .httpClientBuilder(UrlConnectionHttpClient.builder())
                        .build())
                .build();

        employeeTableSchema = TableSchema.fromBean(Employee.class);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        int statusCode = STATUS_CODE_NO_CONTENT;

        try {
            String body = input.getBody();
            if (StringUtils.isNotBlank(body)) {
                Employee employee = new Gson().fromJson(body, Employee.class);
                if (employee != null) {
                    DynamoDbTable<Employee> inputTable = dbClient.table("Employee", employeeTableSchema);
                    inputTable.putItem(employee);
                    statusCode = STATUS_CODE_CREATED;
                }
            }

            return new APIGatewayProxyResponseEvent().withStatusCode(statusCode)
                    .withIsBase64Encoded(Boolean.FALSE)
                    .withHeaders(Collections.emptyMap());
        } catch (Exception e) {
            statusCode = STATUS_CODE_SERVER_ERROR;
            return new APIGatewayProxyResponseEvent().withStatusCode(statusCode)
                    .withIsBase64Encoded(Boolean.FALSE)
                    .withHeaders(Collections.emptyMap())
                    .withBody("EXCEPTION: " + e);

        }
    }

//    private String getPageContents(String address) throws IOException {
//        URL url = new URL(address);
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
//            return br.lines().collect(Collectors.joining(System.lineSeparator()));
//        }
//    }
}