package com.example.community.provider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SimpleConnectionPool {
    private String url;
    private String username;
    private String password;
    private List<Connection> connectionPool;
    private List<Connection> usedConnections = new ArrayList<>();
    private int initialPoolSize = 10;
    private int maxPoolSize = 50;

    public SimpleConnectionPool(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find the driver in the classpath!", e);
        }
        connectionPool = new ArrayList<>(maxPoolSize);
        for (int i = 0; i < initialPoolSize; i++) {
            connectionPool.add(createConnection());
        }
    }

    public Connection getConnection() {
        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < maxPoolSize) {
                connectionPool.add(createConnection());
            } else {
                throw new RuntimeException("Maximum pool size reached, no available connections!");
            }
        }
        Connection connection = connectionPool.remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    public boolean releaseConnection(Connection connection) {
        if (connection != null) {
            if (usedConnections.remove(connection)) {
                connectionPool.add(connection);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create connection!", e);
        }
    }

    public int getSize() {
        return connectionPool.size() + usedConnections.size();
    }

    public void shutdown() {
        usedConnections.addAll(connectionPool);
        connectionPool.clear();
        for (Connection c : usedConnections) {
            try {
                c.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to close connection!", e);
            }
        }
        usedConnections.clear();
    }
}