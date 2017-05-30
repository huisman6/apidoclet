package org.apidoclet.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apidoclet.model.BizCode;
import org.apidoclet.model.EnumInfo;
import org.apidoclet.model.ModelInfo;
import org.apidoclet.model.RestClass;
import org.apidoclet.model.RestService;
import org.apidoclet.model.RestServices;
import org.apidoclet.server.helper.DigestUtils;

/**
 * @author huisman
 */
public class VersionedRestApp implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
   * {@code VersionGroupedRestClass#getId()} ==> VersionGroupedRestClass
   */
  private Map<String, VersionGroupedRestClass> idToRestClassMap =
      new HashMap<>();
  /**
   * java bean full qualified name => {@link ModelInfo}</p> all parsed java bean models
   */
  private Map<String, ModelInfo> modelMap = new HashMap<>();
  /**
   * java enum class full qualified name => {@link ModelInfo} </p> all parsed java enums
   */
  private Map<String, EnumInfo> enumMap = new HashMap<>();

  /**
   * java bean full qualified name => {@link ModelInfo} </p> provided by
   * {@link RestServices#getModelInfos()}
   */
  private Map<String, ModelInfo> providedModelMap = new HashMap<>();

  /**
   * java enum class full qualified name => {@link ModelInfo} </p> provided by
   * {@link RestServices#getEnumInfos()}
   */
  private Map<String, EnumInfo> providedEnumMap = new HashMap<>();

  /**
   * provided by {@code RestServices#getBizCodes()}
   */
  private List<BizCode> providedBizCodes = new ArrayList<>();

  /**
   * use crc32 algorithm map {@code RestService#getApp()} to numberic id
   */
  private String id;

  /**
   * original rest app
   */
  private RestService original;

  public VersionedRestApp() {
    super();
  }



  public VersionedRestApp(RestService original, List<ModelInfo> providedModels,
      List<BizCode> providedBizCodes, List<EnumInfo> providedEnums) {
    super();
    if (original == null) {
      throw new IllegalArgumentException("rest app is null");
    }
    this.original = original;

    List<RestClass> restClasses = original.getRestClasses();
    if (restClasses != null && restClasses.size() > 0) {
      for (RestClass restClass : restClasses) {
        VersionGroupedRestClass versionGroupedRestClass =
            new VersionGroupedRestClass(restClass, original.getApp());
        this.idToRestClassMap.put(versionGroupedRestClass.getId(),
            versionGroupedRestClass);
      }
    }
    this.id = String.valueOf(DigestUtils.crc32(this.original.getApp()));
    List<ModelInfo> models = original.getModelInfos();
    if (models != null && !models.isEmpty()) {
      for (ModelInfo model : models) {
        this.modelMap.put(model.getClassName(), model);
      }
    }

    List<EnumInfo> enums = original.getEnumInfos();
    if (enums != null && !enums.isEmpty()) {
      for (EnumInfo senum : enums) {
        this.enumMap.put(senum.getClassName(), senum);
      }
    }

    // restservices provided
    if (providedEnums != null && providedEnums.size() > 0) {
      for (EnumInfo providedEnum : providedEnums) {
        this.providedEnumMap.put(providedEnum.getClassName(), providedEnum);
      }
    }
    // restservices provided
    if (providedBizCodes != null && !providedBizCodes.isEmpty()) {
      this.providedBizCodes.addAll(providedBizCodes);
    }

    // restservices provided
    if (providedModels != null && !providedModels.isEmpty()) {
      for (ModelInfo model : providedModels) {
        this.providedModelMap.put(model.getClassName(), model);
      }
    }
  }

  /**
   * 当前rest app 的ID
   */
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setOriginal(RestService original) {
    this.original = original;
  }



  /**
   * find rest class by its {@code VersionGroupedRestClass#getId()}
   */
  public VersionGroupedRestClass getRestClass(String restClassId) {
    return this.idToRestClassMap.get(restClassId);
  }

  public RestService getOriginal() {
    return original;
  }


  /**
   * find {@code ModelInfo} by  full qualified class name
   */
  public ModelInfo getModel(String className) {
    return this.modelMap.get(className);
  }

  /**
   * find {@code EnumInfo} by its full qualified name
   */
  public EnumInfo getEnum(String className) {
    return this.enumMap.get(className);
  }

  /**
   * find {@code ModelInfo} from provided models
   */
  public ModelInfo getProvidedModel(String className) {
    return this.providedModelMap.get(className);
  }

  /**
   * find {@code EnumInfo} from provided models
   */
  public EnumInfo getProvidedEnum(String className) {
    return this.providedEnumMap.get(className);
  }


  /**
   * find additional bizcodes from providedBizCodes
   */
  public List<BizCode> getProvidedBizCodes() {
    return Collections.unmodifiableList(this.providedBizCodes);
  }



  /**
   * 获取spi id对应的spi class
   */
  public Map<String, VersionGroupedRestClass> getIdToSpiClassMap() {
    return Collections.unmodifiableMap(this.idToRestClassMap);
  }


  /**
   * sort by {@code RestClass#getSummary()}
   */
  public List<VersionGroupedRestClass> getSpiClasses() {
    final List<VersionGroupedRestClass> spiClasses =
        new ArrayList<>(this.idToRestClassMap.values());
    Collections.sort(spiClasses, new Comparator<VersionGroupedRestClass>() {
      @Override
      public int compare(VersionGroupedRestClass s1, VersionGroupedRestClass s2) {
        return s1.getOriginal().getSummary()
            .compareTo(s2.getOriginal().getSummary());
      }
    });
    return spiClasses;
  }
}
