package ph.edu.usc.sql_roble;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SqlAppActivity extends AppCompatActivity {
    EditText username, password, delete_name, currentname, newname;
    ListView displayList;
    ArrayAdapter<String> adapter;
    ArrayList<String> userList;

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

        displayList = findViewById(R.id.display_list);
        userList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        displayList.setAdapter(adapter);

        helper = new myDBAdapter(this);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser(v);
            }
        });
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(v);
                viewData(v);
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
        userList.clear();
        Cursor cursor = helper.getAllData();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String pass = cursor.getString(2);
                userList.add(id + " - " + name + " - " + pass);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }


    public void updateData(View view){
        String name, updatename;
        name = currentname.getText().toString();
        updatename = newname.getText().toString();
        if (name.isEmpty() || updatename.isEmpty()){
            Toast.makeText(getApplicationContext(), "Enter All Needed Data!", Toast.LENGTH_LONG).show();
        } else {
            int success = helper.updateData(name, updatename);
            if (success <= 0) {
                Toast.makeText(getApplicationContext(), "Unsuccessful in updating", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Data has been updated successfully", Toast.LENGTH_LONG).show();
            }
        }

    }
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














