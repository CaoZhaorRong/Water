package net.lzzy.water.frament;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.hjq.toast.ToastUtils;

import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;
import net.lzzy.water.R;
import net.lzzy.water.models.LocationFactory;
import net.lzzy.water.models.Locations;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * @author 菜鸡
 */
public class LocationFragment extends BaseFragment {
    private ListView iv;
    private List<Locations> locations;
    private OnToAddLocationFragment listener;
    private LocalBroadcastManager broadcastManager;
    private GenericAdapter<Locations> adapter;

    public LocationFragment() {
    }
    @Override
    protected void populate() {
        locations = LocationFactory.getLocations();
       // notification();
        initView();
        showLocation();
    }

    private void showLocation() {
        adapter = new GenericAdapter<Locations>(getContext(), R.layout.location_item, locations) {
            @Override
            public void populate(ViewHolder holder, Locations location) {
                holder.getView(R.id.location_view).setVisibility(View.VISIBLE);
                holder.getView(R.id.location_layout).setVisibility(View.VISIBLE);
                holder.setTextView(R.id.location_name, location.getName())
                        .setTextView(R.id.location_location, location.getAddress())
                        .setTextView(R.id.location_phone, location.getTelephone());
                TextView tvBtn = holder.getView(R.id.location_delete);
                tvBtn.setOnClickListener(view -> {

                    int i =LocationFactory.delete(location.getId());
                    if (i==1){
                        ToastUtils.show("删除地址");
                        locations = LocationFactory.getLocations();
                        showLocation();
                    }
                });
            }
            @Override
            public boolean persistInsert(Locations locations) {
                return false;
            }

            @Override
            public boolean persistDelete(Locations locations) {
                return false;
            }
        };
        iv.setAdapter(adapter);
    }

    private void initView() {
        iv = findViewById(R.id.location_iv);
        ImageView back = findViewById(R.id.activity_location_back);
        back.setOnClickListener(view -> getActivity().onBackPressed());
        TextView add = findViewById(R.id.add_location);
        add.setOnClickListener(view -> {
            listener.onToAddLocationFragment();
        });
    }
    @Override
    public int getLayout() {
        return R.layout.fragment_location;
    }

    @Override
    public void search(String kw) {
    }

    private void notification(){
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
       // intentFilter.addAction(LL);
        broadcastManager.registerReceiver(mAdDownLoadReceiver, intentFilter);
    }
    private BroadcastReceiver mAdDownLoadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            locations = LocationFactory.getLocations();
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        //broadcastManager.unregisterReceiver(mAdDownLoadReceiver);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnToAddLocationFragment) {
            listener = (OnToAddLocationFragment) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnToAddLocationFragment{

        /**
         * 通知刷新
         */
        void onToAddLocationFragment();
        void onBackLocationFragment();
    }

}
