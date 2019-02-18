package ch.keybridge.lib.dependency;

import ch.keybridge.lib.dependency.io.DependencyWriter;
import ch.keybridge.lib.dependency.model.License;
import ch.keybridge.lib.dependency.model.MavenArtifact;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Andrius Druzinis-Vitkus
 * @since 0.0.1 created 2019-02-08
 */
public class AppMetadataTest {
  private AppMetadata metadata;

  @Before
  public void setUp() throws IOException {
    metadata = AppMetadata.getInstance();
  }

  /**
   * Check application own metadata.
   */
  @Test
  public void testApplicationOwnArtifact() {
    System.out.println(metadata.getApplicationArtifact());

    MavenArtifact artifact = metadata.getApplicationArtifact();
    assertEquals("keybridge.lib", artifact.getGroupId());
    assertEquals("jar", artifact.getPackaging());
  }

  /**
   * Print out the dependencies in tree and list forms.
   * @throws IOException
   */
  @Test
  public void testDependencies() throws IOException {
    System.out.println("Dependency hierarchy:\n");
    DependencyWriter.printHierarchy(metadata.getDependencyHierarchy(), System.out);

    System.out.println("\nDependency list:\n");
    metadata.getDependencies().forEach(System.out::println);
  }

  /**
   * Print all dependencies and their associated licenses.
   */
  @Test
  public void printLicenses() {
    metadata.getLicenses().forEach(((artifact, licenses) -> {
      System.out.println(artifact + ":");
      licenses.forEach(l -> System.out.println("\t" + l));
      System.out.println();
    }));
  }

  /**
   * Grab the first license and print its content using the file automatically downloaded by Maven
   * during the build.
   *
   * @throws IOException
   */
  @Test
  public void printSomeLicenseContent() throws IOException {
    // grab the first license
    License license = metadata.getLicenses().values().stream()
        .flatMap(Collection::stream)
        .findAny()
        .orElseThrow(IllegalStateException::new);

    Path licensePath = metadata.resolveLicencePath(license);
    Files.readAllLines(licensePath).forEach(System.out::println);
  }
}