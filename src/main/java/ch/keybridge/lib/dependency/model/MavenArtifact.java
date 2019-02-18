package ch.keybridge.lib.dependency.model;

import lombok.Value;

/**
 * An immutable Maven artifact DTO.
 *
 * @author Andrius Druzinis-Vitkus
 * @since 0.0.1 created 2019-02-07
 */
@Value
public class MavenArtifact {
  private final String groupId;
  private final String artifactId;
  private final String packaging;
  private final String version;

  public MavenArtifact(String groupId, String artifactId, String packaging, String version) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.packaging = packaging;
  }

  @Override
  public String toString() {
    return groupId + ':' + artifactId + ':' + packaging + ':' + version;
  }

  /**
   * Get a short string identifier of this artifact.
   * @return versioned artifact name
   */
  public String getVersionedArtifactName() {
    return groupId + ':' + artifactId + ':' + version;
  }
}
