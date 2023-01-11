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

    private void createTables() {
        String sql;
        try {
            sql = """
                        CREATE TABLE Category
                        (
                            ID INTEGER PRIMARY KEY,
                            Name TEXT NOT NULL
                        )""";
            statement.execute(sql);
        } catch (SQLException ignored) {
        }

        try {
            sql = """
                        CREATE TABLE Phrase
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
                        CREATE TABLE Game
                        (
                            ID INTEGER PRIMARY KEY AUTOINCREMENT,
                            Record_ID INTEGER references Record(ID)
                        )""";
            statement.execute(sql);
        } catch (SQLException ignored) {
        }
        try {
            sql = """
                        CREATE TABLE Player
                        (
                            ID INTEGER PRIMARY KEY AUTOINCREMENT,
                            Name TEXT NOT NULL
                        )""";
            statement.execute(sql);
        } catch (SQLException ignored) {
        }
        try {
            sql = """
                        CREATE TABLE Record
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
                        CREATE TABLE Move
                        (
                            ID INTEGER PRIMARY KEY AUTOINCREMENT,
                            RollResult TEXT not null,
                            GuessedLetter CHAR,
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
                        CREATE TABLE Player_Games
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
                        CREATE TABLE Phrase_Games
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
                        CREATE TABLE Wheel
                        (
                            ID INTEGER PRIMARY KEY,
                            item1 TEXT,
                            item2 TEXT,
                            item3 TEXT,
                            item4 TEXT,
                            item5 TEXT,
                            item6 TEXT,
                            item7 TEXT,
                            item8 TEXT,
                            item9 TEXT,
                            item10 TEXT,
                            item11 TEXT,
                            item12 TEXT,
                            item13 TEXT,
                            item14 TEXT,
                            item15 TEXT,
                            item16 TEXT,
                            item17 TEXT,
                            item18 TEXT,
                            item19 TEXT,
                            item20 TEXT
                        )""";
            statement.execute(sql);
        } catch (SQLException ignored) {
        }
    }
    private void insertPhrases() {
        HashMap<String, Integer> categories = getCategoriesID();
        ArrayList<Phrase> phrases = new ArrayList<>();
        phrases.add(new Phrase("Cicha woda brzegi rwie", "Przysłowia"));
        phrases.add(new Phrase("Baba z wozu koniom lżej", "Przysłowia"));
        phrases.add(new Phrase("Co się odwlecze to się nie uciecze", "Przysłowia"));
        phrases.add(new Phrase("Uderz w stół a nożyce się odezwą", "Przysłowia"));
        phrases.add(new Phrase("Ciągnie swój do swego", "Przysłowia"));
        phrases.add(new Phrase("Im dalej w las tym więcej drzew", "Przysłowia"));
        phrases.add(new Phrase("Co dwie głowy to nie jedna", "Przysłowia"));
        phrases.add(new Phrase("Dzieci i ryby głosu nie mają", "Przysłowia"));
        phrases.add(new Phrase("Czas to pieniądz", "Przysłowia"));
        phrases.add(new Phrase("Elektryka prąd nie tyka", "Przysłowia"));

        phrases.add(new Phrase("Szklana pułapka", "Filmy"));
        phrases.add(new Phrase("Przeminęło z wiatrem", "Filmy"));
        phrases.add(new Phrase("Ojciec chrzestny", "Filmy"));
        phrases.add(new Phrase("Kod da Vinci", "Filmy"));
        phrases.add(new Phrase("Faceci w czerni", "Filmy"));
        phrases.add(new Phrase("To nie jest kraj dla starych ludzi", "Filmy"));
        phrases.add(new Phrase("Zielony rycerz", "Filmy"));
        phrases.add(new Phrase("Raport mniejszosci", "Filmy"));
        phrases.add(new Phrase("Pulp fiction", "Filmy"));
        phrases.add(new Phrase("John Wick", "Filmy"));

        phrases.add(new Phrase("Ania z Zielonego Wzgórza", "Książki"));
        phrases.add(new Phrase("Sto lat samotności", "Książki"));
        phrases.add(new Phrase("Pan Tadeusz", "Książki"));
        phrases.add(new Phrase("Pan Wołodyjowski", "Książki"));
        phrases.add(new Phrase("Ziemia obiecana", "Książki"));
        phrases.add(new Phrase("I już nie było nikogo", "Książki"));
        phrases.add(new Phrase("Zbrodnia i kara", "Książki"));
        phrases.add(new Phrase("Cierpienia młodego Wertera", "Książki"));
        phrases.add(new Phrase("Morderstwo w Orient Expressie", "Książki"));
        phrases.add(new Phrase("Dom z liści", "Książki"));

        phrases.add(new Phrase("Super Mario Bros", "Gry"));
        phrases.add(new Phrase("The Legend of Zelda", "Gry"));
        phrases.add(new Phrase("DOOM", "Gry"));
        phrases.add(new Phrase("Geometry Dash", "Gry"));
        phrases.add(new Phrase("Uncharted", "Gry"));
        phrases.add(new Phrase("Deep Rock Galactic", "Gry"));
        phrases.add(new Phrase("Fallout", "Gry"));
        phrases.add(new Phrase("Bioshock", "Gry"));
        phrases.add(new Phrase("Final Fantasy", "Gry"));
        phrases.add(new Phrase("Tetris", "Gry"));

        phrases.add(new Phrase("Warszawa", "Stolice"));
        phrases.add(new Phrase("Kijów", "Stolice"));
        phrases.add(new Phrase("Paryz", "Stolice"));
        phrases.add(new Phrase("Berlin", "Stolice"));
        phrases.add(new Phrase("Praga", "Stolice"));
        phrases.add(new Phrase("Meksyk", "Stolice"));
        phrases.add(new Phrase("Waszyngton", "Stolice"));
        phrases.add(new Phrase("Mińsk", "Stolice"));
        phrases.add(new Phrase("Rzym", "Stolice"));
        phrases.add(new Phrase("Lizbona", "Stolice"));

        for (Phrase phrase : phrases) {
            try {
                int category = categories.get(phrase.category());
                String sql = String.format("""
                                    INSERT OR REPLACE INTO Phrase (ID, Phrase, Category_ID)
                                    SELECT NULL, '%s', %d
                                    WHERE NOT EXISTS (SELECT * FROM Phrase WHERE Phrase = '%s' AND Category_ID = %d)""",
                        phrase.phrase(), category, phrase.phrase(), category);
                statement.execute(sql);
            } catch (SQLException ignored) {
            }
        }
    }


    private void insertCategories(){
        ArrayList<String> phrases = new ArrayList<>();

        phrases.add("Przysłowia");
        phrases.add("Książki");
        phrases.add("Filmy");
        phrases.add("Gry");
        phrases.add("Stolice");

        for (String phrase : phrases) {
            try {
                String sql = String.format("""
                                    INSERT OR REPLACE INTO Category (ID, Name)
                                    SELECT NULL, '%s'
                                    WHERE NOT EXISTS (SELECT * FROM Category WHERE Name = '%s')""",
                        phrase, phrase);
                statement.execute(sql);
            } catch (SQLException ignored) {}
        }
    }

    public ArrayList<Phrase> getAllPhrasesFromCategory(String category){
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

    public ArrayList<String> getAllCategories(){
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

    public HashMap<String, Integer> getCategoriesID(){
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
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID from Player WHERE Name = ?");
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            int id = -1;
            if(resultSet.next()) {
                id = resultSet.getInt(1);
            }
            if(id == -1) {
                preparedStatement = connection.prepareStatement("INSERT INTO Player(Name) VALUES(?)");
                preparedStatement.setString(1, playerName);
                preparedStatement.executeUpdate();
                ResultSet ids = preparedStatement.getGeneratedKeys();
                if(ids.next()) {
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
        try{
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

    public boolean saveGameResult(String playerName, int score) {
        try {
            int playerID = getPlayerID(playerName);
            if(recordNotInDatabase(playerID, score)) {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Record(Points, Player_ID) VALUES(?, ?)");
                preparedStatement.setInt(1, score);
                preparedStatement.setInt(2, playerID);
                return preparedStatement.executeUpdate() == 1;
            }
            return true;
        } catch (Exception ignored) {return false;}
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
                    char match = Character.toLowerCase(toMatch.charAt(i));
                    if ((match != '_' && match != phrase.charAt(i))) {
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
            if ((count != null)){
                sql += String.format("\nlimit %d", count);
            }
            ResultSet results = statement.executeQuery(sql);
            int i = 1;
            while (results.next()) {
                LeaderboardRecord record = new LeaderboardRecord(i, results.getString("Name"), results.getInt("Points"));
                leaderboard.add(record);
                i++;
            }
        } catch (SQLException ignored) {}
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

        statement.execute("DELETE FROM RECORD");

        Iterator<String> keyRecord = records.keys();
        while (keyRecord.hasNext()) {
            String player = keyRecord.next();
            Integer score = records.getInt(player);
            // if there is a new player - add him
            String sql = String.format("""
                            INSERT OR REPLACE INTO Player (ID, Name)
                            SELECT NULL, '%s'
                            WHERE NOT EXISTS (SELECT * FROM Player WHERE Name = '%s')""", player, player);
            statement.execute(sql);

            // add new high scores table
            sql = String.format("INSERT INTO Record (ID, Points, Player_ID) VALUES (NULL, %d, (SELECT ID FROM Player where Name = '%s'));", score, player);
            statement.execute(sql);
        }
        return true;
    }

    public void insertMove(String rollResult, char guessedLetter, String guessedPhrase, Integer result, Integer gameID, Integer playerID) throws SQLException {
        String in = String.format("NULL, '%s', '%c', '%s', %d, %d, %d", rollResult, guessedLetter, guessedPhrase, result, gameID, playerID);
        statement.execute(String.format("INSERT INTO MOVE (ID, RollResult, GuessedLetter, GuessedPhrase, Result, Game_ID, Player_ID) VALUES (%s)", in));
    }
    public static void main(String[] args){
        Database db = Database.getInstance();
        ArrayList<String> phrases = db.getMatchingPhrases("A__");
        for (String phrase : phrases){
            System.out.println(phrase);
        }
    }
}
