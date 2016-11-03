package com.passioncreativestudio.mmkexchange.localbank;

/**
 * Created by kyawagwin on 3/11/16.
 */
public class Rate {

    private Integer buyingRate;
    private Integer sellingRate;
    private String id;
    private Object standardRate;

    /**
     *
     * @return
     * The buyingRate
     */
    public Integer getBuyingRate() {
        return buyingRate;
    }

    /**
     *
     * @param buyingRate
     * The buyingRate
     */
    public void setBuyingRate(Integer buyingRate) {
        this.buyingRate = buyingRate;
    }

    /**
     *
     * @return
     * The sellingRate
     */
    public Integer getSellingRate() {
        return sellingRate;
    }

    /**
     *
     * @param sellingRate
     * The sellingRate
     */
    public void setSellingRate(Integer sellingRate) {
        this.sellingRate = sellingRate;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The _id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The standardRate
     */
    public Object getStandardRate() {
        return standardRate;
    }

    /**
     *
     * @param standardRate
     * The standardRate
     */
    public void setStandardRate(Object standardRate) {
        this.standardRate = standardRate;
    }

}
