package com.zeus.office5;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentActivity;
        import android.support.v4.app.FragmentTransaction;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.TabHost;
        import android.widget.TabHost.TabContentFactory;

        import com.activeandroid.ActiveAndroid;
        import com.activeandroid.query.Delete;
        import com.activeandroid.query.Select;
        import com.zeus.office5.R;

/**
 * @author mwho
 *
 */
public class TabsFragmentActivity extends FragmentActivity implements AdminDetailFragment.AdminLogoutInterface, UserDetailFragment.UserLogoutInterface,TabHost.OnTabChangeListener,Tab1Fragment.Fragment1ListenerInterface,Tab1Fragment.Fragment1AdminListenerInterface {

    public TabHost mTabHost;
    public HashMap mapTabInfo = new HashMap();
    private TabInfo mLastTab = null;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Bundle bundleArgs;

    @Override
    public void onBackPressed() {
        new Delete().from(AllowedTags.class).execute();
        sp=getSharedPreferences("login",Context.MODE_PRIVATE);
        editor.putInt("lock",0);
        editor.commit();
        super.onBackPressed();

    }

    @Override
    protected void onStop() {
        new Delete().from(AllowedTags.class).execute();
        sp=getSharedPreferences("login",Context.MODE_PRIVATE);
        editor.putInt("lock",0);
        editor.commit();
        super.onStop();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu,menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id=item.getItemId();
//        if(id==R.id.addTab){
//            TabInfo tabInfo = null;
//            int num=mTabHost.getTabWidget().getTabCount();
//            TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab "+(num+1)).setIndicator("Tab "+(num+1)),
//                    ( tabInfo = new TabInfo("Tab "+(num+1), Tab1Fragment.class, null)));
//        this.mapTabInfo.put(tabInfo.tag, tabInfo);
//            mTabHost.setCurrentTab(num);
//        }
//        return true;
//    }




    @Override
    public void onFragment1Click(String curUserUsername) {
        TabInfo tabInfo = null;
            int num=mTabHost.getTabWidget().getTabCount();
        bundleArgs=new Bundle();
        bundleArgs.putInt("TabId",(num));
        bundleArgs.putString("curUserUsername",curUserUsername);
            TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab "+(num+1)).setIndicator("Tab "+(num+1)),
                    ( tabInfo = new TabInfo("Tab "+(num+1), UserDetailFragment.class, null)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
//        Log.i("Tag Check",mTabHost.getCurrentTabTag());
            mTabHost.setCurrentTab(num);
    }

    @Override
    public void onFragment1AdminClick(String curAdminUsername) {
        TabInfo tabInfo=null;
        int num=mTabHost.getTabWidget().getTabCount();
        bundleArgs=new Bundle();
        bundleArgs.putInt("TabId",(num));
        bundleArgs.putString("curAdminUsername",curAdminUsername);
        TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Admin"+(num+1)).setIndicator("Admin"+(num+1)),
                ( tabInfo = new TabInfo("Admin"+(num+1),AdminDetailFragment.class , null)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        mTabHost.setCurrentTab(num);
    }

    @Override
    public void userLogoutClick(int searchTag) {

        mTabHost.getTabWidget().getChildTabViewAt(mTabHost.getCurrentTab()).setVisibility(View.GONE);
        List<AllowedTags> allowedTags= new Select().from(AllowedTags.class).where("tag=?",searchTag).execute();
        if(allowedTags!=null&&allowedTags.size()>0){
            int lock=sp.getInt("lock",1);
            Log.i("lock",""+lock);
            --lock;
            editor.putInt("lock",lock).commit();
            allowedTags.get(0).delete();
            Log.i("lock",""+lock);
        }
        mTabHost.setCurrentTab(0);
    }

    @Override
    public void adminLogoutClick(int searchTag) {

        mTabHost.getTabWidget().getChildTabViewAt(mTabHost.getCurrentTab()).setVisibility(View.GONE);
        List<AllowedTags> allowedTags= new Select().from(AllowedTags.class).where("tag=?",searchTag).execute();
        if(allowedTags!=null&&allowedTags.size()>0){
            Log.i("lock",""+sp.getInt("lock",-1));
            editor.putInt("lock",0).commit();
            Log.i("lock","0");
            allowedTags.get(0).delete();
        }
        mTabHost.setCurrentTab(0);
    }

    private class TabInfo {
        private String tag;
        private Class clss;
        private Bundle args;
        private Fragment fragment;
        TabInfo(String tag, Class clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }

    }

    class TabFactory implements TabContentFactory {

        private final Context mContext;

        /**
         * @param context
         */
        public TabFactory(Context context) {
            mContext = context;
        }

        /** (non-Javadoc)
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }

    }
    /** (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Step 1: Inflate layout
        setContentView(R.layout.tabs_layout);
        // Step 2: Setup TabHost
        initialiseTabHost(savedInstanceState);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }

        sp=getSharedPreferences("login",Context.MODE_PRIVATE);
        editor=sp.edit();
        Boolean firstLogin=sp.getBoolean("firstLogin",true);
        if(firstLogin){
            ActiveAndroid.beginTransaction();
            try{
            AdminInfo admin1=new AdminInfo("admin","admin");
            admin1.save();
            AdminInfo admin2=new AdminInfo("system","dbms");
            admin2.save();
            UserInfo user1=new UserInfo("aman","aman",1*1000);
            user1.save();
            UserInfo user2=new UserInfo("zeus","zeus",2*1000);
            user2.save();
            UserInfo  user3=new UserInfo("phoenix","phoenix",3*1000);
            user3.save();
                ActiveAndroid.setTransactionSuccessful();
            }finally {
                ActiveAndroid.endTransaction();
            }
            editor=sp.edit();
            editor.putBoolean("firstLogin",false);
            editor.putInt("lock",0);
            editor.commit();
            Log.i("First login","success");
        }



    }

    /** (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }

    /**
     * Step 2: Setup TabHost
     */
    private void initialiseTabHost(Bundle args) {
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;
        TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator("Tab 1"),
                ( tabInfo = new TabInfo("Tab1", Tab1Fragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
//        TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator("Tab 2"), ( tabInfo = new TabInfo("Tab2", Tab1Fragment.class, args)));
//        this.mapTabInfo.put(tabInfo.tag, tabInfo);
//        TabsFragmentActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab3").setIndicator("Tab 3"), ( tabInfo = new TabInfo("Tab3", Tab1Fragment.class, args)));
//        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        // Default to first tab
        this.onTabChanged("Tab1");
        //
        mTabHost.setOnTabChangedListener(this);
    }


    public static void addTab(TabsFragmentActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(activity.new TabFactory(activity));
        String tag = tabSpec.getTag();

        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);

        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {

            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.detach(tabInfo.fragment);
            ft.commit();
            activity.getSupportFragmentManager().executePendingTransactions();
        }

        tabHost.addTab(tabSpec);
    }

    /** (non-Javadoc)
     * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
     */
    public void onTabChanged(String tag) {
        TabInfo newTab = (TabInfo) this.mapTabInfo.get(tag);
        if (mLastTab != newTab) {
            FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    ft.detach(mLastTab.fragment);
                }
            }
            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(this,
                            newTab.clss.getName(), newTab.args);
                    newTab.fragment.setArguments(bundleArgs);
                    ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
                } else {
                    ft.attach(newTab.fragment);
                }
            }

            mLastTab = newTab;
            ft.commit();
            this.getSupportFragmentManager().executePendingTransactions();
        }
    }


}