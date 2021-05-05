package fr.ng.ibalix.nationsarcade;

import java.sql.*;

public class MySQL
{
	// ------------------------------------------------------------
	// Fields and constructor
	// ------------------------------------------------------------

	private transient final String url;
	private transient final String usr;
	private transient final String pwd;
	private Connection connection;

	public MySQL(String url, String usr, String pwd)
	{
		this.url = url;
		this.usr = usr;
		this.pwd = pwd;
		connect();
	}

	// ------------------------------------------------------------
	// Get a connection
	// ------------------------------------------------------------

	public void connect() {
		try {
			connection = DriverManager.getConnection(url, usr, pwd);
		} catch(SQLException e) {
			try {
				connection = DriverManager.getConnection(url, usr, pwd);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	public Connection getConnection() {
		try {
			if(connection == null || connection.isClosed()) {
				try {
					connection = DriverManager.getConnection(url, usr, pwd);
					return connection;
				} catch (SQLException e) {
					connection = DriverManager.getConnection(url, usr, pwd);
				}
			}
		} catch (SQLException e) {}

		return connection;
	}

	// ------------------------------------------------------------
	// Execute a query
	// ------------------------------------------------------------

	public boolean executeQuery(String query)
	{
		try
		{
			Connection connection = getConnection();
			connection.prepareStatement(query).executeUpdate();
			return true;
		}
		catch (SQLException exception) {
			try {
				connection.close();
				connection = DriverManager.getConnection(url, usr, pwd);
				connection.prepareStatement(query).executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	// ------------------------------------------------------------
	// Select
	// ------------------------------------------------------------

	public ResultSet get(String query)
	{
		try {
			Connection connection = getConnection();
			return connection.prepareStatement(query).executeQuery();
		}
		catch (SQLException exception) {
			try {
				connection.close();
				connection = DriverManager.getConnection(url, usr, pwd);
				return connection.prepareStatement(query).executeQuery();
			} catch (SQLException e) {}
			return null;
		}
	}

	// ------------------------------------------------------------
	// Select a specific object
	// ------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public <T> T getObject(String query, String objectId)
	{
		Object object = null;

		try
		{
			Connection connection = getConnection();
			ResultSet result = connection.prepareStatement(query).executeQuery();

			if (result.next()) {
				object = result.getObject(objectId);
			}

			result.close();
			connection.close();
		}
		catch (SQLException exception)
		{
			exception.printStackTrace();
		}

		return (T) object;
	}
}