package com.leandrosps.bug_bash.app;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.leandrosps.bug_bash.app.entites.Submission;

@Repository
public interface SubmissionsRepository extends CrudRepository<Submission, UUID> {
}
