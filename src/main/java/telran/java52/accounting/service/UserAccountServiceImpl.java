package telran.java52.accounting.service;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java52.accounting.dao.UserRepository;
import telran.java52.accounting.dto.RolesDto;
import telran.java52.accounting.dto.UserDto;
import telran.java52.accounting.dto.UserEditDto;
import telran.java52.accounting.dto.UserRegisterDto;
import telran.java52.accounting.exeption.IncorrectRoleExeption;
import telran.java52.accounting.exeption.UserExistsException;
import telran.java52.accounting.exeption.UserNotFoundExeption;
import telran.java52.accounting.model.Role;
import telran.java52.accounting.model.UserAccount;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService, CommandLineRunner {

	final UserRepository userRepository;
	final ModelMapper modelMapper;
	final PasswordEncoder passwordEncoder;

	@Override
	public UserDto register(UserRegisterDto user) {
		if (userRepository.existsById(user.getLogin())) {
			throw new UserExistsException();
		}
		UserAccount userAccount = modelMapper.map(user, UserAccount.class);
		String password = passwordEncoder.encode(user.getPassword());
		userAccount.setPassword(password);
		userAccount = userRepository.save(userAccount);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto getUser(String login) {
		UserAccount userAccount = userRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto updateUser(String login, UserEditDto user) {
		UserAccount userAccount = userRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
			userAccount.setFirstName(user.getFirstName());
		}
		if (user.getLastName() != null && !user.getLastName().isEmpty()) {
			userAccount.setLastName(user.getLastName());
		}
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
		boolean res;
		try {
			if (isAddRole) {
				res = userAccount.addRole(role);
			} else {
				res = userAccount.removeRole(role);
			}
			if (res) {
				userRepository.save(userAccount);
			}
		} catch (Exception e) {
			throw new IncorrectRoleExeption();
		}
		return modelMapper.map(userAccount, RolesDto.class);
	}

	@Override
	public void changePassword(String login, String newPassword) {
		UserAccount userAccount = userRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		String password = passwordEncoder.encode(newPassword);
		userAccount.setPassword(password);
		userAccount = userRepository.save(userAccount);
	}

	@Override
	public void run(String... args) throws Exception {
		if (!userRepository.existsById("admin")) {
			String password = passwordEncoder.encode("admin");
			UserAccount userAccount = new UserAccount("admin", "", "", password);
			userAccount.addRole(Role.ADMINISTRATOR.name());
			userAccount.addRole(Role.MODERATOR.name());
			userRepository.save(userAccount);
		}
	}

}
