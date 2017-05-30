package org.apidoclet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * all java doc tags on class or method 
 */
public class JavaDocTags implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * all java doc tags on class or method;
   */
  private Map<String, List<JavaDocTag>> allTags = new HashMap<>();

  public JavaDocTags() {
    super();
  }

  /**
   * get doc tags by tag kind
   */
  public List<JavaDocTag> tags(String kind) {
    return allTags.get(kind);
  }

  /**
   * add new doc tag
   */
  public void add(JavaDocTag tag) {
    if (tag == null || tag.getKind() == null) {
      return;
    }
    List<JavaDocTag> tags = this.allTags.get(tag.getKind());
    if (tags == null) {
      tags = new ArrayList<JavaDocTags.JavaDocTag>();
    }
    tags.add(tag);
    this.allTags.put(tag.getKind(), tags);
  }



  /* json-serialize */public Map<String, List<JavaDocTag>> getAllTags() {
    return allTags;
  }

  /* json-deserialize */public void setAllTags(
      Map<String, List<JavaDocTag>> allTags) {
    if (allTags == null) {
      this.allTags = new HashMap<>();
    } else {
      this.allTags = allTags;
    }
  }

  @Override
  public String toString() {
    return "JavaDocTags [allTags=" + allTags + "]";
  }



  public static class JavaDocTag implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * tag name </p> 
     * 
     * example ：@param balabala ==> name= @param
     */
    private String name;

    /**
     * tag category </p>
     * example ：@return balabala ==> kind = @param
     */
    private String kind;
    /**
     * tag description (usually,the remaining part except the kind ) </p> 
     * 
     * example ：@return  balabala ====> text= balabala
     */
    private String text;

    /**
     * @param name
     * @param kind
     * @param text
     */
    public JavaDocTag(String kind, String name, String text) {
      super();
      this.name = name;
      this.kind = kind;
      this.text = text;
    }

    public JavaDocTag() {
      super();
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getKind() {
      return kind;
    }

    public void setKind(String kind) {
      this.kind = kind;
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return "JavaDocTag [name=" + name + ", kind=" + kind + ", text=" + text
          + "]";
    }

  }
}
