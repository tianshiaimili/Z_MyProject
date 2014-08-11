/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   FragmentUtils.java

package com.hua.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

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

public class FragmentUtils
{
	 private static final String TAG = "com/pccw/gzmobile/app/FragmentUtils.getSimpleName()";
	
    private static abstract class AbsFragmentTabSwitcher
    {
        public abstract void switchTab(String s, boolean flag);

        public static final int INVALID_TAB_INDEX = -1;
        protected FragmentActivity mFragmentActivity;
        protected int mContainerId;
        protected FragmentTabSwitcherFeed mSwitcherFeed;
        //是HashMap的一个子类，保存了记录的插入顺序，在用Iterator遍历LinkedHashMap时，先得到的记录肯定是先插入的.
        protected LinkedHashMap mRootFragmentTags;//这个hashmap是一头进一头出的 
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
         * 获取当前选中的下标
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
         * 获取上一个的index
         * @return
         */
        public int getPreviousTabIndex()
        {
            return mPreviousRootFragmentTag == null ? -1 : ((Integer)mRootFragmentTags.get(mPreviousRootFragmentTag)).intValue();
        }

        /**
         * 判断是否同一个tab标签
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

            Log.w(FragmentUtils.TAG, (new StringBuilder("FragmentTabSwitcher root fragment tags : ")).append(mRootFragmentTags).toString());
            mTabCount = mRootFragmentTags.size();
            String tag;
            for(tagSetIterator = tagSet.iterator(); tagSetIterator.hasNext(); mTabStacks.put(tag, new LinkedList()))
                tag = (String)tagSetIterator.next();

            Log.w(FragmentUtils.TAG, (new StringBuilder("FragmentTabSwitcher tab stacks : ")).append(mTabStacks).toString());
        }
    }

    /**
     * 一个控制下边tab的类
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
         * 添加tab 后刷新
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
         * 相当于添加新的tab
         */
        public void switchTab(String rootFragmentTag, boolean skipSameTab)
        {
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
                Log.w(FragmentUtils.TAG, (new StringBuilder(String.valueOf(getClass().getSimpleName()))).append(" no need to switch same tab : ").append(mCurrentRootFragmentTag).toString());
                if(mSwitcherListener != null)
                {
                    int tabIndex = getCurrentTabIndex();
                    int preTabIndex = getPreviousTabIndex();
                    Log.d(FragmentUtils.TAG, (new StringBuilder("onTabSelected : ")).append(mCurrentRootFragmentTag).append(", ").append(tabIndex).append(", ").append(mPreviousRootFragmentTag).append(", ").append(preTabIndex).toString());
                    mSwitcherListener.onTabSelected(mCurrentRootFragmentTag, tabIndex, mPreviousRootFragmentTag, preTabIndex);
                }
                return;
            }
            Log.d(FragmentUtils.TAG, (new StringBuilder(String.valueOf(getClass().getSimpleName()))).append(" switch tab : ").append(rootFragmentTag).toString());
           
            if(getCurrentTabStack().size() == 0)
                pushFragment(true, new Fragment[] {
                    mSwitcherFeed.newRootFragment(rootFragmentTag)
                });
            else
                pushFragment(false, new Fragment[] {
                    peekTopmostFragment()
                });
            if(mSwitcherListener != null)
            {
                int tabIndex = getCurrentTabIndex();
                int preTabIndex = getPreviousTabIndex();
                Log.d(FragmentUtils.TAG, (new StringBuilder("onTabSelected : ")).append(mCurrentRootFragmentTag).append(", ").append(tabIndex).append(", ").append(mPreviousRootFragmentTag).append(", ").append(preTabIndex).toString());
                mSwitcherListener.onTabSelected(mCurrentRootFragmentTag, tabIndex, mPreviousRootFragmentTag, preTabIndex);
            }
        }

