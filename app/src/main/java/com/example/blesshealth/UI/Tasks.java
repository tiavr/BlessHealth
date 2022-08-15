package com.example.blesshealth.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blesshealth.NF.Patient;
import com.example.blesshealth.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The purpose of this class is to select the different tasks that are done for the patient and send it to the database.
 */
public class Tasks extends AppCompatActivity {
    Connection connection;
    private static final String url = "jdbc:mysql://10.37.2.153:3306/healthdb?useSSL=false";
    private static final String user = "root";
    private static final String pass = "phantom";

    private Button doneBtn;
    private Button nextBtn;
    private ListView listView;
    ArrayAdapter<String> adapter;
    private TextView text;
    String[] arrayTasks = {"Sheets", "Medication" , "Food", "Samples"};
    String id_patient;
    Patient patient;

    /**
     * This method initialize the activity and all its functionnalities.
     * Enables the connection to the database and its manipulation with listeners.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        //Retrieve the id from the Scan class
        Bundle b = getIntent().getExtras();
        id_patient = b.getString("id");


        listView = findViewById(R.id.listView_data);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, arrayTasks);
        listView.setAdapter(adapter);


        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }
        // Connection to database
        try {
            String query = "SELECT * FROM patient WHERE id=" + id_patient + ";";
            String queryTask = "SELECT * FROM tasks;";
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connecting database...");
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Connected");
            // Retrieve the database's information to display on the screen.
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    patient = new Patient(rs.getInt("id"), rs.getString("name"), rs.getString("fullname"), rs.getTimestamp("dateofbirth"));
                    text = findViewById(R.id.num_chambre);
                    text.setText(patient.toString() + rs.getDate("dateofbirth"));
                }
                rs.close();
            Statement stmt2 = connection.createStatement();
            ResultSet rsTask = stmt2.executeQuery(queryTask);
                while(rsTask.next()){
                    if(rsTask.getInt("Sheets") == 1){
                        listView.getChildAt(0).setBackgroundColor(Color.BLACK);
                        View v = listView.getChildAt(0);
                        TextView txtview = ((TextView)v.findViewById(android.R.id.text1));
                        txtview.setTextColor(Color.BLACK);
                    }
                }
                rsTask.close();
        }
        catch (SQLException | ClassNotFoundException se) {
            System.err.println(se);
            se.printStackTrace();
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i<listView.getChildCount(); i++){
                    if(listView.isItemChecked(i)){
                        listView.getChildAt(i).setBackgroundColor(Color.parseColor("#006d77"));
                        View v = listView.getChildAt(i);
                        TextView txtview = ((TextView)v.findViewById(android.R.id.text1));
                        txtview.setTextColor(Color.WHITE);

                    }
                    else{
                        listView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                        View v = listView.getChildAt(i);
                        TextView txtview = ((TextView)v.findViewById(android.R.id.text1));
                        txtview.setTextColor(Color.BLACK);
                    }
                }
            }
        });


        // Listener on the done button to send the information into the database.
        doneBtn = findViewById(R.id.done);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = doneBtn.getId();
                if(id == R.id.done){
                    String itemSelected = "Done";
                    // Retrieve the selected items.
                    for (int i =0; i<listView.getCount(); i++){
                        if (listView.isItemChecked(i)){
                            // SQL Script to execute
                            // This function sets to 1 the selected tasks in the database.
                            String queryDone =
                                    "UPDATE TASKS" +
                                            " SET " + listView.getItemAtPosition(i) + " = ? " +
                                            "WHERE id_patient = " + id_patient + ";";
                            //Database connection
                            try {
                                Class.forName("com.mysql.jdbc.Driver");
                                System.out.println("Connecting database...");
                                connection = DriverManager.getConnection(url, user, pass);
                                System.out.println("Connected");
                                // Executing the script
                                PreparedStatement pstmt = connection.prepareStatement(queryDone);
                                pstmt.setInt(1, 1);
                                pstmt.addBatch();
                                pstmt.executeBatch();

                            }
                            catch (SQLException | ClassNotFoundException se) {
                                System.err.println(se);
                                se.printStackTrace();
                            }
                        }

                    }
                    Toast.makeText(getApplicationContext(), itemSelected, Toast.LENGTH_SHORT).show();
                    // Uncheck the selected items
                    for (int i =0; i<listView.getCount(); i++){
                        listView.setItemChecked(i, false);
                        listView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                        View v = listView.getChildAt(i);
                        TextView txtview = ((TextView)v.findViewById(android.R.id.text1));
                        txtview.setTextColor(Color.BLACK);
                    }


            }
        }});

        nextBtn = findViewById(R.id.next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivityTasks();
            }
        });

    }

    /**
     * Redirects to the Scanner activity.
     */
    public void openActivityTasks(){

        Intent intent = new Intent(this, Scan.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
  }

    /**
     * This menu allows the user to reset the tasks to 0 (undone) for the patient in the database. Basically, the user will use this Refresh button when the day is finished.
     * @param item The item in the menu
     * @return It refreshes the data for the patient. It resets to 0 the value of the tasks in the database for the patient.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                String queryDone =
                        "UPDATE TASKS " +
                                "  SET Sheets = ?, Food = ?, Medication = ?, Samples = ? ;";
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    System.out.println("Connecting database...");
                    connection = DriverManager.getConnection(url, user, pass);
                    System.out.println("Connected");
                    PreparedStatement pstmt = connection.prepareStatement(queryDone);
                    pstmt.setInt(1, 0);
                    pstmt.setInt(2, 0);
                    pstmt.setInt(3, 0);
                    pstmt.setInt(4, 0);
                    pstmt.addBatch();
                    pstmt.executeBatch();

                } catch (SQLException | ClassNotFoundException se) {
                    System.err.println(se);
                    se.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Database refreshed ", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static void main(String[] args) {

    }
}