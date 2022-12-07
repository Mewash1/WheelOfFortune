package pap.z26.wheeloffortune;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseCommand {
    public interface Command {
        void execute(Statement stmt);
    }

    public interface returnArrayListCommand {
        ArrayList<String> execute(Statement stmt);
    }

    public static class CreateTables implements Command {
        public void execute(Statement stmt) {
            try {
                String sql_Phrases = """
                        CREATE TABLE Phrases(
                            PhraseID INTEGER PRIMARY KEY,
                            Content TEXT NOT NULL,
                            Category TEXT NOT NULL
                        )""";
                stmt.execute(sql_Phrases);
            } catch (SQLException ignored) {
            }

            try {
                String sql_HighScores = """
                        CREATE TABLE HighScores(
                            PlayerID INTEGER PRIMARY KEY,
                            Score INTEGER NOT NULL,
                            AchievedOn DATE NOT NULL
                        )
                        """;
                stmt.execute(sql_HighScores);
            } catch (SQLException ignored) {
            }

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
            } catch (SQLException ignored) {
            }
        }
    }

    public static class InsertPhrases implements Command {
        public void execute(Statement stmt) {

            ArrayList<String> phrases = new ArrayList<>();
            phrases.add("INSERT INTO Phrases VALUES (1, 'Cicha woda brzegi rwie', 'Przysłowia')");
            phrases.add("INSERT INTO Phrases VALUES (2, 'Baba z wozu koniom lżej', 'Przysłowia')");
            phrases.add("INSERT INTO Phrases VALUES (3, 'Co się odwlecze to nie uciecze', 'Przysłowia')");
            phrases.add("INSERT INTO Phrases VALUES (4, 'Uderz w stół a nożyce się odezwą', 'Przysłowia')");
            phrases.add("INSERT INTO Phrases VALUES (5, 'Elektryka prąd nie tyka', 'Przysłowia')");
            phrases.add("INSERT INTO Phrases VALUES (6, 'Szklana pułapka', 'Filmy')");
            phrases.add("INSERT INTO Phrases VALUES (7, 'Przeminęło z wiatrem', 'Filmy')");
            phrases.add("INSERT INTO Phrases VALUES (8, 'Ojciec Chrzestny', 'Filmy')");
            phrases.add("INSERT INTO Phrases VALUES (9, 'Kod da Vinci', 'Filmy')");
            phrases.add("INSERT INTO Phrases VALUES (10, 'Faceci w czerni', 'Filmy')");
            for (String phrase : phrases) {
                try {
                    stmt.execute(phrase);
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public static class getAllPhrases implements returnArrayListCommand {
        @Override
        public ArrayList<String> execute(Statement stmt) {
            ArrayList<String> phrases = new ArrayList<>();
            try {
                ResultSet results = stmt.executeQuery("SELECT Content, Category FROM Phrases");
                while (results.next()) {
                    phrases.add(results.getString("Content") + "\n" + results.getString("Category"));
                }
            } catch (SQLException ignored) {
            }
            return phrases;
        }
    }

    public static void callCommand(Command command, Statement stmt) {
        command.execute(stmt);
    }

    public static ArrayList<String> callReturnArrayListCommand(returnArrayListCommand command, Statement stmt) {
        return command.execute(stmt);
    }
}

