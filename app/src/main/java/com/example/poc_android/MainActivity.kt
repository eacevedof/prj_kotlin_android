package com.example.poc_android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Replace with your actual layout

        dbHelper = DatabaseHelper(this)

        // Example CRUD operations
        createUserExample()
        readUserExample()
        updateUserExample()
        readAllUsersExample()
        deleteUserExample()

        dbHelper.close() // Close the database connection when done
    }

    private fun createUserExample() {
        val birthDate = Calendar.getInstance().apply {
            set(1990, Calendar.JANUARY, 15) // Year, Month (0-indexed), Day
        }.time
        val newUser = User(
            name = "John Doe",
            birthDate = birthDate,
            email = "john.doe@example.com",
            phone = "123-456-7890",
            username = "johndoe",
            accessPassword = "password123"
        )
        val id = dbHelper.createUser(newUser)
        Log.d("CRUD", "User created with ID: $id")
    }

    private fun readUserExample() {
        val user = dbHelper.getUser("your_user_uuid_here") // Replace with an actual UUID
        if (user != null) {
            Log.d("CRUD", "Retrieved user: ${user.name}, ${user.email}")
        } else {
            Log.d("CRUD", "User not found")
        }
    }

    private fun updateUserExample() {
        val birthDate = Calendar.getInstance().apply {
            set(1992, Calendar.MARCH, 20)
        }.time
        val updatedUser = User(
            uuid = "your_user_uuid_here", // Replace with the UUID of the user to update
            name = "Jane Doe",
            birthDate = birthDate,
            email = "jane.doe@example.com",
            phone = "987-654-3210",
            username = "janedoe",
            accessPassword = "newpassword"
        )
        val rowsAffected = dbHelper.updateUser(updatedUser)
        Log.d("CRUD", "Rows affected by update: $rowsAffected")
    }

    private fun readAllUsersExample() {
        val users = dbHelper.getAllUsers()
        Log.d("CRUD", "All users:")
        users.forEach { user ->
            Log.d("CRUD", "${user.name}, ${user.username}")
        }
    }

    private fun deleteUserExample() {
        val rowsAffected = dbHelper.deleteUser("your_user_uuid_here") // Replace with the UUID to delete
        Log.d("CRUD", "Rows affected by delete: $rowsAffected")
    }
}