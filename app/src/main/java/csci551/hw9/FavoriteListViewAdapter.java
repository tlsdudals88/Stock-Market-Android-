package csci551.hw9;

/**
 * Created by Youngmin on 2016. 4. 7..
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FavoriteListViewAdapter extends BaseAdapter
{
    Activity context;

    ArrayList<String> symbol;
    ArrayList<String> name;
    ArrayList<String> stock_price;
    ArrayList<String> change_percentage;
    ArrayList<String> market_cap;


    // String title[];
    // String description[];


    public FavoriteListViewAdapter(Activity context, ArrayList<String> symbol,
                                   ArrayList<String> name,
                                   ArrayList<String> stock_price,
                                   ArrayList<String> change_percentage,
                                   ArrayList<String> market_cap) {
        super();
        this.context = context;
        this.symbol = symbol;
        this.name = name;
        this.stock_price = stock_price;
        this.change_percentage = change_percentage;
        this.market_cap = market_cap;

    }

    public boolean hasStableIds() {
        return true;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        //return title.length;
        return symbol.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        //return null;
        return symbol.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class ViewHolder {
        TextView txtViewSymbol;
        TextView txtViewName;
        TextView txtViewStockPrice;
        TextView txtViewChangePercent;
        TextView txtViewMarketCap;
    }


    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.custom_list_favorite, null);
            holder = new ViewHolder();
            holder.txtViewSymbol = (TextView) convertView.findViewById(R.id.symbol);
            holder.txtViewName = (TextView) convertView.findViewById(R.id.name);
            holder.txtViewStockPrice = (TextView) convertView.findViewById(R.id.stock_price);
            holder.txtViewChangePercent = (TextView) convertView.findViewById(R.id.change_percentage);
            holder.txtViewMarketCap = (TextView) convertView.findViewById(R.id.market_cap);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }



        holder.txtViewSymbol.setText(symbol.get(position));
        holder.txtViewName.setText(name.get(position));
        holder.txtViewStockPrice.setText("$ "+stock_price.get(position));

        if(change_percentage.get(position).charAt(0) == '-') {
            holder.txtViewChangePercent.setText(change_percentage.get(position) + "%");
            holder.txtViewChangePercent.setBackgroundColor(0xfff00000);
        }
        else {
            holder.txtViewChangePercent.setText("+" + change_percentage.get(position) + "%");
            holder.txtViewChangePercent.setBackgroundColor(0xf00ff000);
        }


        holder.txtViewMarketCap.setText("Market Cap: "+market_cap.get(position));

        return convertView;
    }

}
