import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

            // read each portion of line into variables, construct new Person and add them to the ArrayList
            /*String id = scanner.next();
            String firstName = scanner.next();
            String lastName = scanner.next();
            String email = scanner.next();
            String country = scanner.next();
            String ipAddress = scanner.next();*/
            String toSplit = scanner.nextLine();
            String[] splitString = toSplit.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            Person person = new Person(splitString[0], splitString[1], splitString[2], splitString[3], splitString[4], splitString[5]);
            people.add(person);
        }

        System.out.println(people);

        Spark.get(
                "/",
                (request, response) -> {
                    return null;
                },
                new MustacheTemplateEngine()
        );

    }
}
