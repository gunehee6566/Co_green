package ca.bcit.co_green;

public class User {
    String id;
    String name;
    float co2;

    public User(){};
    public User(String id, String name){
        this.id= id;
        this.name=name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCo2() {
        return co2;
    }

    public void setCo2(float co2) {
        this.co2 = co2;
    }
}
