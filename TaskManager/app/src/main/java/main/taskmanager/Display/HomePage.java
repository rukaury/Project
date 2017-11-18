package main.taskmanager.Display;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import main.taskmanager.R;
import main.taskmanager.javaActions.DatabaseHelper;
import main.taskmanager.javaActions.MainDrawerListAdapter;
import main.taskmanager.javaActions.Task;
import main.taskmanager.javaActions.TaskListAdapter;
import main.taskmanager.javaActions.User;

// https://www.anintegratedworld.com/creating-a-simple-navigation-drawer-in-android/
// https://android.jlelse.eu/android-adding-badge-or-count-to-the-navigation-drawer-84c93af1f4d9

public class HomePage extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter mStringAdaptor;
    private MainDrawerListAdapter userListAdaptor;
    private String[] mStringOfPlanets;
    private ArrayList<User> userList;

    private String[] stringTaskList;
    private TaskListAdapter contentAdapter;
    private ListView contentList;

    private String groupName;
    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        setContentView(R.layout.activity_home_page);
        groupName = databaseHelper.getActiveGroup();
        getSupportActionBar().setTitle(groupName);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        contentList = (ListView) findViewById(R.id.TaskList);

        userList = databaseHelper.getAllActiveUsers();
        userListAdaptor = new MainDrawerListAdapter(this, R.layout.drawer_list_item,userList);

        // Set the adapter for the list view
        mDrawerList.setAdapter(userListAdaptor);
        View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.drawer_list_footer, null, false);
        mDrawerList.addFooterView(footerView);


        //ArrayList<Task> testTaskList = new ArrayList<>();
        //testTaskList.add(new Task("Jack",5000, "urgent", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec porttitor nisl quis eros luctus consectetur. Suspendisse placerat dolor ornare nibh consectetur, a consectetur nisl fringilla. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus semper metus mauris, sed suscipit enim ullamcorper a. "));
        //testTaskList.add(new Task("Jack",00, "urgent", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec porttitor nisl quis eros luctus consectetur. Suspendisse placerat dolor ornare nibh consectetur, a consectetur nisl fringilla. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus semper metus mauris, sed suscipit enim ullamcorper a. "));
        //testTaskList.add(new Task("Jack",10, "urgent", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec porttitor nisl quis eros luctus consectetur. Suspendisse placerat dolor ornare nibh consectetur, a consectetur nisl fringilla. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus semper metus mauris, sed suscipit enim ullamcorper a. "));
        //testTaskList.add(task);

        ArrayList<Task> taskList;
        taskList = databaseHelper.getAllActiveTasks();

        contentAdapter = new TaskListAdapter(this, taskList);

        contentList.setAdapter(contentAdapter);
    }

    public void addTask(View view) {
        Intent intent = new Intent(this, CreateTask.class);
        startActivity(intent);
    }


    public void addUser(View view) {
        Intent intent = new Intent(this,DisplayUser.class);
        intent.putExtra("previousActivity", "HomePage");
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        userList = databaseHelper.getAllActiveUsers();
        userListAdaptor = new MainDrawerListAdapter(this, R.layout.drawer_list_item,userList);

        // Set the adapter for the list view
        mDrawerList.setAdapter(userListAdaptor);
    }
}
