package com.example.inventory_and_roi_system.models;

//Item of Sale Record
//Snapshot of complete transaction (cannot be changed)

public class SaleRecord extends Product {
    String date;

    //Constructor
    public SaleRecord(String name, int quantity, double pricePer, double salePrice, String date){
        super(name, quantity, pricePer, salePrice);
        this.date = date;
    }

    //Getter
    public String getDate(){return date;}

}
