package com.example.inventory_and_roi_system.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;

//Handles File I/O
//Responsible for saving and loading Inventory and Sale Record Chart

public class DataManager {
    //File path for storage
    private static final String INV_FILE = "inventory.csv";
    private static final String SALE_FILE = "sales.csv";

    //Writing inventory and sale record table into CSV format
    //parameter: active inventory list and sale record list
    public static void saveAll(ObservableList<ActiveProduct> inv, ObservableList<SaleRecord> sales) {
        try (PrintWriter pw1 = new PrintWriter(new FileWriter(INV_FILE));
             PrintWriter pw2 = new PrintWriter(new FileWriter(SALE_FILE))) {
            for (ActiveProduct p : inv) {
                pw1.println(p.getName() + "," + p.getQuantity() + "," + p.getPricePer() + "," + p.getSalePrice());
            }
            for (SaleRecord s : sales) {
                pw2.println(s.getDate() + "," + s.getName() + "," + s.getQuantity() + "," + s.getPricePer() + "," + s.getSalePrice());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    //Reads inventory.csv and reconstructs ActiveProduct objects
    public static ObservableList<ActiveProduct> loadInventory() {
        ObservableList<ActiveProduct> list = FXCollections.observableArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader(INV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                list.add(new ActiveProduct(p[0], Integer.parseInt(p[1]), Double.parseDouble(p[2]), Double.parseDouble(p[3])));
            }
        } catch (Exception e) {} // File new or empty
        return list;
    }

    //Reads sales.csv and reconstructs SaleRecord objects
    public static ObservableList<SaleRecord> loadSales() {
        ObservableList<SaleRecord> list = FXCollections.observableArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader(SALE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                list.add(new SaleRecord(p[1], Integer.parseInt(p[2]), Double.parseDouble(p[3]), Double.parseDouble(p[4]), p[0]));
            }
        } catch (Exception e) {} // File new or empty
        return list;
    }
}