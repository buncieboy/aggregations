import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

import javax.persistence.EntityManagerFactory;
import java.util.Map;

public class AggregationTesting {

    private EntityManagerFactory entityManagerFactory;
    private SessionFactory sessionFactory;

    public void setUp() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        Session session = sessionFactory.openSession();
        entityManagerFactory = session.getEntityManagerFactory();
        session.close();
    }

    public void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public void insertBooks() {
		System.out.println("Inserting books");
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(new Book("Title Example"));
        session.save(new Book("Title Example"));
        session.save(new Book("The Sequel"));
        session.getTransaction().commit();
        session.close();
		System.out.println("Books inserted\n");
    }

    public void readBooks() {
		System.out.println("Reading books from search");
        SearchSession searchSession = Search.session(entityManagerFactory.createEntityManager());
        SearchResult<Book> result = searchSession.search(Book.class)
                .where(f -> f.matchAll())
                .fetchAll();
        for (Book book : result.hits()) {
            System.out.println("Book title: " + book.getTitle());
        }
		System.out.println("All books read\n");
    }

    public void aggregationQuery(int maxTermCount) {
		System.out.println("Running aggregation with max term count of " + maxTermCount);
		Long startTime = System.currentTimeMillis();
        SearchSession searchSession = Search.session(entityManagerFactory.createEntityManager());
        AggregationKey<Map<String, Long>> countByTitleKey = AggregationKey.of("countsByTitle");
        SearchResult<Book> result = searchSession.search(Book.class)
                .where(f -> f.matchAll())
                .aggregation(countByTitleKey, f -> f.terms().field("title", String.class).maxTermCount(maxTermCount))
                .fetch(0);
        Map<String, Long> countByTitle = result.aggregation(countByTitleKey);
        for (String key : countByTitle.keySet()) {
            System.out.println(key + ": " + countByTitle.get(key));
        }
		System.out.println("Aggregation finished in: " + (System.currentTimeMillis()-startTime)+"\n");
    }
}
