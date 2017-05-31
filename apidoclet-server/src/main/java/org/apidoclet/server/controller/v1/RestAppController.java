package org.apidoclet.server.controller.v1;

import javax.servlet.http.HttpServletRequest;

import org.apidoclet.server.helper.DigestUtils;
import org.apidoclet.server.service.VersionGroupedAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/apps")
public class RestAppController {
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private VersionGroupedAppService versionGroupedAppService;

  @RequestMapping(value = "/id")
  public Object appIds(HttpServletRequest request, @RequestParam(value = "app", required = false) String app) {
    if (StringUtils.hasText(app)) {
      return DigestUtils.crc32(app);
    }
    String remoteIp=request.getHeader("X-Real-Ip");
    if (!StringUtils.hasText(remoteIp)) {
      remoteIp=request.getRemoteAddr();
    }
    logger.info(" find all app's id,clientIp：" + remoteIp);
    return this.versionGroupedAppService.findAppIds();
  }

  @RequestMapping(value = "/{id}/del")
  public void deleteById(HttpServletRequest request, @PathVariable(value = "id") String id) {
    String remoteIp=request.getHeader("X-Real-Ip");
    if (!StringUtils.hasText(remoteIp)) {
      remoteIp=request.getRemoteAddr();
    }
    logger.info("delete app by id,id={}，clientIp：{}", id, remoteIp);
    this.versionGroupedAppService.deleteById(id);
  }

  @RequestMapping(value = "/{id}")
  public Object app(HttpServletRequest request, @PathVariable(value = "id") String id) {
    String remoteIp=request.getHeader("X-Real-Ip");
    if (!StringUtils.hasText(remoteIp)) {
      remoteIp=request.getRemoteAddr();
    }
    logger.info("get app by id,id={}，clientIp：{}", id,remoteIp);
    return this.versionGroupedAppService.findById(id);
  }

  @RequestMapping(value = "/name/{app}")
  public Object findByApp(HttpServletRequest request, @PathVariable(value = "app") String app) {
    String remoteIp=request.getHeader("X-Real-Ip");
    if (!StringUtils.hasText(remoteIp)) {
      remoteIp=request.getRemoteAddr();
    }
    logger.info("get app by name,clientIp:" + remoteIp+",app="+app);
    return this.versionGroupedAppService.findById(String.valueOf(DigestUtils.crc32(app)));
  }

}
