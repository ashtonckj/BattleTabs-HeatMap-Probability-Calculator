package Battleship;

import static Battleship.BattleshipData.*;
import java.util.List;
import java.util.ArrayList;

public class ShipPlacementConfidence {
    public void detectShips() {
        ShipPlacementConfidence confidence = new ShipPlacementConfidence();
        confidence.calculateConfidenceFromHeatMap();

        // Check for guaranteed (100%) placements
        for (int shipNumber = 0; shipNumber < selectedShips.size(); shipNumber++) {
            if (!selectedShips.get(shipNumber).isSunk) {
                for (int row = 0; row < boardSize; row++) {
                    for (int col = 0; col < boardSize; col++) {
                        if (heatMapCount[shipNumber][row][col] == totalPlacements[shipNumber]) {
                            System.out.println("Found guaranteed ship placements!");
                            return;
                        }
                    }
                }
            }
        }
    }

    public void calculateConfidenceFromHeatMap() {
        System.out.println("\n=== SHIP PLACEMENT CONFIDENCE REPORT ===");
        for (int shipNumber = 0; shipNumber < selectedShips.size(); shipNumber++) {
            if (selectedShips.get(shipNumber).isSunk) {
                System.out.println("\nShip " + shipNumber + " (" + selectedShips.get(shipNumber).shipShape + "): ALREADY SUNK");
                continue;
            }
            if (totalPlacements[shipNumber] == 0) {
                System.out.println("\nShip " + shipNumber + " (" + selectedShips.get(shipNumber).shipShape + "): NO VALID PLACEMENTS!");
                continue;
            }

            System.out.println("\nShip " + shipNumber + " (" + selectedShips.get(shipNumber).shipShape + "):");
            System.out.println("  Total possible placements: " + totalPlacements[shipNumber]);
            boolean foundHighConfidence = false;
            for (int row = 0; row < boardSize; row++) {
                for (int col = 0; col < boardSize; col++) {
                    if (heatMapCount[shipNumber][row][col] > 0) {
                        double confidencePercentage = (double) heatMapCount[shipNumber][row][col] / totalPlacements[shipNumber] * 100;

                        if (confidencePercentage >= 50.0) { // Only show positions with 50%+ confidence
                            String confidenceLevel = (confidencePercentage == 100.0) ? "GUARANTEED"
                                                : (confidencePercentage >= 90.0) ? "VERY HIGH"
                                                : (confidencePercentage >= 75.0) ? "HIGH" : "MODERATE";
                            System.out.println("  Position (" + row + "," + col + "): "
                                            + String.format("%.1f", confidencePercentage) + "% - " + confidenceLevel
                                            + " (appears in " + heatMapCount[shipNumber][row][col] + "/"
                                            + totalPlacements[shipNumber] + " placements)");
                            foundHighConfidence = true;
                        }
                    }
                }
            }
            if (!foundHighConfidence) {
                System.out.println("  No high-confidence positions found (all positions < 50% confidence)");
            }
        }

        // Find best target recommendations
        System.out.println("\n=== TARGET RECOMMENDATIONS ===");
        findBestTargetsFromHeatMap();
    }

    private void findBestTargetsFromHeatMap() {
        List<TargetRecommendation> recommendations = new ArrayList<>();

        // Calculate probability for each unhit position
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                // Skip already hit/missed/blocked positions
                if (board[row][col] != CellState.EMPTY) {
                    continue;
                }

                // Calculate combined probability from all ships
                double confidencePercentage = 0.0;
                for (int shipNumber = 0; shipNumber < selectedShips.size(); shipNumber++) {
                    if (!selectedShips.get(shipNumber).isSunk && heatMapCount[shipNumber][row][col] > 0
                            && totalPlacements[shipNumber] > 0) {
                        confidencePercentage += (double) heatMapCount[shipNumber][row][col]
                                / totalPlacements[shipNumber] * 100;
                    }
                }

                if (confidencePercentage >= 20.0) { // Only show targets with 20%+ probability
                    recommendations.add(new TargetRecommendation(row, col, confidencePercentage));
                }
            }
        }

        // Sort recommendations by probability (highest first)
        recommendations.sort((a, b) -> Double.compare(b.probability, a.probability));

        if (recommendations.isEmpty()) {
            System.out.println("No high-probability targets found.");
        } else {
            System.out.println("Top target recommendations:");
            for (int i = 0; i < Math.min(10, recommendations.size()); i++) {
                TargetRecommendation rec = recommendations.get(i);
                System.out.println((i + 1) + ". Target (" + rec.row + "," + rec.col + "): " + String.format("%.1f", rec.probability) + "% chance of hit");
            }
        }
    }

    // Helper class for target recommendations
    private static class TargetRecommendation {
        int row, col;
        double probability;

        TargetRecommendation(int row, int col, double probability) {
            this.row = row;
            this.col = col;
            this.probability = probability;
        }
    }
}