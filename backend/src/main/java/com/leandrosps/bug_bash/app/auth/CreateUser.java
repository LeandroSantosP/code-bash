package com.leandrosps.bug_bash.app.auth;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(CreateUser.class);

	private final LocalUserRepository localUserRepository;
	private final PasswordEncoder passwordEncoder;

	public CreateUser(LocalUserRepository localUserRepository, PasswordEncoder passwordEncoder) {
		this.localUserRepository = localUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public record InputCreateUser(
			@NotBlank(message = "username is required") @Size(min = 3, max = 30, message = "username must be between 3 and 30 characters") String username,
			@NotBlank(message = "password is required") @Size(min = 8, max = 100, message = "password must be between 8 and 100 characters") String password) {
	}

	public void exec(InputCreateUser input) {
		this.localUserRepository.findByUsername(input.username()).ifPresentOrElse(existingUser -> {
			throw new AppException("username already exists", HttpStatus.BAD_REQUEST);
		}, () -> {
			LocalUser user = new LocalUser();
			user.setUsername(input.username());
			user.setPasswordHash(passwordEncoder.encode(input.password()));
			user.setRole("USER");
			user.setEnabled(true);
			user.setCreatedAt(Instant.now());
			LOGGER.info("creating local user with username={}", user.getUsername());
			this.localUserRepository.save(user);
		});
	}

}
