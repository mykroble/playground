package ph.edu.usc.online_ticket_reservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private String[] categories = {"Flights", "Bus", "Train", "Hotel", "My Trips"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Edge-to-Edge UI Handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        // Initialize ListView
        listView = findViewById(R.id.listViewCategories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        listView.setAdapter(adapter);

        // Handle Item Clicks
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            String selectedCategory = categories[position];

            switch (selectedCategory) {
                case "Flights":
                    startActivity(new Intent(MainActivity.this, FlightSearchActivity.class));
                    break;
                case "Bus":
                    startActivity(new Intent(MainActivity.this, BusSearchActivity.class));
                    break;
                case "Train":
                    startActivity(new Intent(MainActivity.this, TrainSearchActivity.class));
                    break;
                case "Hotel":
                    startActivity(new Intent(MainActivity.this, HotelSearchActivity.class));
                    break;
                case "My Trips":
                    startActivity(new Intent(MainActivity.this, MyTripsActivity.class));
                    break;
            }
        });
    }
}