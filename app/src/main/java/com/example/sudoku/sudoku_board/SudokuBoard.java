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
    private final Rect  textBoundsRect = new Rect();

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
            textColor = array.getInteger(R.styleable.SudokuBoard_textColor, 0);
            sudoku = Sudoku.getVisibleNumbersMatrix();
            answer = Sudoku.getAnswerUser();

        } finally {
            array.recycle();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isValid;

        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();

        if(action == MotionEvent.ACTION_DOWN) {

            Sudoku.setSelectedColumn((int) Math.ceil(x/cellSize));
            Sudoku.setSelectedRow((int) Math.ceil(y/cellSize));

            isValid = true;
        }
        else {
            isValid = false;
        }

        return isValid;
    }

    public boolean setNumInCell(int num) {
        if(Sudoku.getSelectedColumn() != -1 && Sudoku.getSelectedRow() != -1) {

            answer[Sudoku.getSelectedColumn()-1][Sudoku.getSelectedRow()-1] = num;
            Log.println(Log.INFO, "Selected cell", " i = "  + Sudoku.getSelectedColumn() + "j = " + Sudoku.getSelectedRow());
            return Sudoku.checkNumInCell(Sudoku.getSelectedColumn()-1, Sudoku.getSelectedRow()-1, num);
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
        boardCellFillColor.setStrokeWidth(16);
        boardCellFillColor.setColor(cellFillColor);

        boardCelsHighLightColor.setStyle(Paint.Style.FILL);
        boardCelsHighLightColor.setStrokeWidth(16);
        boardCelsHighLightColor.setColor(cellsHighLightColor);

        colorCell(canvas, Sudoku.getSelectedColumn(), Sudoku.getSelectedRow());

        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), boardColorPaint);

        drawBoard(canvas);

        drawText(canvas);
    }

    private void colorCell(Canvas canvas, int c, int r) {

        if(c != -1 && r != -1 && sudoku[c-1][r-1] == 0 ) {

            canvas.drawRect((c-1) * cellSize, 0, c*cellSize, 9*cellSize,
                    boardCelsHighLightColor);

            canvas.drawRect(0, (r-1)*cellSize, cellSize*9, cellSize*r,
                    boardCelsHighLightColor);

            canvas.drawRect((c-1)*cellSize, r*cellSize, c*cellSize, (r-1)*cellSize,
                    boardCellFillColor);
        }
        else {
            Sudoku.setSelectedColumn(-1);
            Sudoku.setSelectedRow(-1);
        }
        invalidate();
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
        textPaint.setColor(textColor);
        textPaint.setStrokeWidth(cellSize);

        for(int i = 0; i < sudoku.length; i++) {

            for(int j = 0; j < sudoku[i].length; j++) {

                textPaint.setColor(textColor);

                if(sudoku[i][j] != 0) {

                    textPaint.setColor(textColor);
                    String text = sudoku[i][j] + "";

                    textPaint.getTextBounds(text, 0, 1, textBoundsRect);

                    float width = textPaint.measureText(text);
                    float height = textBoundsRect.height();

                    canvas.drawText(text, (i * cellSize) + (cellSize - width)/2,
                            (j+1)*cellSize - (cellSize-height)/2, textPaint);
                }

                if(answer[i][j] != 0) {
                    if(Sudoku.checkNumInCell(i, j, answer[i][j])) {
                        textPaint.setColor(textCorrectAnswerColor);
                    }
                    else {
                        textPaint.setColor(textWrongAnswerColor);
                    }

                    String text = answer[i][j] + "";

                    textPaint.getTextBounds(text, 0, 1, textBoundsRect);

                    float width = textPaint.measureText(text);
                    float height = textBoundsRect.height();

                    canvas.drawText(text, (i * cellSize) + (cellSize - width)/2,
                            (j+1)*cellSize - (cellSize-height)/2, textPaint);
                }

            }
        }
    }

    private void drawBoard(Canvas canvas) {
        for(int c = 0; c < 10; c++) {
            if(c%3 == 0) {
                drawThickLine();
            }
            else {
                drawThinLine();
            }
            canvas.drawLine(cellSize * c , 0, cellSize * c, getWidth(), boardColorPaint);
        }

        for (int r = 0; r < 10; r++) {
            if(r%3 == 0) {
                drawThickLine();
            }
            else {
                drawThinLine();
            }
            canvas.drawLine(0, cellSize * r, getWidth(), cellSize * r, boardColorPaint);
        }
    }

    public  int[][] getAnswer() {
        return answer;
    }
}
