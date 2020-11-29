package com.gtechnologies.videog.Activity;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gtechnologies.videog.Fragment.Drama;
import com.gtechnologies.videog.Fragment.Hits;
import com.gtechnologies.videog.Fragment.Home;
import com.gtechnologies.videog.Fragment.Language;
import com.gtechnologies.videog.Fragment.Movie;
import com.gtechnologies.videog.Fragment.Selected;
import com.gtechnologies.videog.Fragment.Subscription;
import com.gtechnologies.videog.Fragment.Video;
import com.gtechnologies.videog.Interface.LanguageInterface;
import com.gtechnologies.videog.Library.KeyWord;
import com.gtechnologies.videog.Library.Utility;
import com.gtechnologies.videog.R;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LanguageInterface {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    TextView pageTitle;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Utility utility = new Utility(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        pageTitle = findViewById(R.id.page_title);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        navigationView.setNavigationItemSelectedListener(this);
        //utility.setFont(pageTitle);
        String topTitle = getResources().getString(R.string.titleEn);
        if (utility.getLangauge().equals("bn")) {
            topTitle = getResources().getString(R.string.titleBn);
        }
        pageTitle.setText(topTitle);
        callFragment(R.id.nav_home, new Home(MainActivity.this), KeyWord.HOME, topTitle);
        changeNavigationText();
    }

    @Override
    public void onBackPressed() {
        try {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    if (isTaskRoot()) {
                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        HashMap<String, Integer> screen = utility.getScreenRes();
                        int width = screen.get(KeyWord.SCREEN_WIDTH);
                        int height = screen.get(KeyWord.SCREEN_HEIGHT);
                        int mywidth = (width / 10) * 7;
                        final Dialog dialog = new Dialog(this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.setContentView(R.layout.dialog_layout);
                        TextView tv = dialog.findViewById(R.id.permission_message);
                        Button yes = dialog.findViewById(R.id.dialog_yes);
                        Button no = dialog.findViewById(R.id.dialog_no);
                        tv.setText(utility.getLangauge().equals("bn") ? getString(R.string.exit_dialog_bn) : getString(R.string.exit_dialog));
                        LinearLayout ll = dialog.findViewById(R.id.dialog_layout_size);
                        utility.setFonts(new View[]{tv, yes, no});
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
                        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        params.width = mywidth;
                        ll.setLayoutParams(params);
                        yes.setText(utility.getLangauge().equals("bn") ? getString(R.string.yes_bn) : getString(R.string.yes));
                        no.setText(utility.getLangauge().equals("bn") ? getString(R.string.no_bn) : getString(R.string.no));
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
//                            Fragment live = (Fragment) getSupportFragmentManager().findFragmentByTag(KeyWord.LIVE);
//                            if (live != null && live.isVisible()) {
//                                live.onDestroy();
//                            }
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        });
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setCancelable(false);
                        dialog.show();
                    } else {
                        super.onBackPressed();
                    }
                } else {
                    getSupportFragmentManager().popBackStackImmediate();
                }
            }
        } catch (Exception ex) {
            //utility.call_error(ex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        drawerLayout.closeDrawers();
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            String topTitle = getResources().getString(R.string.titleEn);
            if (utility.getLangauge().equals("bn")) {
                topTitle = getResources().getString(R.string.titleBn);
            }
            //String topTitle = getResources().getString(R.string.titleEn);
            callFragment(R.id.nav_home, new Home(MainActivity.this), KeyWord.HOME, topTitle);
        } else if (id == R.id.nav_hits) {
            String topTitle = getResources().getString(R.string.hits);
            if (utility.getLangauge().equals("bn")) {
                topTitle = getResources().getString(R.string.hits_bn);
            }
            //String topTitle = getResources().getString(R.string.hits);
            callFragment(R.id.nav_hits, new Hits(MainActivity.this), KeyWord.HITS, topTitle);
        } else if (id == R.id.nav_selected) {
            String topTitle = getResources().getString(R.string.selected);
            if (utility.getLangauge().equals("bn")) {
                topTitle = getResources().getString(R.string.selected_bn);
            }
            //String topTitle = getResources().getString(R.string.selected);
            callFragment(R.id.nav_selected, new Selected(MainActivity.this), KeyWord.SELECTED, topTitle);
        } /*else if (id == R.id.nav_mylist) {
            String topTitle = getResources().getString(R.string.mylist);
            if (utility.getLangauge().equals("bn")) {
                topTitle = getResources().getString(R.string.mylist_bn);
            }
            callFragment(R.id.nav_mylist, new Hits(MainActivity.this, topTitle), KeyWord.MY_LIST, topTitle);
        } */ else if (id == R.id.nav_song) {
            String topTitle = getResources().getString(R.string.video_song);
            if (utility.getLangauge().equals("bn")) {
                topTitle = getResources().getString(R.string.video_song_bn);
            }
            callFragment(R.id.nav_song, new Video(MainActivity.this), KeyWord.VIDEO_SONG, topTitle);
        } else if (id == R.id.nav_drama) {
            String topTitle = getResources().getString(R.string.drama);
            if (utility.getLangauge().equals("bn")) {
                topTitle = getResources().getString(R.string.drama_bn);
            }
            callFragment(R.id.nav_drama, new Drama(MainActivity.this), KeyWord.DRAMA, topTitle);
        } else if (id == R.id.nav_movie) {
            String topTitle = getResources().getString(R.string.movie);
            if (utility.getLangauge().equals("bn")) {
                topTitle = getResources().getString(R.string.movie_bn);
            }
            callFragment(R.id.nav_movie, new Movie(MainActivity.this), KeyWord.MOVIE, topTitle);
        } else if (id == R.id.nav_subscription) {
            String topTitle = getResources().getString(R.string.subscription);
            if (utility.getLangauge().equals("bn")) {
                topTitle = getResources().getString(R.string.subscription_bn);
            }
            callFragment(R.id.nav_subscription, new Subscription(MainActivity.this,this), KeyWord.SUBSCRIPTION, topTitle);
        } else if (id == R.id.nav_language) {
            callFragment(R.id.nav_language, new Language(MainActivity.this, this), KeyWord.LANGUAGE, "Language");
        }/*
        else if (id == R.id.nav_subscription) {
            callFragment(R.id.nav_language, new Hits(MainActivity.this, "Subscription"), KeyWord.LANGUAGE, "Subscription");
        }*/
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void callFragment(int itemId, android.support.v4.app.Fragment fragment, String tag, String title) {
        pageTitle.setText(title);
        //utility.setFont(pageTitle);
        try {
            while (fragmentManager.getBackStackEntryCount() != 0) {
                fragmentManager.popBackStackImmediate();
            }
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.containerView, fragment, tag).commit();
            navigationView.getMenu().findItem(itemId).setChecked(true);
        } catch (Exception ex) {
            //utility.call_error(ex);
        }
    }

    private void changeNavigationText() {
        String language = utility.getLangauge();
        Menu menu = navigationView.getMenu();
        if (language.equals("bn")) {
            menu.findItem(R.id.nav_home).setTitle(R.string.home_bn);
            menu.findItem(R.id.nav_hits).setTitle(R.string.hits_bn);
            menu.findItem(R.id.nav_selected).setTitle(R.string.selected_bn);
            //menu.findItem(R.id.nav_mylist).setTitle(R.string.mylist_bn);
            menu.findItem(R.id.nav_song).setTitle(R.string.video_song_bn);
            menu.findItem(R.id.nav_drama).setTitle(R.string.drama_bn);
            menu.findItem(R.id.nav_movie).setTitle(R.string.movie_bn);
            //menu.findItem(R.id.nav_offline).setTitle(R.string.offline_bn);
            menu.findItem(R.id.nav_subscription).setTitle(R.string.subscription_bn);
        } else {
            menu.findItem(R.id.nav_home).setTitle(R.string.home);
            menu.findItem(R.id.nav_hits).setTitle(R.string.hits);
            menu.findItem(R.id.nav_selected).setTitle(R.string.selected);
            //menu.findItem(R.id.nav_mylist).setTitle(R.string.mylist);
            menu.findItem(R.id.nav_song).setTitle(R.string.video_song);
            menu.findItem(R.id.nav_drama).setTitle(R.string.drama);
            menu.findItem(R.id.nav_movie).setTitle(R.string.movie);
            //menu.findItem(R.id.nav_offline).setTitle(R.string.offline);
            menu.findItem(R.id.nav_subscription).setTitle(R.string.subscription);
        }
    }

    @Override
    public void changeLanguage() {
        changeNavigationText();
    }
}
