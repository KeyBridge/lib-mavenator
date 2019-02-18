package ch.keybridge.lib.dependency.io;

import ch.keybridge.lib.dependency.model.License;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;
import lombok.Value;

/**
 * A DTO for reading Maven license:download-licenses output XML file. This class should only
 * be used for extracting data from this file and nothing else.
 *
 * Developer note: the interesting Dependency constructor sets all fields to null creating
 * a seemingly useless object. However, this constructor is created to appease JAXB,
 * since JAXB requires a no-arguments constructor. This approach works since Java 5. See the following:
 *
 * <blockquote>
 * Eric WestfallJanuary 26, 2011 at 3:39 AM
 * <br>
 * Thanks for this post, it was very informative.
 * <br>
 * As part of the research we've been doing on our project, we discovered that since Java 1.5 (and thanks to JSR-133)
 * it's possible to construct objects using reflection that have only a private constructor as well as only final
 * fields, and JAXB can take advantage of this.
 * </blockquote>
 *
 * from <a href="http://blog.bdoughan.com/2010/12/jaxb-and-immutable-objects.html">this blog post</a>.
 *
 * @author Andrius Druzinis-Vitkus
 * @since 0.0.1 created 2019-02-07
 */
@XmlRootElement(name = "licenseSummary")
@XmlAccessorType(XmlAccessType.FIELD)
@Value
public class LicenseSummary {

  @XmlElementWrapper(name="dependencies")
  @XmlElement(name="dependency")
  private final List<Dependency> dependencies = new ArrayList<>();

  @XmlAccessorType(XmlAccessType.FIELD)
  @Value
  public static class Dependency {
    private final String groupId;
    private final String artifactId;
    private final String version;

    @XmlElementWrapper(name="licenses")
    @XmlElement(name="license")
    private final List<License> licenses;

    public Dependency() {
      this.groupId = null;
      this.artifactId = null;
      this.version = null;
      this.licenses = null;
    }

    /**
     * Get a short string identifier of this artifact.
     * @return versioned artifact name
     */
    public String getVersionedArtifactName() {
      return groupId + ':' + artifactId + ':' + version;
    }
  }
}
