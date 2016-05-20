package csci551.hw9;

/**
 * Created by Youngmin on 2016. 4. 7..
 */

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter
{
    Activity context;

    ArrayList<String> title;
    ArrayList<String> description;
    ArrayList<String> publisher;
    ArrayList<String> published_date;
    ArrayList<String> url;

    // String title[];
    // String description[];

    public ListViewAdapter(Activity context, ArrayList<String> title,
                           ArrayList<String> description, ArrayList<String> publisher,
                           ArrayList<String> published_date, ArrayList<String> url ) {
        super();
        this.context = context;
        this.title = title;
        this.description = description;
        this.publisher = publisher;
        this.published_date = published_date;
        this.url = url;
    }

    public boolean hasStableIds() {
        return true;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        //return title.length;
        return title.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class ViewHolder {
        TextView txtViewTitle;
        TextView txtViewDescription;
        TextView txtViewPublisher;
        TextView txtViewPublished_date;
    }




    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.custom_list_news, null);
            holder = new ViewHolder();
            holder.txtViewTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtViewDescription = (TextView) convertView.findViewById(R.id.description);
            holder.txtViewPublisher = (TextView) convertView.findViewById(R.id.publisher);
            holder.txtViewPublished_date = (TextView) convertView.findViewById(R.id.published_date);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        // Log.e("url", url.get(position));



        holder.txtViewTitle.setPaintFlags(holder.txtViewTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // holder.txtViewTitle.setText(title.get(position));

        holder.txtViewTitle.setMovementMethod(LinkMovementMethod.getInstance());
        holder.txtViewTitle.setText(Html.fromHtml("<a href=\""
                + url.get(position) + "\">" + title.get(position) + "</a>"));
        holder.txtViewTitle.setLinkTextColor(Color.BLACK);

        holder.txtViewDescription.setText(description.get(position));
        holder.txtViewPublisher.setText("Publisher : "+publisher.get(position));
        holder.txtViewPublished_date.setText("Date : "+published_date.get(position));

        return convertView;
    }

}
