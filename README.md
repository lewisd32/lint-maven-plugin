[![travisci](https://travis-ci.org/lewisd32/lint-maven-plugin.svg)](https://travis-ci.org/lewisd32/lint-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.lewisd/lint-maven-plugin/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3Acom.lewisd)


# de.lgohlke.selenium-webdriver
adds some essential webdriver util classes

Note: it is similiar to https://github.com/webdriverextensions/webdriverextensions, but has more emphasis on web automation instead of testing

# usage

in your `pom.xml`
```xml

<project>
    ...
    <properties>
        <drivers.installation.directory>/tmp/webdrivers</drivers.installation.directory>
    </properties>
    ...
    <build>
        <plugins>
            ...
            <plugin>
                <groupId>com.github.webdriverextensions</groupId>
                <artifactId>webdriverextensions-maven-plugin</artifactId>
                <version>1.1.1</version>
                <configuration>
                    <drivers>
                        <driver>
                            <name>chromedriver</name>
                            <platform>linux</platform>
                            <version>2.19</version>
                        </driver>
                        <driver>
                            <name>phantomjs</name>
                            <platform>linux</platform>
                            <version>1.9.7</version>
                        </driver>
                    </drivers>
                    <installationDirectory>${drivers.installation.directory}</installationDirectory>
                    <keepDownloadedWebdrivers>true</keepDownloadedWebdrivers>
                </configuration>
                <executions>
                    <execution>
                        <id>webdriver download</id>
                        <phase>generate-resources</phase>
                        <goals>
                            <goal>install-drivers</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            ...
            
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-failsafe-plugin</artifactId>
                <version>2.18.1</version>
                <executions>
                    <execution>
                        <id>test</id>
                        <goals>
                            <goal>integration-test</goal>
                            <goal>verify</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <includes>
                        <include>**/*IT.java</include>
                    </includes>
                    <environmentVariables>
                        <DRIVERS_PATH>${drivers.installation.directory}</DRIVERS_PATH>
                    </environmentVariables>
                </configuration>
            </plugin>
            ...
        </plugins>
    </build>
    ...
    <dependencies>
        <dependency>
            <groupId>de.lgohlke.selenium</groupId>
            <artifactId>webdriver</artifactId>
            <version>LATEST</version>
        </dependency>
    </dependencies>
</project>
            
```


```java
  import de.lgohlke.junit.DriverService;
  import de.lgohlke.selenium.webdriver.DriverType;
  import org.junit.Rule;
  import org.junit.Test;
  import org.openqa.selenium.WebDriver;
  
  import static org.assertj.core.api.Assertions.assertThat;
  
  public class DemoTest {
      @Rule
      public DriverService driverService = new DriverService(DriverType.CHROME);
      
      @Test
      public void test() throws InterruptedException {
          WebDriver driver = driverService.getDriver();
          driver.get("https://google.de");
          assertThat(driver.getPageSource()).isNotEmpty();
      }
  }
```


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

