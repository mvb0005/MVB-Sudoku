package MVB;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Sudoku {
    public int[] board = new int[81];
    public NumberMask[] numberMasks = new NumberMask[9];

    public int num0s = 81;

    public Sudoku(String sudoku) {
        for (int i = 0; i < sudoku.length(); i++) {
            board[i] = Integer.valueOf(sudoku.substring(i, i + 1));
            if (board[i] != 0) {
                num0s--;
            }
        }
        for (int i = 0; i < 9; i++) {
            numberMasks[i] = new NumberMask(i + 1, board);
        }
    }

    public boolean solve() {
        while (num0s != 0 && solveStep()) {
        }
        return num0s == 0;
    }

    public boolean solveStep() {
        int startNum0s = num0s;
        Set<Integer> solvedIndexes;
        for (int i = 0; i < 9; i++) {
            solvedIndexes = numberMasks[i].scan();
            for (int index : solvedIndexes) {
                falsifyIndex(index);
                board[index] = i + 1;
            }
        }
        return startNum0s != num0s;
    }

    public void falsifyIndex(int index) {
        for (NumberMask numberMask : numberMasks) {
            numberMask.falsifyIndex(index);
        }
        num0s--;
    }

    public String toCheckString() {
        String returnString = "";
        for (int i = 0; i < 81; i++) {
            returnString += board[i];
        }
        return returnString;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 81; i++) {
            if (i % 3 == 0 && i > 0) {
                sb.append("|");
            }
            if (i % 9 == 0) {
                sb.append("\n|");
            }
            if (i % 27 == 0) {
                sb.append("---+---+---|\n|");
            }
            sb.append(board[i]);
        }
        sb.append("|\n|---+---+---|\n");
        return sb.toString();
    }

    private class NumberMask {
        public boolean[] possibleLocations = new boolean[81];
        public int number;

        public NumberMask(int desirednumber, int[] board) {
            Arrays.fill(possibleLocations, true);
            number = desirednumber;
            for (int i = 0; i < 81; i++) {
                int currentNum = board[i];
                if (currentNum != 0) {
                    if (currentNum != number) {
                        possibleLocations[i] = false;
                    } else {
                        Set<Integer> s = new HashSet<Integer>();
                        s.addAll(getHorizontalRow(i));
                        s.addAll(getVerticalRow(i));
                        s.addAll(getCell(i));
                        for (int index : s) {
                            possibleLocations[index] = false;
                        }
                    }
                }
            }
        }

        void falsifyPositions(Set<Integer> positions) {
            for (int i : positions) {
                possibleLocations[i] = false;
            }
        }

        void falsifyIndex(int index) {
            possibleLocations[index] = false;
        }

        Set<Integer> scan() {
            Set<Integer> solvedPositions = new HashSet<Integer>();
            int currentWinner;
            for (int i = 0; i < 9; i++) {
                currentWinner = checkHorizontal(solvedPositions, i);
                if (currentWinner != -1) {
                    solvedPositions.add(currentWinner);
                }
                currentWinner = checkVertical(solvedPositions, i);
                if (currentWinner != -1) {
                    solvedPositions.add(currentWinner);
                }
                currentWinner = checkCell(solvedPositions, i);
                if (currentWinner != -1) {
                    solvedPositions.add(currentWinner);
                }
            }
            return solvedPositions;
        }

        private int checkHorizontal(Set<Integer> solvedPositions, int indexToSolve) {
            int currentWinner;
            Set<Integer> currentSet = new HashSet<>();
            currentSet.addAll(getHorizontalRow(indexToSolve * 9));
            currentWinner = returnWinner(currentSet);
            if (currentWinner != -1) {
                currentSet.addAll(getVerticalRow(currentWinner));
                currentSet.addAll(getCell(currentWinner));
                falsifyPositions(currentSet);
                return currentWinner;
            }
            return -1;
        }

        private int checkVertical(Set<Integer> solvedPositions, int indexToSolve) {
            int currentWinner;
            Set<Integer> currentSet = new HashSet<>();
            currentSet.addAll(getVerticalRow(indexToSolve));
            currentWinner = returnWinner(currentSet);
            if (currentWinner != -1) {
                currentSet.addAll(getHorizontalRow(currentWinner));
                currentSet.addAll(getCell(currentWinner));
                falsifyPositions(currentSet);
                return currentWinner;
            }
            return -1;
        }

        private int checkCell(Set<Integer> solvedPositions, int indexToSolve) {
            int currentWinner;
            Set<Integer> currentSet = new HashSet<>();
            currentSet.addAll(getCell(toOneD(indexToSolve * 3 % 9, indexToSolve)));
            currentWinner = returnWinner(currentSet);
            if (currentWinner != -1) {
                currentSet.addAll(getVerticalRow(currentWinner));
                currentSet.addAll(getHorizontalRow(currentWinner));
                falsifyPositions(currentSet);
                return currentWinner;
            }
            return -1;
        }

        int returnWinner(Set<Integer> squaresToCheck) {
            int winner = -1;
            for (int i : squaresToCheck) {
                if (possibleLocations[i]) {
                    if (winner == -1) {
                        winner = i;
                    } else {
                        return -1;
                    }
                }
            }
            return winner;
        }

        ArrayList<Integer> getVerticalRow(int index) {
            ArrayList<Integer> verticalRowList = new ArrayList<Integer>();
            int offset = index % 9;
            for (int i = 0; i < 81; i += 9) {
                verticalRowList.add(i + offset);
            }
            return verticalRowList;
        }

        ArrayList<Integer> getHorizontalRow(int index) {
            ArrayList<Integer> horizontalRowList = new ArrayList<Integer>();
            int offset = index % 9;
            for (int i = 0; i < 9; i++) {
                horizontalRowList.add(index - offset + i);
            }
            return horizontalRowList;
        }

        ArrayList<Integer> getCell(int index) {
            ArrayList<Integer> cellList = new ArrayList<Integer>();
            int xoffset = index % 9 / 3;
            int yoffset = index / 9 / 3;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    cellList.add(toOneD(xoffset * 3 + i, yoffset * 3 + j));
                }
            }
            return cellList;
        }

        int toOneD(int x, int y) {
            return y * 9 + x;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 81; i++) {
                if (i % 3 == 0 && i > 0) {
                    sb.append("|");
                }
                if (i % 9 == 0) {
                    sb.append("\n|");
                }
                if (i % 27 == 0) {
                    sb.append("---+---+---|\n|");
                }
                sb.append(possibleLocations[i] ? 1 : 0);
            }
            sb.append("|\n|---+---+---|\n" + number + "\n");
            return sb.toString();
        }
    }
}