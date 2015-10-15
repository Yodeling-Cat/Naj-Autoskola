package com.spiraclestudios.autoskola;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.Tracker;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoznostiTestuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoznostiTestuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoznostiTestuFragment extends Fragment {
    private static final String TAG = "MoznostiTestuFragment";
    private String mFragmentName = "MoznostiTestuFragment";
    private Tracker mTracker;

    public final static String EXTRA_SKUPINA =
            "com.spiraclestudios.autoskola.MOZNOSTI_TESTU_SKUPINA";
    public final static String EXTRA_INDEX =
            "com.spiraclestudios.autoskola.MOZNOSTI_TESTU_INDEX";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_SKUPINA = "skupina";
    private static final String ARG_PARAM_INDEX = "index";

    // TODO: Rename and change types of parameters
    private long mParamSkupina;
    private long mParamIndex;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param skupina Parameter 1.
     * @param index Parameter 2.
     * @return A new instance of fragment MoznostiTestuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoznostiTestuFragment newInstance(long skupina, long index) {
        MoznostiTestuFragment fragment = new MoznostiTestuFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM_SKUPINA, skupina);
        args.putLong(ARG_PARAM_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    public MoznostiTestuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // [SetUp Activity]
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamSkupina = getArguments().getLong(ARG_PARAM_SKUPINA);
            mParamIndex = getArguments().getLong(ARG_PARAM_INDEX);
        }

        // Obtain the shared Tracker instance
        mTracker = ((AnalyticsApplication) getActivity().getApplication()).getDefaultTracker();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // [Inflate the layout]
        Helper.setTheme(getActivity());
        View view = inflater.inflate(R.layout.fragment_moznosti_testu, container, false);


        //[Výber testu - Start button]
        view.findViewById(R.id.zacat_test).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TestActivity.class);

                intent.putExtra(EXTRA_SKUPINA, mParamSkupina);
                intent.putExtra(EXTRA_INDEX, mParamIndex);
                startActivity(intent);
                getFragmentManager().popBackStackImmediate();

            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
