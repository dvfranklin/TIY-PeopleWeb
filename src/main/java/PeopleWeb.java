import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class PeopleWeb {

    public static void main(String[] args) throws IOException{

        // initialize ArrayList and Scanner to read from CSV file
        ArrayList<Person> people = new ArrayList<>();
        File peopleFile = new File("people.csv");
        Scanner scanner = new Scanner(peopleFile);
        scanner.useDelimiter("[,\\n]");

        // skip first line with variable info
        scanner.nextLine();

        while(scanner.hasNext()){

            // construct new Person by splitting line at commas, add Person to the ArrayList
            String toSplit = scanner.nextLine();
            String[] splitString = toSplit.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            Person person = new Person(Integer.valueOf(splitString[0]), splitString[1], splitString[2], splitString[3], splitString[4], splitString[5]);
            people.add(person);
        }


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
                    if(offset < (people.size() - 20)) {
                        // set nextOffset to the offset plus twenty
                        nextOffset = offset + 20;
                        m.put("nextOffset", nextOffset);
                    } else{
                        nextOffset = 0;
                    }

                    // Note that subList returns a List, not an ArrayList. You'll need to create a variable
                    // that can hold a List of People.
                    List<Person> currentPeople = people.subList(offset, offset+20);


                    // put the variable containing the list of people into the model with a key named "people"
                    m.put("currentPeople", currentPeople);

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


                    for(int i = 0; i < people.size(); i++) {
                        // check if this Person instance's id property is equal to the ID in the request's query params.
                        if(people.get(i).getId() == id) {
                            Person person = people.get(i);
                            m.put("person", person);
                            break;
                        }
                    }
                    
                    // return a ModelAndView object for the person.mustache template.
                    return new ModelAndView(m, "person.mustache");
                },
                new MustacheTemplateEngine()
        );

    }
}
