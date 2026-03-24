package com.example.inventory_and_roi_system.controllers;

import com.example.inventory_and_roi_system.models.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//Main Controller
//Coordinates UI updates, file I/O triggers, and multi-thread price simulation

public class HelloController {

    //UI Components
    //Inventory Table
    @FXML private TableView<ActiveProduct> inventoryTable;
    @FXML private TableColumn<ActiveProduct, String> nameCol;
    @FXML private TableColumn<ActiveProduct, Integer> qtyCol;
    @FXML private TableColumn<ActiveProduct, Double> costCol, totalCostCol, salePriceCol, totalSaleCol, profitCol, roiCol;

    //Value Chart Summary for Inventory table
    @FXML private TableView<ValueChartSummary> valueTable;
    @FXML private TableColumn<ValueChartSummary, Double> totalPortfolioCostCol, totalPortfolioSaleCol, totalPortfolioProfitCol, totalPortfolioROICol;

    @FXML private TextField nameInput, qtyInput, costInput, saleInput;

    //Sales Record Table
    @FXML private TableView<SaleRecord> saleLogTable;
    @FXML private TableColumn<SaleRecord, String> saleDateCol, saleNameCol;
    @FXML private TableColumn<SaleRecord, Integer> saleQtyCol;
    @FXML private TableColumn<SaleRecord, Double> saleCostPerCol, saleTotalCostCol, salePricePerCol, saleTotalSaleCol, saleActualProfitCol, saleROICol;

    //Value Chart Summary for Sale Record Table
    @FXML private TableView<SaleSummary> saleSummaryTable;
    @FXML private TableColumn<SaleSummary, Double> totalSoldCostCol, totalSoldRevenueCol, totalActualProfitCol, totalActualROICol;

    @FXML private ToggleButton simToggle; // The On/Off Switch

    private ObservableList<SaleRecord> saleDataHistory;
    private ScheduledExecutorService scheduler; //Manges multi thread background simulator

    //Sets up table bindings and starts background services
    public void initialize() {
        // 1. Column Setup
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        setupCurrencyColumn(costCol, "pricePer");
        setupCurrencyColumn(totalCostCol, "totalCost");
        setupCurrencyColumn(salePriceCol, "salePrice");
        setupCurrencyColumn(totalSaleCol, "totalSale");
        setupCurrencyColumn(profitCol, "expectedProfit");
        setupROIColumn(roiCol, "profitPercentage");

        setupCurrencyColumn(totalPortfolioCostCol, "totalCost");
        setupCurrencyColumn(totalPortfolioSaleCol, "totalSale");
        setupCurrencyColumn(totalPortfolioProfitCol, "totalProfit");
        setupROIColumn(totalPortfolioROICol, "overallROI");

        saleDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        saleNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        saleQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        setupCurrencyColumn(saleCostPerCol, "pricePer");
        setupCurrencyColumn(saleTotalCostCol, "totalCost");
        setupCurrencyColumn(salePricePerCol, "salePrice");
        setupCurrencyColumn(saleTotalSaleCol, "totalSale");
        setupCurrencyColumn(saleActualProfitCol, "expectedProfit");
        setupROIColumn(saleROICol, "profitPercentage");

        setupCurrencyColumn(totalSoldCostCol, "totalCost");
        setupCurrencyColumn(totalSoldRevenueCol, "totalSale");
        setupCurrencyColumn(totalActualProfitCol, "totalProfit");
        setupROIColumn(totalActualROICol, "overallROI");

        // 2. Load Data from CSV via DataManager
        inventoryTable.setItems(DataManager.loadInventory());
        saleDataHistory = DataManager.loadSales();
        saleLogTable.setItems(saleDataHistory);

        refreshActiveSummary(inventoryTable.getItems());
        refreshSaleSummary(saleDataHistory);

        // 3. Start Multi-threading Simulation
        startPriceSimulation();
    }

    //separate thread to change prices every 3 seconds
    private void startPriceSimulation() {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            // Check if user has turned simulation ON(ToggleButton)
            if (simToggle != null && simToggle.isSelected()) {
                try {
                    List<ActiveProduct> currentItems = new ArrayList<>(inventoryTable.getItems());
                    for (ActiveProduct p : currentItems) {
                        p.simulatePriceFluctuation();
                    }

                    Platform.runLater(() -> {
                        inventoryTable.refresh();
                        refreshActiveSummary(inventoryTable.getItems());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3, 3, TimeUnit.SECONDS);
    }

    //Handle double clicks events on inventory table
    @FXML
    private void handleToggleSimulation() {
        if (simToggle.isSelected()) {
            simToggle.setText("Simulation: LIVE");
            simToggle.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            simToggle.setText("Simulation: OFF");
            simToggle.setStyle("-fx-background-color: #7d7d7d; -fx-text-fill: white; -fx-font-weight: bold;");
        }
    }

    //Moving Active inventory to sales record
    @FXML
    private void handleTableClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            ActiveProduct selected = inventoryTable.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            TablePosition pos = inventoryTable.getSelectionModel().getSelectedCells().get(0);
            TableColumn col = pos.getTableColumn();
            if (col == nameCol) processSale(selected);
            else if (col == qtyCol) editQuantity(selected);
            else if (col == salePriceCol) editSalePrice(selected);
        }
    }

    //Formats double values as Currency ($10.00)
    private void processSale(ActiveProduct p) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Process Sale");
        dialog.showAndWait().ifPresent(val -> {
            try {
                int soldQty = Integer.parseInt(val);
                if (soldQty > 0 && soldQty <= p.getQuantity()) {
                    saleDataHistory.add(new SaleRecord(p.getName(), soldQty, p.getPricePer(), p.getSalePrice(), LocalDate.now().toString()));
                    p.setQuantity(p.getQuantity() - soldQty);
                    if (p.getQuantity() <= 0) inventoryTable.getItems().remove(p);
                    finalizeChanges();
                    refreshSaleSummary(saleDataHistory);
                }
            } catch (Exception e) { showNumError(); }
        });
    }

