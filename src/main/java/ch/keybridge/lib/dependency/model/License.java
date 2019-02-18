package ch.keybridge.lib.dependency.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import lombok.Value;

/**
 * @author Andrius Druzinis-Vitkus
 * @since 0.0.1 created 2019-02-07
 */
@Value
@XmlAccessorType(XmlAccessType.FIELD)
public class License {
  /**
   * Licence name, e.g. New BSD License
   */
  private final String name;
  /**
   * Licence URL, e.g. http://www.opensource.org/licenses/bsd-license.php
   */
  private final String url;
  /**
   * Distribution mechanism, e.g. repo.
   */
  private final String distribution;
  /**
   * File name for the local copy of the license.
   */
  private final String file;

  public License() {
    this.name = null;
    this.url = null;
    this.distribution = null;
    this.file = null;
  }

  public License(String name, String url, String distribution, String file) {
    this.name = name;
    this.url = url;
    this.distribution = distribution;
    this.file = file;
  }
}
