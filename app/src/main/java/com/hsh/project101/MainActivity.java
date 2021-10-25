package com.hsh.project101;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.hsh.project101.constant.AppleConstant.AUTH_TOKEN;
import static com.hsh.project101.constant.AppleConstant.IMG_SIZE;
import static com.hsh.project101.constant.AppleConstant.SEARCH_OFFSET;
import static com.hsh.project101.constant.AppleConstant.SEARCH_URL;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @ViewById(R.id.sv)
    SearchView sv;

    @ViewById(R.id.rv)
    RecyclerView rv;

    private ArrayList<MusicData> musicData;
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
    private CustomProgressDialog dialog;

    @Bean
    MusicRvAdapter adapter;

    int offset = 0;
    String types = "songs";
    String terms;
    String token;

    @AfterViews
    void init() {
        getToken();
        initListener();
        initRv();

        dialog = new CustomProgressDialog(this);
    }

    @Background
    void getToken() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://music.apple.com/tw/browse")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String rs = response.body().string();
            Pattern pattern = Pattern.compile("%22token%22%3A%22([\\S]*)%22%7D");
            Matcher matcher = pattern.matcher(rs);

            if(matcher.find()) {
                token = "Bearer " + matcher.group(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    void initRv() {
        musicData = new ArrayList<>();
        adapter.setList(musicData);
        layoutManager = new LinearLayoutManager(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(layoutManager);
    }

    void initListener() {
        sv.setOnQueryTextListener(this);
        rv.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isLoading = true;
                            offset += SEARCH_OFFSET;
                            dialog.show();
                            search();

                        }
                    }
                }
            }
        });

    }

    @Background
    void search() {
        if (TextUtils.isEmpty(terms) || TextUtils.isEmpty(types)) {
            endGetData();
            return;
        }

        OkHttpClient client = new OkHttpClient();

        String url = String.format(SEARCH_URL, types, terms, offset);

        if(TextUtils.isEmpty(token)) {
            token = AUTH_TOKEN;
        }

        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .url(url)
                .build();


        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            String rs = response.body().string();
            JsonArray datas = gson.fromJson(rs, JsonObject.class).getAsJsonObject("results").getAsJsonObject(types).getAsJsonArray("data");

            for (int i = 0; i < datas.size(); i++) {
                musicData.add(parse(datas.get(i).getAsJsonObject().getAsJsonObject("attributes")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        endGetData();
    }

    private MusicData parse(JsonObject data) {
        MusicData musicData = new MusicData();
        musicData.setName(data.get("name").getAsString());
        musicData.setAlbumName(data.get("albumName").getAsString());
        musicData.setImgUrl(data.getAsJsonObject("artwork").get("url").getAsString().replace("{w}x{h}", IMG_SIZE));
        musicData.setDurationInMillis(data.get("durationInMillis").getAsInt());
        musicData.setM4aUrl(data.getAsJsonArray("previews").get(0).getAsJsonObject().get("url").getAsString());
        return musicData;
    }

    @UiThread
    void endGetData() {
        if (offset == 0) {
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyItemInserted(offset);
        }
        isLoading = false;
        dialog.dismiss();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(sv.getWindowToken(), 0);

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        terms = query;
        offset = 0;
        musicData.clear();
        dialog.show();
        sv.clearFocus();
        search();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}