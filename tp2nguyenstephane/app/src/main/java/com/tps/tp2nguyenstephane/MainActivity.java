package com.tps.tp2nguyenstephane;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_CONTACT = 1;

    private String currentContactName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            String backgroundColorString = savedInstanceState.getString("color");
            View entireScreen = findViewById(R.id.entire_screen);

            if (backgroundColorString != null) {
                int backgroundColor = Color.parseColor(backgroundColorString);
                entireScreen.setBackgroundColor(backgroundColor);
            }

            currentContactName = savedInstanceState.getString("CURRENT_CONTACT_NAME", "");
            TextView contactText = findViewById(R.id.contact_text);
            contactText.setText(currentContactName);
        }

        // Button to navigate to URL
        Button openUrlButton = findViewById(R.id.open_url_button);
        openUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlToNavigateTo = "https://www.dragonflycave.com/favorite.html";
                Intent urlIntent = new Intent(Intent.ACTION_VIEW);
                urlIntent.setData(Uri.parse(urlToNavigateTo));
                startActivity(urlIntent);
            }
        });

        // View contact list
        Button contact = findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactListIntent = new Intent(Intent.ACTION_PICK, android.provider.ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactListIntent, REQUEST_SELECT_CONTACT);
            }
        });


        // Change the text color with implicit intent
        Intent intent = new Intent(this, ColorActivity.class);

        Button button = findViewById(R.id.color_button);
        View entireScreen = findViewById(R.id.entire_screen);

        // retrieve data when activity is done
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult colorActivityResult) {
                        if (colorActivityResult.getResultCode() == Activity.RESULT_OK && colorActivityResult.getData() != null) {
                            String color = colorActivityResult.getData().getStringExtra("color");
                            entireScreen.setBackgroundColor(Color.parseColor(color));
                        }

                    }
                }
        );

        button.setOnClickListener(view -> {
            launcher.launch(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        TextView contactText = findViewById(R.id.contact_text);

        if (requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            String[] projection = { ContactsContract.Contacts.DISPLAY_NAME };


            try (Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    currentContactName = name;
                    contactText.setText(name);
                }
            }
        }
    }

}