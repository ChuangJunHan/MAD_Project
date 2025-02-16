package com.sp.mad_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class databaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chat_groups.db";
    private static final int DATABASE_VERSION = 9;

    // Table names
    private static final String TABLE_GROUPS = "groups";
    private static final String TABLE_GROUP_MEMBERS = "group_members";
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_DRAWINGS = "drawings";
    private static final String TABLE_COMMENTS = "comments"; // Added a table for comments
    private static final String TABLE_TODOS = "todos";
    private static final String TABLE_TASKS = "tasks";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_GANTT_CHARTS = "gantt_charts";

    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "username";
    private static final String COLUMN_USER_PASSWORD = "password";

    private static final String COLUMN_GROUP_ID = "id";
    private static final String COLUMN_GROUP_NAME = "name";
    private static final String COLUMN_GROUP_DESCRIPTION = "description";
    private static final String COLUMN_GROUP_KEY = "group_key";
    private static final String COLUMN_MAX_MEMBERS = "max_members";

    // Columns for tasks table
    private static final String COLUMN_TASK_ID = "id";
    private static final String COLUMN_TASK_GROUP_ID = "group_id";
    private static final String COLUMN_TASK_NAME = "name";
    private static final String COLUMN_TASK_DEADLINE = "deadline";
    private static final String COLUMN_TASK_PROGRESS = "progress";
    private static final String COLUMN_TASK_ASSIGNED_MEMBER = "assigned_member";

    // Columns for group members
    private static final String COLUMN_MEMBER_ID = "id";
    private static final String COLUMN_MEMBER_NAME = "member_name";
    private static final String COLUMN_GROUP_ID_FK = "group_id";
    private static final String COLUMN_IS_CREATOR = "is_creator";

    // Columns for messages
    private static final String COLUMN_MESSAGE_ID = "id";
    private static final String COLUMN_MESSAGE_SENDER = "sender";
    private static final String COLUMN_MESSAGE_CONTENT = "message";
    private static final String COLUMN_MESSAGE_TYPE = "type"; // "message" or "event"
    private static final String COLUMN_MESSAGE_DATE = "date";

    // Columns for drawings table
    private static final String COLUMN_DRAWING_ID = "id";
    private static final String COLUMN_DRAWING_GROUP_ID = "group_id";
    private static final String COLUMN_DRAWING_PATH = "path";

    // Columns for comments table
    private static final String COLUMN_COMMENT_ID = "id";
    private static final String COLUMN_COMMENT_TASK_ID = "task_id";
    private static final String COLUMN_COMMENT_TEXT = "text";

    // Columns for todos table
    private static final String COLUMN_TODO_ID = "id";
    private static final String COLUMN_TODO_TASK_ID = "task_id";
    private static final String COLUMN_TODO_TEXT = "todo_text";
    private static final String COLUMN_TODO_COMPLETED = "completed";

    private static final String COLUMN_GANTT_ID = "id"; // New column
    private static final String COLUMN_GANTT_GROUP_ID = "group_id"; // Reference to group ID
    private static final String COLUMN_GANTT_DATA = "gantt_data"; // Gantt chart data (e.g., JSON)


    public databaseHelper(Context context) {
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
        db.execSQL(CREATE_GROUPS_TABLE);

        // Create tasks table
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK_GROUP_ID + " INTEGER, " +
                COLUMN_TASK_NAME + " TEXT, " +
                COLUMN_TASK_DEADLINE + " TEXT, " +
                COLUMN_TASK_PROGRESS + " INTEGER, " +
                COLUMN_TASK_ASSIGNED_MEMBER + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_TASK_GROUP_ID + ") REFERENCES " + TABLE_GROUPS + "(" + COLUMN_GROUP_ID + "))";
        db.execSQL(CREATE_TASKS_TABLE);

        // Create group members table
        String CREATE_GROUP_MEMBERS_TABLE = "CREATE TABLE " + TABLE_GROUP_MEMBERS + " (" +
                COLUMN_MEMBER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MEMBER_NAME + " TEXT, " +
                COLUMN_GROUP_ID_FK + " INTEGER, " +
                COLUMN_IS_CREATOR + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COLUMN_GROUP_ID_FK + ") REFERENCES " + TABLE_GROUPS + "(" + COLUMN_GROUP_ID + "))";
        db.execSQL(CREATE_GROUP_MEMBERS_TABLE);

        // Create messages table
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + " (" +
                COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GROUP_ID_FK + " INTEGER, " +
                COLUMN_MESSAGE_SENDER + " TEXT, " +
                COLUMN_MESSAGE_CONTENT + " TEXT, " +
                COLUMN_MESSAGE_TYPE + " TEXT, " +
                COLUMN_MESSAGE_DATE + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_GROUP_ID_FK + ") REFERENCES " + TABLE_GROUPS + "(" + COLUMN_GROUP_ID + "))";
        db.execSQL(CREATE_MESSAGES_TABLE);

        // Create drawings table
        String CREATE_DRAWINGS_TABLE = "CREATE TABLE " + TABLE_DRAWINGS + " (" +
                COLUMN_DRAWING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DRAWING_GROUP_ID + " TEXT, " +
                COLUMN_DRAWING_PATH + " TEXT)";
        db.execSQL(CREATE_DRAWINGS_TABLE);

        // Create comments table
        String CREATE_COMMENTS_TABLE = "CREATE TABLE " + TABLE_COMMENTS + " (" +
                COLUMN_COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_COMMENT_TASK_ID + " INTEGER, " +
                COLUMN_COMMENT_TEXT + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_COMMENT_TASK_ID + ") REFERENCES " + TABLE_TASKS + "(" + COLUMN_TASK_ID + "))";
        db.execSQL(CREATE_COMMENTS_TABLE);

        // Create todos table
        String CREATE_TODOS_TABLE = "CREATE TABLE " + TABLE_TODOS + " (" +
                COLUMN_TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TODO_TASK_ID + " INTEGER, " +
                COLUMN_TODO_TEXT + " TEXT, " +
                COLUMN_TODO_COMPLETED + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COLUMN_TODO_TASK_ID + ") REFERENCES " + TABLE_TASKS + "(" + COLUMN_TASK_ID + "))";
        db.execSQL(CREATE_TODOS_TABLE);

        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_NAME + " TEXT UNIQUE, " +
                COLUMN_USER_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_GANTT_CHARTS_TABLE = "CREATE TABLE " + TABLE_GANTT_CHARTS + " (" +
                COLUMN_GANTT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GANTT_GROUP_ID + " INTEGER, " +
                COLUMN_GANTT_DATA + " TEXT, " + // Stores serialized Gantt chart data
                "FOREIGN KEY(" + COLUMN_GANTT_GROUP_ID + ") REFERENCES " + TABLE_GROUPS + "(" + COLUMN_GROUP_ID + "))";
        db.execSQL(CREATE_GANTT_CHARTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_MEMBERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRAWINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
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
    public boolean addMemberToGroup(long groupId, String memberName, boolean isCreator) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("group_id", groupId);
        values.put("member_name", memberName);
        values.put("is_creator", isCreator ? 1 : 0);

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

    // Add a message or event to a group
    public List<Message> getMessagesForGroupById(int groupId) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_MESSAGE_SENDER + ", " + COLUMN_MESSAGE_CONTENT + ", " +
                        COLUMN_MESSAGE_TYPE + ", date " + // Include date
                        "FROM " + TABLE_MESSAGES +
                        " WHERE " + COLUMN_GROUP_ID_FK + " = ?",
                new String[]{String.valueOf(groupId)});

        if (cursor.moveToFirst()) {
            do {
                String sender = cursor.getString(0);
                String content = cursor.getString(1);
                String type = cursor.getString(2);
                String date = cursor.getString(3); // Get the date

                messages.add(new Message(sender, content, type, date)); // Pass date to the Message constructor
            } while (cursor.moveToNext());
        }

        cursor.close();
        return messages;
    }

    // Retrieve messages for a group
    public void addMessageToGroupById(int groupId, String sender, String content, String type, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_ID_FK, groupId);
        values.put(COLUMN_MESSAGE_SENDER, sender);
        values.put(COLUMN_MESSAGE_CONTENT, content);
        values.put(COLUMN_MESSAGE_TYPE, type); // "message" or "event"
        values.put("date", date); // Add the date

        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }

    // Updated addDrawingToGroup
    public void addDrawingToGroup(int groupId, String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("group_id", groupId);
        values.put("path", path);
        db.insert("drawings", null, values);
        db.close();
    }

    // Updated getDrawingsForGroup
    public List<Drawing> getDrawingsForGroup(int groupId) {
        List<Drawing> drawings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM drawings WHERE group_id = ?", new String[]{String.valueOf(groupId)});

        if (cursor.moveToFirst()) {
            do {
                drawings.add(new Drawing(cursor.getInt(0), cursor.getInt(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return drawings;
    }

    public long addTaskToProject(int projectId, String taskName, String deadline, String memberName, int progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_GROUP_ID, projectId);
        values.put(COLUMN_TASK_NAME, taskName);
        values.put(COLUMN_TASK_DEADLINE, deadline);
        values.put(COLUMN_TASK_PROGRESS, progress); // Default progress
        values.put(COLUMN_TASK_ASSIGNED_MEMBER, memberName);
        return db.insert(TABLE_TASKS, null, values);
    }


    public long addCommentToTask(long taskId, String commentText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMMENT_TASK_ID, taskId);  // Linking the comment to the task
        values.put(COLUMN_COMMENT_TEXT, commentText);  // Storing the comment text
        return db.insert(TABLE_COMMENTS, null, values);  // Insert into the Comments table
    }

    public long addTodoToTask(long taskId, String todoText, boolean completed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TODO_TASK_ID, taskId);
        values.put(COLUMN_TODO_TEXT, todoText);
        values.put(COLUMN_TODO_COMPLETED, completed ? 1 : 0);
        return db.insert(TABLE_TODOS, null, values);
    }

    // Get task by ID
    public Task getTaskById(int taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE id = ?", new String[]{String.valueOf(taskId)});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline"));
            int progress = cursor.getInt(cursor.getColumnIndexOrThrow("progress"));
            String assignedMember = cursor.getString(cursor.getColumnIndexOrThrow("assigned_member"));
            int groupId = cursor.getInt(cursor.getColumnIndexOrThrow("group_id")); // Include groupId
            cursor.close();
            return new Task(id, name, deadline, progress, assignedMember, groupId); // Pass groupId
        }
        cursor.close();
        return null;
    }


    // Get todos by task ID
    public List<String> getTodosByTask(int taskId) {
        List<String> todos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT todo_text FROM todos WHERE task_id = ?", new String[]{String.valueOf(taskId)});
        while (cursor.moveToNext()) {
            todos.add(cursor.getString(cursor.getColumnIndexOrThrow("todo_text")));
        }
        cursor.close();
        return todos;
    }

    // Get comments by task ID
    public List<String> getCommentsByTask(int taskId) {
        List<String> comments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT text FROM comments WHERE task_id = ?", new String[]{String.valueOf(taskId)});
        while (cursor.moveToNext()) {
            comments.add(cursor.getString(cursor.getColumnIndexOrThrow("text")));
        }
        cursor.close();
        return comments;
    }

    public void deleteTodosByTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("todos", "task_id = ?", new String[]{String.valueOf(taskId)});
    }

    public void deleteCommentsByTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("comments", "task_id = ?", new String[]{String.valueOf(taskId)});
    }

    public boolean updateTask(int taskId, String taskName, String deadline, String memberName, int progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", taskName);
        values.put("deadline", deadline);
        values.put("progress", progress);
        values.put("assigned_member", memberName);

        int rows = db.update("tasks", values, "id = ?", new String[]{String.valueOf(taskId)});
        return rows > 0;
    }

    // Add a new user
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, username);
        values.put(COLUMN_USER_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1; // Return true if successful
    }

    // Authenticate user
    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USER_NAME + " = ? AND " + COLUMN_USER_PASSWORD + " = ?", new String[]{username, password});

        boolean isValid = cursor.moveToFirst(); // Check if any result exists
        cursor.close();
        db.close();
        return isValid;
    }

    // Fetch all users not in the specified group
    public List<String> getAllUsersNotInGroup(long groupId) {
        List<String> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT username FROM users WHERE username NOT IN (" +
                "SELECT member_name FROM group_members WHERE group_id = ?)";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(groupId)});

        if (cursor.moveToFirst()) {
            do {
                users.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return users;
    }

    public List<Task> getTasksByGroupId(int groupId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM tasks WHERE group_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(groupId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline"));
                int progress = cursor.getInt(cursor.getColumnIndexOrThrow("progress"));
                String assignedMember = cursor.getString(cursor.getColumnIndexOrThrow("assigned_member"));
                tasks.add(new Task(id, name, deadline, progress, assignedMember, groupId));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    // Fetch group name by group ID
    public String getGroupNameById(int groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM groups WHERE id = ?", new String[]{String.valueOf(groupId)});
        String groupName = null;
        if (cursor.moveToFirst()) {
            groupName = cursor.getString(0);
        }
        cursor.close();
        return groupName != null ? groupName : "Unknown Group";
    }

    // Fetch group description by group ID
    public String getGroupDescriptionById(int groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT description FROM groups WHERE id = ?", new String[]{String.valueOf(groupId)});
        String groupDescription = null;
        if (cursor.moveToFirst()) {
            groupDescription = cursor.getString(0);
        }
        cursor.close();
        return groupDescription != null ? groupDescription : "No description available.";
    }

    // Fetch group key by group ID
    public String getGroupKeyById(int groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT group_key FROM groups WHERE id = ?", new String[]{String.valueOf(groupId)});
        String groupKey = null;
        if (cursor.moveToFirst()) {
            groupKey = cursor.getString(0);
        }
        cursor.close();
        return groupKey != null ? groupKey : "No key available.";
    }

    // Fetch members by group ID
    public List<String> getMembersByGroupId(int groupId) {
        List<String> members = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT member_name FROM group_members WHERE group_id = ?",
                new String[]{String.valueOf(groupId)});
        if (cursor.moveToFirst()) {
            do {
                members.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return members;
    }

    public boolean saveGanttChartData(int groupId, String ganttData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GANTT_GROUP_ID, groupId);
        values.put(COLUMN_GANTT_DATA, ganttData);

        Cursor cursor = db.rawQuery("SELECT * FROM gantt_charts WHERE group_id = ?", new String[]{String.valueOf(groupId)});
        boolean chartExists = cursor.moveToFirst();
        cursor.close();

        long result;
        if (chartExists) {
            // Update existing Gantt chart
            result = db.update(TABLE_GANTT_CHARTS, values, "group_id = ?", new String[]{String.valueOf(groupId)});
        } else {
            // Insert new Gantt chart
            result = db.insert(TABLE_GANTT_CHARTS, null, values);
        }

        return result != -1;
    }

    public String getGanttChartData(int groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_GANTT_DATA + " FROM " + TABLE_GANTT_CHARTS +
                " WHERE " + COLUMN_GANTT_GROUP_ID + " = ?", new String[]{String.valueOf(groupId)});

        if (cursor.moveToFirst()) {
            String ganttData = cursor.getString(0);
            cursor.close();
            return ganttData;
        }

        cursor.close();
        return null; // No data found
    }

    // Fetch all tasks assigned to the current user
    public List<Task> getTasksForUser(String username) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM tasks WHERE assigned_member = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline"));
                int progress = cursor.getInt(cursor.getColumnIndexOrThrow("progress"));
                int groupId = cursor.getInt(cursor.getColumnIndexOrThrow("group_id"));

                tasks.add(new Task(id, name, deadline, progress, username, groupId));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return tasks;
    }


    public List<Message> getEventsForUserGroups(String username) {
        List<Message> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get group IDs the user is part of
        String groupQuery = "SELECT group_id FROM group_members WHERE member_name = ?";
        Cursor groupCursor = db.rawQuery(groupQuery, new String[]{username});

        List<String> groupIds = new ArrayList<>();
        while (groupCursor.moveToNext()) {
            groupIds.add(groupCursor.getString(0));
        }
        groupCursor.close();

        if (groupIds.isEmpty()) {
            return events; // No groups, no events
        }

        // Query to get events for those groups
        String placeholders = new String(new char[groupIds.size() - 1]).replace("\0", "?,") + "?";
        String eventQuery = "SELECT sender, message, type, date FROM messages WHERE type = 'event' AND group_id IN (" + placeholders + ")";
        Cursor eventCursor = db.rawQuery(eventQuery, groupIds.toArray(new String[0]));

        if (eventCursor.moveToFirst()) {
            do {
                String sender = eventCursor.getString(0);
                String content = eventCursor.getString(1);
                String type = eventCursor.getString(2);
                String date = eventCursor.getString(3); // Get the date

                events.add(new Message(sender, content, type, date)); // Pass date to the Message constructor
            } while (eventCursor.moveToNext());
        }

        eventCursor.close();
        return events;
    }

    public List<Group> getGroupsWithEventCounts(String username) {
        List<Group> groups = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT g.id, g.name, " +
                "(SELECT COUNT(*) FROM messages m WHERE m.group_id = g.id) AS event_count " +
                "FROM groups g " +
                "JOIN group_members gm ON g.id = gm.group_id " +
                "WHERE gm.member_name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int eventCount = cursor.getInt(2);

                groups.add(new Group(id, name, eventCount, 0)); // No task count for chat groups
            } while (cursor.moveToNext());
        }

        cursor.close();
        return groups;
    }

    public List<Group> getGroupsWithTaskCounts(String username) {
        List<Group> groups = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT g.id, g.name, " +
                "(SELECT COUNT(*) FROM tasks t WHERE t.group_id = g.id) AS task_count " +
                "FROM groups g " +
                "JOIN group_members gm ON g.id = gm.group_id " +
                "WHERE gm.member_name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int taskCount = cursor.getInt(2);

                groups.add(new Group(id, name, 0, taskCount)); // No event count for task groups
            } while (cursor.moveToNext());
        }

        cursor.close();
        return groups;
    }

    public boolean isGroupCreator(int groupId, String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM group_members WHERE group_id = ? AND member_name = ? AND is_creator = 1",
                new String[]{String.valueOf(groupId), username}
        );
        boolean isCreator = cursor.moveToFirst();
        cursor.close();
        return isCreator;
    }

    public List<Group> getGroupsWithGanttCharts(String username) {
        List<Group> groups = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT g.id, g.name, " +
                "(SELECT COUNT(*) FROM gantt_charts gc WHERE gc.group_id = g.id) AS gantt_count " +
                "FROM groups g " +
                "JOIN group_members gm ON g.id = gm.group_id " +
                "WHERE gm.member_name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int ganttCount = cursor.getInt(2);

                groups.add(new Group(id, name, 0, ganttCount)); // Add ganttCount as taskCount
            } while (cursor.moveToNext());
        }

        cursor.close();
        return groups;
    }
}
