package Battleship;

import static Battleship.BattleshipData.*;
import java.util.List;
import java.util.ArrayList;

public class HeatMapGenerator {
    public int[] rotateCoordinate(int[] coord, int rotation) {
        switch (rotation) {
            case 0:
                return new int[] { coord[0], coord[1] }; // 0 degrees
            case 1:
                return new int[] { coord[1], -coord[0] }; // 90 degrees
            case 2:
                return new int[] { -coord[0], -coord[1] }; // 180 degrees
            case 3:
                return new int[] { -coord[1], coord[0] }; // 270 degrees
            default:
                return new int[] { coord[0], coord[1] };
        }
    }

    public boolean canShipFitAtPosition(ShipShape selectedShips, int row, int col, int rotation) {
        for (int[] coord : selectedShips.shipCoordinates) {
            int[] rotated = rotateCoordinate(coord, rotation);
            int newRow = row + rotated[0];
            int newCol = col + rotated[1];

            if (newRow < 0 || newRow >= boardSize || newCol < 0 || newCol >= boardSize) {
                return false; // Out of bounds
            }
            if (board[newRow][newCol] == CellState.MISS || board[newRow][newCol] == CellState.ROCK || board[newRow][newCol] == CellState.SUNK) {
                return false; // There's a miss here, ship can't be here
            }
        }
        return true;
    }

    public void addShipProbability(ShipShape selectedShips, int shipNumber, int row, int col, int rotation) {
        int[][] occupiedGrid = new int[boardSize][boardSize];
        for (int[] coord : selectedShips.shipCoordinates) {
            int[] rotated = rotateCoordinate(coord, rotation);
            int newRow = row + rotated[0];
            int newCol = col + rotated[1];
            occupiedGrid[newRow][newCol] = 1;
        }

        // Check if remaining ships can fit with this placement
        if (!canRemainingShipsFit(shipNumber, occupiedGrid)) {
            return;
        }

        // calculateConfidenceFromHeatMap
        totalPlacements[shipNumber]++;

        // addCountHitsInShipPosition
        int hitCount = 0;
        for (int[] coord : selectedShips.shipCoordinates) {
            int[] rotated = rotateCoordinate(coord, rotation);
            int newRow = row + rotated[0];
            int newCol = col + rotated[1];
            heatMapCount[shipNumber][newRow][newCol]++;

            if (board[newRow][newCol] == CellState.REVEAL || board[newRow][newCol] == CellState.HIT) {
                hitCount++;
            }
        }

        // addProbabilityToUnhitCells
        double weight = hitCount > 0 ? 4 * hitCount : 1;
        for (int[] coord : selectedShips.shipCoordinates) {
            int[] rotated = rotateCoordinate(coord, rotation);
            int newRow = row + rotated[0];
            int newCol = col + rotated[1];

            if (board[newRow][newCol] == CellState.EMPTY) { // Only add to empty cells
                heatMapWeight[shipNumber][newRow][newCol] += weight;
            }
        }
    }

