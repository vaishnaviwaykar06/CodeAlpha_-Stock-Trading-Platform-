import java.util.Scanner;

/**
 * Entry point for the Stock Trading Platform.
 * Presents a console menu for the user to view market data,
 * buy/sell stocks, check their portfolio, and exit (saving data).
 */
public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   WELCOME TO THE STOCK TRADING PLATFORM");
        System.out.println("==========================================");

        System.out.print("Enter your username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            username = "guest";
        }

        TradingPlatform platform = new TradingPlatform(username);

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    platform.getMarket().simulateMarketMovement();
                    platform.getMarket().displayMarket();
                    break;
                case "2":
                    handleBuy(platform);
                    break;
                case "3":
                    handleSell(platform);
                    break;
                case "4":
                    platform.displayPortfolio();
                    break;
                case "5":
                    platform.displayTransactionHistory();
                    break;
                case "6":
                    platform.saveUser();
                    System.out.println("Goodbye, " + username + "!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select a number from 1 to 6.");
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n---------- MAIN MENU -----------");
        System.out.println("1. View Market Data");
        System.out.println("2. Buy Stock");
        System.out.println("3. Sell Stock");
        System.out.println("4. View Portfolio");
        System.out.println("5. View Transaction History");
        System.out.println("6. Save & Exit");
        System.out.print("Enter your choice: ");
    }

    private static void handleBuy(TradingPlatform platform) {
        platform.getMarket().displayMarket();

        System.out.print("Enter stock symbol to buy: ");
        String symbol = scanner.nextLine().trim();

        System.out.print("Enter quantity to buy: ");
        int quantity = readPositiveInt();

        if (quantity == -1) {
            System.out.println("Invalid quantity entered.");
            return;
        }

        String result = platform.buyStock(symbol, quantity);
        System.out.println(result);
    }

    private static void handleSell(TradingPlatform platform) {
        System.out.println("\nYour current holdings:");
        if (platform.getUser().getHoldings().isEmpty()) {
            System.out.println("You don't own any stocks yet.");
            return;
        }
        platform.displayPortfolio();

        System.out.print("Enter stock symbol to sell: ");
        String symbol = scanner.nextLine().trim();

        System.out.print("Enter quantity to sell: ");
        int quantity = readPositiveInt();

        if (quantity == -1) {
            System.out.println("Invalid quantity entered.");
            return;
        }

        String result = platform.sellStock(symbol, quantity);
        System.out.println(result);
    }

    // Safely reads an integer from the user; returns -1 if invalid
    private static int readPositiveInt() {
        try {
            int value = Integer.parseInt(scanner.nextLine().trim());
            return value > 0 ? value : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
