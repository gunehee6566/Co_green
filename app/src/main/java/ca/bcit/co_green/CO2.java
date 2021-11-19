package ca.bcit.co_green;

public class CO2 {
    String id;
    String driveDistance;
    String elecUsed;

    public CO2(){};
    public CO2(String id, String driveDistance, String elecUsed){
        this.id = id;
        this.driveDistance = driveDistance;
        this.elecUsed = elecUsed;
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

}
