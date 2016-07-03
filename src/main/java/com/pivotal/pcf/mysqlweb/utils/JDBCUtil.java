package com.pivotal.pcf.mysqlweb.utils;

/**
 * Created by pasapicella on 26/09/15.
 */
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtil
{
    public static void close
            (ResultSet resultSet, CallableStatement statement, Connection connection)
    {

        try
        {
            if (resultSet != null)
                close(resultSet);
            if (statement != null)
                close(statement);
            if (connection != null)
                close(connection);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void close
            (ResultSet resultSet, Statement statement, Connection connection)
    {
        try
        {
            if (resultSet != null)
                close(resultSet);
            if (statement != null)
                close(statement);
            if (connection != null)
                close(connection);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void close
            (ResultSet resultSet, PreparedStatement statement, Connection connection)
    {
        try
        {
            if (resultSet != null)
                close(resultSet);
            if (statement != null)
                close(statement);
            if (connection != null)
                close(connection);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void close(ResultSet resultSet)
    {
        try
        {
            if (resultSet != null)
                resultSet.close();
        }
        catch (SQLException ex)
        {
            while (ex != null)
            {
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("Message: " + ex.getMessage());
                System.out.println("Vendor: " + ex.getErrorCode());
                ex.printStackTrace();
                ex = ex.getNextException();
                System.out.println("");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void close(Statement statement)
    {
        try
        {
            if (statement != null)
                statement.close();
        }
        catch (SQLException ex)
        {
            while (ex != null)
            {
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("Message: " + ex.getMessage());
                System.out.println("Vendor: " + ex.getErrorCode());
                ex.printStackTrace();
                ex = ex.getNextException();
                System.out.println("");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void close(PreparedStatement statement)
    {
        try
        {
            if (statement != null)
                statement.close();
        }
        catch (SQLException ex)
        {
            while (ex != null)
            {
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("Message: " + ex.getMessage());
                System.out.println("Vendor: " + ex.getErrorCode());
                ex.printStackTrace();
                ex = ex.getNextException();
                System.out.println("");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void close(CallableStatement statement)
    {
        try
        {
            if (statement != null)
                statement.close();
        }
        catch (SQLException ex)
        {
            while (ex != null)
            {
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("Message: " + ex.getMessage());
                System.out.println("Vendor: " + ex.getErrorCode());
                ex.printStackTrace();
                ex = ex.getNextException();
                System.out.println("");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void close(Connection connection)
    {
        try
        {
            if (connection != null)
                connection.close();
        }
        catch (SQLException ex)
        {
            while (ex != null)
            {
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("Message: " + ex.getMessage());
                System.out.println("Vendor: " + ex.getErrorCode());
                ex.printStackTrace();
                ex = ex.getNextException();
                System.out.println("");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
