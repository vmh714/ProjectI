package com.example.sudoku.generate_sudoku;

import java.lang.*;
import java.util.ArrayList;
import java.util.List;

public class Sudoku
{
    private static int[][] visibleNumbersMatrix;
    //keyMatrix - filled sudoku
    private static int[][] keyMatrix;
    private static int[][] answerUser;
    //remainsArrange -
    private static int countCorrectAnswers;
    private int N; // number of columns/rows.
    private int SRN; // square root of N
    private int K; // No. Of missing digits
    private static int selected_row;
    private static int selected_column;
    private static Difficult difficult;
    public enum Difficult {
        Easy,
        Normal,
        Hard
    }
    private static Sudoku sudoku;

    //The difficult define num of missing digits
    private static final int EASY   = 35;
    private static final int NORMAL = 43;
    private static final int HARD   = 50;

    // Constructor
    private Sudoku(int N, int K)
    {
        this.N = N;
        this.K = K;

        countCorrectAnswers = K;

        selected_column = -1;
        selected_row    = -1;

        // Compute square root of N
        Double SRNd = Math.sqrt(N);
        SRN = SRNd.intValue();

        answerUser = new int[N][N];
        visibleNumbersMatrix = new int[N][N];
    }
    public static int[][] getVisibleNumbersMatrix() {
        if(visibleNumbersMatrix == null) {
            generateSudoku(Difficult.Easy);
        }
        return visibleNumbersMatrix;
    }
    public static Difficult getDifficult() {
        return difficult;
    }
    public static int[][] generateSudoku(Difficult difficult) {

        keyMatrix = new int[9][9];
        Sudoku.difficult = difficult;

        switch (difficult) {

            case Easy: {
                sudoku = new Sudoku(9, EASY);
                break;
            }

            case Normal: {
                sudoku = new Sudoku(9, NORMAL);
                break;
            }

            case Hard: {
                sudoku = new Sudoku(9, HARD);
                break;
            }
        }

        sudoku.fillValues();

        for(int i = 0; i < sudoku.visibleNumbersMatrix.length; i++) {
            for(int j = 0; j < sudoku.visibleNumbersMatrix.length; j++) {

                sudoku.keyMatrix[i][j] = sudoku.visibleNumbersMatrix[i][j];

            }
        }

        sudoku.removeKDigits();

        return sudoku.visibleNumbersMatrix;

    }

    public static boolean checkNumInCell(int i, int j, int num) {

        return keyMatrix[i][j] == num;

    }

    public static boolean checkWin() {

        countCorrectAnswers = 81;
        countCorrectAnswers -= difficult == Difficult.Easy ? EASY :
                difficult == Difficult.Normal ? NORMAL : HARD;

        for(int i = 0; i < answerUser.length; i++) {

            for(int j = 0; j < answerUser[i].length; j++) {

                countCorrectAnswers += checkNumInCell(i, j, answerUser[i][j]) ? 1: 0;

            }
        }

        return countCorrectAnswers == 81;
    }

    // Sudoku Generator
    private void fillValues()
    {
        // Fill the diagonal of SRN x SRN matrices
        fillDiagonal();

        // Fill remaining blocks
        fillRemaining(0, SRN);

    }

    // Fill the diagonal SRN number of SRN x SRN matrices
    private void fillDiagonal()
    {

        for (int i = 0; i<N; i=i+SRN)

            // for diagonal box, start coordinates->i==j
            fillBox(i, i);
    }

    // Returns false if given 3 x 3 block contains num.
    private boolean unUsedInBox(int rowStart, int colStart, int num)
    {
        for (int i = 0; i<SRN; i++)
            for (int j = 0; j<SRN; j++)
                if (visibleNumbersMatrix[rowStart+i][colStart+j]==num)
                    return false;

        return true;
    }

    // Fill a 3 x 3 matrix.
    private void fillBox(int row,int col)
    {
        int num;
        for (int i=0; i<SRN; i++)
        {
            for (int j=0; j<SRN; j++)
            {
                do
                {
                    num = randomGenerator(N);
                }
                while (!unUsedInBox(row, col, num));

                visibleNumbersMatrix[row+i][col+j] = num;
            }
        }
    }

