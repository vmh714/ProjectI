package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sudoku.generate_sudoku.Sudoku;

public class ActivityNewGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
    }

    public void onClick_Easy(View view) {
        Intent intent = new Intent(this, ActivityGame.class);
        Sudoku.generateSudoku(Sudoku.Difficult.Easy);
        this.startActivity(intent);
    }

    public void onClick_Normal(View view) {
        Intent intent = new Intent(this, ActivityGame.class);
        Sudoku.generateSudoku(Sudoku.Difficult.Normal);
        this.startActivity(intent);
    }

    public void onClick_Hard(View view) {
        Intent intent = new Intent(this, ActivityGame.class);
        Sudoku.generateSudoku(Sudoku.Difficult.Hard);
        this.startActivity(intent);
    }
}