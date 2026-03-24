package com.example.inventory_and_roi_system.models;
import java.util.List;

//Summarize SaleRecord and ActiveProduct Chart in another table

public class ValueChart {
    //Accepts any list that comes inherits from product class
    //Return total investment cost across all inventory or sale record
    public double getOverallCost(List<? extends Product> items){
        //convert items into a stream, extract doubles of total cost and return the total
        return items.stream().mapToDouble(Product::getTotalCost).sum();
    }
    //Return total revenue across all inventory or sale record
    public double getOverallSale(List<? extends Product>items){
        return items.stream().mapToDouble(Product::getTotalSale).sum();
    }
    //Return total net profit across all inventory or sale record
    public double getOverallProfit(List<? extends Product>items){
        return getOverallSale(items) - getOverallCost(items);
    }
    //Return ROI across all inventory or sale record
    public double getOverallProfitPercentage(List<? extends Product>items){
        // so we can't divide by 0 and break code
        if (getOverallCost(items) == 0){
            return 0.0;
        }
        return (getOverallProfit(items)/getOverallCost(items))*100;
    }

}
