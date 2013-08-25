package com.lewisd.maven.lint.rules;

import org.apache.maven.model.InputSource;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3ReaderEx;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class MavenProjectUtil {
    public static final String POM_XML_START = "<project>\n";
    public static final String POM_XML_END = "</project>";

    public static MavenProject getMavenProjectFromXML(String pomXML) throws IOException, XmlPullParserException {
        return initNewMavenProject(getMavenModelFromXML(pomXML));
    }

    public static MavenProject getMavenProjectFromPOM(String filename) throws IOException, XmlPullParserException {
        return initNewMavenProject(getMavenModelFromPom(filename));
    }

    private static MavenProject initNewMavenProject(Model mavenModelFromPom) {
        MavenProject mavenProject = new MavenProject(mavenModelFromPom);
        mavenProject.setOriginalModel(mavenModelFromPom);
        return mavenProject;
    }

    private static Model getMavenModelFromXML(String pomXML) throws IOException, XmlPullParserException {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(pomXML.getBytes());
        return new MavenXpp3ReaderEx().read(arrayInputStream, true, new InputSource());
    }

    private static Model getMavenModelFromPom(String filename) throws IOException, XmlPullParserException {
        InputSource inputSource = new InputSource();
        inputSource.setLocation(filename);
        return new MavenXpp3ReaderEx().read(new FileInputStream(filename), true, inputSource);
    }
}
