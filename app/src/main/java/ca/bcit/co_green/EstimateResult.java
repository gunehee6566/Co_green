package ca.bcit.co_green;

public class EstimateResult {
    private double co2e;
    private String co2e_unit;
    private String id;
    private String source;
    private String year;
    private String region;
    private String category;

    public double getCo2e() {
        return co2e;
    }

    public void setCo2e(double co2e) {
        this.co2e = co2e;
    }

    public String getCo2e_unit() {
        return co2e_unit;
    }

    public void setCo2e_unit(String co2e_unit) {
        this.co2e_unit = co2e_unit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
