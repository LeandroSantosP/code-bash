package com.leandrosps.bug_bash.app.query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leandrosps.bug_bash.app.db.SubmissionsRepository;
import com.leandrosps.bug_bash.app.entites.Analysis;
import com.leandrosps.bug_bash.app.erros.SubmissionsNotFoundException;

@Service
public class SubmissionsQuery {

	public record GetSubmissionByIdResponse(UUID id, String code, boolean roastMode, int score, Instant createdAt,
			List<Analysis> analyses) {
	}

	@Autowired
	private SubmissionsRepository submissionsRepository;

	public GetSubmissionByIdResponse getSubmissionById(String id) {
		var submissionOut = submissionsRepository.findById(UUID.fromString(id))
				.orElseThrow(() -> new SubmissionsNotFoundException(id));

		var analysesOut = submissionsRepository.findAnalysesBySubmissionId(UUID.fromString(id));
		return new GetSubmissionByIdResponse(submissionOut.getId(), submissionOut.getCode(), submissionOut.isRoastMode(),
				submissionOut.getScore(), submissionOut.getCreatedAt(), analysesOut);
	}
}
