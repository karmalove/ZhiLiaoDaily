package kevin.app.zhiliaodaily.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Handler;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;


import kevin.app.zhiliaodaily.R;
import kevin.app.zhiliaodaily.model.Latest;


/**
 * Created by Kevin on 2015/11/19.
 * 图片轮播控件
 */
public class Kanner extends FrameLayout implements View.OnClickListener {
    private List<Latest.TopStoriesEnity> topStoriesEnities;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private List<View> views;
    private Context context;
    private ViewPager vp;
    private boolean isAutoPlay;
    private int currenItem;
    private int delayTime;
    private LinearLayout ll_dot;
    private List<ImageView> iv_docts;
    private Handler handler = new Handler();
    private OnItemClickListener mItemClickListener;

    public Kanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        this.context = context;
        this.topStoriesEnities = new ArrayList<>();
        initView();
    }

    private void initView() {
        views = new ArrayList<View>();
        iv_docts = new ArrayList<ImageView>();
        delayTime = 2000;
    }

    public Kanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Kanner(Context context) {
        this(context, null);
    }

    public void setTopEnities(List<Latest.TopStoriesEnity> topEntities) {
        this.topStoriesEnities = topEntities;
        reset();
    }

    public void reset() {
        views.clear();
        initUI();
    }

    private void initUI() {
        View view = LayoutInflater.from(context).inflate(R.layout.kanner_layout, this, true);
        vp = (ViewPager) view.findViewById(R.id.vp);
        ll_dot = (LinearLayout) view.findViewById(R.id.ll_dot);
        ll_dot.removeAllViews();

        int len = topStoriesEnities.size();
        for (int i = 0; i < len; i++) {
            View fm = LayoutInflater.from(context).inflate(R.layout.kanner_content_layout, null);
            ImageView iv = (ImageView) fm.findViewById(R.id.iv_title);
            TextView tv_title = (TextView) fm.findViewById(R.id.tv_title);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (i == 0) {
                mImageLoader.displayImage(topStoriesEnities.get(len - 1).getImage(), iv, options);
                tv_title.setText(topStoriesEnities.get(0).getTitle());
            }
            fm.setOnClickListener(this);
            views.add(fm);
        }
        vp.setAdapter(new MyPagerAdapter());
        vp.setFocusable(true);
        vp.setCurrentItem(1);
        currenItem = 1;
        vp.addOnPageChangeListener(new MyOnPageChangeListener());
        startPlay();
    }

    private void startPlay() {
        isAutoPlay = true;
        handler.postDelayed(task, 3000);
    }

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (isAutoPlay) {
                currenItem = currenItem % (topStoriesEnities.size() + 1) + 1;
                if (currenItem == 1) {
                    vp.setCurrentItem(currenItem, false);
                    handler.post(task);
                } else {
                    vp.setCurrentItem(currenItem);
                    handler.postDelayed(task, 5000);
                }
            } else {
                handler.postDelayed(task, 5000);
            }
        }
    };

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return false;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int arg0, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i=0;i<iv_docts.size();i++){
                if (i==arg0-1){
                    iv_docts.get(i).setImageResource(R.drawable.dot_focus);
                }else {
                    iv_docts.get(i).setImageResource(R.drawable.dot_blur);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:
                    isAutoPlay=false;
                    break;
                case 2:
                    isAutoPlay=true;
                    break;
                case 0:
                    if (vp.getCurrentItem()==0){
                        vp.setCurrentItem(topStoriesEnities.size(),false);
                    }else if (vp.getCurrentItem()==topStoriesEnities.size()+1){
                        vp.setCurrentItem(1,false);
                    }
                    currenItem=vp.getCurrentItem();
                    isAutoPlay=true;
                    break;

            }
        }
    }
    public void setOnItemClickListener(OnItemClickListener mItemClickListener){
        this.mItemClickListener=mItemClickListener;
    }
    public interface OnItemClickListener{
        public void click(View v,Latest.TopStoriesEnity enity);
    }
    @Override
    public void onClick(View v) {
        if (mItemClickListener!=null){
            Latest.TopStoriesEnity enity=topStoriesEnities.get(vp.getCurrentItem()-1);
            mItemClickListener.click(v,enity);
        }
    }
}
