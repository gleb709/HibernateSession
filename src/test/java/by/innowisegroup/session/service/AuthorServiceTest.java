package by.innowisegroup.session.service;

import by.innowisegroup.session.entity.Author;
import by.innowisegroup.session.entity.Book;
import by.innowisegroup.session.session.MySessionFactory;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthorServiceTest {

    private AuthorService authorService;

    private Author author1;
    private Author author2;
    private Author author3;
    private Book book1;
    private Book book2;
    private Book book3;

    @BeforeAll
    void init(){
        authorService = new AuthorService();

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
    @DisplayName("Find author by id")
    void findAuthorByIdTest(){
        Author author = authorService.findById(author2.getId());

        assertEquals(author2, author);
    }

    @Test
    @DisplayName("Add new author to the database")
    void addAuthorTest(){
        book1.setAuthor(author1);
        book2.setAuthor(author1);
        List<Book> bookList = new ArrayList<>(List.of(book1, book2));
        author1.setBooks(bookList);

        authorService.add(author1);

        Session session = MySessionFactory.getSessionFactory().openSession();
        session.getTransaction().begin();
        Query query = session.createQuery("from Author where name = :paramName");
        query.setParameter("paramName", author1.getName());
        assertEquals(author1, query.getSingleResult(), "There is no new authors in the database");
        Author author = (Author) query.getSingleResult();
        assertEquals(bookList, author.getBooks(), "Book lists are not the same");
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @DisplayName("Throw Lazy init exception, when we try get child objects from database without session")
    void LazyExceptionTest(){
        Session session = MySessionFactory.getSessionFactory().openSession();
        session.getTransaction().begin();
        Query query = session.createQuery("from Author where name = :paramName");
        query.setParameter("paramName", author2.getName());
        Author author = (Author) query.getSingleResult();
        assertEquals(author2.getName(), author.getName(), "Fail while try to get the author from database");
        session.getTransaction().commit();
        session.close();

        assertThrows(LazyInitializationException.class, () -> author.getBooks().get(0), "Expect to get lazy init exception");
    }

    @Test
    @DisplayName("Delete author from database")
    void deleteAuthorTest(){

        authorService.delete(author2);

        Session session = MySessionFactory.getSessionFactory().openSession();
        session.getTransaction().begin();
        Query query = session.createQuery("from Author where name = :paramName");
        query.setParameter("paramName", author2.getName());
        assertFalse(query.getResultList().contains(author2));
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @DisplayName("Update author in database")
    void updateAuthorTest(){
        String newName = "newName";
        author2.setName(newName);

        authorService.update(author2);

        Session session = MySessionFactory.getSessionFactory().openSession();
        session.getTransaction().begin();
        Query query = session.createQuery("from Author where name =: name");
        query.setParameter("name", newName);
        Author author = (Author) query.getSingleResult();
        session.getTransaction().commit();
        session.close();

        assertEquals(newName, author.getName());
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