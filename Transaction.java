import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single buy or sell transaction made by a user.
 */
public class Transaction {
    private String type;       // "BUY" or "SELL"
    private String symbol;
    private int quantity;
    private double pricePerShare;
    private LocalDateTime timestamp;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Transaction(String type, String symbol, int quantity, double pricePerShare) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor used when loading a transaction back from a saved file
    public Transaction(String type, String symbol, int quantity, double pricePerShare, LocalDateTime timestamp) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerShare() {
        return pricePerShare;
    }

    public double getTotalValue() {
        return quantity * pricePerShare;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    
    public String toCsv() {
        return type + "," + symbol + "," + quantity + "," + pricePerShare + "," + getFormattedTimestamp();
    }

    // Rebuilds a Transaction object 
    public static Transaction fromCsv(String csvLine) {
        String[] parts = csvLine.split(",");
        String type = parts[0];
        String symbol = parts[1];
        int quantity = Integer.parseInt(parts[2]);
        double price = Double.parseDouble(parts[3]);
        LocalDateTime timestamp = LocalDateTime.parse(parts[4], FORMATTER);
        return new Transaction(type, symbol, quantity, price, timestamp);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-4s %4d x %-6s @ $%.2f = $%.2f",
                getFormattedTimestamp(), type, quantity, symbol, pricePerShare, getTotalValue());
    }
}
