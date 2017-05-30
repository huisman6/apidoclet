package org.apidoclet.server.controller.v1;

import org.apidoclet.server.helper.DigestUtils;
import org.apidoclet.server.service.VersionGroupedAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/apps")
public class RestAppController {

  @Autowired
  private VersionGroupedAppService versionGroupedAppService;


  /**
   * get app's ids
   */
  @RequestMapping(value = "/id")
  public Object appIds(@RequestParam(value = "app", required = false) String app) {
    if (StringUtils.hasText(app)) {
      return DigestUtils.crc32(app);
    }
    return this.versionGroupedAppService.findAppIds();
  }

  @RequestMapping(value = "/{id}/del")
  public void deleteById(@PathVariable(value = "id") String id) {
    this.versionGroupedAppService.deleteById(id);
  }

  @RequestMapping(value = "/{id}")
  public Object app(@PathVariable(value = "id") String id) {
    return this.versionGroupedAppService.findById(id);
  }

  @RequestMapping(value = "/name/{app}")
  public Object findByApp(@PathVariable(value = "app") String app) {
    return this.versionGroupedAppService.findById(String.valueOf(DigestUtils.crc32(app)));
  }

}
