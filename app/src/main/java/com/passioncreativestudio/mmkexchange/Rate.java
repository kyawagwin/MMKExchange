package com.passioncreativestudio.mmkexchange;

public class Rate {

    private String id;
    private String baseCurrency;
    private int v;
    private long rateTimestamp;
    private Double MMK;
    private Double ZAR;
    private Double BRL;
    private Double RUB;
    private Double INR;
    private Double KRW;
    private Double NOK;
    private Double HKD;
    private Double SGD;
    private Double NZD;
    private Double MXN;
    private Double SEK;
    private Double CNY;
    private Double CHF;
    private Double CAD;
    private Double AUD;
    private Double GBP;
    private Double JPY;
    private Double EUR;
    private Double USD;
    private Double MYR;
    private Double TWD;
    private Double THB;

    public Rate(String id, String baseCurrency, Integer v, Integer rateTimestamp, Double MMK, Double ZAR, Double BRL, Double RUB, Double INR, Double NOK, Double HKD, Double SGD, Double NZD, Double MXN, Double SEK, Double CNY, Double CHF, Double CAD, Double AUD, Double GBP, Double JPY, Double EUR, Double USD, Double KRW, Double MYR, Double TWD, Double THB) {
        this.KRW = KRW;
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.v = v;
        this.rateTimestamp = rateTimestamp;
        this.MMK = MMK;
        this.ZAR = ZAR;
        this.BRL = BRL;
        this.RUB = RUB;
        this.INR = INR;
        this.NOK = NOK;
        this.HKD = HKD;
        this.SGD = SGD;
        this.NZD = NZD;
        this.MXN = MXN;
        this.SEK = SEK;
        this.CNY = CNY;
        this.CHF = CHF;
        this.CAD = CAD;
        this.AUD = AUD;
        this.GBP = GBP;
        this.JPY = JPY;
        this.EUR = EUR;
        this.USD = USD;
        this.MYR = MYR;
        this.TWD = TWD;
        this.THB = THB;
    }

    public String getId() {
        return id;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public Integer getV() {
        return v;
    }

    public long getRateTimestamp() {
        return rateTimestamp;
    }

    public Double getMMK() {
        return MMK;
    }

    public Double getZAR() {
        return ZAR;
    }

    public Double getBRL() {
        return BRL;
    }

    public Double getRUB() {
        return RUB;
    }

    public Double getINR() {
        return INR;
    }

    public Double getKRW() {
        return KRW;
    }

    public Double getNOK() {
        return NOK;
    }

    public Double getHKD() {
        return HKD;
    }

    public Double getSGD() {
        return SGD;
    }

    public Double getNZD() {
        return NZD;
    }

    public Double getMXN() {
        return MXN;
    }

    public Double getSEK() {
        return SEK;
    }

    public Double getCNY() {
        return CNY;
    }

    public Double getCHF() {
        return CHF;
    }

    public Double getCAD() {
        return CAD;
    }

    public Double getAUD() {
        return AUD;
    }

    public Double getGBP() {
        return GBP;
    }

    public Double getJPY() {
        return JPY;
    }

    public Double getEUR() {
        return EUR;
    }

    public Double getUSD() {
        return USD;
    }

    public Double getMYR() {
        return USD;
    }

    public Double getTWD() {
        return USD;
    }

    public Double getTHB() {
        return USD;
    }
}