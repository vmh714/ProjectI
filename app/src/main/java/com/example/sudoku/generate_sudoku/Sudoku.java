package com.example.sudoku.generate_sudoku;

import java.lang.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sudoku
{
    //Mảng 2 chiều lưu trữ các số hiển thị cho người chơi (bảng Sudoku với các ô trống)
    private static int[][] visibleNumbersMatrix;
    //Mảng 2 chiều lưu trữ bảng Sudoku đã được giải hoàn chỉnh
    private static int[][] keyMatrix;
    // Mảng 2 chiều lưu trữ các câu trả lời của người dùng.
    private static int[][] answerUser;
    //Biến đếm số lượng câu trả lời đúng
    private static int countCorrectAnswers;
    private int N; // Kích thước của bảng Sudoku 9
    private int SRN; // Căn bậc hai của N 3
    private int K; // Số lượng ô trống cần tạo -độ khó
    //Tọa độ của ô đang chọn
    private static int selected_row;
    private static int selected_column;
    private static Difficult difficult; // Enum xác định độ khó
    public enum Difficult {
        Easy,
        Normal,
        Hard
    }
    private static Sudoku sudoku;

    //Hằng số xác định các ô trống tương ứng với mức độ khó
    private static final int EASY   = 1;
    private static final int NORMAL = 43;
    private static final int HARD   = 50;

    // Constructor
    private Sudoku(int N, int K)
    {
        this.N = N;
        this.K = K;

        countCorrectAnswers = K;//số ô che tương ứng với số ô cần điền để chiến thắng
        selected_column = -1;//tọa độ ô được chọn ban đầu
        selected_row    = -1;
        // Compute square root of N
        Double SRNd = Math.sqrt(N);
        SRN = SRNd.intValue();
        answerUser = new int[N][N];//khởi tạo mảng chứa câu trả lời
        visibleNumbersMatrix = new int[N][N];//khởi tạo mảng chứa câu đố
    }
    //Phương thức trả về mảng chứa câu đố, khởi tạo mặc định với độ khó là dễ
    public static int[][] getVisibleNumbersMatrix() {
        if(visibleNumbersMatrix == null) {
            generateSudoku(Difficult.Easy);
        }
        return visibleNumbersMatrix;
    }
    // getter lấy độ khó
    public static Difficult getDifficult() {
        return difficult;
    }
    public static int[][] generateSudoku(Difficult difficult) {
        keyMatrix = new int[9][9];
        Sudoku.difficult = difficult;
        switch (difficult) {// Xác định độ khó và tạo một bảng sudoku đó theo độ khó đã chọn
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
        sudoku.fillValues();//điền số vào mảng câu đố
        //sao chép mảng câu đố vào mảng lời giải
        for(int i = 0; i < sudoku.visibleNumbersMatrix.length; i++) {
            for(int j = 0; j < sudoku.visibleNumbersMatrix.length; j++) {
                sudoku.keyMatrix[i][j] = sudoku.visibleNumbersMatrix[i][j];

            }
        }
        // hoàn thiện mảng câu đố bằng cách che đi K ô tương ứng với độ khó
        sudoku.removeKDigits();
        return sudoku.visibleNumbersMatrix;
    }
    //kiểm tra xem số nhập vào có trùng với kết quả không
    public static boolean checkNumInCell(int i, int j, int num) {
        return keyMatrix[i][j] == num;
    }
    //kiểm tra xem người chơi đã thắng chưa
    public static boolean checkWin() {
        countCorrectAnswers = 81;
        //tính số câu trả lời đúng bằng cách dựa vào số ô trống ban đầu và số ô trả lời đúng
        countCorrectAnswers -= difficult == Difficult.Easy ? EASY :
                difficult == Difficult.Normal ? NORMAL : HARD;
        for(int i = 0; i < answerUser.length; i++) {
            for(int j = 0; j < answerUser[i].length; j++) {
                countCorrectAnswers += checkNumInCell(i, j, answerUser[i][j]) ? 1: 0;
            }
        }
        return countCorrectAnswers == 81;
    }
//    public static boolean checkWin() {
//        // Kiểm tra xem tất cả các ô đã được điền chưa
//        if (!isBoardFilled()) {
//            return false; // Chưa điền xong
//        }
//
//        // Nếu đã điền xong, kiểm tra tính đúng sai
//        for (int i = 0; i < answerUser.length; i++) {
//            for (int j = 0; j < answerUser[i].length; j++) {
//                if (answerUser[i][j] != keyMatrix[i][j]) {
//                    // Có ít nhất một ô sai
//                    return false;
//                }
//            }
//        }
//
//        // Tất cả các ô đều đúng
//        return true;
//    }
//
//    // Phương thức kiểm tra xem bảng đã được điền đầy chưa
//    private static boolean isBoardFilled() {
//        for (int i = 0; i < answerUser.length; i++) {
//            for (int j = 0; j < answerUser[i].length; j++) {
//                if (answerUser[i][j] == 0) {
//                    return false; // Vẫn còn ô trống
//                }
//            }
//        }
//        return true; // Tất cả các ô đã được điền
//    }

    // Sudoku Generator
    private void fillValues()
    {
        // điền các số trong các lưới 3x3 nhỏ trước
        fillDiagonal();
        // điền các số vào các ô còn lại
        fillRemaining(0, SRN);
    }

    private void fillDiagonal()
    {
        for (int i = 0; i<N; i=i+SRN)
            // điền các số ngẫu nhiên trong các ô 3x3 nhỏ có tọa độ chéo
            fillBox(i, i);
    }


    // điền các ô trong lưới 3x3
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
    //điền các ô còn lại đảm bảo điều kiện của bảng sudoku
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

    // Kiểm tra xem điền số vào ô có tọa độ i,j có hợp lệ không
    private boolean CheckIfSafe(int i,int j,int num)
    {
        return (unUsedInRow(i, num) &&
                unUsedInCol(j, num) &&
                unUsedInBox(i-i%SRN, j-j%SRN, num));
    }
    // kiểm tra số đã có trong hàng chưa
    private boolean unUsedInRow(int i,int num)
    {
        for (int j = 0; j<N; j++)
            if (visibleNumbersMatrix[i][j] == num)
                return false;
        return true;
    }

    // kiểm tra số đã có trong cột chưa
    private boolean unUsedInCol(int j,int num)
    {
        for (int i = 0; i<N; i++)
            if (visibleNumbersMatrix[i][j] == num)
                return false;
        return true;
    }
    //kiểm tra số đã có trong lưới 3x3 chưa
    private boolean unUsedInBox(int rowStart, int colStart, int num)
    {
        for (int i = 0; i<SRN; i++)
            for (int j = 0; j<SRN; j++)
                if (visibleNumbersMatrix[rowStart+i][colStart+j]==num)
                    return false;

        return true;
    }
    // trả một số ngẫu nhiên từ 1 đến 9
    private int randomGenerator(int num)
    {
        return (int) Math.floor((Math.random()*num+1));
    }
    // xóa ngẫu nhiên K ô để tạo thành các ô trống
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
//    private void removeKDigits() {
//        int count = K;
//        List<Integer> cellIds = new ArrayList<>();
//        for (int i = 0; i < N * N; i++) {
//            cellIds.add(i);
//        }
//        Collections.shuffle(cellIds); // Xáo trộn thứ tự các ô
//
//        while (count != 0 && !cellIds.isEmpty()) {
//            int cellId = cellIds.remove(0);
//            int i = cellId / N;
//            int j = cellId % N;
//
//            // Lưu lại giá trị hiện tại để khôi phục nếu cần
//            int temp = visibleNumbersMatrix[i][j];
//            if (temp == 0) continue; // Ô đã trống
//
//            visibleNumbersMatrix[i][j] = 0;
//
//            // Kiểm tra xem Sudoku có còn giải duy nhất sau khi xóa
//            if (hasUniqueSolution()) {
//                count--;
//            } else {
//                // Khôi phục giá trị nếu việc xóa tạo ra nhiều lời giải
//                visibleNumbersMatrix[i][j] = temp;
//            }
//        }
//    }
//
//    // Phương thức kiểm tra tính duy nhất của lời giải
//    private boolean hasUniqueSolution() {
//        int[][] tempBoard = new int[N][N];
//        for (int i = 0; i < N; i++) {
//            System.arraycopy(visibleNumbersMatrix[i], 0, tempBoard[i], 0, N);
//        }
//
//        return countSolutions(tempBoard) == 1;
//    }
//
//    // Phương thức đệ quy để đếm số lượng lời giải
//    private int countSolutions(int[][] board) {
//        int[] emptyCell = findEmptyCell(board);
//        if (emptyCell == null) {
//            return 1; // Không còn ô trống, đây là một lời giải
//        }
//
//        int row = emptyCell[0];
//        int col = emptyCell[1];
//        int count = 0;
//
//        for (int num = 1; num <= N; num++) {
//            if (CheckIfSafe(row, col, num)) {
//                board[row][col] = num;
//                count += countSolutions(board);
//                board[row][col] = 0; // Backtrack
//
//                // Nếu đã tìm thấy hơn 1 lời giải, dừng lại
//                if (count > 1) {
//                    return count;
//                }
//            }
//        }
//        return count;
//    }
//
//    // Phương thức tìm ô trống đầu tiên
//    private int[] findEmptyCell(int[][] board) {
//        for (int i = 0; i < N; i++) {
//            for (int j = 0; j < N; j++) {
//                if (board[i][j] == 0) {
//                    return new int[]{i, j};
//                }
//            }
//        }
//        return null; // Không tìm thấy ô trống
//    }
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