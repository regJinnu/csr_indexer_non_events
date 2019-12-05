package com.gdn.qa.x_search.api.test.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdn.qa.x_search.api.test.data.*;
import com.gdn.x.product.domain.event.enums.ItemChangeEventType;
import com.gdn.x.product.domain.event.model.ItemViewConfig;
import com.gdn.x.product.domain.event.model.Price;
import com.gdn.x.product.domain.event.model.PristineDataItemEventModel;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author kumar on 01/08/18
 * @project X-search
 */
@Slf4j
@Service
public class KafkaHelper {

  @Autowired
  private KafkaSender kafkaSender;

  @Autowired
  private ApplicationContext applicationContext;


  private ObjectMapper objectMapper = new ObjectMapper();

  public void publishOOSEvent(String level2Id, String level2MerchantCode, String type) {

    OOSEvent oosEvent = OOSEvent.builder().
        level2Id(level2Id).
        level2MerchantCode(level2MerchantCode).
        storeId("10001").
        uniqueId(level2Id).
        timestamp(System.currentTimeMillis()).build();

    try {

      if (type.equals("oos"))
        kafkaSender.send("com.gdn.x.inventory.stock.oos.event",
            objectMapper.writeValueAsString(oosEvent));
      else if (type.equals("nonOOS"))
        kafkaSender.send("com.gdn.x.inventory.stock.non.oos.event",
            objectMapper.writeValueAsString(oosEvent));

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

  public void publishStoreClosedEvent(String businessPartnerCode, boolean isDelayShipping) {

    Date date = new Date();
    DateTime dtStartWithBuffer = new DateTime(date);
    DateTime dtStart = dtStartWithBuffer.plusDays(2);
    DateTime dtEnd = dtStart.plusDays(2);

    BPStoreClosedEvent bpStoreClosedEvent = BPStoreClosedEvent.builder().
        timestamp(System.currentTimeMillis()).
        businessPartnerCode(businessPartnerCode).
        startDate(dtStart.getMillis()).
        endDate(dtEnd.getMillis()).
        publishType("EXECUTE_START").
        startDateWithBufferClosingStoreDay(dtStartWithBuffer.getMillis()).
        delayShipping(isDelayShipping).build();

    try {
      kafkaSender.send("com.gdn.x.bpservice.store.closing.publish",
          objectMapper.writeValueAsString(bpStoreClosedEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

  public void publishBPprofileFieldUpdateEvent(String businessPartnerCode) {

    CompanyV0 companyV0 = CompanyV0.builder().
        changedFields(Collections.singleton(CompanyChangeFields.CNC_ACTIVATED)).
        cncActivated(false).
        build();

    BPprofileUpdateFields bPprofileUpdateFields = BPprofileUpdateFields.builder().
        timestamp(System.currentTimeMillis()).
        businessPartnerCode(businessPartnerCode).
        company(companyV0).
        build();

    try {
      kafkaSender.send("com.gdn.x.businesspartner.profile.update.fields",
          objectMapper.writeValueAsString(bPprofileUpdateFields));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void publishLogisticOptionChange(String merchant,
      String logisticOption,
      String commType,
      String logisticProductCodeList) {

    ArrayList<String> commissionTypeList = new ArrayList<>();
    if (commType.contains(",")) {
      commissionTypeList.addAll(Arrays.asList(commType.split(",")));
    } else {
      commissionTypeList.add(commType);
    }

    ArrayList<String> merchantList = new ArrayList<>();
    if (merchant.contains(",")) {
      merchantList.addAll(Arrays.asList(merchant.split(",")));
    } else {
      merchantList.add(merchant);
    }


    ArrayList<String> logisticProdCodeList = new ArrayList<>();
    if (logisticProductCodeList.contains(",")) {
      logisticProdCodeList.addAll(Arrays.asList(logisticProductCodeList.split(",")));
    } else {
      logisticProdCodeList.add(logisticOption);
    }

    LogisticOptionChange logisticOptionChange = LogisticOptionChange.builder().
        timestamp(System.currentTimeMillis()).
        commissionTypeList(commissionTypeList).
        merchantIdList(merchantList).
        logisticProductCodeList(logisticProdCodeList).
        logisticOptionCode(logisticOption).
        markForDelete(false).
        activeStatus(true).
        build();

    try {
      kafkaSender.send("com.gdn.x.shipping.domain.logistic.option.change.event",
          objectMapper.writeValueAsString(logisticOptionChange));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

  public void publishLogisticProductOriginsChangeEvent(String logisticProductCode) {

    List<String> originList = new ArrayList<>(Arrays.asList(
        "Origin-Jakarta",
        "Origin-Surabaya",
        "Origin-Bandung",
        "Origin-Denpasar"
));

    LogisticProductOriginChangeEvent logisticProductOriginChangeEvent =
        LogisticProductOriginChangeEvent.builder().
            originList(originList).
            logisticProductCode(logisticProductCode).
            build();

    try {
      kafkaSender.send("com.gdn.x.shipping.domain.origin.change.event",
          objectMapper.writeValueAsString(logisticProductOriginChangeEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

  public void publishItemChangeEvent(String itemSku,
      String productSku,
      boolean isArchived,
      boolean isSynchronised,
      List<ItemChangeEventType> itemChangeEventTypes,
      Set<Price> price,
      boolean off2OnChannelActive,
      PristineDataItemEventModel pristineDataItemEventModel,Set<ItemViewConfig> itemViewConfigs) {

    ItemChangeEvent itemChangeEvent = ItemChangeEvent.builder().
        timestamp(System.currentTimeMillis()).
        itemSku(itemSku).
        productSku(productSku).
        isArchived(isArchived).
        isSynchronized(isSynchronised).
        uniqueId(itemSku).
        itemChangeEventTypes(itemChangeEventTypes).
        price(price).
        off2OnChannelActive(off2OnChannelActive).
        pristineDataItem(pristineDataItemEventModel).
        itemViewConfigs(itemViewConfigs).
        build();

    try {
      kafkaSender.send("com.gdn.x.product.item.change",
          objectMapper.writeValueAsString(itemChangeEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void publishProductChangeEvent(String productCode,
      String productSku,
      boolean markForDelete,
      boolean isSynchronised) {

    ProductChangeEvent productChangeEvent = ProductChangeEvent.builder().
        timestamp(System.currentTimeMillis()).
        productCode(productCode).
        productSku(productSku).
        markForDelete(markForDelete).
        isSynchronized(isSynchronised).
        build();

    try {
      kafkaSender.send("com.gdn.x.product.product.change",
          objectMapper.writeValueAsString(productChangeEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }


  public void publishCampaignEvent(String campaignName,
      String campaignCode,
      String productSku,
      String itemSku,
      Double discount) {

    Date date = new Date();
    DateTime dtStart = new DateTime(date);
    DateTime dtEnd = dtStart.plusDays(1);

    String itemSku1 = itemSku.split(",")[0];
    String itemSku2 = itemSku.split(",")[1];

    ProductSkuEventModel productSkuEventModel1 = ProductSkuEventModel.builder()
        .productSku(productSku)
        .itemSku(itemSku1)
        .discount(discount)
        .build();

    ProductSkuEventModel productSkuEventModel2 = ProductSkuEventModel.builder()
        .productSku(productSku)
        .itemSku(itemSku2)
        .discount(discount)
        .build();

    List<ProductSkuEventModel> skuList = new ArrayList<>();
    skuList.add(productSkuEventModel1);
    skuList.add(productSkuEventModel2);


    CampaignPublishEvent campaignPublishEvent = CampaignPublishEvent.builder()
        .timestamp(System.currentTimeMillis())
        .campaignName(campaignName)
        .campaignCode(campaignCode)
        .skuList(skuList)
        .promotionStartTime(dtStart.toDate())
        .promotionEndTime(dtEnd.toDate())
        .build();
    try {
      kafkaSender.send("com.gdn.x.campaign.published",
          objectMapper.writeValueAsString(campaignPublishEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void publishCampaignEventExclusive(String campaignName,
      String campaignCode,
      String productSku,
      String itemSku,
      Double discount,
      int quota,
      String tagLabel,
      boolean exclusive) {

    Date date = new Date();
    DateTime dtStart = new DateTime(date);
    DateTime dtEnd = dtStart.plusDays(1);

    String itemSku1 = itemSku.split(",")[0];
    String itemSku2 = itemSku.split(",")[1];

    ProductSkuEventModel productSkuEventModel1 = ProductSkuEventModel.builder()
        .productSku(productSku)
        .itemSku(itemSku1)
        .discount(discount)
        .quota(quota)
        .build();

    ProductSkuEventModel productSkuEventModel2 = ProductSkuEventModel.builder()
        .productSku(productSku)
        .itemSku(itemSku2)
        .discount(discount)
        .quota(quota)
        .build();

    List<ProductSkuEventModel> skuList = new ArrayList<>();
    skuList.add(productSkuEventModel1);
    skuList.add(productSkuEventModel2);


    CampaignPublishEvent campaignPublishEvent = CampaignPublishEvent.builder()
        .timestamp(System.currentTimeMillis())
        .campaignName(campaignName)
        .campaignCode(campaignCode)
        .skuList(skuList)
        .promotionStartTime(dtStart.toDate())
        .promotionEndTime(dtEnd.toDate())
        .tagLabel(tagLabel)
        .exclusive(true)
        .build();
    try {
      kafkaSender.send("com.gdn.x.campaign.published",
          objectMapper.writeValueAsString(campaignPublishEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void campaignLiveEventExclusive(String campaignCode,
      String campaignName,
      String tagLabel,
      boolean exclusive) {
    Date date = new Date();
    DateTime dtStart = new DateTime(date);
    DateTime dtEnd = dtStart.plusDays(1);

    CampaignEventModel campaignEventModel = CampaignEventModel.builder()
        .campaignCode(campaignCode)
        .campaignName(campaignName)
        .promotionEndTime(dtEnd.toDate())
        .promotionStartTime(dtStart.toDate())
        .exclusive(true)
        .tagLabel(tagLabel)
        .build();

    CampaignLiveExclusive campaignLiveExclusive = CampaignLiveExclusive.builder()
        .campaigns(Collections.singletonList(campaignEventModel))
        .build();
    try {
      kafkaSender.send("com.gdn.x.campaign.live",
          objectMapper.writeValueAsString(campaignLiveExclusive));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

  public void campaignRemoveEvent(String campaignCode,
      String productSku,
      String itemSkuForRemove,
      Double discount) {

    ProductSkuEventModel productSkuEventModel = ProductSkuEventModel.builder()
        .productSku(productSku)
        .itemSku(itemSkuForRemove)
        .discount(discount)
        .build();

    CampaignRemoveEvent campaignRemoveEvent = CampaignRemoveEvent.builder()
        .campaignCode(campaignCode)
        .skuList(Collections.singletonList(productSkuEventModel))
        .timestamp(System.currentTimeMillis())
        .build();
    try {
      kafkaSender.send("com.gdn.x.campaign.product.removed",
          objectMapper.writeValueAsString(campaignRemoveEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void campaignLiveEvent(String campaignCode,
      String campaignName,
      boolean isExclusive,
      String tagLabel,
      boolean markForDelete) {

    Date dtStart = new Date();
    Date dtEnd = new Date(dtStart.getTime() + (1000 * 60 * 60 * 24));


    List<CampaignEventModel> campaignEventModelList = new ArrayList<>();

    CampaignEventModel campaignEventModel = CampaignEventModel.builder()
        .campaignCode(campaignCode)
        .campaignName(campaignName)
        .exclusive(isExclusive)
        .tagLabel(tagLabel)
        .promotionEndTime(dtEnd)
        .promotionStartTime(dtStart)
        .build();

    campaignEventModelList.add(campaignEventModel);

    CampaignLiveEvents campaignLiveEvents = CampaignLiveEvents.builder()
        .campaigns(campaignEventModelList)
        .timestamp(System.currentTimeMillis())
        .storeId("10001")
        .markForDelete(markForDelete)
        .build();
    try {
      kafkaSender.send("com.gdn.x.campaign.live",
          objectMapper.writeValueAsString(campaignLiveEvents));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void campaignStopEvent(String campaignCode, boolean markForDelete) {

    CampaignStopEvent campaignStopEvent = CampaignStopEvent.builder()
        .campaignCode(campaignCode)
        .timestamp(System.currentTimeMillis())
        .storeId("10001")
        .markForDelete(markForDelete)
        .build();
    try {
      kafkaSender.send("com.gdn.x.campaign.stopped",
          objectMapper.writeValueAsString(campaignStopEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void campaignEndEvent(String campaignCodeList, boolean markForDelete) {
    List<String> campaignList = new ArrayList<>();
    campaignList.add(String.valueOf(campaignCodeList));
    CampaignEndEvent campaignEndEvent = CampaignEndEvent.builder().
        campaignCodeList(campaignList).
        markForDelete(false).build();
    try {
      kafkaSender.send("com.gdn.x.campaign.ended",
          objectMapper.writeValueAsString(campaignEndEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void productAdjustmentChangeEvent(String adjustmentName,
      String description,
      String promoItemSKU,
      long promoValue,
      boolean promoActivated) {
    Date date = new Date();
    DateTime dtStart = new DateTime(date);
    DateTime dtEnd = dtStart.plusDays(1);
    AdjustmentProductChangeModel adjustmentProductChangeModel =
        AdjustmentProductChangeModel.builder()
            .timestamp(System.currentTimeMillis())
            .adjustmentName(adjustmentName)
            .productSku(promoItemSKU)
            .description(description)
            .value(promoValue)
            .activated(promoActivated)
            .endDate(dtEnd.toDate())
            .startDate(dtStart.toDate())
            .build();
    try {
      kafkaSender.send("com.gdn.x.promotion.adjustment.product.change",
          objectMapper.writeValueAsString(adjustmentProductChangeModel));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void promoBundlingActivateEvent(String promoBundlingId,
      String promoItemSKU,
      String promoBundlingType,
      List<String> complementaryProducts) {
    Date date = new Date();
    DateTime presentDate = new DateTime(date);
    DateTime dtStart = presentDate.withTimeAtStartOfDay();
    DateTime dtEnd = dtStart.plusDays(1);
    PromoBundlingActivateModel promoBundlingModel = PromoBundlingActivateModel.builder()
        .timestamp(System.currentTimeMillis())
        .promoBundlingId(promoBundlingId)
        .mainItemSku(promoItemSKU)
        .promoBundlingType(promoBundlingType)
        .storeId("10001")
        .startDate(dtStart.toDate())
        .endDate(dtEnd.toDate())
        .complementaryProducts(complementaryProducts)
        .build();
    try {
      kafkaSender.send("com.gdn.x.promotion.promo.bundling.activated",
          objectMapper.writeValueAsString(promoBundlingModel));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void promoBundlingDeactivatedEvent(
  String promoItemSKU ,
  String promoBundlingType){
    PromoBundlingDeactivateModel promoBundlingDeactivateModel=PromoBundlingDeactivateModel.builder()
        .timestamp(System.currentTimeMillis())
        .sku(promoItemSKU)
        .storeId("10001")
        .promoBundlingType(promoBundlingType).build();
    try {
      kafkaSender.send("com.gdn.x.promotion.promo.bundling.deactivated",
          objectMapper.writeValueAsString(promoBundlingDeactivateModel));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

  public void pristineEvent(String productIdforPristine,
      String ProductItemId,
      String PristineAttributesName,
      String PristineAttributesValue,
      List<String> BlibliCategoryHierarchy,
      String Category,
      String PristineID,
      String itemCount) {
    MergedProductAttributeDetail mergedProductAttributeDetail =
        MergedProductAttributeDetail.builder()
            .name(PristineAttributesName)
            .value(PristineAttributesValue)
            .build();

    Set<MergedProductAttributeDetail> attributes = new HashSet<>();
    attributes.add(mergedProductAttributeDetail);
    MergedProductItemDetail mergedProductItemDetail = MergedProductItemDetail.builder()
        .productItemId(ProductItemId)
        .actionType(ActionType.ADD)
        .attributes(attributes)
        .build();

    List<MergedProductItemDetail> productItemDetails = new ArrayList<>();
    productItemDetails.add(mergedProductItemDetail);

    MergedProductDetailNew mergedProductDetailNew = MergedProductDetailNew.builder()
        .productId(productIdforPristine)
        .productItemDetails(productItemDetails)
        .blibliCategoryHierarchy(BlibliCategoryHierarchy)
        .category(Category)
        .id(PristineID)
        .build();

    PristineEventsModel pristineEventsModel = PristineEventsModel.builder()
        .eventDateTime(System.currentTimeMillis())
        .itemCount(Integer.parseInt(itemCount))
        .productDetail(mergedProductDetailNew)
        .productId(productIdforPristine)
        .timestamp(System.currentTimeMillis())
        .build();
    try {
      kafkaSender.send("com.gdn.ext.catalog.approved.product.change",
          objectMapper.writeValueAsString(pristineEventsModel));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void publishLogisticProductChange(String merchant,
      String logisticOption,
      String commType,
      String logisticProductCode) {

    ArrayList<String> commissionTypeList = new ArrayList<>();
    if (commType.contains(",")) {
      commissionTypeList.addAll(Arrays.asList(commType.split(",")));
    } else {
      commissionTypeList.add(commType);
    }

    ArrayList<String> merchantList = new ArrayList<>();
    if (merchant.contains(",")) {
      merchantList.addAll(Arrays.asList(merchant.split(",")));
    } else {
      merchantList.add(merchant);
    }


    ArrayList<String> logisticOptionCodeList = new ArrayList<>();
    if (logisticOption.contains(",")) {
      logisticOptionCodeList.addAll(Arrays.asList(logisticOption.split(",")));
    } else {
      logisticOptionCodeList.add(logisticOption);
    }

    LogisticProductChangeEvent logisticProductChangeEvent = LogisticProductChangeEvent.builder().
        timestamp(System.currentTimeMillis()).
        commissionTypeList(commissionTypeList).
        merchantIdList(merchantList).
        logisticProductCode(logisticProductCode).
        logisticOptionCodeList(logisticOptionCodeList).
        markForDelete(false).
        activeStatus(true).
        build();

    try {
      kafkaSender.send("com.gdn.x.shipping.domain.logistic.product.change.event",
          objectMapper.writeValueAsString(logisticProductChangeEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

  public void publishLogisticProductOriginsChangeEvent(String logisticProductCode,String location) {

    List<String> originList = new ArrayList<>();
    originList.add(location);

    LogisticProductOriginChangeEvent logisticProductOriginChangeEvent =
        LogisticProductOriginChangeEvent.builder().
            timestamp(System.currentTimeMillis()).
            originList(originList).
            logisticProductCode(logisticProductCode).
            build();

    try {
      kafkaSender.send("com.gdn.x.shipping.domain.origin.change.event",
          objectMapper.writeValueAsString(logisticProductOriginChangeEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }


  public void publishOfflineItemChangeEvent(Map<String,String> params) {

    OfflineItemChange offlineItemChange =
        OfflineItemChange.builder().
            timestamp(System.currentTimeMillis()).
            uniqueId(params.get("uniqueId")).
            merchantCode(params.get("merchantCode")).
            itemSku(params.get("itemSku")).
            pickupPointCode(params.get("pickupPointCode")).
            productSku(params.get("productSku")).
            offerPrice(Double.valueOf(params.get("defCncOfferPrice"))).
            build();

    try {
      kafkaSender.send("com.gdn.x.product.offlineitem.change",
          objectMapper.writeValueAsString(offlineItemChange));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

  public void publishOfflineItemChangeEventforDefCncJob(Map<String,String> payload) {
    OfflineItemChange offlineItemChange1 =
        OfflineItemChange.builder().
            timestamp(System.currentTimeMillis()).
            uniqueId(payload.get("uniqueId")).
            merchantCode(payload.get("merchantCode")).
            itemSku(payload.get("itemSku")).
            itemCode(payload.get("itemCode")).
            pickupPointCode(payload.get("pickupPointCode")).
            productSku(payload.get("productSku")).
            merchantSku(payload.get("merchantSku")).
            externalPickupPointCode(payload.get("externalPickupPointCode")).
            offerPrice(Double.valueOf(payload.get("offerPrice"))).
            markForDelete(true).
            build();
    try {
      kafkaSender.send("com.gdn.x.product.offlineitem.change",
          objectMapper.writeValueAsString(offlineItemChange1));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }
}
