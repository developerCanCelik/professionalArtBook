package com.cancelik.professionalartbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.BitSet;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    static ArrayList<Bitmap> artImageList;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_art, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_art){
            Intent intent = new Intent(MainActivity.this,MainActivity2.class);
            intent.putExtra("info","new");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

        ArrayList<String> artNameList = new ArrayList<>();
        artImageList = new ArrayList<>();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1,artNameList);
        listView.setAdapter(arrayAdapter);


        String uri = "content://"+ArtContentProvider.PROVIDER_NAME;
        Uri artUri = Uri.parse(uri);
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(artUri,null,null,null,"name");
        if (cursor != null){
            while (cursor.moveToNext()){
                //imleç devam ettiği sürece demek
                artNameList.add(cursor.getString(cursor.getColumnIndex(ArtContentProvider.NAME)));
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex(ArtContentProvider.IMAGE));
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                artImageList.add(image);

                arrayAdapter.notifyDataSetChanged();
            }
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                intent.putExtra("info","old");
                intent.putExtra("names",artNameList.get(i));
                intent.putExtra("position",i);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }
}