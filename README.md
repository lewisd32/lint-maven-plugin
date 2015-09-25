[![travisci](https://travis-ci.org/lewisd32/lint-maven-plugin.svg)](https://travis-ci.org/lewisd32/lint-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.lewisd/lint-maven-plugin/badge.svg)](https://search.maven.org/#search|ga|1|g%3A%22com.lewisd%22%20AND%20a%3A%22lint-maven-plugin%22)

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
				<version>0.0.8</version>
				<executions>
					<execution>
						<id>pom-lint</id>
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

Configure Rules and Failing on violation
=

```xml
<plugin>
	<groupId>com.lewisd</groupId>
	<artifactId>lint-maven-plugin</artifactId>
	<version>0.0.8</version>
	<configuration>
		<failOnViolation>false</failOnViolation>
		<onlyRunRules>
			<rule>ExecutionId</rule>
		</onlyRunRules>
		<xmlOutputFile>${project.build.directory}/maven-lint-result.xml</xmlOutputFile>
	</configuration>
	<executions>
		<execution>
			<id>pom-lint</id>
			<phase>validate</phase>
			<goals>
				<goal>check</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```

List available Rules
=

```bash
mvn com.lewisd:lint-maven-plugin:list
```

- DotVersionProperty

The convention is to specify properties used to hold versions as some.library.version,
or some-library.version, but never some-library-version or some.library-version.

- VersionProp

The ${version} property is deprecated. Use ${project.version} instead.

- GAVOrder

Maven convention is that the groupId, artifactId, and version elements be listed
in that order. Other elements with short, simple content, such as type, scope, classifier,
etc, should be before elements with longer content, such as configuration, executions,
and exclusions, otherwise they can be easily missed, leading to confusion.

- RedundantDepVersion

Dependency versions should be set in one place, and not overridden without
the version. If, for example, <dependencyManagement> sets a version, and
somewhere overrides it, but with the same version, this can make version
more difficult, due to the repetition.

- RedundantPluginVersion

Plugin versions should be set in one place, and not overridden without changing
version. If, for example, <pluginManagement> sets a version, and <plugins>
overrides it, but with the same version, this can make version upgrades more
due to the repetition.

- ProfileOnlyAddModules

Profiles who's ids match the pattern with-.* must only add modules to the
reactor.

- DuplicateDep

Multiple dependencies, in <dependencies> or <managedDependencies>, with the
co-ordinates are reduntant, and can be confusing. If they have different
they can lead to unexpected behaviour.

- ExecutionId

Executions should always specify an id, so they can be overridden in child
and uniquely identified in build logs.

- OSSDevelopersSectionRule

The users/developers need to know where to get active bugs and to report new
to.

- OSSContinuousIntegrationManagementSectionRule

For better understanding the project a link to the used integration system
users to trust.

- OSSInceptionYearRule

For better understanding the project the inception year of your project is
required.

- OSSIssueManagementSectionRule

The users/developers need to know where to get active bugs and to report new
to.

- OSSLicenseSectionRule

Each project should be licensed under a specific license so the terms of usage
clear.

- OSSUrlSectionRule

For better understanding the project a link to your project is required.

