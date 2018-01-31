package be.vdab;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
/*
Batch Updates: Statements zonder parameter.
Voorbeeld: Eerste vb uit hfdst Transacties.
*/
public class Vb9_1 {
    private static final String URL = "jdbc:mysql://localhost/tuincentrum?useSSL=false";
    private static final String USER = "cursist";
    private static final String PASSWORD = "cursist";
    private static final String UPDATE_PRIJS_10_PROCENT = 
            "update planten set verkoopprijs = verkoopprijs * 1.1 where verkoopprijs >= 100";
    private static final String UPDATE_PRIJS_5_PROCENT = 
            "update planten set verkoopprijs = verkoopprijs * 1.05 where verkoopprijs < 100";
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement()) {
            // Stelt het transactie isolatie level in zodat dirty reads vermeden worden:
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            // Voegt het Statement toe aan het netwerkpakket. Doet dit ook op de volgende regel.
            statement.addBatch(UPDATE_PRIJS_10_PROCENT);
            statement.addBatch(UPDATE_PRIJS_5_PROCENT);
            /*
            Stuurt de Statements als één netwerkpakket naar de database.
            De database voert de update statements uit en stuurt daarna één netwerkpakket terug.
            Dit pakket bevat een array met per update statement het aantal gewijzigde records.
            */
            int[] aantalGewijzigdeRecordsPerUpdate = statement.executeBatch();
            System.out.println(aantalGewijzigdeRecordsPerUpdate[0] + " planten met 10% verhoogd");
            System.out.println(aantalGewijzigdeRecordsPerUpdate[1] + " planten met 5% verhoogd");
            connection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
}
