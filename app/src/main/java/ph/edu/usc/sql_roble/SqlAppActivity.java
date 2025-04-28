package ph.edu.usc.sql_roble;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SqlAppActivity extends AppCompatActivity {
    EditText username, password, delete_name, currentname, newname;
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
        Toast.makeText(getApplicationContext(),
                data,
                Toast.LENGTH_SHORT).show();

    }

    public void updateData(View view){}
    public void deleteData(View view){}
}














