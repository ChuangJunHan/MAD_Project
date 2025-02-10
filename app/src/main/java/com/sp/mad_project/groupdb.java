package com.sp.mad_project;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
public class groupdb extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "ProjectManager.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_GROUPS = "groups";
    private static final String COLUMN_GROUP_ID = "id";
    private static final String COLUMN_GROUP_NAME = "name";
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_TASK_ID = "id";
    private static final String COLUMN_TASK_NAME = "task_name";
    private static final String COLUMN_GROUP_NAME_FK = "group_name"; // Foreign Key
    private static final String COLUMN_TASK_STATUS = "status"; // (To be done, In Progress, Complete)
    private static final String COLUMN_TASK_DATE = "due_date";
    public groupdb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Groups Table
        String CREATE_GROUPS_TABLE = "CREATE TABLE " + TABLE_GROUPS + " ("
                + COLUMN_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_GROUP_NAME + " TEXT UNIQUE);";
        db.execSQL(CREATE_GROUPS_TABLE);

        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + " ("
                + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TASK_NAME + " TEXT, "
                + COLUMN_GROUP_NAME_FK + " TEXT, "
                + COLUMN_TASK_STATUS + " TEXT, "
                + COLUMN_TASK_DATE + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_GROUP_NAME_FK + ") REFERENCES " + TABLE_GROUPS + "(" + COLUMN_GROUP_NAME + "));";
        db.execSQL(CREATE_TASKS_TABLE);


    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        onCreate(db);
    }
        public boolean insertGroup(String groupName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NAME, groupName);

        long result = db.insert(TABLE_GROUPS, null, values);
        db.close();
        return result != -1;
    }
    public boolean insertTask(String taskName, String groupName, String status, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_NAME, taskName);
        values.put(COLUMN_GROUP_NAME_FK, groupName);
        values.put(COLUMN_TASK_STATUS, status);
        values.put(COLUMN_TASK_DATE, dueDate);

        long result = db.insert(TABLE_TASKS, null, values);
        db.close();
        return result != -1;
    }
    public List<String[]> getTasksByGroup(String groupName) {
        List<String[]> tasksList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_GROUP_NAME_FK + " = ?", new String[]{groupName});
        if (cursor.moveToFirst()) {
            do {
                String[] task = new String[]{
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DATE))
                };
                tasksList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tasksList;
    }

    public List<String> getAllGroups() {
        List<String> groupsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_GROUP_NAME + " FROM " + TABLE_GROUPS, null);

        if (cursor.moveToFirst()) {
            do {
                groupsList.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return groupsList;
    }
}
