
import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;

public class Main {
    public static void main(String[] args) {
        Map<String, Player> players = new HashMap<>();
        Map<String, Match> matches = new HashMap<>();
        Map<String, Integer> casinoBalanceChanges = new HashMap<>();

        List<String> playerData = readFile("resource/player_data.txt");
        List<String> matchData = readFile("resource/match_data.txt");

        List<String> illegitimatePlayers = new ArrayList<>();

        // Process match data
        processMatchData(matchData, matches);

        // Process player data
        processPlayerData(playerData, players, illegitimatePlayers,matches, casinoBalanceChanges);

        // Calculate legitimate and illegitimate actions
        List<String> legitimatePlayers = new ArrayList<>();

        calculateLegitimacy(players, legitimatePlayers, illegitimatePlayers);

        // Calculate win rates for legitimate players
        Map<String, Double> winRates = calculateWinRates(players);

        // Calculate casino's balance
        int casinoBalance = calculateCasinoBalance(casinoBalanceChanges);

        // Write results to result.txt file
        writeResults("src/result.txt", legitimatePlayers, illegitimatePlayers, winRates, casinoBalance);
    }

    // Read file content into a list
    private static List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    // Process match data and populate matches map
    private static void processMatchData(List<String> matchData, Map<String, Match> matches) {
        for (String line : matchData) {
            String[] parts = line.split(",");
            if (parts.length == 4) {
                String matchId = parts[0];
                double rateSideA = Double.parseDouble(parts[1]);
                double rateSideB = Double.parseDouble(parts[2]);
                String result = parts[3];
                Match match = new Match(matchId, rateSideA, rateSideB, result);
                matches.put(matchId, match);
            }
        }
    }

    private static void calculateLegitimacy(Map<String, Player> players, List<String> legitimatePlayers,
                                            List<String> illegitimatePlayers) {
        Set<String> illegitimateIds = new HashSet<>();
        for (Player player : players.values()) {
            if (illegitimatePlayers.contains(player.getId())) {
                illegitimateIds.add(player.getId());
            } else {
                legitimatePlayers.add(player.getId() + " " + player.getBalance() + " " +
                        String.format("%.2f", player.calculateWinRate()));
            }
        }
        illegitimatePlayers.clear(); // Clear before re-populating
        for (String playerId : illegitimateIds) {
            illegitimatePlayers.add(playerId + " " + "Some specific reason for illegitimacy"); // Replace with appropriate reason
        }
        legitimatePlayers.sort(Comparator.naturalOrder());
        illegitimatePlayers.sort(Comparator.naturalOrder());
    }

    private static void writeResults(String filename, List<String> legitimatePlayers, List<String> illegitimatePlayers,
                                     Map<String, Double> winRates, int casinoBalance) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write legitimate players' data
            for (String playerData : legitimatePlayers) {
                writer.println(playerData);
            }
            if (legitimatePlayers.isEmpty() || illegitimatePlayers.isEmpty()) {
                writer.println(); // Empty line between sections
            }

            // Write illegitimate players' data
            for (String playerData : illegitimatePlayers) {
                writer.println(playerData);
            }
            if (!legitimatePlayers.isEmpty() || !illegitimatePlayers.isEmpty()) {
                writer.println(); // Empty line between sections
            }

            // Write casino balance
            writer.println("Casino Balance: " + casinoBalance);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processPlayerData(List<String> playerData, Map<String, Player> players,
            List<String> illegitimatePlayers, Map<String, Match> matches,
            Map<String, Integer> casinoBalanceChanges) {
        for (String line : playerData) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                String playerId = parts[0];
                String operation = parts[1];
                if (!players.containsKey(playerId)) {
                    players.put(playerId, new Player(playerId));
                }
                Player player = players.get(playerId);
                switch (operation) {
                    case "DEPOSIT":
                        if (parts.length == 4) {
                            int depositAmount = Integer.parseInt(parts[3]);
                            player.deposit(depositAmount);
                        } else {
                            illegitimatePlayers.add(line);
                        }
                        break;
                    case "WITHDRAW":
                        if (parts.length == 4) {
                            int withdrawAmount = Integer.parseInt(parts[3]);
                            if (!player.withdraw(withdrawAmount)) {
                                illegitimatePlayers.add(line);
                            }
                            if (player.getBalance() <= 0) {
                                illegitimatePlayers.add(playerId + " LOSES: Balance reached zero or negative");
                            }
                        } else {
                            illegitimatePlayers.add(line);
                        }
                        break;
                    case "BET":
                        if (parts.length == 6) {
                            String matchId = parts[2];
                            int betAmount = Integer.parseInt(parts[3]);
                            String chosenSide = parts[4];
                            player.placeBet(betAmount, matchId, chosenSide, matches);
                            casinoBalanceChanges.merge(matchId, -betAmount, Integer::sum);
                            if (player.getBalance() <= 0) {
                                illegitimatePlayers.add(playerId + " LOSES: Balance reached zero or negative");
                            }
                        } else {
                            illegitimatePlayers.add(line);
                        }
                        break;
                    default:
                        illegitimatePlayers.add(line);
                        break;
                }
            }
        }
    }



    // Calculate win rates for legitimate players
    private static Map<String, Double> calculateWinRates(Map<String, Player> players) {
        Map<String, Double> winRates = new HashMap<>();
        for (Player player : players.values()) {
            double winRate = player.calculateWinRate();
            if (winRate > 0.0) {
                winRates.put(player.getId(), winRate);
            }
        }
        return winRates;
    }

    private static int calculateCasinoBalance(Map<String, Integer> casinoBalanceChanges) {
        int casinoBalance = 0;
        for (int change : casinoBalanceChanges.values()) {
            casinoBalance += change;
        }
        return casinoBalance;
    }
}

