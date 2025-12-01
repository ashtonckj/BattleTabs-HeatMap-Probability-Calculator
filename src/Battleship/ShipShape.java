package Battleship;

public class ShipShape {
    String shipShape;
    int[][] shipCoordinates;
    int shipRotationAmount;
    int shipSize;
    boolean isSunk;

    public ShipShape(String shipShape, int[][] shipCoordinates, int shipRotationAmount) {
        this.shipShape = shipShape;
        this.shipCoordinates = shipCoordinates;
        this.shipRotationAmount = shipRotationAmount;
        this.shipSize = shipCoordinates.length;
        this.isSunk = false;
    }

    public void setIsSunk() {
        this.isSunk = !this.isSunk;
    }

    public static final ShipShape[] availableShips = {
        new ShipShape("1x1", new int[][]{{0, 0}}, 1),
        new ShipShape("1x2", new int[][]{{0, 0}, {0, 1}}, 2),
        new ShipShape("1x3", new int[][]{{0, 0}, {0, 1}, {0, 2}}, 2),
        new ShipShape("1x4", new int[][]{{0, 0}, {0, 1}, {0, 2}, {0, 3}}, 2),
        new ShipShape("1x5", new int[][]{{0, 0}, {0, 1}, {0, 2}, {0, 3}, {0, 4}}, 2),
        new ShipShape("2x2", new int[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}}, 1),
        new ShipShape("+ Shape", new int[][]{{0, 1}, {1, 0}, {1, 1}, {1, 2}, {2, 1}}, 1),
        new ShipShape("x Shape", new int[][]{{0, 0}, {0, 2}, {1, 1}, {2, 0}, {2, 2}}, 1),
        new ShipShape("o Shape", new int[][]{{0, 1}, {1, 0}, {1, 2}, {2, 1}}, 1),
        new ShipShape("O Shape", new int[][]{{0, 0}, {0, 1}, {0, 2}, {1, 0}, {1, 2}, {2, 0}, {2, 1}, {2, 2}}, 1),
        new ShipShape("l Shape", new int[][]{{0, 0}, {1, 0}, {1, 1}}, 4),
        new ShipShape("L Shape", new int[][]{{0, 0}, {1, 0}, {2, 0}, {2, 1}}, 4),
        new ShipShape("Arch Shape", new int[][]{{0, 0}, {0, 1}, {0, 2}, {1, 0}, {1, 2}}, 4),
        new ShipShape("T Shape", new int[][]{{0, 0}, {0, 1}, {0, 2}, {1, 1}}, 4),
        new ShipShape("S Shape", new int[][]{{0, 1}, {0, 2}, {1, 0}, {1, 1}}, 4),
        new ShipShape("Zig-Zag Shape", new int[][]{{0, 0}, {0, 1}, {1, 1}, {1, 2}, {2, 2}, {2, 3}}, 4),
    };
}