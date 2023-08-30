package net.buildtheearth.buildteam.database;

import net.buildtheearth.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Represents a user in the database
 */
public class User
{
    public boolean bNavigatorEnabled;

    public User(boolean bNavigatorEnabled)
    {
        this.bNavigatorEnabled = bNavigatorEnabled;
    }

    public static User fetchUser(UUID uuid)
    {
//        User user = null;
//
//        Connection connection = Main.buildTeamTools.getDBConnection().connection;
//
//        String sql;
//        Statement SQL = null;
//        ResultSet resultSet = null;
//
//        try
//        {
//            //Compiles the command to fetch steps
//            sql = "Select * FROM Users WHERE UUID = "+uuid.toString();
//            SQL = connection.createStatement();
//
//            //Executes the query
//            resultSet = SQL.executeQuery(sql);
//            while (resultSet.next())
//            {
//                user = new User(resultSet.getBoolean("NavigatorEnabled"));
//            }
//        }
//        catch(SQLException se)
//        {
//            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildTeamTools] - SQL - SQL Error fetching User by UUID");
//            se.printStackTrace();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        return user;

        User user = new User(true);
        return user;
    }
}
