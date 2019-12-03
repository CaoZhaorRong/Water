package net.lzzy.water.activites;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import net.lzzy.water.R;
import net.lzzy.water.frament.AddLocationFragment;
import net.lzzy.water.frament.LocationFragment;
import static net.lzzy.water.activites.SubmitActivity.PRESENT;

/**
 * @author 菜鸡
 */
public class LocationActivity extends BaseActivity implements LocationFragment.OnToAddLocationFragment {

    private boolean present;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        present = getIntent().getBooleanExtra(PRESENT,false);
        if (present){
            getManager().beginTransaction().replace(R.id.fragment_location,AddLocationFragment.newInstance(true)).commit();
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (present){
            finish();
        }else {
            onBackLocationFragment();
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_location;
    }

    @Override
    public int getContainerId() {
        return R.id.fragment_location;
    }

    @Override
    public Fragment createFragment() {
        return new LocationFragment();
    }

    @Override
    public void onToAddLocationFragment() {
        getManager().beginTransaction().replace(R.id.fragment_location,AddLocationFragment.newInstance(present)).commit();
    }
    @Override
    public void onBackLocationFragment() {
        getManager().beginTransaction().replace(R.id.fragment_location,new LocationFragment()).commit();
    }

}
