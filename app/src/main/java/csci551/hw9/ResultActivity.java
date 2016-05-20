package csci551.hw9;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class ResultActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    Boolean bool = false;
    String name;
    String symbol;
    String stock_price;
    String change_percentage;
    String market_cap;

    Activity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
               // Log.e("result",result.getPostId().toString());
                Toast.makeText(getApplicationContext(), "Post Successfully", Toast.LENGTH_SHORT).show();

                /*
                if(result.getPostId() != null)
                    Toast.makeText(getApplicationContext(), "Post", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                */
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
            }
        });




        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    this.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("asdf","asdf");

        } catch (NoSuchAlgorithmException e) {
            Log.e("asdf","asdf");
        }



        Intent intent = getIntent();
        String stock_json_string = intent.getStringExtra("stock_json_string");
        try {
            JSONObject stock_json_object = new JSONObject(stock_json_string); // String to Object
            name = stock_json_object.getString("Name");
            symbol = stock_json_object.getString("Symbol");
            stock_price = stock_json_object.getString("LastPrice");
            change_percentage = stock_json_object.getString("ChangePercent");
            market_cap = stock_json_object.getString("MarketCap");

            setTitle(name);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button
        ActionBar actionBar = getSupportActionBar(); // manipulate actionbar
        // actionBar.setIcon(R.drawable.icon); // put the icon into the actionbar


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout); // get a tableLayout id
        tabLayout.addTab(tabLayout.newTab().setText("CURRENT")); // add tab
        tabLayout.addTab(tabLayout.newTab().setText("HISTORICAL"));
        tabLayout.addTab(tabLayout.newTab().setText("NEWS"));
        tabLayout.setTabMode(TabLayout.MODE_FIXED); // not scrolled
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL); // fill the tab


        final CustomViewPager viewPager = (CustomViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.e("position", Integer.toString(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // viewPager.setCurrentItem(tab.getPosition());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        //  preparation code here

        MenuItem item = menu.findItem(R.id.star);

        // parsing list view to determine if it's already contained in the table.
        for(int i=0; i<MainActivity.symbol.size(); i++) {

            if(symbol == null)
                return false;

            if(symbol.equals(MainActivity.symbol.get(i))) {
                item.setVisible(false);
                item.setIcon(R.drawable.btn_rating_star_off_pressed);
                item.setVisible(true);
                bool = true;
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.star) {

            if(bool == false) { // turn off -> turn on (add table column)
                item.setVisible(false);
                item.setIcon(R.drawable.btn_rating_star_off_pressed);
                item.setVisible(true);
                bool = true;

                // Inflate Favorite Listview
                // View view = LayoutInflater.from(getApplication()).inflate(R.layout.content_main, null);

                // Save stock information to global ArrayList
                MainActivity.symbol.add(symbol);
                MainActivity.name.add(name);
                MainActivity.stock_price.add(stock_price);
                MainActivity.change_percentage.add(change_percentage);
                MainActivity.market_cap.add(market_cap);

                // Save Global ArrayList into the SharedPreferences
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
            }
            else { // turn on -> turn off (delete table column)
                item.setVisible(false);
                item.setIcon(R.drawable.btn_rating_star_off_normal);
                item.setVisible(true);
                bool = false;

                // Inflate Favorite Listview
                // View view = LayoutInflater.from(getApplication()).inflate(R.layout.content_main, null);

                // Delete stock information in the global ArrayList
                int index = MainActivity.symbol.indexOf(symbol);

                MainActivity.symbol.remove(index);
                MainActivity.name.remove(index);
                MainActivity.stock_price.remove(index);
                MainActivity.change_percentage.remove(index);
                MainActivity.market_cap.remove(index);

                //  Delete stock information in the SharedPreferences
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

            }

        }

        if (item.getItemId() == R.id.facebook) {

            if (ShareDialog.canShow(ShareLinkContent.class)) {

                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("Current Stock Price of " + name + " is " + stock_price)
                        .setImageUrl(Uri.parse("http://chart.finance.yahoo.com/t?s=" + symbol + "&lang=en-US&width=1000&height=1000"))
                        .setContentDescription("Stock Information of " + name)
                        .setContentUrl(Uri.parse("http://finance.yahoo.com/q?s=" + symbol))
                        .build();

                shareDialog.show(linkContent, ShareDialog.Mode.FEED);
                // shareDialog.show(linkContent, ShareDialog.Mode.WEB);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        Log.e("request", Integer.toString(requestCode));
        Log.e("request", Integer.toString(resultCode));
        Log.e("request", data.toString());

    }


}
