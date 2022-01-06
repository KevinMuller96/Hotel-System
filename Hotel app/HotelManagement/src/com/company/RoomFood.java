package com.company;
import java.io.Serializable;
import java.time.*;

public class RoomFood implements Serializable {


    private int foodID;
    private LocalDate orderTime;

    public RoomFood(int foodID, LocalDate orderTime) {
        this.foodID = foodID;
        this.orderTime = orderTime;
    }

    @Override
    public String toString() {

        return "\nfoodID: " + foodID +
                "\norderTime: " + orderTime;
    }

    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public LocalDate getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDate orderTime) {
        this.orderTime = orderTime;
    }
}