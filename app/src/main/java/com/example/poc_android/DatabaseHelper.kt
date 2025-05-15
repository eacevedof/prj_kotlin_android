package com.example.poc_android

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 1

        // Table and Column names
        private const val TABLE_USERS = "users"
        private const val COLUMN_UUID = "uuid"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_BIRTH_DATE = "birth_date"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_ACCESS_PASSWORD = "access_password"

        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE $TABLE_USERS ($COLUMN_UUID TEXT PRIMARY KEY, " +
                    "$COLUMN_NAME TEXT, " +
                    "$COLUMN_BIRTH_DATE TEXT, " +
                    "$COLUMN_EMAIL TEXT, " +
                    "$COLUMN_PHONE TEXT, " +
                    "$COLUMN_USERNAME TEXT UNIQUE, " +
                    "$COLUMN_ACCESS_PASSWORD TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun createUser(user: User): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_UUID, user.uuid)
            put(COLUMN_NAME, user.name)
            put(COLUMN_BIRTH_DATE, DATE_FORMAT.format(user.birthDate))
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_PHONE, user.phone)
            put(COLUMN_USERNAME, user.username)
            put(COLUMN_ACCESS_PASSWORD, user.accessPassword)
        }
        val newRowId = db.insert(TABLE_USERS, null, values)
        db.close()
        return newRowId
    }

    fun getUser(uuid: String): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null, // Todas las columnas
            "$COLUMN_UUID=?",
            arrayOf(uuid),
            null, null, null
        )
        if (cursor == null) {
            return null
        }

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = cursor.getUserFromCursor()
        }
        cursor?.close()
        db.close()
        return user
    }

    fun getAllUsers(): MutableList<User> {
        val userList = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS, null,
            null, null,
            null, null,
            null
        )

        if (cursor.count == 0) {
            return userList
        }

        if (cursor.moveToFirst()) {
            do {
                userList.add(cursor.getUserFromCursor())
            }
            while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return userList
    }

    fun updateUser(user: User): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, user.name)
            put(COLUMN_BIRTH_DATE, DATE_FORMAT.format(user.birthDate))
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_PHONE, user.phone)
            put(COLUMN_USERNAME, user.username)
            put(COLUMN_ACCESS_PASSWORD, user.accessPassword)
        }
        val rowsAffected = db.update(
            TABLE_USERS,
            values,
            "$COLUMN_UUID=?",
            arrayOf(user.uuid)
        )
        db.close()
        return rowsAffected
    }

    fun deleteUser(uuid: String): Int {
        val db = this.writableDatabase
        val rowsAffected = db.delete(
            TABLE_USERS,
            "$COLUMN_UUID=?",
            arrayOf(uuid)
        )
        db.close()
        return rowsAffected
    }

    private fun Cursor.getUserFromCursor(): User {
        val uuid = getString(getColumnIndexOrThrow(COLUMN_UUID))
        val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
        val birthDateString = getString(getColumnIndexOrThrow(COLUMN_BIRTH_DATE))
        val birthDate = DATE_FORMAT.parse(birthDateString) as Date
        val email = getString(getColumnIndexOrThrow(COLUMN_EMAIL))
        val phone = getString(getColumnIndexOrThrow(COLUMN_PHONE))
        val username = getString(getColumnIndexOrThrow(COLUMN_USERNAME))
        val accessPassword = getString(getColumnIndexOrThrow(COLUMN_ACCESS_PASSWORD))
        return User(uuid, name, birthDate, email, phone, username, accessPassword)
    }
}