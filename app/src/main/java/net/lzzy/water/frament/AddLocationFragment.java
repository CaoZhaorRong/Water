package net.lzzy.water.frament;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hengyi.wheelpicker.ppw.CityWheelPickerPopupWindow;
import net.lzzy.water.R;
import net.lzzy.water.models.LocationFactory;
import net.lzzy.water.models.Locations;
import java.util.Objects;
import java.util.UUID;

/**
 * @author 菜鸡
 */
public class AddLocationFragment extends BaseFragment {

    public static final int RESULT_CODE_LOCATION = 3;
    public static final String PRESENT = "present";
    private LocationFragment.OnToAddLocationFragment listener;
    public static final String RESULT_LOCATION = "result";
    private boolean present = false;

    public AddLocationFragment() {
    }

    public static AddLocationFragment newInstance(boolean present) {
        //静态工厂方法
        AddLocationFragment fragment = new AddLocationFragment();
        Bundle args = new Bundle();
        args.putBoolean(PRESENT,present);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            present = getArguments().getBoolean(PRESENT);
        }
    }
    @Override
    protected void populate() {
        getAddress();
    }

    private void getAddress() {
        ImageView ivBack = findViewById(R.id.l_back);
        ivBack.setOnClickListener(view ->{
            if (present){
                getActivity().onBackPressed();
            }else {
                listener.onBackLocationFragment();
            }
        });
        TextView tvSave = findViewById(R.id.l_save);
        EditText edtName = findViewById(R.id.l_username);
        EditText edtPhone= findViewById(R.id.l_phone);
        EditText ediLocations = findViewById(R.id.l_locations);

        ediLocations.setOnClickListener(view1 -> {
            CityWheelPickerPopupWindow wheelPickerPopupWindow = new CityWheelPickerPopupWindow(Objects.requireNonNull(getActivity()));
            wheelPickerPopupWindow.setListener((province, city, district, postCode) ->{
                ediLocations.setText(province + city + district);
            });
            wheelPickerPopupWindow.show();
        });
        EditText edtAddress =findViewById(R.id.l_address);
        tvSave.setOnClickListener(view -> {
            String name = edtName.getText().toString();
            String phone = edtPhone.getText().toString();
            String address = edtAddress.getText().toString();
            String location = ediLocations.getText().toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)|| TextUtils.isEmpty(address)|| TextUtils.isEmpty(location)) {
                Toast.makeText(getContext(), "信息不完整", Toast.LENGTH_SHORT).show();
            }else {
                String locations  = location.concat(address);
                Locations l = new Locations();
                l.setId(UUID.randomUUID());
                l.setAddress(locations);
                l.setName(name);
                l.setTelephone(phone);
                LocationFactory.insert(l);
                if (present){
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_LOCATION,l);
                    getActivity().setResult(RESULT_CODE_LOCATION, intent);
                    getActivity().finish();
                }else {
                    listener.onBackLocationFragment();
                }
            }
        });
    }

    @Override
    public int getLayout() {
        return R.layout.locations;
    }

    @Override
    public void search(String kw) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LocationFragment.OnToAddLocationFragment) {
            listener = (LocationFragment.OnToAddLocationFragment) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
