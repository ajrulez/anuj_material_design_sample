package com.alifesoftware.material.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.alifesoftware.material.R;
import com.alifesoftware.material.adapter.SimpleRecyclerAdapter;
import com.alifesoftware.material.model.VersionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anuj on 10/10/15.
 *
 * Inspired by: Android-Material-Design-for-pre-Lollipop-master
 *
 * Link: https://github.com/ajrulez/Android-Material-Design-for-pre-Lollipop
 *
 */
public class NavDrawerViewPagerActivity extends AppCompatActivity {
    // Log Tag
    private final static String TAG = "ND_VP_Activity";

    // Toolbar
    private Toolbar mToolbar;

    // Drawer Layout
    private DrawerLayout mDrawerLayout;

    // Navigation View
    private NavigationView mNavigationView;

    // View Pager
    private ViewPager mViewPager;

    // TabLayout that will get bound to the ViewPager
    private TabLayout mTabLayout;

    // Activity specific flags
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;

    // Flag to keep track of current selected position of Nav Item and View Pager item
    private int mCurrentSelectedPosition;

    // Preference File
    private static final String PREFERENCES_FILE = "mymaterialapp_settings";

    // Keys to save data for retaining state
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    // View Pager Adapter
    private ViewPagerAdapter mViewPagerAdapter;

