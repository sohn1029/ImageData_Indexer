package com.sohn.data_maker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Inflater;


public
class DrawingView extends View {

    //    생성된 원들을 담아놓는 리스트이다
//    원의 정보가 들어가있는 CircleInfoData(원정보) 를 담고있다
//    원이 생성될 시 drawnCircleList에 원들이 추가된다
//    원을 클릭 한지 확인하기 위해 onTouchEvent - ACTION_MOVE 에서 포문으로 각각의 원들을 불러온다
//    원을 그리기 위해서 onDraw에서 drawnCircleList에서 원의 대한 정보를 가져와 그리도록한다
    //private ArrayList<Point> drawnCircleList = new ArrayList<>();
    private ArrayList<ArrayList<Point>> groupList = new ArrayList<>();
    private ArrayList<Integer> grouptypeList = new ArrayList<Integer>();

    int[] colorlist = {Color.argb(255,255,0,0),
            Color.argb(255,0,255,0),
            Color.argb(255,0,0,255),
            Color.argb(255,255,120,120),
            Color.argb(255,120,255,120),
            Color.argb(255,120,120,255),
            Color.argb(255,255,120,0),
            Color.argb(255,255,0,120),
            Color.argb(255,120,255,0),
            Color.argb(255,120,0,255),
            Color.argb(255,0,120,255),
            Color.argb(255,0,255,120)};
    int current = -1;
    int current_group = -1;
    float current_x = -1;
    float current_y = -1;


    private void showBottomSheetDialog(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.addpoint_bottom_sheet);

        Button add_btn = bottomSheetDialog.findViewById(R.id.add_point);
        Button delete_btn = bottomSheetDialog.findViewById(R.id.delete_point);

        add_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(current == groupList.get(current_group).size()-1){
                    createCircle(current_group, 0, groupList.get(current_group).get(current).getCircleX(), groupList.get(current_group).get(current).getCircleY(), 20, 20);
                }
                else{
                    createCircle(current_group, current+1, groupList.get(current_group).get(current).getCircleX(), groupList.get(current_group).get(current).getCircleY(), 20, 20);
                }

