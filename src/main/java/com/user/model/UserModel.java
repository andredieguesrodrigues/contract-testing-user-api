package com.user.model;

import com.util.Convert;
import lombok.Data;

@Data
public class UserModel extends Convert {
    private Long id;
    private boolean activeUser;
    private String userName;
    private String initialPassword;
}
