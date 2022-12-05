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

    public ArrayList<String> getMatchingPhrases(String toMatch){
        ArrayList<String> allPhrases = DatabaseCommand.callReturnArrayListCommand(new DatabaseCommand.GetAllPhrases(), establishConnection());
        ArrayList<String> matchingPhrases = new ArrayList<>();
        for (String phrase : allPhrases){
            boolean isMatching = true;
            if (phrase.length() == toMatch.length()){
                for (int i = 0; i < phrase.length(); i++){
                    if ((Character.compare(toMatch.charAt(i), '_') != 0 && (Character.compare(toMatch.charAt(i), phrase.charAt(i)) != 0))){
                        isMatching = false;
                        break;
                    }
                }
                if (isMatching){
                    matchingPhrases.add(phrase);
                }
            }
        }
        return matchingPhrases;
    }
    /*public ArrayList<LeaderboardRecord> getHighScores(int count) {
        return null;
    }*/

    public boolean updateDatabase() {
        return false; // to na później
    }

    public static void main(String[] args) {
        Database database = Database.getInstance();
        database.insertPhrases();
        System.out.println(database.getMatchingPhrases("Kod da _____"));
    }
}
