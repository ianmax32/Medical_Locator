package com.naitech.medicalLocator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Medical_aid.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Medical_aid#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Medical_aid extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spinner medicalAidFund;
    private Spinner medicalAidOptionPlan;
    String medicalAidUsed ="";

    private OnFragmentInteractionListener mListener;

    public Medical_aid() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Medical_aid.
     */
    // TODO: Rename and change types and number of parameters
    public static Medical_aid newInstance(String param1, String param2) {
        Medical_aid fragment = new Medical_aid();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medical_aid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<String> medicalAidFunds = new ArrayList<>();
        List<String> medicalAidOptionPlans = new ArrayList<>();
        medicalAidFund = (Spinner) view.findViewById(R.id.medicalAidFund);
        medicalAidOptionPlan = view.findViewById(R.id.medicalAidOptionPlan);

        medicalAidFunds.add("AECI MEDICAL AID SOCIETY");
        medicalAidFunds.add("ALLIANCE-MIDMED MEDICAL SCHEME");
        medicalAidFunds.add("ANGLO MEDICAL SCHEME");
        medicalAidFunds.add("BANKMED");
        medicalAidFunds.add("BESTMED MEDICAL SCHEMEE");
        medicalAidFunds.add("DISCOVERY HEALTH MEDICAL SCHEME");
        medicalAidFunds.add("FEDHEALTH MEDICAL SCHEME");
        medicalAidFunds.add("GOVERNMENT EMPLOYEES MEDICAL SCHEME (GEMS)");
        medicalAidFunds.add("MOMENTUM MEDICAL SCHEME");
        medicalAidFunds.add("NETCARE MEDICAL SCHEME");

        //more to be added


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(view.getContext(),R.layout.item,medicalAidFunds);
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_multichoice);
        medicalAidFund.setAdapter(arrayAdapter);

        medicalAidFund.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0: medicalAidUsed = parent.getSelectedItem().toString();break;
                    case 1: medicalAidUsed = parent.getSelectedItem().toString();break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
