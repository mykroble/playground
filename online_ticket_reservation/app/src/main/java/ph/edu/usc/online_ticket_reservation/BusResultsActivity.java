package ph.edu.usc.online_ticket_reservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BusResultsActivity extends AppCompatActivity {

    private ListView busListView;
    private BusAdapter adapter;
    private List<Bus> busList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_results);

        busListView = findViewById(R.id.listViewBuses);
        busList = new ArrayList<>();

        // Dummy data (Replace with actual API data)
        busList.add(new Bus("Bus A", "10:00 AM", "2:00 PM", "4h", "$20"));
        busList.add(new Bus("Bus B", "11:30 AM", "3:30 PM", "4h", "$25"));

        adapter = new BusAdapter(this, busList);
        busListView.setAdapter(adapter);

        // On item click, open details activity
        busListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Bus selectedBus = busList.get(position);
            Intent intent = new Intent(BusResultsActivity.this, BusDetailsActivity.class);
            intent.putExtra("bus", selectedBus);
            startActivity(intent);
        });
    }
}