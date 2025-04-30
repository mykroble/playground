package ph.edu.usc.online_ticket_reservation;

import java.io.Serializable;

public class Bus implements Serializable {
    private String name, departureTime, arrivalTime, duration, price;

    public Bus(String name, String departureTime, String arrivalTime, String duration, String price) {
        this.name = name;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.price = price;
    }

    public String getName() { return name; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public String getDuration() { return duration; }
    public String getPrice() { return price; }
}
