package ch.keybridge.lib.dependency.io;

import ch.keybridge.lib.dependency.model.MavenArtifact;
import ch.keybridge.lib.dependency.model.MavenDependency;
import java.io.IOException;
import java.util.Arrays;

/**
 * Utilities for printing maven dependency hierarchies.
 *
 * @author Andrius Druzinis-Vitkus
 * @since 0.0.1 created 2019-02-07
 */
public class DependencyWriter {

  /**
   * Print dependency hierarchy into an Appendable stream, such as System.out.
   *
   * How to use:
   *
   * <pre>
   * //Write to stream. Most efficient, should be used whenever an appendable output stream is available.
   * DependencyWriter.printHierarchy(hierarchy,System.out);
   *
   * // Write to string
   * StringWriter stringWriter = new StringWriter();
   * DependencyWriter.printHierarchy(hierarchy,stringWriter);
   * System.out.println(stringWriter.toString());
   *
   * // Write to file
   * DependencyWriter.printHierarchy(hierarchy, Files.newBufferedWriter("/path/to/file"));
   * </pre>
   *
   * @param dependency dependency hierarchy
   * @param appendable appendable output stream
   * @throws IOException in case of failure to write to stream.
   */
  public static void printHierarchy(MavenDependency dependency, Appendable appendable) throws IOException {
    printArtifact(dependency.getArtifact(), appendable);
    appendable.append('\n');
    for (MavenDependency d : dependency.getTransitiveDependencies()) {
      printHierarchy(d, appendable, 1);
    }
  }

  /**
   * Print maven artifact information into an Appendable stream, such as System.out.
   * @param artifact Maven artifact
   * @param appendable appendable output stream
   * @throws IOException in case of failure to write to stream.
   */
  private static void printArtifact(MavenArtifact artifact, Appendable appendable) throws IOException {
    appendable.append(artifact.getGroupId());
    appendable.append(':');
    appendable.append(artifact.getArtifactId());
    appendable.append(':');
    appendable.append(artifact.getPackaging());
    appendable.append(':');
    appendable.append(artifact.getVersion());
  }

  /**
   * Print maven artifact information into an Appendable stream, such as System.out.
   * @param dependency Maven dependency
   * @param appendable appendable output stream
   * @throws IOException in case of failure to write to stream.
   */
  private static void printHierarchy(MavenDependency dependency, Appendable appendable, int offset) throws IOException {
    char[] indent = new char[offset];
    Arrays.fill(indent, '\t');
    String indentStr = String.valueOf(indent);

    appendable.append(indentStr);
    printArtifact(dependency.getArtifact(), appendable);
    appendable.append('\n');
    for (MavenDependency transitiveDep : dependency.getTransitiveDependencies()) {
      printHierarchy(transitiveDep, appendable, offset + 1);
    }
  }
}
