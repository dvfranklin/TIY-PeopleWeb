import org.h2.tools.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestPerson {

    private Connection connection;
    private PeopleService service;

    @Before
    public void Before() throws SQLException {
        Server server = Server.createTcpServer("-baseDir", "./data").start();
        connection = DriverManager.getConnection("jdbc:h2:" + server.getURL() + "/test");
        service = new PeopleService(connection);
    }

    @Test
    public void whenPersonInsertedThenExistInDB() throws SQLException {
        //arrange
        service.initDatabase();
        Person person = new Person("Dan", "Franklin", "dvfranklin@gmail.com", "USA", "127.0.0.1");

        //act
        service.insertPerson(person);

        //assert
        assertThat(person.getId(), not(nullValue()));
    }


    @Test
    public void whenPersonSelectedThenPersonReturned() throws SQLException {
        //arrange
        service.initDatabase();
        Person newUser = new Person("Dan", "Franklin", "dvfranklin@gmail.com", "USA", "127.0.0.1");
        service.insertPerson(newUser);

        //act
        Person person = service.selectPerson(newUser.getId());

        //assert
        assertThat(person, not(nullValue()));
    }


    @After
    public void After() throws SQLException {

        connection.close();

        File dataFolder = new File("data");
        if(dataFolder.exists()){
            for(File file : dataFolder.listFiles()){
                if(file.getName().startsWith("test.h2.")){
                    file.delete();
                }
            }
        }
    }
}

