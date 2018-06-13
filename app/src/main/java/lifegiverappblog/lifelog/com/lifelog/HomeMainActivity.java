package lifegiverappblog.lifelog.com.lifelog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import lifegiverappblog.lifelog.com.lifelog.fragments.AccountFragment;
import lifegiverappblog.lifelog.com.lifelog.fragments.HomeFragment;
import lifegiverappblog.lifelog.com.lifelog.fragments.NotificationFragment;
import lifegiverappblog.lifelog.com.lifelog.posts.LgPostActivity;

public class HomeMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String current_user_id;
    private FloatingActionButton fab_post_add;
    private BottomNavigationView mainBottomNav;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(mAuth.getCurrentUser()!=null)
        {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, mainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            viewPager = (ViewPager) findViewById(R.id.viewpager);
            setupViewPager(viewPager);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
            setupTabIcons();


            FABmenuBtn();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
           sendToLogin();
        }
        else
            {
                current_user_id = mAuth.getCurrentUser().getUid();
                firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                         if(task.isSuccessful())
                         {
                                if(!task.getResult().exists())
                                {
                                    Intent setupIntent = new Intent(HomeMainActivity.this, SetupAccountActivity.class);
                                    startActivity(setupIntent);
                                    finish();
                                }
                         }
                         else
                             {
                                 String errorMessage = task.getException().getMessage();
                                 Toast.makeText(HomeMainActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG);
                             }
                    }
                });
            }
    }
    /**
     * Adding custom view to tab
     */
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Home");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home_black_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Accounts");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_person_black_24dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Notification");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_notifications_black_24dp, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

    }

    /**
     * Adding fragments to ViewPager
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HomeFragment(), "Home");
        adapter.addFrag(new AccountFragment(), "Accounts");
        adapter.addFrag(new NotificationFragment(), "Notification");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final java.util.List<Fragment> mFragmentList = new ArrayList<>();
        private final java.util.List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            // return null to display only the icon
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

            case R.id.action_logout_btn:
                logOut();
                return true;

            case R.id.action_settings_btn:
                Intent accSettingsIntent = new Intent(HomeMainActivity.this, SetupAccountActivity.class);
                startActivity(accSettingsIntent);

                default:
                    return false;
        }

    }
    private void sendToLogin() {
        Intent intent = new Intent(HomeMainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void logOut() {
       mAuth.signOut();
       sendToLogin();
    }
    private void FABmenuBtn() {

        final FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.homeBtn);
        FloatingActionButton mFabNews = (FloatingActionButton) findViewById(R.id.AddNewsFeedBtn);
        FloatingActionButton mFabNewfriend = (FloatingActionButton) findViewById(R.id.AddNewFriendBtn);
        FloatingActionButton mFabLifegroups = (FloatingActionButton) findViewById(R.id.AddLifegroupsAttendanceBtn);

        final LinearLayout mLGLayout = (LinearLayout) findViewById(R.id.AddNewsLayout);
        final LinearLayout mNFLayout = (LinearLayout) findViewById(R.id.AddNewFriendLayout);
        final LinearLayout mANNLayout = (LinearLayout) findViewById(R.id.AddLifegroupsLayout);

        final Animation mShowButton = AnimationUtils.loadAnimation(HomeMainActivity.this, R.anim.show_button);
        final Animation mHideButton = AnimationUtils.loadAnimation(HomeMainActivity.this, R.anim.hide_button);
        final Animation mShowLayout = AnimationUtils.loadAnimation(HomeMainActivity.this, R.anim.show_layout);
        final Animation mHideLayout = AnimationUtils.loadAnimation(HomeMainActivity.this, R.anim.hide_layout);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLGLayout.getVisibility() == View.VISIBLE && mNFLayout.getVisibility() == View.VISIBLE && mANNLayout.getVisibility() == View.VISIBLE) {
                    mLGLayout.setVisibility(View.GONE);
                    mNFLayout.setVisibility(View.GONE);
                    mANNLayout.setVisibility(View.GONE);

                    mLGLayout.startAnimation(mHideLayout);
                    mNFLayout.startAnimation(mHideLayout);
                    mANNLayout.startAnimation(mHideLayout);

                    mFab.startAnimation(mHideButton);
                } else {
                    mLGLayout.setVisibility(View.VISIBLE);
                    mNFLayout.setVisibility(View.VISIBLE);
                    mANNLayout.setVisibility(View.VISIBLE);

                    mLGLayout.startAnimation(mShowLayout);
                    mNFLayout.startAnimation(mShowLayout);
                    mANNLayout.startAnimation(mShowLayout);

                    mFab.startAnimation(mShowButton);
                }
            }
        });

        mFabNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeMainActivity.this, LgPostActivity.class));

                mLGLayout.setVisibility(View.GONE);
                mNFLayout.setVisibility(View.GONE);
                mANNLayout.setVisibility(View.GONE);


            }
        });

        mFabNewfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeMainActivity.this, LgPostActivity.class));


                mLGLayout.setVisibility(View.GONE);
                mNFLayout.setVisibility(View.GONE);
                mANNLayout.setVisibility(View.GONE);
            }
        });

        mFabLifegroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeMainActivity.this, LgPostActivity.class));

                mLGLayout.setVisibility(View.GONE);
                mNFLayout.setVisibility(View.GONE);
                mANNLayout.setVisibility(View.GONE);
            }
        });


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Calendar) {
            startActivity(new Intent(HomeMainActivity.this, RegisterActivity.class));
            finish();
        } else if (id == R.id.nav_todolist) {
            startActivity(new Intent(HomeMainActivity.this, RegisterActivity.class));
            finish();
        } else if (id == R.id.nav_columnar) {
            startActivity(new Intent(HomeMainActivity.this, RegisterActivity.class));
            finish();
        } else if (id == R.id.nav_charts) {
            startActivity(new Intent(HomeMainActivity.this, RegisterActivity.class));
            finish();
        }
        else if (id == R.id.nav_organization) {
            startActivity(new Intent(HomeMainActivity.this, RegisterActivity.class));
            finish();
        }
        else if (id == R.id.nav_About) {
            startActivity(new Intent(HomeMainActivity.this, RegisterActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
