package com.example.naren.xkcdapp.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.naren.xkcdapp.R;
import com.example.naren.xkcdapp.activities.AboutActivity;
import com.example.naren.xkcdapp.util.network.VolleySingleton;
import com.example.naren.xkcdapp.model.Comic;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import uk.co.senab.photoview.PhotoViewAttacher;

import static com.example.naren.xkcdapp.model.Comic.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private Comic comic = new Comic();
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonObjectRequest;
    private PhotoViewAttacher mAttacher;
    private TextView comic_title;
    private ImageView comic_img;
    private Button mNextButton, mLoadLatestButton;
    private TextView mAltText;
    private ProgressBar mProgressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_id);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_drawer_id);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("xkcd");

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.open_drawer, R.string.close_drawer);

        mDrawerLayout.setDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
        mNextButton = (Button) findViewById(R.id.next_button);
        mLoadLatestButton = (Button) findViewById(R.id.latest_button);
        mAltText = (TextView) findViewById(R.id.alt_text);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar);

        //Volley library used to get JSON request
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

        comic_title = (TextView) findViewById(R.id.textView_comic_title);
        comic_img = (ImageView) findViewById(R.id.imageView_comic_img);

        getComic();
    }

    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.previous_button:

                mNextButton.setVisibility(View.VISIBLE);
                mLoadLatestButton.setVisibility(View.VISIBLE);


                if (mAltText.getVisibility() == View.VISIBLE) {

                    mAltText.setVisibility(View.GONE);
                    getPreviousComic(comic.getNum() - 1);


                } else {

                    getPreviousComic(comic.getNum() - 1);

                }

                break;

            case R.id.latest_button:

                mNextButton.setVisibility(View.GONE);
                mLoadLatestButton.setVisibility(View.GONE);


                if (mAltText.getVisibility() == View.VISIBLE) {

                    mAltText.setVisibility(View.GONE);
                    getComic();

                } else {

                    getComic();

                }

                break;

            case R.id.next_button:

                mNextButton.setVisibility(View.VISIBLE);
                mLoadLatestButton.setVisibility(View.VISIBLE);


                if (mAltText.getVisibility() == View.VISIBLE) {

                    mAltText.setVisibility(View.GONE);
                    getNextComic(comic.getNum() + 1);


                } else {

                    getNextComic(comic.getNum() + 1);

                }

                break;

            case R.id.fab:

                Random random = new Random();

                int rand = random.nextInt(1564 - 1) + 1;

                mNextButton.setVisibility(View.VISIBLE);

                if (mAltText.getVisibility() == View.VISIBLE) {

                    mAltText.setVisibility(View.GONE);
                    getRandomComic(rand);

                } else {

                    getRandomComic(rand);
                }

                Toast.makeText(MainActivity.this, "Random Comic: " + rand, Toast.LENGTH_SHORT).show();

                break;

            default:
                break;
        }
    }

    /*

    Volley library used for getComic() method to get JSON data and is then parsed and placed
    in the viewholder

     */
    private void getComic() {

        /*

        Make a JSON object request with custom url as described in the methods below

        */
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                getLatestComicUrl(), (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //Parsing JSON data and placing the data in the appropriate viewholder
                try {
                    comic.setTitle(response.getString("title"));
                    comic.setImg(response.getString("img"));
                    comic.setNum(response.getInt("num"));
                    comic.setAlt_text(response.getString("alt"));

                    mNextButton.setVisibility(View.GONE);
                    mLoadLatestButton.setVisibility(View.GONE);

                    comic_title.setText(comic.getTitle());

                    /*

                    Picasso library used to load and place the image in the ImageView.
                    Additionally, PhotoView library used to add the appropriate gestures on the
                    comic

                     */
                    Picasso.with(MainActivity.this).load(comic.getImg()).into(comic_img);
                    mAttacher = new PhotoViewAttacher(comic_img);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();

            }
        });

        //JSON object request added to the queue
        mRequestQueue.add(mJsonObjectRequest);
    }

    private void getPreviousComic(int num) {

        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                getPreviousComicUrl(num), (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    comic.setTitle(response.getString("title"));
                    comic.setImg(response.getString("img"));
                    comic.setNum(response.getInt("num"));
                    comic.setAlt_text(response.getString("alt"));

                    comic_title.setText(comic.getTitle());
                    Picasso.with(MainActivity.this).load(comic.getImg()).into(comic_img);
                    mAttacher = new PhotoViewAttacher(comic_img);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();

            }
        });

        mRequestQueue.add(mJsonObjectRequest);
    }

    private void getNextComic(int num) {

        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                getNextComicUrl(num), (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    comic.setTitle(response.getString("title"));
                    comic.setImg(response.getString("img"));
                    comic.setNum(response.getInt("num"));
                    comic.setAlt_text(response.getString("alt"));

                    comic_title.setText(comic.getTitle());
                    Picasso.with(MainActivity.this).load(comic.getImg()).into(comic_img);
                    mAttacher = new PhotoViewAttacher(comic_img);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();

            }
        });

        mRequestQueue.add(mJsonObjectRequest);
    }

    private void getRandomComic(int num) {

        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                getRandomComicUrl(num), (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    comic.setTitle(response.getString("title"));
                    comic.setImg(response.getString("img"));
                    comic.setNum(response.getInt("num"));
                    comic.setAlt_text(response.getString("alt"));

                    comic_title.setText(comic.getTitle());
                    Picasso.with(MainActivity.this).load(comic.getImg()).into(comic_img);
                    mAttacher = new PhotoViewAttacher(comic_img);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();

            }
        });

        mRequestQueue.add(mJsonObjectRequest);
    }

    private void searchComic(int num) {

        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                getSearchedComicUrl(num), (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    comic.setTitle(response.getString("title"));
                    comic.setImg(response.getString("img"));
                    comic.setNum(response.getInt("num"));
                    comic.setAlt_text(response.getString("alt"));

                    comic_title.setText(comic.getTitle());

                    Picasso.with(MainActivity.this).load(comic.getImg()).into(comic_img);
                    mAttacher = new PhotoViewAttacher(comic_img);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();

            }
        });

        mRequestQueue.add(mJsonObjectRequest);
    }

    /*

    Customised urls to fetch specific comic. View the Comic model class for JSON urls and
    endpoints

     */
    private String getSearchedComicUrl(int num) {
        return COMIC_URL + num + COMIC_URL_ENDPOINT;
    }


    private String getPreviousComicUrl(int num) {

        return COMIC_URL + num + COMIC_URL_ENDPOINT;
    }

    private String getLatestComicUrl() {

        return DEFAULT_COMIC_URL;
    }


    private String getNextComicUrl(int num) {

        return COMIC_URL + num + COMIC_URL_ENDPOINT;
    }

    private String getRandomComicUrl(int num) {

        return COMIC_URL + num + COMIC_URL_ENDPOINT;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        Intent intent = null;

        switch (menuItem.getItemId()) {

            case R.id.nav_comic:
                break;


            case R.id.nav_about:

                //Launch about activity
                intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;

        }

        return false;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        /*

        Find and add the search ability using the SearchView

         */
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint("Enter number 1 - 1564");
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);

        /*

        Upon successful query, perform the request

         */
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                int num = Integer.parseInt(query);

                if (mAltText.getVisibility() == View.VISIBLE) {

                    mAltText.setVisibility(View.GONE);

                    searchComic(num);
                    mNextButton.setVisibility(View.VISIBLE);


                } else {

                    searchComic(num);
                    mNextButton.setVisibility(View.VISIBLE);

                }


                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.action_share:

                Intent shareImageIntent = new Intent(Intent.ACTION_SEND);
                Uri comicUri = Uri.parse(comic.getImg());
                shareImageIntent.setType("text/plain");
                shareImageIntent.putExtra(Intent.EXTRA_TEXT, comicUri.toString());
                startActivity(Intent.createChooser(shareImageIntent, "Share Image"));

                break;

            case R.id.action_explain:

                String comicURL = "http://explainxkcd.com/" + comic.getNum();

                Intent explainComicIntent = new Intent(Intent.ACTION_VIEW);
                explainComicIntent.setData(Uri.parse(comicURL));
                startActivity(explainComicIntent);

                break;

            case R.id.action_show_alt:

                mAltText.setText(comic.getAlt_text());
                mAltText.setVisibility(View.VISIBLE);

                break;

            case R.id.action_download:

                Toast.makeText(this, "Downloading Image...", Toast.LENGTH_SHORT).show();
                new MyAsyncTask().execute(comic.getImg());

                break;

        }

        return super.onOptionsItemSelected(item);
    }


    /*

    AsyncTask used to perform background image downloading operation. The image is downloaded
    in the users public external Downloads folder

     */
    class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {


        int counter = 0;

        @Override
        protected void onPreExecute() {
            mProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            mProgressbar.setProgress(values[0]);
        }


        @Override
        protected Boolean doInBackground(String... params) {

            boolean success = false;

            URL imageDownloadURL;
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            File folder;

            try {
                imageDownloadURL = new URL(params[0]);
                connection = (HttpURLConnection) imageDownloadURL.openConnection();
                connection.connect();
                inputStream = connection.getInputStream();

                folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +
                        "/" + Uri.parse(params[0]).getLastPathSegment());

                outputStream = new FileOutputStream(folder);

                int read = -1;

                byte[] buffer = new byte[1024];

                while ((read = inputStream.read(buffer)) != -1) {

                    if (outputStream != null) {
                        outputStream.write(buffer, 0, read);

                        while (counter < 5) {
                            SystemClock.sleep(1000);
                            counter++;
                            publishProgress(counter * 20);
                        }

                        publishProgress(counter);
                    }

                }

                success = true;

            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (connection != null) {
                    connection.disconnect();
                }

                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return success;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressbar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Downloaded Successfully In Your Downloads Folder", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {

            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
        }
    }


}
