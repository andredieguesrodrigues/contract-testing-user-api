package com.user.model;

import com.util.Convert;
import lombok.Data;

@Data
public class EmployeeModel extends Convert {
    private Long id;
    private boolean currentEmployee;
    private String name;
}