    /**
     * onCreate to set up the Navigation Drawers
     * and View Pager along with other elements of
     * the view
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the NavigationDrawer ViewPager Combo Layout
        setContentView(R.layout.activity_navdrawer_viewpager);

        // Setup the Toolbar
        mToolbar = (Toolbar) findViewById(R.id.tabanim_toolbar);
        setSupportActionBar(mToolbar);

        // Set the Back Arrow or Home Up button
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set up Drawer Layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);

        // Read the setting from Shared Preferences
        mUserLearnedDrawer = Boolean.valueOf(readSharedSetting(this, PREF_USER_LEARNED_DRAWER, "false"));

        // Restore state from Shared Preferences
        if(savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Set up Navigation Drawer
        setupNavDrawer();

        // Inflate the Navigation View
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        // Set up the View  Pager
        mViewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        setupViewPager(mViewPager);

        // Inflate and setup Tabs
        mTabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);

        // Bind the TabLayout to ViewPager
        mTabLayout.setupWithViewPager(mViewPager);

        // Set up the Navigation View Item Click Listener/handler
        // Basically this does all the Navigation Magic from Navigation Drawer
        setupNavigationViewItemClickHandler();

        // Set up the Tab Item Scroll or Click Listener
        // Basically this does all the Navigation Magic from the Tab of ViewPager
        setupTabItemClickHandler();

        // Close the Drawer when the Activity Starts
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Method to show a Toast
     *
     * @param msg
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Method to show a Snackbar
     *
     * @param msg
     */
    private void showSnackbar(String msg) {
        Snackbar.make(mViewPager, msg, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Method to set up the Navigation Drawer
     * for this Activity
     *
     */
    private void setupNavDrawer() {
        if(mToolbar != null &&
                getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.ic_drawer);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open the Drawer
                    //
                    // If you want to Have the Drawer on Right then
                    // both openDrawer and closeDrawer will use GravityCompat.END
                    //
                    // Also, in XML android.support.design.widget.NavigationView
                    // should have layout gravity as end
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        if (! mUserLearnedDrawer) {
            // Open the Drawer
            //
            // If you want to Have the Drawer on Right then
            // both openDrawer and closeDrawer will use GravityCompat.END
            //
            // Also, in XML android.support.design.widget.NavigationView
            // should have layout gravity as end
            mDrawerLayout.openDrawer(GravityCompat.START);
            mUserLearnedDrawer = true;
            saveSharedSetting(this, PREF_USER_LEARNED_DRAWER, "true");
        }
    }

    /**
     * Method to set up the ViewPager
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        // Create the Adapter for the ViewPager
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // As a sample, add 3 Fragments
        mViewPagerAdapter.addFragment(new GenericFragment(getResources().getColor(R.color.cardview_light_background)),
                getString(R.string.item_one));
        mViewPagerAdapter.addFragment(new GenericFragment(getResources().getColor(R.color.cardview_light_background)),
                getString(R.string.item_two));
        mViewPagerAdapter.addFragment(new GenericFragment(getResources().getColor(R.color.cardview_light_background)),
                getString(R.string.item_three));

        // Bind the Adapter to the ViewPager
        viewPager.setAdapter(mViewPagerAdapter);
    }

    /**
     * Set up the Navigation View Item Click Listener / Handler
     * Basically this does all the Navigation Magic when we click on
     * any item in the Navigation Drawer
     *
     */
    private void setupNavigationViewItemClickHandler() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_1:
                        showSnackbar(getString(R.string.item_one));
                        // Set the selected index position
                        mCurrentSelectedPosition = 0;
                        break;
                    case R.id.navigation_item_2:
                        showSnackbar(getString(R.string.item_two));
                        // Set the selected index position
                        mCurrentSelectedPosition = 1;
                        break;
                    case R.id.navigation_item_3:
                        showSnackbar(getString(R.string.item_three));
                        // Set the selected index position
                        mCurrentSelectedPosition = 2;
                        break;
                    default:
                        break;
                }

                // Must set the current item for the ViewPager for the
                // Navigation to work correct because in this case
                // we want to use both Navigation Drawer and View Pager
                // for Navigation of Fragments.
                //
                // It is totally possible that we use Navigation Drawer for
                // something totally independent of the ViewPager, and in that
                // case, we do not need this
                //
                mViewPager.setCurrentItem(mCurrentSelectedPosition);

                // Close the Drawer
                //
                // If you want to Have the Drawer on Right then
                // both openDrawer and closeDrawer will use GravityCompat.END
                //
                // Also, in XML android.support.design.widget.NavigationView
                // should have layout gravity as end
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    /**
     * Set up the Tab Item Scroll or Click Listener
     *
     * Basically this does all the Navigation Magic from the Tab of ViewPager
     */
    private void setupTabItemClickHandler() {
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // Must set the current item for the ViewPager for the
                // Navigation to work correct because the TabLayout is bound
                // to the ViewPager
                mViewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        showToast(getString(R.string.item_one));
                        break;
                    case 1:
                        showToast(getString(R.string.item_two));

                        break;
                    case 2:
                        showToast(getString(R.string.item_three));

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * Method to create the Main Menu / Option Menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handler for Option Menu item selected
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * onSaveInstanceState is used to save the state of this
     * Activity during configuration changes
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    /**
     * onRestoreInstanceState is used to restore the state of this
     * Activity after configuration changes
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
        Menu menu = mNavigationView.getMenu();
        menu.getItem(mCurrentSelectedPosition).setChecked(true);
    }

    /**
     * Method to save settings to Shared Preferences
     *
     * @param ctx
     * @param settingName
     * @param settingValue
     */
    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    /**
     * Method to read settings from Shared Preferences
     *
     * @param ctx
     * @param settingName
     * @param defaultValue
     * @return
     */
    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    /**
     * ViewPagerAdapter class is used to implement the Page Adapter
     * for the ViewPager.
     *
     * We need to add Fragments to the Adapter and these are the
     * fragments that get switched when we navigate using either
     * NavigationDrawer or ViewPager
     *
     */
    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        // List of Fragments added to the Adapter
        private final List<Fragment> mFragmentList = new ArrayList<>();

        // List of titles of the Fragments added to the Adapter
        private final List<String> mFragmentTitleList = new ArrayList<>();

        /**
         * Constructor
         *
         * @param manager
         */
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        /**
         * Get the item at position from the Fragment List
         *
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        /**
         * Get the count/number of Fragments for this Adapter
         *
         * @return
         */
        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        /**
         * Method to add a Fragment to the Adapter
         * @param fragment
         * @param title
         */
        public void addFragment(Fragment fragment, String title) {
            // Add the Fragment to the List of Fragments
            mFragmentList.add(fragment);
            // Add the Title of Fragment to List of Title
            mFragmentTitleList.add(title);
        }

        /**
         * Method to get title of a Fragment by Position
         *
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /**
     * Generic Fragment Class - We will add Generic Fragments
     * to ViewPager. When we have specific content to display
     * for each Fragment, we can create more classes or maybe
     * extend GenericFragment
     *
     */
    public static class GenericFragment extends Fragment {
        // Color of the Fragment Background
        private int color;

        /**
         * Constructor
         *
         */
        public GenericFragment() {
            // Nothing to do
        }

        /**
         * Constructor with Background Color
         * attribute
         *
         * @param color
         */
        @SuppressLint("ValidFragment")
        public GenericFragment(int color) {
            this.color = color;
        }

        /**
         * Create View is used to inflate and setup the View
         * for the Fragment
         * @param inflater
         * @param container
         * @param savedInstanceState
         * @return
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the VIew from XML
            View view = inflater.inflate(R.layout.generic_fragment, container, false);

            // Set the FrameLayout and set the background color
            final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.emptyfrag_bg);
            frameLayout.setBackgroundColor(color);

            // Create RecyclerView
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.emptyfrag_scrollableview);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);

            List<String> list = new ArrayList<>();
            for (int i = 0; i < VersionModel.data.length; i++) {
                list.add(VersionModel.data[i]);
            }

            RecyclerView.Adapter adapter = new SimpleRecyclerAdapter(list);
            recyclerView.setAdapter(adapter);

            return view;
        }
    }
}
