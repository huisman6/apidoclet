package org.apidoclet.server.controller.v1;

import java.util.HashMap;
import java.util.Map;

import org.apidoclet.model.RestServices;
import org.apidoclet.server.service.VersionGroupedAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huisman
 */
@RestController
public class RestAppsImportController {

  @Autowired
  private VersionGroupedAppService versionGroupedAppService;

  /**
   * rest app import
   */
  @RequestMapping(value = "/v1/apps/import", method = RequestMethod.POST)
  public Map<String,Object> importRestApps(@RequestBody RestServices restServices) {
    if (restServices != null) {
      this.versionGroupedAppService.addRestApps(restServices);
    }
    Map<String, Object> responseEntity = new HashMap<>();
    responseEntity.put("status", "ok");
    responseEntity.put("code", 0);
    return responseEntity;
  }

}