    //Open dialog to update quantity of existing item
    //If quantity is set to 0 remove item from inventory
    private void editQuantity(ActiveProduct p) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(p.getQuantity()));
        dialog.showAndWait().ifPresent(val -> {
            try {
                p.setQuantity(Integer.parseInt(val));
                if (p.getQuantity() <= 0) inventoryTable.getItems().remove(p);
                finalizeChanges();
            } catch (Exception e) { showNumError(); }
        });
    }

    //Open dialog manually updating sale price of item
    private void editSalePrice(ActiveProduct p) {
        TextInputDialog dialog = new TextInputDialog(String.format("%.2f", p.getSalePrice()));
        dialog.showAndWait().ifPresent(val -> {
            try {
                p.setSalePrice(Double.parseDouble(val));
                finalizeChanges();
            } catch (Exception e) { showNumError(); }
        });
    }

    //Util refresh table visual and update summary total
    private void finalizeChanges() {
        inventoryTable.refresh();
        refreshActiveSummary(inventoryTable.getItems());
    }

    //error pop up window for invalid input
    private void showNumError() {
        new Alert(Alert.AlertType.ERROR, "Invalid number input.").show();
    }

    //Event handler for Add button
    //take input field and create a new ActiveProduct item
    @FXML
    private void onAddClick() {
        try {
            inventoryTable.getItems().add(new ActiveProduct(nameInput.getText(), Integer.parseInt(qtyInput.getText()), Double.parseDouble(costInput.getText()), Double.parseDouble(saleInput.getText())));
            nameInput.clear(); qtyInput.clear(); costInput.clear(); saleInput.clear();
            refreshActiveSummary(inventoryTable.getItems());
        } catch (Exception e) { showNumError(); }
    }

    //Event handler for save button
    @FXML
    private void onSaveClick() {
        DataManager.saveAll(inventoryTable.getItems(), saleDataHistory);
        new Alert(Alert.AlertType.INFORMATION, "Data Saved to CSV.").show();
    }

    //Recalculates value chart for active inventory
    private void refreshActiveSummary(ObservableList<ActiveProduct> data) {
        double cost = 0, sale = 0;
        for (ActiveProduct p : data) { cost += p.getTotalCost(); sale += p.getTotalSale(); }
        double profit = sale - cost;
        double roi = (cost == 0) ? 0 : (profit / cost) * 100;
        valueTable.setItems(FXCollections.observableArrayList(new ValueChartSummary(cost, sale, profit, roi)));
    }

    //Recalculates value chart for sales history
    private void refreshSaleSummary(ObservableList<SaleRecord> data) {
        double cost = 0, rev = 0;
        for (SaleRecord s : data) { cost += s.getTotalCost(); rev += s.getTotalSale(); }
        double profit = rev - cost;
        double roi = (cost == 0) ? 0 : (profit / cost) * 100;
        saleSummaryTable.setItems(FXCollections.observableArrayList(new SaleSummary(cost, rev, profit, roi)));
    }

    //display numbers as currency($0.00)
    private <T> void setupCurrencyColumn(TableColumn<T, Double> col, String prop) {
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(String.format("$%.2f", item));
            }
        });
    }

    //display numbers as whole number percentages (0%)
    private <T> void setupROIColumn(TableColumn<T, Double> col, String prop) {
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(Math.round(item) + "%");
            }
        });
    }

    // Summary Classes
    public static class ValueChartSummary {
        private final double totalCost, totalSale, totalProfit, overallROI;
        public ValueChartSummary(double c, double s, double p, double r) { this.totalCost = c; this.totalSale = s; this.totalProfit = p; this.overallROI = r; }
        public double getTotalCost() { return totalCost; }
        public double getTotalSale() { return totalSale; }
        public double getTotalProfit() { return totalProfit; }
        public double getOverallROI() { return overallROI; }
    }

    public static class SaleSummary {
        private final double totalCost, totalSale, totalProfit, overallROI;
        public SaleSummary(double c, double s, double p, double r) { this.totalCost = c; this.totalSale = s; this.totalProfit = p; this.overallROI = r; }
        public double getTotalCost() { return totalCost; }
        public double getTotalSale() { return totalSale; }
        public double getTotalProfit() { return totalProfit; }
        public double getOverallROI() { return overallROI; }
    }
}