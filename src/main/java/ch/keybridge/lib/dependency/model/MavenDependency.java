package ch.keybridge.lib.dependency.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Value;

/**
 * An immutable DTO that represents a Maven dependency: a Maven artifact, its scope in the POM, and its transitive
 * dependencies.
 *
 * @author Andrius Druzinis-Vitkus
 * @since 0.0.1 created 2019-02-07
 */
@Value
public class MavenDependency {
  private final MavenArtifact artifact;
  private final MavenDependencyScope scope;
  final List<MavenDependency> transitiveDependencies = new ArrayList<>();

  /**
   * Get a flattened collection of dependencies stored in this hierarchy.
   * @return flattened collection of dependencies
   */
  public Collection<MavenDependency> getFlattenedDependencies() {
    Collection<MavenDependency> dependencies = new ArrayList<>();
    addRecursively(this, dependencies);
    return dependencies;
  }

  /**
   * Internal method to traverse the dependency hierarchy.
   * @param dependency a valid MavenDependency
   * @param collector a collection into which all dependencies will be added.
   */
  private static void addRecursively(MavenDependency dependency, Collection<MavenDependency> collector) {
    collector.add(dependency);
    dependency.getTransitiveDependencies().forEach(t -> addRecursively(t, collector));
  }
}
