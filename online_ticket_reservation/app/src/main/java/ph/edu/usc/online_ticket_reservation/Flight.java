package ph.edu.usc.online_ticket_reservation;

import java.io.Serializable;

public class Flight implements Serializable {
    private String airline, flightNumber, departureTime, arrivalTime, duration, price;

    public Flight(String airline, String flightNumber, String departureTime, String arrivalTime, String duration, String price) {
        this.airline = airline;
        this.flightNumber = flightNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.price = price;
    }

    public String getAirline() { return airline; }
    public String getFlightNumber() { return flightNumber; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public String getDuration() { return duration; }
    public String getPrice() { return price; }
}
