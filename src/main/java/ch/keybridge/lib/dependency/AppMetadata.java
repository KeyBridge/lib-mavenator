package ch.keybridge.lib.dependency;

import ch.keybridge.lib.dependency.io.DependencyReader;
import ch.keybridge.lib.dependency.io.LicenseReader;
import ch.keybridge.lib.dependency.io.LicenseSummary;
import ch.keybridge.lib.dependency.model.License;
import ch.keybridge.lib.dependency.model.MavenArtifact;
import ch.keybridge.lib.dependency.model.MavenDependency;
import ch.keybridge.lib.dependency.util.ResourceUtility;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;

/**
 * The main entry point to the library.
 *
 * Has default paths of data files pre-specified.
 *
 * @author Andrius Druzinis-Vitkus
 * @since 0.0.1 created 2019-02-05
 */
public class AppMetadata {
  public static final String DEPENDENCY_FILE = "META-INF/build/dependency-tree.txt";
  public static final String LICENSE_FILE = "META-INF/build/licenses.xml";
  public static final String LICENSES_DIR = "META-INF/build/licenses/";

  private MavenDependency dependencies;
  private Map<MavenArtifact, List<License>> licenses;

  /**
   * Get an instance with pre-loaded dependency and license lists.
   * @return a new AppMetadata instance
   * @throws IOException
   */
  public static AppMetadata getInstance() throws IOException {
    AppMetadata appMetadata = new AppMetadata();

    appMetadata.loadDependencyData();
    appMetadata.loadLicenseData();

    return appMetadata;
  }

  /**
   * Get the Maven artifact for the application itself. Can be used to get the application version.
   * @return MavenArtifact for the application itself.
   */
  public MavenArtifact getApplicationArtifact() {
    return dependencies.getArtifact();
  }

  /**
   * Get the dependency tree.
   * @return dependency tree.
   */
  public MavenDependency getDependencyHierarchy() {
    return dependencies;
  }

  /**
   * Get all dependencies (including transitive ones) in a list.
   * @return collection of all dependencies.
   */
  public Collection<MavenDependency> getDependencies() {
    return dependencies.getFlattenedDependencies();
  }

  public Map<MavenArtifact, List<License>> getLicenses() {
    return licenses;
  }

  /**
   * Resolve the path to the content of a License
   * @param license a non-null license instance
   * @return a valid path, if specified
   * @throws IOException in case of failure to create path.
   */
  public Path resolveLicencePath(License license) throws IOException {
    return ResourceUtility.getResourcePath(LICENSES_DIR + license.getFile());
  }

  /**
   * Load the dependency hierarchy file.
   * @throws IOException in case of failure to find or read the file
   */
  private void loadDependencyData() throws IOException {
    Path dependencyPath = ResourceUtility.getResourcePath(DEPENDENCY_FILE);
    dependencies = DependencyReader.parseDependencyHierarchy(dependencyPath);
  }

  /**
   * Load the license file.
   * @throws IOException in case of failure to find or read the file
   */
  private void loadLicenseData() throws IOException {
    Path licensePath = ResourceUtility.getResourcePath(LICENSE_FILE);

    try {
      /**
       * Read license information into a map of
       * groupId:artifactId:version to List-of-licenses.
       */
      LicenseSummary summary = LicenseReader.read(licensePath);
      Map<String, List<License>> licenseMap = summary.getDependencies().stream()
          .collect(Collectors.toMap(LicenseSummary.Dependency::getVersionedArtifactName,
              LicenseSummary.Dependency::getLicenses));

      /**
       * Map licenses to our actual artifacts.
       */
      licenses = new LinkedHashMap<>();
      for (MavenDependency dependency : dependencies.getFlattenedDependencies()) {
        final String versionedArtifact = dependency.getArtifact().getVersionedArtifactName();
        List<License> licenseList = licenseMap.getOrDefault(versionedArtifact, Collections.emptyList());
        licenses.put(dependency.getArtifact(), Collections.unmodifiableList(licenseList));
      }
      licenses = Collections.unmodifiableMap(licenses);
    } catch (JAXBException e) {
      throw new IOException("Unable to parse XML of the license file");
    }
  }
}
