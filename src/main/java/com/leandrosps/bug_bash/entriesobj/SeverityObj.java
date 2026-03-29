package com.leandrosps.bug_bash.entriesobj;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SeverityObj {
	low, medium, high;

	@JsonCreator
	public static SeverityObj fromString(String value) {
		return SeverityObj.valueOf(value.toLowerCase());
	}
}