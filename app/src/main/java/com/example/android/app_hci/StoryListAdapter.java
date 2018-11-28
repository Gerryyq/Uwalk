package com.example.android.app_hci;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//Adapter to grab content of stories
public class StoryListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Story> stories;
    private ArrayList<Story> arraylist;


    public StoryListAdapter(Context _context, List<Story> stories) {
        this.context = _context;
        this.stories = stories;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(stories);
    }

    @Override
    public int getCount() {
        return stories.size();
    }

    @Override
    public Object getItem(int position) {
        return stories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder{
        TextView title,description;
        ImageView image;
    }

    //Retrieving layout from list_stories and inputing value to populate the listview
    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_stories,null);

            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.description = (TextView) convertView.findViewById(R.id.tv_description);
            holder.image = (ImageView)  convertView.findViewById(R.id.tv_imageBackground);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (stories.get(position) != null) {
            String text_title = stories.get(position).getTitle();
            String text_description = stories.get(position).getDescription();
            int image = stories.get(position).getImage();
            holder.title.setText(text_title);
            holder.description.setText(text_description);
            holder.image.setImageResource(image);
            holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        return convertView;
    }

    // Filter Class for search bar, search base on title
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        stories.clear();
        if (charText.length() == 0) {
            stories.addAll(arraylist);
        } else {
            for (Story c : arraylist) {
                if (c.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    stories.add(c);
                }
            }
        }
        notifyDataSetChanged();
    }




}
