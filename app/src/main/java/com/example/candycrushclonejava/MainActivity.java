package com.example.candycrushclonejava;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int[] candies = {
            R.drawable.bluecandy,
            R.drawable.greencandy,
            R.drawable.redcandy,
            R.drawable.purplecandy,
            R.drawable.orangecandy,
            R.drawable.yellowcandy,
    };
    GridLayout gridLayout;
    int widthOfBlock, noOfBlocks = 8, widthOfScreen, heightOfScreen,
            candyToBeDragged, candyToBeReplaced,
            notCandy = R.drawable.transparent, interval = 100;
    ArrayList<ImageView> candy = new ArrayList<>();
    Handler handler;
    TextView points, gameOverTextView, target;
    ImageView restartGame;
    int scoreResult = 0, noOfMoves = 0;
    Runnable repeatChecker = new Runnable() {
        @Override
        public void run() {
            try {
                allowed();//checks all the combinations of candies in a row or column to find match
                moveDownCandy();
            } finally {
                handler.postDelayed(repeatChecker, interval);
            }
        }
    };

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        points = findViewById(R.id.points);
        gameOverTextView = findViewById(R.id.gameOver);
        gameOverTextView.setVisibility(View.INVISIBLE);
        target = findViewById(R.id.target);
        restartGame = findViewById(R.id.restart);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthOfScreen = displayMetrics.widthPixels;
        heightOfScreen = displayMetrics.heightPixels;
        widthOfBlock = widthOfScreen / noOfBlocks;
        createBoard();
        scoreResult = 0;
        for (ImageView imageView : candy) {
            imageView.setOnTouchListener(new OnSwipeListener(this) {
                @Override
                void onSwipeBottom() {
                    super.onSwipeBottom();
                    //identifies the candies to be interchanged
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged + noOfBlocks;//adds 8 to interchange vertically
                    ++noOfMoves;
                    /*counts number of moves to help calculate the score.
                        counting of the score starts only after the first move is played
                    */
                    candyInterChange();
                    /*this is to check if the move is valid or not
                        the movie is valid only if the move leads to the candies being popped
                     */
                    if (!allowed()) {
                        //if the move is not valid then the candies are interchanged again
                        candyInterChange();
                        Toast.makeText(MainActivity.this, "sorry, Move not allowed!", Toast.LENGTH_SHORT).show();
                    } else {
                        ++noOfMoves;
                    }
                }

                @Override
                void onSwipeLeft() {
                    super.onSwipeLeft();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged - 1;//for horizontal interchange
                    ++noOfMoves;
                    candyInterChange();
                    if (!allowed()) {
                        candyInterChange();
                        Toast.makeText(MainActivity.this, "sorry, Move not allowed!", Toast.LENGTH_SHORT).show();
                    } else {
                        ++noOfMoves;
                    }
                }

                @Override
                void onSwipeRight() {
                    super.onSwipeRight();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged + 1;
                    candyInterChange();
                    ++noOfMoves;
                    if (!allowed()) {
                        candyInterChange();
                        Toast.makeText(MainActivity.this, "sorry, Move not allowed!", Toast.LENGTH_SHORT).show();
                    } else{
                        ++noOfMoves;
                    }

                }

                @Override
                void onSwipeTop() {
                    super.onSwipeTop();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged - noOfBlocks;
                    candyInterChange();
                    ++noOfMoves;
                    if (!allowed()) {
                        candyInterChange();
                        Toast.makeText(MainActivity.this, "sorry, Move not allowed!", Toast.LENGTH_SHORT).show();
                    } else{
                        ++noOfMoves;
                    }
                }
            });
        }
        handler = new Handler();
        startRepeat();
        restartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scoreResult = 0;
                points.setText("");
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    private void moveDownCandy() {
        //this method replaces the candies that have been popped
        int i;
        Integer[] firstRow = {0, 1, 2, 3, 4, 5, 6, 7};
        List<Integer> list = Arrays.asList(firstRow);
        for (i = 55; i >= 0; i--) {
            if ((int) candy.get(i + noOfBlocks).getTag() == notCandy) {
                /*it checks vertically after the first row if any of the candies have been popped
                        it exchanges the resource of the box with the box above
                        and it does it till all of the boxes has been replaced with a candy*/
                candy.get(i + noOfBlocks).setImageResource((int) candy.get(i).getTag());
                candy.get(i + noOfBlocks).setTag(candy.get(i).getTag());
                candy.get(i).setImageResource(notCandy);
                candy.get(i).setTag(notCandy);
                //this generates random candies to insert into the grid from above
                if (list.contains(i) && (int) candy.get(i).getTag() == notCandy) {
                    int randomColor = (int) Math.floor(Math.random() * candies.length);
                    candy.get(i).setImageResource(candies[randomColor]);
                    candy.get(i).setTag(candies[randomColor]);
                }
            }
        }
        //this generates random candies to insert into the first row only
        for (i = 0; i < 8; i++) {
            if ((int) candy.get(i).getTag() == notCandy) {
                int randomColor = (int) Math.floor(Math.random() * candies.length);
                candy.get(i).setImageResource(candies[randomColor]);
                candy.get(i).setTag(candies[randomColor]);
            }
        }
    }

    private boolean checkRowForThree() {
        boolean a = false;
        for (int i = 0; i < noOfBlocks * noOfBlocks - 2; i++) {
            int chosenCandy = (int) candy.get(i).getTag();
            boolean isBlank = (int) candy.get(i).getTag() == notCandy;
            /*this checks the validity for 3 in a row.
               the not valid array contains the combinations which will not work
               for example the index 8 is not in the first row instead it is the first box of the
               second row. So there can never be 3 candies in a row starting from index 6.
               Similarly with all the other ends of the rows.*/
            Integer[] notValid = {6, 7, 14, 15, 22, 23, 30, 31, 38, 39, 46, 47, 54, 55};
            List<Integer> list = Arrays.asList(notValid);
            if (!list.contains(i)) {
                int x = i;
                //this checks if there are 3 in a row.
                if ((int) candy.get(x++).getTag() == chosenCandy && !isBlank &&
                        (int) candy.get(x++).getTag() == chosenCandy &&
                        (int) candy.get(x).getTag() == chosenCandy) {
                    a = true;//this keeps check if the candies match
                    if(noOfMoves > 0) {
                        scoreResult += 3;
                    } else {
                        scoreResult = 0;
                    }
                    points.setText(String.valueOf(scoreResult));
                    scoreCheck(scoreResult);
                    //if there are 3 in a row then all the 3 candies are popped
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                }
            }
        }
        moveDownCandy();
        return a; // if the candies match then true is returned
    }

    private boolean checkRowForFour() {
        boolean a = false;
        for (int i = 0; i < noOfBlocks * noOfBlocks - 3; i++) {
            int chosenCandy = (int) candy.get(i).getTag();
            boolean isBlank = (int) candy.get(i).getTag() == notCandy;
            Integer[] notValid = {5, 6, 7, 13, 14, 15, 21, 22, 23, 29, 30, 31, 37, 38, 39, 45, 46, 47, 53, 54, 55};
            List<Integer> list = Arrays.asList(notValid);
            if (!list.contains(i)) {
                int x = i;
                if ((int) candy.get(x++).getTag() == chosenCandy && !isBlank &&
                        (int) candy.get(x++).getTag() == chosenCandy &&
                        (int) candy.get(x++).getTag() == chosenCandy &&
                        (int) candy.get(x).getTag() == chosenCandy) {
                    a = true;//this keeps check if the candies match
                    if(noOfMoves > 0) {
                        scoreResult += 4;
                    } else {
                        scoreResult = 0;
                    }
                    points.setText(String.valueOf(scoreResult));
                    scoreCheck(scoreResult);
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                }
            }
        }
        moveDownCandy();
        return a;//if the candies match and are popped then it returns true
    }

    private boolean checkRowForFive() {
        boolean a = false;
        for (int i = 0; i < noOfBlocks * noOfBlocks - 3; i++) {
            int chosenCandy = (int) candy.get(i).getTag();
            boolean isBlank = (int) candy.get(i).getTag() == notCandy;
            Integer[] notValid = {4, 5, 6, 7, 12, 13, 14, 15, 20, 21, 22, 23, 28, 29, 30, 31, 36, 37, 38, 39, 44, 45, 46, 47, 52, 53, 54, 55};
            List<Integer> list = Arrays.asList(notValid);
            if (!list.contains(i)) {
                int x = i;
                if ((int) candy.get(x++).getTag() == chosenCandy && !isBlank &&
                        (int) candy.get(x++).getTag() == chosenCandy &&
                        (int) candy.get(x++).getTag() == chosenCandy &&
                        (int) candy.get(x++).getTag() == chosenCandy &&
                        (int) candy.get(x).getTag() == chosenCandy) {
                    a = true;
                    if(noOfMoves > 0) {
                        scoreResult += 5;
                    } else {
                        scoreResult = 0;
                    }
                    points.setText(String.valueOf(scoreResult));
                    scoreCheck(scoreResult);
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                }
            }
        }
        moveDownCandy();
        return a;
    }

    private boolean checkColForThree() {
        boolean a = false;
        for (int i = 0; i < 48; i++) {
            int chosenCandy = (int) candy.get(i).getTag();
            boolean isBlank = (int) candy.get(i).getTag() == notCandy;
            int x = i;
            /*this checks vertically to see if all the candies are valid
                like if we take index 0 then we will take index ((0 + 8) = 8) which is just below it
                then we take index ((0 + 2*8) = 16) which is vertically below box with index 8
             */
            if ((int) candy.get(x).getTag() == chosenCandy && !isBlank &&
                    (int) candy.get(x + noOfBlocks).getTag() == chosenCandy &&
                    (int) candy.get(x + 2 * noOfBlocks).getTag() == chosenCandy) {
                a = true;
                if(noOfMoves > 0) {
                    scoreResult += 3;
                } else {
                    scoreResult = 0;
                }
                points.setText(String.valueOf(scoreResult));
                scoreCheck(scoreResult);
                //we pop the three candies
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
                x = x + noOfBlocks;
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
                x = x + noOfBlocks;
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
            }
        }
        moveDownCandy();
        return a;
    }

    private boolean checkColForFour() {
        boolean a = false;
        for (int i = 0; i < 40; i++) {
            int chosenCandy = (int) candy.get(i).getTag();
            boolean isBlank = (int) candy.get(i).getTag() == notCandy;
            int x = i;
            if ((int) candy.get(x).getTag() == chosenCandy && !isBlank &&
                    (int) candy.get(x + noOfBlocks).getTag() == chosenCandy &&
                    (int) candy.get(x + 2 * noOfBlocks).getTag() == chosenCandy &&
                    (int) candy.get(x + 3 * noOfBlocks).getTag() == chosenCandy) {
                a = true;
                if(noOfMoves > 0) {
                    scoreResult += 4;
                } else {
                    scoreResult = 0;
                }
                points.setText(String.valueOf(scoreResult));
                scoreCheck(scoreResult);
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
                x = x + noOfBlocks;
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
                x = x + noOfBlocks;
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
                x = x + noOfBlocks;
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
            }
        }
        moveDownCandy();
        return a;
    }

    private boolean checkColForFive() {
        boolean a = false;
        for (int i = 0; i < 32; i++) {
            int chosenCandy = (int) candy.get(i).getTag();
            boolean isBlank = (int) candy.get(i).getTag() == notCandy;
            int x = i;
            if ((int) candy.get(x).getTag() == chosenCandy && !isBlank &&
                    (int) candy.get(x + noOfBlocks).getTag() == chosenCandy &&
                    (int) candy.get(x + 2 * noOfBlocks).getTag() == chosenCandy &&
                    (int) candy.get(x + 3 * noOfBlocks).getTag() == chosenCandy &&
                    (int) candy.get(x + 4 * noOfBlocks).getTag() == chosenCandy) {
                a = true;
                if(noOfMoves > 0) {
                    scoreResult += 5;
                } else {
                    scoreResult = 0;
                }
                points.setText(String.valueOf(scoreResult));
                scoreCheck(scoreResult);
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
                x = x + noOfBlocks;
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
                x = x + noOfBlocks;
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
                x = x + noOfBlocks;
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
                x = x + noOfBlocks;
                candy.get(x).setImageResource(notCandy);
                candy.get(x).setTag(notCandy);
            }
        }
        moveDownCandy();
        return a;
    }

    void startRepeat() {
        repeatChecker.run();
    }

    private void candyInterChange() {
        //the 2 candies that are selected, their image resourse and tags are exchanged.
        int background = (int) candy.get(candyToBeReplaced).getTag();
        int background1 = (int) candy.get(candyToBeDragged).getTag();
        candy.get(candyToBeDragged).setImageResource(background);
        candy.get(candyToBeReplaced).setImageResource(background1);
        candy.get(candyToBeDragged).setTag(background);
        candy.get(candyToBeReplaced).setTag(background1);
    }

    public void createBoard() {
        //the size and width is set according to the size of the screen
        gridLayout = findViewById(R.id.board);
        gridLayout.setRowCount(noOfBlocks);
        gridLayout.setColumnCount(noOfBlocks);
        gridLayout.getLayoutParams().width = widthOfScreen;
        gridLayout.getLayoutParams().height = widthOfScreen;

        for (int i = 0; i < noOfBlocks * noOfBlocks; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);//id is set according to it's position.
            imageView.setLayoutParams(new ViewGroup.LayoutParams(widthOfBlock, widthOfBlock));
            imageView.setMaxHeight(widthOfBlock);
            imageView.setMaxWidth(widthOfBlock);
            int randomCandy = (int) Math.floor(Math.random() * candies.length);
            imageView.setImageResource(candies[randomCandy]);
            imageView.setTag(candies[randomCandy]);//the candies of same color have the same tag
            candy.add(imageView);//we store all the images in order in the array candy
            gridLayout.addView(imageView);
        }

        scoreResult = 0;
    }

    private void scoreCheck(int score) {
        //this keeps check of the score and to reset the game
        if (score >= 500) {
            gridLayout.setVisibility(View.GONE);
            target.setVisibility(View.GONE);
            gameOverTextView.setVisibility(View.VISIBLE);
        }
    }

    private boolean allowed() {
        //this checks if the move is allowed
        boolean b1, b2, b3, b4, b5, b6;
        b1 = checkRowForFive();
        b2 = checkColForFive();
        b3 = checkRowForFour();
        b4 = checkColForFour();
        b5 = checkRowForThree();
        b6 = checkColForThree();
        //if even anyone of the combinations are successful the true is sent else false is sent
        if (b1 || b2 || b3 || b4 || b5 || b6) {
            return true;
        }
        return false;
    }

}