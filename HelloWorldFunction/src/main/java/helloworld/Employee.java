package helloworld;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Objects;

@DynamoDbBean
public class Employee {

    private String EmployeeId;
    private String LocationId;
    private String DepartmentId;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("EmployeeId")
    public String getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(String employeeId) {
        EmployeeId = employeeId;
    }

    @DynamoDbAttribute("LocationId")
    public String getLocationId() {
        return LocationId;
    }

    public void setLocationId(String locationId) {
        LocationId = locationId;
    }

    @DynamoDbAttribute("DepartmentId")
    public String getDepartmentId() {
        return DepartmentId;
    }

    public void setDepartmentId(String departmentId) {
        DepartmentId = departmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return EmployeeId.equals(employee.EmployeeId) &&
                Objects.equals(LocationId, employee.LocationId)

                && Objects.equals(DepartmentId, employee.DepartmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(EmployeeId, LocationId, DepartmentId);
    }
}
