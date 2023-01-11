package pap.z26.wheeloffortune;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Console;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.*;

public class Database {

    private static volatile Database instance;
    private Statement statement;
    private Connection connection;

    private Database() {
        this.statement = establishConnection();
        try {
            this.connection = statement.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.createTables();
//        this.insertCategories();
//        this.insertPhrases();
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
//    private void insertPhrases() {
//        HashMap<String, Integer> categories = getCategoriesID();
//        ArrayList<Phrase> phrases = new ArrayList<>();
//        phrases.add(new Phrase("Cicha woda brzegi rwie", "Przysłowia"));
//        phrases.add(new Phrase("Baba z wozu koniom lżej", "Przysłowia"));
//        phrases.add(new Phrase("Co się odwlecze to się nie uciecze", "Przysłowia"));
//        phrases.add(new Phrase("Uderz w stół a nożyce się odezwą", "Przysłowia"));
//        phrases.add(new Phrase("Ciągnie swój do swego", "Przysłowia"));
//        phrases.add(new Phrase("Im dalej w las tym więcej drzew", "Przysłowia"));
//        phrases.add(new Phrase("Co dwie głowy to nie jedna", "Przysłowia"));
//        phrases.add(new Phrase("Dzieci i ryby głosu nie mają", "Przysłowia"));
//        phrases.add(new Phrase("Czas to pieniądz", "Przysłowia"));
//        phrases.add(new Phrase("Elektryka prąd nie tyka", "Przysłowia"));
//
//        phrases.add(new Phrase("Szklana pułapka", "Filmy"));
//        phrases.add(new Phrase("Przeminęło z wiatrem", "Filmy"));
//        phrases.add(new Phrase("Ojciec chrzestny", "Filmy"));
//        phrases.add(new Phrase("Kod da Vinci", "Filmy"));
//        phrases.add(new Phrase("Faceci w czerni", "Filmy"));
//        phrases.add(new Phrase("To nie jest kraj dla starych ludzi", "Filmy"));
//        phrases.add(new Phrase("Zielony rycerz", "Filmy"));
//        phrases.add(new Phrase("Raport mniejszosci", "Filmy"));
//        phrases.add(new Phrase("Pulp fiction", "Filmy"));
//        phrases.add(new Phrase("John Wick", "Filmy"));
//
//        phrases.add(new Phrase("Ania z Zielonego Wzgórza", "Książki"));
//        phrases.add(new Phrase("Sto lat samotności", "Książki"));
//        phrases.add(new Phrase("Pan Tadeusz", "Książki"));
//        phrases.add(new Phrase("Pan Wołodyjowski", "Książki"));
//        phrases.add(new Phrase("Ziemia obiecana", "Książki"));
//        phrases.add(new Phrase("I już nie było nikogo", "Książki"));
//        phrases.add(new Phrase("Zbrodnia i kara", "Książki"));
//        phrases.add(new Phrase("Cierpienia młodego Wertera", "Książki"));
//        phrases.add(new Phrase("Morderstwo w Orient Expressie", "Książki"));
//        phrases.add(new Phrase("Dom z liści", "Książki"));
//
//        phrases.add(new Phrase("Super Mario Bros", "Gry"));
//        phrases.add(new Phrase("The Legend of Zelda", "Gry"));
//        phrases.add(new Phrase("DOOM", "Gry"));
//        phrases.add(new Phrase("Geometry Dash", "Gry"));
//        phrases.add(new Phrase("Uncharted", "Gry"));
//        phrases.add(new Phrase("Deep Rock Galactic", "Gry"));
//        phrases.add(new Phrase("Fallout", "Gry"));
//        phrases.add(new Phrase("Bioshock", "Gry"));
//        phrases.add(new Phrase("Final Fantasy", "Gry"));
//        phrases.add(new Phrase("Tetris", "Gry"));
//
//        phrases.add(new Phrase("Warszawa", "Stolice"));
//        phrases.add(new Phrase("Kijów", "Stolice"));
//        phrases.add(new Phrase("Paryz", "Stolice"));
//        phrases.add(new Phrase("Berlin", "Stolice"));
//        phrases.add(new Phrase("Praga", "Stolice"));
//        phrases.add(new Phrase("Meksyk", "Stolice"));
//        phrases.add(new Phrase("Waszyngton", "Stolice"));
//        phrases.add(new Phrase("Mińsk", "Stolice"));
//        phrases.add(new Phrase("Rzym", "Stolice"));
//        phrases.add(new Phrase("Lizbona", "Stolice"));
//
//        for (Phrase phrase : phrases) {
//            try {
//                int category = categories.get(phrase.category());
//                String sql = String.format("""
//                                    INSERT OR REPLACE INTO Phrase (ID, Phrase, Category_ID)
//                                    SELECT NULL, '%s', %d
//                                    WHERE NOT EXISTS (SELECT * FROM Phrase WHERE Phrase = '%s' AND Category_ID = %d)""",
//                        phrase.phrase(), category, phrase.phrase(), category);
//                statement.execute(sql);
//            } catch (SQLException ignored) {
//            }
//        }
//    }
//
//
//    private void insertCategories(){
//        ArrayList<String> phrases = new ArrayList<>();
//
//        phrases.add("Przysłowia");
//        phrases.add("Książki");
//        phrases.add("Filmy");
//        phrases.add("Gry");
//        phrases.add("Stolice");
//
//        for (String phrase : phrases) {
//            try {
//                String sql = String.format("""
//                                    INSERT OR REPLACE INTO Category (ID, Name)
//                                    SELECT NULL, '%s'
//                                    WHERE NOT EXISTS (SELECT * FROM Category WHERE Name = '%s')""",
//                        phrase, phrase);
//                statement.execute(sql);
//            } catch (SQLException ignored) {}
//        }
//    }

    public ArrayList<Phrase> getAllPhrasesFromCategory(String category) {
        // if category == null, the method returns all phrases
        ArrayList<Phrase> phrases = new ArrayList<>();
        try {
            String sql = null;
            if (category != null)
                sql = String.format("SELECT Phrase, c2.name from Phrase JOIN Category C2 on C2.ID = Phrase.Category_ID WHERE C2.NAME = '%s'", category);
            else
                sql = "SELECT Phrase, c2.name from Phrase JOIN Category C2 on C2.ID = Phrase.Category_ID";
            ResultSet results = statement.executeQuery(sql);
            while (results.next()) {
                phrases.add(new Phrase(results.getString("Phrase"), results.getString("Name")));
            }
        } catch (SQLException ignored) {
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
        } catch (SQLException ignored) {
        }
        return categories;
    }

    public HashMap<String, Integer> getCategoriesID() {
        HashMap<String, Integer> categories = new HashMap<String, Integer>();
        ResultSet results = null;
        try {
            results = statement.executeQuery("SELECT * from Category");
            while (results.next()) {
                categories.put(results.getString("Name"), results.getInt("ID"));
            }
        } catch (SQLException ignored) {
        }
        return categories;
    }

    public Phrase getRandomPhrase(String category) {
        ArrayList<Phrase> phrases = this.getAllPhrasesFromCategory(category);
        Random randomizer = new Random();
        return phrases.get(randomizer.nextInt(phrases.size()));
    }

    private int getPlayerID(String playerName) {
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

    private boolean recordNotInDatabase(int playerID, int score) {
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

    public boolean saveGameResult(String playerName, int score, int gameID) {
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
                    if(recordID == -1) return false;
                    preparedStatement = connection.prepareStatement("UPDATE Game SET Record_ID = ? WHERE ID = ?");
                    preparedStatement.setInt(1, recordID);
                    preparedStatement.setInt(2, gameID);
                    return preparedStatement.executeUpdate() == 1;
                }
                return false;
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

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

    public ArrayList<LeaderboardRecord> getHighScores(Integer count) {
        ArrayList<LeaderboardRecord> leaderboard = new ArrayList<>();
        try {
            String sql;
            sql = """
                    SELECT Name, Points from Record
                    join Player P on P.ID = Record.Player_ID
                    order by Points desc""";
            if ((count != null)) {
                sql += String.format("\nlimit %d", count);
            }
            ResultSet results = statement.executeQuery(sql);
            int i = 1;
            while (results.next()) {
                LeaderboardRecord record = new LeaderboardRecord(i, results.getString("Name"), results.getInt("Points"));
                leaderboard.add(record);
                i++;
            }
        } catch (SQLException ignored) {
        }
        return leaderboard;
    }

    public boolean updateDatabase(JSONObject phrases, JSONObject records) throws SQLException {
        Iterator<String> keyPhrase = phrases.keys();
        while (keyPhrase.hasNext()) {
            String phrase = keyPhrase.next();
            String category = phrases.getString(phrase);

            // if there is a new category, add it

            String sql = String.format("""
                            INSERT OR REPLACE INTO Category (ID, Name)
                            SELECT NULL, '%s'
                            WHERE NOT EXISTS (SELECT * FROM Category WHERE Name = '%s')""",
                    category, category);
            statement.execute(sql);


            // add new phrases
            HashMap<String, Integer> categories = getCategoriesID();
            sql = String.format("""
                            INSERT OR REPLACE INTO Phrase (ID, Phrase, Category_ID)
                            SELECT NULL, '%s', %d
                            WHERE NOT EXISTS (SELECT * FROM Phrase WHERE Phrase = '%s' AND Category_ID = %d)""",
                    phrase, categories.get(category), phrase, categories.get(category));
            statement.execute(sql);
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
        return true;
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

    public synchronized int getGameID(String type) {
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