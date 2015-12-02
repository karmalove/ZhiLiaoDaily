package kevin.app.zhiliaodaily.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import kevin.app.zhiliaodaily.R;
import kevin.app.zhiliaodaily.activity.MainActivity;
import kevin.app.zhiliaodaily.adapter.NewsItemAdapter;
import kevin.app.zhiliaodaily.model.NewsListItem;
import kevin.app.zhiliaodaily.util.Constant;
import kevin.app.zhiliaodaily.util.HttpUtils;

/**
 * Created by Kevin on 2015/11/13.
 */
public class MenuFragment extends BaseFragment implements View.OnClickListener {
    private ListView lv_item;
    private TextView tv_download, tv_main, tv_backup, tv_login;
    private LinearLayout ll_menu;
    private List<NewsListItem> items;
    private Handler handler = new Handler();
    private boolean isLight;
    private NewsTypeAdapter mAdapter;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu, container, false);
        ll_menu = (LinearLayout) view.findViewById(R.id.ll_menu);
        tv_login = (TextView) view.findViewById(R.id.tv_login);
        tv_backup = (TextView) view.findViewById(R.id.tv_backup);
        tv_download = (TextView) view.findViewById(R.id.tv_download);
        tv_download.setOnClickListener(this);
        tv_main = (TextView) view.findViewById(R.id.tv_main);
        tv_main.setOnClickListener(this);
        lv_item = (ListView) view.findViewById(R.id.lv_item);
        lv_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getFragmentManager()
                        .beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                        .replace(
                                R.id.fl_content,
                                new NewsFragment(items.get(position).getId(),
                                        items.get(position).getTitle()), "news").commit();
                ((MainActivity)mActivity).setCurId(items.get(position).getId());
                ((MainActivity)mActivity).closeMenu();
            }
        });
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        isLight=((MainActivity)mActivity).isLight();
        items=new ArrayList<NewsListItem>();
        if (HttpUtils.isNetworkConnected(mActivity)){
            HttpUtils.get(Constant.THEMES,new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    String json=response.toString();

                }
            });
        }

    }

    @Override
    public void onClick(View v) {

    }
}
