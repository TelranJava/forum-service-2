package telran.java52.accounting.service;

import telran.java52.accounting.dto.RolesDto;
import telran.java52.accounting.dto.UserDto;
import telran.java52.accounting.dto.UserEditDto;
import telran.java52.accounting.dto.UserRegisterDto;

public class UserAccountServiceImpl implements UserAccountService {
	
	//HW

	@Override
	public UserDto register(UserRegisterDto user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDto getUser(String login) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDto updateUser(String login, UserEditDto user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDto removeUser(String login) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RolesDto changeRolesList(String login, String roles, boolean isAddRole) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changePassword(String login, String newPassword) {
		// TODO Auto-generated method stub

	}

}
