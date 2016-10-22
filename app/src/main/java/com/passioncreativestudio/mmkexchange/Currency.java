package com.passioncreativestudio.mmkexchange;

import java.util.HashMap;

public class Currency {
    private String name;
    private String sign;
    private String description;

    public Currency(String name, String sign, String description) {
        this.name = name;
        this.sign = sign;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getSign() {
        return sign;
    }

    public String getDescription() {
        return description;
    }

    public static HashMap<String, Currency> initCurrencies() {
        HashMap<String, Currency> currenciesMap = new HashMap<>();
        currenciesMap.put("AUD", new Currency("AUD", "$", "Australia Dollar"));
        currenciesMap.put("BRL", new Currency("BRL", "R$", "Brazilian Real"));
        currenciesMap.put("CAD", new Currency("CAD", "$", "Canada Dollar"));
        currenciesMap.put("CHF", new Currency("CHF", "Fr", "Swiss Franc"));
        currenciesMap.put("CNY", new Currency("CNY", "¥", "Chinese Yuan"));
        currenciesMap.put("EUR", new Currency("EUR", "€", "Euro"));
        currenciesMap.put("GBP", new Currency("GBP", "£", "Pound Sterling"));
        currenciesMap.put("HKD", new Currency("HKD", "$", "Hong Kong Dollar"));
        currenciesMap.put("INR", new Currency("INR", "₹", "Indian Rupee"));
        currenciesMap.put("JPY", new Currency("JPY", "¥", "Japanese Yen"));
        currenciesMap.put("KRW", new Currency("KRW", "₩", "South Korean Won"));
        currenciesMap.put("MMK", new Currency("MMK", "Ks", "Myanmar Kyat"));
        currenciesMap.put("MYR", new Currency("MYR", "RM", "Malaysia Ringgit"));
        currenciesMap.put("MXN", new Currency("MXN", "$", "Mexican Peso"));
        currenciesMap.put("NOK", new Currency("NOK", "kr", "Norwegian Krone"));
        currenciesMap.put("NZD", new Currency("NZD", "$", "New Zealand Dollar"));
        currenciesMap.put("RUB", new Currency("RUB", "₽", "Russian Ruble"));
        currenciesMap.put("SEK", new Currency("SEK", "kr", "Swedish Krona"));
        currenciesMap.put("SGD", new Currency("SGD", "$", "Singapore Dollar"));
        currenciesMap.put("THB", new Currency("THB", "฿", "Thailand Baht"));
        currenciesMap.put("TWD", new Currency("TWD", "$", "Taiwan Dollar"));
        currenciesMap.put("USD", new Currency("USD", "$", "United States Dollar"));
        currenciesMap.put("ZAR", new Currency("ZAR", "R", "South African Rand"));


        return currenciesMap;
    }
}
