package Battleship;

import static Battleship.BattleshipData.*;

public class HeatMapDisplay {
    // Helper method to get ship colour
    private String getShipColour(int shipNumber) {
        switch (shipNumber) {
            case 0:
                return "\033[35m"; // Purple
            case 1:
                return "\033[32m"; // Green
            case 2:
                return "\033[33m"; // Yellow
            case 3:
                return "\033[36m"; // Cyan
            default:
                return "\033[37m"; // White
        }
    }

    public void displayHeatMapTotal() {
        int max = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (heatMapTotalWeight[i][j] > max) {
                    max = heatMapTotalWeight[i][j];
                }
            }
        }

        System.out.println("Heat Map Total:");
        // Column numbers
        System.out.print("   ");
        for (int j = 0; j < boardSize; j++) {
            System.out.print("  " + j + "    ");
        }
        System.out.println();

        // Top border
        System.out.print("\033[94m  ╔\033[0m");
        for (int j = 0; j < boardSize - 1; j++) {
            System.out.print("\033[94m══════╦\033[0m");
        }
        System.out.println("\033[94m══════╗\033[0m");

        for (int i = 0; i < boardSize; i++) {
            // First row
            System.out.print(i + "\033[94m ║\033[0m");
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == CellState.MISS) {
                    System.out.print("\033[96m ???? \033[0m"); // Miss - in Cyan
                } else if (board[i][j] == CellState.ROCK) {
                    System.out.print("\033[90m ████ \033[0m"); // Rock - in Grey
                } else if (board[i][j] == CellState.REVEAL) {
                    System.out.print("\033[93m|\033[96m>--<\033[93m|\033[0m"); // Reveal - in Blue & Yellow
                } else if (board[i][j] == CellState.HIT) {
                    System.out.print("\033[93m >--< \033[0m"); // Hit - in Yellow
                } else if (board[i][j] == CellState.SUNK) {
                    System.out.print("\033[91m >--< \033[0m"); // Sunk - in Red
                } else if (guaranteedShipDisplayRow1[i][j] != null) {
                    System.out.print(guaranteedShipDisplayRow1[i][j]);
                } else {
                    int value = heatMapTotalWeight[i][j];
                    double ratio = (max == 0) ? 0 : (double) value / max;
                    int r, g, b;

                    if (ratio < 0.5) {
                        // Interpolate white (255,255,255) to yellow (255,230,0)
                        double t = ratio / 0.5;
                        r = 255;
                        g = (int) (255 - t * 25); // 255 → 230
                        b = (int) (255 - t * 255); // 255 → 0
                    } else {
                        // Interpolate yellow (255,230,0) to reddish-orange (255,80,0)
                        double t = (ratio - 0.5) / 0.5;
                        r = 255;
                        g = (int) (230 - t * 150); // 230 → 80
                        b = 0;
                    }

                    String display = value == 0 ? "    \033[30m" + value
                                : value < 10 ? "    " + value
                                : value < 100 ? "   " + value
                                : value < 1000 ? "  " + value
                                : value < 10000 ? " " + value : "" + value;
                    System.out.printf("\033[1;38;2;%d;%d;%dm%s \033[0m", r, g, b, display);
                }

                if (j < boardSize - 1) {
                    System.out.print("\033[94m║\033[0m");
                } else {
                    System.out.println("\033[94m║\033[0m");
                }
            }

            // Second row
            System.out.print("  \033[94m║\033[0m");
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == CellState.MISS) {
                    System.out.print("\033[96m ???? \033[0m"); // Miss - in Cyan
                } else if (board[i][j] == CellState.ROCK) {
                    System.out.print("\033[90m ████ \033[0m"); // Rock - in Grey
                } else if (board[i][j] == CellState.REVEAL) {
                    System.out.print("\033[93m|\033[96m>--<\033[93m|\033[0m"); // Reveal - in Blue & Yellow
                } else if (board[i][j] == CellState.HIT) {
                    System.out.print("\033[93m >--< \033[0m"); // Hit - in Yellow
                } else if (board[i][j] == CellState.SUNK) {
                    System.out.print("\033[91m >--< \033[0m"); // Sunk - in Red
                } else if (guaranteedShipDisplayRow2[i][j] != null) {
                    System.out.print(guaranteedShipDisplayRow2[i][j]);
                } else {
                    int value = heatMapTotalCount[i][j];
                    String display = value == 0 ? " \033[30m" + value + "\033[0m"
                            : value < 10 ? " " + value : value < 100 ? "" + value : "" + value;
                    System.out.print(" T:" + display + " ");
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
                    System.out.print("\033[94m══════╬\033[0m");
                }
                System.out.println("\033[94m══════╣\033[0m");
            } else {
                System.out.print("\033[94m  ╚\033[0m");
                for (int j = 0; j < boardSize - 1; j++) {
                    System.out.print("\033[94m══════╩\033[0m");
                }
                System.out.println("\033[94m══════╝\033[0m");
            }
        }

        if (!selectedShips.isEmpty()) {
            System.out.println("- \033[35m" + selectedShips.get(0).shipShape + "\033[0m is "
                    + (selectedShips.get(0).isSunk ? "\033[41m  \033[0m" : "\033[42m  \033[0m"));
            System.out.println("- \033[32m" + selectedShips.get(1).shipShape + "\033[0m is "
                    + (selectedShips.get(1).isSunk ? "\033[41m  \033[0m" : "\033[42m  \033[0m"));
            System.out.println("- \033[33m" + selectedShips.get(2).shipShape + "\033[0m is "
                    + (selectedShips.get(2).isSunk ? "\033[41m  \033[0m" : "\033[42m  \033[0m"));
            System.out.println("- \033[36m" + selectedShips.get(3).shipShape + "\033[0m is "
                    + (selectedShips.get(3).isSunk ? "\033[41m  \033[0m" : "\033[42m  \033[0m"));
        }
    }

    public void drawGuaranteedShips() {
        // Initialize the display arrays
        guaranteedShipDisplayRow1 = new String[boardSize][boardSize];
        guaranteedShipDisplayRow2 = new String[boardSize][boardSize];

        // Check each ship for 100% confidence positions
        for (int shipNumber = 0; shipNumber < selectedShips.size(); shipNumber++) {
            if (selectedShips.get(shipNumber).isSunk)
                continue;

            boolean hasGuaranteedPositions = false;
            for (int row = 0; row < boardSize; row++) {
                for (int col = 0; col < boardSize; col++) {
                    if (heatMapCount[shipNumber][row][col] == totalPlacements[shipNumber]
                            && heatMapCount[shipNumber][row][col] > 0) {
                        hasGuaranteedPositions = true;
                        break;
                    }
                }
                if (hasGuaranteedPositions)
                    break;
            }

            if (hasGuaranteedPositions) {
                // Create ship outline map
                boolean[][] shipPositions = new boolean[boardSize][boardSize];
                for (int row = 0; row < boardSize; row++) {
                    for (int col = 0; col < boardSize; col++) {
                        shipPositions[row][col] = (heatMapCount[shipNumber][row][col] == totalPlacements[shipNumber]
                                && heatMapCount[shipNumber][row][col] > 0);
                    }
                }

                // Apply outline to both rows for each cell
                for (int row = 0; row < boardSize; row++) {
                    for (int col = 0; col < boardSize; col++) {
                        if (shipPositions[row][col]) {
                            String[] outlineChars = getShipOutlineChars(shipNumber, row, col, shipPositions);
                            String colour = getShipColour(shipNumber);
                            guaranteedShipDisplayRow1[row][col] = colour + outlineChars[0] + "\033[0m";
                            guaranteedShipDisplayRow2[row][col] = colour + outlineChars[1] + "\033[0m";
                        }
                    }
                }
            }
        }
    }

    public String[] getShipOutlineChars(int shipNumber, int row, int col, boolean[][] shipPositions) {
        // Check surrounding positions
        boolean up = (row > 0) ? shipPositions[row - 1][col] : false;
        boolean down = (row < boardSize - 1) ? shipPositions[row + 1][col] : false;
        boolean left = (col > 0) ? shipPositions[row][col - 1] : false;
        boolean right = (col < boardSize - 1) ? shipPositions[row][col + 1] : false;

        // Count connections
        int connections = 0;
        if (up)
            connections++;
        if (down)
            connections++;
        if (left)
            connections++;
        if (right)
            connections++;

        String[] result = new String[2]; // [row1, row2]

        // Determine outline characters based on connections and position
        if (totalPlacements[shipNumber] > 1) {
            result[0] = " |  | ";
            result[1] = " |  | ";
            return result;
        }
        if (connections == 0) {
            result[0] = " ┌──┐ ";
            result[1] = " └──┘ ";
        } else if (connections == 1) {
            if (up) {
                result[0] = " │  │ ";
                result[1] = " └──┘ ";
            } else if (down) {
                result[0] = " ┌──┐ ";
                result[1] = " │  │ ";
            } else if (left) {
                result[0] = "───┐  ";
                result[1] = "───┘  ";
            } else if (right) {
                result[0] = "  ┌───";
                result[1] = "  └───";
            }
        } else if (connections == 2) {
            if (up && down) {
                result[0] = " │  │ ";
                result[1] = " │  │ ";
            } else if (left && right) {
                result[0] = "──────";
                result[1] = "──────";
            } else if (up && right) {
                result[0] = " │  └─";
                result[1] = " └────";
            } else if (up && left) {
                result[0] = "─┘  │ ";
                result[1] = "────┘ ";
            } else if (down && right) {
                result[0] = " ┌────";
                result[1] = " │  ┌─";
            } else if (down && left) {
                result[0] = "────┐ ";
                result[1] = "─┐  │ ";
            }
        } else if (connections == 3) {
            if (!up) {
                result[0] = "──────";
                result[1] = "─┐  ┌─";
            } else if (!down) {
                result[0] = "─┘  └─";
                result[1] = "──────";
            } else if (!left) {
                result[0] = " │  └─";
                result[1] = " │  ┌─";
            } else if (!right) {
                result[0] = "─┘  │ ";
                result[1] = "─┐  │ ";
            }
        } else if (connections == 4) {
            result[0] = "─┘  └─";
            result[1] = "─┐  ┌─";
        }
        return result;
    }

}