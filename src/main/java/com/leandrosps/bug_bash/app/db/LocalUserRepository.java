package com.leandrosps.bug_bash.app.db;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.leandrosps.bug_bash.app.entites.LocalUser;

@Repository
public interface LocalUserRepository extends CrudRepository<LocalUser, UUID> {
	Optional<LocalUser> findByUsername(String username);
}
