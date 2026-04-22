package backend;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainApp extends Application {

    private TableView<Transaction> table;
    private PieChart pieChart;
    private Label balanceLabel;
    private ObservableList<Transaction> transactionData = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        DatabaseManager.initializeDatabase();
        loadDataFromDatabase();

        // Layouts
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f172a; -fx-font-family: 'Segoe UI', Arial, sans-serif;");

        // Sidebar (Form)
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Main Content (Dashboard & Table)
        VBox mainContent = createMainContent();
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setTitle("JavaFX Expense Tracker - Capstone");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadDataFromDatabase() {
        transactionData.clear();
        List<Transaction> list = DatabaseManager.getTransactions();
        transactionData.addAll(list);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(25));
        sidebar.setPrefWidth(320);
        sidebar.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 1 0 0;");

        Label title = new Label("TrackPulse");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #3b82f6;");

        Label subtitle = new Label("Add New Transaction");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #f8fafc; -fx-padding: 20 0 10 0;");

        // Form Fields
        TextField descField = new TextField();
        descField.setPromptText("e.g. Groceries");
        descField.setStyle(getGlassStyle());

        TextField amountField = new TextField();
        amountField.setPromptText("e.g. 50.00");
        amountField.setStyle(getGlassStyle());

        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList("Expense", "Income"));
        typeBox.setValue("Expense");
        typeBox.setStyle(getGlassStyle());

        ComboBox<String> categoryBox = new ComboBox<>(FXCollections.observableArrayList("General", "Food", "Transport", "Utilities", "Entertainment", "Shopping", "Salary"));
        categoryBox.setValue("General");
        categoryBox.setStyle(getGlassStyle());

        Button addButton = new Button("Add Transaction");
        addButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        addButton.setMaxWidth(Double.MAX_VALUE);

        addButton.setOnAction(e -> {
            try {
                String text = descField.getText();
                double amount = Double.parseDouble(amountField.getText());
                String type = typeBox.getValue().toLowerCase();
                String category = categoryBox.getValue();
                String date = LocalDate.now().toString();

                if (!text.isEmpty()) {
                    DatabaseManager.addTransaction(text, amount, type, category, date);
                    descField.clear();
                    amountField.clear();
                    refreshDashboard();
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid Amount", "Please enter a valid number for the amount.");
            }
        });

        // Delete Button
        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setOnAction(e -> {
            Transaction selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                DatabaseManager.deleteTransaction(selected.getId());
                refreshDashboard();
            } else {
                showAlert("No Selection", "Please select a transaction to delete.");
            }
        });

        sidebar.getChildren().addAll(
                title, subtitle,
                new Label("Description"), descField,
                new Label("Amount ($)"), amountField,
                new Label("Type"), typeBox,
                new Label("Category"), categoryBox,
                addButton, deleteButton
        );

        // Styling all labels inside sidebar
        for (javafx.scene.Node node : sidebar.getChildren()) {
            if (node instanceof Label && node != title && node != subtitle) {
                node.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
                VBox.setMargin(node, new Insets(5, 0, -15, 0));
            }
        }

        return sidebar;
    }

    private VBox createMainContent() {
        VBox main = new VBox(20);
        main.setPadding(new Insets(25));
        
        // Top Summary Pane
        HBox summaryBox = new HBox(20);
        balanceLabel = new Label("Total Balance: $0.00");
        balanceLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #f8fafc; -fx-padding: 20; -fx-background-color: #1e293b; -fx-background-radius: 12; -fx-border-color: #3b82f6; -fx-border-radius: 12; -fx-border-width: 2;");
        summaryBox.getChildren().add(balanceLabel);

        // Charts & Data
        HBox contentBox = new HBox(20);
        
        // Table
        table = new TableView<>();
        table.setItems(transactionData);
        table.setStyle("-fx-background-color: #1e293b; -fx-control-inner-background: #1e293b; -fx-table-cell-border-color: #334155; -fx-text-background-color: #f8fafc;");
        table.prefWidthProperty().bind(main.widthProperty().multiply(0.55));
        
        TableColumn<Transaction, String> textCol = new TableColumn<>("Description");
        textCol.setCellValueFactory(data -> data.getValue().textProperty());
        textCol.setPrefWidth(180);

        TableColumn<Transaction, Number> amtCol = new TableColumn<>("Amount ($)");
        amtCol.setCellValueFactory(data -> data.getValue().amountProperty());
        amtCol.setPrefWidth(100);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> data.getValue().typeProperty());
        typeCol.setPrefWidth(80);

        TableColumn<Transaction, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(data -> data.getValue().categoryProperty());
        catCol.setPrefWidth(120);

        table.getColumns().addAll(textCol, amtCol, typeCol, catCol);

        // Chart
        pieChart = new PieChart();
        pieChart.setStyle("-fx-text-fill: #f8fafc;");
        pieChart.prefWidthProperty().bind(main.widthProperty().multiply(0.40));
        
        contentBox.getChildren().addAll(table, pieChart);
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        main.getChildren().addAll(summaryBox, contentBox);

        updateDashboardStats();

        return main;
    }

    private void refreshDashboard() {
        loadDataFromDatabase();
        updateDashboardStats();
    }

    private void updateDashboardStats() {
        double balance = 0;
        Map<String, Double> expensesByCategory = new HashMap<>();

        for (Transaction t : transactionData) {
            if ("income".equalsIgnoreCase(t.getType())) {
                balance += t.getAmount();
            } else {
                balance -= t.getAmount();
                expensesByCategory.put(
                    t.getCategory(), 
                    expensesByCategory.getOrDefault(t.getCategory(), 0.0) + t.getAmount()
                );
            }
        }

        balanceLabel.setText(String.format("Total Balance: $%.2f", balance));

        // Update Chart
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey() + " ($" + entry.getValue() + ")", entry.getValue()));
        }
        pieChart.setData(pieData);
    }

    private String getGlassStyle() {
        return "-fx-background-color: #0f172a; -fx-text-fill: #f8fafc; -fx-border-color: #334155; -fx-border-radius: 6; -fx-padding: 8;";
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
