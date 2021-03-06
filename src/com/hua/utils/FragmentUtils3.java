/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   FragmentUtils.java

package com.hua.utils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.hua.activity.R;
import com.hua.app.BaseFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

// Referenced classes of package com.pccw.gzmobile.app:
//            BaseFragment

public class FragmentUtils3
{
	 private static final String TAG = "com/pccw/gzmobile/app/FragmentUtils.getSimpleName()";
	
    private static abstract class AbsFragmentTabSwitcher
    {
        public abstract void switchTab(String s, boolean flag);

        public static final int INVALID_TAB_INDEX = -1;
        protected FragmentActivity mFragmentActivity;
        protected int mContainerId;
        protected FragmentTabSwitcherFeed mSwitcherFeed;
        //锟斤拷HashMap锟斤拷一锟斤拷锟斤拷锟洁，锟斤拷锟斤拷锟剿硷拷录锟侥诧拷锟斤拷顺锟斤拷锟斤拷锟斤拷Iterator锟斤拷锟斤拷LinkedHashMap时锟斤拷锟饺得碉拷锟侥硷拷录锟较讹拷锟斤拷锟饺诧拷锟斤拷锟�
        protected LinkedHashMap mRootFragmentTags;//锟斤拷锟絟ashmap锟斤拷一头锟斤拷一头锟斤拷锟斤拷 
        protected final LinkedHashMap mTabStacks = new LinkedHashMap();
        private final int mTabCount;
        protected Fragment mCurrentFragment;
        protected String mCurrentRootFragmentTag;
        protected String mPreviousRootFragmentTag;
        protected FragmentTabSwitcherListener mSwitcherListener;
    	
        public void setSwitcherListener(FragmentTabSwitcherListener listener)
        {
            mSwitcherListener = listener;
        }

        public FragmentActivity getFragmentActivity()
        {
            return mFragmentActivity;
        }

        public int getTabCount()
        {
            return mTabCount;
        }

        public Fragment getCurrentTabFragment()
        {
            return mCurrentFragment;
        }

        public String getCurrentTabId()
        {
            return mCurrentRootFragmentTag;
        }

        /**
         * 锟斤拷取锟斤拷前选锟叫碉拷锟铰憋拷
         * @return
         */
        public int getCurrentTabIndex()
        {
            return ((Integer)mRootFragmentTags.get(mCurrentRootFragmentTag)).intValue();
        }

        public String getPreviousTabId()
        {
            return mPreviousRootFragmentTag;
        }

        /**
         * 锟斤拷取锟斤拷一锟斤拷锟斤拷index
         * @return
         */
        public int getPreviousTabIndex()
        {
            return mPreviousRootFragmentTag == null ? -1 : ((Integer)mRootFragmentTags.get(mPreviousRootFragmentTag)).intValue();
        }

        /**
         * 锟叫讹拷锟角凤拷同一锟斤拷tab锟斤拷签
         * @return
         */
        protected boolean isSameTab()
        {
            return mPreviousRootFragmentTag != null && mPreviousRootFragmentTag.equals(mCurrentRootFragmentTag);
        }

        public void switchTab(String rootFragmentTag)
        {
            switchTab(rootFragmentTag, true);
        }


        public AbsFragmentTabSwitcher(FragmentActivity act, int containerId, FragmentTabSwitcherFeed feed)
        {
            mFragmentActivity = act;
            if(mFragmentActivity == null)
                throw new RuntimeException("FragmentActivity can NOT be null.");
            mContainerId = containerId;
            if(mContainerId == 0)
                throw new RuntimeException("Container id can NOT be 0.");
            mSwitcherFeed = feed;
            if(mSwitcherFeed == null)
                throw new RuntimeException("FragmentTabSwitcherFeed can NOT be null.");
            LinkedHashSet tagSet = feed.getRootFragmentTags();
            LogUtils2.d("tagSet===="+tagSet.size());
            if(tagSet == null || tagSet.size() == 0)
                throw new RuntimeException("FragmentTabSwitcherFeed.getRootFragmentTags() returns null or size is 0.");
            mRootFragmentTags = new LinkedHashMap(3);
            Iterator tagSetIterator = tagSet.iterator();
            for(int index = 0; tagSetIterator.hasNext(); index++)
            {
                String tag = (String)tagSetIterator.next();
                if(tag == null)
                    throw new RuntimeException("tab root fragment tag can NOT be null.");
                if(tag.equals(""))
                    throw new RuntimeException("tab root fragment tag can NOT be empty.");
                mRootFragmentTags.put(tag, Integer.valueOf(index));
            }

            Log.w(FragmentUtils3.TAG, (new StringBuilder("FragmentTabSwitcher root fragment tags : ")).append(mRootFragmentTags).toString());
            mTabCount = mRootFragmentTags.size();
            String tag;
            for(tagSetIterator = tagSet.iterator(); tagSetIterator.hasNext(); mTabStacks.put(tag, new LinkedList()))
                tag = (String)tagSetIterator.next();

            Log.w(FragmentUtils3.TAG, (new StringBuilder("FragmentTabSwitcher tab stacks : ")).append(mTabStacks).toString());
        }
    }

