package kr.ac.yuhan.cs.admin.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TODO = "todo";
    private static final String COLUMN_ADMIN_ID = "adminId";
    private static final String COLUMN_DATE = "date";
    public static final String COLUMN_CONTENT = "content";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TODO + " (" +
                COLUMN_ADMIN_ID + " VARCHAR(20) PRIMARY KEY, " +
                COLUMN_DATE + " DATE, " +
                COLUMN_CONTENT + " VARCHAR(50)" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    public boolean addTodo(String adminId, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ADMIN_ID, adminId);
        contentValues.put(COLUMN_DATE, getDateTime());
        contentValues.put(COLUMN_CONTENT, content);

        Log.d(TAG, "addTodo: Adding " + content + " to " + TABLE_TODO);

        long result = db.insert(TABLE_TODO, null, contentValues);

        // If data is inserted incorrectly, it will return -1
        return result != -1;
    }

    public Cursor getTodosByDate(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {COLUMN_CONTENT};
        String selection = COLUMN_DATE + "=?";
        String[] selectionArgs = {date};
        return db.query(TABLE_TODO, columns, selection, selectionArgs, null, null, null);
    }

    public Cursor getTodosByAdminId(String adminId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TODO +
                " WHERE " + COLUMN_ADMIN_ID + " = '" + adminId + "'";
        return db.rawQuery(query, null);
    }

    public boolean updateTodo(String adminId, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CONTENT, content);

        Log.d(TAG, "updateTodo: Updating " + content + " for " + adminId);

        int result = db.update(TABLE_TODO, contentValues,
                COLUMN_ADMIN_ID + " = ?", new String[]{adminId});

        return result > 0;
    }

    public boolean deleteTodo(String adminId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TODO,
                COLUMN_ADMIN_ID + " = ?", new String[]{adminId});
        return result > 0;
    }

    // Helper method to get current date and time
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}

