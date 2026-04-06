package com.leandrosps.bug_bash.app.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.leandrosps.bug_bash.app.db.LocalUserRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

	private final LocalUserRepository localUserRepository;

	public DatabaseUserDetailsService(LocalUserRepository localUserRepository) {
		this.localUserRepository = localUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var localUser = localUserRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
		return User.withUsername(localUser.getUsername()).password(localUser.getPasswordHash()).roles(localUser.getRole())
				.disabled(!localUser.isEnabled()).build();
	}

}