        /**
         * 存放TabfragmentTag的LinkedList
         * 里面的mTabStacks 是一个LinkedHashMap 他存放格式是（“Tag”，LinkedList）
         * tag就是界面下面的那5个tab
         * @return
         */
        private LinkedList getCurrentTabStack()
        {
        	LogUtils2.w("mCurrentRootFragmentTag==="+mCurrentRootFragmentTag);
            if(mCurrentRootFragmentTag == null)
            {
                Log.w(FragmentUtils.TAG, "Please call switchTab() first before using other methods.");
                return null;
            }
            LinkedList tab = (LinkedList)mTabStacks.get(mCurrentRootFragmentTag);
            if(tab == null)
            {
                Log.w(FragmentUtils.TAG, (new StringBuilder("Can NOT find the the tab with key ")).append(mCurrentRootFragmentTag).toString());
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
                Log.w(FragmentUtils.TAG, "Current tab stack is empty.");
            return ret;
        }

        /**
         * 从getCurrentTabStack（）获取到第一个fragment
         * @return
         */
        public Fragment peekTopmostFragment()
        {
            if(isCurrentTabStackEmpty())
                return null;
            else
                return mFragmentActivity.getSupportFragmentManager().findFragmentByTag((String)getCurrentTabStack().getFirst());
        }

        /**
         * 往getCurrentTabStack（）中添加fragment或者...
         * @param add 判断是添加还是绑缚到fragment
         * @param fragments 需要添加的fragment
         */
        private void pushFragment(boolean add, Fragment fragments[])
        {
            FragmentManager manager = mFragmentActivity.getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            Fragment afragment[];
            int j = (afragment = fragments).length;
            for(int i = 0; i < j; i++)
            {
                Fragment fragment = afragment[i];
                if(mCurrentFragment != null)
                {
                    Log.d(FragmentUtils.TAG, (new StringBuilder("Detach fragment ")).append(mCurrentFragment.getTag()).toString());
                   /**
                    * detach()会将view从viewtree中删除,和remove()不同,
                    * 此时fragment的状态依然保持着,在使用attach()时会再次调用onCreateView()
                    * 来重绘视图,注意使用detach()
                    * 后fragment.isAdded()方法将返回false,在使用attach()
                    * 还原fragment后isAdded()会依然返回false(需要再次确认)
                    */
                    LogUtils2.i("pushFragment*******");
                    ft.detach(mCurrentFragment);
                }
                if(add)
                {
                	//例如 "TV - 3"
                    String fragmentTag = (new StringBuilder(String.valueOf(mCurrentRootFragmentTag))).append("-").append(getCurrentTabStack().size()).toString();
                    Log.d(FragmentUtils.TAG, (new StringBuilder("Add new fragment ")).append(fragmentTag).toString());
                    ft.add(mContainerId, fragment, fragmentTag);
                    getCurrentTabStack().addFirst(fragmentTag);
                } else
                {
                	LogUtils2.e("pushFragment_____________");
                    Log.d(FragmentUtils.TAG, (new StringBuilder("Attach fragment ")).append(fragment.getTag()).toString());
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
         * 根绝Tag获取到当前在LinkedList的首个fragment
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
                Log.d(FragmentUtils.TAG, (new StringBuilder("Remove fragment ")).append(tag).toString());
                return mFragmentActivity.getSupportFragmentManager().findFragmentByTag(tag);
            }
        }

        /**
         * 移除当前的首个fragment
         * @return
         */
        public Fragment popFragment()
        {
            FragmentManager manager = mFragmentActivity.getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.remove(popTopmostFragment());
            mCurrentFragment = peekTopmostFragment();
            ft.attach(mCurrentFragment);
            Log.d(FragmentUtils.TAG, (new StringBuilder("Attach fragment ")).append(mCurrentFragment.getTag()).toString());
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
                Log.d(FragmentUtils.TAG, (new StringBuilder("Attach fragment ")).append(mCurrentFragment.getTag()).toString());
                ft.commit();
            }
            return mCurrentFragment;
        }

        public boolean isRootFragment()
        {
            return getCurrentTabStack().size() == 1;
        }

        /**
         * 返回上一个fragmenttab
         */
        public void onFragmentActivityBackPressed()
        {
            onFragmentActivityBackPressed(null);
        }

        
        public void onFragmentActivityBackPressed(Runnable superOnBackPressed)
        {
            Fragment f = getCurrentTabFragment();
            LogUtils2.d("Currtent=size=="+getCurrentTabStack().size());
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
                Log.w(FragmentUtils.TAG, (new StringBuilder(String.valueOf(getClass().getSimpleName()))).append(" no need to switch same tab : ").append(mCurrentRootFragmentTag).toString());
                if(mSwitcherListener != null)
                {
                    int tabIndex = getCurrentTabIndex();
                    int preTabIndex = getPreviousTabIndex();
                    Log.d(FragmentUtils.TAG, (new StringBuilder("onTabSelected : ")).append(mCurrentRootFragmentTag).append(", ").append(tabIndex).append(", ").append(mPreviousRootFragmentTag).append(", ").append(preTabIndex).toString());
                    mSwitcherListener.onTabSelected(mCurrentRootFragmentTag, tabIndex, mPreviousRootFragmentTag, preTabIndex);
                }
                return;
            }
            Log.d(FragmentUtils.TAG, (new StringBuilder(String.valueOf(getClass().getSimpleName()))).append(" switch tab : ").append(rootFragmentTag).toString());
            FragmentManager manager = mFragmentActivity.getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            if(mCurrentFragment != null)
            {
                Log.d(FragmentUtils.TAG, (new StringBuilder("detach fragment : ")).append(mCurrentFragment).toString());
                ft.detach(mCurrentFragment);
            } else
            if(manager.findFragmentByTag(rootFragmentTag) != null)
                Log.w(FragmentUtils.TAG, "Strange case : current is null but the coming one is not null. Have you re-assigned the switcher instance?");
            mCurrentFragment = manager.findFragmentByTag(rootFragmentTag);
            if(mCurrentFragment == null)
            {
                mCurrentFragment = mSwitcherFeed.newRootFragment(rootFragmentTag);
                Log.d(FragmentUtils.TAG, (new StringBuilder("add fragment : ")).append(mCurrentFragment).toString());
                ft.add(mContainerId, mCurrentFragment, rootFragmentTag);
            } else
            {
                Log.d(FragmentUtils.TAG, (new StringBuilder("re-attach fragment : ")).append(mCurrentFragment).toString());
                ft.attach(mCurrentFragment);
            }
            ft.commit();
            if(mSwitcherListener != null)
            {
                int tabIndex = getCurrentTabIndex();
                int preTabIndex = getPreviousTabIndex();
                Log.d(FragmentUtils.TAG, (new StringBuilder("onTabSelected : ")).append(mCurrentRootFragmentTag).append(", ").append(tabIndex).append(", ").append(mPreviousRootFragmentTag).append(", ").append(preTabIndex).toString());
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


    private FragmentUtils()
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

    public static LinkedHashSet makeRootFragmentTags(String[] tags)
    {
        if(tags == null || tags.length == 0)
            return null;
        LinkedHashSet tagSet = new LinkedHashSet(tags.length);
        int size = tags.length;
        for(int i = 0; i < size; i++)
            tagSet.add(tags[i]);

        LogUtils2.i("tags[i]="+tags[0]+tags[1]+tags[2]);
        
        
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