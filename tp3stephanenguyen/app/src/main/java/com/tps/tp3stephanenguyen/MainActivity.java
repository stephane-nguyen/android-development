package com.tps.tp3stephanenguyen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText numberInput;
    private Button moduloButton;
    private Button clearButton;
    private Button deleteOneNumberButton;
    private String moduloNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handleUserGuideButton();
        handleCalculator();
    }

    // User Guide
    private void handleUserGuideButton() {
        Button userGuideButton = findViewById(R.id.userGuideButton);
        userGuideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayUserGuideFragment();
            }
        });
    }
    private void displayUserGuideFragment() {
        UserGuideFragment userGuideFragment = new UserGuideFragment();

        // Be retro-compatible with all previous android versions
        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.user_guide_container, userGuideFragment, "")
            .commit();
    }

    // Calculator
    private void handleCalculator() {
        attachNumericPad();
        onPressModuloButton();
        setInputToZero();
        deleteLastNumber();
    }

    private void attachNumericPad() {
        moduloButton = findViewById(R.id.moduloButton);

        int[] buttonIds = {
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5,
            R.id.button6, R.id.button7, R.id.button8, R.id.button9
        };

        View.OnClickListener numberPadClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendNumberToModuloButton(((Button) v).getText().toString(), moduloButton);
            }
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(numberPadClickListener);
        }
    }

    private void appendNumberToModuloButton(String padClickedStr, Button moduloButton) {
        String moduloOf = "Modulo of ";
        moduloNumber += padClickedStr;
        String stringToDisplay = moduloOf + moduloNumber;
        moduloButton.setText(stringToDisplay);
    }

    private void onPressModuloButton() {
        moduloButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Wait for 2 seconds (2000 milliseconds)
                final int DELAY_IN_MS = 2000;
                moduloButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handleSnackbar();
                    }
                }, DELAY_IN_MS);
                return true;
            }
        });
    }

    private void handleSnackbar() {
        moduloButton = findViewById(R.id.moduloButton);
        numberInput = findViewById(R.id.numberInput);

        ConstraintLayout layout = null;
        try {
            int selectedNumber = Integer.parseInt(moduloNumber);
            int inputNumber = Integer.parseInt(numberInput.getText().toString());

            layout = findViewById(R.id.constraintLayout);

            if (inputNumber % selectedNumber == 0) {
                Snackbar.make(layout, inputNumber + " is a multiple of " + selectedNumber, Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(layout, inputNumber + " is not a multiple of " + selectedNumber, Snackbar.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            Snackbar.make(layout, "Invalid input", Snackbar.LENGTH_LONG).show();
        }
    }

    private void setInputToZero() {
        clearButton = findViewById(R.id.buttonClear);
        moduloButton = findViewById(R.id.moduloButton);
        numberInput = findViewById(R.id.numberInput);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberInput.setText("");
                moduloNumber = "";
                moduloButton.setText(getString(R.string.modulo));
            }
        });
    }
    private void deleteLastNumber() {
        deleteOneNumberButton = findViewById(R.id.deleteOneNumber);
        moduloButton = findViewById(R.id.moduloButton);
        numberInput = findViewById(R.id.numberInput);

        deleteOneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = moduloButton.getText().toString();
                char lastChar = number.charAt(number.length() - 1);

                if (Character.isDigit(lastChar)) {
                    String updatedNumber = number.substring(0, number.length() - 1);
                    moduloNumber = moduloNumber.substring(0, moduloNumber.length() - 1);
                    moduloButton.setText(updatedNumber);
                } else {
                    moduloButton.setText((getString(R.string.modulo)));
                }
            }
        });
    }

    // About Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.aboutItem) {
            openDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDialog() {
        Dialog dialog = new MaterialAlertDialogBuilder(this)
                .setMessage("Math instructor is about modulo")
                .setTitle(getString(R.string.about))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // No need to add anything here, dialog will close automatically
                    }
                })
                .create();
        dialog.show();
    }

    // Languages switch
    public void onUsaFlagClicked(View view) {
        setLocale("en");
    }
    public void onFranceFlagClicked(View view) {
        setLocale("fr");
    }
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);

        // Restart the current activity
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();
    }
}