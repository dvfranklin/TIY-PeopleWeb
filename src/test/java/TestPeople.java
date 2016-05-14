import org.h2.tools.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestPeople {

    private Connection connection;
    private PeopleService service;

    @Before
    public void Before() throws SQLException {
        Server server = Server.createTcpServer("-baseDir", "./data").start();
        connection = DriverManager.getConnection("jdbc:h2:" + server.getURL() + "/test");
        service = new PeopleService(connection);
    }

    @Test
    public void whenDbPopulatedThenAllPeopleExist() throws SQLException, FileNotFoundException {
        //arrange
        service.initDatabase();
        service.populateDatabase();

        //act
        ArrayList<Person> people = service.selectPeople(0);

        //assert
        assertThat(people.size(), is(1000));

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