    /**
     * 一锟斤拷锟斤拷锟斤拷锟铰憋拷tab锟斤拷锟斤拷
     * @author Hua
     *
     */
    public static abstract class FragmentTabBarController
    {


        private Context mContext;
        private boolean mHorizontalTabBar;
        private LinearLayout mLinearLayout;
        private LayoutInflater mLayoutInflater;
        private boolean built;

        public FragmentTabBarController(Context context, ViewGroup container, boolean horizontalTabBar)
        {
            mContext = context;
            mHorizontalTabBar = horizontalTabBar;
            mLinearLayout = new LinearLayout(mContext);
            mLayoutInflater = LayoutInflater.from(mContext);
            if(mHorizontalTabBar)
            {
                mLinearLayout.setOrientation(0);
                int tabBarHeight = -2;
                if(container.getLayoutParams().height == -1)
                    tabBarHeight = -1;
                mLinearLayout.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, tabBarHeight));
                mLinearLayout.setGravity(16);
                container.addView(mLinearLayout);
            } else
            {
                mLinearLayout.setOrientation(1);
                int tabBarWidth = -2;
                if(container.getLayoutParams().width == -1)
                    tabBarWidth = -1;
                mLinearLayout.setLayoutParams(new android.widget.LinearLayout.LayoutParams(tabBarWidth, -1));
                mLinearLayout.setGravity(1);
                ScrollView sv = new ScrollView(mContext);
                sv.setVerticalScrollBarEnabled(false);
                sv.setVerticalFadingEdgeEnabled(false);
                if(AndroidSDK.API_LEVEL >= 9)
                    sv.setOverScrollMode(2);
                sv.addView(mLinearLayout);
                container.addView(sv);
            }
        }
    	
        /**
         * 锟斤拷锟絫ab 锟斤拷刷锟斤拷
         * @param rebuild
         */
        private void refreshTabBar(boolean rebuild)
        {
            if(rebuild)
                mLinearLayout.removeAllViews();
            int count = getCount();
            int childCount = mLinearLayout.getChildCount();
            View convertView = null;
            View tabBarItemView = null;
            for(int i = 0; i < count; i++)
            {
                if(childCount > i)
                    convertView = mLinearLayout.getChildAt(i);
                LogUtils2.w("begin getview in refreshTabBar+++++++++++");
                tabBarItemView = getView(i, convertView, mLinearLayout, mLayoutInflater);
                LogUtils2.w("tabBarItemView=="+tabBarItemView);
                if(rebuild)
                    if(mHorizontalTabBar)
                    {
                    	LogUtils2.e("mHorizontalTabBar=="+mHorizontalTabBar);
                    	LogUtils2.e("tabBarItemView==***"+tabBarItemView.getClass());
                    	LogUtils2.e("tabBarItemView.getLayoutParams().height=="+
                    			tabBarItemView.getLayoutParams().WRAP_CONTENT);
                    	
                    	android.widget.LinearLayout.LayoutParams ll = 
                    			new android.widget.LinearLayout.LayoutParams(0, tabBarItemView.getLayoutParams().WRAP_CONTENT);
                    	LogUtils2.e("ll=="+ll);
                        ll.weight = 1.0F;
                        mLinearLayout.addView(tabBarItemView, ll);
                    } else
                    {
                        mLinearLayout.addView(tabBarItemView);
                    }
            }

        }

        public void createTabBar()
        {
            if(!built)
            {
                refreshTabBar(true);
                built = true;
            } else
            {
                throw new IllegalStateException("Tab bar has been created. No need to create again.");
            }
        }

        public void refreshTabBar()
        {
            if(!built)
            {
                throw new IllegalStateException("Tab bar has not been created.");
            } else
            {
                refreshTabBar(false);
                return;
            }
        }

        public abstract int getCount();

        public abstract View getView(int i, View view, ViewGroup viewgroup, LayoutInflater layoutinflater);

        public final View getItem(int position)
        {
            if(!built)
                throw new IllegalStateException("Tab bar has not been created.");
            else
                return mLinearLayout.getChildAt(position);
        }

    }

    /**
     * 
     * @author Hua
     *
     */
    public static class FragmentTabSwitcher extends AbsFragmentTabSwitcher
    {


        public FragmentTabSwitcher(FragmentActivity act, int containerId, FragmentTabSwitcherFeed feed)
        {
            super(act, containerId, feed);
        }
    	
        /**
         * 锟洁当锟斤拷锟斤拷锟斤拷碌锟絫ab
         */
        public void switchTab(String rootFragmentTag, boolean skipSameTab)
        {
        	LogUtils2.v("********switchTab*********");
        	
        	if(getCurrentTabStack() != null){
        		
        		LogUtils2.e("switchTab=="+getCurrentTabStack().size());
        		LogUtils2.d("switchTab=="+getCurrentTabStack().getFirst());
        		LogUtils2.e("switchTab=="+getCurrentTabStack().getLast());
        	}
        	
            if(rootFragmentTag == null)
                throw new NullPointerException("Fragment tag can NOT be null.");
            mPreviousRootFragmentTag = mCurrentRootFragmentTag;
            mCurrentRootFragmentTag = rootFragmentTag;
            if(skipSameTab && isSameTab())
            {
            	LogUtils2.w("same**************");
                Log.w(FragmentUtils3.TAG, (new StringBuilder(String.valueOf(getClass().getSimpleName()))).append(" no need to switch same tab : ").append(mCurrentRootFragmentTag).toString());
                if(mSwitcherListener != null)
                {
                    int tabIndex = getCurrentTabIndex();
                    int preTabIndex = getPreviousTabIndex();
                    Log.d(FragmentUtils3.TAG, (new StringBuilder("onTabSelected : ")).append(mCurrentRootFragmentTag).append(", ").append(tabIndex).append(", ").append(mPreviousRootFragmentTag).append(", ").append(preTabIndex).toString());
                    mSwitcherListener.onTabSelected(mCurrentRootFragmentTag, tabIndex, mPreviousRootFragmentTag, preTabIndex);
                }
                return;
            }
            Log.d(FragmentUtils3.TAG, (new StringBuilder(String.valueOf(getClass().getSimpleName()))).append(" switch tab : ").append(rootFragmentTag).toString());
           
            if(getCurrentTabStack().size() == 0){
            	LogUtils2.i("getCurrentTabStack().size() == 0");
                pushFragment(true, new Fragment[] {
                    mSwitcherFeed.newRootFragment(rootFragmentTag)
                });
            } 
            else {
            	
            	LogUtils2.i("getCurrentTabStack().size() 锟斤拷+++===0");
                pushFragment(false, new Fragment[] {
                    peekTopmostFragment()
                });
            }
            if(mSwitcherListener != null)
            {
                int tabIndex = getCurrentTabIndex();
                int preTabIndex = getPreviousTabIndex();
                Log.d(FragmentUtils3.TAG, (new StringBuilder("onTabSelected : ")).append(mCurrentRootFragmentTag).append(", ").append(tabIndex).append(", ").append(mPreviousRootFragmentTag).append(", ").append(preTabIndex).toString());
                mSwitcherListener.onTabSelected(mCurrentRootFragmentTag, tabIndex, mPreviousRootFragmentTag, preTabIndex);
            }
        }

        /**
         * 锟斤拷锟絋abfragmentTag锟斤拷LinkedList
         * 锟斤拷锟斤拷锟絤TabStacks 锟斤拷一锟斤拷LinkedHashMap 锟斤拷锟脚革拷式锟角ｏ拷锟斤拷Tag锟斤拷锟斤拷LinkedList锟斤拷
         * tag锟斤拷锟角斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�锟斤拷tab
         * @return
         */
        private LinkedList getCurrentTabStack()
        {
        	LogUtils2.w("mCurrentRootFragmentTag==="+mCurrentRootFragmentTag);
            if(mCurrentRootFragmentTag == null)
            {
                Log.w(FragmentUtils3.TAG, "Please call switchTab() first before using other methods.");
                return null;
            }
            LinkedList tab = (LinkedList)mTabStacks.get(mCurrentRootFragmentTag);
            if(tab == null)
            {
                Log.w(FragmentUtils3.TAG, (new StringBuilder("Can NOT find the the tab with key ")).append(mCurrentRootFragmentTag).toString());
                return null;
            } else
            {
            	LogUtils2.i("tab.szie=="+tab.size());
                return tab;
            }
        }

        public boolean isCurrentTabStackEmpty()
        {
            LinkedList tab = getCurrentTabStack();
            boolean ret = tab == null || tab.isEmpty();
            if(ret)
                Log.w(FragmentUtils3.TAG, "Current tab stack is empty.");
            return ret;
        }

        /**
         * 锟斤拷getCurrentTabStack锟斤拷锟斤拷锟斤拷取锟斤拷锟斤拷一锟斤拷fragment
         * 褰撴寜涓嬭繑鍥為敭鍚�鎶婂綋鍓嶇殑fragment鍘绘帀锛屾妸涔嬪墠鐨勬崲鍋氫负绗竴涓�         * @return
         */
        public Fragment peekTopmostFragment()
        {
            if(isCurrentTabStackEmpty())
                return null;
            else
                return mFragmentActivity.getSupportFragmentManager().findFragmentByTag((String)getCurrentTabStack().getFirst());
        }

        /**
         * 锟斤拷getCurrentTabStack锟斤拷锟斤拷锟斤拷锟斤拷锟絝ragment锟斤拷锟斤拷...
         * @param add 锟叫讹拷锟斤拷锟斤拷踊锟斤拷前蟾康锟絝ragment
         * @param fragments 锟斤拷要锟斤拷拥锟絝ragment
         */
        private void pushFragment(boolean add, Fragment fragments[])
        {
            FragmentManager manager = mFragmentActivity.getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.setCustomAnimations(R.anim.push_left_in,0,0,R.anim.push_left_out);
            Fragment afragment[];
            int j = (afragment = fragments).length;
            for(int i = 0; i < j; i++)
            {
                Fragment fragment = afragment[i];
                if(mCurrentFragment != null)
                {
                    Log.d(FragmentUtils3.TAG, (new StringBuilder("Detach fragment ")).append(mCurrentFragment.getTag()).toString());
                   /**
                    * detach()锟结将view锟斤拷viewtree锟斤拷删锟斤拷,锟斤拷remove()锟斤拷同,
                    * 锟斤拷时fragment锟斤拷状态锟斤拷然锟斤拷锟斤拷锟斤拷,锟斤拷使锟斤拷attach()时锟斤拷锟劫次碉拷锟斤拷onCreateView()
                    * 锟斤拷锟截伙拷锟斤拷图,注锟斤拷使锟斤拷detach()
                    * 锟斤拷fragment.isAdded()锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷false,锟斤拷使锟斤拷attach()
                    * 锟斤拷原fragment锟斤拷isAdded()锟斤拷锟斤拷然锟斤拷锟斤拷false(锟斤拷要锟劫达拷确锟斤拷)
                    */
                    LogUtils2.i("pushFragment*******");
                    ft.detach(mCurrentFragment);
                }
                if(add)
                {
                	LogUtils2.i("add********=="+add);
                	//锟斤拷锟斤拷 "TV - 3"
                    String fragmentTag = (new StringBuilder(String.valueOf(mCurrentRootFragmentTag))).append("-").append(getCurrentTabStack().size()).toString();
                    Log.d(FragmentUtils3.TAG, (new StringBuilder("Add new fragment ")).append(fragmentTag).toString());
                    ft.add(mContainerId, fragment, fragmentTag);
                    
                    getCurrentTabStack().addFirst(fragmentTag);
                } else
                {
                	LogUtils2.e("pushFragment_____________");
                	LogUtils2.i("add********=="+add);
                    Log.d(FragmentUtils3.TAG, (new StringBuilder("Attach fragment ")).append(fragment.getTag()).toString());
                    ft.attach(fragment);
                }
                mCurrentFragment = fragment;
            }

            ft.commit();
        }

        public void pushFragment(Fragment fragment)
        {
            pushFragment(true, new Fragment[] {
                fragment
            });
        }

        public void pushFragments(Fragment fragments[])
        {
            pushFragment(true, fragments);
        }

        /**
         * 锟斤拷锟絋ag锟斤拷取锟斤拷锟斤拷前锟斤拷LinkedList锟斤拷锟阶革拷fragment
         * @return
         */
        private Fragment popTopmostFragment()
        {
            if(isCurrentTabStackEmpty())
            {
                return null;
            } else
            {
                String tag = (String)getCurrentTabStack().removeFirst();
                Log.d(FragmentUtils3.TAG, (new StringBuilder("Remove fragment ")).append(tag).toString());
                return mFragmentActivity.getSupportFragmentManager().findFragmentByTag(tag);
            }
        }

        /**
         * 锟狡筹拷前锟斤拷锟阶革拷fragment
         * @return
         */
        public Fragment popFragment()
        {
            FragmentManager manager = mFragmentActivity.getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
//            ft.setCustomAnimations(R.anim.push_left_in,0,0,R.anim.push_left_out);
//            ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            ft.remove(popTopmostFragment());
            mCurrentFragment = peekTopmostFragment();
            ft.attach(mCurrentFragment);
            Log.d(FragmentUtils3.TAG, (new StringBuilder("Attach fragment ")).append(mCurrentFragment.getTag()).toString());
            ft.commit();
            return mCurrentFragment;
        }

        public Fragment popToRootFragment()
        {
            int size = getCurrentTabStack().size();
            if(size > 1)
            {
                FragmentManager manager = mFragmentActivity.getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                for(int i = 0; i < size - 1; i++)
                    ft.remove(popTopmostFragment());

                mCurrentFragment = peekTopmostFragment();
                ft.attach(mCurrentFragment);
                Log.d(FragmentUtils3.TAG, (new StringBuilder("Attach fragment ")).append(mCurrentFragment.getTag()).toString());
                ft.commit();
            }
            return mCurrentFragment;
        }

        public boolean isRootFragment()
        {
            return getCurrentTabStack().size() == 1;
        }

        /**
         * 锟斤拷锟斤拷锟斤拷一锟斤拷fragmenttab
         */
        public void onFragmentActivityBackPressed()
        {
            onFragmentActivityBackPressed(null);
        }

        
        public void onFragmentActivityBackPressed(Runnable superOnBackPressed)
        {
            Fragment f = getCurrentTabFragment();
            LogUtils2.d("Currtent=size=="+getCurrentTabStack().size());
            LogUtils2.d("getCurrentTabFragment=="+getCurrentTabFragment());
            if(f == null)
            {
            	LogUtils2.e("f==="+f);
                if(superOnBackPressed != null)
                    superOnBackPressed.run();
                else
                    mFragmentActivity.finish();
            } else
            if((f instanceof BaseFragment) && ((BaseFragment)f).onHostActivityBackPressed())
                System.out.println("Fragment handled onBackPressed().");
            else
            if(isRootFragment())
                mFragmentActivity.finish();
            else{
                LogUtils2.e("onFragmentActivityBackPressed888");
            	popFragment();
            	}
            }

        public void switchTab(String s)
        {
            super.switchTab(s);
        }

        public Fragment getCurrentTabFragment()
        {
            return super.getCurrentTabFragment();
        }

        public  int getPreviousTabIndex()
        {
            return super.getPreviousTabIndex();
        }

        public  void setSwitcherListener(FragmentTabSwitcherListener fragmenttabswitcherlistener)
        {
            super.setSwitcherListener(fragmenttabswitcherlistener);
        }

        public  String getCurrentTabId()
        {
            return super.getCurrentTabId();
        }

        public  int getTabCount()
        {
            return super.getTabCount();
        }

        public  int getCurrentTabIndex()
        {
            return super.getCurrentTabIndex();
        }

        public  FragmentActivity getFragmentActivity()
        {
            return super.getFragmentActivity();
        }

        public  String getPreviousTabId()
        {
            return super.getPreviousTabId();
        }

    }
    /////////////////////////

    public static interface FragmentTabSwitcherFeed
    {

        public abstract Fragment newRootFragment(String s);

        public abstract LinkedHashSet getRootFragmentTags();
    }

    public static interface FragmentTabSwitcherListener
    {

        public abstract void onTabSelected(String s, int i, String s1, int j);
    }

    /**
     * 
     * @author Hua
     *
     */
    public static class FragmentTabSwitcherWithoutZorder extends AbsFragmentTabSwitcher
    {

        public void switchTab(String rootFragmentTag, boolean skipSameTab)
        {
            if(rootFragmentTag == null)
                throw new NullPointerException("Fragment tag can NOT be null.");
            mPreviousRootFragmentTag = mCurrentRootFragmentTag;
            mCurrentRootFragmentTag = rootFragmentTag;
            if(skipSameTab && isSameTab())
            {
                Log.w(FragmentUtils3.TAG, (new StringBuilder(String.valueOf(getClass().getSimpleName()))).append(" no need to switch same tab : ").append(mCurrentRootFragmentTag).toString());
                if(mSwitcherListener != null)
                {
                    int tabIndex = getCurrentTabIndex();
                    int preTabIndex = getPreviousTabIndex();
                    Log.d(FragmentUtils3.TAG, (new StringBuilder("onTabSelected : ")).append(mCurrentRootFragmentTag).append(", ").append(tabIndex).append(", ").append(mPreviousRootFragmentTag).append(", ").append(preTabIndex).toString());
                    mSwitcherListener.onTabSelected(mCurrentRootFragmentTag, tabIndex, mPreviousRootFragmentTag, preTabIndex);
                }
                return;
            }
            Log.d(FragmentUtils3.TAG, (new StringBuilder(String.valueOf(getClass().getSimpleName()))).append(" switch tab : ").append(rootFragmentTag).toString());
            FragmentManager manager = mFragmentActivity.getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            if(mCurrentFragment != null)
            {
                Log.d(FragmentUtils3.TAG, (new StringBuilder("detach fragment : ")).append(mCurrentFragment).toString());
                ft.detach(mCurrentFragment);
            } else
            if(manager.findFragmentByTag(rootFragmentTag) != null)
                Log.w(FragmentUtils3.TAG, "Strange case : current is null but the coming one is not null. Have you re-assigned the switcher instance?");
            mCurrentFragment = manager.findFragmentByTag(rootFragmentTag);
            if(mCurrentFragment == null)
            {
                mCurrentFragment = mSwitcherFeed.newRootFragment(rootFragmentTag);
                Log.d(FragmentUtils3.TAG, (new StringBuilder("add fragment : ")).append(mCurrentFragment).toString());
                ft.add(mContainerId, mCurrentFragment, rootFragmentTag);
            } else
            {
                Log.d(FragmentUtils3.TAG, (new StringBuilder("re-attach fragment : ")).append(mCurrentFragment).toString());
                ft.attach(mCurrentFragment);
            }
            ft.commit();
            if(mSwitcherListener != null)
            {
                int tabIndex = getCurrentTabIndex();
                int preTabIndex = getPreviousTabIndex();
                Log.d(FragmentUtils3.TAG, (new StringBuilder("onTabSelected : ")).append(mCurrentRootFragmentTag).append(", ").append(tabIndex).append(", ").append(mPreviousRootFragmentTag).append(", ").append(preTabIndex).toString());
                mSwitcherListener.onTabSelected(mCurrentRootFragmentTag, tabIndex, mPreviousRootFragmentTag, preTabIndex);
            }
        }

        public  void switchTab(String s)
        {
            super.switchTab(s);
        }

        public  Fragment getCurrentTabFragment()
        {
            return super.getCurrentTabFragment();
        }

        public  int getPreviousTabIndex()
        {
            return super.getPreviousTabIndex();
        }

        public  void setSwitcherListener(FragmentTabSwitcherListener fragmenttabswitcherlistener)
        {
            super.setSwitcherListener(fragmenttabswitcherlistener);
        }

        public  String getCurrentTabId()
        {
            return super.getCurrentTabId();
        }

        public  int getTabCount()
        {
            return super.getTabCount();
        }

        public  int getCurrentTabIndex()
        {
            return super.getCurrentTabIndex();
        }

        public  FragmentActivity getFragmentActivity()
        {
            return super.getFragmentActivity();
        }

        public  String getPreviousTabId()
        {
            return super.getPreviousTabId();
        }

        public FragmentTabSwitcherWithoutZorder(FragmentActivity act, int containerId, FragmentTabSwitcherFeed feed)
        {
            super(act, containerId, feed);
        }
    }


    private FragmentUtils3()
    {
    }

    public static void printFragmentStates(Fragment f)
    {
        if(f == null)
        {
            return;
        } else
        {
            Log.d(TAG, (new StringBuilder(String.valueOf(f.getClass().getSimpleName()))).append(".isAdded() : ").append(f.isAdded()).toString());
            Log.d(TAG, (new StringBuilder(String.valueOf(f.getClass().getSimpleName()))).append(".isDetached() : ").append(f.isDetached()).toString());
            Log.d(TAG, (new StringBuilder(String.valueOf(f.getClass().getSimpleName()))).append(".isHidden() : ").append(f.isHidden()).toString());
            Log.d(TAG, (new StringBuilder(String.valueOf(f.getClass().getSimpleName()))).append(".isInLayout() : ").append(f.isInLayout()).toString());
            Log.d(TAG, (new StringBuilder(String.valueOf(f.getClass().getSimpleName()))).append(".isRemoving() : ").append(f.isRemoving()).toString());
            Log.d(TAG, (new StringBuilder(String.valueOf(f.getClass().getSimpleName()))).append(".isResumed() : ").append(f.isResumed()).toString());
            Log.d(TAG, (new StringBuilder(String.valueOf(f.getClass().getSimpleName()))).append(".isVisible() : ").append(f.isVisible()).toString());
            return;
        }
    }

    public static void add(FragmentActivity act, Fragment fragment, int containerId, String tag)
    {
        FragmentManager manager = act.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(containerId, fragment, tag);
        ft.commit();
    }

    public static void add(FragmentActivity act, Fragment fragment, int containerId, String tag, String backStackStateName)
    {
        FragmentManager manager = act.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(containerId, fragment, tag);
        ft.addToBackStack(backStackStateName);
        ft.commit();
    }

    public static void remove(FragmentActivity act, Fragment fragment)
    {
        FragmentManager manager = act.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    public static void remove(FragmentActivity act, Fragment fragment, String backStackStateName)
    {
        FragmentManager manager = act.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.remove(fragment);
        ft.addToBackStack(backStackStateName);
        ft.commit();
    }

    public static void replace(FragmentActivity act, Fragment fragment, int containerId, String tag)
    {
        FragmentManager manager = act.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(containerId, fragment, tag);
        ft.commit();
    }

    public static void replace(FragmentActivity act, Fragment fragment, int containerId, String tag, String backStackStateName)
    {
        FragmentManager manager = act.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(containerId, fragment, tag);
        ft.addToBackStack(backStackStateName);
        ft.commit();
    }

    public static void attach(FragmentActivity act, Fragment fragment)
    {
        FragmentManager manager = act.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.attach(fragment);
        ft.commit();
    }

    public static void attach(FragmentActivity act, Fragment fragment, String backStackStateName)
    {
        FragmentManager manager = act.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.attach(fragment);
        ft.addToBackStack(backStackStateName);
        ft.commit();
    }

    public static void detach(FragmentActivity act, Fragment fragment)
    {
        FragmentManager manager = act.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.detach(fragment);
        ft.commit();
    }

    public static void detach(FragmentActivity act, Fragment fragment, String backStackStateName)
    {
        FragmentManager manager = act.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.detach(fragment);
        ft.addToBackStack(backStackStateName);
        ft.commit();
    }

    public static void show(FragmentActivity act, Fragment fragment)
    {
        FragmentManager manager = act.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.show(fragment);
        ft.commit();
    }

    public static void hide(FragmentActivity act, Fragment fragment)
    {
        FragmentManager manager = act.getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.hide(fragment);
        ft.commit();
    }

    public static void setContentViewFragment(FragmentActivity act, int containerId, Fragment fragment, String tag)
    {
        act.getSupportFragmentManager().beginTransaction().add(containerId, fragment, tag).commit();
    }

    public static LinkedHashSet makeRootFragmentTags(String... tags)
    {
        if(tags == null || tags.length == 0)
            return null;
        LinkedHashSet tagSet = new LinkedHashSet(tags.length);
        int size = tags.length;
        for(int i = 0; i < size; i++)
            tagSet.add(tags[i]);

//        LogUtils2.i("tags[i]="+tags[0]+tags[1]+tags[2]);
        
        
        return tagSet;
    }

}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 51 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/