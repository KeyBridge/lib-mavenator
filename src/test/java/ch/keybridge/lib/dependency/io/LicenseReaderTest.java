package ch.keybridge.lib.dependency.io;

import ch.keybridge.lib.dependency.util.ResourceUtility;
import java.nio.file.Path;
import org.junit.Test;

/**
 * @author Andrius Druzinis-Vitkus
 * @since 0.0.1 created 2019-02-07
 */
public class LicenseReaderTest {


  @Test
  public void parseLicences() throws Exception  {
    final Path licenseListFile = ResourceUtility.getResourcePath("exampledata/licenses.xml");
    System.out.println(LicenseReader.read(licenseListFile));
  }
}