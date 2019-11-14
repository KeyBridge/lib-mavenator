package ch.keybridge.lib.dependency.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Read output from the Maven license:download-licenses goal.
 *
 * @author Andrius Druzinis-Vitkus
 * @since 0.0.1 created 2019-02-07
 */
public class LicenseReader {

  /**
   * Read output from the Maven license:download-licenses goal.
   *
   * @param file path to the licenses XML file.
   * @return parsed file content
   * @throws IOException   on read error
   * @throws JAXBException on parse error
   */
  public static LicenseSummary read(Path file) throws IOException, JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(LicenseSummary.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

    try (BufferedReader r = Files.newBufferedReader(file)) {
      return (LicenseSummary) jaxbUnmarshaller.unmarshal(r);
    }
  }
}
