package org.example;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Car {
    private String licenseplate;

    public Car(String licenseplate) {
        this.licenseplate = licenseplate;
    }

    public String getLicenseplate() {
        return licenseplate;
    }
}

class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/parking_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Nimcet@1837";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}

class ParkingSport {
    private int spotNumber;
    private boolean available;
    private Car car;

    public ParkingSport(int spotNumber) {
        this.spotNumber = spotNumber;
        this.available = true;
        this.car = null;
    }

    public int getSpotNumber() {
        return spotNumber;
    }

    public boolean isAvailable() {
        return available;
    }

    public Car getCar() {
        return car;
    }

    public void occupy(Car car) {
        this.car = car;
        this.available = false;
    }

    public void vacate() {
        this.car = null;
        this.available = true;
    }
}

class ParkingLot {
    private List<ParkingSport> sports;

    public ParkingLot(int capacity) {
        this.sports = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            sports.add(new ParkingSport(i));
        }
    }

    public boolean parkCar(Car car) {
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO parked_cars (license_plate, spot_number) VALUES (?, ?)")) {

            for (ParkingSport spot : sports) {
                if (spot.isAvailable()) {
                    spot.occupy(car);
                    preparedStatement.setString(1, car.getLicenseplate());
                    preparedStatement.setInt(2, spot.getSpotNumber());
                    preparedStatement.executeUpdate();
                    System.out.println("Car with number: " + car.getLicenseplate() + " parked at spot number: " + spot.getSpotNumber());
                    return true;
                }
            }
            System.out.println("Sorry parking is full");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeCar(String licensePlate) {
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM parked_cars WHERE license_plate = ?")) {

            for (ParkingSport spot : sports) {
                if (!spot.isAvailable() && spot.getCar().getLicenseplate().equalsIgnoreCase(licensePlate)) {
                    spot.vacate();
                    preparedStatement.setString(1, licensePlate);
                    preparedStatement.executeUpdate();
                    System.out.println("Car with number: " + licensePlate + " removed from parking and spot num " + spot.getSpotNumber());
                    return true;
                }
            }
            System.out.println("Car with number " + licensePlate + " not found");
            return false;
        } catch (SQLException e) {
              e.printStackTrace();
            return false;
        }
    }
}

class Test {
    public static void main(String[] args) {
        ParkingLot parkingLot = new ParkingLot(10);
        Car car1 = new Car("UP807673");
        Car car2 = new Car("DL837273");
        Car car3 = new Car("MP841732");
        Car car4 = new Car("MP8414732");
        Car car5 = new Car("MP841882");
        Car car6 = new Car("MP8415442");
        Car car7 = new Car("BH2345679");

        parkingLot.parkCar(car1);
        parkingLot.parkCar(car2);
        parkingLot.parkCar(car3);
        parkingLot.parkCar(car4);
        parkingLot.parkCar(car5);



        parkingLot.parkCar(car6);
        parkingLot.parkCar((car7));
    }
}


