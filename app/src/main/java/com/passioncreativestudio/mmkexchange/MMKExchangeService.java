package com.passioncreativestudio.mmkexchange;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MMKExchangeService {
    @GET("history/?date={date}")
    Call<com.passioncreativestudio.mmkexchange.localbank.Rate> historyRate(@Path("date") String date);
}