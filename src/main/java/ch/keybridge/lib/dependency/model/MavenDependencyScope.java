package ch.keybridge.lib.dependency.model;

import java.util.Objects;

/**
 * Maven scopes.
 *
 * @see <a href="https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope">Maven documentation</a>
 */
public enum MavenDependencyScope {
  COMPILE,
  PROVIDED,
  RUNTIME,
  TEST,
  SYSTEM,
  IMPORT;

  /**
   * Parse from string (case-insensitive).
   * @param value a scope name
   * @return parsed scope
   */
  public static MavenDependencyScope fromString(String value) {
    Objects.requireNonNull(value, "maven dependency scope");
    return valueOf(value.toUpperCase());
  }
}