    // Random generator
    private int randomGenerator(int num)
    {
        return (int) Math.floor((Math.random()*num+1));
    }

    // Check if safe to put in cell
    private boolean CheckIfSafe(int i,int j,int num)
    {
        return (unUsedInRow(i, num) &&
                unUsedInCol(j, num) &&
                unUsedInBox(i-i%SRN, j-j%SRN, num));
    }

    // check in the row for existence
    private boolean unUsedInRow(int i,int num)
    {
        for (int j = 0; j<N; j++)
            if (visibleNumbersMatrix[i][j] == num)
                return false;
        return true;
    }

    // check in the row for existence
    private boolean unUsedInCol(int j,int num)
    {
        for (int i = 0; i<N; i++)
            if (visibleNumbersMatrix[i][j] == num)
                return false;
        return true;
    }

    // A recursive function to fill remaining
    // matrix
    private boolean fillRemaining(int i, int j)
    {
        // System.out.println(i+" "+j);
        if (j>=N && i<N-1)
        {
            i = i + 1;
            j = 0;
        }
        if (i>=N && j>=N)
            return true;

        if (i < SRN)
        {
            if (j < SRN)
                j = SRN;
        }
        else if (i < N-SRN)
        {
            if (j==(int)(i/SRN)*SRN)
                j = j + SRN;
        }
        else
        {
            if (j == N-SRN)
            {
                i = i + 1;
                j = 0;
                if (i>=N)
                    return true;
            }
        }

        for (int num = 1; num<=N; num++)
        {
            if (CheckIfSafe(i, j, num))
            {
                visibleNumbersMatrix[i][j] = num;
                if (fillRemaining(i, j+1))
                    return true;

                visibleNumbersMatrix[i][j] = 0;
            }
        }
        return false;
    }

    // Remove the K no. of digits to
    // complete game
    private void removeKDigits()
    {
        int count = K;
        while (count != 0)
        {
            int cellId = randomGenerator(N*N)-1;

            // System.out.println(cellId);
            // extract coordinates i and j
            int i = (cellId/N);
            int j = cellId%9;
            if (j != 0)
                j = j - 1;

            // System.out.println(i+" "+j);
            if (visibleNumbersMatrix[i][j] != 0)
            {
                count--;
                visibleNumbersMatrix[i][j] = 0;
            }
        }
    }
    public static List<int[]> getCellsWithValue(int value) {
        List<int[]> cells = new ArrayList<>();
        if (value == 0) return cells;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (visibleNumbersMatrix[i][j] == value || answerUser[i][j] == value) {
                    cells.add(new int[]{i, j});
                }
            }
        }
        return cells;
    }
    public static int getSelectedColumn() {
        return selected_column;
    }

    public static void setSelectedColumn(int selected_column) {
        Sudoku.selected_column = selected_column;
    }

    public static int getSelectedRow() {
        return selected_row;
    }

    public static void setSelectedRow(int selected_row) {
        Sudoku.selected_row = selected_row;
    }

    public static int[][] getKeyMatrix() {
        return keyMatrix;
    }

    public static int[][] getAnswerUser() {
        return answerUser;
    }

    public static void setAnswerUser(int[][] answerUser) {
        Sudoku.answerUser = answerUser;
    }

    public static void setKeyMatrix(int[][] keyMatrix) {
        Sudoku.keyMatrix = keyMatrix;
    }

    public static void setVisibleNumbersMatrix(int[][] visibleNumbersMatrix) {
        Sudoku.visibleNumbersMatrix = visibleNumbersMatrix;
    }
    // Phương thức trả về bảng Sudoku của người dùng, bao gồm các số đã điền và các ô trống
    public static int[][] getUserBoard() {
        int[][] userBoard = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // Nếu ô đó có số (từ visibleNumbersMatrix), gán số đó vào bảng
                if (visibleNumbersMatrix[i][j] != 0) {
                    userBoard[i][j] = visibleNumbersMatrix[i][j];
                } else {
                    // Nếu ô đó trống, kiểm tra xem người dùng đã điền số nào vào chưa (từ answerUser)
                    userBoard[i][j] = answerUser[i][j]; // Nếu answerUser[i][j] == 0, ô đó vẫn trống
                }
            }
        }
        return userBoard;
    }
}