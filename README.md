One-Time Usage Instructions
===========================
If you want to try out lint, or run it one time with minimal hassle and no config changes, follow these simple steps:

1. Run "mvn install" in the lint-maven-plugin folder to install the plugin into your local Maven repository.
2. Change your current folder to one containing a Maven project that you want to lint.
3. Run "mvn com.lewisd:lint-maven-plugin:check".

Add Lint to your Build
===========================
1. Run "mvn install" in the lint-maven-plugin folder to install the plugin into your local Maven repository.
2. Add a plugin declaration to your project's pom file as follows:

```xml	
	[...]
	<build>
		<plugins>
			<plugin>
				<groupId>com.lewisd</groupId>
				<artifactId>lint-maven-plugin</artifactId>
				<version>0.0.7</version>
				<executions>
					<execution>
						<phase>validate</phase>
						<goals>
							<goal>check</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
	[...]
```

3\. Build your project as usual (usually "mvn install"). The build will fail if lint finds a problem.
