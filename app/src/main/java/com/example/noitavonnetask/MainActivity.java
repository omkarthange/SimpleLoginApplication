package com.example.noitavonnetask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    int SELECT_PICTURE = 200;

    Button registerButton;
    Button loginButton;
    LinearLayout welcomeLayout;
    LinearLayout viewProfileLayout;
    ImageView profilePhoto;
    ImageView selectProfilePhoto;

    Button addButton;
    LinearLayout addButtonLayout;
    EditText addItemKeyEditText;
    EditText addItemValueEditText;
    Button addItemCancelButton;
    Button addItemAddButton;

    Button clearAllData;
    Button signOut;

    ListView listView;
    ListAdapter listAdapter;
    ArrayList<Item> itemArrayList;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerButton = findViewById(R.id.register_button);
        loginButton = findViewById(R.id.login_button);
        welcomeLayout = findViewById(R.id.welcomeLayout);
        viewProfileLayout = findViewById(R.id.viewProfileLayout);
        profilePhoto = findViewById(R.id.profile_photo);
        selectProfilePhoto = findViewById(R.id.select_profile_photo);

        addButton = findViewById(R.id.addButton);
        addButtonLayout = findViewById(R.id.addButtonLayout);
        addItemKeyEditText = findViewById(R.id.addItemKeyEditText);
        addItemValueEditText = findViewById(R.id.addItemValueEditText);
        addItemCancelButton = findViewById(R.id.addItemCancelButton);
        addItemAddButton = findViewById(R.id.addItemAddButton);

        clearAllData = findViewById(R.id.clearAllData);
        signOut = findViewById(R.id.sign_out);

        listView = findViewById(R.id.profile_listView);
        itemArrayList = new ArrayList<>();
        listAdapter = new ListAdapter(MainActivity.this, itemArrayList);
        listView.setAdapter(listAdapter);

        boolean isLoginSuccess = false;

        Intent intent = this.getIntent();
        if(intent!=null){
            try {
                isLoginSuccess = intent.getExtras().getBoolean("isLoginSuccess");
            }catch (Exception ignored){
            }
        }

        if(isLoginSuccess){
            welcomeLayout.setVisibility(View.GONE);
            viewProfileLayout.setVisibility(View.VISIBLE);
            try {
                Map<String, String> outputMap = SharedPreference.getMap(MainActivity.this);
                for (Map.Entry<String,String> mapItem : outputMap.entrySet()){
                    Item item = new Item(mapItem.getKey(), mapItem.getValue());
                    itemArrayList.add(item);
                    listAdapter.notifyDataSetChanged();
                }
                getImage();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        if(SharedPreference.getName(MainActivity.this).length()>0){
//            welcomeLayout.setVisibility(View.GONE);
//            viewProfileLayout.setVisibility(View.VISIBLE);
//        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });

        selectProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
//                profilePhoto.setDrawingCacheEnabled(true);
//                saveImageToInternalStorage(MainActivity.this, profilePhoto.getDrawingCache(), "Profile_Photo");
//                SharedPreference.saveImage(MainActivity.this, profilePhoto.getDrawingCache());
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButtonLayout.setVisibility(View.VISIBLE);
            }
        });

        addItemCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButtonLayout.setVisibility(View.GONE);
            }
        });

        addItemAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateNewData()){
                    try {
                        SharedPreference.editValue(MainActivity.this, addItemKeyEditText.getText().toString(), addItemValueEditText.getText().toString());
                        Item item = new Item(addItemKeyEditText.getText().toString(), addItemValueEditText.getText().toString());
                        itemArrayList.add(item);
                        listAdapter.notifyDataSetChanged();
                        addItemKeyEditText.setText("");
                        addItemValueEditText.setText("");
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(addButtonLayout.getWindowToken(), 0);
                        addButtonLayout.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        clearAllData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference.clearData(MainActivity.this);
                deleteImage();
                profilePhoto.setImageResource(R.drawable.ic_baseline_account_circle_24);
                listAdapter.clear();
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                welcomeLayout.setVisibility(View.VISIBLE);
                viewProfileLayout.setVisibility(View.GONE);
            }
        });
    }

    public boolean validateNewData(){
        if(addItemKeyEditText.getText().toString().length()==0){
            addItemKeyEditText.setError("Key should not be empty");
            return false;
        }

        if(addItemValueEditText.getText().toString().length()==0){
            addItemValueEditText.setError("Value should not be empty");
            return false;
        }

        return true;
    }

    public void deleteImage(){
        String filename = "image.png";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(storageDir, filename);
        if (file.exists()) {
            boolean isDeleted = file.delete();
            if (isDeleted) {
                Log.d("FileDeletion", "The file was successfully deleted");
            } else {
                Log.e("FileDeletion", "The file could not be deleted");
            }
        } else {
            Log.e("FileDeletion", "The file does not exist");
        }
    }

    private void getImage(){
        String filename = "image.png";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(storageDir, filename);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        if (bitmap != null) {
            // Image is in storage
            Glide.with(this)
                    .load(bitmap)
                    .circleCrop()
                    .into(profilePhoto);
//                    profilePhoto.setImageBitmap(bitmap);
        }
    }

    private void saveImage(){
        Bitmap bitmap = ((BitmapDrawable) profilePhoto.getDrawable()).getBitmap();
        String filename = "image.png";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(storageDir, filename);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this function is triggered when
    // the Select Image Button is clicked
    private void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    Glide.with(this)
                            .load(selectedImageUri)
                            .circleCrop()
                            .into(profilePhoto);
                    profilePhoto.setImageURI(selectedImageUri);
                    saveImage();
                }
            }
        }
    }

}