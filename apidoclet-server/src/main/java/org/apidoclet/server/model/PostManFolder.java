package org.apidoclet.server.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Jerry.hu
 * @Create: Jerry.hu (2016-01-29 10:03)
 */
public class PostManFolder implements Serializable{
    private static final long serialVersionUID = 7189256738296301544L;
    private String id;
    private String name;
    private String description;
    private List<String> order;
    private String owner;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getOrder() {
        return order;
    }

    public void setOrder(List<String> order) {
        this.order = order;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
