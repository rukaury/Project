package main.taskmanager.javaActions;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/**
 * Created by Production on 10/13/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    // Database Name
    private static final String DB_NAME = "userInfo";

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_TASKS = "tasks";
    private static final String TABLE_RESOURCES = "resource";

    //Same colum names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    //Users Table columns
    private static final String KEY_TITLE = "title";
    private static final String KEY_PASSWORD = "pass";
    private static final String KEY_POINTS = "points";  // Same column name also in tasks table
    private static final String KEY_GROUP = "team";

    //Tasks Table columns
    private static final String KEY_USER_POST = "userpost";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TAG = "tag";
    private static final String KEY_RESOURCE = "resource";

    //Group Table columns
    private static final String KEY_ACTIVE_GROUP = "lastactivegroup";

    //Table create Statements
    private static final String CREATE_TABLE_GROUP = "CREATE TABLE " + TABLE_GROUPS + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT, " + KEY_ACTIVE_GROUP +
            " TEXT)";

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USERS + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT, " + KEY_GROUP + " TEXT, "
            + KEY_PASSWORD + " TEXT, " + KEY_POINTS + " TEXT, " + KEY_TITLE + " TEXT)";

    private static final String CREATE_TABLE_TASK = "CREATE TABLE " + TABLE_TASKS + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_USER_POST + " TEXT, " + KEY_POINTS + "" +
            " TEXT, "
            + KEY_TAG + " TEXT, " + KEY_DESCRIPTION + " TEXT, " + KEY_GROUP + " TEXT, " +
            KEY_RESOURCE + " TEXT)";

    private static final String CREATE_TABLE_RESOURCES = "CREATE TABLE " + TABLE_RESOURCES + "" +
            "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT, " + KEY_GROUP +
            " TEXT)";

    // Static strings to save data

    private static String activeGroup;
    private static ArrayList<User> activeUsers;
    private static ArrayList<Task> activeTasks;
    private static DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseHelper(context);
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_GROUP);
        database.execSQL(CREATE_TABLE_USER);
        database.execSQL(CREATE_TABLE_TASK);
        database.execSQL(CREATE_TABLE_RESOURCES);
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int OldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_RESOURCES);
        onCreate(database);
    }

    public void addGroup(Group group) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, group.getGroupName());
        values.put(KEY_ACTIVE_GROUP, "0");
        long insert = database.insert(TABLE_GROUPS, null, values);
    }


    public void addUser(User user) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getUsername());
        values.put(KEY_TITLE, user.getTitle());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_POINTS, Integer.toString(user.getPointAmount()));
        values.put(KEY_GROUP, user.getGroupName());
        database.insert(TABLE_USERS, null, values);
        if (activeUsers != null) activeUsers.add(user);
    }

    public void addTask(Task task, String groupName, String resource) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_POST, task.getUserPost());
        values.put(KEY_DESCRIPTION, task.getDescription());
        values.put(KEY_TAG, task.getTag());
        values.put(KEY_POINTS, Integer.toString(task.getPointAmount()));
        values.put(KEY_GROUP, groupName);
        values.put(KEY_RESOURCE, resource);
        database.insert(TABLE_TASKS, null, values);
        if (activeTasks != null) activeTasks.add(task);
    }

    public void addResource(Resource resource) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, resource.getName());
        values.put(KEY_GROUP, resource.getGroup());
        long insert = database.insert(TABLE_RESOURCES, null, values);
    }

    public ArrayList<User> getAllActiveUsers() {
        if (activeUsers == null) {
            activeUsers = new ArrayList<User>();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from users where " + KEY_GROUP + " = '" +
                    getActiveGroup() + "'", null);

            cursor.moveToFirst();

            while (cursor.isAfterLast() == false) {
                User user = new User(cursor.getString(cursor.getColumnIndex(KEY_NAME)), Integer
                        .parseInt(cursor
                                .getString(cursor.getColumnIndex(KEY_POINTS))), cursor
                        .getString(cursor.getColumnIndex(KEY_PASSWORD)), cursor
                        .getString(cursor.getColumnIndex(KEY_TITLE)), cursor
                        .getString(cursor.getColumnIndex(KEY_GROUP)));
                activeUsers.add(user);
                cursor.moveToNext();
            }
        }
        return activeUsers;
    }

    /*
        Before using this method, we need to discuss how we want to work with the static variables,
     */

    public ArrayList<Task> getAllActiveTasks() {
        if (activeTasks == null) {
            activeTasks = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from " + TABLE_TASKS + " where " + KEY_GROUP +
                    " = ?", new String[]{getActiveGroup()});

            cursor.moveToFirst();

            while (cursor.isAfterLast() == false) {
                Task task = new Task(cursor.getString(cursor.getColumnIndex(KEY_USER_POST)),
                        Integer.parseInt(cursor
                                .getString(cursor.getColumnIndex(KEY_POINTS))), cursor
                        .getString(cursor.getColumnIndex(KEY_TAG)), cursor
                        .getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                activeTasks.add(task);
                cursor.moveToNext();
            }
        }

        return activeTasks;
    }

    public ArrayList<String> getAllGroups() {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from groups", null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            String groupName = (cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            array_list.add(SimpleAction.capitalizeString(groupName));
            cursor.moveToNext();
        }

        return array_list;
    }

    public ArrayList<String> getAllResources() {
        ArrayList<String> resources = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_RESOURCES + " where " + KEY_GROUP +
                " = ?", new String[]{getActiveGroup()});

        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            String resource = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            resources.add(resource);
            cursor.moveToNext();
        }

        return resources;
    }

    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_NAME + "= ? AND " + KEY_GROUP + " = ?", new
                String[]{user.getUsername(), user.getGroupName()});
        db.delete(TABLE_TASKS, KEY_USER_POST + "= ? AND " + KEY_GROUP + " = ?", new
                String[]{user.getUsername(), user.getGroupName()});
        if (activeTasks != null && !activeTasks.isEmpty()) {
            SQLiteDatabase db1 = this.getReadableDatabase();
            Cursor cursor = db1.rawQuery("select * from " + TABLE_TASKS + " where " + KEY_GROUP +
                    " = '" + user.getGroupName() + "' AND " + KEY_USER_POST + " = '" + user
                            .getUsername() + "'",
                    null);

            cursor.moveToFirst();

            while (cursor.isAfterLast() == false) {
                Task task = new Task(cursor.getString(cursor.getColumnIndex(KEY_USER_POST)),
                        Integer.parseInt(cursor
                                .getString(cursor.getColumnIndex(KEY_POINTS))), cursor
                        .getString(cursor.getColumnIndex(KEY_TAG)), cursor
                        .getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                activeTasks.remove(task);
                cursor.moveToNext();
            }

        }
        activeUsers.remove(user);
    }

    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_GROUP + "= ? AND " + KEY_TAG + " = ?", new
                String[]{getActiveGroup(), task.getTag()});
        activeTasks.remove(task);
    }

    public Integer deleteGroup(Group group) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_GROUP + "= ? ", new String[]{group.getGroupName()});
        db.delete(TABLE_TASKS, KEY_GROUP + "= ? ", new String[]{group.getGroupName()});
        return db.delete(TABLE_GROUPS, KEY_NAME + "= ? ", new String[]{group.getGroupName()});
    }

    public Integer deleteResource(Resource resource) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_RESOURCES, KEY_NAME + "= ? AND " +
                KEY_GROUP + " = ? ", new String[]{resource.getName(), resource.getGroup()});
    }

    public String getActiveGroup() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_GROUPS + " where " + KEY_ACTIVE_GROUP +
                " = ?", new String[]{"1"});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            activeGroup = cursor.getString(cursor.getColumnIndex("name"));
        }
        return activeGroup;
    }

    public String getTaskResource(String tag) {
        String ret = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_TASKS + " where " + KEY_TAG +
                " = ?", new String[]{tag});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ret = cursor.getString(cursor.getColumnIndex(KEY_RESOURCE));
        }
        return ret;
    }

    public void setActiveGroup(String activeGroup) {
        DatabaseHelper.activeGroup = activeGroup;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ACTIVE_GROUP, "0");
        db.update(TABLE_GROUPS, contentValues, null, null);
        contentValues.put(KEY_ACTIVE_GROUP, "1");
        db.update(TABLE_GROUPS, contentValues, "name = ?", new String[]{activeGroup});
    }

    public void setActiveUsers(ArrayList<User> activeUsers) {
        DatabaseHelper.activeUsers = activeUsers;
    }

    public void setActiveTasks(ArrayList<Task> activeTasks) {
        DatabaseHelper.activeTasks = activeTasks;
    }

    public void closeConnection() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
    }

    public User getUser(String name) {
        for (User user : activeUsers) {
            if (user.getUsername().equals(name)) return user;
        }
        return null;
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_POINTS, user.getPointAmount());
        db.update(TABLE_USERS, contentValues,
                "name = ?", new String[]{user.getUsername()});
    }

    public void updateTask(Task task, String res) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TAG, task.getTag());
        contentValues.put(KEY_USER_POST, task.getUserPost());
        contentValues.put(KEY_POINTS, task.getPointAmount());
        contentValues.put(KEY_DESCRIPTION, task.getDescription());
        contentValues.put(KEY_RESOURCE, res);
        db.update(TABLE_TASKS, contentValues,
                "tag = ?", new String[]{task.getTag()});
    }

}