    public void generateHeatMap() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                heatMapTotalCount[i][j] = 0;
                heatMapTotalWeight[i][j] = 0;
            }
        }

        for (int shipNumber = 0; shipNumber < selectedShips.size(); shipNumber++) {
            totalPlacements[shipNumber] = 0;
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    heatMapCount[shipNumber][i][j] = 0;
                    heatMapWeight[shipNumber][i][j] = 0;
                }
            }
            if (selectedShips.get(shipNumber).isSunk) {
                continue;
            }

            // Try all positions and rotations for the ship
            for (int row = 0; row < boardSize; row++) {
                for (int col = 0; col < boardSize; col++) {
                    for (int rotation = 0; rotation < selectedShips.get(shipNumber).shipRotationAmount; rotation++) {
                        if (canShipFitAtPosition(selectedShips.get(shipNumber), row, col, rotation)) {
                            addShipProbability(selectedShips.get(shipNumber), shipNumber, row, col, rotation);
                        }
                    }
                }
            }

            // Add this ship's probabilities to the total
            for (int row = 0; row < boardSize; row++) {
                for (int col = 0; col < boardSize; col++) {
                    heatMapTotalCount[row][col] += heatMapCount[shipNumber][row][col];
                    heatMapTotalWeight[row][col] += heatMapWeight[shipNumber][row][col];
                }
            }
        }
    }

    public void getBestTarget() {
        int maxHeat = -1;
        int[] bestTarget = new int[2];

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                // Skip cells that are already hit, missed, or rocks
                if (board[row][col] == CellState.ROCK || board[row][col] == CellState.REVEAL || board[row][col] == CellState.HIT) {
                    continue;
                }
                if (heatMapTotalWeight[row][col] > maxHeat) {
                    maxHeat = heatMapTotalWeight[row][col];
                    bestTarget[0] = row;
                    bestTarget[1] = col;
                }
            }
        }
        System.out.println("Best target: " + bestTarget[0] + ", " + bestTarget[1]);
    }

    public boolean canRemainingShipsFit(int placedShipNumber, int[][] occupiedGrid) {
        // Count remaining unsunk ships (excluding the one we're placing)
        int remainingShipsCount = 0;
        int[] remainingShipNumbers = new int[4];

        for (int shipNumber = 0; shipNumber < selectedShips.size(); shipNumber++) {
            if (!selectedShips.get(shipNumber).isSunk && shipNumber != placedShipNumber) {
                remainingShipNumbers[remainingShipsCount] = shipNumber;
                remainingShipsCount++;
            }
        }

        if (remainingShipsCount == 0) {
            return true;
        }
        storedConfigurations.clear();
        // Start recursive validation with the first remaining ship
        return canRemainingShipsFitRecursive(remainingShipNumbers, remainingShipsCount, 0, occupiedGrid);
    }

    // Method to validate that all hit spots are covered by predicted ships
    public boolean validateAllHitsCovered(int[][] occupiedGrid) {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if ((board[row][col] == CellState.REVEAL || board[row][col] == CellState.HIT) && occupiedGrid[row][col] != 1) {
                    return false; // Found a hit spot not covered by any predicted ship
                }
            }
        }
        return true; // All hit spots are covered
    }

    public boolean canRemainingShipsFitRecursive(int[] remainingShipNumbers, int remainingShipsCount, int currentShipIndex, int[][] occupiedGrid) {
        // Base case: if we've successfully placed all remaining ships
        if (currentShipIndex >= remainingShipsCount) {
            return validateAllHitsCovered(occupiedGrid);
        }

        int trueCount = 0; // Count successful placements for this ship
        // Try to place the current ship anywhere on the board
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                for (int rotation = 0; rotation < 4; rotation++) {
                    boolean canPlace = true;

                    // Check if ship can fit at this position and rotation
                    for (int[] coord : selectedShips.get(remainingShipNumbers[currentShipIndex]).shipCoordinates) {
                        int[] rotated = rotateCoordinate(coord, rotation);
                        int newRow = row + rotated[0];
                        int newCol = col + rotated[1];

                        // Check bounds
                        if (newRow < 0 || newRow >= boardSize || newCol < 0 || newCol >= boardSize) {
                            canPlace = false;
                            break;
                        }

                        // Check if position is blocked (miss, rock, sunk ship, or any occupied space byother ships)
                        if (board[newRow][newCol] == CellState.MISS || board[newRow][newCol] == CellState.ROCK || board[newRow][newCol] == CellState.SUNK || occupiedGrid[newRow][newCol] == 1) {
                            canPlace = false;
                            break;
                        }
                    }

                    if (canPlace) {
                        // Create new occupied grid with this ship placement
                        int[][] occupiedGridTotal = new int[boardSize][boardSize];
                        for (int i = 0; i < boardSize; i++) {
                            for (int j = 0; j < boardSize; j++) {
                                occupiedGridTotal[i][j] = occupiedGrid[i][j];
                            }
                        }

                        // Mark this ship's position as occupied
                        for (int[] coord : selectedShips.get(remainingShipNumbers[currentShipIndex]).shipCoordinates) {
                            int[] rotated = rotateCoordinate(coord, rotation);
                            int newRow = row + rotated[0];
                            int newCol = col + rotated[1];
                            occupiedGridTotal[newRow][newCol] = 1;
                        }

                        // Recursively check if remaining ships can fit
                        if (canRemainingShipsFitRecursive(remainingShipNumbers, remainingShipsCount, currentShipIndex + 1, occupiedGridTotal)) {
                            trueCount++;

                            // Store configuration data for potential printing
                            if (trueCount <= 4) {
                                storedConfigurations.add(new ConfigurationData(currentShipIndex, trueCount, row, col, rotation, occupiedGridTotal));
                            }
                            return true; // Found a valid configuration
                        }
                    }
                }
            }
        }
        return false; // No valid placement found for this ship
    }

    public List<ConfigurationData> storedConfigurations = new ArrayList<>();

    public class ConfigurationData {
        int shipIndex;
        int solutionNumber;
        int row;
        int col;
        int rotation;
        int[][] occupiedGrid;

        ConfigurationData(int shipIndex, int solutionNumber, int row, int col, int rotation, int[][] occupiedGrid) {
            this.shipIndex = shipIndex;
            this.solutionNumber = solutionNumber;
            this.row = row;
            this.col = col;
            this.rotation = rotation;
            this.occupiedGrid = new int[boardSize][boardSize];
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    this.occupiedGrid[i][j] = occupiedGrid[i][j];
                }
            }
        }
    }

    public void printStoredConfigurations() {
        if (storedConfigurations.isEmpty()) {
            System.out.println("No configurations stored.");
            return;
        }

        for (ConfigurationData config : storedConfigurations) {
            System.out.println("Ship " + config.shipIndex + " - Solution " + config.solutionNumber + " at (" + config.row + "," + config.col + ") rotation " + config.rotation);
            System.out.println("Occupied Grid:");
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    System.out.print(config.occupiedGrid[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println();
        }

        System.out.println("Testing Grid:");
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                System.out.print(heatMapCount[2][i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}