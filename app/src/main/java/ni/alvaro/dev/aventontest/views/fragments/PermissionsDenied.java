package ni.alvaro.dev.aventontest.views.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import ni.alvaro.dev.aventontest.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PermissionsDenied.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PermissionsDenied#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PermissionsDenied extends Fragment {

    private static final String MESSAGE_PARAM = "MESSAGE_PARAM";
    private OnFragmentInteractionListener mListener;
    private int messageRes;

    public PermissionsDenied() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param errorMessageRes Res representing message to be shown.
     * @return A new instance of fragment PermissionsDenied.
     */
    public static PermissionsDenied newInstance(int errorMessageRes) {
        PermissionsDenied fragment = new PermissionsDenied();
        Bundle args = new Bundle();
        args.putInt(MESSAGE_PARAM, errorMessageRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            messageRes = getArguments().getInt(MESSAGE_PARAM);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_permissions_denied, container, false);
        ((TextView)v.findViewById(R.id.messageTxt)).setText(messageRes);
        return v;
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
