package com.lewisd.maven.lint;

import org.apache.maven.model.InputLocation;
import org.apache.maven.model.InputSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ViolationSuppressorImpl implements ViolationSuppressor {

    private static enum ParserState {
        UNKNOWN, STARTING_TAG, STARTING_COMMENT, IN_COMMENT, IN_END_TAG;
    }

    @Override
    public boolean isSuppressed(final Violation violation) {
        return findSuppressionComment(violation) != null;
    }

    public String findSuppressionComment(final Violation violation) {
        final Rule rule = violation.getRule();
        final InputLocation inputLocation = violation.getInputLocation();
        return findSuppressionComment(rule, inputLocation);
    }

    private String findSuppressionComment(final Rule rule, final InputLocation inputLocation) {
        InputSource source = inputLocation.getSource();

        // can be null in unit tests, file and filereader can not easily mocked
        if (null == source) {
            return null;
        } else {
            File file = new File(source.getLocation());
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));

                int lineNo = 1;
                String line = null;
                while ((line = reader.readLine()) != null && lineNo < inputLocation.getLineNumber()) {
                    lineNo++;
                }
                if (line != null) {
                    if (inputLocation.getColumnNumber() < 1 && inputLocation.getColumnNumber() > line.length()) {
                        return findSuppressionComment(rule, line, reader);
                    } else {
                        final String lineAfterViolation = line.substring(inputLocation.getColumnNumber() - 1);
                        return findSuppressionComment(rule, lineAfterViolation, reader);
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
            return null;
        }
    }

    private String findSuppressionComment(final Rule rule, final String originalLine, final BufferedReader reader) throws IOException {
        String line = originalLine;
        int index = 0;
        String comment = "";
        boolean tagClosed = false;
        ParserState state = ParserState.UNKNOWN;
        while (true) {
            while (index >= line.length()) {
                index = 0;
                line = reader.readLine();
                comment += "\n";
                if (line == null)
                    return null;
            }
            char c = line.charAt(index);
            if (state == ParserState.STARTING_TAG) {
                if (c == '!') {
                    comment = comment + c;
                    state = ParserState.STARTING_COMMENT;
                } else if (c == '/') {
                    if (tagClosed) {
                        // any comments outside of more than one end tag are not considered.
                        return null;
                    }
                    state = ParserState.IN_END_TAG;
                    tagClosed = true;
                } else {
                    return null;
                }
            } else if (state == ParserState.STARTING_COMMENT) {
                comment = comment + c;
                if (c != '-') {
                    state = ParserState.IN_COMMENT;
                }
            } else if (state == ParserState.IN_COMMENT) {
                comment = comment + c;
                if (c == '>') {
                    state = ParserState.UNKNOWN;
                    if (containsSuppression(rule, comment)) {
                        return comment;
                    }
                }
            } else if (state == ParserState.IN_END_TAG) {
                if (c == '>') {
                    state = ParserState.UNKNOWN;
                }
            } else {
                if (c == '<') {
                    state = ParserState.STARTING_TAG;
                    comment = "<";
                }
            }

            ++index;
        }
    }

    private boolean containsSuppression(Rule rule, String comment) {
        return comment.toUpperCase().contains(("NOLINT:" + rule.getIdentifier()).toUpperCase());
    }

}
