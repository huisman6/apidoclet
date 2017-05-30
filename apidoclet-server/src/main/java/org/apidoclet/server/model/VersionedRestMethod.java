package org.apidoclet.server.model;

import java.io.Serializable;

import org.apidoclet.model.RestClass;
import org.apidoclet.server.helper.DigestUtils;
import org.springframework.util.StringUtils;

/**
 *
 * @author huisman
 */
public class VersionedRestMethod implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * original rest method
   */
  private RestClass.Method original;
  /**
   * method identity
   */
  private String id;
  /**
   * method namespace, usually set to {@code RestService#getApp()}
   */
  private String namespace;

  /**
   * method version
   */
  private String version;

  public VersionedRestMethod() {
    super();
  }

  public VersionedRestMethod(String namespace, RestClass.Method original) {
    super();
    if (original == null) {
      throw new IllegalArgumentException("spi method is null");
    }
    if (StringUtils.isEmpty(namespace)) {
      throw new IllegalArgumentException("method namespace is null");
    }
    this.original = original;
    this.namespace = namespace;
    // use crc32 to generate numberic id
    this.id =
        String.valueOf(DigestUtils.crc32(namespace
            + RestClass.Method.IDENTITY_SPILIT_CHAR + original.getIdentity()));
    this.version = this.original.getVersion();
  }

  public RestClass.Method getOriginal() {
    return original;
  }

  public void setOriginal(RestClass.Method original) {
    this.original = original;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }


  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return "VersionedRestMethod [original=" + original + ", id=" + id
        + ", namespace=" + namespace + ", version=" + version + "]";
  }

}
