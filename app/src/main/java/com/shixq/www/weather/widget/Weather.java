package com.shixq.www.weather.widget;

/**
 * Created by shixq on 2018/3/17.
 */

public class Weather {
    private String cityName;
    //当前温度
    private String currTemp;
    //最低温度
    private String minTemp;
    //最高温度
    private String maxTemp;
    //风
    private String wind;
    private String weather;
    private String pm25;
    private String pm25Description;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCurrTemp() {
        return currTemp;
    }

    public void setCurrTemp(String currTemp) {
        this.currTemp = currTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getPm25Description() {
        return pm25Description;
    }

    public void setPm25Description(String pm25Description) {
        this.pm25Description = pm25Description;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "cityName='" + cityName + '\'' +
                ", currTemp='" + currTemp + '\'' +
                ", minTemp='" + minTemp + '\'' +
                ", maxTemp='" + maxTemp + '\'' +
                ", wind='" + wind + '\'' +
                ", weather='" + weather + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", pm25Description='" + pm25Description + '\'' +
                '}';
    }
}
