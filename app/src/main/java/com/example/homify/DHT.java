package com.example.homify;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class DHT {
    private String Time;
    private Integer Humidity, Temperature;


    public DHT(){

    }

    public DHT(String Time, Integer humidity, Integer temperature) {
        this.Time = Time;
        this.Humidity = humidity;
        this.Temperature = temperature;

    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    public Integer getHumidity() {
        return Humidity;
    }

    public void setHumidity(Integer humidity) {
        this.Humidity = humidity;
    }

    public Integer getTemperature() {
        return Temperature;
    }

    public void setTemperature(Integer temperature) {
        this.Temperature = temperature;
    }

    public void printDTH(){
        String[] dateParts = this.getTime().split("T");
        String[] datePartDate =dateParts[0].split("-");
        String year = datePartDate[0];
        String month = datePartDate[1];
        String day = datePartDate[2];
        String[] datePartH=dateParts[1].split("Z");
        String[] datePartTime=datePartH[0].split(":");
        String hour = datePartTime[0];
        String minute = datePartTime[1];
        String seconds = datePartTime[2];
        String finale = "Anno: "+year+" Mese: "+month+" Giorno: "+day+" alle "+datePartH[0];
        System.out.println(finale+"\n"+this.getTemperature()+"° con il "+this.getHumidity()+"% di umidità\n");

    }

    public Long setTimeAsSeconds(){
        String[] dateParts = this.getTime().split("T");
        String[] datePartDate =dateParts[0].split("-");
        String year = datePartDate[0];
        String month = datePartDate[1];
        String day = datePartDate[2];
        String[] datePartH=dateParts[1].split("Z");
        String[] datePartTime=datePartH[0].split(":");
        String hour = datePartTime[0];
        String minute = datePartTime[1];
        String seconds = datePartTime[2];
        ZonedDateTime ToDateTime = ZonedDateTime.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute), Integer.parseInt(seconds), 0, ZoneId.of("Europe/Rome"));
        ZonedDateTime FromDateTime = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("Europe/Rome"));

        long diff = ChronoUnit.SECONDS.between(FromDateTime, ToDateTime);
        System.out.println("Difference: " + diff + " seconds");
        return diff;
    }
}
