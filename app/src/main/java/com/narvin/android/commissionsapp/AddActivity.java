package com.narvin.android.commissionsapp;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import static com.narvin.android.commissionsapp.CommissionContract.CommissionEntry.COMMISSION_NAME;
import static com.narvin.android.commissionsapp.CommissionContract.CommissionEntry.COMMISSION_VALUE;
import static com.narvin.android.commissionsapp.CommissionContract.CommissionEntry.QUANTITY;

public class AddActivity extends AppCompatActivity {

    private EditText nameView;
    private EditText valueView;
    private FloatingActionButton addButton;
    private RadioButton fixedValueButton;
    private RadioButton percentageBasedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //New entry fields & buttons
        nameView = (EditText) findViewById(R.id.commission_name_view);
        valueView = (EditText) findViewById(R.id.commission_value_view);
        fixedValueButton = (RadioButton) findViewById(R.id.fixed_RadioButton);
        percentageBasedButton = (RadioButton) findViewById(R.id.percentage_RadioButton);

        //Button to add new entry to database
        addButton = (FloatingActionButton) findViewById(R.id.commission_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addNewItem();
            }
        });
    }

    private void addNewItem() {

        //Get the values to be stored
        String commissionName = nameView.getText().toString();
        String commissionValue = valueView.getText().toString();

        //Check if user input is not empty string
        if (!TextUtils.isEmpty(commissionName) && !TextUtils.isEmpty(commissionValue)) {

            //Case for flat rate commission
            if (fixedValueButton.isChecked()) {

                //Values to be inserted as new row in database
                ContentValues values = new ContentValues();
                values.put(CommissionContract.CommissionEntry.TYPE,
                        CommissionContract.CommissionEntry.FIXED_TYPE);
                values.put(COMMISSION_NAME, commissionName);
                values.put(COMMISSION_VALUE, commissionValue);
                values.put(QUANTITY, 0);

                //initiate new row insertion in database
                getContentResolver().insert(CommissionContract.CommissionEntry.CONTENT_URI, values);

                //Exit Activity
                finish();

                //Case for percentage based commission
            } else if (percentageBasedButton.isChecked()) {

                //Values to be inserted as new row in database
                ContentValues values = new ContentValues();
                values.put(CommissionContract.CommissionEntry.TYPE,
                        CommissionContract.CommissionEntry.PERCENTAGE_TYPE);
                values.put(COMMISSION_NAME, commissionName);
                values.put(COMMISSION_VALUE, commissionValue);
                values.put(QUANTITY, 0);

                //initiate new row insertion in database
                getContentResolver().insert(CommissionContract.CommissionEntry.CONTENT_URI, values);

                //Exit Activity
                finish();

            } else {
                Toast.makeText(AddActivity.this, R.string.error_select_commission_type,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AddActivity.this, R.string.error_select_name_value_type,
                    Toast.LENGTH_LONG).show();
        }

    }
}
