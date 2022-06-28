package server;

public class Report {

    private float longitude, latitude;
    private String reason;

    public Report(float longitude, float latitude, String radius){
        this.longitude = longitude;
        this.latitude = latitude;
        this.reason = radius;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public String getReason() {
        return reason;
    }

}
