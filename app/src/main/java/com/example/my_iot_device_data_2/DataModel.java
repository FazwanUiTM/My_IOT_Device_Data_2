package com.example.my_iot_device_data_2;

public class DataModel {

    String Date;
    String Temp;
    String Humid;

    public DataModel(){

    }

    public DataModel(String date, String temp, String humid) {
        Date = date;
        Temp = temp;
        Humid = humid;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTemp() {
        return Temp;
    }

    public void setTemp(String temp) {
        Temp = temp;
    }

    public String getHumid() {
        return Humid;
    }

    public void setHumid(String humid) {
        Humid = humid;
    }

}

