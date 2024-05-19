package telran.java52.accounting.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import telran.java52.accounting.dto.RolesDto;
import telran.java52.accounting.dto.UserDto;
import telran.java52.accounting.dto.UserEditDto;
import telran.java52.accounting.dto.UserRegisterDto;
import telran.java52.accounting.service.UserAccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class UserAccountController {

	final UserAccountService accountService;

	@PostMapping("/register")
	public UserDto register(@RequestBody UserRegisterDto user) {
		return accountService.register(user);
	}

	@PostMapping("/login")
	public UserDto login(Principal principal) { // (@RequestHeader("Authorization") String token)
		return accountService.getUser(principal.getName());
	}

	@GetMapping("/user/{login}")
	public UserDto getUser(@PathVariable String login) {
		return accountService.getUser(login);
	}

	@DeleteMapping("/user/{login}")
	public UserDto removeUser(@PathVariable String login) {
		return accountService.removeUser(login);
	}

	@PutMapping("/user/{login}")
	public UserDto updateUser(@PathVariable String login, @RequestBody UserEditDto user) {
		return accountService.updateUser(login, user);
	}

	@PutMapping("/user/{login}/role/{roles}")
	public RolesDto addRole(@PathVariable String login, @PathVariable String role) {
		return accountService.changeRolesList(login, role, true);
	}

	@DeleteMapping("/user/{login}/role/{roles}")
	public RolesDto deleteRole(@PathVariable String login, @PathVariable String role) {
		return accountService.changeRolesList(login, role, false);
	}

	@PutMapping("/user/password")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void changePassword(Principal principal, @RequestHeader("X-Password") String newPassword) { 
		accountService.changePassword(principal.getName(), newPassword);
	}

}
