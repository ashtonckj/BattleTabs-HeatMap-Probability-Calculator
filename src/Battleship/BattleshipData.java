package Battleship;

import java.util.ArrayList;
import java.util.List;

public class BattleshipData {
    public static int boardSize;
    public static CellState[][] board;
    public static CellState[][][] sinkBoardDisplay;
    public static int[][][] heatMapCount; // Stores probability counts
    public static int[][][] heatMapWeight;
    public static int[][] heatMapTotalWeight;
    public static int[][] heatMapTotalCount; // Array to store multiple heatmaps
    public static int[] totalPlacements;
    public static List<ShipShape> selectedShips;
    public static ShipShape[] availableShips;
    public static int rockBinaryLength = 64;
    public static String[][] guaranteedShipDisplayRow1;
    public static String[][] guaranteedShipDisplayRow2;

    public enum CellState {
        MISS,
        ROCK,
        EMPTY,
        REVEAL,
        HIT,
        SUNK
    }

    static {
        int size = 8;
        boardSize = size;
        board = new CellState[size][size];
        sinkBoardDisplay = new CellState[4][size][size];
        heatMapCount = new int[4][size][size];
        heatMapWeight = new int[4][size][size];
        heatMapTotalCount = new int[size][size];
        heatMapTotalWeight = new int[size][size];
        totalPlacements = new int[4];
        selectedShips = new ArrayList<>();
        guaranteedShipDisplayRow1 = new String[boardSize][boardSize];
        guaranteedShipDisplayRow2 = new String[boardSize][boardSize];

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                for (int shipNumber = 0; shipNumber < 4; shipNumber++) {
                    heatMapCount[shipNumber][i][j] = 0;
                    heatMapWeight[shipNumber][i][j] = 0;
                    sinkBoardDisplay[shipNumber][i][j] = CellState.EMPTY;
                    totalPlacements[shipNumber] = 0;
                }
                heatMapTotalCount[i][j] = 0;
                heatMapTotalWeight[i][j] = 0;
                board[i][j] = CellState.EMPTY;
            }
        }
    }
}