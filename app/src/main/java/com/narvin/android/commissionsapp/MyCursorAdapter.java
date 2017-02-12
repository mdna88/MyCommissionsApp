package com.narvin.android.commissionsapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static android.R.attr.id;
import static android.R.id.edit;
import static android.content.Context.ALARM_SERVICE;

/**
 * Created by michaeldnarvaez on 7/17/16.
 */
public class MyCursorAdapter extends CursorAdapter {

    private ViewHolder holder;
    private CommissionsFragment mFragment;

    public MyCursorAdapter(Context context, Cursor c, CommissionsFragment fragment) {
        super(context, c);

        //Fragment that calls the Adapter, passed in for methods accessibility
        mFragment = fragment;

    }

    private int getItemViewType(Cursor cursor) {
        String type = cursor.getString(cursor.getColumnIndex("type"));
        if (type.equals("1")) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return getItemViewType(cursor);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        holder = new ViewHolder();
        View v = null;


        if (cursor.getString(cursor.getColumnIndex("type")).equals("0")) {
            v = inflater.inflate(R.layout.list_item, parent, false);
            holder.nameView = (TextView) v
                    .findViewById(R.id.item_name);
            holder.valueView = (TextView) v
                    .findViewById(R.id.item_value);
            holder.quantityView = (TextView) v
                    .findViewById(R.id.item_quantity);
            holder.addButton = (Button) v
                    .findViewById(R.id.item_add_button);

        } else {
            v = inflater.inflate(R.layout.list_item_alt, parent, false);
            holder.nameView = (TextView) v
                    .findViewById(R.id.item_name);
            holder.valueView = (TextView) v
                    .findViewById(R.id.item_value);
            holder.quantityView = (TextView) v
                    .findViewById(R.id.item_quantity);
            holder.addButton = (Button) v
                    .findViewById(R.id.item_add_button);

        }

        v.setTag(holder);

        return v;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        final int ROW_ID = cursor.getInt( cursor.getColumnIndex("_id") );

        final ViewHolder holder = (ViewHolder) view.getTag();

        final int commissionType = (cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(1))));
        final String commissionName = (cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));
        String commissionValue = (cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))));
        final String commissionQuantity = (cursor.getString(cursor.getColumnIndex(cursor.getColumnName(4))));

        String nameString = "" + commissionName;
        holder.nameView.setText(nameString);

        String valueString = null;
        if (commissionType == 0) {
            valueString = context.getResources().getString(R.string.what_i_make)
                    + commissionValue;
        } else {
            valueString = context.getResources().getString(R.string.what_i_make_percentage)
                    + " " + commissionValue + "%";
        }


        //TODO: Add on data change listener fou ui update
        holder.valueView.setText(valueString);

        String quantityString = context.getResources().getString(R.string.quantity_sold)
                + " " + commissionQuantity;
        holder.quantityView.setText(quantityString);

        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (commissionType == 0) {

                    int updatedQuantity = Integer.parseInt(commissionQuantity) + 1;

                    updateQuantity(context, ROW_ID, updatedQuantity);

                    holder.quantityView.setText(context.getResources().getString(R.string.quantity_sold)
                            + " " + updatedQuantity);
                    mFragment.fetchData();

                } else if (commissionType == 1) {

                    showAmountDialog(context, ROW_ID, Integer.parseInt(commissionQuantity),
                            holder.quantityView);

                }

            }
        });

    }

    //Displays Dialog, takes input from user for new sale and updates entry quantity
    public String showAmountDialog(Context parent, int id, int quantity, TextView v) {

        final Context context = parent;
        final int rowId = id;
        final int currentQuantity = quantity;
        final TextView view = v;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        edt.setInputType(InputType.TYPE_CLASS_NUMBER);

        dialogBuilder.setTitle("New Sale");
        dialogBuilder.setMessage("Enter Amount Below");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if (!edt.getText().toString().equals("")) {

                    // Adds up previous quantity sold plus new amount added by user
                    int updatedQuantity = currentQuantity + Integer.parseInt(edt.getText().toString());

                    updateQuantity(context, rowId, updatedQuantity);

                    holder.quantityView.setText(context.getResources().getString(R.string.quantity_sold)
                            + " " + updatedQuantity);
                    mFragment.fetchData();
                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();

        return null;
    }

    private void updateQuantity(Context context, int id, int quantity) {

        // Values with new quantity to update item with
        ContentValues values = new ContentValues();
        values.put(CommissionContract.CommissionEntry.QUANTITY, quantity);

        // Updates the quantity
        context.getContentResolver().update(
                ContentUris.withAppendedId(CommissionContract.CommissionEntry.CONTENT_URI, id),
                values,
                null,
                null);

    }

    private class ViewHolder {
        private TextView nameView;
        private TextView valueView;
        private TextView quantityView;
        private Button addButton;

    }
    
}