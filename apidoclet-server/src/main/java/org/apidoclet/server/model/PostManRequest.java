package org.apidoclet.server.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Jerry.hu
 * @Create: Jerry.hu (2016-01-29 10:03)
 */
public class PostManRequest implements Serializable {
    private static final long serialVersionUID = 2712929718872442080L;
    private String folder;
    private String id;
    private String name;
    private String dataMode;
    private List<PostData> data;
    private String rawModeData;
    private String descriptionFormat;
    private String description;
    private String headers;
    private String method;
    private Object pathVariables;
    private String url;
    private String preRequestScript;
    private String tests;
    private String currentHelper = "normal";
    private String helperAttributes;
    private String collectionId;
    private String[] responses= new String[]{};
    private String time;
    private String version;


    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataMode() {
        return dataMode;
    }

    public void setDataMode(String dataMode) {
        this.dataMode = dataMode;
    }

    public List<PostData> getData() {
        return data;
    }

    public void setData(List<PostData> data) {
        this.data = data;
    }

    public String getRawModeData() {
        return rawModeData;
    }

    public void setRawModeData(String rawModeData) {
        this.rawModeData = rawModeData;
    }

    public String getDescriptionFormat() {
        return descriptionFormat;
    }

    public void setDescriptionFormat(String descriptionFormat) {
        this.descriptionFormat = descriptionFormat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(Object pathVariables) {
        this.pathVariables = pathVariables;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPreRequestScript() {
        return preRequestScript;
    }

    public void setPreRequestScript(String preRequestScript) {
        this.preRequestScript = preRequestScript;
    }

    public String getTests() {
        return tests;
    }

    public void setTests(String tests) {
        this.tests = tests;
    }

    public String getCurrentHelper() {
        return currentHelper;
    }

    public void setCurrentHelper(String currentHelper) {
        this.currentHelper = currentHelper;
    }

    public String getHelperAttributes() {
        return helperAttributes;
    }

    public void setHelperAttributes(String helperAttributes) {
        this.helperAttributes = helperAttributes;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String[] getResponses() {
        return responses;
    }

    public void setResponses(String[] responses) {
        this.responses = responses;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public static class PostData {
        private String key;
        private String value;
        private String type ="text";
        private boolean enabled=true;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

}
