package ch.keybridge.lib.dependency.io;

import ch.keybridge.lib.dependency.model.MavenArtifact;
import ch.keybridge.lib.dependency.model.MavenDependency;
import ch.keybridge.lib.dependency.model.MavenDependencyScope;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for reading output files created by the maven dependency:tree goal.
 * @author Andrius Druzinis-Vitkus
 * @since 0.0.1 created 2019-02-07
 */
public class DependencyReader {

  /**
   * Remove leading tree-like characters from single line of output from maven dependency:tree, e.g.
   *
   * <pre>
   * keybridge.application:web-cbrs-boundary:war:1.5.0
   * +- keybridge.faces:faces-common:jar:4.0.0:compile
   * |  +- org.primefaces:primefaces:jar:6.2:compile
   * |  +- org.ocpsoft.prettytime:prettytime-integration-jsf:jar:4.0.1.Final:compile
   * |  |  \- org.ocpsoft.prettytime:prettytime:jar:4.0.1.Final:compile
   * |  +- com.vladsch.flexmark:flexmark-ext-tables:jar:0.40.4:compile
   * |  |  +- com.vladsch.flexmark:flexmark-util:jar:0.40.4:compile
   * |  |  +- com.vladsch.flexmark:flexmark:jar:0.40.4:compile
   * |  |  \- com.vladsch.flexmark:flexmark-formatter:jar:0.40.4:compile
   * |  +- com.vladsch.flexmark:flexmark-ext-gitlab:jar:0.40.4:compile
   * |  +- com.vladsch.flexmark:flexmark-ext-gfm-tasklist:jar:0.40.4:compile
   * |  +- com.vladsch.flexmark:flexmark-ext-macros:jar:0.40.4:compile
   * |  \- keybridge.lib:wadl:jar:1.0.0:compile
   * </pre>
   *
   * @return a clean string containing the dependency information
   */
  public static String trimLine(String line) {
    return line.replaceAll("^[-+ |\\\\]+ | +$", "");
  }

  /**
   * Parse a Maven artifact in the form {groupId}:{artifactId}:{packaging}:{version}
   * or {groupId}:{artifactId}:{version}, e.g.:
   * <pre>keybridge.lib:wadl:jar:1.0.0</pre>
   * @param line Maven artifact string
   * @return parsed MavenArtifact object.
   */
  public static MavenArtifact parseArtifact(String line) {
    String[] tokens = trimLine(line).split(":");
    if (tokens.length < 3) {
      throw new IllegalArgumentException("Unexpected number of tokens: " + line);
    }
    String groupId = tokens[0];
    String artifactId = tokens[1];
    String packaging = tokens.length == 4 ? tokens[2] : null;
    String version = tokens.length == 4 ? tokens[3] : tokens[2];
    return new MavenArtifact(groupId, artifactId, packaging, version);
  }

  /**
   * Parse a Maven dependency in the form {groupId}:{artifactId}:{packaging}:{version}:{scope}, e.g.:
   * <pre>keybridge.lib:wadl:jar:1.0.0</pre>
   * @param line Maven artifact string
   * @return parsed MavenArtifact object.
   */
  public static MavenDependency parseDependency(String line) {
    String[] tokens = trimLine(line).split(":");
    if (tokens.length != 5) {
      throw new IllegalArgumentException("Unexpected number of tokens: " + line);
    }
    MavenArtifact artifact = new MavenArtifact(tokens[0], tokens[1], tokens[2], tokens[3]);
    return new MavenDependency(artifact, MavenDependencyScope.fromString(tokens[4]));
  }

  /**
   * Parse the indentation level from a single line of output of the Maven dependency:tree goal.
   *
   * <pre>
   * keybridge.application:web-cbrs-boundary:war:1.5.0                                -> 0
   * +- keybridge.faces:faces-common:jar:4.0.0:compile                                -> 1
   * |  +- org.primefaces:primefaces:jar:6.2:compile                                  -> 2
   * |  +- org.ocpsoft.prettytime:prettytime-integration-jsf:jar:4.0.1.Final:compile  -> 2
   * |  |  \- org.ocpsoft.prettytime:prettytime:jar:4.0.1.Final:compile               -> 3
   * </pre>
   * @param line a single line of output of the Maven dependency:tree goal
   * @return line indent
   */
  public static int getIndent(String line) {
    if (line == null || line.isEmpty()) throw new IllegalArgumentException("Empty string");
    int idx = 0;
    while (true) {
      char c = line.charAt(idx++);
      if (!(c <= ' ' || c == '+' || c == '-' || c == '\\' || c == '|')) break;
    }
    if ((idx - 1) % 3 != 0) throw new IllegalStateException();
    return (idx - 1) / 3;
  }

  /**
   * Parse the output of the Maven dependency:tree goal. Example:
   * <pre>
   * keybridge.application:web-cbrs-boundary:war:1.5.1
   * +- keybridge.faces:faces-common:jar:4.0.0:compile
   * |  +- org.primefaces:primefaces:jar:6.2:compile
   * |  +- org.ocpsoft.prettytime:prettytime-integration-jsf:jar:4.0.1.Final:compile
   * |  |  \- org.ocpsoft.prettytime:prettytime:jar:4.0.1.Final:compile
   * |  +- com.vladsch.flexmark:flexmark-ext-tables:jar:0.40.4:compile
   * |  |  +- com.vladsch.flexmark:flexmark-util:jar:0.40.4:compile
   * |  |  +- com.vladsch.flexmark:flexmark:jar:0.40.4:compile
   * |  |  \- com.vladsch.flexmark:flexmark-formatter:jar:0.40.4:compile
   * |  +- com.vladsch.flexmark:flexmark-ext-gitlab:jar:0.40.4:compile
   * |  +- com.vladsch.flexmark:flexmark-ext-gfm-tasklist:jar:0.40.4:compile
   * |  +- com.vladsch.flexmark:flexmark-ext-macros:jar:0.40.4:compile
   * |  \- keybridge.lib:wadl:jar:1.0.0:compile
   * </pre>
   * @param dependencyTree path to the dependency:tree output
   * @return parsed dependency hierarchy
   * @throws IOException in case of failure to read input file.
   */
  public static MavenDependency parseDependencyHierarchy(Path dependencyTree) throws IOException {
    String line;
    List<MavenDependency> parents = new ArrayList<>();

    try (BufferedReader r = Files.newBufferedReader(dependencyTree)) {
      /**
       * The first line of dependency:tree is the project itself.
       */
      parents.add(new MavenDependency(parseArtifact(r.readLine()), null));

      while ((line = r.readLine()) != null) {
        final int indent = getIndent(line);
        final MavenDependency current = parseDependency(line);

        parents.get(indent - 1).getTransitiveDependencies().add(current);

        if (indent < parents.size()) {
          parents.set(indent, current);
        } else {
          parents.add(current);
        }
      }
    }
    return parents.get(0);
  }
}
