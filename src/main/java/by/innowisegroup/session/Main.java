package by.innowisegroup.session;

import by.innowisegroup.session.entity.Author;
import by.innowisegroup.session.service.AuthorService;
import by.innowisegroup.session.session.MySessionFactory;
import org.hibernate.Session;

public class Main {

    public static void main(String[] args) {


        Author author = new Author();
        author.setName("NewName");
        System.out.println(author.toString());
    }

}