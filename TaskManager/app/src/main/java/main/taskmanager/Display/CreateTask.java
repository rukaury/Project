package main.taskmanager.Display;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import main.taskmanager.R;
import main.taskmanager.javaActions.DatabaseHelper;
import main.taskmanager.javaActions.Task;
import main.taskmanager.javaActions.User;

import static android.widget.ArrayAdapter.createFromResource;

public class CreateTask extends AppCompatActivity {

    private static Spinner from;
    private static TextInputEditText tag;
    private static TextInputEditText description;
    private static TextInputEditText amount;
    private static DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        database = DatabaseHelper.getInstance(getApplicationContext());

        ArrayList<User> users = database.getAllActiveUsers();
        String[] usersArray = new String[users.size()];

        for(int i = 0; i<users.size(); i++) {
            usersArray[i] = users.get(i).getUsername();
        }


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getApplicationContext() ,android.R.layout.simple_spinner_dropdown_item, usersArray);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        spinner.setAdapter(adapter);

        from = (Spinner) findViewById(R.id.spinner);
        tag = (TextInputEditText) findViewById(R.id.editTextTag);
        description = (TextInputEditText) findViewById(R.id.editTextDescription);
        amount = (TextInputEditText) findViewById(R.id.editTextAmount);
    }

    public void addTask(View view) {
        Task task = new Task(from.getSelectedItem().toString(), Integer.parseInt(amount.getText().toString()), tag.getText().toString(), description.getText().toString());
        database.addTask(task);
        Toast.makeText(this.getApplicationContext(), "Task added succesfully",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    public void onCancelTask(View view) {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
}