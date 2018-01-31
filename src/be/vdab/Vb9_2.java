package be.vdab;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
/*
Batch Updates: Meerdere uitvoeringen van één SQL statement met parameter(s).
Voorbeeld: Gebruiker tikt meerdere soortnamen. Je voegt daarmee records toe aan de table soorten.
*/
public class Vb9_2 {
    private static final String URL = "jdbc:mysql://localhost/tuincentrum?useSSL=false";
    private static final String USER = "cursist";
    private static final String PASSWORD = "cursist";
    private static final String INSERT_SOORT = 
            "insert into soorten(naam) values (?)";
    public static void main(String[] args) {
        // Set om namen in op te slaan
        Set<String> namen = new LinkedHashSet<>();
        // Opent een Scanner(System.in)
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Tik soortnamen, tik stop na de laatste naam");
            /*
            De iteratie declareert één keer een variabele naam.
            De gebruiker tikt per iteratie een naam.
            Zolang die naam verschilt van stop voeg je de naam toe aan namen.
            Netbeans kan op deze opdracht een onterechte warning geven.
            */
            for (String naam; ! "stop".equalsIgnoreCase(naam = scanner.nextLine()); namen.add(naam));
        } /*
        Het try-blok met de Scanner wordt hier meteen gesloten, 
        vóór het try-blok waar de Connection wordt geopend = in ander try-blok.
        In andere voorbeelden zit het blok met de Scanner "rond" alls db-bewerkingen ??
        Beter: zoals hier. --> Zo snel mogelijk het blok sluiten:
        Resources openen > er iets mee doen > sluiten !
        */
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                // Het PreparedStatement wordt één keer aangemaakt
                PreparedStatement statement = connection.prepareStatement(INSERT_SOORT)) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            for (String naam: namen) {
                // Vult de parameter in het insert statement met één van de ingetikte namen.
                statement.setString(1, naam);
                // Voegt het PreparedStatement met zijn insert statemetn en ingevulde parameter toe aan het netwerkpakket
                statement.addBatch();
            }
            /*
            Stuurt het PreparedStatement als één netwerkpakket naar de db.
            De db voert alle insert statements uit en stuurt daarna één netwerkpakket terug.
            Dit pakket bevat een array met per insert statemetn het aantal toegevoegde records
            */
            int[] aantalToegevoegdeRecordsPerInsert = statement.executeBatch();
            connection.commit();
            System.out.println("Aantal toegevoegde soorten:");
            System.out.println(Arrays.stream(aantalToegevoegdeRecordsPerInsert).sum());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
}
