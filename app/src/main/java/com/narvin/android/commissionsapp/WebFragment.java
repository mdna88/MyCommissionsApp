package com.narvin.android.commissionsapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WebFragment extends Fragment {

    private WebView webView;

    public WebFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_web_view, container, false);
        TextView toolbarTitle = (TextView) getActivity().findViewById(R.id.toolbar_title);
        toolbarTitle.setText("News & Articles");

        //Check for empty url argument
        if (getArguments() != null) {

            ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_web);
            progressBar.setVisibility(View.VISIBLE);

            String url = getArguments().getString("url");
            webView = (WebView) rootView.findViewById(R.id.webView1);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(url);

            progressBar.setVisibility(View.GONE);

        } else {
            Toast.makeText(getActivity(), "Error loading URL", Toast.LENGTH_SHORT).show();
        }

        // Inflate the layout for this fragment
        return rootView;
    }

}
