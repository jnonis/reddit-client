package com.example.redditclient.ui.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.redditclient.R;

/**
 * Dialog which shows an error message.
 */
public class ErrorDialogFragment extends DialogFragment {
    /** Message resource argument. */
    protected static final String ARG_ERROR_MESSAGE_RES =
            "ARG_ERROR_MESSAGE_RES";
    /** Argument that indicates if the dialog should finish the activity. */
    protected static final String ARG_FINISH = "ARG_FINISH";
    /** Message resource. */
    private int mErrorMessageRes;
    /** Indicates if the dialog should finish the activity. */
    private boolean mFinish;

    /** Create a new instance.
     *
     * @param resErrorMessage The resource error message that will be displayed to the user.
     * @param finish Whether the activity should be finished after showing the error.
     */
    public static ErrorDialogFragment newInstance(int resErrorMessage,
            boolean finish) {
        Bundle args = new Bundle();
        args.putInt(ARG_ERROR_MESSAGE_RES, resErrorMessage);
        args.putBoolean(ARG_FINISH, finish);
        ErrorDialogFragment fragment = new ErrorDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mErrorMessageRes = getArguments().getInt(ARG_ERROR_MESSAGE_RES);
        mFinish = getArguments().getBoolean(ARG_FINISH);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String errorMessage = getString(mErrorMessageRes);
        AlertDialog dialog = new AlertDialog.Builder(getActivity(), getTheme())
                .setMessage(errorMessage)
                .setPositiveButton(R.string.button_ok,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mFinish && getActivity() != null) {
            getActivity().finish();
        }
    }
}
