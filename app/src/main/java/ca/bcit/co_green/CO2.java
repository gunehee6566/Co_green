package ca.bcit.co_green;

import android.webkit.HttpAuthHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CO2 {
    String id;
    String driveDistance;
    String elecUsed;
    Date timestamp;
    float co2;
    Map<String, String> co2Dictionary = new HashMap<>();



    public CO2(){};
    public CO2(String id, String driveDistance, String elecUsed){
        this.id = id;
        this.driveDistance = driveDistance;
        this.elecUsed = elecUsed;
        timestamp = new Date();
    }
    public String getId(){
        return id;
    }
    public void setId(String id){this.id = id;}

    public String getDriveDistance(){
        return driveDistance;
    }
    public void setDriveDistance(String driveDistance){
        this.driveDistance = driveDistance;
    }

    public String getElecUsed(){
        return elecUsed;
    }
    public void setElecUsed(String elecUsed){
        this.elecUsed = elecUsed;
    }

    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public float getCo2() {
        //return co2;
        return Float.parseFloat(driveDistance) + Float.parseFloat(elecUsed);
    }

    public void setCo2(float co2) {
        this.co2 = co2;
    }

    public Map<String, String> getCo2Dictionary() {
        return co2Dictionary;
    }

    public void setCo2Dictionary(Map<String, String> co2Dictionary) {
        this.co2Dictionary = co2Dictionary;
    }
}
