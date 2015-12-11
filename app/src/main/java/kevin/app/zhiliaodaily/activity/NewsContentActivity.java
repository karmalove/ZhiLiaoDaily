package kevin.app.zhiliaodaily.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;

import kevin.app.zhiliaodaily.R;
import kevin.app.zhiliaodaily.db.WebCacheDbHelper;
import kevin.app.zhiliaodaily.model.Content;
import kevin.app.zhiliaodaily.model.StoriesEntity;
import kevin.app.zhiliaodaily.util.Constant;
import kevin.app.zhiliaodaily.util.HttpUtils;
import kevin.app.zhiliaodaily.view.RevealBackgroundView;

/**
 * Created by Kevin on 2015/11/30.
 */
public class NewsContentActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener {
    private WebView mWebView;
    private StoriesEntity entity;
    private Content content;
    private RevealBackgroundView vRevealBackgroundView;
    private CoordinatorLayout coordinatorLayout;
    private WebCacheDbHelper dbHelper;
    private boolean isLight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_content_layout);
        dbHelper = new WebCacheDbHelper(this, 1);
        isLight = getIntent().getBooleanExtra("isLight", true);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        coordinatorLayout.setVisibility(View.INVISIBLE);
        entity = (StoriesEntity) getIntent().getSerializableExtra("entity");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("享受阅读的乐趣");
        toolbar.setBackgroundColor(getResources().getColor(isLight ? R.color.light_toolbar : R.color.dark_toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //开启DOM storage API功能
        mWebView.getSettings().setDomStorageEnabled(true);
        //开启database storage API功能
        mWebView.getSettings().setDatabaseEnabled(true);
        //开启Application Cache功能
        mWebView.getSettings().setAppCacheEnabled(true);
        if (HttpUtils.isNetworkConnected(this)) {
            HttpUtils.get(Constant.CONTENT + entity.getId(), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int i, Header[] headers, String responseString) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    responseString = responseString.replaceAll("'", "''");
                    db.execSQL("replace into Cache(newsId,json) values(" + entity.getId() + ",'" + responseString + "')");
                    db.close();
                    parseJson(responseString);
                }
            });
        } else {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from Cache where newsId=" + entity.getId(), null);
            if (cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndex("json"));
                parseJson(json);
            }
            cursor.close();
            db.close();
        }
        setupRevealBackground(savedInstanceState);
    }

    private void parseJson(String responseString) {
        Gson gson = new Gson();
        content = gson.fromJson(responseString, Content.class);
        /*final ImageLoader imageLoader=ImageLoader.getInstance();
        DisplayImageOptions options=new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        imageLoader.displayImage(content.getImage(),iv,options);*/
        String css = " <link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
        String html = "<html><head>" + css + "</head><body>" + content.getBody() + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        mWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackgroundView.setOnStateChangeListener(this);
        if (savedInstanceState==null){
            final int[] startingLocation=getIntent().getIntArrayExtra(Constant.START_LOCATION);
            vRevealBackgroundView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackgroundView.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackgroundView.startFromLocation(startingLocation);
                    return true;
                }
            });
        }else {
            vRevealBackgroundView.setToFinishedFrame();
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED==state){
            coordinatorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,R.anim.slide_out_to_left);
    }
}
