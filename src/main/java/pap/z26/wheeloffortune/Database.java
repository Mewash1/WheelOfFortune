package pap.z26.wheeloffortune;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {

    private static volatile Database instance;

    private Connection connection;

    private Database(){
        DatabaseCommand.callCommand(new DatabaseCommand.CreateTables(), establishConnection());
        this.insertPhrases();
    }

    public static Database getInstance() {
        Database result = instance;
        if (result != null) {
            return result;
        }
        synchronized (Database.class) {
            if (instance == null) {
                instance = new Database();
            }
            return instance;
        }
    }

    private Statement establishConnection(){
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Missing SQLite JDBC driver");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:wofDatabase.db");
            return connection.createStatement();
        } catch (SQLException e) {
            System.err.println("Couldn't connect to wofDatabase.db");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
    private void insertPhrases() {
        DatabaseCommand.callCommand(new DatabaseCommand.InsertPhrases(), establishConnection());
    }
    public String getRandomPhrase(String category) {
        return null;
    }

    public ArrayList<String> getCategoriesList() {
        return null;
    }

    public boolean saveGameResult(String playerName, int score) {
        return false;
    }

    /*public ArrayList<LeaderboardRecord> getHighScores(int count) {
        return null;
    }*/

    public boolean updateDatabase() {
        return false; // to na później
    }
}
