import java.util.logging.Level;

public class Main {
    public static void main(String[] args){
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
        System.setProperty("hibernate.search.backend.directory.type", "local-heap");

        AggregationTesting aggregationTesting = new AggregationTesting();
        aggregationTesting.setUp();
        aggregationTesting.insertBooks();
        aggregationTesting.readBooks();
        aggregationTesting.aggregationQuery(10);
        aggregationTesting.aggregationQuery(1000);
        aggregationTesting.aggregationQuery(100000);
        aggregationTesting.aggregationQuery(1000000);
        aggregationTesting.aggregationQuery(10000000);
        aggregationTesting.aggregationQuery(100000000);
        aggregationTesting.aggregationQuery(1000000000);
        //Throws error if not below 2147483631
        aggregationTesting.aggregationQuery(2147483630);
        aggregationTesting.tearDown();
    }
}
