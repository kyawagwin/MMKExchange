package com.passioncreativestudio.mmkexchange;

public class CurrencyRate {
    private String name;
    private Double rate;

    public CurrencyRate(String name, Double rate) {
        this.name = name;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public Double getRate() {
        return rate;
    }
}
