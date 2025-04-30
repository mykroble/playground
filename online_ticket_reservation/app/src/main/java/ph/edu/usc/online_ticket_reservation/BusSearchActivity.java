package ph.edu.usc.online_ticket_reservation;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class BusSearchActivity extends AppCompatActivity {

    private EditText departureCity, arrivalCity, travelDate;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_search);

        departureCity = findViewById(R.id.editTextDepartureCity);
        arrivalCity = findViewById(R.id.editTextArrivalCity);
        travelDate = findViewById(R.id.editTextTravelDate);
        searchButton = findViewById(R.id.buttonSearchBus);

        // Open date picker on click
        travelDate.setOnClickListener(view -> showDatePicker());

        // Search button action
        searchButton.setOnClickListener(view -> {
            String depCity = departureCity.getText().toString();
            String arrCity = arrivalCity.getText().toString();
            String date = travelDate.getText().toString();

            Intent intent = new Intent(BusSearchActivity.this, BusResultsActivity.class);
            intent.putExtra("departureCity", depCity);
            intent.putExtra("arrivalCity", arrCity);
            intent.putExtra("travelDate", date);
            startActivity(intent);
        });
    }

    // Method to show a date picker
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) ->
                        travelDate.setText(selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay),
                year, month, day);
        datePickerDialog.show();
    }
}