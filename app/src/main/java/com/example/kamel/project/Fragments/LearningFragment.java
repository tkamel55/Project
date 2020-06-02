package com.example.kamel.project.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kamel.project.Adapter.ProductAdapter;
import com.example.kamel.project.Model.Product;
import com.example.kamel.project.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LearningFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LearningFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LearningFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;
    List<Product> productList;

    //the recyclerview
    RecyclerView recyclerView;


    public LearningFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LearningFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LearningFragment newInstance(String param1, String param2) {
        LearningFragment fragment = new LearningFragment();
        Bundle args = new Bundle();


        fragment.setArguments(args);
        return fragment;


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing the productlist
        productList = new ArrayList<>();


        //adding some important book items to the list
        productList.add(
                new Product(
                        1,
                        "Types,Variables And Oprators\n",
                        60000,
                        R.drawable.books,
                        "https://ftms.edu.my/v2/wp-content/uploads/2019/02/PROG0101_CH04.pdf"

                ));


        productList.add(
                new Product(
                        1,
                        "Loops, Arrays ",


                        60000,
                        R.drawable.books,
                        "https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-092-introduction-to-programming-in-java-january-iap-2010/lecture-notes/MIT6_092IAP10_lec03.pdf  "
                ));

        productList.add(
                new Product(
                        1,
                        "Classes and Objects",
                        60000,
                        R.drawable.books,
                         "http://www.cloudbus.org/~raj/254/Lectures/Lecture7.pdf"
                ));
        productList.add(
                new Product(
                        1,
                        "Access Control,Java API, Classes",
                        60000,
                        R.drawable.books,
                        "https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-092-introduction-to-programming-in-java-january-iap-2010/lecture-notes/MIT6_092IAP10_lec05.pdf"
                ));
        productList.add(
                new Product(
                        1,
                        " Design, Debugging, Interfaces",
                        60000,
                        R.drawable.books,
                       "https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-092-introduction-to-programming-in-java-january-iap-2010/lecture-notes/MIT6_092IAP10_lec06.pdf"

                ));
        productList.add(
                new Product(
                        1,
                        "Inheritance, exceptions, file I/O\t",
                        60000,
                        R.drawable.books,
                        "https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-092-introduction-to-programming-in-java-january-iap-2010/lecture-notes/MIT6_092IAP10_lec07.pdf"
                ));

        productList.add(
                new Product(
                        1,
                        "Strings",
                        60000,
                        R.drawable.books,
                      "https://iith.ac.in/~rogers/pds_theory/lect20.pdf"
                ));









    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_learning, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //creating recyclerview adapter
        ProductAdapter adapter = new ProductAdapter(getActivity(), productList);
        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);

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
