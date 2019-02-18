package ch.keybridge.lib.dependency.util;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility methods for accessing files inside the JAR.
 * @author Key Bridge
 * @since 0.0.1 created 2019-02-07
 */
public class ResourceUtility {

  /**
   * Get a Path to a file inside the JAR.
   * @param path file path within the JAR.
   * @return resolved Path
   * @throws FileNotFoundException in case the file is not found.
   */
  public static Path getResourcePath(String path) throws FileNotFoundException {
    URL url = ResourceUtility.class.getClassLoader().getResource(path);
    if (url == null) throw new FileNotFoundException("Resource not found: " + path);
    try {
      return Paths.get(url.toURI());
    } catch (URISyntaxException e) {
      throw new FileNotFoundException("Error when accessing path " + url);
    }
  }
}
