package ph.edu.usc.online_ticket_reservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FlightResultsActivity extends AppCompatActivity {

    private ListView flightListView;
    private FlightAdapter flightAdapter;
    private List<Flight> flightList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_results);

        flightListView = findViewById(R.id.listViewFlights);
        flightList = new ArrayList<>();

        // Dummy flight data
        flightList.add(new Flight("Airline A", "A123", "08:00 AM", "11:00 AM", "3h", "$250"));
        flightList.add(new Flight("Airline B", "B456", "10:00 AM", "1:30 PM", "3.5h", "$300"));

        flightAdapter = new FlightAdapter(this, flightList);
        flightListView.setAdapter(flightAdapter);

        // Click event to book a flight
        flightListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
//            Flight selectedFlight = flightList.get(position);
//            Intent intent = new Intent(FlightResultsActivity.this, FlightDetailsActivity.class);
//            intent.putExtra("flight", selectedFlight);
//            startActivity(intent);
        });
    }
}