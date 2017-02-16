package com.narvin.android.commissionsapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by michaeldnarvaez on 7/17/16.
 */
public class CommissionsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the commission data loader
     */
    private static final int COMMISSION_LOADER = 0;
    private View parentView;
    private View rootView;
    private MyCursorAdapter mCursorAdapter;
    private ListView mListView;
    /** Projection for content provider */
    private String[] projection = {
            CommissionContract.CommissionEntry.KEY_ID,
            CommissionContract.CommissionEntry.TYPE,
            CommissionContract.CommissionEntry.COMMISSION_NAME,
            CommissionContract.CommissionEntry.COMMISSION_VALUE,
            CommissionContract.CommissionEntry.QUANTITY,
    };

    /**
     * Public Constructor
     */
    public CommissionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mCursorAdapter = new MyCursorAdapter(getActivity(), null, CommissionsFragment.this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        TextView toolbarTitle = (TextView) getActivity().findViewById(R.id.toolbar_title);
        toolbarTitle.setText("MyCommissions");

        //Floating Add new item button
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        return rootView;
    }

    /**
     * Retrieves data from SQLite database, starts CursorAdapter and fills listView
     * Also, handles user click events on the list item
     */
    public void fetchData() {

        mListView = (ListView) parentView.findViewById(R.id.listview_commissions);

        mListView.setAdapter(mCursorAdapter);
        mListView.setEmptyView(parentView.findViewById(R.id.empty_element));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int adapterPosition = position - mListView.getHeaderViewsCount();
                Cursor cursor = (Cursor) mCursorAdapter.getItem(adapterPosition);

                int posId = cursor.getInt(cursor.getColumnIndex(
                        CommissionContract.CommissionEntry.KEY_ID));
                String name = cursor.getString(cursor.getColumnIndex(
                        CommissionContract.CommissionEntry.COMMISSION_NAME));
                String type = cursor.getString(cursor.getColumnIndex(
                        CommissionContract.CommissionEntry.TYPE));
                double value = cursor.getDouble(cursor.getColumnIndex(
                        CommissionContract.CommissionEntry.COMMISSION_VALUE));
                double quantity = cursor.getDouble(cursor.getColumnIndex(
                        CommissionContract.CommissionEntry.QUANTITY));

                Bundle bundle = new Bundle();
                bundle.putInt("id", posId);
                bundle.putString("name", name);
                bundle.putString("type", type);
                bundle.putDouble("value", value);
                bundle.putDouble("quantity", quantity);

                DetailFragment fragment = new DetailFragment();
                fragment.setArguments(bundle);
                FragmentTransaction fragTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.fragment_container, fragment).addToBackStack("detail");
                fragTransaction.commit();

            }
        });

        // Start the the loader
        getLoaderManager().initLoader(COMMISSION_LOADER, null, this);

        refreshTotalCommissions();
    }

    /**
     * Method with simple algorithm that calculates total commissions and displays it
     */
    public void refreshTotalCommissions() {

        Cursor cursor = getActivity().getContentResolver().query(
                CommissionContract.CommissionEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        double totalCommissions = 0;

        //Loops trough every row of the commissions table and adds them together
        while (!cursor.isAfterLast()) {
            int commissionType = (cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(1))));
            String commissionValue = (cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))));
            String commissionQuantity = (cursor.getString(cursor.getColumnIndex(cursor.getColumnName(4))));

            if (commissionType == 0) {
                double currentValue = Double.parseDouble(commissionValue) * Double.parseDouble(commissionQuantity);
                totalCommissions += currentValue;

            } else {
                double currentValue = (Double.parseDouble(commissionValue) * .01) * Double.parseDouble(commissionQuantity);
                totalCommissions += currentValue;
            }

            cursor.moveToNext();
        }

        cursor.close();

        //Format the commission total to two decimals after period
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        twoDForm.setMinimumFractionDigits(2);

        TextView totalCommissionsView = (TextView) parentView.findViewById(
                R.id.total_commissions_view);

        totalCommissionsView.setText("Total Commissions: $" + NumberFormat.getNumberInstance(Locale.US).format(totalCommissions));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fetchData();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Get Reference to the content_main view
        parentView = getActivity().findViewById(R.id.drawer_layout);
        fetchData();

    }

    @Override
    public void onResume() {
        super.onResume();
        fetchData();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                CommissionContract.CommissionEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Update the cursor with the new data from database
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Reset the adapter, preparing it for the new data
        mCursorAdapter.swapCursor(null);

    }
}
