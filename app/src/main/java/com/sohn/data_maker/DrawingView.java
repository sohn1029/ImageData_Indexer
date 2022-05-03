package com.sohn.data_maker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class DrawingView extends View {

    //    생성된 원들을 담아놓는 리스트이다
//    원의 정보가 들어가있는 CircleInfoData(원정보) 를 담고있다
//    원이 생성될 시 drawnCircleList에 원들이 추가된다
//    원을 클릭 한지 확인하기 위해 onTouchEvent - ACTION_MOVE 에서 포문으로 각각의 원들을 불러온다
//    원을 그리기 위해서 onDraw에서 drawnCircleList에서 원의 대한 정보를 가져와 그리도록한다
    private ArrayList<ArrayList<Point>> groupList = new ArrayList<>();
    private ArrayList<String> grouptypeList = new ArrayList<String>();
    private Map<String, Integer> groupColor = new HashMap<String,Integer>();

    int[] colorlist = {Color.argb(255,235,50,50),
            Color.argb(255,55,126,184),
            Color.argb(255,77,175,74),
            Color.argb(255,152,78,163),
            Color.argb(255,235,117,0),
            Color.argb(255,195,195,51),
            Color.argb(255,166,86,40),
            Color.argb(255,249,140,195),
            Color.argb(255,162,162,162)};

    int current = -1;
    int current_group = -1;
    float current_x = -1;
    float current_y = -1;
    int origin_x = 0;
    int origin_y = 0;
    float scale_x = 0;
    float scale_y = 0;
    int[] img_boarder = new int[4];
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
                    if(Math.pow(current_x-x,2) + Math.pow(current_y-y,2) > Math.pow(groupList.get(current_group).get(current).getRadius()/4,2)){
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
            paint.setColor(colorlist[groupColor.get(grouptypeList.get(paint_color++))]);
            for (int i = 0; i < group.size(); i++) {
                canvas.drawCircle(group.get(i).getCircleX(),
                        group.get(i).getCircleY(),
                        group.get(i).getRadius()/6,
                        paint);
                paint.setStrokeWidth(5);
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
                paint.setStrokeWidth(5);
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
        if(x < img_boarder[0]){
            x = img_boarder[0];
        }
        if(y < img_boarder[1]){
            y = img_boarder[1];
        }
        if(x > img_boarder[0]+img_boarder[2]){
            x = img_boarder[0]+img_boarder[2];
        }
        if(y > img_boarder[1]+img_boarder[3]){
            y = img_boarder[1]+img_boarder[3];
        }
        System.out.println("point : " + x);
        System.out.println("point : " + y);
        circleInfoData.setCircleY(Math.round(y));
        circleInfoData.setCircleX(Math.round(x));

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
                60);
        groupList.get(groupIndex).add(place, circleInfoData);
        this.invalidate();

    }

    public void deleteCircle(int groupIndex, int place){
        if(groupList.get(groupIndex).size() == 3){
            groupList.remove(groupIndex);
            grouptypeList.remove(groupIndex);
            this.invalidate();
            return;
        }
        groupList.get(groupIndex).remove(place);
        this.invalidate();
    }

    public void createGroup(String type){
        ArrayList<Point> group = new ArrayList<>();
        groupList.add(group);
        grouptypeList.add(type);
        if(!groupColor.containsKey(type)){
            Collection<Integer> arr = groupColor.values();
            int[] tmp = new int[colorlist.length];
            for(int i = 0; i < arr.size(); i++){
                tmp[(int) arr.toArray()[i]]++;
            }
            int min = 0;
            for(min = 0; min < colorlist.length; min++){
                if(tmp[min] == 0){
                    break;
                }
            }
            groupColor.put(type, min);
        }
        createCircle(groupList.size()-1, 0, getWidth()/2, getHeight()/2, 0, 60);
        createCircle(groupList.size()-1, 0, getWidth()/2, getHeight()/2,51, -30);
        createCircle(groupList.size()-1, 0, getWidth()/2, getHeight()/2,-51, -30);
    }

    public void deleteGroup(String type){
        for(int i = 0; i < groupList.size(); i++){
            if(grouptypeList.get(i).equals(type)){
                groupList.remove(i);
                grouptypeList.remove(i);
            }
        }
        groupColor.remove(type);
    }

    public void sortGroup(ArrayList<String> order){
        ArrayList<ArrayList<Point>> newgroupList = new ArrayList<>();
        ArrayList<String> newgroupTypeList = new ArrayList<>();

        for(int i = 0; i < order.size(); i++){
            for(int j = 0; j < grouptypeList.size(); j++){

                if(order.get(i) == grouptypeList.get(j)){

                    newgroupList.add(0,groupList.get(j));
                    newgroupTypeList.add(0,grouptypeList.get(j));

                }
            }
        }
        groupList = newgroupList;
        grouptypeList = newgroupTypeList;
        this.invalidate();

    }

    public Map<String, Integer> getGroupColor(){
        return groupColor;
    }

    public void setGroupColor(Map<String, Integer> newgroupColor){
        groupColor = newgroupColor;
    }

    public void printArray(ArrayList<String> target){
        for(int i = 0; i < target.size(); i++){
            System.out.print(target.get(i) + " ");
        }
    }





    public void resetView(){
        groupList = new ArrayList<>();
        grouptypeList = new ArrayList<String>();
        groupColor = new HashMap<String,Integer>();
        current = -1;
        current_group = -1;
        current_x = -1;
        current_y = -1;

        this.invalidate();
    }

    public void updateImageInfo(ImageView imageView){

        if (imageView == null || imageView.getDrawable() == null)
            return;

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];
        scale_x = scaleX;
        scale_y = scaleY;
        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();
        origin_x = origW;
        origin_y = origH;
        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        img_boarder[2] = actW;
        img_boarder[3] = actH;

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - actH)/2;
        int left = (int) (imgViewW - actW)/2;

        img_boarder[0] = left;
        img_boarder[1] = top;

        return;

    }
    public void makeImage(String dir, String cur_file) throws IOException {
        ResultImageManager nr = new ResultImageManager(groupList, grouptypeList, groupColor);
        nr.makeImage(dir, cur_file, origin_x, origin_y, scale_x, scale_y, img_boarder);
    }
}