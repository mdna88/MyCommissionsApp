package com.narvin.android.commissionsapp;


import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    View parentView;
    private TextView detailName;
    private TextView detailValue;
    private TextView detailQuantity;
    private TextView detailTotal;
    private FloatingActionButton clearButton;
    private FloatingActionButton deleteButton;
    private int id;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        // Hide option menu for this fragment
        menu.findItem(R.id.action_delete_all).setVisible(false);
        menu.findItem(R.id.action_clear_all).setVisible(false);

        //TODO: Add options menu item for edit mode
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView toolbarTitle = (TextView) getActivity().findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Item Details");

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Get Reference to the content_main view
        parentView = getActivity().findViewById(R.id.drawer_layout);

        id = getArguments().getInt("id");
        String name = getArguments().getString("name");
        String type = getArguments().getString("type");
        double value = getArguments().getDouble("value");
        String quantity = String.valueOf(getArguments().getDouble("quantity"));

        DecimalFormat twoDForm = new DecimalFormat("0.00");
        twoDForm.setMinimumFractionDigits(2);

        double totalComm;
        String valueString;

        if (type.equals("0")) {
            totalComm = value * Double.parseDouble(quantity);
            valueString = "Value Per Sale: $" + twoDForm.format(value);

        } else {
            totalComm = (value * .01) * Double.parseDouble(quantity);
            valueString = "Value Per Sale: " + value + "%";

        }

        detailName = (TextView) parentView.findViewById(R.id.detail_sale_item);
        detailValue = (TextView) parentView.findViewById(R.id.detail_value);
        detailQuantity = (TextView) parentView.findViewById(R.id.detail_quantity);
        detailTotal = (TextView) parentView.findViewById(R.id.detail_total_commissions);
        clearButton = (FloatingActionButton) parentView.findViewById(R.id.fab_clear);
        deleteButton = (FloatingActionButton) parentView.findViewById(R.id.fab_delete);

        detailName.setText(name);
        detailValue.setText(valueString);
        detailQuantity.setText("Quantity Sold: " + quantity);
        detailTotal.setText("$" + twoDForm.format(totalComm));

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Clear Quantity")
                        .setMessage("Are you sure you want to clear the quantity?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                // Values with new quantity to update item with, in this case 0
                                ContentValues values = new ContentValues();
                                values.put(CommissionContract.CommissionEntry.QUANTITY, 0);

                                // Updates the quantity
                                getActivity().getContentResolver().update(
                                        ContentUris.withAppendedId(
                                                CommissionContract.CommissionEntry.CONTENT_URI, id),
                                        values,
                                        null,
                                        null);

                                detailQuantity.setText("0");
                                detailTotal.setText("$0.00");
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete this item?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                Toast.makeText(getActivity(), "Item Deleted", Toast.LENGTH_SHORT).show();

                                // Updates the quantity
                                getActivity().getContentResolver().delete(
                                        ContentUris.withAppendedId(
                                                CommissionContract.CommissionEntry.CONTENT_URI, id),
                                        null,
                                        null);


                                getFragmentManager().popBackStackImmediate();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

    }

}
