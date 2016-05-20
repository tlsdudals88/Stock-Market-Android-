package csci551.hw9;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import uk.co.senab.photoview.PhotoViewAttacher;

public class TabFragment1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("fragment", "fragment1");

        View fragment1 = inflater.inflate(R.layout.content_tab_fragment1, container, false); // loading xml UI

        Intent intent = getActivity().getIntent(); // get intent in Fragment

        String input_text = intent.getStringExtra("input_text"); // Symbol
        String stock_json_string = intent.getStringExtra("stock_json_string"); // JSON_String

        try {
            JSONObject stock_json_object = new JSONObject(stock_json_string); // String to Object
            final String symbol = stock_json_object.getString("Symbol");
            String name = stock_json_object.getString("Name");
            String last_price = stock_json_object.getString("LastPrice");
            String change = stock_json_object.getString("Change");
            String change_percent = stock_json_object.getString("ChangePercent");
            String timestamp = stock_json_object.getString("Timestamp");
            String market_cap = stock_json_object.getString("MarketCap");
            String volume = stock_json_object.getString("Volume");
            String change_YTD = stock_json_object.getString("ChangeYTD");
            String change_percent_YTD = stock_json_object.getString("ChangePercentYTD");
            String high = stock_json_object.getString("High");
            String low = stock_json_object.getString("Low");
            String open = stock_json_object.getString("Open");

            TextView title = (TextView) fragment1.findViewById(R.id.stock_detail);

            TextView textView1 = (TextView) fragment1.findViewById(R.id.name);
            TextView textView2 = (TextView) fragment1.findViewById(R.id.symbol);
            TextView textView3 = (TextView) fragment1.findViewById(R.id.lastprice);
            TextView textView4 = (TextView) fragment1.findViewById(R.id.change);
            TextView textView5 = (TextView) fragment1.findViewById(R.id.timestamp);
            TextView textView6 = (TextView) fragment1.findViewById(R.id.marketcap);
            TextView textView7 = (TextView) fragment1.findViewById(R.id.volume);
            TextView textView8 = (TextView) fragment1.findViewById(R.id.change_YTD);
            TextView textView9 = (TextView) fragment1.findViewById(R.id.high);
            TextView textView10 = (TextView) fragment1.findViewById(R.id.low);
            TextView textView11 = (TextView) fragment1.findViewById(R.id.open);

            TextView today = (TextView) fragment1.findViewById(R.id.today);


            class DownloadImageTask extends AsyncTask<String, Void, Bitmap> { // Async function for yahoo image
                ImageView bmImage;

                public DownloadImageTask(ImageView bmImage) {
                    this.bmImage = bmImage;
                }

                protected Bitmap doInBackground(String... urls) {
                    String urldisplay = urls[0];
                    Bitmap mIcon11 = null;
                    try {
                        InputStream in = new java.net.URL(urldisplay).openStream();
                        mIcon11 = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }
                    return mIcon11;
                }

                protected void onPostExecute(Bitmap result) {
                    bmImage.setImageBitmap(result);
                }
            }

            ImageView img = (ImageView) fragment1.findViewById((R.id.imageView1));
            new DownloadImageTask(img) // download image from url
                    .execute("http://chart.finance.yahoo.com/t?s=" + symbol + "&lang=en-US");
                    // .execute("http://chart.finance.yahoo.com/t?s=" +symbol+ "&lang=en-US&width=400&height=300");


            img.setOnClickListener(new View.OnClickListener() { // img onclick listner
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setLayout(1000, 800); // dialog layout
                    LayoutInflater inflater = getActivity().getLayoutInflater(); // load other xml layout
                    View dialogLayout = inflater.inflate(R.layout.dialog_image, null); // load other xml layout
                    ImageView image = (ImageView) dialogLayout.findViewById((R.id.dialog_img));
                    new DownloadImageTask(image)
                            .execute("http://chart.finance.yahoo.com/t?s=" + symbol + "&lang=en-US&width=1000&height=800");

                    PhotoViewAttacher mAttacher = new PhotoViewAttacher(image); // zoom api
                    mAttacher.update(); // whenever zoom or scroll, update it(redraw)

                    dialog.setView(dialogLayout); // put dialogLayout into the AlertDialog
                    dialog.show(); // show the dialog

                }
            });

            title.setText("\nStock Detail\n");
            textView1.setText(name);
            textView2.setText(symbol);
            textView3.setText(last_price);

            if(change_percent.charAt(0) == '-') {
                change_percent = "("+change_percent+"%)";

                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(change + "" + change_percent)
                        .append("  ", new ImageSpan(getActivity(), R.drawable.down), 0);
                textView4.setText(builder);
            }
            else {
                change_percent = "(+"+change_percent+"%)";

                SpannableStringBuilder builder1 = new SpannableStringBuilder();
                builder1.append(change + "" + change_percent)
                        .append("  ", new ImageSpan(getActivity(), R.drawable.up), 0);
                textView4.setText(builder1);
            }

            textView5.setText(timestamp);
            textView6.setText(market_cap);
            textView7.setText(volume);

            if(change_percent_YTD.charAt(0) == '-') {
                change_percent_YTD = "("+change_percent_YTD+"%)";

                SpannableStringBuilder builder2 = new SpannableStringBuilder();
                builder2.append(change_YTD + "" + change_percent_YTD)
                        .append("  ", new ImageSpan(getActivity(), R.drawable.down), 0);
                textView8.setText(builder2);
            }
            else {
                change_percent_YTD = "(+"+change_percent_YTD+"%)";

                SpannableStringBuilder builder3 = new SpannableStringBuilder();
                builder3.append(change_YTD + "" + change_percent_YTD)
                        .append("  ", new ImageSpan(getActivity(), R.drawable.up), 0);
                textView8.setText(builder3);
            }

            textView9.setText(high);
            textView10.setText(low);
            textView11.setText(open);
            today.setText("\nToday\'s Stock Activity\n");


        } catch(JSONException e){
            e.printStackTrace();
        }

        return fragment1;
    }
}
