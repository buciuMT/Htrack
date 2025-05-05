package com.example.gym.database
import java.sql.Connection
import java.sql.DriverManager

object Database {
    private const val JDBC_URL = "jdbc:mariadb://localhost:3306/htrack"
    private const val DB_USER = "dev"
    private const val DB_PASSWORD = "PASSWORD"

    fun connect(): Connection {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)
    }

    /*fun findUserByUsername(username: String): User? {
        val conn = connect()
        val stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")
        stmt.setString(1, username)
        val rs = stmt.executeQuery()

        return if (rs.next()) {
            User(
                name = rs.getString("username"),

            )
        } else null
    }

    fun getAllUsers(): List<User> {
        val conn = connect()
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery("SELECT * FROM user")
        val users = mutableListOf<User>()

        while (rs.next()) {
            users.add(User(name = rs.getString("username")))
        }

        return users
    }*/
}
