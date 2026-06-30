import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Tracks users cash balance, what stocks they hold, how much they
 * originally spent on each (for profit/loss calculation), and their
 * full transaction history.
 */
public class User {
    private String username;
    private double cashBalance;

    // number of shares owned
    private Map<String, Integer> holdings;

    // total amount originally spent acquiring current shares
    // (used to calculate profit/loss against current market value)
    private Map<String, Double> costBasis;

    private List<Transaction> transactionHistory;

    public User(String username, double startingCash) {
        this.username = username;
        this.cashBalance = startingCash;
        this.holdings = new LinkedHashMap<>();
        this.costBasis = new LinkedHashMap<>();
        this.transactionHistory = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public Map<String, Integer> getHoldings() {
        return holdings;
    }

    public Map<String, Double> getCostBasis() {
        return costBasis;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public int getSharesOwned(String symbol) {
        return holdings.getOrDefault(symbol, 0);
    }

    /**
     * Applies a buy: deducts cash, adds shares, updates cost basis,
     * and logs the transaction.
     */
    public void applyBuy(String symbol, int quantity, double pricePerShare) {
        double totalCost = quantity * pricePerShare;
        cashBalance -= totalCost;

        holdings.put(symbol, getSharesOwned(symbol) + quantity);
        costBasis.put(symbol, costBasis.getOrDefault(symbol, 0.0) + totalCost);

        transactionHistory.add(new Transaction("BUY", symbol, quantity, pricePerShare));
    }

    /**
     * Applies a sell: adds cash, removes shares, reduces cost basis
     * proportionally, and logs the transaction.
     */
    public void applySell(String symbol, int quantity, double pricePerShare) {
        double totalProceeds = quantity * pricePerShare;
        cashBalance += totalProceeds;

        int remainingShares = getSharesOwned(symbol) - quantity;

        // Reduce the cost basis proportionally to how many shares are being sold
        double avgCostPerShare = costBasis.getOrDefault(symbol, 0.0) / Math.max(1, getSharesOwned(symbol));
        double costBasisRemoved = avgCostPerShare * quantity;
        costBasis.put(symbol, Math.max(0, costBasis.getOrDefault(symbol, 0.0) - costBasisRemoved));

        if (remainingShares <= 0) {
            holdings.remove(symbol);
            costBasis.remove(symbol);
        } else {
            holdings.put(symbol, remainingShares);
        }

        transactionHistory.add(new Transaction("SELL", symbol, quantity, pricePerShare));
    }

    public void addCash(double amount) {
        cashBalance += amount;
    }

    public void deductCash(double amount) {
        cashBalance -= amount;
    }
}
