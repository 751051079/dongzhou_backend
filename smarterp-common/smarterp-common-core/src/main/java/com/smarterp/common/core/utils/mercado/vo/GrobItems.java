package com.smarterp.common.core.utils.mercado.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GrobItems {

    /**
     * 标题
     */
    private String title;

    private String site_id = "CBT";
    /**
     * 类目
     */
    private String category_id;

    /**
     *价格
     */
    private BigDecimal price;

    /**
     *货币美元USD
     */
    private String currency_id = "USD";

    /**
     *数量
     */
    private int available_quantity;

    /**
     *
     */
    private String buying_mode = "buy_it_now";

    /**
     *
     */
    private String listing_type_id = "gold_pro";

    /**
     *
     */
    private String condition = "new";

    /**
     *
     */
    private String description;

    /**
     *
     */
    private String video_id;

    /**
     * 主图id
     */
    private String thumbnail_id;

    private List<Attributes> sale_terms;

    /**
     *
     */
    private List<ItemPictures> pictures = new ArrayList<ItemPictures>();

    /**
     *
     */
    private List<Attributes> attributes = null;

    /**
     *
     */
    private List<Variations> variations = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }

    public int getAvailable_quantity() {
        return available_quantity;
    }

    public void setAvailable_quantity(int available_quantity) {
        this.available_quantity = available_quantity;
    }

    public String getBuying_mode() {
        return buying_mode;
    }

    public void setBuying_mode(String buying_mode) {
        this.buying_mode = buying_mode;
    }

    public String getListing_type_id() {
        return listing_type_id;
    }

    public void setListing_type_id(String listing_type_id) {
        this.listing_type_id = listing_type_id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getThumbnail_id() {
        return thumbnail_id;
    }

    public void setThumbnail_id(String thumbnail_id) {
        this.thumbnail_id = thumbnail_id;
    }

    public List<ItemPictures> getPictures() {
        return pictures;
    }

    public void setPictures(List<ItemPictures> pictures) {
        this.pictures = pictures;
    }

    public List<Attributes> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attributes> attributes) {
        this.attributes = attributes;
    }

    public List<Variations> getVariations() {
        return variations;
    }

    public void setVariations(List<Variations> variations) {
        this.variations = variations;
    }

    public List<Attributes> getSale_terms() {
        return sale_terms;
    }

    public void setSale_terms(List<Attributes> sale_terms) {
        this.sale_terms = sale_terms;
    }
}
