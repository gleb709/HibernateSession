package by.innowisegroup.session.service;

import by.innowisegroup.session.entity.Author;
import by.innowisegroup.session.entity.Book;
import by.innowisegroup.session.session.MySessionFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CacheLevelTest {

    private Author author1;
    private Author author2;
    private Author author3;
    private Book book1;
    private Book book2;
    private Book book3;

    @BeforeAll
    void init(){

        author1 = new Author();
        author2 = new Author();
        author3 = new Author();

        book1 = new Book();
        book2 = new Book();
        book3 = new Book();

        book1.setName("book1");
        book2.setName("book2");
        book3.setName("book3");

        author1.setName("author1");
        author2.setName("author2");
        author3.setName("author3");
    }

    @BeforeEach
    void addInfoToDB(){
        book3.setAuthor(author2);
        author2.setBooks(new ArrayList<>(List.of(book3)));
        Session session = MySessionFactory.getSessionFactory().openSession();
        session.getTransaction().begin();
        session.save(author2);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @DisplayName("Check first-level cache. Try session methods to control cache")
    void firstLevelCacheTest(){
       Session session = MySessionFactory.getSessionFactory().openSession();
       session.getTransaction().begin();
       Author author = session.get(Author.class, author2.getId()); // Добавление объекта в кеш
        assertTrue(session.contains(author));
        session.clear(); // очищение кеша
        assertFalse(session.contains(author)); // contains проверяет наличие в кеше проверяемого объекта
        author = session.get(Author.class, author2.getId()); // Добавление объекта в кеш
        assertTrue(session.contains(author));
        session.evict(author);//удаление объекта из кеша
        assertFalse(session.contains(author));
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @DisplayName("Check second-level cache")
    void secondLevelCache(){
        Session session = MySessionFactory.getSessionFactory().openSession();
        session.getTransaction().begin();
        Author author = session.get(Author.class, author2.getId()); // Добавление объекта в кеш
        Long id = author.getId();
        session.getTransaction().commit();
        session.close();
        assertTrue(MySessionFactory.getSessionFactory().getCache().contains(Author.class, id));
        MySessionFactory.getSessionFactory().getCache().evictAll();
        assertFalse(MySessionFactory.getSessionFactory().getCache().contains(Author.class, id));
    }

    @AfterEach
    void deleteTableInfo(){
        Session session = MySessionFactory.getSessionFactory().openSession();
        session.getTransaction().begin();
        session.createSQLQuery("delete from Book").executeUpdate();
        session.createSQLQuery("delete from Author").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    @AfterAll
    static void removeDB(){
        Session session = MySessionFactory.getSessionFactory().openSession();
        session.getTransaction().begin();
        session.createSQLQuery("DROP TABLE Book").executeUpdate();
        session.createSQLQuery("DROP TABLE Car").executeUpdate();
        session.createSQLQuery("DROP TABLE Author").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
