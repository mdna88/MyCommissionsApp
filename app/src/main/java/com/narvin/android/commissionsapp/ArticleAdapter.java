package com.narvin.android.commissionsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by michaeldnarvaez on 7/1/16.
 */
public class ArticleAdapter extends ArrayAdapter<ArticleContract> {

    private ArrayList<ArticleContract> mArticleContractList;

    /**
     * Public constructor
     * @param context Calling parent activity
     * @param articleContractList List of articles received from JSON parse via webHose API
     */
    public ArticleAdapter(Context context, ArrayList<ArticleContract> articleContractList) {
        super(context, 0, articleContractList);
        mArticleContractList = articleContractList;

    }

    @Override
    public View getView(int position, View convertedView, ViewGroup parent) {

        //position of listItem within listView
        final int itemPosition = position;

        //Check current listItem status
        View listItemView = convertedView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_news, parent, false);
        }

        //Gets individual article
        ArticleContract currentArticleContract = getItem(position);

        //Set Header
        TextView headerView = (TextView) listItemView.findViewById(R.id.news_header);
        headerView.setText(currentArticleContract.getHeader());

        //Set SubHeader
        TextView subHeaderView = (TextView) listItemView.findViewById(R.id.news_subHeader);
        subHeaderView.setText(currentArticleContract.getSubHeader());

        //Handles user clicks on list items and takes them to the article on the web view
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get parent activity context
                Activity hostActivity = (Activity) v.getContext();

                //get the url for clicked item
                String currentUrl = mArticleContractList.get(itemPosition).getUrl();

                //Add url to bundle
                Bundle bundle = new Bundle();
                bundle.putString("url", currentUrl);

                //Start webView and load in the article
                WebFragment fragment = new WebFragment();
                //Add the url reference
                fragment.setArguments(bundle);
                FragmentTransaction fragTransaction = ((FragmentActivity) hostActivity).getSupportFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.fragment_container, fragment).addToBackStack("webView");
                fragTransaction.commit();

            }
        });

        ImageButton listMenuButton = (ImageButton) listItemView.findViewById(R.id.news_menu_button);
        listMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Reference to the url for the clicked list item article
                final String currentUrl = mArticleContractList.get(itemPosition).getUrl();

                //Reference to the current list item
                final View view = v;

                //List item menu with share option
                PopupMenu menu = new PopupMenu(v.getContext(), v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.item_share:
                                //Start url share intent
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_TEXT, currentUrl);
                                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this article!");
                                view.getContext().startActivity(Intent.createChooser(intent, "Share"));

                                break;

                        }
                        return true;
                    }
                });
                //inflate the menu for the list item
                menu.inflate(R.menu.list_item_menu);
                menu.show();

            }
        });

        return listItemView;

    }

}

