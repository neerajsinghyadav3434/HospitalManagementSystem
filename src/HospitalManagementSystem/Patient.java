package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Patient {
    private Connection connection;
    private Scanner scanner;

    public Patient(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void addPatient() {
        System.out.print("Enter Patient Name: ");
        scanner.nextLine(); // Consume leftover newline from previous input
        String name = scanner.nextLine();
        System.out.print("Enter Patient Age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consume leftover newline after age input
        System.out.print("Enter Patient Gender: ");
        String gender = scanner.nextLine();

        try {
            String query = "INSERT INTO patients(name, age, gender) VALUES(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Patient Added Successfully!");
            } else {
                System.out.println("Failed to add Patient!");
            }
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("Error adding patient: " + e.getMessage());
        }
    }


    public void viewPatient() {
        String query = "SELECT * FROM patients";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Patients: ");
            System.out.println("+------------+-------------------+----------+---------------+");
            System.out.println("| Patient ID | Name              | Age      | Gender        |");
            System.out.println("+------------+-------------------+----------+---------------+");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                System.out.printf("| %-10d | %-17s | %-8d | %-13s |\n", id, name, age, gender);
                System.out.println("+------------+-------------------+----------+---------------+");
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("Error viewing patients: " + e.getMessage());
        }
    }

    public boolean getPatientById(int id) {
        String query = "SELECT * FROM patients WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            boolean exists = resultSet.next();
            resultSet.close();
            preparedStatement.close();

            return exists;
        } catch (SQLException e) {
            System.out.println("Error fetching patient by ID: " + e.getMessage());
        }
        return false;
    }

    public void updatePatient() {
        System.out.print("Enter Patient ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume leftover newline

        // Check if the patient exists before proceeding
        if (!getPatientById(id)) {
            System.out.println("Patient with ID " + id + " does not exist.");
            return;
        }

        System.out.println("Enter new details (leave blank to keep unchanged):");

        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter new age: ");
        String newAgeInput = scanner.nextLine();
        System.out.print("Enter new gender: ");
        String newGender = scanner.nextLine();

        // SQL query with conditional updates
        StringBuilder queryBuilder = new StringBuilder("UPDATE patients SET ");
        boolean needsComma = false;

        if (!newName.isEmpty()) {
            queryBuilder.append("name = ?");
            needsComma = true;
        }
        if (!newAgeInput.isEmpty()) {
            if (needsComma) queryBuilder.append(", ");
            queryBuilder.append("age = ?");
            needsComma = true;
        }
        if (!newGender.isEmpty()) {
            if (needsComma) queryBuilder.append(", ");
            queryBuilder.append("gender = ?");
        }
        queryBuilder.append(" WHERE id = ?");

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString());
            int parameterIndex = 1;

            if (!newName.isEmpty()) {
                preparedStatement.setString(parameterIndex++, newName);
            }
            if (!newAgeInput.isEmpty()) {
                int newAge = Integer.parseInt(newAgeInput);
                preparedStatement.setInt(parameterIndex++, newAge);
            }
            if (!newGender.isEmpty()) {
                preparedStatement.setString(parameterIndex++, newGender);
            }

            preparedStatement.setInt(parameterIndex, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Patient details updated successfully!");
            } else {
                System.out.println("Failed to update patient details.");
            }

            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("Error updating patient: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid age entered. Please enter a valid number.");
        }
    }

}
