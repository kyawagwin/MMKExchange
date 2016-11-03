package com.passioncreativestudio.mmkexchange.localbank;

import java.util.ArrayList;
import java.util.List;

public class LocalBank {

    private String id;
    private Integer rateTimestamp;
    private Integer v;
    private List<Bank> banks = new ArrayList<Bank>();

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The _id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The rateTimestamp
     */
    public Integer getRateTimestamp() {
        return rateTimestamp;
    }

    /**
     * @param rateTimestamp The rateTimestamp
     */
    public void setRateTimestamp(Integer rateTimestamp) {
        this.rateTimestamp = rateTimestamp;
    }

    /**
     * @return The v
     */
    public Integer getV() {
        return v;
    }

    /**
     * @param v The __v
     */
    public void setV(Integer v) {
        this.v = v;
    }

    /**
     * @return The banks
     */
    public List<Bank> getBanks() {
        return banks;
    }

    /**
     * @param banks The banks
     */
    public void setBanks(List<Bank> banks) {
        this.banks = banks;
    }

}