package com.narvin.android.commissionsapp;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<ArticleContract>>, SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_delete_all).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Set title on the action bar
        TextView toolbarTitle = (TextView) getActivity().findViewById(R.id.toolbar_title);
        toolbarTitle.setText("News & Articles");

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        //Check internet connection before starting AsyncTaskLoader
        if (isOnline()) {

            /**
             * Showing Swipe Refresh animation on activity create
             * As animation won't start on onCreate, post runnable is used
             */
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            //Initiate the Web API data fetching process
                                            fetchArticles();
                                        }
                                    }
            );

        } else {
            updateDisplay(new ArrayList<ArticleContract>());
        }

    }

    //Starts the ArticleContract Loader
    private void fetchArticles() {
        getActivity().getSupportLoaderManager().initLoader(1, null, this).forceLoad();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Reference to fragment parentView
        rootView= inflater.inflate(R.layout.fragment_news, container, false);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public Loader<ArrayList<ArticleContract>> onCreateLoader(int id, Bundle args) {
        return new ArticleLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ArticleContract>> loader, ArrayList<ArticleContract> data) {
        updateDisplay(data);
        getLoaderManager().destroyLoader(loader.getId());

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ArticleContract>> loader) {
        updateDisplay(new ArrayList<ArticleContract>());

    }

    // Updates ListView with extracted info from the Loader
    protected void updateDisplay(final ArrayList<ArticleContract> articleContractList) {

        if (articleContractList != null) {

            ArticleAdapter adapter = new ArticleAdapter(getActivity(), articleContractList);

            final ListView listView = (ListView) rootView.findViewById(R.id.news_listView);
            listView.setAdapter(adapter);
            listView.setEmptyView(rootView.findViewById(R.id.empty_element2));
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (listView.getChildAt(0) != null) {
                        swipeRefreshLayout.setEnabled(listView.getFirstVisiblePosition() == 0 && listView.getChildAt(0).getTop() == 0);
                    }
                }
            });

            swipeRefreshLayout.setRefreshing(false);

        } else {

        }

    }

    //Used to convert Images to thumbnails from JSON parse
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "thumbnail");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    // Checks internet connectivity.
    protected boolean isOnline() {

        ConnectivityManager connectManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = connectManager.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRefresh() {
        if (isOnline()) {
            fetchArticles();
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }

    }
}
