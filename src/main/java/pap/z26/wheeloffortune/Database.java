package pap.z26.wheeloffortune;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class Database {

    private static volatile Database instance;
    private Statement statement;

    private Database() {
        this.statement = establishConnection();
        this.createTables();
        this.insertCategories();
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

    private Statement establishConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Missing SQLite JDBC driver");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:wofDatabase.db");
            return connection.createStatement();
        } catch (SQLException e) {
            System.err.println("Couldn't connect to wofDatabase.db");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private void createTables(){
        DatabaseCommand.callVoidCommand(new DatabaseCommand.CreateTables(), this.statement);
    }
    private void insertPhrases() {
        DatabaseCommand.callVoidCommand(new DatabaseCommand.InsertPhrases(), this.statement);
    }

    private void insertCategories(){
        DatabaseCommand.callVoidCommand(new DatabaseCommand.insertCategories(), this.statement);
    }

    public Phrase getRandomPhrase(String category) {
        ArrayList<String> allPhrases = (ArrayList<String>) DatabaseCommand.callObjectCommand(new DatabaseCommand.getAllPhrases(), statement);
        ArrayList<Phrase> phraseNames = new ArrayList<>();
        for (String phrase : allPhrases) {
            String[] phraseList = phrase.split("\n");
            if (category == null || phraseList[1].equals(category)) {
                phraseNames.add(new Phrase(phraseList[0], phraseList[1]));
            }
        }
        Random randomizer = new Random();
        return phraseNames.get(randomizer.nextInt(phraseNames.size()));
    }

    public ArrayList<String> getCategoriesList() {
        return (ArrayList<String>) DatabaseCommand.callObjectCommand(new DatabaseCommand.getAllCategories(), statement);
    }

    public boolean saveGameResult(String playerName, int score) {
        try {
            String sql_select = String.format("SELECT ID from Player WHERE Name LIKE '%s'", playerName);
            String sql_insert = String.format("INSERT INTO Record (ID, Points, Player_ID) VALUES (NULL, %d, (%s))", score, sql_select);
            statement.executeQuery(sql_insert);
        } catch (Exception ignored) {return false;}
        return true;
    }

    public ArrayList<String> getMatchingPhrases(String toMatch) {

        ArrayList<String> allPhrases = (ArrayList<String>) DatabaseCommand.callObjectCommand(new DatabaseCommand.getAllPhrases(), statement);
        ArrayList<String> phraseNames = new ArrayList<>();
        for (String phrase : allPhrases) {
            phraseNames.add(phrase.split("\n")[0]);
        }
        ArrayList<String> matchingPhrases = new ArrayList<>();
        for (String phrase : phraseNames) {
            boolean isMatching = true;
            if (phrase.length() == toMatch.length()) {
                for (int i = 0; i < phrase.length(); i++) {
                    if ((toMatch.charAt(i) != '_' && (toMatch.charAt(i) != phrase.charAt(i)))) {
                        isMatching = false;
                        break;
                    }
                }
                if (isMatching) {
                    matchingPhrases.add(phrase);
                }
            }
        }
        return matchingPhrases;
    }

    public ArrayList<LeaderboardRecord> getHighScores(int count) {
        ArrayList<LeaderboardRecord> leaderboard = new ArrayList<>();
        try {
            ResultSet results = statement.executeQuery(String.format("""
                    SELECT Name, Points from Record
                    join Player P on P.ID = Record.Player_ID
                    order by Points desc
                    limit %d;""", count));
            int i = 1;
            while (results.next()) {
                LeaderboardRecord record = new LeaderboardRecord(i, results.getString("Name"), results.getInt("Points"));
                leaderboard.add(record);
                i++;
            }
        } catch (SQLException ignored) {}
        return leaderboard;
    }

    public boolean updateDatabase(String serverIpAddress, int serverPort) {
        try{
            Socket socket = new Socket(serverIpAddress, serverPort);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

        } catch (IOException ignored) {}

        return false;
    }

    public static void main(String[] args){
        Database db = Database.getInstance();
        System.out.println(db.getRandomPhrase("Filmy"));
        System.out.println(db.getRandomPhrase("Filmy"));
        //db.saveGameResult("Adam", 10);
        ArrayList<LeaderboardRecord> leaderboard = new ArrayList<>();
        leaderboard = db.getHighScores(6);
        for (LeaderboardRecord record : leaderboard){
            System.out.println(record);
        }
    }
}
