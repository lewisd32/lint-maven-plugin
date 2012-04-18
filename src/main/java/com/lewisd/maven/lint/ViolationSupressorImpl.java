package com.lewisd.maven.lint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.maven.model.InputLocation;

public class ViolationSupressorImpl implements ViolationSupressor {

	private static enum ParserState {
		UNKNOWN, STARTING_TAG, STARTING_COMMENT, IN_COMMENT, IN_END_TAG;
	}

	@Override
	public boolean isSuppressed(Violation violation) {
		return isSuppressed(violation.getRule(), violation.getInputLocation());
	}

	private boolean isSuppressed(final Rule rule, final InputLocation inputLocation) {
		File file = new File(inputLocation.getSource().getLocation());
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			
			int lineNo = 1;
			String line = null;
			while ( (line = reader.readLine()) != null && lineNo < inputLocation.getLineNumber()) {
				lineNo++;
			}
			if (line != null) {
				if (inputLocation.getColumnNumber() < 1 && inputLocation.getColumnNumber() > line.length()) {
					System.err.println("Column number (" + inputLocation.getColumnNumber() + ") out of range. Looking for supression in: " + line);
					return containsSuppression(rule, line, reader);
				} else {
					final String lineAfterViolation = line.substring(inputLocation.getColumnNumber() - 1);
					System.err.println("Looking for supression in: " + lineAfterViolation);
					return containsSuppression(rule, lineAfterViolation, reader);
				}
			}
		} catch (IOException e) {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					// Do nothing here.  Nothing we can do.
				}
			}
			// TODO: need a better exception type here
			throw new RuntimeException("Error while checking for suppression in " + file, e);
		}
		return false;
	}
	
	private boolean containsSuppression(final Rule rule, final String originalLine, final BufferedReader reader) throws IOException {
		String line = originalLine;
		int index = 0;
		String comment = "";
		boolean tagClosed = false;
		ParserState state = ParserState.UNKNOWN;
		while (true) {
			while (index >= line.length()) {
				index = 0;
				line = reader.readLine();
				if (line == null)
					return false;
			}
			char c = line.charAt(index);
			if (state == ParserState.STARTING_TAG) {
				if (c == '!') {
					state = ParserState.STARTING_COMMENT;
				} else if (c == '/') {
					if (tagClosed) {
						// any comments outside of more than one end tag are not considered.
						return false;
					}
					state = ParserState.IN_END_TAG;
					tagClosed = true;
				} else {
					return false;
				}
			} else if (state == ParserState.STARTING_COMMENT) {
				if (c != '-') {
					state = ParserState.IN_COMMENT;
					comment = comment + c;
				}
			} else if (state == ParserState.IN_COMMENT) {
				if (c == '>') {
					state = ParserState.UNKNOWN;
					if (containsSupression(rule, comment)) {
						return true;
					}
				} else {
					comment = comment + c;
				}
			} else if (state == ParserState.IN_END_TAG) {
				if (c == '>') {
					state = ParserState.UNKNOWN;
				}				
			} else {
				if (c == '<') {
					state = ParserState.STARTING_TAG;
				}
			}
			
			++index;
		}
	}

	private boolean containsSupression(Rule rule, String comment) {
		return comment.contains("NOLINT:" + rule.getIdentifier());
	}

}
