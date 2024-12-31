package com.example.sudoku.sudoku_board;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.sudoku.R;
import com.example.sudoku.generate_sudoku.Sudoku;

import java.util.List;

public class SudokuBoard extends View {
    private int cellSize;
    private final int boardColor;
    private int[][] sudoku;
    private int[][] answer;
    private final int cellFillColor;
    private final int cellsHighLightColor;
    private final int textColor;
    private final int textWrongAnswerColor;
    private final int textCorrectAnswerColor;
    private final Paint boardColorPaint = new Paint();
    private final Paint boardCellFillColor = new Paint();
    private final Paint boardCelsHighLightColor = new Paint();
    private final Paint textPaint = new Paint();
    private final Rect textBoundsRect = new Rect();
    private boolean[][] initialCells; // Ma trận boolean để lưu trạng thái ô đề bài
    private final int cellsValueHighLightColor;
    private final Paint boardCellValueHighLightColor = new Paint();

    public SudokuBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SudokuBoard,
                0, 0);

        try {
            boardColor = array.getInteger(R.styleable.SudokuBoard_boardColor, 0);
            cellFillColor = array.getInteger(R.styleable.SudokuBoard_cellFillColor, 0);
            cellsHighLightColor = array.getInteger(R.styleable.SudokuBoard_cellsHighLightColor, 0);
            textWrongAnswerColor = array.getInteger(R.styleable.SudokuBoard_textWrongAnswerColor, 0);
            textCorrectAnswerColor = array.getInteger(R.styleable.SudokuBoard_textCorrectAnswerColor, 0);
            cellsValueHighLightColor = array.getInteger(R.styleable.SudokuBoard_cellsValueHighLightColor, 0);
            textColor = array.getInteger(R.styleable.SudokuBoard_textColor, 0);
            sudoku = Sudoku.getVisibleNumbersMatrix();
            answer = Sudoku.getAnswerUser();

            initialCells = new boolean[9][9];
            // Đánh dấu các ô đề bài
            initialCells = new boolean[9][9];
            // Đánh dấu các ô đề bài
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    initialCells[i][j] = sudoku[i][j] != 0;
                }
            }

        } finally {
            array.recycle();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            int col = (int) Math.ceil(x / cellSize);
            int row = (int) Math.ceil(y / cellSize);

            // Kiểm tra nếu chạm ngoài bảng
            if (col < 1 || col > 9 || row < 1 || row > 9) {
                resetSelectedCell();
                invalidate(); // Cập nhật lại giao diện
                return true;
            }

            // Nếu ô đang được chọn, bỏ chọn nó đi
            if (Sudoku.getSelectedColumn() == col && Sudoku.getSelectedRow() == row) {
                resetSelectedCell();
            } else {
                Sudoku.setSelectedColumn(col);
                Sudoku.setSelectedRow(row);
            }
            invalidate(); // Cập nhật lại giao diện sau khi chọn ô
            return true;
        }
        return false;
    }
public boolean setNumInCell(int num) {
    if (Sudoku.getSelectedColumn() != -1 && Sudoku.getSelectedRow() != -1) {
        int row = Sudoku.getSelectedRow() - 1;
        int col = Sudoku.getSelectedColumn() - 1;

        // Kiểm tra nếu là ô đề bài thì không thay đổi giá trị trong answer
        if (!isInitialCell(row, col)) {
            // Kiểm tra nếu num == 0 thì xóa ô đó
            if (num == 0) {
                answer[col][row] = 0;
            } else {
                answer[col][row] = num;
            }

            Log.println(Log.INFO, "Selected cell", " i = " + col + "j = " + row);
            invalidate();
            return Sudoku.checkNumInCell(col, row, num);
        }
    }
    return true;
}

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);

        int dimension = Math.min(getMeasuredWidth(), getMeasuredHeight());

        cellSize = dimension / 9;

        setMeasuredDimension(dimension, dimension);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(16);
        boardColorPaint.setColor(boardColor);
        boardColorPaint.setAntiAlias(true);

        boardCellFillColor.setStyle(Paint.Style.FILL);
        boardCellFillColor.setColor(cellFillColor);

        boardCelsHighLightColor.setStyle(Paint.Style.FILL);
        boardCelsHighLightColor.setColor(cellsHighLightColor);

        boardCellValueHighLightColor.setStyle(Paint.Style.FILL);
        boardCellValueHighLightColor.setColor(cellsValueHighLightColor);

        colorCell(canvas, Sudoku.getSelectedColumn(), Sudoku.getSelectedRow());

        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), boardColorPaint);

        drawBoard(canvas);

        drawText(canvas);
    }

