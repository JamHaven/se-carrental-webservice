package pacApp.pacModel;

import javax.persistence.*;

@Entity
@Table(name = "Car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CarID", updatable = false, nullable = false)
    private long id;

    @Column(name = "Type")
    private String type;

    @Column(name = "Latitude")
    private Double latitude;

    @Column(name = "Longitude")
    private Double longitude;

    protected Car(){}

    public Car(long id, String type){
        this.id = id;
        this.type = type;
    }

    public long getId() {
        return this.id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type){
        this.type = type;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return String.format("Car[id=%d, type='%s']",id, type);
    }
}
