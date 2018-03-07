package com.databyte.indra.remote;


public class ApiUtils {
    private ApiUtils() {
    }


        public static final String BASE_URL = "https://f67bbd97.ngrok.io/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}

