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

public class FlightSearchActivity extends AppCompatActivity {

    private EditText fromCity, toCity, departureDate, returnDate;
    private Button searchFlightsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_search);

        fromCity = findViewById(R.id.editTextFromCity);
        toCity = findViewById(R.id.editTextToCity);
        departureDate = findViewById(R.id.editTextDepartureDate);
        returnDate = findViewById(R.id.editTextReturnDate);
        searchFlightsButton = findViewById(R.id.buttonSearchFlights);

        // Open date picker on click
        departureDate.setOnClickListener(view -> showDatePicker(departureDate));
        returnDate.setOnClickListener(view -> showDatePicker(returnDate));

        // Search button action
        searchFlightsButton.setOnClickListener(view -> {
            String from = fromCity.getText().toString();
            String to = toCity.getText().toString();
            String depDate = departureDate.getText().toString();
            String retDate = returnDate.getText().toString();

            Intent intent = new Intent(FlightSearchActivity.this, FlightResultsActivity.class);
            intent.putExtra("from", from);
            intent.putExtra("to", to);
            intent.putExtra("departureDate", depDate);
            intent.putExtra("returnDate", retDate);
            startActivity(intent);
        });
    }

    // Method to show a date picker
    private void showDatePicker(EditText dateField) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) ->
                        dateField.setText(selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay),
                year, month, day);
        datePickerDialog.show();
    }
}