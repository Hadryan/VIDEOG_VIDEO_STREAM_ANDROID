package com.gtechnologies.videog.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.gtechnologies.videog.Adapter.VideoAdapter;
import com.gtechnologies.videog.Http.ContentApiClient;
import com.gtechnologies.videog.Http.ContentApiInterface;
import com.gtechnologies.videog.Library.EndlessScrollListener;
import com.gtechnologies.videog.Library.Message;
import com.gtechnologies.videog.Library.Utility;
import com.gtechnologies.videog.Model.Content;
import com.gtechnologies.videog.Model.ContentResponse;
import com.gtechnologies.videog.R;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 3/29/2018.
 */

public class Selected extends Fragment {

    Context context;
    Utility utility;
    ListView listView;
    TextView noData;
    GifImageView loadingImage;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    ContentResponse contentResponse;
    List<Content> contents = null;
    VideoAdapter adapter;

    public Selected(){}

    @SuppressLint("ValidFragment")
    public Selected(Context context){
        this.context = context;
        this.utility = new Utility(this.context);
        contents = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_layout, null);
        listView = view.findViewById(R.id.listview);
        noData = view.findViewById(R.id.noData);
        loadingImage = view.findViewById(R.id.loadingImage);
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                utility.logger("Page: "+(page-1)+", Total Count: "+totalItemsCount);
                if(totalItemsCount%getResources().getInteger(R.integer.data_size)==0) {
                    getHighlight(page - 1);
                }
                return true;
            }
        });
        getHighlight(0);
        utility.setFont(noData);
        noData.setText(utility.getLangauge().equals("bn")?context.getString(R.string.no_data_bn):context.getString(R.string.no_data));
        return view;
    }

    private void getHighlight(int pageNumber){
        if(utility.isNetworkAvailable()) {
            Call<ContentResponse> call = apiInterface.getHighlight(context.getString(R.string.authorization_key), utility.getMsisdn(), "", pageNumber, getResources().getInteger(R.integer.data_size));
            call.enqueue(new Callback<ContentResponse>() {
                @Override
                public void onResponse(Call<ContentResponse> call, Response<ContentResponse> response) {
                    try {
                        if (response.isSuccessful() && response.code() == 200) {
                            contentResponse = response.body();
                            contents.addAll(contentResponse.getContents());
                            if (contents.size() > 0) {
                                utility.hideAndShowView(new View[]{listView, noData, loadingImage}, listView);
                                adapter = new VideoAdapter(context, contents);
                                listView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                listView.setSelection(contents.size() > 20 ? contents.size() - contentResponse.getContents().size() - 2 : 0);
                            } else {
                                utility.hideAndShowView(new View[]{listView, noData, loadingImage}, noData);
                            }
                        } else {
                            utility.logger(String.valueOf(response.code()));
                            //utility.showToast(String.valueOf(response.code()));
                            utility.hideAndShowView(new View[]{listView, noData, loadingImage}, noData);
                        }
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                        //utility.showToast(ex.toString());
                        utility.hideAndShowView(new View[]{listView, noData, loadingImage}, noData);
                    }
                }

                @Override
                public void onFailure(Call<ContentResponse> call, Throwable t) {
                    utility.logger(t.toString());
                    utility.showToast(Message.HTTP_ERROR);
                }
            });
        }
        else {
            utility.showToast(Message.NO_NET);
        }
    }
}
