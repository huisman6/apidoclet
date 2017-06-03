package org.apidoclet.core.exporter;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.spi.exporter.RestServicesExporter;
import org.apidoclet.model.RestServices;

import com.alibaba.fastjson.JSON;

/**
 * export the parsed {@link RestServices} to an external server
 * 
 * by default, we always attempt to export the parsed {@code RestServices} to
 * URL-"http://localhost:8089/v1/apps/import"(local api-doclet server is running with port 8089 )
 * for test, you can override the default export url by passing option :
 * 
 * -exportTo http://you-apidoclet-server-domain/v1/apps/import
 */
public class DefaultRestServicesExporter implements RestServicesExporter {
  private static final String[] defaultExportUrls =
      new String[] {"http://localhost:8089/v1/apps/import"};

  @Override
  public void exportTo(RestServices restServices, ApiDocletOptions options) {
    // explicit provide export url?
    String providedUrl = options.getExportTo();
    if (org.apidoclet.core.util.StringUtils.isNullOrEmpty(providedUrl)) {
      for (String url : defaultExportUrls) {
        if (export(url, restServices, options)) {
          return;
        }
      }
    } else {
      export(providedUrl, restServices, options);
    }
  }



  private boolean export(String providedUrl, RestServices restApps,
      ApiDocletOptions options) {
    HttpURLConnection connection = null;
    options.getDocReporter().printNotice(
        "start exporting api doc to:" + providedUrl);
    try {
      URL url = new URL(providedUrl);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setReadTimeout(30 * 1000);
      connection.setConnectTimeout(10 * 1000);
      connection.setDoOutput(true);
      connection.setUseCaches(false);
      // json serialization
      connection.setRequestProperty("Content-Type",
          "application/json;charset=utf-8");
      connection.connect();

      try (BufferedOutputStream out =
          new BufferedOutputStream(connection.getOutputStream());) {
        // default encoding - utf-8
        out.write(JSON.toJSONBytes(restApps));
        out.flush();
        out.close();

        // read after write.
        try (BufferedReader reader =
            new BufferedReader(new InputStreamReader(
                connection.getInputStream(), StandardCharsets.UTF_8));) {
          StringBuilder message = new StringBuilder(80);
          String decodedString = null;
          while ((decodedString = reader.readLine()) != null) {
            message.append(decodedString).append(" ");
          }
          options.getDocReporter().printNotice(
              "api doc has exported，received response:" + message);
        }
      }
      return true;
    } catch (Exception e) {
      options.getDocReporter().printNotice(
          "ignored ====>> export to url:" + providedUrl + ",message:"
              + e.getMessage());
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return false;
  }
}
