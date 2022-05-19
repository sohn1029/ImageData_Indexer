package com.sohn.data_maker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;

import android.util.Log;

import android.view.View;

import android.view.WindowManager;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;

public class StartActivity extends AppCompatActivity implements ShapeClickInterface, GroupListAdapter.OnStartDragListener{
    String cur_dir;
    DrawingView drawingview;
    TextView dir_text;
    ArrayList<DocumentFile> files;
    private RecyclerView listview;
    private GroupListAdapter adapter;
    int current_img = -1;
    ArrayList<String> itemList;
    private ItemTouchHelper mItemTouchHelper;
    Button nextButton;
    String[] supportExt = {"jpg", "jpeg", "png"};
    //Get file path
    //input : uri
    //output : path string
    public String getPath(final Uri uri){
        File file1 = new File(uri.getPath());
        final String[] split = file1.getPath().split(":");//split the path.
        final String type = split[0];
        System.out.println(type);
        if ("/tree/primary".equalsIgnoreCase(type)) {
            return Environment.getExternalStorageDirectory() + "/" + split[1];
        }else {
            return "/storage/" + type + "/" + split[1];
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        drawingview = findViewById(R.id.drawing_view);
        drawingview.setClickListener(this);

        listview = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listview.setLayoutManager(layoutManager);

        itemList = new ArrayList<>();
        itemList.add("+");

        adapter = new GroupListAdapter(this, itemList, onClickItem, this);
        listview.setAdapter(adapter);

        MyListDecoration decoration = new MyListDecoration();
        listview.addItemDecoration(decoration);

        GroupTouchHelperCallback mCallback = new GroupTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(mCallback);
        mItemTouchHelper.attachToRecyclerView(listview);

        this.dir_text = findViewById(R.id.directory_id);
        selectDir();

        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(next_listener);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    public void updateGroupTxt() throws IOException {
        File file = new File(cur_dir + "/" + "group.txt");
        FileWriter fw = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(fw);
        Map<String, Integer> groupColor = drawingview.getGroupColor();
        for(int i = 0 ; i < itemList.size()-1; i++){
            System.out.println(itemList.get(i));
            writer.write(itemList.get(i) + " " + groupColor.get(itemList.get(i)) + "\n");
        }
        writer.close();
    }

    public void setGroup() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(cur_dir + "/" + "group.txt"));
        String str;
        Map<String, Integer> newgroupColor = new HashMap<String, Integer>();
        while ((str = reader.readLine()) != null) {
            String[] tmp = str.split(" ");
            itemList.add(itemList.size()-1, tmp[0]);
            drawingview.sortGroup(itemList);
            adapter.updateList(itemList);

            if(!tmp[1].equals("null")){
                newgroupColor.put(tmp[0], Integer.parseInt(tmp[1]));
            }
        }
        drawingview.setGroupColor(newgroupColor);
        reader.close();
    }

    //if : add item
    //else : add group
    final private View.OnClickListener onClickItem = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            String str = (String) v.getTag();
            System.out.println(str);
            if(str.equals("+")){
                //popup
                Intent intent = new Intent(StartActivity.this, MakeGroupActivity.class);
                startActivityForResult(intent, 1);

            }
            else{

                drawingview.createGroup(str);
                drawingview.sortGroup(itemList);
                try {
                    updateGroupTxt();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    //change item position
    @Override
    public void onItemMove(int fromPosition, int toPosition){
        System.out.println("from" + fromPosition);
        System.out.println("to" + toPosition);
        if(toPosition == itemList.size()-1 || fromPosition == itemList.size()-1){
            return;
        }
        Collections.swap(itemList, fromPosition, toPosition);
        adapter.notifyItemMoved(fromPosition, toPosition);
        drawingview.sortGroup(itemList);
        try {
            updateGroupTxt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //delete item position
    @Override
    public void onItemSwiped(int Position){
        System.out.println("target : " + Position);
        if(Position == itemList.size()-1){
            adapter.notifyDataSetChanged();
            return;
        }
        drawingview.deleteGroup(itemList.get(Position));
        itemList.remove(Position);
        adapter.notifyDataSetChanged();
        drawingview.sortGroup(itemList);
        try {
            updateGroupTxt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //textview click action
    public void onTextViewClicked(View view){
        selectDir();
    }

    //change task directory
    void selectDir(){

        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);

    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
    //set path img to main_img imageview
    public void SetImg(String path){
        File imgFile = new File(path);
        Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        bm = rotateBitmap(bm, orientation);
        ImageView imageView = (ImageView) findViewById(R.id.main_img);
        imageView.setImageBitmap(bm);
        drawingview = new DrawingView(this);
        drawingview = findViewById(R.id.drawing_view);
        drawingview.setClickListener(this);
    }
    public boolean isSupportExt(String ext){
        for(String support : supportExt){
            if(support.equals(ext)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 9999:
                if (resultCode == RESULT_OK) {
                    Log.i("Test", "Result URI " + data.getData().getPath());

                    //get directory path
                    Uri uri = data.getData();
                    cur_dir = getPath(uri);

                    //set directory text to textview
                    dir_text.setText(cur_dir);

                    //get file list
                    DocumentFile pickedDir = DocumentFile.fromTreeUri(this, uri);
                    DocumentFile[] tmp = pickedDir.listFiles();

                    files = new ArrayList<>(Arrays.asList(tmp));
                    System.out.println(files.size());
                    for (int i = 0; i < files.size(); i++) {

                        String fileext = files.get(i).getName().split("\\.")[1];
                        if(!isSupportExt(fileext)){
                            files.remove(i);
                            i--;
                        }

                    }
                    File file = new File(cur_dir + "/" + "group.txt");
                    if(!file.exists()){
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        try {
                            setGroup();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                    if(files.size() >= 1){
                        current_img = 0;
                        SetImg(cur_dir + "/" + files.get(current_img).getName());
                        ImageView imageView = (ImageView) findViewById(R.id.main_img);
                        drawingview.updateImageInfo(imageView);
                    }
                    else{
                        ImageView imageView = (ImageView) findViewById(R.id.main_img);
                        imageView.setImageResource(R.drawable.no_img);
                        Toast.makeText(this, "No image", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case 1:
                if(resultCode == RESULT_OK){
                    System.out.println("get text");
                    String result = (String) data.getStringExtra("result");
                    if(itemList.size() == drawingview.colorlist.length+1){
                        Toast.makeText(this, "The number of groups must be less than " + (drawingview.colorlist.length+1), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(itemList.contains(result)){
                        Toast.makeText(this, "Already Exist!", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    itemList.add(itemList.size()-1, result);
                    drawingview.sortGroup(itemList);
                    adapter.updateList(itemList);

                    try {
                        updateGroupTxt();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    @Override
    public void onCircleClick(){

    }
    @Override
    public void onStartDrag(GroupListAdapter.ViewHolder holder){
        mItemTouchHelper.startDrag(holder);
        System.out.println("drag start");
    }



    Button.OnClickListener next_listener = new Button.OnClickListener(){
        public void onClick(View v){

            try {
                drawingview.makeImage(cur_dir, "indexed_" + files.get(current_img).getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            drawingview.resetView();
            current_img += 1;
            if(current_img > files.size()-1){
                finish();
                return;
            }
            SetImg(cur_dir + "/" + files.get(current_img).getName());
            ImageView imageView = (ImageView) findViewById(R.id.main_img);
            drawingview.updateImageInfo(imageView);
        }
    };





}