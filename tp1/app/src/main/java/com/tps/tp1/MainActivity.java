package com.tps.tp1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Currency exchange rates
    private static final double DOLLAR_RATE = 1.08;
    private static final double POUND_RATE = 1.18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI Components
        TextView amountInput = findViewById(R.id.amount_input_text);
        RadioButton fromEuro = findViewById(R.id.from_euro);
        RadioButton fromDollars = findViewById(R.id.from_dollars);
        RadioButton fromPounds = findViewById(R.id.from_pounds);
        RadioButton toEuro = findViewById(R.id.to_euro);
        RadioButton toDollars = findViewById(R.id.to_dollars);
        RadioButton toPounds = findViewById(R.id.to_pounds);
        Button convertButton = findViewById(R.id.convert_button);
        TextView result = findViewById(R.id.text_view_result);

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = amountInput.getText().toString();

                double amount = Double.parseDouble(amountStr);
                double conversionResult = 0;

                // Euro conversions
                if (fromEuro.isChecked()) {
                    if (toDollars.isChecked()) {
                        conversionResult = amount * DOLLAR_RATE;
                    } else if (toPounds.isChecked()) {
                        conversionResult = amount * POUND_RATE;
                    } else if (toEuro.isChecked()) {
                        conversionResult = amount;
                    }
                }

                // Dollar conversions
                if (fromDollars.isChecked()) {
                    if (toEuro.isChecked()) {
                        conversionResult = amount / DOLLAR_RATE;
                    } else if (toPounds.isChecked()) {
                        conversionResult = amount / DOLLAR_RATE * POUND_RATE;
                    } else if (toDollars.isChecked()) {
                        conversionResult = amount;
                    }
                }

                // Pound conversions
                if (fromPounds.isChecked()) {
                    if (toEuro.isChecked()) {
                        conversionResult = amount / POUND_RATE;
                    } else if (toDollars.isChecked()) {
                        conversionResult = amount / POUND_RATE * DOLLAR_RATE;
                    } else if (toPounds.isChecked()) {
                        conversionResult = amount;
                    }
                }

                result.setText("RESULT: " + conversionResult);
            }
        });
    }

}
