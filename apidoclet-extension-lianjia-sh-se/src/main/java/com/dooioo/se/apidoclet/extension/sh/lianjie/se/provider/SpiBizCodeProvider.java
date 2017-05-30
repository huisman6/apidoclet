package com.dooioo.se.apidoclet.extension.sh.lianjie.se.provider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dooioo.se.apidoclet.core.ApiDocletOptions;
import com.dooioo.se.apidoclet.core.spi.provider.BizCodeProvider;
import com.dooioo.se.apidoclet.core.util.ClassUtils;
import com.dooioo.se.apidoclet.model.BizCode;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;

/**
 * 默认SPI业务码的实现
 */
public class SpiBizCodeProvider implements BizCodeProvider {

  @Override
  public List<BizCode> provided(ApiDocletOptions options) {
    return null;
  }

  @Override
  public List<BizCode> produce(ClassDoc classDoc, ApiDocletOptions options) {
    if (classDoc == null || classDoc.isException()
        || classDoc.isAnnotationType() || classDoc.isPrimitive()
        || classDoc.isPrivate()) {
      return null;
    }

    // 我们过滤一个类里的所有静态的，并且类型为BizCode的
    List<BizCode> bizcodeFields = new ArrayList<>();
    // 查找所有字段
    bizcodeFields.addAll(filterBizCodeFields(classDoc, options));
    // 所有接口
    ClassDoc[] interfaces = classDoc.interfaces();
    for (ClassDoc ci : interfaces) {
      bizcodeFields.addAll(filterBizCodeFields(ci, options));
    }

    // 所有父类
    ClassDoc superClass = classDoc.superclass();
    while (superClass != null
        && !superClass.qualifiedTypeName().equals(Object.class.getName())) {
      bizcodeFields.addAll(filterBizCodeFields(superClass, options));
      superClass = superClass.superclass();
    }
    return bizcodeFields;

  }

  @Override
  public List<BizCode> produce(ClassDoc classDoc, MethodDoc methodDoc,
      ApiDocletOptions apiDocletOptions) {
    // 我们的方法上都是Lorik Rest codes，无法解析业务码，忽略
    return null;
  }

  /**
   * 业务码必须为static /public 字段
   * 
   * @author huisman
   * @param classDoc
   * @since 2016年1月16日
   */
  private List<BizCode> filterBizCodeFields(ClassDoc classDoc,
      ApiDocletOptions options) {
    List<BizCode> bizCodeFields = new ArrayList<BizCode>();
    Map<String, com.dooioo.se.lorik.spi.view.support.BizCode> bizCodeMap =
        new HashMap<>();
    try {
      // 使用自定义的类加载器加载
      Class<?> clazz =
          Class.forName(classDoc.qualifiedName(), true,
              options.getApidocletClassLoader());
      for (Field field : clazz.getDeclaredFields()) {
        if (!Modifier.isStatic(field.getModifiers())
            && !Modifier.isPublic(field.getModifiers())) {
          continue;
        }
        if (!field
            .getType()
            .getName()
            .equals(
                com.dooioo.se.lorik.spi.view.support.BizCode.class.getName())) {
          continue;
        }
        // 解析业务码，因为是静态、public常量，可直接获取
        Object codeObj = field.get(null);
        if (codeObj == null) {
          continue;
        }
        // classloader不一样，不能直接赋值
        com.dooioo.se.lorik.spi.view.support.BizCode docBizCode =
            new com.dooioo.se.lorik.spi.view.support.BizCode(
                (Integer) ClassUtils.getFieldValue(codeObj, "code"),
                (String) ClassUtils.getFieldValue(codeObj, "message"));
        bizCodeMap.put(field.getName(), docBizCode);
      }

    } catch (ClassNotFoundException | IllegalArgumentException
        | IllegalAccessException e) {
      options.getDocReporter().printWarning(
          "error occured when resolve bizcode,class:"
              + classDoc.qualifiedTypeName() + ",message:" + e.getMessage());
      return bizCodeFields;
    }

    if (bizCodeMap.isEmpty()) {
      return bizCodeFields;
    }

    FieldDoc[] fields = classDoc.fields();
    for (FieldDoc fieldDoc : fields) {
      if (!bizCodeMap.containsKey(fieldDoc.name())) {
        continue;
      }
      // 我们文档中解析后的业务码
      BizCode code = new BizCode();
      code.setComment(fieldDoc.commentText());
      code.setDeclaredClass(fieldDoc.containingClass().qualifiedName());
      code.setName(fieldDoc.name());

      // 获取类里的业务码信息
      com.dooioo.se.lorik.spi.view.support.BizCode bizCode =
          bizCodeMap.get(fieldDoc.name());
      if (bizCode != null) {
        code.setCode(String.valueOf(bizCode.getCode()));
        code.setMessage(bizCode.getMessage());
      }
      bizCodeFields.add(code);
    }
    return bizCodeFields;
  }
}
