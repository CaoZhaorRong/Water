package net.lzzy.water.frament;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.lzzy.water.R;


public class TakeFragment extends BaseFragment {

    private OnFragmentInteractionListener mListener;

    public TakeFragment() {
    }

    @Override
    protected void populate() {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_take;
    }

    @Override
    public void search(String kw) {

    }

    public static TakeFragment newInstance(String param1, String param2) {
        TakeFragment fragment = new TakeFragment();
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
