package com.hua.homefragment.subfragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.hua.activity.R;
import com.hua.adapter.HotFragmentListMsgAdapter;
import com.hua.contants.Constant;
import com.hua.model.HotInfos;

public class HotFragment2 extends Fragment implements OnItemClickListener{
	private ListView lv_hot;
	private HotFragmentListMsgAdapter hotAdapter;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_hot, null);
		return v;
	}

	

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		lv_hot = (ListView) view.findViewById(R.id.lv_hot);
		lv_hot.setOnItemClickListener(this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getHotData();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	private void getHotData() {
		List<HotInfos> list = new ArrayList<HotInfos>();
		for(int i = 0; i<Constant.hot_icon.length;i++){
			HotInfos info = new HotInfos();
			info.setImg_bg(Constant.hot_icon[i]);
			info.setTitle(Constant.hot_title[i]);
			info.setIntro(Constant.hot_intro[i]);
			list.add(info);
		}
		hotAdapter = new HotFragmentListMsgAdapter(getActivity(), list);
		lv_hot.setAdapter(hotAdapter);
		
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
//		HotInfos infos = hotAdapter.getItem(position);
//		Intent intent = new Intent();
//		intent.setClass(getActivity(), DynamicMsgInfoActivity.class);
//		Bundle bundle = new Bundle();
//		bundle.putInt("icon", infos.getImg_bg());
//		bundle.putString("title", infos.getTitle());
//		bundle.putString("intro", infos.getIntro());
//		intent.putExtras(bundle);
//		startActivity(intent);
	}
}
