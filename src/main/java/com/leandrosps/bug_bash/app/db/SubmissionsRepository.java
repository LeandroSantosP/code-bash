package com.leandrosps.bug_bash.app.db;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.leandrosps.bug_bash.app.entites.Analysis;
import com.leandrosps.bug_bash.app.entites.Submission;

@Repository
public interface SubmissionsRepository extends CrudRepository<Submission, UUID> {
	@Query("SELECT * FROM analyses WHERE submission_id = :id")
	List<Analysis> findAnalysesBySubmissionId(UUID id);
}
