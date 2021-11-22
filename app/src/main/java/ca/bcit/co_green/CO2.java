package ca.bcit.co_green;

import java.util.Date;

public class CO2 {
    String id;
    String driveDistance;
    String elecUsed;
    Date timestamp;
    float co2;

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
    public void setId(){
        this.id = id;
    }
    public String getDriveDistance(){
        return driveDistance;
    }
    public void setDriveDistance(){
        this.driveDistance = driveDistance;
    }
    public String getElecUsed(){
        return elecUsed;
    }
    public void setElecUsed(){
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
}
