import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PeopleWeb {

    public static void main(String[] args) throws IOException, SQLException {

        Server server = Server.createTcpServer("-baseDir", "./data").start();
        Connection connection = DriverManager.getConnection("jdbc:h2:" + server.getURL() + "/main");
        PeopleService service = new PeopleService(connection);
        service.initDatabase();

        server.createWebServer("-baseDir", "./data").start();
        service.populateDatabase();


        Spark.get(
                "/",
                (request, response) -> {
                    // Create a HashMap for your model
                    HashMap m = new HashMap();

                    // declare a variable named offset to hold your offset
                    int offset;


                    // because we're going to have to parse an integer from string data we need to open a try/catch block
                    try {

                        // Get the offset query parameter from the request.
                        String offString = request.queryParams("offset");
                        offset = Integer.valueOf(offString);
                    } catch (Exception e){

                        // When the number can't be parsed default the offset to 0
                        offset = 0;
                    }

                    // create an Integer variable named "backOffset" to hold the offset for the previous page and set it to null.
                    int backOffset;


                    // Check if the offset is not 0. This tells us we're not on the first page.
                    if(offset > 0){

                        // If so, set backOffset to the offset minus twenty
                        backOffset = offset - 20;
                        m.put("backOffset", backOffset);
                    } else{
                        backOffset = 0;
                    }


                    // Create another Integer variable named nextOffset and set that to null.
                    int nextOffset;

                    // check if the current offset is less than the size of the people array, minus twenty
                    if(offset < (service.getDbSize() - 20)) {
                        // set nextOffset to the offset plus twenty
                        nextOffset = offset + 20;
                        m.put("nextOffset", nextOffset);
                    } else{
                        nextOffset = 0;
                    }

                    ArrayList<Person> people = service.selectPeople(offset);

                    // put the variable containing the list of people into the model with a key named "people"
                    m.put("currentPeople", people);

                    // return a ModelAndView object for the people.mustache template.
                    return new ModelAndView(m, "people.mustache");
                },
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/person",
                (request, response) -> {
                    // create a HashMap for your model
                    HashMap m = new HashMap();

                    // Create a variable to hold the ID property and read it from the request's query params
                    int id = Integer.valueOf(request.queryParams("id"));
                    m.put("person", service.selectPerson(id));
                    
                    // return a ModelAndView object for the person.mustache template.
                    return new ModelAndView(m, "person.mustache");
                },
                new MustacheTemplateEngine()
        );

    }
}
