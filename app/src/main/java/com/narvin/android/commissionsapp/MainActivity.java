package com.narvin.android.commissionsapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    SQLHelper mSQLHelper;
    NavigationView navigationView = null;
    Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        //Check if a fragment has been loaded if null start new one
        if (savedInstanceState == null) {
            //Set initial Fragment
            CommissionsFragment fragment = new CommissionsFragment();
            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.fragment_container, fragment, "commissions");
            fragTransaction.commit();

        } else {
            CommissionsFragment fragment = (CommissionsFragment) getSupportFragmentManager()
                    .findFragmentByTag("commissions");
        }

        //Support Action Bar
        toolbar = (Toolbar) findViewById(R.id.new_toolbar);
        setSupportActionBar(toolbar);

        //Side navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //SQLLite manager class instantiation
        mSQLHelper = new SQLHelper(this);

    }

    /**
     * ActionBar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Check Selected ActionBar menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Instantiated to get access to its fragment methods
        final CommissionsFragment fragment = (CommissionsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        switch (item.getItemId()) {
            //Delete all entries
            case R.id.action_delete_all:
                new AlertDialog.Builder(this)
                        .setTitle("Delete All Items?")
                        .setMessage("Are you sure you want to delete all entries?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                //Deletes entire database
                                mSQLHelper.deleteAllEntries();
                                //Resets the listView
                                fragment.fetchData();

                                Toast.makeText(MainActivity.this, "Items Deleted",
                                        Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

                break;

            //Clear quantities for all entries
            case R.id.action_clear_all:
                new AlertDialog.Builder(this)
                        .setTitle("Reset All Quantities?")
                        .setMessage("Are you sure you want to reset all quantities?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                mSQLHelper.clearAllQuantity();
                                fragment.fetchData();

                                Toast.makeText(MainActivity.this, "Items Reset",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

                break;

            default:
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {
            //Case MyCommissions tab is selected
            case R.id.commissions:
                CommissionsFragment commFragment = new CommissionsFragment();
                FragmentTransaction fragCommTransaction = getSupportFragmentManager().beginTransaction();
                fragCommTransaction.replace(R.id.fragment_container, commFragment, "commissions").addToBackStack("commissions");
                fragCommTransaction.commit();

                break;

            //Case News & Articles tab is selected
            case R.id.news:
                NewsFragment newsFragment = new NewsFragment();
                FragmentTransaction fragNewsTransaction = getSupportFragmentManager().beginTransaction();
                fragNewsTransaction.replace(R.id.fragment_container, newsFragment, "news").addToBackStack("news");
                fragNewsTransaction.commit();

                break;

            //Case Share tab is selected
            case R.id.nav_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.narvin.android.commissionsapp");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this amazing app!");
                startActivity(Intent.createChooser(intent, "Share"));

                break;

            default:
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}