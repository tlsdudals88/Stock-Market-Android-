package csci551.hw9;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class TabFragment3 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("fragment", "fragment3");

        View fragment3 = inflater.inflate(R.layout.content_tab_fragment3, container, false);

        TextView title1 = (TextView) fragment3.findViewById(R.id.news_feed);
        title1.setText("\nNews Feed\n");

        Intent intent = getActivity().getIntent();
        String input_text = intent.getStringExtra("input_text");
        String news_json_string = intent.getStringExtra("news_json_string"); // JSON_String

        try {
            
            ArrayList<String> title = new ArrayList<String>();
            ArrayList<String> description = new ArrayList<String>();
            ArrayList<String> publisher = new ArrayList<String>();
            ArrayList<String> published_date = new ArrayList<String>();
            ArrayList<String> url = new ArrayList<String>();

            JSONObject news_json_object = new JSONObject(news_json_string); // String to Object
            JSONObject inner_d_obj = news_json_object.getJSONObject("d"); // inside d object
            JSONArray inner_results_array = inner_d_obj.getJSONArray("results"); // inside results array


            for (int i=0; i<inner_results_array.length(); i++) {
                // inside metadata object (multiple metadata object)
                JSONObject inner_metadata_obj = inner_results_array.getJSONObject(i);

                // Log.e("news",inner_metadata_obj.toString());

                String news_url = new String(inner_metadata_obj.getString("Url").getBytes("ISO-8859-1"),"UTF-8");
                String news_title = new String(inner_metadata_obj.getString("Title").getBytes("ISO-8859-1"),"UTF-8");

                Log.e("title",news_title);

                String news_description = new String(inner_metadata_obj.getString("Description").getBytes("ISO-8859-1"),"UTF-8");
                String news_publisher = new String(inner_metadata_obj.getString("Source").getBytes("ISO-8859-1"),"UTF-8");
                String news_published_date = new String(inner_metadata_obj.getString("Date").getBytes("ISO-8859-1"),"UTF-8");

                title.add(news_title);
                description.add(news_description);
                publisher.add(news_publisher);
                published_date.add(news_published_date);
                url.add(news_url);

            }

            ListView lv = (ListView) fragment3.findViewById(R.id.list);
            ListViewAdapter lvAdapter
                    = new ListViewAdapter(getActivity(), title, description, publisher, published_date, url);

            lv.setAdapter(lvAdapter);


        } catch(JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return fragment3;
    }




}