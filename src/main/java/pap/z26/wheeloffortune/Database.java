package pap.z26.wheeloffortune;

import org.json.JSONObject;

import java.sql.*;
import java.util.*;

public class Database {

    private static volatile Database instance;
    private final Statement statement;
    private Connection connection;

    private Database() {
        this.statement = establishConnection();
        try {
            this.connection = statement.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.createTables();
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

    private void createTables() {
        String sql;
        try {
            sql = """
                    CREATE TABLE IF NOT EXISTS Category
                    (
                        ID INTEGER PRIMARY KEY,
                        Name TEXT NOT NULL
                    )""";
            statement.execute(sql);
        } catch (SQLException ignored) {
        }

        try {
            sql = """
                    CREATE TABLE IF NOT EXISTS Phrase
                    (
                        ID INTEGER PRIMARY KEY,
                        Phrase TEXT NOT NULL,
                        Category_ID references Category(ID)
                    )""";
            statement.execute(sql);
        } catch (SQLException ignored) {
        }

        try {
            sql = """
                    CREATE TABLE IF NOT EXISTS Game
                    (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        Type TEXT,
                        Record_ID INTEGER references Record(ID)
                    )""";
            statement.execute(sql);
        } catch (SQLException ignored) {
        }
        try {
            sql = """
                    CREATE TABLE IF NOT EXISTS Player
                    (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        Name TEXT NOT NULL
                    )""";
            statement.execute(sql);
        } catch (SQLException ignored) {
        }
        try {
            sql = """
                    CREATE TABLE IF NOT EXISTS Record
                    (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        Points INTEGER NOT NULL,
                        Player_ID INTEGER references Player(ID)
                    )""";
            statement.execute(sql);
        } catch (SQLException ignored) {
        }
        try {
            sql = """
                    CREATE TABLE IF NOT EXISTS Move
                    (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        RollResult INTEGER,
                        GuessedLetter TEXT,
                        GuessedPhrase TEXT,
                        Result INTEGER,
                        Game_ID INTEGER references Game(ID),
                        Player_ID INTEGER references Player(ID)
                    )
                    """;
            statement.execute(sql);
        } catch (SQLException ignored) {
        }
        try {
            sql = """
                    CREATE TABLE IF NOT EXISTS Player_Games
                    (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        Player_ID INTEGER references Player(ID),
                        Game_ID INTEGER references Game(ID)
                    )""";
            statement.execute(sql);
        } catch (SQLException ignored) {
        }
        try {
            sql = """
                    CREATE TABLE IF NOT EXISTS Phrase_Games
                    (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        Phrase_ID INTEGER references Phrase(ID),
                        Game_ID INTEGER references Game(ID)
                    )""";
            statement.execute(sql);
        } catch (SQLException ignored) {
        }
        try {
            sql = """
                    CREATE TABLE IF NOT EXISTS Wheel
                    (
                        ID INTEGER PRIMARY KEY,
                        item1 INTEGER,
                        item2 INTEGER,
                        item3 INTEGER,
                        item4 INTEGER,
                        item5 INTEGER,
                        item6 INTEGER,
                        item7 INTEGER,
                        item8 INTEGER,
                        item9 INTEGER,
                        item10 INTEGER,
                        item11 INTEGER,
                        item12 INTEGER,
                        item13 INTEGER,
                        item14 INTEGER,
                        item15 INTEGER,
                        item16 INTEGER,
                        item17 INTEGER,
                        item18 INTEGER,
                        item19 INTEGER,
                        item20 INTEGER
                    )""";
            statement.execute(sql);
        } catch (SQLException ignored) {
        }
    }

    /**
     * Return all phrases from given {@link String category}.
     * If {@link String category} is null, return all phrases.
     */
    public ArrayList<Phrase> getAllPhrasesFromCategory(String category) {
        ArrayList<Phrase> phrases = new ArrayList<>();
        try {
            PreparedStatement preparedStatement;

            if (category != null) {
                preparedStatement = connection.prepareStatement("SELECT Phrase, c2.name from Phrase JOIN Category C2 on C2.ID = Phrase.Category_ID WHERE C2.NAME = ?");
                preparedStatement.setString(1, category);
            }
            else
                preparedStatement = connection.prepareStatement("SELECT Phrase, c2.name from Phrase JOIN Category C2 on C2.ID = Phrase.Category_ID");
            ResultSet results = preparedStatement.executeQuery();
            while (results.next()) {
                phrases.add(new Phrase(results.getString("Phrase"), results.getString("Name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return phrases;
    }

    public ArrayList<String> getAllCategories() {
        ArrayList<String> categories = new ArrayList<>();
        try {
            ResultSet results = statement.executeQuery("SELECT Name from Category");
            while (results.next()) {
                categories.add(results.getString("Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return categories;
    }

    public HashMap<String, Integer> getCategoriesID() {
        HashMap<String, Integer> categories = new HashMap<>();
        ResultSet results;
        try {
            results = statement.executeQuery("SELECT * from Category");
            while (results.next()) {
                categories.put(results.getString("Name"), results.getInt("ID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return categories;
    }

    public Phrase getRandomPhrase(String category) {
        ArrayList<Phrase> phrases = this.getAllPhrasesFromCategory(category);
        Random randomizer = new Random();
        return phrases.get(randomizer.nextInt(phrases.size()));
    }

    /**
     * Returns id of given player. If the player doesn't exist, it gets added to database
     * with a new id.
     */
    public int getPlayerID(String playerName) {
        if (playerName.equals("SYSTEM")) return -1;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID from Player WHERE Name = ?");
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            int id = -1;
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            if (id == -1) {
                preparedStatement = connection.prepareStatement("INSERT INTO Player(Name) VALUES(?)");
                preparedStatement.setString(1, playerName);
                preparedStatement.executeUpdate();
                ResultSet ids = preparedStatement.getGeneratedKeys();
                if (ids.next()) {
                    id = ids.getInt(1);
                }
            }
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Checks whether the record of given playerID and score exists in database.
     */
    public boolean recordNotInDatabase(int playerID, int score) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Record WHERE Player_ID = ? AND Points = ?");
            preparedStatement.setInt(1, playerID);
            preparedStatement.setInt(2, score);
            ResultSet resultSet = preparedStatement.executeQuery();
            return !resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Save a new score to the Records table and update the Game table accordingly.
     */
    public void saveGameResult(String playerName, int score, int gameID) {
        try {
            int playerID = getPlayerID(playerName);
            if (recordNotInDatabase(playerID, score)) {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Record(Points, Player_ID) VALUES(?, ?)");
                preparedStatement.setInt(1, score);
                preparedStatement.setInt(2, playerID);
                if(preparedStatement.executeUpdate() == 1) {
                    int recordID = -1;
                    ResultSet keys = preparedStatement.getGeneratedKeys();
                    if(keys.next()) recordID = keys.getInt(1);
                    if(recordID == -1) return;
                    preparedStatement = connection.prepareStatement("UPDATE Game SET Record_ID = ? WHERE ID = ?");
                    preparedStatement.setInt(1, recordID);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Returns all phrases that match the given string.
     * For example, if the string is a_b_c, the method might return aAbBc, or aabbc.
     */
    public ArrayList<String> getMatchingPhrases(String toMatch) {
        ArrayList<Phrase> allPhrases = getAllPhrasesFromCategory(null);
        ArrayList<String> phraseNames = new ArrayList<>();
        for (Phrase phrase : allPhrases) {
            phraseNames.add(phrase.phrase());
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

    /**
     * Get the top {@link Integer count} records from the leaderboard.
     * If {@link Integer count} is null, get all records.
     */
    public ArrayList<LeaderboardRecord> getHighScores(Integer count) {
        ArrayList<LeaderboardRecord> leaderboard = new ArrayList<>();
        try {
            PreparedStatement preparedStatement;
            if (count != null) {
                preparedStatement = connection.prepareStatement("""
                    SELECT Name, Points from Record
                    join Player P on P.ID = Record.Player_ID
                    order by Points desc
                    limit ?""");
                preparedStatement.setInt(1, count);
            }
            else {
                preparedStatement = connection.prepareStatement("""
                    SELECT Name, Points from Record
                    join Player P on P.ID = Record.Player_ID
                    order by Points desc
                    """);
            }
            ResultSet results = preparedStatement.executeQuery();
            int i = 1;
            while (results.next()) {
                LeaderboardRecord record = new LeaderboardRecord(i, results.getString("Name"), results.getInt("Points"));
                leaderboard.add(record);
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return leaderboard;
    }

    /**
     * Updates the local database with new phrases and new high scores. It also adds new categories and new players
     * if it's necessary.
     */
    public void updateDatabase(JSONObject phrases, JSONObject records) throws SQLException {
        Iterator<String> keyPhrase = phrases.keys();
        while (keyPhrase.hasNext()) {
            String phrase = keyPhrase.next();
            String category = phrases.getString(phrase);

            // if there is a new category, add it
            PreparedStatement preparedStatement = connection.prepareStatement("""
                            INSERT OR REPLACE INTO Category (ID, Name)
                            SELECT NULL, ?
                            WHERE NOT EXISTS (SELECT * FROM Category WHERE Name = ?)""");
            preparedStatement.setString(1, category);
            preparedStatement.setString(2, category);
            preparedStatement.executeQuery();

            // add new phrases
            HashMap<String, Integer> categories = getCategoriesID();
            preparedStatement = connection.prepareStatement("""
                            INSERT OR REPLACE INTO Phrase (ID, Phrase, Category_ID)
                            SELECT NULL, ?, ?
                            WHERE NOT EXISTS (SELECT * FROM Phrase WHERE Phrase = ? AND Category_ID = ?)""");
            preparedStatement.setString(1, phrase);
            preparedStatement.setInt(2, categories.get(category));
            preparedStatement.setString(3, phrase);
            preparedStatement.setInt(4, categories.get(category));
            preparedStatement.executeQuery();
        }

        Iterator<String> keyRecord = records.keys();
        while (keyRecord.hasNext()) {
            String player = keyRecord.next();
            int score = records.getInt(player);
            int playerID = getPlayerID(player);

            if(recordNotInDatabase(playerID, score)) {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Record(Points, Player_ID) VALUES(?, ?)");
                preparedStatement.setInt(1, score);
                preparedStatement.setInt(2, playerID);
                preparedStatement.executeUpdate();
            }
        }
    }

    public void insertMove(int rollResult, String guessedLetter, String guessedPhrase, Integer result, Integer gameID, String playerName) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Move(RollResult, GuessedLetter, GuessedPhrase, Result, Game_ID, Player_ID) VALUES(?, ?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, rollResult);
            preparedStatement.setString(2, guessedLetter);
            preparedStatement.setString(3, guessedPhrase);
            preparedStatement.setInt(4, result == null ? -174 : result);
            preparedStatement.setInt(5, gameID);
            preparedStatement.setInt(6, getPlayerID(playerName));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public HashMap<GameState, ArrayList<Integer>> getWheelContents() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Wheel");
            ResultSet resultSet = preparedStatement.executeQuery();
            HashMap<GameState, ArrayList<Integer>> wheelContents = new HashMap<>();
            GameState[] states = GameState.values();
            int index = 0;
            while (resultSet.next()) {
                ArrayList<Integer> contents = new ArrayList<>();
                int partIndex = 2;
                while (partIndex <= 21 && resultSet.getInt(partIndex) != -174) {
                    contents.add(resultSet.getInt(partIndex));
                    partIndex++;
                }
                wheelContents.put(states[index], contents);
                index++;
            }
            return wheelContents;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add a new game to the database and return its ID.
     * @return -1 if the game couldn't be added to database, else - it's id
     */
    public synchronized int addNewGame(String type) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Game(Type) VALUES(?)");
            preparedStatement.setString(1, type);
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}