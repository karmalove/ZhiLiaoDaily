package kevin.app.zhiliaodaily.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import kevin.app.zhiliaodaily.R;
import kevin.app.zhiliaodaily.activity.MainActivity;
import kevin.app.zhiliaodaily.model.StoriesEntity;
import kevin.app.zhiliaodaily.util.PreUtils;

/**
 * Created by Kevin on 2015/11/18.
 */
public class NewsItemAdapter extends BaseAdapter {
    private List<StoriesEntity> entities;
    private Context context;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private boolean isLight;
    public NewsItemAdapter(Context context,List<StoriesEntity> items){
        this.context=context;
        entities=items;
        isLight=((MainActivity)context).isLight();
        mImageLoader=ImageLoader.getInstance();
        options=new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public int getCount() {
        return entities.size();
    }

    @Override
    public Object getItem(int position) {
        return entities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.news_item,parent,false);
            viewHolder.tv_title=(TextView)convertView.findViewById(R.id.tv_title);
            viewHolder.iv_title=(ImageView)convertView.findViewById(R.id.iv_title);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        String readSeq= PreUtils.getStringFromDefault(context,"read","");
        if (readSeq.contains(entities.get(position).getId()+"")){
            viewHolder.tv_title.setTextColor(context.getResources().getColor(R.color.clicked_tv_textcolor));
        }else {
            viewHolder.tv_title.setTextColor(context.getResources().getColor(isLight?android.R.color.black:
            android.R.color.white));
        }

        ((LinearLayout)viewHolder.iv_title.getParent().getParent().getParent()).setBackgroundColor(context.getResources().getColor(
                isLight?R.color.light_news_item:R.color.dark_news_item));
        ( (FrameLayout)viewHolder.tv_title.getParent().getParent()).setBackgroundResource(isLight ?
                R.drawable.item_background_selector_light : R.drawable.item_background_selector_dark);
        StoriesEntity entity=entities.get(position);
        viewHolder.tv_title.setText(entity.getTitle());
        if (entity.getImages()!=null){
            viewHolder.tv_title.setVisibility(View.VISIBLE);
            mImageLoader.displayImage(entity.getImages().get(0),viewHolder.iv_title,options);
        }else {
            viewHolder.iv_title.setVisibility(View.GONE);
        }
        return convertView;
    }
    public void updateTheme(){
        isLight=((MainActivity)context).isLight();
        notifyDataSetChanged();
    }
    public static class ViewHolder{
        TextView tv_title;
        ImageView iv_title;
    }
}
