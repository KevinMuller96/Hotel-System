package com.company;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


public class Menues {
    static Scanner input = new Scanner(System.in);
    private static final String url = "jdbc:mysql://localhost:3306/hotelmanagement?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static final String user = "root";
    private static final String password = "Kaktus100";
    protected static Statement sqlStatement = null;
    protected static HashMap<Integer, Room> rooms = new HashMap();
    protected static HashMap<Integer, Customer> customers = new HashMap();
    protected static HashMap<Integer, Food> foods = new HashMap();
    protected static HashMap<Integer, RoomFood> room_food = new HashMap();

    protected static void managementMenu() {
        int selection = 0;


        try (Connection connection = DriverManager.getConnection(url, user, password)) {

            System.out.println("Connections successful!");

            sqlStatement = connection.createStatement();


            boolean exit = false;
            while (!exit) {
                System.out.println("Make a choice\n");
                System.out.println("1.Admin");
                System.out.println("2.Customer");
                System.out.println("0.Exit");
                boolean exit2 = false;
                while (!exit2) {
                    try {
                        selection = input.nextInt();
                        input.nextLine();
                        if (selection <= 2 && selection >= 0) {
                            exit2 = true;
                        } else {
                            System.err.println("Please input a valid number");
                        }
                    } catch (Exception e) {
                        input.nextLine();
                        System.err.println("Please input a valid number");
                    }
                }
                switch (selection) {
                    case 1: adminMenu();
                    break;
                    case 2: customerMenu();
                    break;
                    case 0: {
                        saveToFile();
                        exit = true;
                        break;
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected static void adminMenu() throws SQLException {
        int selection = 0;

        boolean exit = false;
        while (!exit) {
            System.out.println("Make a choice\n");
            System.out.println("1.Administrate Customers");
            System.out.println("2.Administrate Rooms");
            System.out.println("3.Administrate Foods");
            System.out.println("4.Upload file data to database");
            System.out.println("0.Exit");
            boolean exit2 = false;
            while (!exit2) {
                try {
                    selection = input.nextInt();
                    input.nextLine();
                    if (selection <= 4 && selection >= 0) {
                        exit2 = true;
                    } else {
                        System.err.println("Please input a valid number");
                    }
                } catch (Exception e) {
                    input.nextLine();
                    System.err.println("Please input a valid number");
                }
            }
            switch (selection) {
                case 1: admCustomerMenu();
                break;
                case 2: admRoomMenu();
                break;
                case 3: admFoodMenu();
                break;
                case 4: Actions.uploadToDatabase();
                break;
                case 0: exit = true;
                break;
            }
        }
    }

    protected static void customerMenu() throws SQLException {
        int selection = 0;

        boolean exit = false;
        while (!exit) {
            System.out.println("Make a choice\n");
            System.out.println("1.Book a room");
            System.out.println("2.Room details and availability");
            System.out.println("3.Order Food");
            System.out.println("4.Show Bill");
            System.out.println("5.Check Out");
            System.out.println("0.Exit");

            boolean exit2 = false;
            while (!exit2) {
                try {
                    selection = input.nextInt();
                    input.nextLine();
                    if (selection <= 5 && selection >= 0) {
                        exit2 = true;
                    } else {
                        System.err.println("Please input a valid number");
                    }
                } catch (Exception e) {
                    input.nextLine();
                    System.err.println("Please input a valid number");
                }
            }
            switch (selection) {
                case 1: Actions.bookRoom();
                break;
                case 2: Actions.roomAvailability();
                break;
                case 3: Actions.orderFood();
                break;
                case 4: Actions.showBill();
                break;
                case 5: Actions.checkOut();
                break;
                case 0: exit = true;
                break;
            }
        }
    }

    protected static void admRoomMenu() throws SQLException {
        int selection = 0;

        boolean exit = false;
        while (!exit) {
            System.out.println("Make a choice\n");
            System.out.println("1.Create Room");
            System.out.println("2.Show all Rooms");
            System.out.println("3.Update Room");
            System.out.println("4.Delete Room");
            System.out.println("0.Exit");
            boolean exit2 = false;
            while (!exit2) {
                try {
                    selection = input.nextInt();
                    input.nextLine();
                    if (selection <= 4 && selection >= 0) {
                        exit2 = true;
                    } else {
                        System.err.println("Please input a valid number");
                    }
                } catch (Exception e) {
                    input.nextLine();
                    System.err.println("Please input a valid number");
                }
            }
            switch (selection) {
                case 1: Actions.create("room");
                break;
                case 2: Actions.showALL(rooms);
                break;
                case 3: Actions.update("room");
                break;
                case 4: Actions.delete("room");
                break;
                case 0: exit = true;
                break;
            }
        }
    }

    protected static void admCustomerMenu() throws SQLException {
        int selection = 0;

        boolean exit = false;
        while (!exit) {
            System.out.println("Make a choice\n");
            System.out.println("1.Create Customer");
            System.out.println("2.Create new Booking");
            System.out.println("3.Show all Customers");
            System.out.println("4.Search Customer by ID");
            System.out.println("5.Update Customer");
            System.out.println("6.Checkout Customer");
            System.out.println("7.Delete Customer");
            System.out.println("0.Exit");
            boolean exit2 = false;
            while (!exit2) {
                try {
                    selection = input.nextInt();
                    input.nextLine();
                    if (selection <= 7 && selection >= 0) {
                        exit2 = true;
                    } else {
                        System.err.println("Please input a valid number");
                    }
                } catch (Exception e) {
                    input.nextLine();
                    System.err.println("Please input a valid number");
                }
            }
            switch (selection) {
                case 1: Actions.create("customer");
                break;
                case 2: Actions.bookRoom();
                break;
                case 3: Actions.showALL(customers);
                break;
                case 4: Actions.findCustomerID();
                break;
                case 5: Actions.update("customer");
                break;
                case 6: Actions.checkOut();
                break;
                case 7: Actions.delete("customer");
                break;
                case 0: exit = true;
                break;
            }
        }
    }

    protected static void admFoodMenu() throws SQLException {
        int selection = 0;

        boolean exit = false;
        while (!exit) {
            System.out.println("Make a choice\n");
            System.out.println("1.Add new Food");
            System.out.println("2.Show all Foods");
            System.out.println("3.Update Food");
            System.out.println("4.Remove Food");
            System.out.println("5.Order food to room");
            System.out.println("0.Exit");
            boolean exit2 = false;
            while (!exit2) {
                try {
                    selection = input.nextInt();
                    input.nextLine();
                    if (selection <= 5 && selection >= 0) {
                        exit2 = true;
                    } else {
                        System.err.println("Please input a valid number");
                    }
                } catch (Exception e) {
                    input.nextLine();
                    System.err.println("Please input a valid number");
                }
            }
            switch (selection) {
                case 1: Actions.create("food");
                break;
                case 2: Actions.showALL(foods);
                break;
                case 3: Actions.update("food");
                break;
                case 4: Actions.delete("food");
                break;
                case 5: Actions.orderFood();
                break;
                case 0: exit = true;
                break;
            }
        }
    }

    protected static void saveToFile() {
        try {
            FileOutputStream fos = new FileOutputStream("CustomerHashMap.txt");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(customers);
            System.out.println("Customers saved.");
            oos.close();
            fos.close();

            FileOutputStream fosTwo = new FileOutputStream("RoomHashMap.txt");
            ObjectOutputStream oosTwo = new ObjectOutputStream(fosTwo);
            oosTwo.writeObject(rooms);
            System.out.println("Rooms saved.");
            oosTwo.close();
            fosTwo.close();

            FileOutputStream fosThree = new FileOutputStream("FoodHashMap.txt");
            ObjectOutputStream oosThree = new ObjectOutputStream(fosThree);
            oosThree.writeObject(foods);
            System.out.println("Foods saved.");
            oosThree.close();
            fosThree.close();

            FileOutputStream fosFour = new FileOutputStream("RoomFoodHashMap.txt");
            ObjectOutputStream oosFour = new ObjectOutputStream(fosFour);
            oosFour.writeObject(Menues.room_food);
            System.out.println("Metadata for transactions saved.");
            oosFour.close();
            fosFour.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("unchecked")
    protected static void loadFromFile() {
        try {
            FileInputStream fis = new FileInputStream("CustomerHashMap.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            customers = (HashMap<Integer, Customer>) ois.readObject();
            ois.close();
            fis.close();


            FileInputStream fisTwo = new FileInputStream("RoomHashMap.txt");
            ObjectInputStream oisTwo = new ObjectInputStream(fisTwo);
            rooms = (HashMap<Integer, Room>) oisTwo.readObject();
            oisTwo.close();
            fisTwo.close();

            FileInputStream fisThree = new FileInputStream("FoodHashMap.txt");
            ObjectInputStream oisThree = new ObjectInputStream(fisThree);
            foods = (HashMap<Integer, Food>) oisThree.readObject();
            oisThree.close();
            fisThree.close();

            FileInputStream fisFour = new FileInputStream("RoomFoodHashMap.txt");
            ObjectInputStream oisFour = new ObjectInputStream(fisFour);
            room_food = (HashMap<Integer, RoomFood>) oisFour.readObject();
            oisFour.close();
            fisFour.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}