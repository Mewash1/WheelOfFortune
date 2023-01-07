package pap.z26.wheeloffortune;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseCommand {
    public interface voidCommand {
        void execute(Statement stmt);
    }

    public interface objectCommand {
        Object execute(Statement stmt);
    }

    public static class CreateTables implements voidCommand {
        public void execute(Statement stmt) {
            String sql;
            try {
                sql = """
                        CREATE TABLE Category
                        (
                            ID INTEGER PRIMARY KEY,
                            Name TEXT NOT NULL
                        )""";
                stmt.execute(sql);
            } catch (SQLException ignored) {}

            try {
                sql = """
                        CREATE TABLE Phrase
                        (
                            ID INTEGER PRIMARY KEY,
                            Phrase TEXT NOT NULL,
                            Category_ID references Category(ID)
                        )""";
                stmt.execute(sql);
            } catch (SQLException ignored) {}

            try {
                sql = """
                        CREATE TABLE Game
                        (
                            ID INTEGER PRIMARY KEY,
                            Record_ID INTEGER references Record(ID)
                        )""";
                stmt.execute(sql);
            } catch (SQLException ignored) {}
            try {
                sql = """
                        CREATE TABLE Player
                        (
                            ID INTEGER PRIMARY KEY,
                            Name TEXT NOT NULL
                        )""";
                stmt.execute(sql);
            } catch (SQLException ignored) {}
            try {
                sql = """
                        CREATE TABLE Record
                        (
                            ID INTEGER PRIMARY KEY,
                            Points INTEGER NOT NULL,
                            Player_ID INTEGER references Player(ID)
                        )""";
                stmt.execute(sql);
            } catch (SQLException ignored) {}
            try {
                sql = """
                        CREATE TABLE Move
                        (
                            ID INTEGER PRIMARY KEY,
                            RollResult TEXT not null,
                            GuessedLetter CHAR,
                            GuessedPhrase TEXT,
                            Result INTEGER,
                            Game_ID INTEGER references Game(ID),
                            Player_ID INTEGER references Player(ID)
                        )
                        """;
                stmt.execute(sql);
            } catch (SQLException ignored) {}
            try {
                sql = """
                        CREATE TABLE Player_Games
                        (
                            ID INTEGER PRIMARY KEY,
                            Player_ID INTEGER references Player(ID),
                            Game_ID INTEGER references Game(ID)
                        )""";
                stmt.execute(sql);
            } catch (SQLException ignored) {}
            try {
                sql = """
                        CREATE TABLE Phrase_Games
                        (
                            ID INTEGER PRIMARY KEY,
                            Phrase_ID INTEGER references Phrase(ID),
                            Game_ID INTEGER references Game(ID)
                        )""";
                stmt.execute(sql);
            } catch (SQLException ignored) {}
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
                stmt.execute(sql);
            } catch (SQLException ignored) {}
            }

        }

    public static class InsertPhrases implements voidCommand {
        public void execute(Statement stmt) {

            ArrayList<String> phrases = new ArrayList<>();
            phrases.add("INSERT INTO Phrase VALUES (1, 'Cicha woda brzegi rwie, 1)");
            phrases.add("INSERT INTO Phrase VALUES (2, 'Baba z wozu koniom lżej', 1)");
            phrases.add("INSERT INTO Phrase VALUES (3, 'Co się odwlecze to nie uciecze', 1)");
            phrases.add("INSERT INTO Phrase VALUES (4, 'Uderz w stół a nożyce się odezwą', 1)");
            phrases.add("INSERT INTO Phrase VALUES (5, 'Elektryka prąd nie tyka', 1)");
            phrases.add("INSERT INTO Phrase VALUES (6, 'Szklana pułapka', 2)");
            phrases.add("INSERT INTO Phrase VALUES (7, 'Przeminęło z wiatrem', 2)");
            phrases.add("INSERT INTO Phrase VALUES (8, 'Ojciec Chrzestny', 2)");
            phrases.add("INSERT INTO Phrase VALUES (9, 'Kod da Vinci', 2)");
            phrases.add("INSERT INTO Phrase VALUES (10, 'Faceci w czerni', 2)");
            for (String phrase : phrases) {
                try {
                    stmt.execute(phrase);
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public static class insertCategories implements voidCommand {
        @Override
        public void execute(Statement stmt) {
            ArrayList<String> phrases = new ArrayList<>();
            phrases.add("INSERT INTO Category VALUES (1, 'Przysłowia')");
            phrases.add("INSERT INTO Category VALUES (2, 'Filmy')");
            phrases.add("INSERT INTO Category VALUES (3, 'Książki')");
            for (String phrase : phrases) {
                try {
                    stmt.execute(phrase);
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public static class getAllPhrases implements objectCommand {
        @Override
        public Object execute(Statement stmt) {
            ArrayList<String> phrases = new ArrayList<>();
            try {
                ResultSet results = stmt.executeQuery("SELECT Phrase, c2.name from Phrase JOIN Category C2 on C2.ID = Phrase.Category_ID");
                while (results.next()) {
                    phrases.add(results.getString("Phrase") + "\n" + results.getString("Name"));
                }
            } catch (SQLException ignored) {
            }
            return phrases;
        }
    }

    public static void callVoidCommand(voidCommand command, Statement stmt) {
        command.execute(stmt);
    }

    public static Object callObjectCommand(objectCommand command, Statement stmt) {
        return command.execute(stmt);
    }
}

