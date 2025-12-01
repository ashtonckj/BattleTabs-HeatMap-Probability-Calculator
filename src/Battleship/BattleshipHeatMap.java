package Battleship;

import static Battleship.BattleshipData.*;
import java.util.Scanner;
/* Additional Features:
 * - Use Ship PowerAbilities (Create New JavaFile & Code Each Ability)
 * - Best Sonar Target (Find Lowest-Middle Probability)
 * - Meybe Try GUI
 * - Add Probability after Sonar is Used
 * - Detecting if some ships are a subset of other ships
*/

public class BattleshipHeatMap {
    public void processMove(Scanner scanner, int row, int col) {
        if (board[row][col] == CellState.ROCK) {
            System.out.println("A rock is in the way!");
            return;
        }
        if (board[row][col] == CellState.MISS || board[row][col] == CellState.HIT) {
            board[row][col] = CellState.EMPTY;
            return;
        }
        if (board[row][col] == CellState.REVEAL) {
            board[row][col] = CellState.HIT;
            return;
        }

        System.out.print("Was this a reveal / hit / miss / sonar? : ");
        String response = scanner.nextLine().trim().toLowerCase();
        switch (response) {
            case "reveal":
                board[row][col] = CellState.REVEAL;
                break;
            case "hit":
                board[row][col] = CellState.HIT;
                break;
            case "miss":
                board[row][col] = CellState.MISS;
                break;
            case "sonar":
                applySonar(scanner, row, col);
                break;
            default:
                System.out.println("\033[31mInvalid response. Please enter hit, reveal, miss, or sonar.\033[0m");
                break;
        }
    }

    public void applySonar(Scanner scanner, int row, int col) {
        System.out.print("Sonar Number: ");
        int radius = scanner.nextInt();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                int distance = Math.abs(i - row) + Math.abs(j - col);
                if (distance < radius) {
                    board[i][j] = CellState.MISS;
                }
                if (distance == radius) {
                    int sonarBonus = 0;
                    heatMapTotalWeight[i][j] += sonarBonus;
                }
            }
        }
    }

    public void selectShips(Scanner scanner) {
        System.out.println("Select 4 ship types for the game:");
        for (int i = 0; i < ShipShape.availableShips.length; i++) {
            System.out.println((i + 1) + ". " + ShipShape.availableShips[i].shipShape + " (" + ShipShape.availableShips[i].shipSize + " box)");
        }

        selectedShips.clear();
        for (int i = 0; i < 4; i++) {
            System.out.print("Enter selection " + (i + 1) + " (1-16): ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice < 1 || choice > ShipShape.availableShips.length) {
                System.out.println("\033[31mInvalid selection. Please choose between 1-16.\033[0m");
                i--;
                continue;
            }
            ShipShape originalShip = ShipShape.availableShips[choice - 1];
            ShipShape newShip = new ShipShape(originalShip.shipShape, originalShip.shipCoordinates, originalShip.shipRotationAmount);
            selectedShips.add(newShip);
            System.out.println("Added: " + selectedShips.get(i).shipShape);
        }
        System.out.println("\nSelected ships:");
        for (int i = 0; i < 4; i++) {
            System.out.println("- " + selectedShips.get(i).shipShape);
        }
    }

    public static void main(String[] args) {
        BattleshipHeatMap game = new BattleshipHeatMap();
        HeatMapDisplay display = new HeatMapDisplay();
        HeatMapGenerator generate = new HeatMapGenerator();
        Rock rock = new Rock();
        ShipPlacementConfidence confidence = new ShipPlacementConfidence();
        Sink sink = new Sink();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Initial Board State: ");
        generate.generateHeatMap();
        display.displayHeatMapTotal();
        game.selectShips(scanner);

        while (true) {
            int row = -1, col = -1;
            generate.generateHeatMap();
            display.drawGuaranteedShips();
            display.displayHeatMapTotal();
            generate.getBestTarget();

            while (true) {
                System.out.print("Enter coordinates to fire at (row [0-7] and column [0-7], separated by space): ");
                String userInput = scanner.nextLine().trim();
                if (userInput.equalsIgnoreCase("print")) {
                    generate.printStoredConfigurations();
                    confidence.detectShips();
                    break;
                }

                if (userInput.equalsIgnoreCase("rock")) {
                    System.out.println("(Yes) Use Today's Rock Pattern");
                    System.out.println("1. Add New Rock Pattern");
                    System.out.println("2. Search for a Rock Pattern");
                    System.out.println("3. Use a Selected Rock Pattern");
                    System.out.println("4. Place/Remove a Custom Rock Piece");
                    System.out.print("Choose an option: ");
                    String rockChoice = scanner.nextLine().toLowerCase();

                    switch (rockChoice) {
                        case "yes":
                            rock.useOriginalSet(scanner);
                            break;
                        case "1":
                            rock.addPattern(scanner);
                            break;
                        case "2":
                            rock.searchAndSetOriginal(scanner);
                            break;
                        case "3":
                            System.out.print("Enter a Rock Pattern Number: ");
                            try {
                                String patternNumberStr = scanner.nextLine();
                                int patternNumber = Integer.parseInt(patternNumberStr);
                                rock.displayPatternWithRotations(scanner, patternNumber);
                            } catch (NumberFormatException e) {
                                System.out.println("\033[31mInvalid input. Please enter a valid number.\033[0m");
                            }
                            break;
                        case "4":
                            System.out.print("Enter coordinates to rock at (row [0-7] and column [0-7], separated by space): ");
                            String userInputRock = scanner.nextLine().trim();
                            String[] parts = userInputRock.split("\\s+");
                            if (parts.length != 2) {
                                System.out.println("\033[31mInvalid input. Please enter exactly two numbers separated by a space.\033[0m");
                                continue;
                            }
                            row = Integer.parseInt(parts[0]);
                            col = Integer.parseInt(parts[1]);

                            if (row < 0 || row > 7 || col < 0 || col > 7) {
                                System.out.println("\033[31mInvalid coordinates. Row and column must be between 0 and 7.\033[0m");
                                continue;
                            }
                            rock.registerRock(row, col);
                            break;
                        default:
                            System.out.println("\033[31mInvalid input. Please enter a correct option.\033[0m");
                            break;
                    }
                    break;
                }

                if (userInput.equalsIgnoreCase("sink")) {
                    sink.removeShips(scanner);
                    break;
                }

                try {
                    String[] parts = userInput.split("\\s+");
                    if (parts.length != 2) {
                        System.out.println("\033[31mInvalid input. Please enter exactly two numbers separated by a space.\033[0m");
                        continue;
                    }
                    row = Integer.parseInt(parts[0]);
                    col = Integer.parseInt(parts[1]);

                    if (row < 0 || row > 7 || col < 0 || col > 7) {
                        System.out.println("\033[31mInvalid coordinates. Row and column must be between 0 and 7.\033[0m");
                        continue;
                    }
                    game.processMove(scanner, row, col);
                    break; // valid input
                } catch (NumberFormatException e) {
                    System.out.println("\033[31mInvalid input. Please enter valid integers.\033[0m");
                }
            }
        }
    }
}