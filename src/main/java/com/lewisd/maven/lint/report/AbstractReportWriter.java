package com.lewisd.maven.lint.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public abstract class AbstractReportWriter implements ReportWriter {

	protected FileWriter createOutputFileWriter(final File outputFile) throws IOException {
        File parentFile = outputFile.getAbsoluteFile().getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
		return new FileWriter(outputFile);
	}
	
}
