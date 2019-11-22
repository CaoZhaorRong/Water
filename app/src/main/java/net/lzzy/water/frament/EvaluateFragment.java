package net.lzzy.water.frament;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;


import net.lzzy.water.R;


/**
 * @author 菜鸡
 */
public class EvaluateFragment extends BaseFragment {
    private OnFragmentInteractionListener mListener;

    public EvaluateFragment() {
    }

    @Override
    protected void populate() {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_evaluate;
    }

    @Override
    public void search(String kw) {

    }

    public static EvaluateFragment newInstance(String param1, String param2) {
        EvaluateFragment fragment = new EvaluateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
