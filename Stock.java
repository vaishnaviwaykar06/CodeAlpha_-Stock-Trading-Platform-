import java.util.Random;

/**
 * Represents a single stock available on the market.
 * Keeps track of its symbol, name, and current price.
 */
public class Stock {
    private String symbol;
    private String name;
    private double price;
    private static final Random random = new Random();

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        // Don't let price go negative or to zero
        this.price = Math.max(0.01, price);
    }

    /**
     * randomly changing the price up or down by a small percentage (-5% to +5%).
     */
    public void fluctuatePrice() {
        double changePercent = (random.nextDouble() * 10 - 5) / 100.0; // -5% to +5%
        double newPrice = price + (price * changePercent);
        setPrice(newPrice);
    }

    @Override
    public String toString() {
        return String.format("%-6s %-20s $%.2f", symbol, name, price);
    }
}
