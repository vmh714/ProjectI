package com.example.sudoku;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sudoku.generate_sudoku.Sudoku;
import com.example.sudoku.sudoku_board.SudokuBoard;

public class ActivityGame extends AppCompatActivity implements Runnable {

    private SudokuBoard sudokuBoard;
    private TextView textViewMistakeCount;
    private TextView textViewTimer;
    private int countMistake;
    private int seconds;
    private boolean isStop;
    private boolean isPaused = false;
    private boolean allowTick = false;
    private Handler handlerTimer;
    private TextView textViewDifficult;
    private boolean[] isNumCompleted; //mảng boolean để theo dõi trạng thái hoàn thành của các số
//    private static final String FILE = "saving.json";
    private final String mistakes = "Mistake";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        countMistake = 0;
        seconds = 0;
        textViewMistakeCount = findViewById(R.id.textViewMistakeCount);
        textViewMistakeCount.setText(mistakes + " " + countMistake + "/" + 3);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewTimer.setText("00:00");
        sudokuBoard = findViewById(R.id.sudokuBoard);
        //Intent intent = getIntent();
        handlerTimer = new Handler();

        isNumCompleted = new boolean[9]; // Khởi tạo mảng với 9 phần tử
        for (int i = 0; i < 9; i++) {
            isNumCompleted[i] = false; // Khởi tạo tất cả các số đều chưa hoàn thành
        }

        textViewDifficult = findViewById(R.id.textViewDifficult);

        // Lấy độ khó và hiển thị
        Sudoku.Difficult difficult = Sudoku.getDifficult();
        String difficultStr = difficult.toString(); // Chuyển enum Difficult thành String
        textViewDifficult.setText(difficultStr);

        Button buttonPause = findViewById(R.id.buttonPause);
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePause();
            }
        });
        checkAndUpdateButtonVisibility();
        startGame();
    }

    private void startGame() {
        allowTick = true;
        handlerTimer.postDelayed(this::run, 1000);
    }

    // Xử lý tạm dừng/tiếp tục
    private void handlePause() {
        isPaused = !isPaused;
        if (isPaused) {
            allowTick = false; // Không cho phép onTick() chạy khi tạm dừng
            sudokuBoard.setEnabled(false);
            // Tạo dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_pause, null);
            Button buttonResume = dialogView.findViewById(R.id.buttonResume);
            Button buttonExit = dialogView.findViewById(R.id.buttonExit);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            //Khi người dùng nhấn ra ngoài dialog sẽ không bị mất
            dialog.setCanceledOnTouchOutside(false);

            // Xử lý sự kiện cho nút "Resume"
            buttonResume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    isPaused = false;
                    allowTick = true;
                    sudokuBoard.setEnabled(true);
                }
            });

            // Xử lý sự kiện cho nút "Exit"
            buttonExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    isPaused = false;
                    allowTick = false;
                    Intent intent = new Intent(ActivityGame.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            // Hiển thị dialog
            dialog.show();
        } else {
            allowTick = true; // Cho phép onTick() chạy khi tiếp tục
            sudokuBoard.setEnabled(true);
        }
    }

    public void onTick() {
        if (!isStop) {
            this.seconds++;
            int seconds = this.seconds % 60;
            int minutes = this.seconds / 60;
            textViewTimer.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }

    @Override
    public void onStop() {
        isStop = true;
        super.onStop();
    }

    @Override
    public void onStart() {
        isStop = false;
        super.onStart();
    }
    private void showAlterDialogGameOver() {

        DialogInterface.OnClickListener listener = (dialog, which) -> {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    Intent intentAgain = new Intent(this, ActivityGame.class);
                    startActivity(intentAgain);
                    Sudoku.generateSudoku(Sudoku.getDifficult());
                    finish();
                    break;

                case Dialog.BUTTON_NEGATIVE:
                    Intent intentExit = new Intent(this, MainActivity.class);
                    startActivity(intentExit);
                    finish();
                    break;
            }

        };

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.app_game_over);
        adb.setMessage(R.string.app_msg_game_again);
        adb.setPositiveButton(R.string.app_yes, listener);
        adb.setNegativeButton(R.string.app_no,  listener);
        adb.create();
        adb.show();
    }

    private void showAlterDialogWin() {

        DialogInterface.OnClickListener listener = (dialog, which) -> {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    Intent intentAgain = new Intent(this, ActivityGame.class);
                    startActivity(intentAgain);
                    Sudoku.generateSudoku(Sudoku.getDifficult());
                    finish();
                    break;

                case Dialog.BUTTON_NEGATIVE:
                    Intent intentExit = new Intent(this, MainActivity.class);
                    startActivity(intentExit);
                    finish();
                    break;
            }

        };

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.app_win);
        adb.setMessage(R.string.app_msg_game_again);
        adb.setPositiveButton(R.string.app_yes, listener);
        adb.setNegativeButton(R.string.app_no,  listener);
        adb.create();
        adb.show();
    }

    private void setNum(int num) {
        countMistake = sudokuBoard.setNumInCell(num) ? countMistake: countMistake + 1;
        textViewMistakeCount.setText(mistakes + " " + countMistake + "/" + 3);
        if(countMistake > 2) showAlterDialogGameOver();
        checkAndUpdateButtonVisibility();
        if(Sudoku.checkWin()) {
            showAlterDialogWin();
        }
    }
    // Phương thức kiểm tra và cập nhật trạng thái hiển thị của các nút số
    private void checkAndUpdateButtonVisibility() {
        int[][] board = Sudoku.getUserBoard();
        int[] numCounts = new int[9];

        // Đếm số lần xuất hiện của mỗi số trong bảng
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] != 0) {
                    numCounts[board[r][c] - 1]++;
                }
            }
        }

        // Cập nhật trạng thái hiển thị của các nút số
        for (int i = 0; i < 9; i++) {
            isNumCompleted[i] = numCounts[i] >= 9;
            Button button = getButtonByNumber(i + 1);
            if (button != null) {
                button.setVisibility(isNumCompleted[i] ? View.GONE : View.VISIBLE);
            }
        }
    }

    // Phương thức lấy Button tương ứng với số
    private Button getButtonByNumber(int num) {
        switch (num) {
            case 1:
                return findViewById(R.id.buttonOne);
            case 2:
                return findViewById(R.id.buttonTwo);
            case 3:
                return findViewById(R.id.buttonThree);
            case 4:
                return findViewById(R.id.buttonFour);
            case 5:
                return findViewById(R.id.buttonFive);
            case 6:
                return findViewById(R.id.buttonSix);
            case 7:
                return findViewById(R.id.buttonSeven);
            case 8:
                return findViewById(R.id.buttonEight);
            case 9:
                return findViewById(R.id.buttonNine);
            default:
                return null;
        }
    }
    public void onClick_One(View view) {
        setNum(1);
    }

    public void onClick_Two(View view) {
        setNum(2);
    }


    public void onClick_Three(View view) {
        setNum(3);
    }


    public void onClick_Four(View view) {
        setNum(4);
    }

    public void onClick_Five(View view) {
        setNum(5);
    }

    public void onClick_Six(View view) {
        setNum(6);
    }

    public void onClick_Seven(View view) {
        setNum(7);
    }

    public void onClick_Eight(View view) {
        setNum(8);
    }

    public void onClick_Nine(View view) {
        setNum(9);
    }

    @Override
    public void run() {
        if (allowTick) {
            onTick();
        }
        handlerTimer.postDelayed(this::run, 1000);
    }
}