package by.innowisegroup.session.service;

import by.innowisegroup.session.entity.Author;
import by.innowisegroup.session.entity.Book;
import by.innowisegroup.session.session.MySessionFactory;
import org.hibernate.Session;

import javax.persistence.Query;

public class AuthorService {

    public void add(Author author){
        Session session = MySessionFactory.getSessionFactory().openSession();
        session.getTransaction().begin();
        session.save(author);
        session.getTransaction().commit();

        session.getTransaction().begin();
        Query query = session.createQuery("from Author where name = :paramName");
        query.setParameter("paramName", author.getName());
        session.getTransaction().commit();
        session.close();
    }

    public Author findById(Long id){
        Session session = MySessionFactory.getSessionFactory().openSession();
        session.getTransaction().begin();
        Author author = session.get(Author.class, id);
        author.getBooks().size(); // По идее должно быть ДТО
        session.getTransaction().commit();
        session.close();
        return author;
    }

    public void update(Author author){
        Session session = MySessionFactory.getSessionFactory().openSession();
        session.getTransaction().begin();
        session.update(author);
        session.getTransaction().commit();
        session.close();
    }

    public void delete(Author author){
        Session session = MySessionFactory.getSessionFactory().openSession();
        session.getTransaction().begin();
        session.delete(author);
        session.getTransaction().commit();
        session.close();
    }
}
