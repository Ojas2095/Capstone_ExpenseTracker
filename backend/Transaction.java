package backend;

import javafx.beans.property.*;

public class Transaction {
    private final IntegerProperty id;
    private final StringProperty text;
    private final DoubleProperty amount;
    private final StringProperty type;
    private final StringProperty category;

    public Transaction(Integer id, String text, Double amount, String type, String category) {
        this.id = new SimpleIntegerProperty(id);
        this.text = new SimpleStringProperty(text);
        this.amount = new SimpleDoubleProperty(amount);
        this.type = new SimpleStringProperty(type);
        this.category = new SimpleStringProperty(category);
    }
    
    public int getId() { return id.get(); }
    public String getText() { return text.get(); }
    public double getAmount() { return amount.get(); }
    public String getType() { return type.get(); }
    public String getCategory() { return category.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty textProperty() { return text; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty typeProperty() { return type; }
    public StringProperty categoryProperty() { return category; }
}
