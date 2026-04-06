package com.leandrosps.bug_bash.app.db;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.leandrosps.bug_bash.app.entites.Analysis;

@Repository
public interface AnalysisRepository extends CrudRepository<Analysis, UUID> {
}