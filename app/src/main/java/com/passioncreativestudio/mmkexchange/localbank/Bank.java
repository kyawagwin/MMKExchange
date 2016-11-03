package com.passioncreativestudio.mmkexchange.localbank;

import java.util.ArrayList;
import java.util.List;

public class Bank {

    private String bankName;
    private String id;
    private List<Rate> rates = new ArrayList<Rate>();

    /**
     *
     * @return
     * The bankName
     */
    public String getBankName() {
        return bankName;
    }

    /**
     *
     * @param bankName
     * The bankName
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
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
     * The rates
     */
    public List<Rate> getRates() {
        return rates;
    }

    /**
     *
     * @param rates
     * The rates
     */
    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }

}
