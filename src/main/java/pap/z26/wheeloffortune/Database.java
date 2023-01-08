package pap.z26.wheeloffortune;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

public class Database {

    private static volatile Database instance;

    private Database() {
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

    private void insertPhrases() {
        DatabaseCommand.callCommand(new DatabaseCommand.InsertPhrases(), establishConnection());
    }

    public Phrase getRandomPhrase(String category) {
        ArrayList<String> allPhrases = DatabaseCommand.callReturnArrayListCommand(new DatabaseCommand.getAllPhrases(), establishConnection());
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
        return null;
    }

    public boolean saveGameResult(String playerName, int score) {
        return false;
    }

    public ArrayList<String> getMatchingPhrases(String toMatch) {
        ArrayList<String> allPhrases = DatabaseCommand.callReturnArrayListCommand(new DatabaseCommand.getAllPhrases(), establishConnection());
        ArrayList<String> phraseNames = new ArrayList<>();
        for (String phrase : allPhrases) {
            phraseNames.add(phrase.split("\n")[0]);
        }
        ArrayList<String> matchingPhrases = new ArrayList<>();
        for (String phrase : phraseNames) {
            boolean isMatching = true;
            if (phrase.length() == toMatch.length()) {
                for (int i = 0; i < phrase.length(); i++) {
                    if ((toMatch.charAt(i) != '_' && !(toMatch.charAt(i) == phrase.charAt(i) - 32 ||  toMatch.charAt(i) == phrase.charAt(i)))) {
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
        return null;
    }

    public boolean updateDatabase() {
        return false; // to na później
    }
}
