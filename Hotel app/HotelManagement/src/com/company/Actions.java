package com.company;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class Actions {
    private static final Statement sqlStatement = Menues.sqlStatement;
    private static final Scanner input = new Scanner(System.in);
    protected static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";

    protected static <T> void showALL(HashMap<Integer, T> hashMapT) {
        hashMapT.forEach((I, T) -> System.out.println(("ID:" + I + T.toString() + "\n")));
    }

    protected static void roomAvailability() {
        Menues.rooms.entrySet().stream().filter(room -> room.getValue().getCustomerID() == null).forEach(System.out::println);

    }

    protected static void bookRoom() throws SQLException {
        String choice;
        String firstName = "";
        String lastName = "";
        String phoneNumber;
        int ID;
        int days;
        int roomId;
        int totalRoomPrice;

        System.out.println("Welcome to the hotels automated room booking system\n");
        while (true) {
            System.out.println("Are you a new customer? Y/N");
            choice = input.nextLine();
            if (choice.equalsIgnoreCase("Y")) {
                createCustomer();
                ResultSet largestID = sqlStatement.executeQuery("SELECT MAX(customer_ID) FROM hotelmanagement.Customer;");
                if (largestID.next()) {
                    ID = largestID.getInt(1);
                    break;
                }
            } else if (choice.equalsIgnoreCase("N")) {
                System.out.println("Please enter first name of the person who's booking the room.");
                firstName = input.nextLine();
                System.out.println("please enter the last name of the person who's booking the room.");
                lastName = input.nextLine();
                ResultSet rowCount = sqlStatement.executeQuery("SELECT COUNT(*) FROM customer WHERE firstname LIKE \"" + firstName + "\" AND lastname LIKE \"" + lastName + "\";");
                rowCount.next();
                int count = rowCount.getInt(1);
                if (count > 1) {
                    System.out.println("There where more then one customer matching that exact name, may we also have your phonenumber to narrow down the seach?");
                    while (true) {
                        try {
                            phoneNumber = input.nextLine();
                            ResultSet isEmpty = sqlStatement.executeQuery("SELECT customer_ID FROM customer WHERE phonenumber LIKE \"" + phoneNumber + "\";");
                            if (isEmpty.next()) {
                                break;
                            } else {
                                System.err.println("No user matching that phonenumber was found, please try again.\n");
                            }
                        } catch (Exception e) {
                            input.nextLine();
                            System.err.println("Please use a valid input.\n");
                        }
                    }
                    ResultSet result = sqlStatement.executeQuery("SELECT customer_ID FROM customer WHERE firstname LIKE \"" + firstName + "\" AND lastname LIKE \"" + lastName + "\" AND phonenumber LIKE " + phoneNumber + ";");
                    result.next();
                    ID = result.getInt(1);
                    break;
                } else if (count == 1) {
                    ResultSet result = sqlStatement.executeQuery("SELECT customer_ID FROM customer WHERE firstname LIKE \"" + firstName + "\" AND lastname LIKE \"" + lastName + "\";");
                    result.next();
                    ID = result.getInt(1);
                    break;
                } else {
                    System.err.println("There is no one matching that discription, please try again.\n");
                }
            } else {
                System.err.println("Please use a valid input.\n");
            }
        }
        roomAvailabilityOld();
        System.out.println("\n");
        while (true) {
            try {
                System.out.println("Please choose the room you would like to book by id: ");
                roomId = input.nextInt();
                input.nextLine();
                ResultSet isEmpty = sqlStatement.executeQuery("SELECT price from room WHERE room_ID LIKE " + roomId + " AND customer_ID IS NULL;");
                if (isEmpty.next()) {
                    totalRoomPrice = isEmpty.getInt(1);
                    break;
                } else {
                    System.err.println("No room matching that ID was found, please try again.\n");
                }
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please enter a valid input.\n");
            }
        }

        System.out.println("How many days would you like to stay? ");
        while (true) {
            try {
                days = input.nextInt();
                input.nextLine();
                if (days > 0) {
                    break;
                } else {
                    System.err.println("Please, you can not book a room for 0 or a negative amount of days.");
                }
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please input a valid number.");
            }
        }
        LocalDate todaysDate = (LocalDate.now());
        LocalDate checkoutDate = (LocalDate.now()); // Start date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(dateFormat.parse(String.valueOf(checkoutDate)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ResultSet price = sqlStatement.executeQuery("SELECT bill from customer WHERE customer_ID like " + ID + ";");
        totalRoomPrice = totalRoomPrice * days;
        price.next();
        totalRoomPrice = totalRoomPrice + price.getInt(1);
        c.add(Calendar.DATE, days); // Adderar antalet dagar
        checkoutDate = LocalDate.parse(dateFormat.format(c.getTime())); // checkoutDate har utcheckningsdatum
        PreparedStatement updateRoom = sqlStatement.getConnection().prepareStatement("UPDATE room SET checkin_date = ?, checkout_date = ?, customer_id = ? WHERE Room_ID LIKE ?;");
        updateRoom.setDate(1, Date.valueOf(todaysDate));
        updateRoom.setDate(2, Date.valueOf(checkoutDate));
        updateRoom.setInt(3, ID);
        updateRoom.setInt(4, roomId);
        updateRoom.executeUpdate();
        System.out.println("Room number " + roomId + " has been booked from today until " + checkoutDate + " by " + firstName + " " + lastName);
        PreparedStatement updateCustomer = sqlStatement.getConnection().prepareStatement("UPDATE hotelmanagement.customer SET bill = ? WHERE customer_ID LIKE ?;");
        updateCustomer.setInt(1, totalRoomPrice);
        updateCustomer.setInt(2, ID);
        updateCustomer.executeUpdate();
        System.out.println("\n");

        Menues.rooms.get(roomId).setCheckIn(todaysDate);
        Menues.rooms.get(roomId).setCheckOut(checkoutDate);
        Menues.rooms.get(roomId).setCustomerID(ID);
        Menues.customers.get(ID).setBill(totalRoomPrice);


    }

    protected static void orderFood() throws SQLException {
        String firstName;
        String lastName;
        int customerID;
        int choice;
        int roomID;
        String foodType = "";
        String phoneNumber;
        int foodID;
        int price;

        System.out.println("Welcome to the hotels automated food ordering system.");
        while (true) {
            System.out.println("Please enter first and last name of the person who's ordering the food.");
            firstName = input.nextLine();
            lastName = input.nextLine();
            ResultSet rowCount = sqlStatement.executeQuery("SELECT COUNT(*) FROM customer WHERE firstname LIKE \"" + firstName + "\" AND lastname LIKE \"" + lastName + "\";");
            rowCount.next();
            int count = rowCount.getInt(1);
            if (count > 1) {
                System.out.println("There where more then one customer matching that exact name, may we also have your phonenumber to narrow down the seach?");
                while (true) {
                    try {
                        phoneNumber = input.nextLine();
                        ResultSet isEmpty = sqlStatement.executeQuery("SELECT customer_ID FROM customer WHERE phonenumber LIKE \"" + phoneNumber + "\";");
                        if (isEmpty.next()) {
                            break;
                        } else {
                            System.err.println("No user matching that phonenumber was found, please try again.");
                        }
                    } catch (Exception e) {
                        input.nextLine();
                        System.err.println("Please use a valid input.");
                    }

                }
                ResultSet result = sqlStatement.executeQuery("SELECT customer_ID FROM customer WHERE firstname LIKE \"" + firstName + "\" AND lastname LIKE \"" + lastName + "\" AND phonenumber LIKE " + phoneNumber + ";");
                customerID = result.getInt(1);
                break;
            } else if (count == 1) {
                ResultSet result = sqlStatement.executeQuery("SELECT customer_ID FROM customer WHERE firstname LIKE \"" + firstName + "\" AND lastname LIKE \"" + lastName + "\";");
                result.next();
                customerID = result.getInt(1);
                break;
            } else {
                System.err.println("There is no one matching that discription, please try again.");
            }
        }
        ResultSet resultRoomID = sqlStatement.executeQuery("SELECT room_id FROM room WHERE customer_id LIKE " + customerID + ";");
        if (resultRoomID.next()) {
            roomID = resultRoomID.getInt(1);
            while (true) {
                System.out.println("What kind of food would you like to order?");
                System.out.println("1.Breakfast");
                System.out.println("2.Lunch");
                System.out.println("3.Dinner");
                System.out.println("4.Drinks");
                try {
                    choice = input.nextInt();
                    input.nextLine();
                    if (choice <= 4 && choice > 0) {
                        break;
                    } else {
                        System.err.println("Please enter a valid number.\n");
                    }
                } catch (Exception e) {
                    input.nextLine();
                    System.err.println("Please enter a valid input.\n");
                }
            }
            switch (choice) {
                case 1: foodType = "Breakfast";
                break;
                case 2: foodType = "Lunch";
                break;
                case 3: foodType = "Dinner";
                break;
                case 4: foodType = "Drink";
                break;
            }
            ResultSet result = sqlStatement.executeQuery("SELECT * FROM food WHERE type LIKE \"" + foodType + "\";");
            int columnCount = result.getMetaData().getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = result.getMetaData().getColumnName(i + 1);
            }

            for (String columnName : columnNames) {
                System.out.print(PadRight(columnName));
            }

            while (result.next()) {
                System.out.println();
                for (String columnName : columnNames) {
                    String value = result.getString(columnName);

                    if (value == null)
                        value = "null";

                    System.out.print(PadRight(value));
                }
            }
            while (true) {
                try {
                    System.out.println("\nPlease select a food item by typing it's food_ID");
                    foodID = input.nextInt();
                    input.nextLine();
                    ResultSet isEmpty = sqlStatement.executeQuery("SELECT price FROM food WHERE food_ID LIKE " + foodID + " AND type LIKE \"" + foodType + "\";");
                    if (isEmpty.next()) {
                        price = isEmpty.getInt(1);
                        ResultSet customerBill = sqlStatement.executeQuery("SELECT bill FROM customer WHERE customer_Id LIKE " + customerID + ";");
                        customerBill.next();
                        price = price + customerBill.getInt(1);
                        break;
                    }
                } catch (Exception e) {
                    input.nextLine();
                    System.err.println("Please use a valid input.");
                }
            }
            LocalDate todaysDate = (LocalDate.now());
            PreparedStatement updateCustomer = sqlStatement.getConnection().prepareStatement("UPDATE customer SET bill = ? WHERE customer_id LIKE ?");
            updateCustomer.setInt(1, price);
            updateCustomer.setInt(2, customerID);
            updateCustomer.executeUpdate();
            PreparedStatement updateRoomFood = sqlStatement.getConnection().prepareStatement("INSERT INTO room_food SET room_ID = ?, food_ID = ?, order_time = ?;");
            updateRoomFood.setInt(1, roomID);
            updateRoomFood.setInt(2, customerID);
            updateRoomFood.setDate(3, Date.valueOf(todaysDate));
            updateRoomFood.executeUpdate();
            System.out.println("customer " + firstName + " " + lastName + " is now ordering food to room number " + roomID);
            Menues.customers.get(customerID).setBill(price);
            RoomFood newRoomFood = new RoomFood(foodID, todaysDate);
            Menues.room_food.put(roomID, newRoomFood);
        } else {
            System.err.println("The customer you've entered into the system does not have a room booking assoiciated with them, which is needed for ordering food to your room, please book a room first.");
        }
        System.out.println("\n");
    }

    protected static void showBill() throws SQLException {
        String localInput;
        while (true) {
            System.out.println("Please enter customer ID you would like to see bill for.");
            localInput = input.nextLine();
            ResultSet isEmpty = sqlStatement.executeQuery("SELECT customer_id FROM hotelmanagement.customer WHERE customer_id LIKE " + localInput + ";");
            if (isEmpty.next()) {
                ResultSet result = sqlStatement.executeQuery("SELECT CONCAT(firstname, ' ', lastname) AS Fullname, Bill FROM hotelmanagement.Customer WHERE customer_ID LIKE " + localInput + ";");
                int columnCount = result.getMetaData().getColumnCount();
                String[] columnNames = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    columnNames[i] = result.getMetaData().getColumnName(i + 1);
                }
                for (String columnName : columnNames) {
                    System.out.print(PadRight(columnName));
                }
                while (result.next()) {
                    System.out.println();
                    for (String columnName : columnNames) {
                        String value = result.getString(columnName);
                        if (value == null)
                            value = "null";
                        System.out.print(PadRight(value));
                    }
                }
                break;
            } else {
                System.err.println("No customer matching that ID was found, please try again.\n");
            }
        }
        System.out.println("\n");
    }

    protected static void checkOut() throws SQLException {
        String firstName;
        String lastName;
        int customerID;
        String choice;
        int roomID;

        System.out.println("Welcome to the hotel system for self checkout. Please state your first and then last name:");
        while (true) {
            firstName = input.nextLine();
            lastName = input.nextLine();
            ResultSet isEmpty = sqlStatement.executeQuery("SELECT * FROM hotelmanagement.customer WHERE firstname LIKE \"" + firstName + "\" AND lastname LIKE \"" + lastName + "\";");
            if (isEmpty.next()) {
                ResultSet result = sqlStatement.executeQuery("SELECT * FROM hotelmanagement.customer WHERE firstname LIKE \"" + firstName + "\" AND lastname LIKE \"" + lastName + "\";");
                int columnCount = result.getMetaData().getColumnCount();
                String[] columnNames = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    columnNames[i] = result.getMetaData().getColumnName(i + 1);
                }
                for (String columnName : columnNames) {
                    System.out.print(PadRight(columnName));
                }
                while (result.next()) {
                    System.out.println();
                    for (String columnName : columnNames) {
                        String value = result.getString(columnName);
                        if (value == null)
                            value = "null";
                        System.out.print(PadRight(value));
                    }
                }
                break;
            } else {
                System.err.println("The system could not find any customer going by that name, please try a new name.\n");
            }
        }
        System.out.println("\n");
        while (true) {
            System.out.println("Please enter the customer_id of the customer you would like to checkout.");
            try {
                customerID = input.nextInt();
                input.nextLine();
                ResultSet isEmpty = sqlStatement.executeQuery("SELECT * FROM customer WHERE customer_id LIKE " + customerID + ";");
                if (isEmpty.next()) {
                    ResultSet result = sqlStatement.executeQuery("SELECT s.* FROM (select @p1:=" + customerID + " p) parm , checkoutinfo s;");
                    result.next();
                    roomID = result.getInt(2);
                    int columnCount = result.getMetaData().getColumnCount();
                    String[] columnNames = new String[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        columnNames[i] = result.getMetaData().getColumnName(i + 1);
                    }
                    for (String columnName : columnNames) {
                        System.out.print(PadRight(columnName));
                    }
                    while (result.next()) {
                        System.out.println();
                        for (String columnName : columnNames) {
                            String value = result.getString(columnName);
                            if (value == null)
                                value = "null";
                            System.out.print(PadRight(value));
                        }
                    }

                    break;
                } else {
                    System.err.println("No customer matching that ID was found.\n");
                }
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please enter a valid customer ID.\n");
            }
        }
        while (true) {
            System.out.println("\nWould you like to pay your bill now Y/N");
            choice = input.nextLine();
            if (choice.equalsIgnoreCase("y")) {
                PreparedStatement updateCustomer = sqlStatement.getConnection().prepareStatement("UPDATE customer SET bill = ? WHERE customer_id LIKE ?");
                updateCustomer.setInt(1, 0);
                updateCustomer.setInt(2, customerID);
                updateCustomer.executeUpdate();
                PreparedStatement updateRoomFood = sqlStatement.getConnection().prepareStatement("DELETE FROM Room_food WHERE Room_ID LIKE ?;");
                updateRoomFood.setInt(1, roomID);
                updateRoomFood.executeUpdate();
                sqlStatement.executeUpdate("UPDATE room SET customer_ID = NULL, checkout_date = NULL, checkin_Date = NULL WHERE room_ID LIKE " + roomID + ";");
                System.out.println(ANSI_BLUE + firstName + " " + lastName + " has been checked out.\n" + ANSI_RESET);
                Menues.customers.get(customerID).setBill(0);
                Menues.room_food.remove(roomID);
                Menues.rooms.get(roomID).setCustomerID(null);
                Menues.rooms.get(roomID).setCheckIn(null);
                Menues.rooms.get(roomID).setCheckOut(null);
                break;
            } else if (choice.equalsIgnoreCase("n")) {
                System.out.println("Thank you for using the customer checkout system, the procedure was not finished.");
                break;
            } else {
                System.err.println("Please choose a valid input.");
            }
        }
        System.out.println("\n");
    }

    protected static void findCustomerID() {
        String localInput;

        while (true) {
            System.out.println("Please state the customer_ID of the customer you would like to find.");
            localInput = input.nextLine();
            try {
                ResultSet isEmpty = sqlStatement.executeQuery("SELECT customer_id FROM hotelmanagement.customer WHERE customer_id LIKE " + localInput + ";");
                if (isEmpty.next()) {
                    ResultSet result = sqlStatement.executeQuery("SELECT * from customer WHERE customer_ID LIKE " + localInput + ";");
                    int columnCount = result.getMetaData().getColumnCount();
                    String[] columnNames = new String[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        columnNames[i] = result.getMetaData().getColumnName(i + 1);
                    }

                    for (String columnName : columnNames) {
                        System.out.print(PadRight(columnName));
                    }

                    while (result.next()) {
                        System.out.println();
                        for (String columnName : columnNames) {
                            String value = result.getString(columnName);

                            if (value == null)
                                value = "null";

                            System.out.print(PadRight(value));
                        }
                    }
                    break;
                } else {
                    System.err.println("The system could not find any customer going by that customer_ID, please try a new ID.\n");
                }
            } catch (Exception e) {
                System.err.println("Please usa a acceptable ID\n");
            }
        }
        System.out.println("\n");
    }

    @Deprecated
    protected static void roomAvailabilityOld() throws SQLException {

        ResultSet result = sqlStatement.executeQuery("SELECT room_id, type, beds, price, AC, capacity FROM room WHERE Customer_ID IS NULL;");
        int columnCount = result.getMetaData().getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnNames[i] = result.getMetaData().getColumnName(i + 1);
        }

        for (String columnName : columnNames) {
            System.out.print(PadRight(columnName));
        }

        while (result.next()) {
            System.out.println();
            for (String columnName : columnNames) {
                String value = result.getString(columnName);

                if (value == null)
                    value = "null";

                System.out.print(PadRight(value));
            }
        }
        System.out.println("\n");
    }

    @Deprecated
    protected static void showAllOld(String subject) throws SQLException {
        ResultSet result = sqlStatement.executeQuery("SELECT * from " + subject + ";");
        int columnCount = result.getMetaData().getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnNames[i] = result.getMetaData().getColumnName(i + 1);
        }

        for (String columnName : columnNames) {
            System.out.print(PadRight(columnName));
        }

        while (result.next()) {
            System.out.println();
            for (String columnName : columnNames) {
                String value = result.getString(columnName);

                if (value == null)
                    value = "null";

                if (columnName.equalsIgnoreCase("AC") && value.equalsIgnoreCase("0")) {
                    value = "false";
                }
                if (columnName.equalsIgnoreCase("AC") && value.equalsIgnoreCase("1")) {
                    value = "true";
                }

                System.out.print(PadRight(value));
            }
        }
        System.out.println("\n");

    }

    protected static void create(@NotNull String choice) throws SQLException {
        switch (choice) {
            case "customer": createCustomer();
            break;
            case "room": createRoom();
            break;
            case "food": createFood();
            break;
        }
    }

    protected static void createCustomer() throws SQLException {
        String firstName;
        String lastName;
        String phoneNumber;
        String emailaddress;
        int company;
        boolean allInclusive;
        boolean halfBoard;
        int ID = 0;

        while (true) {
            System.out.print("Please enter customers firstname: ");
            firstName = input.nextLine();

            if (!firstName.isEmpty() && !firstName.isBlank()) {
                break;
            } else {
                System.err.println("Please, a name needs to be more then just whitespace.\n");
            }
        }
        while (true) {
            System.out.print("Please enter customers lastname: ");
            lastName = input.nextLine();
            if (!lastName.isEmpty() && !lastName.isBlank()) {
                break;
            } else {
                System.err.println("Please, a name needs to be more then just whitespace.\n");
            }
        }
        while (true) {
            try {
                System.out.println("Please insert phonenumber with areacode: ");
                phoneNumber = input.nextLine();
                int numberLength = (int) (Math.log10(Double.parseDouble(phoneNumber.substring(1))) + 1);
                if (Double.parseDouble(phoneNumber.substring(1)) > 0 && (numberLength == 12 || numberLength == 13)) {
                    break;
                } else {
                    System.err.println("A phonenumber is made out of 12 or 13 positive integers, please try a valid phonenumber.\n");
                }
            } catch (Exception e) {
                System.err.println("Your number must be made out of integers, no letters allowed.\n");
            }
        }
        System.out.print("Please enter customers email: ");
        emailaddress = input.nextLine();
        while (true) {
            try {
                System.out.println("Please insert customers number of companions:");
                company = input.nextInt();
                input.nextLine();
                break;
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please enter a valid input.\n");
            }
        }
        while (true) {
            try {
                System.out.println("All inclusive true/false:");
                allInclusive = input.nextBoolean();
                input.nextLine();
                break;
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please enter a valid input\n");
            }
        }
        while (true) {
            try {
                System.out.println("Half Board, true/false:");
                halfBoard = input.nextBoolean();
                input.nextLine();
                break;
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please input a valid input\n");
            }
        }
        PreparedStatement createCustomer = sqlStatement.getConnection().prepareStatement("INSERT INTO hotelmanagement.Customer SET firstname = ?, lastname = ?, bill = ?, phonenumber = ?, emailaddress = ?, company = ?, all_inclusive = ?, half_board = ? ;");
        createCustomer.setString(1, firstName);
        createCustomer.setString(2, lastName);
        createCustomer.setInt(3, 0);
        createCustomer.setString(4, phoneNumber);
        createCustomer.setString(5, emailaddress);
        createCustomer.setInt(6, company);
        createCustomer.setBoolean(7, allInclusive);
        createCustomer.setBoolean(8, halfBoard);
        createCustomer.executeUpdate();
        System.out.println("Customer created.\n");

        ResultSet largestID = sqlStatement.executeQuery("SELECT MAX(customer_ID) FROM hotelmanagement.Customer;");
        if (largestID.next()) {
            ID = largestID.getInt(1);
        }
        Customer newCustomer = new Customer(firstName, lastName, 0, phoneNumber, emailaddress, company, allInclusive, halfBoard);
        Menues.customers.put(ID, newCustomer);


    }

    protected static void createRoom() throws SQLException {
        String choice;
        String roomType = "";
        int roomTypeChoice;
        int numBeds;
        int roomCost;
        boolean roomAC = false;
        int capacity;
        int ID = 0;

        while (true) {
            try {
                System.out.println("Which type of room would you like?\n");
                System.out.println("1. Single Room");
                System.out.println("2. Double Room");
                System.out.println("3. Deluxe Single Room");
                System.out.println("4. Deluxe Double Room");
                System.out.println("5. Suite");
                roomTypeChoice = input.nextInt();
                input.nextLine();
                if (roomTypeChoice <= 5 && roomTypeChoice >= 1) {
                    break;
                } else {
                    System.err.println("Please insert a valid number.\n");
                }
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please input a valid number.\n");
            }
        }
        switch (roomTypeChoice) {
            case 1: roomType = "Single room";
            break;
            case 2: roomType = "Double room";
            break;
            case 3: roomType = "Deluxe Single room";
            break;
            case 4: roomType = "Deluxe Double room";
            break;
            case 5: roomType = "Suite";
            break;
        }
        while (true) {
            try {
                System.out.println("How many beds should the room have?");
                numBeds = input.nextInt();
                input.nextLine();
                if (numBeds > 0 && numBeds <= 10) {
                    capacity = (numBeds * 2);
                    break;

                } else {
                    System.err.println("Your input for the number of beds was unreasonable, please look over it again.\n");
                }
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please input a valid number.\n");
            }
        }
        while (true) {
            try {
                System.out.println("How much should the room cost per night?");
                roomCost = input.nextInt();
                input.nextLine();
                if (roomCost > 0) {
                    break;
                } else {
                    System.err.println("Please, the cost of a room can not be negative.\n");
                }
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please input a valid number.\n");
            }
        }
        while (true) {
            try {
                System.out.println("Do you want the room to have air condition? Y/N.");
                choice = input.nextLine();
                if (choice.equalsIgnoreCase("Y")) {
                    roomAC = true;
                    break;
                } else if (choice.equalsIgnoreCase("N")) {
                    break;
                } else {
                    System.err.println("Please input a valid input.\n");
                }
            } catch (Exception e) {
                System.err.println("Please input a valid input.\n");
            }
        }

        PreparedStatement createRoom = sqlStatement.getConnection().prepareStatement("INSERT INTO hotelmanagement.Room SET type = ?, beds = ?, price = ?, AC = ?, capacity = ?;");
        createRoom.setString(1, roomType);
        createRoom.setInt(2, numBeds);
        createRoom.setInt(3, roomCost);
        createRoom.setBoolean(4, roomAC);
        createRoom.setInt(5, capacity);
        createRoom.executeUpdate();
        System.out.println("Room created.\n");

        ResultSet largestID = sqlStatement.executeQuery("SELECT MAX(room_ID) FROM hotelmanagement.room;");
        if (largestID.next()) {
            ID = largestID.getInt(1);
        }
        Room newRoom = new Room(numBeds, roomCost, roomAC, capacity, null, null, null, roomType);
        Menues.rooms.put(ID, newRoom);


    }

    protected static void createFood() throws SQLException {
        String foodName;
        String foodType = "";
        int choice;
        int foodPrice;
        int ID = 0;

        while (true) {
            System.out.print("Please enter the name of your food: ");
            foodName = input.nextLine();
            if (!foodName.isEmpty() && !foodName.isBlank()) {
                break;
            } else {
                System.err.println("Please, a name needs to be more then just whitespace.\n");
            }
        }
        System.out.println("Please choose the type of your food.\n");

        while (true) {
            try {
                System.out.println("1.Breakfast");
                System.out.println("2.Lunch");
                System.out.println("3.Dinner");
                System.out.println("4.Drink");
                choice = input.nextInt();
                input.nextLine();
                if (choice <= 4 && choice >= 1) {
                    break;
                } else {
                    System.err.println("Please insert a valid number.\n");
                }
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please input a valid number.\n");
            }
        }
        switch (choice) {
            case 1: foodType = "Breakfast";
            break;
            case 2: foodType = "Lunch";
            break;
            case 3: foodType = "Dinner";
            break;
            case 4: foodType = "Drink";
            break;
        }
        while (true)
            try {
                System.out.println("Please insert the price");
                foodPrice = input.nextInt();
                input.nextLine();
                if (foodPrice > 0) {
                    break;
                } else {
                    System.err.println("The price must be higher then 0, we do not give food away.\n");
                }
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please chooce a valid input.\n");
            }

        PreparedStatement createFood = sqlStatement.getConnection().prepareStatement("INSERT INTO hotelmanagement.Food SET Price = ?, Type = ?, Name = ?;");
        createFood.setInt(1, foodPrice);
        createFood.setString(2, foodType);
        createFood.setString(3, foodName);
        createFood.executeUpdate();
        System.out.println("Food created.\n");

        ResultSet largestID = sqlStatement.executeQuery("SELECT MAX(food_ID) FROM hotelmanagement.food;");
        if (largestID.next()) {
            ID = largestID.getInt(1);
        }
        Food newFood = new Food(foodPrice, foodName, foodType);
        Menues.foods.put(ID, newFood);

    }

    protected static void update(@NotNull String subject) throws SQLException {
        String localInput;

        showAllOld(subject);
        while (true) {
            System.out.println("Please state the " + subject + "_ID of the " + subject + " you would like to update.");
            localInput = input.nextLine();
            try {
                ResultSet isEmpty = sqlStatement.executeQuery("SELECT " + subject + "_id FROM hotelmanagement." + subject + " WHERE " + subject + "_id LIKE " + localInput + ";");

                if (isEmpty.next()) {
                    break;
                } else {
                    System.err.println("The system could not find any" + subject + " going by that " + subject + " ID, please try a new ID.\n");
                }
            } catch (Exception e) {
                System.err.println("Please use a valid input for the room ID.\n");
            }

        }
        switch (subject) {
            case "customer": updateCustomer(localInput);
            break;
            case "room": updateRoom(localInput);
            break;
            case "food": updateFood(localInput);
            break;
        }

    }

    protected static void updateCustomer(String ID) throws SQLException {
        String firstName;
        String lastName;
        int bill;
        String phoneNumber;
        String emailaddress;
        int company;
        boolean allInclusive;
        boolean halfBoard;
        int idKey;

        while (true) {
            System.out.print("Please enter customers new firstname: ");
            firstName = input.nextLine();

            if (!firstName.isEmpty() && !firstName.isBlank()) {
                break;
            } else {
                System.err.println("Please, a name needs to be more then just whitespace.\n");
            }
        }
        while (true) {
            System.out.print("Please enter customers new lastname: ");
            lastName = input.nextLine();
            if (!lastName.isEmpty() && !lastName.isBlank()) {
                break;
            } else {
                System.err.println("Please, a name needs to be more then just whitespace.\n");
            }
        }
        while (true) {
            try {
                System.out.println("Please insert new balance of bill:");
                bill = input.nextInt();
                input.nextLine();
                if (bill >= 0) {
                    break;
                } else {
                    System.err.println("The bill can not be set to a negative number.\n");
                }
            } catch (Exception e) {
                input.nextLine();
                System.err.println("The balance of the bill must be written in numbers.\n");
            }
        }
        while (true) {
            try {
                System.out.println("Please insert phonenumber with areacode: ");
                phoneNumber = input.nextLine();
                int numberLength = (int) (Math.log10(Double.parseDouble(phoneNumber.substring(1))) + 1);
                if (Double.parseDouble(phoneNumber.substring(1)) > 0 && (numberLength == 12 || numberLength == 13)) {
                    break;
                } else {
                    System.err.println("A phonenumber is made out of 12 or 13 positive integers, please try a valid phonenumber.\n");
                }
            } catch (Exception e) {
                System.err.println("Your number must be made out of integers, no letters allowed.\n");
            }
        }
        System.out.print("Please enter customers new email: ");
        emailaddress = input.nextLine();
        while (true) {
            try {
                System.out.println("Please insert new number of companions:");
                company = input.nextInt();
                input.nextLine();
                break;
            } catch (Exception e) {
                System.err.println("Please enter a valid input.\n");
            }
        }
        while (true) {
            try {
                System.out.println("All inclusive true/false:");
                allInclusive = input.nextBoolean();
                input.nextLine();
                break;
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please input a valid input");
            }
        }
        while (true) {
            try {
                System.out.println("Half Board, true/false:");
                halfBoard = input.nextBoolean();
                input.nextLine();
                break;
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please input a valid input");
            }
        }

        PreparedStatement updateCustomer = sqlStatement.getConnection().prepareStatement("UPDATE hotelmanagement.Customer SET firstname = ?, lastname = ?, bill = ?, phonenumber = ?, emailaddress = ?, company = ?, all_inclusive = ?, half_board = ? WHERE Customer_ID LIKE ?;");
        updateCustomer.setString(1, firstName);
        updateCustomer.setString(2, lastName);
        updateCustomer.setInt(3, bill);
        updateCustomer.setString(4, phoneNumber);
        updateCustomer.setString(5, emailaddress);
        updateCustomer.setInt(6, company);
        updateCustomer.setBoolean(7, allInclusive);
        updateCustomer.setBoolean(8, halfBoard);
        updateCustomer.setString(9, ID);
        updateCustomer.executeUpdate();
        System.out.println("Customer updated.\n");

        idKey = Integer.parseInt(ID);
        Menues.customers.get(idKey).setFirstName(firstName);
        Menues.customers.get(idKey).setLastName(lastName);
        Menues.customers.get(idKey).setBill(bill);
        Menues.customers.get(idKey).setPhoneNumber(phoneNumber);
        Menues.customers.get(idKey).setEmail(emailaddress);
        Menues.customers.get(idKey).setCompany(company);
        Menues.customers.get(idKey).setAllInclusive(allInclusive);
        Menues.customers.get(idKey).setHalfBoard(halfBoard);
    }

    protected static void updateRoom(String ID) throws SQLException {
        String choice;
        String roomType = "";
        int roomTypeChoice;
        int numBeds;
        int roomCost;
        boolean roomAC;
        int capacity;

        while (true) {
            System.out.println("Would you like to update the room Type? Y/N");
            choice = input.nextLine();
            if (choice.equalsIgnoreCase("Y")) {
                System.out.println("Which room type would you like?\n");
                System.out.println("1. Single Room");
                System.out.println("2. Double Room");
                System.out.println("3. Deluxe Single Room");
                System.out.println("4. Deluxe Double Room");
                System.out.println("5. Suite");
                while (true) {
                    try {
                        roomTypeChoice = input.nextInt();
                        input.nextLine();
                        if (roomTypeChoice <= 5 && roomTypeChoice >= 1) {
                            break;
                        } else {
                            System.err.println("Please insert a valid number.");
                        }
                    } catch (Exception e) {
                        input.nextLine();
                        System.err.println("Please input a valid number.");
                    }
                }
                switch (roomTypeChoice) {
                    case 1: roomType = "Single room";
                    break;
                    case 2: roomType = "Double room";
                    break;
                    case 3: roomType = "Deluxe Single room";
                    break;
                    case 4: roomType = "Deluxe Double room";
                    break;
                    case 5: roomType = "Suite";
                    break;
                }
                break;
            } else if (choice.equalsIgnoreCase("N")) {
                break;
            } else {
                System.err.println("Please input a valid awnser.\n");
            }
        }
        while (true) {
            try {
                System.out.println("How many beds should the room have?");
                numBeds = input.nextInt();
                input.nextLine();
                if (numBeds > 0 && numBeds <= 10) {
                    capacity = (numBeds * 2);
                    break;

                } else {
                    System.err.println("Your input for the number of beds was unreasonable, please look over it again.");
                }
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please input a valid number.\n");
            }
        }
        while (true) {
            try {
                System.out.println("How much should the room cost per night?");
                roomCost = input.nextInt();
                input.nextLine();
                if (roomCost > 0) {
                    break;
                } else {
                    System.err.println("Please, the cost of a room can not be negative.\n");
                }
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please input a valid number.\n");
            }
        }
        while (true) {
            try {
                System.out.println("Do you want the room to have air condition? Y/N.");
                choice = input.nextLine();
                if (choice.equalsIgnoreCase("Y")) {
                    roomAC = true;
                    break;
                } else if (choice.equalsIgnoreCase("N")) {
                    roomAC = false;
                    break;
                } else {
                    System.err.println("Please input a valid input.\n");
                }
            } catch (Exception e) {
                System.err.println("Please input a valid input.\n");
            }
        }

        PreparedStatement updateRoom = sqlStatement.getConnection().prepareStatement("UPDATE hotelmanagement.Room SET type = ?, beds = ?, price = ?, AC = ?, capacity = ? WHERE Room_ID LIKE ?;");
        updateRoom.setString(1, roomType);
        updateRoom.setInt(2, numBeds);
        updateRoom.setInt(3, roomCost);
        updateRoom.setBoolean(4, roomAC);
        updateRoom.setInt(5, capacity);
        updateRoom.setString(6, ID);
        updateRoom.executeUpdate();
        System.out.println("Room updated.\n");

        int keyID = Integer.parseInt(ID);
        Menues.rooms.get(keyID).setType(roomType);
        Menues.rooms.get(keyID).setNumbeds(numBeds);
        Menues.rooms.get(keyID).setPrice(roomCost);
        Menues.rooms.get(keyID).setAC(roomAC);
        Menues.rooms.get(keyID).setCapacity(capacity);

    }

    protected static void updateFood(String ID) throws SQLException {
        String foodName;
        String foodType = "";
        int choice;
        int foodPrice;


        while (true) {
            System.out.print("Please enter the new name of your food: ");
            foodName = input.nextLine();
            if (!foodName.isEmpty() && !foodName.isBlank()) {
                break;
            } else {
                System.err.println("Please, a name needs to be more then just whitespace.\n");
            }
        }
        System.out.println("Please choose the new type of your food.\n");
        while (true) {
            try {
                System.out.println("1.Breakfast");
                System.out.println("2.Lunch");
                System.out.println("3.Dinner");
                System.out.println("4.Drink");
                choice = input.nextInt();
                input.nextLine();
                if (choice <= 4 && choice >= 1) {
                    break;
                } else {
                    System.err.println("Please insert a valid number.");
                }
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please input a valid number.");
            }
        }
        switch (choice) {
            case 1: foodType = "Breakfast";
            break;
            case 2: foodType = "Lunch";
            break;
            case 3: foodType = "Dinner";
            break;
            case 4: foodType = "Drink";
            break;
        }
        while (true)
            try {
                System.out.println("Please insert the new price");
                foodPrice = input.nextInt();
                input.nextLine();
                if (foodPrice > 0) {
                    break;
                } else {
                    System.err.println("The price must be higher then 0, we do not give food away.\n");
                }
            } catch (Exception e) {
                input.nextLine();
                System.err.println("Please chooce a valid input.\n");
            }

        PreparedStatement updateFood = sqlStatement.getConnection().prepareStatement("UPDATE hotelmanagement.Food SET Price = ?, Type = ?, Name = ? WHERE Food_ID LIKE ?;");
        updateFood.setInt(1, foodPrice);
        updateFood.setString(2, foodType);
        updateFood.setString(3, foodName);
        updateFood.setString(4, ID);
        updateFood.executeUpdate();
        System.out.println("Food updated.\n");

        int keyID = Integer.parseInt(ID);

        Menues.foods.get(keyID).setPrice(foodPrice);
        Menues.foods.get(keyID).setType(foodType);
        Menues.foods.get(keyID).setName(foodName);


    }

    protected static void delete(String subject) throws SQLException {
        String localInput;


        showAllOld(subject);
        System.out.println("\nPlease state the " + subject + "_ID of the " + subject + " you would like to delete.");
        while (true) {
            localInput = input.nextLine();
            try {
                ResultSet isEmpty = sqlStatement.executeQuery("SELECT " + subject + "_id FROM hotelmanagement." + subject + " WHERE " + subject + "_id LIKE " + localInput + ";");
                if (isEmpty.next()) {
                    if (subject.equalsIgnoreCase("room") || subject.equalsIgnoreCase("food")) {
                        ResultSet isEmptyTwo = sqlStatement.executeQuery("SELECT * FROM room_food WHERE room_id LIKE " + localInput + " OR food_ID LIKE " + localInput + ";");
                        if (isEmptyTwo.next()) {
                            sqlStatement.executeUpdate("DELETE FROM room_food WHERE " + subject + "_id LIKE " + localInput + ";");
                        }
                    }
                    sqlStatement.executeUpdate("DELETE FROM " + subject + " WHERE " + subject + "_id LIKE " + localInput + ";");
                    System.out.println(ANSI_BLUE + "The " + subject + " was deleted\n" + ANSI_RESET);
                    break;
                } else {
                    System.err.println("The system could not find any " + subject + " going by that " + subject + " ID, please try a new ID.");
                }
            } catch (Exception e) {
                System.err.println("Please use a valid input for the Room_ID");
            }
        }
        if (subject.equalsIgnoreCase("room")) {
            Menues.room_food.remove(Integer.parseInt(localInput));
            Menues.rooms.remove(Integer.parseInt(localInput));
        } else if (subject.equalsIgnoreCase("food")) {
            ResultSet roomID = sqlStatement.executeQuery("SELECT room.Room_ID from food\n" +
                                                            "LEFT JOIN room_food ON food.food_ID = room_food.food_ID\n" +
                                                            "LEFT JOIN room ON room_food.room_ID = room.room_ID\n" +
                                                            "WHERE food.Food_ID LIKE \" + localInput + \";");
            int introomID = roomID.getInt(1);
            Menues.room_food.remove(introomID);
            Menues.foods.remove(Integer.parseInt(localInput));
        } else if (subject.equalsIgnoreCase("customer")) {
            Menues.customers.remove(Integer.parseInt(localInput));
        }
    }

    protected static void uploadToDatabase() {
        System.out.println("This operation requiers the database to be emptied beforehand and is mainly used to restore the database in case the information on it is lost or corrupted.");
        System.out.println("Do you still want to proceed? Y/N");
        while (true) {
            String choice = input.nextLine();
            if (choice.equalsIgnoreCase("y")) {
                Menues.customers.forEach((I, Customer) -> {
                            try {
                                PreparedStatement createCustomer = sqlStatement.getConnection().prepareStatement("INSERT INTO hotelmanagement.Customer SET Customer_ID = ?, firstname = ?, lastname = ?, bill = ?, phonenumber = ?, emailaddress = ?, company = ?, all_inclusive = ?, half_board = ? ;");
                                createCustomer.setInt(1, I);
                                createCustomer.setString(2, Customer.getFirstName());
                                createCustomer.setString(3, Customer.getLastName());
                                createCustomer.setInt(4, Customer.getBill());
                                createCustomer.setString(5, Customer.getPhoneNumber());
                                createCustomer.setString(6, Customer.getEmail());
                                createCustomer.setInt(7, Customer.getCompany());
                                createCustomer.setBoolean(8, Customer.isAllInclusive());
                                createCustomer.setBoolean(9, Customer.isHalfBoard());
                                createCustomer.executeUpdate();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );

                Menues.foods.forEach((I, Food) -> {
                            try {
                                PreparedStatement createFood = sqlStatement.getConnection().prepareStatement("INSERT INTO hotelmanagement.Food SET Food_id = ?, Price = ?, Type = ?, Name = ?;");
                                createFood.setInt(1, I);
                                createFood.setInt(2, Food.getPrice());
                                createFood.setString(3, Food.getType());
                                createFood.setString(4, Food.getName());
                                createFood.executeUpdate();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
                Menues.rooms.forEach((I, Room) -> {
                            try {
                                PreparedStatement createRoom = sqlStatement.getConnection().prepareStatement("INSERT INTO hotelmanagement.Room SET Room_id = ?, type = ?, beds = ?, price = ?, AC = ?, capacity = ?, checkin_date = ?, checkout_date = ?, Customer_id = ?;");
                                createRoom.setInt(1, I);
                                createRoom.setString(2, Room.getType());
                                createRoom.setInt(3, Room.getNumbeds());
                                createRoom.setInt(4, Room.getPrice());
                                createRoom.setBoolean(5, Room.isAC());
                                createRoom.setInt(6, Room.getCapacity());
                                if (Room.getCheckIn() == null) {
                                    createRoom.setDate(7, null);
                                } else {
                                    createRoom.setDate(7, Date.valueOf(Room.getCheckIn()));
                                }
                                if (Room.getCheckOut() == null) {
                                    createRoom.setDate(8, null);
                                } else {
                                    createRoom.setDate(8, Date.valueOf(Room.getCheckOut()));
                                }
                                if (Room.getCustomerID() == null) {
                                    createRoom.setNull(9, Types.INTEGER);
                                } else {
                                    createRoom.setInt(9, Room.getCustomerID());
                                }
                                createRoom.executeUpdate();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
                Menues.room_food.forEach((I, Room_food) -> {
                            try {
                                PreparedStatement createRoom_Food = sqlStatement.getConnection().prepareStatement("INSERT INTO hotelmanagement.Room_food SET Room_id = ?, food_id = ?, Order_time = ?");
                                createRoom_Food.setInt(1, I);
                                createRoom_Food.setInt(2, Room_food.getFoodID());
                                createRoom_Food.setDate(3, Date.valueOf(Room_food.getOrderTime()));
                                createRoom_Food.executeUpdate();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
                break;
            } else if (choice.equalsIgnoreCase("n")) {
                System.out.println("Returning to main menu.");
                break;
            } else {
                System.out.println("Please choose a valid input.");
            }
        }
    }

    private static String PadRight(String string) {
        int totalStringLength = 20;
        int charsToPad = totalStringLength - string.length();

        // incase the string is the same length or longer than our maximum lenght
        if (string.length() >= totalStringLength)
            return string;

        StringBuilder stringBuilder = new StringBuilder(string);
        stringBuilder.append(" ".repeat(Math.max(0, charsToPad)));

        return stringBuilder.toString();
    }
}