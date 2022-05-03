package com.sohn.data_maker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.BitmapFactory;


public class ResultImageManager {
    private Map<String, Integer> groupIndex = new HashMap<String,Integer>();


    public void makeImage(String dir, String cur_file, int origin_x, int origin_y, float scale_x, float scale_y, int[] imgboarder) throws IOException {
        File img = new File(dir, cur_file);
        if(!img.exists()){
            System.out.println(img);
        }

        FileOutputStream fiStream = new FileOutputStream(img);
        Bitmap result = Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
        int[] pixels = new int[result.getHeight()*result.getWidth()];
        result.getPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
//        for (int i=0; i<result.getWidth()*5; i++)
//            pixels[i] = Color.BLUE;
//        myBitmap.setPixels(pixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
        result.compress(Bitmap.CompressFormat.JPEG, 100, fiStream);
        fiStream.flush();
        fiStream.close();
    }

    public boolean isIn(int x, int y, ArrayList<Point> pointlist){
        int cnt = 0;
        for(int i = 0; i < pointlist.size(); i++){
            float cur_x1 = pointlist.get(i).getCircleX();
            float cur_y1 = pointlist.get(i).getCircleY();
            float cur_x2 = 0;
            float cur_y2 = 0;
            if(i == pointlist.size()-1){
                cur_x2 = pointlist.get(0).getCircleX();
                cur_y2 = pointlist.get(0).getCircleY();
            }
            else{
                cur_x2 = pointlist.get(i+1).getCircleX();
                cur_y2 = pointlist.get(i+1).getCircleY();
            }
            float inter_x = 0;
            float inter_y = 0;
            if(cur_x1-cur_x2 == 0){//x = n
                inter_x = cur_x1;
                inter_y = y;
                float big_y = Math.max(cur_y2, cur_y1);
                float small_y = Math.min(cur_y2, cur_y1);
                if(inter_x > x && inter_y > small_y && inter_y < big_y){
                    cnt+=1;
                }
            }
            else{
                if(cur_y1 - cur_y2 != 0){
                    inter_x = (y-cur_y1)*(cur_x2-cur_x1)/(cur_y2-cur_y1) + cur_x1;
                    inter_y = y;
                    float big_y = Math.max(cur_y2, cur_y1);
                    float small_y = Math.min(cur_y2, cur_y1);
                    if(inter_x > x && inter_y > small_y && inter_y < big_y){
                        cnt+=1;
                    }
                }
            }
        }
        if(cnt % 2 == 0){
            return false;
        }
        else{
            return true;
        }

    }
    public void updateIndex(ArrayList<String> itemList){
        for(int i = 0; i < itemList.size(); i++){
            if(!groupIndex.containsKey(itemList.get(i))){

            }
        }
    }
}
