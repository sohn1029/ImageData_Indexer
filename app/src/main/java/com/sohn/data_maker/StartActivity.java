package com.sohn.data_maker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class StartActivity extends AppCompatActivity implements ShapeClickInterface{
    String cur_dir;
    DrawingView drawingview;
    TextView dir_text;
    DocumentFile[] files;
    private RecyclerView listview;
    private GroupListAdapter adapter;
    int current_img = -1;
    ArrayList<String> itemList;

    public String getPath(final Context context, final Uri uri){
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

        adapter = new GroupListAdapter(this, itemList, onClickItem);
        listview.setAdapter(adapter);

        MyListDecoration decoration = new MyListDecoration();
        listview.addItemDecoration(decoration);


        this.dir_text = findViewById(R.id.directory_id);
        selectDir();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    private View.OnClickListener onClickItem = new View.OnClickListener(){
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
                drawingview.createGroup(itemList.indexOf(str));

            }

        }
    };

    public void onTextViewClicked(View view){

        selectDir();
    }


    void selectDir(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);
        }
    }

    public void SetImg(String path){
        File imgFile = new File(path);
        Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        ImageView imageView = (ImageView) findViewById(R.id.main_img);
        imageView.setImageBitmap(bm);
        drawingview = new DrawingView(this);
        drawingview = findViewById(R.id.drawing_view);
        drawingview.setClickListener(this);
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
                    cur_dir = getPath(this, uri);

                    //set directory text
                    dir_text.setText(cur_dir);

                    //get file list
                    DocumentFile pickedDir = DocumentFile.fromTreeUri(this, uri);
                    files = pickedDir.listFiles();
                    for (DocumentFile file : files) {
                        Log.d("file", "File : " + file.getName());
                    }
                    if(files.length >= 1){
                        current_img = 0;
                        SetImg(cur_dir + "/" + files[current_img].getName());
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
                    if(itemList.size() == 12){
                        Toast.makeText(this, "The number of groups must be less than 12", Toast.LENGTH_SHORT).show();
                    }
                    if(itemList.contains(result)){
                        Toast.makeText(this, "Already Exist!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    //drawingview.createGroup();
                    itemList.add(itemList.size()-1, result);
                    adapter = new GroupListAdapter(this, itemList, onClickItem);
                    listview.setAdapter(adapter);

//                    MyListDecoration decoration = new MyListDecoration();
//                    listview.addItemDecoration(decoration);
                }
                break;
        }
    }


    @Override
    public void onCircleClick(){

    }

}