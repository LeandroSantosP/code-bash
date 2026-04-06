package com.leandrosps.bug_bash.app.db;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.leandrosps.bug_bash.app.entites.AppUser;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, UUID> {
	Optional<AppUser> findByProviderAndProviderUserId(String provider, String providerUserId);
}
