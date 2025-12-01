package Battleship;

import static Battleship.BattleshipData.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// All Rock-Related Methods
public class Rock {
    public void registerRock(int row, int col) {
        if (board[row][col] == CellState.ROCK) {
            board[row][col] = CellState.EMPTY;
        } else {
            board[row][col] = CellState.ROCK;
        }
    }

    // Handle OriginalSet at startup
    public void useOriginalSet(Scanner scanner) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("RockPatterns.txt"));
            String firstLine = reader.readLine();
            reader.close();

            if (firstLine != null && firstLine.startsWith("OriginalSet: ")) {
                String[] parts = firstLine.split(": ");
                if (parts.length == 2) {
                    int patternNumber = Integer.parseInt(parts[1].trim());
                    System.out.println("\033[32mFound OriginalSet: \033[0m" + patternNumber);
                    displayPatternWithRotations(scanner, patternNumber);
                }
            } else {
                System.out.println("\033[31mNo OriginalSet found. You'll need to search for a pattern first.\033[0m");
            }
        } catch (IOException e) {
            System.out.println("\033[31mError reading file: \033[0m" + e.getMessage());
        }
    }

    // Display pattern with all rotations/mirrors
    public void displayPatternWithRotations(Scanner scanner, int patternNumber) {
        String rockBinary = findPatternByNumber(patternNumber);
        if (rockBinary == null) {
            System.out.println("\033[31mPattern number " + patternNumber + " not found.\033[0m");
            return;
        }

        int[][] binaryBoard = binaryToBoard(rockBinary);
        String[] transformationNames = { "0° Rotation", "90° Rotation", "180° Rotation", "270° Rotation",
                "0° Rotation + Left-Right Mirror", "90° Rotation + Top-Bottom Mirror",
                "180° Rotation + Left-Right Mirror", "270° Rotation + Top-Bottom Mirror" };

        for (int i = 0; i < 8; i++) {
            System.out.println("\n" + transformationNames[i] + ": ");
            int[][] transformed = rockSymmetry(binaryBoard, i);
            printBoard(transformed);

            System.out.print("Is this the correct orientation? (Yes to confirm): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("yes")) {
                System.out.println("\033[32mPattern confirmed!\033[0m");
                for (int row = 0; row < boardSize; row++) {
                    for (int col = 0; col < boardSize; col++) {
                        if (board[row][col] == CellState.ROCK) {
                            board[row][col] = CellState.EMPTY;
                        }
                        if (transformed[row][col] == 1) {
                            board[row][col] = CellState.ROCK;
                        }
                    }
                }
                return;
            }
        }
        System.out.println("\033[31mNo orientation confirmed.\033[0m");
    }

    // Add new pattern to file
    public void addPattern(Scanner scanner) {
        System.out.print("Enter 64-digit binary pattern: ");
        String rockBinary = scanner.nextLine().trim();

        if (rockBinary.length() != rockBinaryLength) {
            System.out.println("\033[31mError: Pattern must be exactly 64 digits!\033[0m");
            return;
        }
        if (!rockBinary.matches("[01]+")) {
            System.out.println("\033[31mError: Pattern must contain only 0s and 1s!\033[0m");
            return;
        }
        // Check for duplicates by reading through existing patterns
        try {
            BufferedReader reader = new BufferedReader(new FileReader("RockPatterns.txt"));
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.trim().isEmpty() || currentLine.startsWith("OriginalSet: "))
                    continue;

                String[] parts = currentLine.split(", ");
                if (parts.length >= 2 && parts[0].matches("\\d+")) {
                    String existingBinary = parts[1];
                    int[][] existingPattern = binaryToBoard(existingBinary);
                    if (matchesAnyTransformation(existingPattern, rockBinary)) {
                        reader.close();
                        System.out.println("\033[31mError: This pattern (or one of its rotations/mirrors) already exists in the file!\033[0m");
                        return;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("\033[31mError reading file for duplicate check: \033[0m" + e.getMessage());
            return;
        }

        try {
            int maxNumber = 0;
            BufferedReader reader = new BufferedReader(new FileReader("RockPatterns.txt"));
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.trim().isEmpty())
                    continue;
                String[] parts = currentLine.split(", ");
                if (parts.length >= 2 && parts[0].matches("\\d+")) {
                    int number = Integer.parseInt(parts[0]);
                    if (number > maxNumber)
                        maxNumber = number;
                }
            }
            reader.close();

            int nextPatternNumber = maxNumber + 1;
            FileWriter writer = new FileWriter("RockPatterns.txt", true);
            writer.write(nextPatternNumber + ", " + rockBinary + "\n");
            writer.close();
            System.out.println("\033[32mPattern " + nextPatternNumber + " added successfully.\033[0m");
        } catch (IOException e) {
            System.out.println("\033[31mError writing to file: \033[0m" + e.getMessage());
        }
    }

    // Search and set as original
    public void searchAndSetOriginal(Scanner scanner) {
        System.out.print("Enter binary digits to search for: ");
        String searchPattern = scanner.nextLine().trim();

        if (!searchPattern.matches("[01]+")) {
            System.out.println("\033[31mError: Search pattern must contain only 0s and 1s!\033[0m");
            return;
        }

        try {
            // Search for patterns matching input (combined searchPatterns logic)
            List<Integer> matchingNumbers = new ArrayList<>();
            List<String> allPatternLines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader("RockPatterns.txt"));
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.trim().isEmpty() || currentLine.startsWith("OriginalSet: "))
                    continue;

                allPatternLines.add(currentLine);
                String[] parts = currentLine.split(", ");
                if (parts.length >= 2 && parts[0].matches("\\d+")) {
                    int patternNumber = Integer.parseInt(parts[0]);
                    String rockBinary = parts[1];
                    int[][] rockPattern = binaryToBoard(rockBinary);
                    if (matchesAnyTransformation(rockPattern, searchPattern)) {
                        matchingNumbers.add(patternNumber);
                    }
                }
            }
            reader.close();

            if (matchingNumbers.isEmpty()) {
                System.out.println("\033[31mNo matching patterns found.\033[0m");
                return;
            }

            System.out.println("\033[32mFound matching pattern(s): \033[0m");
            for (int patternNumber : matchingNumbers) {
                System.out.println("Pattern number: " + patternNumber);
                String rockBinary = findPatternByNumber(patternNumber);
                int[][] binaryBoard = binaryToBoard(rockBinary);
                printBoard(binaryBoard);
            }

            // Let user choose which pattern to set as original
            int chosenPattern;
            if (matchingNumbers.size() == 1) {
                chosenPattern = matchingNumbers.get(0);
                System.out.println("Setting pattern " + chosenPattern + " as OriginalSet.");
            } else {
                System.out.print("Enter pattern number to set as OriginalSet: ");
                chosenPattern = scanner.nextInt();
                scanner.nextLine();

                if (!matchingNumbers.contains(chosenPattern)) {
                    System.out.println("\033[31mInvalid pattern number!\033[0m");
                    return;
                }
            }

            FileWriter writer = new FileWriter("RockPatterns.txt");
            writer.write("OriginalSet: " + chosenPattern + "\n");
            for (String existingLine : allPatternLines) {
                writer.write(existingLine + "\n");
            }
            writer.close();

            System.out.println("\033[32mOriginalSet Has Been Updated To: \033[0m" + chosenPattern);
        } catch (IOException e) {
            System.out.println("\033[31mError handling file: \033[0m" + e.getMessage());
        }
    }

    public boolean matchesAnyTransformation(int[][] rockPattern, String searchPattern) {
        for (int rockSymmetryNum = 0; rockSymmetryNum < 8; rockSymmetryNum++) {
            int[][] transformation = rockSymmetry(rockPattern, rockSymmetryNum);
            String transformedBinary = boardToBinary(transformation);
            if (transformedBinary.contains(searchPattern)) {
                return true;
            }
        }
        return false;
    }

    public String findPatternByNumber(int patternNumberInput) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("RockPatterns.txt"));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                String[] parts = line.split(", ");
                if (parts.length >= 2 && parts[0].matches("\\d+")) {
                    int patternNumber = Integer.parseInt(parts[0]);
                    if (patternNumber == patternNumberInput) {
                        reader.close();
                        return parts[1];
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("\033[31mError reading file: \033[0m" + e.getMessage());
            return null;
        }
        return null;
    }

    public int[][] binaryToBoard(String rockBinary) {
        int[][] rockPattern = new int[boardSize][boardSize];
        for (int i = 0; i < rockBinaryLength; i++) {
            int row = i / boardSize;
            int col = i % boardSize;
            rockPattern[row][col] = rockBinary.charAt(i) - '0'; // ASCII '0' = 48, so ASCII '1' which is 49 - (48) = 1
        }
        return rockPattern;
    }

    public String boardToBinary(int[][] rockPattern) {
        String rockBinary = "";
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                rockBinary += rockPattern[i][j];
            }
        }
        return rockBinary;
    }

    public int[][] rockSymmetry(int[][] rockPattern, int rockSymmetryNum) {
        int[][] newRockPattern = new int[boardSize][boardSize];

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                switch (rockSymmetryNum) {
                    case 0: // 0° rotation
                        newRockPattern[i][j] = rockPattern[i][j];
                        break;
                    case 1: // 90° rotation
                        newRockPattern[j][boardSize - 1 - i] = rockPattern[i][j];
                        break;
                    case 2: // 180° rotation
                        newRockPattern[boardSize - 1 - i][boardSize - 1 - j] = rockPattern[i][j];
                        break;
                    case 3: // 270° rotation
                        newRockPattern[boardSize - 1 - j][i] = rockPattern[i][j];
                        break;
                    case 4: // 0° rotation + left-right mirror
                        newRockPattern[i][boardSize - 1 - j] = rockPattern[i][j];
                        break;
                    case 5: // 90° rotation + top-bottom mirror
                        newRockPattern[boardSize - 1 - j][boardSize - 1 - i] = rockPattern[i][j];
                        break;
                    case 6: // 180° rotation + left-right mirror
                        newRockPattern[boardSize - 1 - i][j] = rockPattern[i][j];
                        break;
                    case 7: // 270° rotation + top-bottom mirror
                        newRockPattern[j][i] = rockPattern[i][j];
                        break;
                    default: // Original rotation
                        newRockPattern[i][j] = rockPattern[i][j];
                        break;
                }
            }
        }
        return newRockPattern;
    }

    // Print board to console
    public void printBoard(int[][] rockPattern) {
        // Top border
        System.out.print("\033[94m  ╔\033[0m");
        for (int j = 0; j < boardSize - 1; j++) {
            System.out.print("\033[94m════╦\033[0m");
        }
        System.out.println("\033[94m════╗\033[0m");

        for (int i = 0; i < boardSize; i++) {
            System.out.print(i + "\033[94m ║\033[0m");
            for (int j = 0; j < boardSize; j++) {
                if (rockPattern[i][j] == 1) {
                    System.out.print("\033[90m ██ \033[0m"); // Rock - in Grey
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
    }
}
