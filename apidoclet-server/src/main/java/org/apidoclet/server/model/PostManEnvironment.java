package org.apidoclet.server.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: Jerry.hu
 * @Create: Jerry.hu (2016-01-29 10:03)
 */
public class PostManEnvironment implements Serializable{
    private static final long serialVersionUID = -3068503916547342431L;
    private String id;
    private String name;
    private List<Environment> values;
    private long timestamp = new Date().getTime();
    private boolean synced =false;
    private String syncedFilename ="";

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

    public List<Environment> getValues() {
        return values;
    }

    public void setValues(List<Environment> values) {
        this.values = values;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public String getSyncedFilename() {
        return syncedFilename;
    }

    public void setSyncedFilename(String syncedFilename) {
        this.syncedFilename = syncedFilename;
    }

    public static class Environment {
        private String key;
        private String value;
        private String type ="text";
        private String name;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
