package com.cancelik.professionalartbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

public class MainActivity2 extends AppCompatActivity {
    ImageView imageView;
    EditText editText;
    Button deleteButton, saveButton, updateButton;
    Bitmap selectedImage;

    String firstName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.editText);
        deleteButton = findViewById(R.id.delete);
        saveButton = findViewById(R.id.save);
        updateButton = findViewById(R.id.update);

        deleteButton.setVisibility(View.GONE);
        updateButton.setVisibility(View.GONE);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        if (info.matches("new")){
            Bitmap background = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_launcher_background);
            imageView.setImageBitmap(background);
            editText.setText("");
            saveButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.GONE);
        }else {
            String name = intent.getStringExtra("names");
            editText.setText(name);
            firstName = name;
            int position = intent.getIntExtra("position",0);
            imageView.setImageBitmap(MainActivity.artImageList.get(position));

            saveButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.VISIBLE);
        }

    }
    public void save(View view){
        /****************** Veri ekleme ************************/
        String artName = editText.getText().toString();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] bytes = outputStream.toByteArray();

        //content resolver işleyebileceği değerleri koyabiliyoruz.
        ContentValues contentValues = new ContentValues();
        contentValues.put(ArtContentProvider.NAME,artName);
        contentValues.put(ArtContentProvider.IMAGE,bytes);

        getContentResolver().insert(ArtContentProvider.CONTENT_URI,contentValues);

        Intent intent = new Intent(MainActivity2.this,MainActivity.class);
        startActivity(intent);

    }
    public void delete(View view){
        String deleteName = editText.getText().toString();
        String[] stringArray = {deleteName};
        getContentResolver().delete(ArtContentProvider.CONTENT_URI,"name=?",stringArray);


        Intent intent = new Intent(MainActivity2.this,MainActivity.class);
        startActivity(intent);
    }
    public void update(View view){
        String artName = editText.getText().toString();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] bytes = outputStream.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ArtContentProvider.NAME,artName);
        contentValues.put(ArtContentProvider.IMAGE,bytes);

        String[] selectionArguments = {firstName};
        getContentResolver().update(ArtContentProvider.CONTENT_URI,contentValues,"name=?",selectionArguments);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
    public void imageClick(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null){
            Uri image = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
                imageView.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
