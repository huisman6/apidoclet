package com.dooioo.se.apidoclet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 所有javadoc Tags，收集类或者方法上的javadoc tag;
 */
public class JavaDocTags implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * 类或者方法上的所有tags
   */
  private Map<String, List<JavaDocTag>> allTags = new HashMap<>();

  public JavaDocTags() {
    super();
  }

  /**
   * 获取某种类型的javadoc tag
   * 
   * @author huisman
   */
  public List<JavaDocTag> tags(String kind) {
    return allTags.get(kind);
  }

  /**
   * 新增javadoc tag
   * 
   * @author huisman
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



  @Override
  public String toString() {
    return "JavaDocTags [allTags=" + allTags + "]";
  }



  public static class JavaDocTag implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 注释tag的名称, 比如：@param userCode 工号，name为userCode.
     */
    private String name;

    /**
     * Tag的类别，比如：@param userCode 工号，kind为param.
     */
    private String kind;
    /**
     * tag的文本说明，，比如：@param userCode 工号，text为工号
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
      return "JavaDocTag [name=" + name + ", kind=" + kind + ", text=" + text + "]";
    }

  }
}
