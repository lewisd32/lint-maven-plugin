package com.lewisd.maven.lint.model;

public class Coordinates {

    private final String groupId;
    private final String artifactId;
    private final String type;
    private final String version;

    public Coordinates(final String groupId, final String artifactId) {
        this(groupId, artifactId, null, null);
    }

    public Coordinates(final String groupId, final String artifactId, final String type) {
        this(groupId, artifactId, type, null);
    }

    public Coordinates(final String groupId, final String artifactId, final String type, final String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.type = type;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public boolean matches(final Coordinates coords) {
        if (groupId != null && !groupId.equals(coords.getGroupId())) {
            return false;
        }
        if (artifactId != null && !artifactId.equals(coords.getArtifactId())) {
            return false;
        }
        if (type != null && !type.equals(coords.getType())) {
            return false;
        }
        if (version != null && !version.equals(coords.getVersion())) {
            return false;
        }
        return true;
    }

    public static Coordinates parse(final String coordinate) {
        final String[] parts = coordinate.split(":");
        final String groupId = parts[0];
        final String artifactId = parts[1];
        if (parts.length > 2) {
            throw new IllegalArgumentException("Coordinate format only supports groupId:artifactId");
        }

        return new Coordinates(groupId, artifactId);
    }

}