private void colorCell(Canvas canvas, int c, int r) {
    if (c != -1 && r != -1) {
        int selectedValue = 0;

        // Tô màu cho hàng và cột
        canvas.drawRect((c - 1) * cellSize, 0, c * cellSize, 9 * cellSize, boardCelsHighLightColor);
        canvas.drawRect(0, (r - 1) * cellSize, cellSize * 9, cellSize * r, boardCelsHighLightColor);

        // Lấy giá trị của ô được chọn
        if (!isInitialCell(r - 1, c - 1)) {
            selectedValue = answer[c - 1][r - 1];
        } else {
            selectedValue = sudoku[c-1][r-1];
        }


        // Highlight các ô có cùng giá trị
        if (selectedValue != 0) {
            List<int[]> cellsToHighlight = Sudoku.getCellsWithValue(selectedValue);
            for (int[] cell : cellsToHighlight) {
                canvas.drawRect(cell[0] * cellSize, cell[1] * cellSize, (cell[0] + 1) * cellSize, (cell[1] + 1) * cellSize, boardCellValueHighLightColor);
            }
        }

        // Tô màu ô được chọn (đè lên trên highlight)
        canvas.drawRect((c - 1) * cellSize, (r - 1) * cellSize, c * cellSize, r * cellSize, boardCellFillColor);
    }
}
    private void drawThickLine() {
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(10);
        boardColorPaint.setColor(boardColor);
        boardColorPaint.setAntiAlias(true);
    }

    private void drawThinLine() {
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(4);
        boardColorPaint.setColor(boardColor);
        boardColorPaint.setAntiAlias(true);
    }

    private void drawText(Canvas canvas) {
        textPaint.setTextSize(cellSize);

        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku[i].length; j++) {
                if (sudoku[i][j] != 0) {
                    String text = String.valueOf(sudoku[i][j]);
                    textPaint.setColor(textColor);
                    float width, height;
                    textPaint.getTextBounds(text, 0, text.length(), textBoundsRect);
                    width = textPaint.measureText(text);
                    height = textBoundsRect.height();
                    canvas.drawText(text, (i * cellSize) + ((cellSize - width) / 2),
                            (j * cellSize + cellSize) - ((cellSize - height) / 2), textPaint);
                }
            }
        }
        textPaint.setColor(textCorrectAnswerColor);
        for (int i = 0; i < answer.length; i++) {
            for (int j = 0; j < answer[i].length; j++) {
                //Nếu ô đó không phải ô đề bài
                if (!initialCells[i][j]) {
                    if (answer[i][j] != 0) {
                        String text = String.valueOf(answer[i][j]);
                        if (Sudoku.checkNumInCell(i, j, answer[i][j])) {
                            textPaint.setColor(textCorrectAnswerColor);
                        } else {
                            textPaint.setColor(textWrongAnswerColor);
                        }
                        float width, height;
                        textPaint.getTextBounds(text, 0, text.length(), textBoundsRect);
                        width = textPaint.measureText(text);
                        height = textBoundsRect.height();
                        canvas.drawText(text, (i * cellSize) + ((cellSize - width) / 2),
                                (j * cellSize + cellSize) - ((cellSize - height) / 2), textPaint);
                    }
                }
            }
        }
    }

    private void drawBoard(Canvas canvas) {
        for (int c = 0; c < 10; c++) {
            if (c % 3 == 0) {
                drawThickLine();
            } else {
                drawThinLine();
            }
            canvas.drawLine(cellSize * c, 0, cellSize * c, getWidth(), boardColorPaint);
        }

        for (int r = 0; r < 10; r++) {
            if (r % 3 == 0) {
                drawThickLine();
            } else {
                drawThinLine();
            }
            canvas.drawLine(0, cellSize * r, getWidth(), cellSize * r, boardColorPaint);
        }
    }

    public int[][] getAnswer() {
        return answer;
    }

    public boolean isInitialCell(int row, int col) {
        if (row < 0 || row > 8 || col < 0 || col > 8) {
            return false;
        }
        return initialCells[col][row]; // Lưu ý: đổi lại thành col, row
    }

    // Phương thức lấy giá trị của selectedRow
    public int getSelectedRow() {
        return Sudoku.getSelectedRow();
    }

    // Phương thức lấy giá trị của selectedColumn
    public int getSelectedColumn() {
        return Sudoku.getSelectedColumn();
    }
    public void resetSelectedCell() {
        Sudoku.setSelectedRow(-1);
        Sudoku.setSelectedColumn(-1);
    }
}