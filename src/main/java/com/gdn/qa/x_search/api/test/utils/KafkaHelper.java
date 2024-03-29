package com.gdn.qa.x_search.api.test.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdn.qa.x_search.api.test.data.*;
import com.gdn.qa.x_search.api.test.models.CatalogDomainEventModel;
import com.gdn.qa.x_search.api.test.models.CategoryDomainEventModel;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.EVENT_ID;
import static com.gdn.qa.x_search.api.test.Constants.UrlConstants.FLASH_SALE_IMAGE;

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
        cncActivated(false).
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

    /**
     * @author poushaliM on 17/12/19
     * @project X-search
     */

  public void publishCategoryChangeEvent(String name, String catagoryCode, boolean activated,String catalogType){

    CatalogDomainEventModel catalogDomainEventModel=CatalogDomainEventModel.builder().name(name).catalogCode(catagoryCode)
        .catalogType(catalogType).build();
    CategoryDomainEventModel categoryDomainEventModel= CategoryDomainEventModel.builder()
        .timestamp(System.currentTimeMillis())
        .name(name)
        .categoryCode(catagoryCode)
        .activated(activated)
        .catalogDomainEventModel(catalogDomainEventModel).build();
    try {
      kafkaSender.send("com.gdn.x.productcategorybase.category.publish",
          objectMapper.writeValueAsString(categoryDomainEventModel));
    }catch (JsonProcessingException e){
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
        .exclusive(exclusive)
        .build();
    try {
      kafkaSender.send("com.gdn.x.campaign.published",
          objectMapper.writeValueAsString(campaignPublishEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void campaignLiveEventExclusive(String campaignName,
      String campaignCode,
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
        .exclusive(exclusive)
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
            build();

    try {
      kafkaSender.send("com.gdn.x.product.offlineitem.change",
          objectMapper.writeValueAsString(offlineItemChange));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

  public void publishOfflineItemChangeEventforDefCncJob(Map<String,String> payload) {
    OfflineItemChange offlineItemChange1 = OfflineItemChange.builder().
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

  public void publishProductReviewEvent(int storeId, String productId, String metaDataType,
      int averageRating, int[] ratings, double[] ratingPercentages, int reviewCount)
      throws JsonProcessingException {
    RatingProductIdModel ratingProductIdModel = RatingProductIdModel.builder().
        storeId(storeId).
        productId(productId).
        metaDataType(metaDataType).
        averageRating(averageRating).
        ratings(ratings).
        ratingPercentages(ratingPercentages).
        reviewCount(reviewCount).build();

    ProductReviewEventModel productReviewEventModel = ProductReviewEventModel.builder().
        timestamp(System.currentTimeMillis()).
        productReviewMetaDataList(new RatingProductIdModel[] {ratingProductIdModel}).
        build();

    kafkaSender.send("com.gdn.x.product.review.aggregate.update.count",
        objectMapper.writeValueAsString(productReviewEventModel));
  }

  public void publishTradeInAggregateEvent(String id, String productSku, String productName, boolean active)
      throws JsonProcessingException {
    TradeInAggregateModel tradeInAggregateModel = TradeInAggregateModel.builder().
        id(id).
        productSku(productSku).
        productName(productName).
        active(active).
        timestamp(System.currentTimeMillis()).
        build();

    kafkaSender.send("com.gdn.aggregate.platform.trade.in.eligible.products",
        objectMapper.writeValueAsString(tradeInAggregateModel));
  }


  public void buyBoxEvent(String itemSku, Double buyBoxScore)
  {
    try {
      ItemBuyBoxScoreDetail itemBuyBoxScoreDetail = ItemBuyBoxScoreDetail.builder()
          .itemSku(itemSku)
          .buyBoxScore(Double.valueOf(buyBoxScore)).build();

      BuyBoxModel buyBoxModel=BuyBoxModel.builder()
          .buyBoxScores(Collections.singletonList(itemBuyBoxScoreDetail))
          .eventId(EVENT_ID)
          .build();

      kafkaSender.send("com.gdn.x.buybox.score.change",
          objectMapper.writeValueAsString(buyBoxModel));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void publishAggregateInventoryChangeEvent(String itemSku,
      boolean cnc,
      String type,
      Map<String, String> location,
      String status1,
      String status2) throws JsonProcessingException {

    StockInformationModel stockInformationModel1 = StockInformationModel.builder().
        location(location.get("location1")).
        status(status1).build();
    StockInformationModel stockInformationModel2 = StockInformationModel.builder().
        location(location.get("location2")).
        status(status2).build();
    StockInformationModel stockInformationModel3 = StockInformationModel.builder().
        location(location.get("location3")).
        status(status1).build();

    AggregateInventoryChangeModel aggregateInventoryChangeModel =
        AggregateInventoryChangeModel.builder().
            itemSku(itemSku).
            cnc(cnc).
            type(type).
            stockInformations(new StockInformationModel[] {stockInformationModel1,
                stockInformationModel2, stockInformationModel3}).
            build();

    kafkaSender.sendEvent("com.gdn.aggregate.modules.inventory.changed.event",
        objectMapper.writeValueAsString(aggregateInventoryChangeModel));
  }

  public void publishAggregateInventoryChangeEvent(String itemSku,
      boolean cnc,
      String type,
      Map<String, String> location,
      String status1,
      String ppCode1,
      String status2,
      String ppCode2) throws JsonProcessingException {

    StockInformationModel stockInformationModel1 = StockInformationModel.builder().
        location(location.get("location1")).
        status(status1).
        pickupPointCode(ppCode1).build();
    StockInformationModel stockInformationModel2 = StockInformationModel.builder().
        location(location.get("location2")).
        status(status2).
        pickupPointCode(ppCode2).build();

    AggregateInventoryChangeModel aggregateInventoryChangeModel =
        AggregateInventoryChangeModel.builder().
            itemSku(itemSku).
            cnc(cnc).
            type(type).
            stockInformations(new StockInformationModel[] {stockInformationModel1, stockInformationModel2}).
            build();

    kafkaSender.sendEvent("com.gdn.aggregate.modules.inventory.changed.event",
        objectMapper.writeValueAsString(aggregateInventoryChangeModel));
  }


  public void publishSearchBwaEvent(Map<String, String> payload) {

    BwaSearchEventModel bwaSearchEventModel = BwaSearchEventModel.builder().
            accountid(payload.get("accountId")).
            userid(payload.get("userId")).
            sessionid(payload.get("sessionId")).
            searchinternalkeyword(payload.get("keyword")).
            searchinternalcategoryid(payload.get("categoryId")).
            searchinternalcategoryname(payload.get("categoryName")).
            clientmemberid(payload.get("clientMemberId")).
            pageurl(payload.get("url")).
            pagetype(payload.get("pageType")).
            device(payload.get("deviceType")).
            devicetype(payload.get("device")).
            browser(payload.get("browser")).
            browserversion(payload.get("browserType")).
            build();
    BwaEventModel bwaEventModel = BwaEventModel.builder().searchinternalkeyword(bwaSearchEventModel).build();
    try {
      kafkaSender.send("topic.bwa.search.internal.keyword",
              objectMapper.writeValueAsString(bwaEventModel));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void publishCampaignEventExclusiveForCnc(String campaignName,
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

    ProductSkuEventModel productSkuEventModel = ProductSkuEventModel.builder()
            .productSku(productSku)
            .itemSku(itemSku)
            .discount(discount)
            .quota(quota)
            .build();

    List<ProductSkuEventModel> skuList = new ArrayList<>();
    skuList.add(productSkuEventModel);


    CampaignPublishEvent campaignPublishEvent = CampaignPublishEvent.builder()
            .timestamp(System.currentTimeMillis())
            .campaignName(campaignName)
            .campaignCode(campaignCode)
            .skuList(skuList)
            .promotionStartTime(dtStart.toDate())
            .promotionEndTime(dtEnd.toDate())
            .tagLabel(tagLabel)
            .exclusive(exclusive)
            .build();
    try {
      kafkaSender.send("com.gdn.x.campaign.published",
              objectMapper.writeValueAsString(campaignPublishEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void publishMerchantVoucherEvent(String itemSku,int voucherCount){

    List<VoucherItemMap> itemMapList = new ArrayList<>();

    VoucherItemMap voucherItemMap = VoucherItemMap.builder()
        .uniqueId(itemSku)
        .voucherCount(voucherCount)
        .cncRuleApplied(false)
        .build();

    itemMapList.add(voucherItemMap);

    MerchantVoucherEventModel merchantVoucher = MerchantVoucherEventModel.builder()
        .voucherItemMap(itemMapList)
        .storeId("10001")
        .build();

    try {
      kafkaSender.send("com.gdn.partners.merchant.voucher.sku.mapping.count.event.external",
          objectMapper.writeValueAsString(merchantVoucher));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void publishOxfordFlagChange(String merchantCode,List<String> brandsToAdd,List<String> brandsToDelete,boolean isOfficial){
    OxfordFlagChangeEventModel oxfordFlagChangeEventModel = OxfordFlagChangeEventModel.builder()
        .timestamp(System.currentTimeMillis())
        .code(merchantCode)
        .officialStore(isOfficial)
        .officialBrands(brandsToAdd)
        .removedOfficialBrands(brandsToDelete)
        .build();

    try {
      kafkaSender.send("oxford_update_merchant_event",
          objectMapper.writeValueAsString(oxfordFlagChangeEventModel));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void publishOxfordSkuChange(String sku,String brand,String store){

    Map<OxfordSkuChangeModel.ContractType, List<String>> map = new HashMap<>();
    map.put(OxfordSkuChangeModel.ContractType.STORE,Collections.singletonList(store));
    map.put(OxfordSkuChangeModel.ContractType.BRAND,Collections.singletonList(brand));

    OxfordSkuChangeModel oxfordSkuChangeModel = OxfordSkuChangeModel.builder()
        .timestamp(System.currentTimeMillis())
        .sku(sku)
        .catalogNamesByContractType(map)
        .build();

    try {
      kafkaSender.send("oxford_update_product_event",
          objectMapper.writeValueAsString(oxfordSkuChangeModel));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void publishFlashSaleCampaignEvent(Map<String,String> params) {

    Date date = new Date();
    DateTime dtStart = new DateTime(date);
    DateTime dtEnd = dtStart.plusDays(1);

    String[] itemSkuArray = params.get("itemSkus").split(",");
    String[] sessionIds = params.get("sessionIds").split(",");

    List<ProductSkuEventModel> skuList = IntStream.range(0, itemSkuArray.length)
        .mapToObj(i -> ProductSkuEventModel.builder()
            .productSku(params.get("productSku"))
            .itemSku(itemSkuArray[i])
            .discount(10.0)
            .quota(10)
            .sessionId(Integer.parseInt(sessionIds[i]))
            .build())
        .collect(Collectors.toList());

    CampaignPublishEvent campaignPublishEvent = CampaignPublishEvent.builder()
        .timestamp(System.currentTimeMillis())
        .campaignName(params.get("campaignName"))
        .campaignCode(params.get("campaignCode"))
        .skuList(skuList)
        .promotionStartTime(dtStart.toDate())
        .promotionEndTime(dtEnd.toDate())
        .tagLabel(FLASH_SALE_IMAGE)
        .exclusive(true)
        .retainData(true)
        .campaignType("FLASH_SALE")
        .build();

    try {
      kafkaSender.send("com.gdn.x.campaign.published",
          objectMapper.writeValueAsString(campaignPublishEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void publishFlashSaleLiveEvent(String campaignName, String campaignCode,int session) {
    Date date = new Date();
    DateTime dtStart = new DateTime(date);
    DateTime dtEnd = dtStart.plusDays(1);

    CampaignEventModel campaignEventModel = CampaignEventModel.builder()
        .campaignCode(campaignCode)
        .campaignName(campaignName)
        .promotionEndTime(dtEnd.toDate())
        .promotionStartTime(dtStart.toDate())
        .exclusive(true)
        .tagLabel(FLASH_SALE_IMAGE)
        .activateSession(session)
        .build();

    CampaignLiveExclusive campaignLiveExclusive = CampaignLiveExclusive.builder()
        .timestamp(System.currentTimeMillis())
        .campaigns(Collections.singletonList(campaignEventModel))
        .build();
    try {
      kafkaSender.send("com.gdn.x.campaign.live",
          objectMapper.writeValueAsString(campaignLiveExclusive));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void publishFlashSaleRemoveEvent(String campaignCode,
      String productSku,
      String itemSkuForRemove,
      int session) {

    ProductSkuEventModel productSkuEventModel = ProductSkuEventModel.builder()
        .productSku(productSku)
        .itemSku(itemSkuForRemove)
        .discount(10.0)
        .quota(10)
        .sessionId(session)
        .blibliDiscount(10.0)
        .blibliQuota(10)
        .build();

    CampaignRemoveEvent campaignRemoveEvent = CampaignRemoveEvent.builder()
        .timestamp(System.currentTimeMillis())
        .campaignCode(campaignCode)
        .skuList(Collections.singletonList(productSkuEventModel))
        .emptyQuota(false)
        .build();
    try {
      kafkaSender.send("com.gdn.x.campaign.product.removed",
          objectMapper.writeValueAsString(campaignRemoveEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void publishFlashSaleStopEvent(String campaignCode, int session) {

    CampaignStopEvent campaignStopEvent = CampaignStopEvent.builder()
        .timestamp(System.currentTimeMillis())
        .campaignCode(campaignCode)
        .session(session)
        .build();
    try {
      kafkaSender.send("com.gdn.x.campaign.stopped",
          objectMapper.writeValueAsString(campaignStopEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void publishFlashSaleEndEvent(String campaignCode, int session) {

    CampaignSession campaignSession = CampaignSession.builder()
        .campaignCode(campaignCode)
        .session(session)
        .build();

    CampaignEndEvent campaignEndEvent = CampaignEndEvent.builder()
        .campaignCodeList(Collections.singletonList(campaignCode))
        .campaignSessionList(Collections.singletonList(campaignSession))
        .markForDelete(false)
        .build();

    try {
      kafkaSender.send("com.gdn.x.campaign.ended",
          objectMapper.writeValueAsString(campaignEndEvent));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

}
