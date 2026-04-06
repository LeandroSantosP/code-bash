package com.leandrosps.bug_bash.entriesobj;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CodeReviewResponse(

		@JsonProperty("feedbackMessage") String feedbackMessage,

		@JsonProperty("severity") SeverityObj severity, // "low", "medium" or "high"

		@JsonProperty("suggestedCode") String suggestedCode,

		@JsonProperty("explanation") String explanation,

		Integer score // 0 to 10
) {
}