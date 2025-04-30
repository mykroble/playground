package ph.edu.usc.sql_roble;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SqlAppActivity extends AppCompatActivity {
    EditText username, password, delete_name, currentname, newname;
    TextView display;
    Button addbtn, viewbtn, deletebtn, updatebtn;
    myDBAdapter helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql_app);

        addbtn = findViewById(R.id.addbtn);
        viewbtn = findViewById(R.id.view);
        deletebtn = findViewById(R.id.delete);
        updatebtn = findViewById(R.id.update);

        username = findViewById(R.id.name);
        password = findViewById(R.id.password);
        delete_name = findViewById(R.id.name_del);
        currentname = findViewById(R.id.current);
        newname = findViewById(R.id.newname);
        display = findViewById(R.id.display);
        helper = new myDBAdapter(this);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser(v);
            }
        });
        viewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewData(v);
            }
        });
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(v); viewData(v);
            }
        });
    }
    public void addUser(View view){
        String name = username.getText().toString();
        String pass = password.getText().toString();
        if (name.isEmpty() || pass.isEmpty()){
            Toast.makeText(getApplicationContext(),
                    "name and password must not be empty", Toast.LENGTH_SHORT).show();
        } else {
            long id = helper.insertData(name, pass);
            if (id <= 0){
                Toast.makeText(getApplicationContext(),
                        "Data was not saved properly", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Data was saved properly", Toast.LENGTH_SHORT).show();
                username.setText("");
                password.setText("");
            }
        }
    }
    public void viewData(View view){
        String data = helper.getData();
        display.setText(data);

    }

    public void updateData(View view){}
    public void deleteData(View view){
        String uname = delete_name.getText().toString();
        if (uname.isEmpty()){
            Toast.makeText(getApplicationContext(), "Enter Data!", Toast.LENGTH_LONG).show();
        } else{
            int uuname = helper.deleteData(uname);
            if (uuname <= 0){
                Toast.makeText(getApplicationContext(), "Unsuccessful", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Data has been deleted", Toast.LENGTH_LONG).show();
        }

        }
    }
}














