package org.apidoclet.server.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Jerry.hu
 * @Create: Jerry.hu (2016-01-29 10:03)
 */
public class PostManMain implements Serializable{
    private static final long serialVersionUID = 3746180928761470544L;
    private String id;
    private String name;
    private String description;
    private String[] order;
    private List<PostManFolder> folders;
    private long timestam;
    private String owner;
    private String remoteLink="https://www.getpostman.com/collections/38aea42f6cdab85d90cd";
    private List<PostManRequest> requests;

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

    public String[] getOrder() {
        return order;
    }

    public void setOrder(String[] order) {
        this.order = order;
    }

    public List<PostManFolder> getFolders() {
        return folders;
    }

    public void setFolders(List<PostManFolder> folders) {
        this.folders = folders;
    }

    public long getTimestam() {
        return timestam;
    }

    public void setTimestam(long timestam) {
        this.timestam = timestam;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRemoteLink() {
        return remoteLink;
    }

    public void setRemoteLink(String remoteLink) {
        this.remoteLink = remoteLink;
    }

    public List<PostManRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<PostManRequest> requests) {
        this.requests = requests;
    }
}
