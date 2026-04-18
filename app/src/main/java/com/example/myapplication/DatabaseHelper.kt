package com.example.myapplication

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DatabaseHelper {
    private const val URL = "jdbc:mysql://10.0.2.2:3306/nihonGo"
    private const val USER = "root"
    private const val PASSWORD = ""

    init {
        try {
            Class.forName("com.mysql.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    fun getConnection(): Connection? {
        return try {
            DriverManager.getConnection(URL, USER, PASSWORD)
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    fun authenticateUser(username: String, password: String): Boolean {
        // Hardcoded default user
        if (username == "jay" && password == "pass") {
            return true
        }

        val connection = getConnection() ?: return false
        val query = "SELECT * FROM users WHERE username = ? AND password = ?"
        return try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, username)
            statement.setString(2, password)
            val resultSet = statement.executeQuery()
            val exists = resultSet.next()
            connection.close()
            exists
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun registerUser(username: String, password: String): Boolean {
        val connection = getConnection() ?: return false
        val query = "INSERT INTO users (username, password) VALUES (?, ?)"
        return try {
            val statement = connection.prepareStatement(query)
            statement.setString(1, username)
            statement.setString(2, password)
            val rowsInserted = statement.executeUpdate()
            connection.close()
            rowsInserted > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }
}
