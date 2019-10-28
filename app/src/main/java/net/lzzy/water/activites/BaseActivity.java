package net.lzzy.water.activites;

import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.lzzy.water.utils.AppUtils;
import net.lzzy.water.utils.StatusBarUtil;

/**
 * @author lzzy_gxy
 * @date 2019/4/11
 * Description:
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Fragment fragment;
    private FragmentManager manager;

    public BaseActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setStatusBar();
        setContentView(getLayout());
        AppUtils.addActivity(this);
        manager = getSupportFragmentManager();
        fragment = manager.findFragmentById(getContainerId());
        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction().add(getContainerId(), fragment).commit();
            //事务 manager.beginTransaction()
        }

    }
    protected void setStatusBar() {
        //这里做了两件事情，1.使状态栏透明并使contentView填充到状态栏 2.预留出状态栏的位置，防止界面上的控件离顶部靠的太近。这样就可以实现开头说的第二种情况的沉浸式状态栏了
        StatusBarUtil.setTransparent(this);
    }

    protected FragmentManager getManager(){
        return manager;
    }

    protected Fragment getFragment(){
        return fragment;
    }

    /**
     * 绑定布局
     * @return id
     */
    public abstract int getLayout();

    /**
     * 绑定容器id
     * @return id
     */
    public abstract int getContainerId();

    /**创建新的Fragment对象
     * @return null
     */
    public abstract Fragment createFragment();


    //region 赋值

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.setRunning(getLocalClassName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppUtils.setStopped(getLocalClassName());
    }

    //endregion

}
