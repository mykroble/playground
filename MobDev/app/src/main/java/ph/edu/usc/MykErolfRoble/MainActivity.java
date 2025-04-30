package ph.edu.usc.MykErolfRoble;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView name, course, year, wham;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (TextView) findViewById(R.id.name);
        course = (TextView) findViewById(R.id.course);
        year = (TextView) findViewById(R.id.year);
        wham = (TextView) findViewById(R.id.wham);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change();
            }
        });

    }
    protected void change(){
        name.setText("Name: Myk Erolf D. Roble");
        course.setText("Course: BSCS");
        year.setText("Year: 2nd Year");
        wham.setText("Wham: Great Reader");
    }
}