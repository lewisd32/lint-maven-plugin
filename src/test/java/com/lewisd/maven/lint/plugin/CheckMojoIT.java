package com.lewisd.maven.lint.plugin;

import org.apache.commons.io.FileUtils;
import org.apache.maven.cli.MavenCli;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.fest.assertions.api.Assertions.fail;
import static org.junit.Assert.assertTrue;

public class CheckMojoIT {

	@Test
	public void testDefaultProject() throws Exception {
        String path     = "src/test/resources/bugs/issue-4-missing-pluginParameterEvaluation";
        String pom      = path + "/pom.xml";
		String goal     = "com.lewisd:lint-maven-plugin:check";

		File   tempFile = new File(".").createTempFile("log", "log");
		tempFile.deleteOnExit();

		File targetDir = new File(path + "/target");

		// everything except the warnings are going into this log
		String[] args = {goal, "-f", pom, "-l", tempFile.getPath(), "-e"};

		PrintStream oldOut = System.out; // remember old System.out
		try {
			ByteArrayOutputStream stdout = new ByteArrayOutputStream();
			System.setOut(new PrintStream(stdout));

			int result = new MavenCli().doMain(args, ".", null, null);

			if (result != 0) {
				String log = FileUtils.readFileToString(tempFile);
				fail("sth went wrong : " + log);
			}

			assertTrue("expected to have some warnings " + stdout, stdout.toString().contains("WARN "));
		} finally {
			System.setOut(oldOut); // cleanup
            FileUtils.deleteDirectory(targetDir);
		}
	}
}
