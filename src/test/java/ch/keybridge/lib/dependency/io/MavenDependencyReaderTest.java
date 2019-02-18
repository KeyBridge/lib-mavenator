package ch.keybridge.lib.dependency.io;

import ch.keybridge.lib.dependency.model.MavenDependency;
import ch.keybridge.lib.dependency.model.MavenDependencyScope;
import ch.keybridge.lib.dependency.util.ResourceUtility;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Andrius Druzinis-Vitkus
 * @since 0.0.1 created 2019-02-07
 */
public class MavenDependencyReaderTest {

  @Test
  public void testLineTrimming() {
    assertEquals("keybridge.application:web-cbrs-boundary:war:1.5.1", DependencyReader.trimLine("keybridge" +
        ".application:web-cbrs-boundary:war:1.5.1"));
    assertEquals("keybridge.faces:faces-common:jar:4.0.0:compile", DependencyReader.trimLine("+- keybridge" +
        ".faces:faces-common:jar:4.0.0:compile"));
    assertEquals("org.primefaces:primefaces:jar:6.2:compile", DependencyReader.trimLine("|  +- org" +
        ".primefaces:primefaces:jar:6.2:compile"));
    assertEquals("keybridge.faces:faces-common:jar:4.0.0:compile", DependencyReader.trimLine("+- keybridge" +
        ".faces:faces-common:jar:4.0.0:compile"));
    assertEquals("keybridge.faces:faces-common:jar:4.0.0:compile", DependencyReader.trimLine("+- keybridge" +
        ".faces:faces-common:jar:4.0.0:compile"));
  }

  @Test
  public void testMavenArtifactParsing() {
    MavenDependency dependency;

    dependency = DependencyReader.parseDependency("|     |  \\- jdom:jdom:jar:1.0:compile");
    assertEquals("jdom", dependency.getArtifact().getGroupId());
    assertEquals("jdom", dependency.getArtifact().getArtifactId());
    assertEquals("1.0", dependency.getArtifact().getVersion());
    assertEquals("jar", dependency.getArtifact().getPackaging());
    assertEquals(MavenDependencyScope.COMPILE, dependency.getScope());

    dependency = DependencyReader.parseDependency("\\- org.glassfish.hk2:hk2-api:jar:2.5.0-b62:compile");
    assertEquals("org.glassfish.hk2", dependency.getArtifact().getGroupId());
    assertEquals("hk2-api", dependency.getArtifact().getArtifactId());
    assertEquals("2.5.0-b62", dependency.getArtifact().getVersion());
    assertEquals("jar", dependency.getArtifact().getPackaging());
    assertEquals(MavenDependencyScope.COMPILE, dependency.getScope());
  }

  @Test
  public void testDependencyLevelInTree() {
    assertEquals(0, DependencyReader.getIndent("keybridge.application:web-cbrs-boundary:war:1.5.1"));
    assertEquals(1, DependencyReader.getIndent("+- keybridge.faces:faces-common:jar:4.0.0:compile"));
    assertEquals(2, DependencyReader.getIndent("|  +- org.primefaces:primefaces:jar:6.2:compile"));
    assertEquals(2, DependencyReader.getIndent("|  +- org.ocpsoft.prettytime:prettytime-integration-jsf:jar:4.0.1.Final:compile"));
    assertEquals(3, DependencyReader.getIndent("|  |  \\- org.ocpsoft.prettytime:prettytime:jar:4.0.1.Final:compile"));
    assertEquals(2, DependencyReader.getIndent("|  +- com.vladsch.flexmark:flexmark-ext-tables:jar:0.40.4:compile"));
  }

  @Test
  public void testHierarchyRead() throws IOException {
    final Path dependencyTreeFile = ResourceUtility.getResourcePath("exampledata/outfile-tree.txt");
    MavenDependency hierarchy = DependencyReader.parseDependencyHierarchy(dependencyTreeFile);
    assertEquals(21, hierarchy.getTransitiveDependencies().size());

    assertEquals(DependencyReader.parseArtifact("keybridge.faces:faces-common:jar:4.0.0"), hierarchy.getTransitiveDependencies().get(0).getArtifact());
    assertEquals(DependencyReader.parseArtifact("keybridge.lib:cbrs-boundary-entity:jar:1.2.0"), hierarchy.getTransitiveDependencies().get(1).getArtifact());
    assertEquals(DependencyReader.parseArtifact("keybridge.lib:gis-dto:jar:2.4.0"), hierarchy.getTransitiveDependencies().get(2).getArtifact());

    /**
     * Write to stream. Most efficient, should be used whenever an appendable output stream is available.
     */
    DependencyWriter.printHierarchy(hierarchy, System.out);

    /**
     * Write to string
     */
    StringWriter stringWriter = new StringWriter();
    DependencyWriter.printHierarchy(hierarchy, stringWriter);
    System.out.println(stringWriter.toString());

    /**
     * Write to file
     */
//    DependencyWriter.printHierarchy(hierarchy, Files.newBufferedWriter("/path/to/file"));
  }
}