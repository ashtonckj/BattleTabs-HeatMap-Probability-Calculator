package Battleship;

import static Battleship.BattleshipData.*;
import java.util.Scanner;

// All Sink-Related Methods
public class Sink {
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

    public boolean canShipSinkAtPosition(ShipShape selectedShips, int row, int col, int rotation) {
        for (int[] coord : selectedShips.shipCoordinates) {
            int[] rotated = rotateCoordinate(coord, rotation);
            int newRow = row + rotated[0];
            int newCol = col + rotated[1];

            if (newRow < 0 || newRow >= boardSize || newCol < 0 || newCol >= boardSize) {
                return false; // Out of bounds
            }
            if (board[newRow][newCol] != CellState.HIT) {
                return false; // There's a hit here, ship can be here
            }
        }
        return true;
    }

    public boolean generateShipSinkPosition(Scanner scanner, ShipShape selectedShips, int choice, int row, int col, int rotation) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                sinkBoardDisplay[choice - 1][i][j] = CellState.EMPTY;
            }
        }

        for (int[] coord : selectedShips.shipCoordinates) {
            int[] rotated = rotateCoordinate(coord, rotation);
            int newRow = row + rotated[0];
            int newCol = col + rotated[1];
            sinkBoardDisplay[choice - 1][newRow][newCol] = CellState.SUNK;
        }

        System.out.println("Sink Ship Pattern:");

        // Top border
        System.out.print("\033[94m  ╔\033[0m");
        for (int j = 0; j < boardSize - 1; j++) {
            System.out.print("\033[94m════╦\033[0m");
        }
        System.out.println("\033[94m════╗\033[0m");

        for (int i = 0; i < boardSize; i++) {
            System.out.print(i + "\033[94m ║\033[0m");
            for (int j = 0; j < boardSize; j++) {
                if (sinkBoardDisplay[choice - 1][i][j] == CellState.SUNK) {
                    System.out.print("\033[91m >< \033[0m"); // Sunk - in Red
                } else if (board[i][j] == CellState.REVEAL) {
                    System.out.print("\033[93m|\033[96m><\033[93m|\033[0m"); // Reveal - in Blue & Yellow
                } else if (board[i][j] == CellState.HIT) {
                    System.out.print("\033[93m >< \033[0m"); // Hit - in Yellow
                } else {
                    System.out.print("    ");
                }

                if (j < boardSize - 1) {
                    System.out.print("\033[94m║\033[0m");
                } else {
                    System.out.println("\033[94m║\033[0m");
                }
            }

            // Bottom border for each row of cells
            if (i < boardSize - 1) {
                System.out.print("\033[94m  ╠\033[0m");
                for (int j = 0; j < boardSize - 1; j++) {
                    System.out.print("\033[94m════╬\033[0m");
                }
                System.out.println("\033[94m════╣\033[0m");
            } else {
                System.out.print("\033[94m  ╚\033[0m");
                for (int j = 0; j < boardSize - 1; j++) {
                    System.out.print("\033[94m════╩\033[0m");
                }
                System.out.println("\033[94m════╝\033[0m");
            }
        }

        System.out.print("   ");
        for (int j = 0; j < boardSize; j++) {
            System.out.print(" " + j + "   ");
        }
        System.out.println();

        System.out.print("Is this the Ship Sunk Pattern correct? (yes / no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("y") || response.equals("yes")) {
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (sinkBoardDisplay[choice - 1][i][j] == CellState.SUNK) {
                        board[i][j] = CellState.SUNK; // Sunken Ship Position
                    }
                }
            }
            System.out.println("\033[32mSunk: \033[0m" + selectedShips.shipShape);
            selectedShips.setIsSunk();
            return true;
        }
        return false;
    }

    public void removeShips(Scanner scanner) {
        System.out.println("Select ship to sink in the game:");
        for (int i = 0; i < selectedShips.size(); i++) {
            if (selectedShips.get(i).isSunk) {
                System.out.println((i + 1) + ". " + selectedShips.get(i).shipShape + " ("
                        + selectedShips.get(i).shipSize + " box) **SUNKED!**");
            } else {
                System.out.println((i + 1) + ". " + selectedShips.get(i).shipShape + " ("
                        + selectedShips.get(i).shipSize + " box)");
            }
        }

        for (int i = 0; i < 1; i++) {
            System.out.print("Enter selection: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice < 1 || choice > selectedShips.size()) {
                System.out.println("\033[31mInvalid selection. Please choose between 1-4.\033[0m");
                i--;
                continue;
            }

            if (selectedShips.get(choice - 1).isSunk) {
                System.out.println("\033[31mShip has been sunk!\033[0m");
                System.out.print("Would you like to unsink this " + selectedShips.get(choice - 1).shipShape + "? (yes/no): ");
                String response = scanner.nextLine().trim().toLowerCase();

                if (response.equals("y") || response.equals("yes")) {
                    for (int row = 0; row < boardSize; row++) {
                        for (int col = 0; col < boardSize; col++) {
                            if (sinkBoardDisplay[choice - 1][row][col] == CellState.SUNK) {
                                sinkBoardDisplay[choice - 1][row][col] = CellState.EMPTY;
                                board[row][col] = CellState.HIT;
                            }
                        }
                    }
                    System.out.println("\033[32mUnsunk: \033[0m" + selectedShips.get(choice - 1).shipShape);
                    selectedShips.get(choice - 1).setIsSunk();
                }
            } else {
                for (int row = 0; row < boardSize; row++) {
                    for (int col = 0; col < boardSize; col++) {
                        for (int rotation = 0; rotation < selectedShips.get(choice - 1).shipRotationAmount; rotation++) {
                            if (canShipSinkAtPosition(selectedShips.get(choice - 1), row, col, rotation)) {
                                if (generateShipSinkPosition(scanner, selectedShips.get(choice - 1), choice, row, col,
                                        rotation)) {
                                    return;
                                }
                            }
                        }
                    }
                }
                System.out.println("\033[31mShip can't be sunk! (No valid spaces)\033[0m");
            }
        }
    }
}
