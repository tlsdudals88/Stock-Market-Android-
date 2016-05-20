package csci551.hw9;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    public static String news_json_string;
    public static String query; // autocomplete query

    public static ArrayList<String> symbol = new ArrayList<String>();
    public static ArrayList<String> name = new ArrayList<String>();
    public static ArrayList<String> stock_price = new ArrayList<String>();
    public static ArrayList<String> change_percentage = new ArrayList<String>();
    public static ArrayList<String> market_cap = new ArrayList<String>();

    public static ArrayList<HashMap<String, String>> mlist = new ArrayList<HashMap<String, String>>();
    public static HashMap<String, String> map = new HashMap<String, String> ();

    Activity context = this;

    public boolean text_search = true;
    public String list_search = "";

    public Timer timer;

    // Get SharedPreference data
    public Preferences pref = new Preferences();

    void settingPref() {

        // pref.clearSharedPreference(context);

        String s;
        String n;
        String st;
        String c;
        String m;

        if(pref.getValue(context, "symbol")==null // empty favorite list
                || pref.getValue(context, "name")==null
                || pref.getValue(context, "stock_price")==null
                || pref.getValue(context, "change_percentage")==null
                || pref.getValue(context, "market_cap")==null){


            symbol = new ArrayList<String>();
            name = new ArrayList<String>();
            stock_price = new ArrayList<String>();
            change_percentage = new ArrayList<String>();
            market_cap = new ArrayList<String>();

        }
        else if(pref.getValue(context, "symbol") == "") { // empty favorite list

            symbol = new ArrayList<String>();
            name = new ArrayList<String>();
            stock_price = new ArrayList<String>();
            change_percentage = new ArrayList<String>();
            market_cap = new ArrayList<String>();
        }

        else { // if there are items in the favorite list
               // Get the data from the SharedPreferences
            s = pref.getValue(context, "symbol");
            n = pref.getValue(context, "name");
            st = pref.getValue(context, "stock_price");
            c = pref.getValue(context, "change_percentage");
            m = pref.getValue(context, "market_cap");

            // Save the SharedPreferences into Global ArrayList
            symbol = new ArrayList<String>(Arrays.asList(s.split(",&")));
            name = new ArrayList<String>(Arrays.asList(n.split(",&")));
            stock_price = new ArrayList<String>(Arrays.asList(st.split(",&")));
            change_percentage = new ArrayList<String>(Arrays.asList(c.split(",&")));
            market_cap = new ArrayList<String>(Arrays.asList(m.split(",&")));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("resume","resume");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        Log.e("stop", "stop");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        Log.e("die", "die");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // make actionbar
        ActionBar actionBar = getSupportActionBar(); // manipulate actionbar
        actionBar.setIcon(R.drawable.icon); // put the icon into the actionbar

        settingPref(); // setting preferences

        final FavoriteListViewAdapter mAdapter;
        final DynamicListView lv;
        lv = (DynamicListView) findViewById(R.id.list_favorites);

        // Refresh Ajax (1.app restart, 2.refresh button, 3.auto_refresh button)
        class RefreshAjax extends AsyncTask<String, Void, String> {

            void updateListView(String json_string, String num){
                // only update stock_price, change_percentage, market_cap

                try {
                    JSONObject json_object = new JSONObject(json_string); // String to Object

                    // Save updated value into the Global ArrayList
                    stock_price.set(Integer.parseInt(num), json_object.getString("LastPrice"));
                    change_percentage.set(Integer.parseInt(num), json_object.getString("ChangePercent"));
                    market_cap.set(Integer.parseInt(num),json_object.getString("MarketCap"));

                    // Save updated Global ArrayList into the SharedPreferences
                    Preferences pref = new Preferences();
                    pref.save(context, "stock_price", TextUtils.join(",&", stock_price));
                    pref.save(context, "change_percentage", TextUtils.join(",&", change_percentage));
                    pref.save(context, "market_cap", TextUtils.join(",&", market_cap));
                    Log.e("symbol : ", pref.getValue(context, "symbol"));
                    Log.e("name : ", pref.getValue(context, "name"));
                    Log.e("stock_price : ", pref.getValue(context, "stock_price"));
                    Log.e("change_percentage : ", pref.getValue(context, "change_percentage"));
                    Log.e("market_cap : ", pref.getValue(context, "market_cap"));


                    // Show updated value in the listView(table)
                    View view = lv.getChildAt(Integer.parseInt(num));
                    TextView tv_price = (TextView) view.findViewById(R.id.stock_price);
                    TextView tv_percent = (TextView) view.findViewById(R.id.change_percentage);
                    TextView tv_market = (TextView) view.findViewById(R.id.market_cap);

                    tv_price.setText("$ "+json_object.getString("LastPrice"));

                    if(json_object.getString("ChangePercent").charAt(0) == '-') {
                        tv_percent.setText(json_object.getString("ChangePercent") + "%");
                        tv_percent.setBackgroundColor(0xfff00000);
                    }
                    else {
                        tv_percent.setText("+" + json_object.getString("ChangePercent") + "%");
                        tv_percent.setBackgroundColor(0xf00ff000);
                    }
                    tv_market.setText("Market Cap: " + json_object.getString("MarketCap"));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL("http://sonorous-treat-124311.appspot.com/hw9/?symbol="+params[0]); // stock url
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // connection

                    conn.setRequestMethod("GET"); // Get method

                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    // TODO: not found check
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        builder.append(str + "\n");
                    }

                    return params[1]+builder.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String stock_json_string) {
                // validation check
                try {
                    String num = stock_json_string.substring(0, 1); // index for the favorite list row
                    stock_json_string = stock_json_string.substring(1); // json value

                    Log.e("json_refresh",stock_json_string);

                    JSONObject stock_json_object = new JSONObject(stock_json_string); // String to Object

                    // validation error
                    if(stock_json_object.getString("Timestamp").equals("31 December 1969 16:00:00"))
                    {
                        // TextView tv = (TextView) findViewById(R.id.validation);
                        // tv.setText("Validation Error");
                        return;
                    }
                    updateListView(stock_json_string, num);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        // 1. refresh all data when restarting.
        for(int i=0; i<symbol.size(); i++)
            new RefreshAjax().execute(symbol.get(i),Integer.toString(i)); // execute refresh ajax

        // 2. Refresh Button
        ImageButton ib = (ImageButton)findViewById(R.id.refresh);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < symbol.size(); i++)
                        new RefreshAjax().execute(symbol.get(i), Integer.toString(i)); // execute refresh ajax
                }
            });

        // 3. Auto Refresh Button (every 10 seconds)
        Switch sw = (Switch) findViewById(R.id.auto_refresh);
        if(timer != null){
            timer.cancel();
        }
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                for (int i = 0; i < symbol.size(); i++)
                                    new RefreshAjax().execute(symbol.get(i), Integer.toString(i)); // execute refresh ajax
                            }
                        }, 0, 10000);
                    } else {
                        if (timer!=null){
                            timer.cancel();
                            timer = null;
                        }
                    }
                }
            });
        //check the current state before we display the screen
        if(sw.isChecked()) {
            // Toast.makeText(getApplicationContext(),"checked before",Toast.LENGTH_LONG).show();
        }
        else {
            // Toast.makeText(getApplicationContext(),"not checked before",Toast.LENGTH_LONG).show();
        }


        // Implement the swipeListView
        mAdapter = new FavoriteListViewAdapter(this, symbol, name, stock_price, change_percentage, market_cap);

        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(mAdapter);
        animationAdapter.setAbsListView(lv);

        mAdapter.notifyDataSetChanged();
        lv.setAdapter(animationAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() { // onClick Listener
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                text_search = false; // disable text_search
                list_search = symbol.get(position).toString(); // enable list_search
                getQuote(view);
                // if users click the row, call the getQuote
            }
        });

        lv.enableSwipeToDismiss( // Swipe Listener
                new OnDismissCallback() {
                    // if user swipe the row in the favorite list
                    @Override
                    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
                        for (final int position : reverseSortedPositions) {

                            // make AlertDialog (okay, cancel)
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setCancelable(true);
                            builder.setTitle("Want to delete " + name.get(position).toString() + " from favorites?");
                            builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {

                                }

                            });

                            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                // if user put the ok button, then delete the row in the favorite list

                                public void onClick(DialogInterface dialog, int which) {

                                    // Log.e("index", Integer.toString(MainActivity.symbol.indexOf(symbol.get(position).toString())));
                                    int index = MainActivity.symbol.indexOf(symbol.get(position).toString());

                                    // Delete the item in the Global ArrayList
                                    MainActivity.symbol.remove(index);
                                    MainActivity.name.remove(index);
                                    MainActivity.stock_price.remove(index);
                                    MainActivity.change_percentage.remove(index);
                                    MainActivity.market_cap.remove(index);

                                    // Delete the item in the SharedPreferences
                                    Preferences pref = new Preferences();
                                    pref.save(context, "symbol", TextUtils.join(",&", MainActivity.symbol));
                                    pref.save(context, "name", TextUtils.join(",&", MainActivity.name));
                                    pref.save(context, "stock_price", TextUtils.join(",&", MainActivity.stock_price));
                                    pref.save(context, "change_percentage", TextUtils.join(",&", MainActivity.change_percentage));
                                    pref.save(context, "market_cap", TextUtils.join(",&", MainActivity.market_cap));
                                    Log.e("symbol : ", pref.getValue(context, "symbol"));
                                    Log.e("name : ", pref.getValue(context, "name"));
                                    Log.e("stock_price : ", pref.getValue(context, "stock_price"));
                                    Log.e("change_percentage : ", pref.getValue(context, "change_percentage"));
                                    Log.e("market_cap : ", pref.getValue(context, "market_cap"));

                                    Log.e("list", MainActivity.symbol.toString());

                                    mAdapter.notifyDataSetChanged();
                                }

                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
        );

        // AutoComplete
        class AutoComplete extends AsyncTask<Void, Void, String> { // Async function for AutoComplete
            @Override
            protected String doInBackground(Void... params) {

                try {
                    URL url = new URL("http://sonorous-treat-124311.appspot.com/hw9/?input="+query); // stock url
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // connection

                    conn.setRequestMethod("GET"); // Get method

                    int responseCode = conn.getResponseCode(); // get response code
                    Log.e("response_stock", Integer.toString(responseCode));

                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");


                    // TODO: not found check
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        builder.append(str + "\n");
                    }

                    return builder.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String lookup) {
                try {
                    JSONArray jsonArray = new JSONArray(lookup);

                    mlist = new ArrayList<HashMap<String, String>>();
                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String symbol = obj.getString("Symbol");
                        String name = obj.getString("Name");
                        String exchange = obj.getString("Exchange");
                        String name_exchange = name+" ( "+exchange+" ) ";

                        map = new HashMap<String, String> ();
                        map.put("symbol",symbol);
                        map.put("name",name_exchange);
                        mlist.add(map);

                        AutoCompleteTextView autoTextView = (AutoCompleteTextView) findViewById(R.id.input_text);
                        SimpleAdapter simpleAdapter = new SimpleAdapter(context, mlist, android.R.layout.simple_list_item_2,
                                new String[]{"symbol", "name"}, new int[]{android.R.id.text1, android.R.id.text2});
                        autoTextView.setAdapter(simpleAdapter);
                        simpleAdapter.notifyDataSetChanged();

                        Log.e("symbol", obj.getString("Symbol"));
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        final AutoCompleteTextView autoTextView = (AutoCompleteTextView) findViewById(R.id.input_text);
        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, mlist, android.R.layout.simple_list_item_2,
                new String[]{"symbol", "name"}, new int[]{android.R.id.text1, android.R.id.text2});
        autoTextView.setAdapter(simpleAdapter);
        autoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View arg1, int index, long arg3) {

                HashMap<String, String> map = (HashMap<String, String>) av.getItemAtPosition(index);

                autoTextView.setText(map.get("symbol")); // click the item and set the symbol text(AAPL)
            }
        });

        autoTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        autoTextView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // no need to do anything
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (((AutoCompleteTextView) autoTextView).isPerformingCompletion()) {
                    return;
                }

                if (charSequence.length() < 3) { // minimum 3
                    return;
                }

                query = charSequence.toString();
                Log.e("query", query);
                simpleAdapter.notifyDataSetChanged();

                new AutoComplete().execute();
            }
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    // get quote button click
    public void getQuote(View view) {

        final String input_text;

        if(text_search == true) { // text search
            EditText editText = (EditText) findViewById(R.id.input_text);
            input_text = editText.getText().toString();
        }
        else { // list search
            input_text = list_search;
        }

        class NewsFeed extends AsyncTask<Void, Void, String> { // Async function for news feed

            @Override
            protected String doInBackground(Void... params) {

                try {
                    URL url = new URL("http://sonorous-treat-124311.appspot.com/hw9/?news="+input_text); // news url
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // connection

                    conn.setRequestMethod("GET"); // Get method

                    int responseCode = conn.getResponseCode(); // get response code
                    Log.e("response_news", Integer.toString(responseCode));

                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    // TODO: not found check
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        builder.append(str + "\n");
                    }

                    return builder.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String json_string) {
                MainActivity.news_json_string = json_string;
            }
        }

    class StockDetail extends AsyncTask<Void, Void, String> { // Async function for stock market
            @Override
            protected String doInBackground(Void... params) {

                try {
                    URL url = new URL("http://sonorous-treat-124311.appspot.com/hw9/?symbol="+input_text); // stock url
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // connection

                    conn.setRequestMethod("GET"); // Get method

                    int responseCode = conn.getResponseCode(); // get response code
                    Log.e("response_stock", Integer.toString(responseCode));

                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    // TODO: not found check
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder builder = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        builder.append(str + "\n");
                    }

                    Log.e("response_stock", builder.toString());
                    return builder.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String stock_json_string) {
                // validation check
                try {
                    JSONObject stock_json_object = new JSONObject(stock_json_string); // String to Object

                    // empty error
                    AutoCompleteTextView atv1 = (AutoCompleteTextView) findViewById(R.id.input_text);
                    if((atv1.getText().toString().equals("")) && text_search == true)
                    {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                        builder2.setCancelable(true);
                        builder2.setTitle("Please enter a Stock Name/Symbol");
                        builder2.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        });

                        AlertDialog alert = builder2.create();
                        alert.show();

                        TextView tv = (TextView) findViewById(R.id.validation);
                        tv.setText("Please enter a Stock Name/Symbol");
                        return;
                    }

                    // validation error
                    if(stock_json_object.getString("Timestamp").equals("31 December 1969 16:00:00"))
                    {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setCancelable(true);
                        builder1.setTitle("Invalid Symbol");
                        builder1.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        });

                        AlertDialog alert = builder1.create();
                        alert.show();

                        TextView tv = (TextView) findViewById(R.id.validation);
                        tv.setText("Invalid Symbol");
                        return;
                    }

                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Server is not loaded, Please try again or check your internet connection", Toast.LENGTH_LONG).show();
                    return;
                }

                // TODO: not found check
                Intent i = new Intent(MainActivity.this, ResultActivity.class);
                i.putExtra("stock_json_string", stock_json_string); // stock json string
                i.putExtra("input_text",input_text); // input text symbol
                i.putExtra("news_json_string",MainActivity.news_json_string); // news json string

                startActivityForResult(i,0);
            }
        }

        new NewsFeed().execute(); // execute news feed
        new StockDetail().execute(); // execute stock detailk detail
    }

    // clear button click
    public void clear(View view) {
        // clear validation
        TextView tv = (TextView) findViewById(R.id.validation);
        tv.setText("");

        // clear AutoCompleteTextView
        AutoCompleteTextView atv = (AutoCompleteTextView) findViewById(R.id.input_text);
        atv.setText("");

    }


}
