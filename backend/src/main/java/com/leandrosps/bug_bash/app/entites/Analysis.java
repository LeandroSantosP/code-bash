package com.leandrosps.bug_bash.app.entites;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.leandrosps.bug_bash.entriesobj.SeverityObj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Table("analyses")
public class Analysis {
	@Id
	private UUID id;
	private UUID submissionId;
	private SeverityObj severity;
	private String feedbackMessage;
	private Instant createdAt;
}
