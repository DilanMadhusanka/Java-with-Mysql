/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textreader.storemysql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author acer
 */
public class Employees {

    private String empName;
    private int empSalary;
    private int empDeptId;

    public void readData() {
        try (Scanner input = new Scanner(new File("C:\\Users\\acer\\Documents\\NetBeansProjects\\TextReader & StoreMySql\\src\\textreader\\storemysql\\emp_data.txt"))) {
            while (input.hasNextLine()) {
                empName = "";
                String line;

                line = input.nextLine();

                // if the line variable has no data then re-iterate the loop to move on the next line
                if (line.length() <= 0) {
                    continue;
                }

                //proccess the line of text for each data item
                try (Scanner data = new Scanner(line)) {
                    while (!data.hasNextInt()) {
                        empName += data.next() + " ";
                    }
                    empName = empName.trim();

                    //get salary
                    if (data.hasNextInt()) {
                        empSalary = data.nextInt();
                    }

                    // get department id
                    if (data.hasNextInt()) {
                        empDeptId = data.nextInt();
                    }
                }
                // check data
//                System.out.println(empName+"\t"+empSalary+"\t"+empDeptId);

                saveData(); //call the method to save the data into the database
            }
        } catch (IOException e) {
            System.out.println("e");
        }
    }

    // save data into the database
    private void saveData() {
        try (Connection conn = connect();
                PreparedStatement pstat = conn.prepareStatement("INSERT INTO employees VALUES(?, ?, ?)")) {
            pstat.setString(1, empName);
            pstat.setInt(2, empSalary);
            pstat.setInt(3, empDeptId);

            pstat.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void displayData() {
        try (Connection conn = connect();
                Statement stat = conn.createStatement()) {

            boolean hasResultSet = stat.execute("SELECT * FROM employees");

            if (hasResultSet) {
                ResultSet result = stat.getResultSet();
                ResultSetMetaData metaData = result.getMetaData();

                //get number of column
                int columnCount = metaData.getColumnCount();

                // display column labels
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(metaData.getColumnLabel(i) + "\t\t");
                }
                System.out.println();

                //display data
                while (result.next()) {
                    System.out.printf("%-20s%10d%15d%n", result.getString("emp_name"), result.getInt("salary"), result.getInt("dept_id"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // create a connection to the database
    private Connection connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/employees", "root", "");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e);
            return null;
        }
    }
}
