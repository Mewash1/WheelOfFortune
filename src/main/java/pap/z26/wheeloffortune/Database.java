package pap.z26.wheeloffortune;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class Database {

    private static volatile Database instance;

    private Connection connection;

    public Database() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Missing SQLite JDBC driver");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:wofDatabase.db");
            Statement stmt = connection.createStatement();

            try{
                String sql_Phrases = """
                    CREATE TABLE Phrases(
                        PhraseID INTEGER PRIMARY KEY,
                        Content TEXT NOT NULL,
                        Category TEXT NOT NULL
                    )""";
                stmt.execute(sql_Phrases);
            } catch (SQLException ignored) {}

            try{
                String sql_HighScores = """
                    CREATE TABLE HighScores(
                        PlayerID INTEGER PRIMARY KEY,
                        Score INTEGER NOT NULL,
                        AchievedOn DATE NOT NULL
                    )
                    """;
                stmt.execute(sql_HighScores);
            } catch (SQLException ignored) {}

            try {
                String sql_PhrasesToHighScores = """
                    CREATE TABLE PhrasesToHighScores(
                        PhsID INTEGER PRIMARY KEY,
                        PlayerID INTEGER NOT NULL,
                        PhraseID INTEGER NOT NULL,
                        CONSTRAINT fk_highscores FOREIGN KEY (PlayerID)
                            REFERENCES HighScores(PlayerID),
                        CONSTRAINT fk_phrases FOREIGN KEY (PhraseID)
                            REFERENCES Phrases(PhraseID)
                    )
                    """;
                stmt.execute(sql_PhrasesToHighScores);
            } catch (SQLException ignored) {}
        } catch (SQLException e) {
            System.err.println("Couldn't connect to wofDatabase.db");
            e.printStackTrace();
            System.exit(1);
        }
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

    public static void main(String[] args){
        Database database = new Database();
    }
}
