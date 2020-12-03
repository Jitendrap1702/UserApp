package com.example.userapp.order;

import com.example.userapp.model.CartItem;
import com.google.firebase.Timestamp;

import java.util.List;

public class Order {
    public int status;
    public String orderId;
    public List<CartItem> items;
    public int subTotal;
    public Timestamp orderPacedTo;

//    public int getStatus{
//        return status;
//    }
    public void setStatus(int status){
        this.status = status;
    }

    public static class orderStatus{
        public static final int PLACED = 1;
    }
}
