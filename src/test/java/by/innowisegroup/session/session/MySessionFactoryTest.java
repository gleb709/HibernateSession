package by.innowisegroup.session.session;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class MySessionFactoryTest {

    private SessionFactory sessionFactory = null;

    @Test
    @DisplayName("Create session, if there is no another session")
    void createSessionIfAnotherSessionNotExistTest(){
        sessionFactory = MySessionFactory.getSessionFactory();

        assertNotNull(sessionFactory, "Null after session init");
    }

    @Test
    @DisplayName("Do not create session, if there is another session")
    void DoNotCreateSessionIfAnotherSessionExistTest(){
        assertNotNull(sessionFactory = MySessionFactory.getSessionFactory(), "Null after session init");
        SessionFactory sessionFactoryActual = sessionFactory;

        try {
            if (sessionFactory == null) {
                Configuration config = new Configuration();
                config.configure();
                sessionFactory = config.buildSessionFactory();
            }
        } catch (Throwable ex) {
            fail("Exception in initializer");
        }
        assertEquals(sessionFactory, sessionFactoryActual);
    }
}