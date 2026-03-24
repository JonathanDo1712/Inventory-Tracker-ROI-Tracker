package com.example.inventory_and_roi_system.models;

//Item of Inventory Tracker
//Implementation of currently stocked items
//These items allow for live updates to quantity and sale prices

public class ActiveProduct extends Product{

    //Constructor
    public ActiveProduct(String name, int quantity, double pricePer, double salePrice){
        super(name, quantity, pricePer, salePrice);
    }

    //Setters
    //Manually update stock levels and prices (the only thing that can change for a product)
    public void setQuantity(int quantity){this.quantity=quantity;}
    public void setSalePrice(double salePrice){this.salePrice=salePrice;}

    //Simulator for multithreading
    //randomly adjust sales price between -2% and +2%
    public void simulatePriceFluctuation() {
        //generate number between 0.98 and 1.02
        double changePercent = 1 + (Math.random() * 0.04 - 0.02);
        this.salePrice *= changePercent;
    }

}