                bottomSheetDialog.dismiss();
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteCircle(current_group, current);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }
    final Handler handler = new Handler();
    Runnable mLongPressed = new Runnable(){
        public void run(){
            System.out.println("group " + current_group + ", point " + current + " long pressed");
            showBottomSheetDialog();
        }
    };
    //    원에서 공용으로 사용하기 위한 페인트 , 색상이나 두께를 해당 프로젝트에 바꾸지않기에 한번 만들고 계속 사용하도록한다.
    private Paint paint;



    //    유저가 화면을 클릭 했을 때 사용하는 엑티비티 혹은 프래그먼트에 전달하는 리스너
    private ShapeClickInterface clickListener;

    public void setClickListener(ShapeClickInterface listener) {
        this.clickListener = listener;
    }


    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);

    }


    /*
     * ACTION_MOVE :
     *  유저가 원 안을 클릭 했을 경우를 확인한다
     *  원내부를 클릭하고 움직일 경우 해당원의 위치를 유저의 드래그를 따라가도록 이동시켜준다
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("cycling");




        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                double min = 100000000;
                boolean istouched = false;
                for(int j = 0; j < groupList.size(); j++){
                    for(int i = 0; i < groupList.get(j).size(); i++){
                        float circleXFloat = groupList.get(j).get(i).getCircleX();
                        float circleYFloat = groupList.get(j).get(i).getCircleY();
                        int circleRadius = groupList.get(j).get(i).getRadius();
                        double dx = Math.pow(x - circleXFloat, 2);
                        double dy = Math.pow(y - circleYFloat, 2);
                        if (dx + dy < Math.pow(circleRadius, 2)) {
                            if(dx + dy < min){
                                min = dx + dy;
                                current = i;
                                current_group = j;
                                current_x = x;
                                current_y = y;
                                handler.postDelayed(mLongPressed, 700);
                                istouched = true;
                            }
                        }
                    }
                }
                if(!istouched){
                    current = -1;
                    current_group = -1;
                }

                break;

            case MotionEvent.ACTION_MOVE:

                if(current != -1){
                    x = event.getX();
                    y = event.getY();
                    if(Math.pow(current_x-x,2) + Math.pow(current_y-y,2) > Math.pow(groupList.get(current_group).get(current).getRadius()/2,2)){
                        handler.removeCallbacks(mLongPressed);
                    }

                    moveCircleShape(x, y, current_group, current);



                }

                break;

            case MotionEvent.ACTION_UP:
                handler.removeCallbacks(mLongPressed);


                break;
        }


//       터치이벤트가 겹쳐져있는 뷰가 없으므로 상위뷰들의 onTouchEvent를 호출하지않기 위해 true로함
        return true;

    }


//    private GestureDetector gestureDetector

    //    그리는 곳
    @Override
    protected void onDraw(Canvas canvas) {
//        원을 담고있는 리스트에서 하나씩 돌려서 원을 생성해주도록함
        int paint_color = 0;
        for(ArrayList<Point> group : groupList){
            paint.setColor(colorlist[grouptypeList.get(paint_color++)]);
            for (int i = 0; i < group.size(); i++) {
                canvas.drawCircle(group.get(i).getCircleX(),
                        group.get(i).getCircleY(),
                        group.get(i).getRadius(),
                        paint);
                if(i+1 != group.size()){
                    canvas.drawLine(group.get(i).getCircleX(),
                            group.get(i).getCircleY(),
                            group.get(i+1).getCircleX(),
                            group.get(i+1).getCircleY(),
                            paint);
                }
                else{
                    canvas.drawLine(group.get(i).getCircleX(),
                            group.get(i).getCircleY(),
                            group.get(0).getCircleX(),
                            group.get(0).getCircleY(),
                            paint);
                }

            }

        }


        super.onDraw(canvas);
    }

    /**
     * 원을 드래그해서 움직이면 동작하는 함수이다
     * circleInfoData (원정보)를 변경시키고 다시 화면을 그리도록함
     *
     * @param x         = 최종 x좌표 - 원의 x값이 된다.
     * @param y         = 최종 y좌표 - 원의 y값이 된다.
     * @param listIndex = drawnCircleList의 들어있는 인덱스값
     */
    private void moveCircleShape(float x, float y, int groupIndex, int listIndex) {

        Point circleInfoData = groupList.get(groupIndex).get(listIndex);

        circleInfoData.setCircleX(Math.round(x));
        circleInfoData.setCircleY(Math.round(y));

        this.invalidate();
    }


    /**
     * 원을 추가해주는 코드
     * 처음 생성된 원은 화면의 정가운대에 추가되며 반지름 값은 5이다.
     * 원의 정보를 drawnCircleList에 담아주고 다시 화면을 로드해 원이 추가된걸 보여주도록한다
     */
    public void createCircle(int groupIndex, int place, int x, int y, int offset_w, int offset_h) {

        Point circleInfoData = new Point(
                x + offset_w,
                y + offset_h,
                40);
        groupList.get(groupIndex).add(place, circleInfoData);
        this.invalidate();

    }

    public void deleteCircle(int groupIndex, int place){
        groupList.get(groupIndex).remove(place);
        this.invalidate();
    }

    public void createGroup(int type){
        ArrayList<Point> group = new ArrayList<>();
        groupList.add(group);
        grouptypeList.add(type);
        createCircle(groupList.size()-1, 0, getWidth()/2, getHeight()/2, 0, 60);
        createCircle(groupList.size()-1, 0, getWidth()/2, getHeight()/2,51, -30);
        createCircle(groupList.size()-1, 0, getWidth()/2, getHeight()/2,-51, -30);
    }

}