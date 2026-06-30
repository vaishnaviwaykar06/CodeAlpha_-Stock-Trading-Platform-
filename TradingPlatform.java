import java.io.*;
import java.util.Map;

/**
 * Ties together the Market and User, handles buy/sell logic,
 * portfolio performance display, and saving/loading data to file.
 */
public class TradingPlatform {
    private Market market;
    private User user;

    private static final String DATA_FOLDER = "data";
    private static final double DEFAULT_STARTING_CASH = 10000.00;

    public TradingPlatform(String username) {
        this.market = new Market();
        this.user = loadUser(username);
    }

    public Market getMarket() {
        return market;
    }

    public User getUser() {
        return user;
    }

    /**
     * Attempts to buy shares of a stock.
     * Returns a result message describing success or failure.
     */
    public String buyStock(String symbol, int quantity) {
        symbol = symbol.toUpperCase();

        if (quantity <= 0) {
            return "Quantity must be greater than zero.";
        }
        if (!market.hasStock(symbol)) {
            return "Stock symbol '" + symbol + "' not found on the market.";
        }

        Stock stock = market.getStock(symbol);
        double totalCost = stock.getPrice() * quantity;

        if (totalCost > user.getCashBalance()) {
            return String.format(
                "Insufficient funds. Need $%.2f but only have $%.2f available.",
                totalCost, user.getCashBalance());
        }

        user.applyBuy(symbol, quantity, stock.getPrice());
        return String.format("Bought %d share(s) of %s at $%.2f each. Total: $%.2f",
                quantity, symbol, stock.getPrice(), totalCost);
    }

    /**
     * Attempts to sell shares of a stock.
     * Returns a result message  success or failure.
     */
    public String sellStock(String symbol, int quantity) {
        symbol = symbol.toUpperCase();

        if (quantity <= 0) {
            return "Quantity must be greater than zero.";
        }
        if (!market.hasStock(symbol)) {
            return "Stock symbol '" + symbol + "' not found on the market.";
        }

        int sharesOwned = user.getSharesOwned(symbol);
        if (quantity > sharesOwned) {
            return String.format("You only own %d share(s) of %s. Cannot sell %d.",
                    sharesOwned, symbol, quantity);
        }

        Stock stock = market.getStock(symbol);
        double totalProceeds = stock.getPrice() * quantity;

        user.applySell(symbol, quantity, stock.getPrice());
        return String.format("Sold %d share(s) of %s at $%.2f each. Total: $%.2f",
                quantity, symbol, stock.getPrice(), totalProceeds);
    }

    /**
     * Calculates the total current market value of the user's holdings
     */
    public double getHoldingsValue() {
        double total = 0.0;
        for (Map.Entry<String, Integer> entry : user.getHoldings().entrySet()) {
            Stock stock = market.getStock(entry.getKey());
            if (stock != null) {
                total += stock.getPrice() * entry.getValue();
            }
        }
        return total;
    }

    /**
     * Calculates the total portfolio value = cash + current value of holdings.
     */
    public double getTotalPortfolioValue() {
        return user.getCashBalance() + getHoldingsValue();
    }

