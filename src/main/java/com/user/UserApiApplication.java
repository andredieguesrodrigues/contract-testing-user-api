package com.user;

import com.util.File;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserApiApplication {

    public static void main(String[] args) {

        if (File.readProperty("execution.mode").equals("real"))
            SpringApplication.run(UserApiApplication.class, args);
        else
            com.user.mock.WiremockManager.mockAPi();
    }

}
