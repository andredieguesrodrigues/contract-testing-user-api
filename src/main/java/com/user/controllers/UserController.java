package com.user.controllers;

import com.google.gson.Gson;
import com.user.entities.User;
import com.user.model.EmployeeModel;
import com.user.model.UserModel;
import com.user.repositories.UserRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.util.Support.generateRandomPassword;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserRepository UserRepository;

	@ApiOperation(value = "Insert new user in database",  notes = "Insert by Id, Name and Currentuser")
	@PostMapping(consumes = "application/json")
	public ResponseEntity<?> adduser(@RequestBody User user) {
		User saveduser = UserRepository.save(user);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/users").path("/{id}")
				.buildAndExpand(saveduser.getId()).toUri();

		return ResponseEntity.created(uri).build();
	}

	@ApiOperation(value = "Read all users from database",  notes = "Read all users from database")
	@GetMapping
	public ResponseEntity<List<User>> listusers() {
		return ResponseEntity.ok().body(UserRepository.findAll());
	}

	@ApiOperation(value = "Read all users matching given Id",  notes = "Read all users by Id")
	@GetMapping("/{id}")
	public UserModel searchById(@PathVariable Long id) {

		String uri = "http://localhost:8080/employees/"+id;
		RestTemplate restTemplate =  new RestTemplate();
		String result = restTemplate.getForObject(uri, String.class);

		UserModel user = new Gson().fromJson(result, UserModel.class);
		EmployeeModel employee = new Gson().fromJson(result, EmployeeModel.class);

		if (employee.isCurrentEmployee() == true){
			user.setId(employee.getId());
			user.setUserName(employee.getName()+"_farfetch_pt");
			user.setInitialPassword(generateRandomPassword(6));
			user.setActiveUser(true);
		}
		else{
			user.setId(employee.getId());
			user.setUserName("N/A");
			user.setInitialPassword("N/A");
			user.setActiveUser(false);
		}

		return user;

	}

}