    public void displayPortfolio() {
        System.out.println("\n===== PORTFOLIO: " + user.getUsername() + " =====");
        System.out.printf("Cash balance: $%.2f%n", user.getCashBalance());

        if (user.getHoldings().isEmpty()) {
            System.out.println("No stocks currently held.");
        } else {
            System.out.println("\nHoldings:");
            System.out.printf("%-6s %-10s %-12s %-12s %-12s %s%n",
                    "SYM", "QTY", "AVG COST", "CURRENT", "VALUE", "PROFIT/LOSS");
            System.out.println("---------------------------------------------------------------");

            for (Map.Entry<String, Integer> entry : user.getHoldings().entrySet()) {
                String symbol = entry.getKey();
                int qty = entry.getValue();
                Stock stock = market.getStock(symbol);
                double currentPrice = stock != null ? stock.getPrice() : 0.0;
                double currentValue = currentPrice * qty;
                double costBasis = user.getCostBasis().getOrDefault(symbol, 0.0);
                double avgCost = qty > 0 ? costBasis / qty : 0.0;
                double profitLoss = currentValue - costBasis;

                System.out.printf("%-6s %-10d $%-11.2f $%-11.2f $%-11.2f %s$%.2f%n",
                        symbol, qty, avgCost, currentPrice, currentValue,
                        profitLoss >= 0 ? "+" : "-", Math.abs(profitLoss));
            }
        }

        System.out.println("---------------------------------------------------------------");
        System.out.printf("Holdings value: $%.2f%n", getHoldingsValue());
        System.out.printf("Total portfolio value (cash + holdings): $%.2f%n", getTotalPortfolioValue());
    }

    public void displayTransactionHistory() {
        System.out.println("\n===== TRANSACTION HISTORY: " + user.getUsername() + " =====");
        if (user.getTransactionHistory().isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        for (Transaction t : user.getTransactionHistory()) {
            System.out.println(t);
        }
    }

    // FILE I/O: SAVE / LOAD 

    private String getUserFilePath(String username) {
        return DATA_FOLDER + File.separator + username + ".csv";
    }

    /**
     * Loads a user's saved data from file if it exists.
     * Otherwise creates a brand-new user with default starting cash.
     */
    private User loadUser(String username) {
        File file = new File(getUserFilePath(username));

        if (!file.exists()) {
            System.out.println("No saved data found for '" + username + "'. Starting fresh with $"
                    + DEFAULT_STARTING_CASH);
            return new User(username, DEFAULT_STARTING_CASH);
        }

        User loadedUser = new User(username, 0.0); // cash will be overwritten below

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // first line: cash balance
            if (line != null) {
                loadedUser.addCash(Double.parseDouble(line.trim()));
            }

            String section = "";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equals("HOLDINGS")) {
                    section = "HOLDINGS";
                    continue;
                } else if (line.equals("TRANSACTIONS")) {
                    section = "TRANSACTIONS";
                    continue;
                }

                if (section.equals("HOLDINGS")) {
                    // format: symbol,quantity,costBasis
                    String[] parts = line.split(",");
                    String symbol = parts[0];
                    int qty = Integer.parseInt(parts[1]);
                    double cost = Double.parseDouble(parts[2]);
                    loadedUser.getHoldings().put(symbol, qty);
                    loadedUser.getCostBasis().put(symbol, cost);
                } else if (section.equals("TRANSACTIONS")) {
                    loadedUser.getTransactionHistory().add(Transaction.fromCsv(line));
                }
            }

            System.out.println("Welcome back, " + username + "! Your portfolio has been loaded.");
        } catch (IOException e) {
            System.out.println("Error loading saved data: " + e.getMessage());
            System.out.println("Starting fresh with $" + DEFAULT_STARTING_CASH);
            return new User(username, DEFAULT_STARTING_CASH);
        }

        return loadedUser;
    }

    /**
     * Saves the current user's cash, holdings, and transaction history to file.
     */
    public void saveUser() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(getUserFilePath(user.getUsername()));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(String.valueOf(user.getCashBalance()));
            writer.newLine();

            writer.write("HOLDINGS");
            writer.newLine();
            for (Map.Entry<String, Integer> entry : user.getHoldings().entrySet()) {
                String symbol = entry.getKey();
                int qty = entry.getValue();
                double cost = user.getCostBasis().getOrDefault(symbol, 0.0);
                writer.write(symbol + "," + qty + "," + cost);
                writer.newLine();
            }

            writer.write("TRANSACTIONS");
            writer.newLine();
            for (Transaction t : user.getTransactionHistory()) {
                writer.write(t.toCsv());
                writer.newLine();
            }

            System.out.println("Portfolio saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
}
