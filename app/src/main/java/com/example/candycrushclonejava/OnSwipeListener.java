package com.example.candycrushclonejava;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeListener implements View.OnTouchListener{
    public GestureDetector gestureDetector;
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }
    public OnSwipeListener(Context context){
        gestureDetector = new GestureDetector(context, new GestureListener());
    }
    private final class GestureListener extends GestureDetector.SimpleOnGestureListener{
        public static final int SWIPE_THRESHOLD = 100;
        public static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            float yDiff = e2.getY() - e1.getY();
            float xDiff = e2.getX() - e1.getX();
            if(Math.abs(xDiff)> Math.abs(yDiff)){
                if(Math.abs(xDiff) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                    if(xDiff > 0){
                        onSwipeRight();
                    } else{
                        onSwipeLeft();
                    }
                    result = true;
                }
            } else {
                if(Math.abs(yDiff) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD){
                    if(yDiff > 0){
                        onSwipeBottom();
                    } else{
                        onSwipeTop();
                    }
                    result = true;
                }
            }
            return result;
        }
    }

    void onSwipeRight(){}
    void onSwipeLeft(){}
    void onSwipeTop(){}
    void onSwipeBottom(){}
}
