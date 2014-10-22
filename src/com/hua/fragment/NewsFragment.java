
package com.hua.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.hua.activity.DetailsActivity_;
import com.hua.activity.ImageDetailActivity_;
import com.hua.activity.R;
import com.hua.adapter.CardsAnimationAdapter;
import com.hua.adapter.NewAdapter;
import com.hua.app.BaseActivity2;
import com.hua.app.BaseFragment2;
import com.hua.bean.NewModle;
import com.hua.contants.Url;
import com.hua.initview.InitView;
import com.hua.network.http.json.NewListJson;
import com.hua.network.utils.HttpUtil;
import com.hua.utils.LogUtils2;
import com.hua.utils.StringUtils;
import com.hua.wedget.swiptlistview.SwipeListView;
import com.hua.wedget.viewimage.Animations.DescriptionAnimation;
import com.hua.wedget.viewimage.Animations.SliderLayout;
import com.hua.wedget.viewimage.SliderTypes.BaseSliderView;
import com.hua.wedget.viewimage.SliderTypes.BaseSliderView.OnSliderClickListener;
import com.hua.wedget.viewimage.SliderTypes.TextSliderView;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.umeng.analytics.MobclickAgent;

@EFragment(R.layout.activity_main)
public class NewsFragment extends BaseFragment2 implements SwipeRefreshLayout.OnRefreshListener,
        OnSliderClickListener {
	/**
	 * 头部的横幅滑动布局
	 */
    protected SliderLayout mDemoSlider;
    /**
     *新的Components SwipeRefreshLayout 类似ScrollView
     */
    @ViewById(R.id.swipe_container)
    protected SwipeRefreshLayout swipeLayout;
    /**
     * 整个布局的listview
     */
    @ViewById(R.id.listview)
    protected SwipeListView mListView;
    @ViewById(R.id.progressBar)
    protected ProgressBar mProgressBar;
    protected HashMap<String, String> url_maps;

    protected HashMap<String, NewModle> newHashMap;

    @Bean  
    protected NewAdapter newAdapter;
    protected List<NewModle> listsModles;
    private int index = 0;
    private boolean isRefresh = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @AfterInject
    protected void initVariate() {

        listsModles = new ArrayList<NewModle>();
        url_maps = new HashMap<String, String>();
        newHashMap = new HashMap<String, NewModle>();
    }

    @AfterViews
    protected void initView() {
        swipeLayout.setOnRefreshListener(this);
        InitView.instance().initSwipeRefreshLayout(swipeLayout);
        InitView.instance().initListView(mListView, getActivity());
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.head_item, null);
        mDemoSlider = (SliderLayout) headView.findViewById(R.id.slider);
        mListView.addHeaderView(headView);
        /**
         * 开发快捷键 ctrl + t 可以查看当前类的结构 包括有那些子类 父类等
         */
//        AnimationAdapter animationAdapter = new CardsAnimationAdapter(newAdapter);
//        animationAdapter.setAbsListView(mListView);
        AnimationAdapter mAnimationAdapter = new CardsAnimationAdapter(newAdapter);
        mAnimationAdapter.setAbsListView(mListView);
        mListView.setAdapter(mAnimationAdapter);
       
        loadData(getNewUrl(index + ""));

        mListView.setOnBottomListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPagte++;
                index = index + 20;
                loadData(getNewUrl(index + ""));
            }
        });
    }

    /**
     * 加载数据 
     * @param url
     */
    private void loadData(String url) {
        if (getMyActivity().hasNetWork()) {
            loadNewList(url);
        } else {
            mListView.onBottomComplete();
            mProgressBar.setVisibility(View.GONE);
            getMyActivity().showShortToast(getString(R.string.not_network));
            String result = getMyActivity().getCacheStr("NewsFragment" + currentPagte);
            if (!StringUtils.isEmpty(result)) {
                getResult(result);
            }
        }
    }

    /**
     * 一开始初始化 
     * @param newModles
     */
    private void initSliderLayout(List<NewModle> newModles) {

        if (!isNullString(newModles.get(0).getImgsrc()))
            newHashMap.put(newModles.get(0).getImgsrc(), newModles.get(0));
        if (!isNullString(newModles.get(1).getImgsrc()))
            newHashMap.put(newModles.get(1).getImgsrc(), newModles.get(1));
        if (!isNullString(newModles.get(2).getImgsrc()))
            newHashMap.put(newModles.get(2).getImgsrc(), newModles.get(2));
        if (!isNullString(newModles.get(3).getImgsrc()))
            newHashMap.put(newModles.get(3).getImgsrc(), newModles.get(3));

        if (!isNullString(newModles.get(0).getImgsrc()))
            url_maps.put(newModles.get(0).getTitle(), newModles.get(0).getImgsrc());
        if (!isNullString(newModles.get(1).getImgsrc()))
            url_maps.put(newModles.get(1).getTitle(), newModles.get(1).getImgsrc());
        if (!isNullString(newModles.get(2).getImgsrc()))
            url_maps.put(newModles.get(2).getTitle(), newModles.get(2).getImgsrc());
        if (!isNullString(newModles.get(3).getImgsrc()))
            url_maps.put(newModles.get(3).getTitle(), newModles.get(3).getImgsrc());

        for (String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            textSliderView.setOnSliderClickListener(this);
            textSliderView
                    .description(name)
                    .image(url_maps.get(name));

            textSliderView.getBundle()
                    .putString("extra", name);
            mDemoSlider.addSlider(textSliderView);
        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        newAdapter.appendList(newModles);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                currentPagte = 1;
                isRefresh = true;
                loadData(getNewUrl("0"));
                index = 0;
                url_maps.clear();
                mDemoSlider.removeAllSliders();
            }
        }, 2000);
    }

    @ItemClick(R.id.listview)
    protected void onItemClick(int position) {
        NewModle newModle = listsModles.get(position - 1);
        enterDetailActivity(newModle);
    }

    /**
     * 点解item后跳转到详细页面
     * @param newModle
     */
    public void enterDetailActivity(NewModle newModle) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("newModle", newModle);
        Class<?> class1;
