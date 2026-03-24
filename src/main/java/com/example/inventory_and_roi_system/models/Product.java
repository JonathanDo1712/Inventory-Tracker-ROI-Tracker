package com.example.inventory_and_roi_system.models;

//Abstract Class
//Foundation of all inventory items(saleRecord, activeProduct)
//Provides financial calculations

public abstract class Product {
    protected String name;
    protected int quantity;
    protected double pricePer; //Price to get item
    protected double salePrice; //Price item is sold for

    //Constructor
    public Product(String name, int quantity, double pricePer, double salePrice){
        this.name = name;
        this.quantity = quantity;
        this.pricePer = pricePer;
        this.salePrice = salePrice;
    }

    //Financial Calculation
    //Return total investment cost(quantity * cost per unit)
    public double getTotalCost(){return this.quantity*this.pricePer;}
    //Return total potential revenue
    public double getTotalSale(){return this.quantity*this.salePrice;}
   //return total net profit
    public double getExpectedProfit(){return getTotalSale()-getTotalCost();}
    //return ROI as a percentage
    public double getProfitPercentage(){
        //Prevents us from diving by 0
        if (this.pricePer == 0) {
            return 0.0;
        }
        return ((this.salePrice - this.pricePer) / this.pricePer) * 100;
    }

    //Getters
    public String getName(){return name;}
    public int getQuantity(){return quantity;}
    public double getPricePer(){return pricePer;}
    public double getSalePrice(){return salePrice;}

}
