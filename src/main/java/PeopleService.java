import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class PeopleService {

    private final Connection connection;

    public PeopleService(Connection connection) {
        this.connection = connection;
    }

    public void initDatabase() throws SQLException {
        connection.createStatement().execute("DROP TABLE IF EXISTS people");
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS people (id IDENTITY, first_name VARCHAR, last_name VARCHAR, email VARCHAR, country VARCHAR, ip_address VARCHAR)");

    }

    public void insertPerson(Person person) throws SQLException {
        PreparedStatement prepStmt = connection.prepareStatement("INSERT INTO people VALUES (NULL, ?, ?, ?, ?, ?)");

        prepStmt.setString(1, person.getFirstName());
        prepStmt.setString(2, person.getLastName());
        prepStmt.setString(3, person.getEmail());
        prepStmt.setString(4, person.getCountry());
        prepStmt.setString(5, person.getIpAddress());
        prepStmt.execute();

        ResultSet results = prepStmt.getGeneratedKeys();
        results.next();
        person.setId(results.getInt(1));
    }

    public Person selectPerson(int personId) throws SQLException {
        PreparedStatement prepStmt = connection.prepareStatement("SELECT * FROM people WHERE id = ?");
        prepStmt.setInt(1, personId);

        ResultSet results = prepStmt.executeQuery();
        if(results.next()) {
            Person person = new Person(results.getInt("id"), results.getString("first_name"), results.getString("last_name"), results.getString("email"), results.getString("country"), results.getString("ip_address"));
            return person;
        }

        return null;
    }

    public void populateDatabase() throws FileNotFoundException, SQLException {
        File peopleFile = new File("people.csv");
        Scanner scanner = new Scanner(peopleFile);
        scanner.useDelimiter("[,\\n]");

        // skip first line with variable info
        scanner.nextLine();

        while(scanner.hasNext()){

            // construct new Person by splitting line at commas, add Person to the database
            String toSplit = scanner.nextLine();
            String[] splitString = toSplit.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            Person person = new Person(splitString[1], splitString[2], splitString[3], splitString[4], splitString[5]);
            insertPerson(person);
        }
    }

    public ArrayList<Person> selectPeople(int offset) throws SQLException {
        ArrayList<Person> people = new ArrayList<>();
        PreparedStatement prepStmt = connection.prepareStatement("SELECT * FROM people LIMIT 20 OFFSET ?");
        prepStmt.setInt(1, offset);

        ResultSet rs = prepStmt.executeQuery();

        while(rs.next()){
            Person person = new Person(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("email"), rs.getString("country"), rs.getString("ip_address"));
            people.add(person);
        }

        return people;
    }

    public int getDbSize() throws SQLException {
        PreparedStatement prepStmt = connection.prepareStatement("SELECT COUNT(*) FROM people");

        ResultSet rs = prepStmt.executeQuery();

        rs.next();
        int size = rs.getInt(1);

        return size;
    }
}