//        Class<?> class2;
       if (newModle.getImagesModle() != null && newModle.getImagesModle().getImgList().size() > 1) {
    	   LogUtils2.d("******ImageDetailActivity_----------------");
            class1 = ImageDetailActivity_.class;
        } else {
        	 LogUtils2.i("******** DetailsActivity_.class**********");
            class1 = DetailsActivity_.class;
        }
        ((BaseActivity2) getActivity()).openActivity(class1,
                bundle, 0);
        // Intent intent = new Intent(getActivity(), class1);
        // intent.putExtras(bundle);
        // IntentUtils.startPreviewActivity(getActivity(), intent);
    }

    /**
     * 相当于用AsynTask 进行异步加载
     * @param url
     */
    @Background
    void loadNewList(String url) {
        String result;
        try {
            result = HttpUtil.getByHttpClient(getActivity(), url);
            LogUtils2.i("result=="+result);
            getResult(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在主线程中 实现数据显示
     * @param result
     */
    @UiThread
    public void getResult(String result) {
        getMyActivity().setCacheStr("NewsFragment" + currentPagte, result);
        if (isRefresh) {
            isRefresh = false;
            newAdapter.clear();
            listsModles.clear();
            LogUtils2.d("Index=="+index);
        }
        mProgressBar.setVisibility(View.GONE);
        swipeLayout.setRefreshing(false);
        List<NewModle> list =
                NewListJson.instance(getActivity()).readJsonNewModles(result,
                        Url.TopId);  
        if (index == 0) {
            initSliderLayout(list);
        } else {
            newAdapter.appendList(list);
        }
        listsModles.addAll(list);
        mListView.onBottomComplete();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        NewModle newModle = newHashMap.get(slider.getUrl());
        enterDetailActivity(newModle);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen"); // 统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }
}
