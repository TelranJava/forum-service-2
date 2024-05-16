package telran.java52.accounting.service;

import telran.java52.accounting.dto.RolesDto;
import telran.java52.accounting.dto.UserDto;
import telran.java52.accounting.dto.UserEditDto;
import telran.java52.accounting.dto.UserRegisterDto;
 
public interface UserAccountService { 		// login === userName

	UserDto register(UserRegisterDto user);    			// Create
					
	UserDto getUser(String login);						// Read

	UserDto updateUser(String login, UserEditDto user);	// Update

	UserDto removeUser(String login);	 				// Delete
	
	RolesDto changeRolesList(String login,String roles,boolean isAddRole);	// Update +

	void changePassword(String login, String newPassword);	// Update +
}
