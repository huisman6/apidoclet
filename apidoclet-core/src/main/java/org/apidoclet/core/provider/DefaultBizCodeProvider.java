package org.apidoclet.core.provider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apidoclet.core.ApiDocletOptions;
import org.apidoclet.core.spi.provider.BizCodeProvider;
import org.apidoclet.core.util.ClassUtils;
import org.apidoclet.core.util.StringUtils;
import org.apidoclet.model.BizCode;
import org.apidoclet.model.util.StandardDocTag;
import org.apidoclet.model.util.Types;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;

/**
 * resolve the {@link BizCode} from {@code @BizCodes} javadoc tag
 */
public class DefaultBizCodeProvider implements BizCodeProvider {
  private static final String DEFAULT_METHOD_DELIMITER = "#";

  @Override
  public List<BizCode> provided(ApiDocletOptions options) {
    return null;
  }

  @Override
  public List<BizCode> produce(ClassDoc classDoc, ApiDocletOptions options) {
    return null;
  }

  @Override
  public List<BizCode> produce(ClassDoc classDoc, MethodDoc methodDoc,
      ApiDocletOptions apiDocletOptions) {
    Tag[] tags = methodDoc.tags(StandardDocTag.TAG_BIZ_CODES);
    if (tags != null && tags.length > 0) {
      Tag tag = tags[0];
      // @bizCodes {@code TestBizCodes#INVALID_PARAM} {@link TestBizCodes#INVALID_PARAM}
      Tag[] inlineTags = tag.inlineTags();
      List<BizCode> bizCodes = new ArrayList<BizCode>(8);
      for (Tag innerTag : inlineTags) {
        String text = innerTag.text();
        if (StringUtils.isNullOrEmpty(text)) {
          continue;
        }
        String[] segments = text.split(DEFAULT_METHOD_DELIMITER);
        if (segments.length != 2) {
          continue;
        }
        if (StringUtils.isNullOrEmpty(segments[1])) {
          continue;
        }
        // not found (imported or full qualified type)
        ClassDoc bizClass = classDoc.findClass(segments[0]);
        if (bizClass == null) {
          continue;
        }
        try {
          // loading class
          Class<?> clazz =
              Class.forName(bizClass.qualifiedName(), true,
                  apiDocletOptions.getApidocletClassLoader());
          Field bizField = clazz.getDeclaredField(segments[1]);
          if (bizField == null) {
            continue;
          }
          if (!Modifier.isStatic(bizField.getModifiers())
              && !Modifier.isPublic(bizField.getModifiers())) {
            continue;
          }
          // static and public field
          Object actualField = bizField.get(null);
          if (actualField == null) {
            continue;
          }
          BizCode bizCode = new BizCode();
          if (Types.isSimpleType(bizField.getType().getName())) {
            // primitive type , string etc，just set code and ignore the message field
            bizCode.setCode(String.valueOf(actualField));
          } else {
            Object code = ClassUtils.getFieldValue(actualField, "code");
            bizCode.setCode(code == null ? null : code.toString());
            Object message = ClassUtils.getFieldValue(actualField, "message");
            bizCode.setMessage(message == null ? null : message.toString());
          }
          bizCode.setDeclaredClass(clazz.getName());
          bizCode.setName(segments[1]);

          // public fields 
          for (FieldDoc fieldDoc : bizClass.fields()) {
            // field name
            if (segments[1].equals(fieldDoc.name())) {
              bizCode.setComment(StringUtils.trim(fieldDoc.commentText()));
              break;
            }
          }
          bizCodes.add(bizCode);
        } catch (Exception e) {
          // ignore?
          apiDocletOptions.getDocReporter().printWarning(
              "error occured when resolve bizcode,class:"
                  + classDoc.qualifiedTypeName() + ",method:"
                  + methodDoc.name() + ",message:" + e.getMessage());
        }
      }
      return bizCodes;
    }
    return null;
  }
}
