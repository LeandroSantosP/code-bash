package com.leandrosps.bug_bash.app.auth;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.leandrosps.bug_bash.app.db.LocalUserRepository;
import com.leandrosps.bug_bash.app.entites.LocalUser;
import com.leandrosps.bug_bash.app.erros.AppException;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Service
public class CreateUser {

	@Autowired
	private LocalUserRepository localUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public record InputCreateUser(
			@NotBlank(message = "username is required") @Size(min = 3, max = 30, message = "username must be between 3 and 30 characters") String username,
			@NotBlank(message = "password is required") @Size(min = 8, max = 100, message = "password must be between 8 and 100 characters") String password) {
	}

	public void exec(InputCreateUser input) {
		this.localUserRepository.findByUsername(input.username()).ifPresentOrElse(existingUser -> {
			throw new AppException("Invalid user User", HttpStatus.BAD_REQUEST);
		}, () -> {
			LocalUser user = new LocalUser();
			user.setUsername(input.username());
			user.setPasswordHash(passwordEncoder.encode(input.password()));
			user.setRole("USER");
			user.setEnabled(true);
			user.setCreatedAt(Instant.now());
			System.out.println("Creating user: " + user.toString());
			this.localUserRepository.save(user);
		});
	}
}
