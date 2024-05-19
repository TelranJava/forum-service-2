package telran.java52.accounting.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java52.accounting.dao.UserRepository;
import telran.java52.accounting.dto.RolesDto;
import telran.java52.accounting.dto.UserDto;
import telran.java52.accounting.dto.UserEditDto;
import telran.java52.accounting.dto.UserRegisterDto;
import telran.java52.accounting.exeption.UserNotFoundExeption;
import telran.java52.accounting.model.UserAccount;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

	final UserRepository userRepository;
	final ModelMapper modelMapper;

	@Override
	public UserDto register(UserRegisterDto user) {
		UserAccount userAccount = modelMapper.map(user, UserAccount.class);
		userAccount = userRepository.save(userAccount);
		return modelMapper.map(userAccount, UserDto.class);
	}
//
	@Override
	public UserDto getUser(String login) {
		UserAccount userAccount = userRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto updateUser(String login, UserEditDto user) {
		UserAccount userAccount = userRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		userAccount.setFirstName(user.getFirstName());
		userAccount.setLastName(user.getLastName());
		userAccount = userRepository.save(userAccount);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto removeUser(String login) {
		UserAccount userAccount = userRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		userRepository.deleteById(login);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public RolesDto changeRolesList(String login, String role, boolean isAddRole) {
		UserAccount userAccount = userRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		if (isAddRole) {
			userAccount.addRole(role);
		} else {
			userAccount.removeRole(role);
		}
		userRepository.save(userAccount);
		Set<String> roleSet = userAccount.getRoles().stream().map(r -> r.toString()).collect(Collectors.toSet());
		return new RolesDto(login, roleSet);
	}

	@Override
	public void changePassword(String login, String newPassword) {
		UserAccount userAccount = userRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		userAccount.setPassword(newPassword);
		userRepository.save(userAccount);
	}

}
