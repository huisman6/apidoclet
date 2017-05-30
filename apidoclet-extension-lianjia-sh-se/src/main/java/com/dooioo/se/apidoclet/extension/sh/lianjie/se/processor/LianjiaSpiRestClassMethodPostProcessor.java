package com.dooioo.se.apidoclet.extension.sh.lianjie.se.processor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apidoclet.core.spi.processor.ProcessorContext;
import org.apidoclet.core.spi.processor.post.RestClassMethodPostProcessor;
import org.apidoclet.model.BizCode;
import org.apidoclet.model.JavaAnnotations;
import org.apidoclet.model.RestClass;
import org.apidoclet.model.JavaAnnotations.AnnotationValue;
import org.apidoclet.model.JavaAnnotations.JavaAnnotation;
import org.apidoclet.model.RestClass.Method;

import com.dooioo.se.lorik.spi.view.support.LorikRest;

/**
 * 后续处理，方法上业务码的转换
 */
public class LianjiaSpiRestClassMethodPostProcessor implements
    RestClassMethodPostProcessor {

  @Override
  public void postProcess(Method method, RestClass restClass,
      ProcessorContext context) {
    Set<BizCode> bizCodes = method.getBizCodes();
    if (bizCodes == null) {
      bizCodes = new HashSet<BizCode>();
    }
    JavaAnnotations methodAnnotations = method.getMethodAnnotations();
    if (methodAnnotations != null) {
      List<JavaAnnotation> annotations =
          methodAnnotations.tags(LorikRest.class.getName());
      if (annotations != null && !annotations.isEmpty()) {
        for (JavaAnnotation javaAnnotation : annotations) {
          AnnotationValue values = javaAnnotation.attribute("codes");
          if (values != null) {
            @SuppressWarnings("unchecked")
            List<Integer> codes = (List<Integer>) values.getValue();
            if (codes != null && codes.size() > 0) {
              for (Integer code : codes) {
                BizCode bc = context.getBizCode(String.valueOf(code));
                if (bc == null) {
                  continue;
                }
                bizCodes.add(bc);
              }
            }
          }
        }
      }
    }
    method.setBizCodes(bizCodes);
  }

}
