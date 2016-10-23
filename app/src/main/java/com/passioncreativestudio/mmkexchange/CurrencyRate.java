package com.passioncreativestudio.mmkexchange;

public class CurrencyRate {
    private String name;
    private Double rate;
    private String description;

    public CurrencyRate() {
    }

    public CurrencyRate(String name, Double rate, String description) {
        this.name = name;
        this.rate = rate;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Double getRate() {
        return rate;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
