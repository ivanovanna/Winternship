import java.util.Map;

class Player {
    private String id;
    private long balance;
    private int betsPlaced;
    private int wins;

    public Player(String id) {
        this.id = id;
        this.balance = 0;
        this.betsPlaced = 0;
        this.wins = 0;
    }

    public String getId() {
        return id;
    }

    public long getBalance() {
        return balance;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public boolean withdraw(int amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public void placeBet(int betAmount, String matchResult, String chosenSide, Map<String, Match> matches) {
        betsPlaced++;
        Match match = matches.get(matchResult);
        if (match != null) {
            if (chosenSide.equals("A") && match.getResult().equals("A")) {
                long winnings = (long) (betAmount * match.getRateSideA());
                balance += winnings;
                wins++;
            } else if (chosenSide.equals("B") && match.getResult().equals("B")) {
                long winnings = (long) (betAmount * match.getRateSideB());
                balance += winnings;
                wins++;
            }
        }
    }
    public double calculateWinRate() {
        if (betsPlaced == 0) return 0.0;
        return (double) wins / betsPlaced;
    }
    }

