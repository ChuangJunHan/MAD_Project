package com.sp.mad_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class chatDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chat_groups.db";
    private static final int DATABASE_VERSION = 2;

    // Table names
    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_GROUP_MEMBERS = "group_members";
    private static final String TABLE_MESSAGES = "messages";

    // Columns for groups table
    private static final String COLUMN_GROUP_ID = "id";
    private static final String COLUMN_GROUP_NAME = "name";
    private static final String COLUMN_GROUP_DESCRIPTION = "description";
    private static final String COLUMN_MAX_MEMBERS = "max_members";
    private static final String COLUMN_GROUP_KEY = "group_key";

    // Columns for group members table
    private static final String COLUMN_MEMBER_ID = "id";
    private static final String COLUMN_MEMBER_NAME = "member_name";
    private static final String COLUMN_GROUP_ID_FK = "group_id";

    // Columns for messages table
    private static final String COLUMN_MESSAGE_ID = "id";
    private static final String COLUMN_MESSAGE_GROUP_NAME = "group_name";
    private static final String COLUMN_MESSAGE_SENDER = "sender";
    private static final String COLUMN_MESSAGE_CONTENT = "message";
    private static final String COLUMN_MESSAGE_TYPE = "type"; // "message" or "event"
    private static final String TABLE_DRAWINGS = "drawings";
    private static final String COLUMN_DRAWING_ID = "id";
    private static final String COLUMN_DRAWING_GROUP_NAME = "group_name";
    private static final String COLUMN_DRAWING_PATH = "path";

    public chatDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create groups table
        String CREATE_GROUPS_TABLE = "CREATE TABLE " + TABLE_GROUPS + " (" +
                COLUMN_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GROUP_NAME + " TEXT, " +
                COLUMN_GROUP_DESCRIPTION + " TEXT, " +
                COLUMN_MAX_MEMBERS + " INTEGER, " +
                COLUMN_GROUP_KEY + " TEXT UNIQUE)";

        // Create group members table
        String CREATE_GROUP_MEMBERS_TABLE = "CREATE TABLE " + TABLE_GROUP_MEMBERS + " (" +
                COLUMN_MEMBER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MEMBER_NAME + " TEXT, " +
                COLUMN_GROUP_ID_FK + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_GROUP_ID_FK + ") REFERENCES " + TABLE_GROUPS + "(" + COLUMN_GROUP_ID + "))";

        // Create messages table
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + " (" +
                COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MESSAGE_GROUP_NAME + " TEXT, " +
                COLUMN_MESSAGE_SENDER + " TEXT, " +
                COLUMN_MESSAGE_CONTENT + " TEXT, " +
                COLUMN_MESSAGE_TYPE + " TEXT)"; // "message" or "event"

        String CREATE_DRAWINGS_TABLE = "CREATE TABLE " + TABLE_DRAWINGS + " ("
                + COLUMN_DRAWING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DRAWING_GROUP_NAME + " TEXT, "
                + COLUMN_DRAWING_PATH + " TEXT)";

        db.execSQL(CREATE_DRAWINGS_TABLE);
        db.execSQL(CREATE_GROUPS_TABLE);
        db.execSQL(CREATE_GROUP_MEMBERS_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_MESSAGES + " ADD COLUMN " + COLUMN_MESSAGE_TYPE + " TEXT DEFAULT 'message'");
        }
    }

    // Add a new group
    public long addGroup(String name, String description, int maxMembers) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NAME, name);
        values.put(COLUMN_GROUP_DESCRIPTION, description);
        values.put(COLUMN_MAX_MEMBERS, maxMembers);
        values.put(COLUMN_GROUP_KEY, java.util.UUID.randomUUID().toString().substring(0, 6));

        long groupId = db.insert(TABLE_GROUPS, null, values);
        db.close();
        return groupId;
    }

    // Get group ID by group key
    public long getGroupIdByKey(String groupKey) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM groups WHERE group_key = ?", new String[]{groupKey});

        if (cursor.moveToFirst()) {
            long groupId = cursor.getLong(0);
            cursor.close();
            return groupId;
        }

        cursor.close();
        return -1; // Return -1 if the group key is invalid
    }

    // Add a single member to a group
    // Add a single member to a group
    public boolean addMemberToGroup(long groupId, String memberName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("group_id", groupId);
        values.put("member_name", memberName);

        // Check if the user is already in the group
        Cursor cursor = db.rawQuery("SELECT * FROM group_members WHERE group_id = ? AND member_name = ?",
                new String[]{String.valueOf(groupId), memberName});
        boolean isAlreadyMember = cursor.moveToFirst();
        cursor.close();

        if (isAlreadyMember) {
            db.close();
            return false; // User is already a member
        }

        // Add the user to the group
        long result = db.insert("group_members", null, values);
        db.close();
        return result != -1; // Return true if the insert was successful
    }


    // Add members to a group
    public void addMembersToGroup(long groupId, List<String> members) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (String member : members) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_MEMBER_NAME, member);
            values.put(COLUMN_GROUP_ID_FK, groupId);
            db.insert(TABLE_GROUP_MEMBERS, null, values);
        }
        db.close();
    }

    // Retrieve groups for a specific user
    public List<String> getGroupsForUser(String userName) {
        List<String> groups = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_GROUP_NAME + " FROM " + TABLE_GROUPS + " g " +
                "JOIN " + TABLE_GROUP_MEMBERS + " gm ON g." + COLUMN_GROUP_ID + " = gm." + COLUMN_GROUP_ID_FK + " " +
                "WHERE gm." + COLUMN_MEMBER_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userName});

        if (cursor.moveToFirst()) {
            do {
                groups.add(cursor.getString(0)); // Add group name to the list
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return groups;
    }

    // Add a message or event to a group
    public void addMessageToGroup(String groupName, String sender, String content, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE_GROUP_NAME, groupName);
        values.put(COLUMN_MESSAGE_SENDER, sender);
        values.put(COLUMN_MESSAGE_CONTENT, content);
        values.put(COLUMN_MESSAGE_TYPE, type); // "message" or "event"
        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }

    // Retrieve messages for a group
    public List<Message> getMessagesForGroup(String groupName) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_MESSAGE_SENDER + ", " + COLUMN_MESSAGE_CONTENT + ", " + COLUMN_MESSAGE_TYPE +
                " FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_MESSAGE_GROUP_NAME + " = ?", new String[]{groupName});

        if (cursor.moveToFirst()) {
            do {
                String sender = cursor.getString(0);
                String message = cursor.getString(1);
                String type = cursor.getString(2);
                messages.add(new Message(sender, message, type));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return messages;
    }

    // Get group description by name
    public String getGroupDescription(String groupName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT description FROM groups WHERE name = ?", new String[]{groupName});

        if (cursor.moveToFirst()) {
            String description = cursor.getString(0);
            cursor.close();
            return description;
        }

        cursor.close();
        return "No description available.";
    }

    // Get members of a group by group name
    public List<String> getGroupMembers(String groupName) {
        List<String> members = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT member_name FROM group_members gm " +
                        "JOIN groups g ON gm.group_id = g.id " +
                        "WHERE g.name = ?", new String[]{groupName});

        if (cursor.moveToFirst()) {
            do {
                members.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return members;
    }

    public long getGroupIdByName(String groupName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM groups WHERE name = ?", new String[]{groupName});
        if (cursor.moveToFirst()) {
            long groupId = cursor.getLong(0);
            cursor.close();
            return groupId;
        }
        cursor.close();
        return -1;
    }

    // Get group key by group name
    public String getGroupKey(String groupName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT group_key FROM groups WHERE name = ?", new String[]{groupName});

        if (cursor.moveToFirst()) {
            String groupKey = cursor.getString(0);
            cursor.close();
            return groupKey;
        }

        cursor.close();
        return "No key available.";
    }

    // Get group name by group ID
    public String getGroupNameById(long groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM groups WHERE id = ?", new String[]{String.valueOf(groupId)});

        if (cursor.moveToFirst()) {
            String groupName = cursor.getString(0);
            cursor.close();
            return groupName;
        }

        cursor.close();
        return null; // Return null if the group ID is invalid
    }

    public void addDrawingToGroup(String groupName, String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DRAWING_GROUP_NAME, groupName);
        values.put(COLUMN_DRAWING_PATH, path);
        db.insert(TABLE_DRAWINGS, null, values);
        db.close();
    }

    public List<Drawing> getDrawingsForGroup(String groupName) {
        List<Drawing> drawings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DRAWINGS + " WHERE " + COLUMN_DRAWING_GROUP_NAME + " = ?", new String[]{groupName});
        if (cursor.moveToFirst()) {
            do {
                drawings.add(new Drawing(cursor.getInt(0), cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return drawings;
    }
}
