package com.leandrosps.bug_bash.app.entites;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("fix_suggestions")
public class FixSuggestion {
    @Id
    private UUID id;
    private UUID analysisId;
    private String originalCode;
    private String suggestedCode;
    private String explanation;
    private Instant createdAt;
}
