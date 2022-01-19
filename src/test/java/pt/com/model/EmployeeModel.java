package pt.com.model;

import lombok.Data;
import pt.com.util.Support;

@Data
public class EmployeeModel extends Support {
    private Long id;
    private boolean currentEmployee;
    private String name;
}
