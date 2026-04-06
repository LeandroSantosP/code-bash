package com.leandrosps.bug_bash.config;

import java.time.Instant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.leandrosps.bug_bash.app.db.LocalUserRepository;
import com.leandrosps.bug_bash.app.entites.LocalUser;

@Configuration
public class BootstrapLocalUserConfig {

	@Bean
	public CommandLineRunner seedLocalUser(LocalUserRepository localUserRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (localUserRepository.findByUsername("joao").isEmpty()) {
				LocalUser user = new LocalUser();
				user.setUsername("joao");
				user.setPasswordHash(passwordEncoder.encode("senha123"));
				user.setRole("USER");
				user.setEnabled(true);
				user.setCreatedAt(Instant.now());
				localUserRepository.save(user);
			}
		};
	}
}
