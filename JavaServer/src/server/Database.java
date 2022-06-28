package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class Database {

    //JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/team5?useSSL=false";

    // Database credentials
    private String USER = "username";
    private String PASS = "password";

    private Connection connection = null;

    Database() {
        //ReadCredentialsFromConfigFile();
        ConnectToLocalDatabase();
    }

    private void ReadCredentialsFromConfigFile() {

        String configFilePath = "config.file";

        File file = new File(configFilePath);
        try {

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            USER = bufferedReader.readLine();
            PASS = bufferedReader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void ConnectToLocalDatabase(){

        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to Database");
        }
        catch (ClassNotFoundException e) {
            System.out.println("ERROR: DRIVER NOT FOUND");
        }
        catch (SQLException e) {
            System.out.println("ERROR: CONNECTION FAILED");
            System.out.println("Terminating program....");
            System.exit(0);
        }

    }

    public void AddToDatabase(Report report){

        if(report == null){
            System.out.println("Report is null, not adding to database");
        }else {
            String cmdAdd = "INSERT INTO reportTB (LONGITUDE, LATITUDE, REASON) VALUES (?,?,?)";

            try {
                PreparedStatement statement = connection.prepareStatement(cmdAdd);
                statement.setFloat(1, report.getLongitude());
                statement.setFloat(2, report.getLatitude());
                statement.setString(3, report.getReason());
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
