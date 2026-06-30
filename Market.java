import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collection;

/**
 * Represents the stock market
 */
public class Market {
    private Map<String, Stock> stocks;

    public Market() {
        stocks = new LinkedHashMap<>();
        loadDefaultStocks();
    }

    // sample stocks
    private void loadDefaultStocks() {
        addStock(new Stock("AAPL", "Apple Inc.", 190.00));
        addStock(new Stock("TSLA", "Tesla Inc.", 250.00));
        addStock(new Stock("GOOG", "Alphabet Inc.", 165.00));
        addStock(new Stock("AMZN", "Amazon.com Inc.", 180.00));
        addStock(new Stock("MSFT", "Microsoft Corp.", 420.00));
        addStock(new Stock("NFLX", "Netflix Inc.", 640.00));
    }

    public void addStock(Stock stock) {
        stocks.put(stock.getSymbol(), stock);
    }

    public Stock getStock(String symbol) {
        return stocks.get(symbol.toUpperCase());
    }

    public boolean hasStock(String symbol) {
        return stocks.containsKey(symbol.toUpperCase());
    }

    public Collection<Stock> getAllStocks() {
        return stocks.values();
    }

    //   every stock's price slightly
    public void simulateMarketMovement() {
        for (Stock stock : stocks.values()) {
            stock.fluctuatePrice();
        }
    }

    public void displayMarket() {
        System.out.println("\n===== MARKET DATA =====");
        System.out.printf("%-6s %-20s %s%n", "SYM", "NAME", "PRICE");
        System.out.println("-----------------------------------");
        for (Stock stock : stocks.values()) {
            System.out.println(stock);
        }
        System.out.println("------------------------------------");
    }
}
