package org.apidoclet.server.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apidoclet.model.RestServices;
import org.apidoclet.server.model.VersionedRestApp;
import org.apidoclet.server.model.VersionedRestApps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author huisman
 */
@Service
public class VersionGroupedAppService implements InitializingBean {
  private Logger logger = LoggerFactory.getLogger(this.getClass());
  /**
   * serialize file name
   */
  private static final String SERIALIZE_FILE_NAME = "allVersionedApps.json";
  /**
   * all rest services
   */
  private VersionedRestApps allVersionedApps = new VersionedRestApps();

  /**
   * serialize object based on fields
   */
  private ObjectMapper appSerializeObjectMapper;

  /**
   * import rest apps
   */
  public void addRestApps(RestServices restApps) {
    this.allVersionedApps.addRestApps(restApps);
    storeApps(this.allVersionedApps);
  }

  /**
   * get all rest app
   */
  public List<VersionedRestApp> findAll() {
    return this.allVersionedApps.getAllApps();
  }

  /**
   * {@code VersionedRestApp#getId()}
   */
  public List<String> findAppIds() {
    return this.allVersionedApps.getAppIds();
  }

  /**
   * find app by its id({@code VersionedRestApp#getId()}
   */
  public VersionedRestApp findById(String id) {
    VersionedRestApp app = this.allVersionedApps.findById(id);
    return app;
  }

  /**
   * delete imported app by its id({@code VersionedRestApp#getId()}
   */
  public void deleteById(String id) {
    this.allVersionedApps.removeByAppId(id);
    storeApps(this.allVersionedApps);
  }



  @Override
  public void afterPropertiesSet() throws Exception {
     this.appSerializeObjectMapper=new ObjectMapper();
     this.appSerializeObjectMapper
     .setVisibility(appSerializeObjectMapper.getSerializationConfig().getDefaultVisibilityChecker()
         // only use fields
         .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
         .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
         .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
         .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
     this.appSerializeObjectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
     //ignore unknown fields
     this.appSerializeObjectMapper
     .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
     
     loadAllApps();
  }

  private synchronized void storeApps(VersionedRestApps apps) {
    logger.info("start serializing apps to disk path：{},appSize={}",
        SERIALIZE_FILE_NAME, apps.getAllApps().size());
    try (OutputStream output =
        new BufferedOutputStream(new FileOutputStream(SERIALIZE_FILE_NAME))) {
      this.appSerializeObjectMapper.writeValue(output, apps);
      logger.info("succeed in serializing apps to disk");
    } catch (IOException e) {
      logger.error("error occured when serialize apps to disk", e);
    }
  }

  private synchronized void loadAllApps() {
    logger.info("deserialize apps from path：" + SERIALIZE_FILE_NAME);
    try {
      VersionedRestApps apps =
          this.appSerializeObjectMapper.readValue(new File(SERIALIZE_FILE_NAME),
              VersionedRestApps.class);
      if (apps != null) {
        this.allVersionedApps = apps;
        logger.info("succeed in  deserializing apps from path：{},appSize=",
            SERIALIZE_FILE_NAME, apps.getAllApps().size());
      } else {
        logger.warn("deserialize apps but result is null, ignored");
      }
    } catch (Exception e) {
      logger.error("fail to deserialize apps, ignored ", e);
    }
  }
}
