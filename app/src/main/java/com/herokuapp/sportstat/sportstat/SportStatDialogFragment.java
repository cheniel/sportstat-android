package com.herokuapp.sportstat.sportstat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;


import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//MyRunsDialogFragment handles all the customized dialog boxes in our project.
//Differentiated by dialog id.
// 
// Ref: http://developer.android.com/reference/android/app/DialogFragment.html
public class SportStatDialogFragment extends DialogFragment {

    // Different dialog IDs
    public static final int DIALOG_ID_ERROR = -1;
    public static final int DIALOG_ID_PHOTO_PICKER = 9;
    public static final int DIALOG_ID_DATE_PICKER = 0;
    public static final int DIALOG_ID_TIME_PICKER = 1;
    public static final int DIALOG_ID_ASSISTS_ALERT = 2;
    public static final int DIALOG_ID_TWO_POINTS_ALERT = 3;
    public static final int DIALOG_ID_THREE_POINTS_ALERT = 4;
    public static final int DIALOG_ID_SHOTS_ATTEMPTED_ALERT = 5;
    public static final int DIALOG_ID_COMMENT_ALERT = 6;
    private static final String TAG = "tag";

    private String okButtonString = "Ok";
    private String cancelButtonString = "Cancel";



    // For photo picker selection:
    public static final int ID_PHOTO_PICKER_FROM_CAMERA = 0;
    public static final int ID_PHOTO_PICKER_FROM_GALLERY = 1;

    private static final String DIALOG_ID_KEY = "dialog_id";
    private static LogGameFragment mParentFragment;

    //A date object to store user-entered date and time
    private Date date;


    //Creating database helper object

    //Creating a datasource helper object

    private boolean dateInitialized;


    public static SportStatDialogFragment newInstance(int dialog_id, LogGameFragment parentFrag) {
        SportStatDialogFragment frag = new SportStatDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ID_KEY, dialog_id);
        frag.setArguments(args);
        mParentFragment = parentFrag;

        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int dialog_id = getArguments().getInt(DIALOG_ID_KEY);

        //Initialize the date object
        date = new Date();



        final Fragment parent = mParentFragment;
                //MainActivity.getFragmentManager().findFragmentById(R.id.log_game_fragment_id);
        // Setup dialog appearance and onClick Listeners
        switch (dialog_id) {
            //The following case is based on the Camera example app
            case DIALOG_ID_PHOTO_PICKER:

                // Build picture picker dialog for choosing from camera or gallery
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                builder.setTitle(R.string.ui_profile_photo_picker_title);

                // Set up click listener, firing intents open camera
                DialogInterface.OnClickListener dlistener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Item is ID_PHOTO_PICKER_FROM_CAMERA
                        // Call the onPhotoPickerItemSelected in the parent
                        // activity, i.e., SettingsTabActivity in this case

//                        ((SettingsFragment) parent)
//                                .onPhotoPickerItemSelected(item);
                    }
                };

//                Set the item/s to display and create the dialog
                builder.setItems(R.array.ui_profile_photo_picker_items, dlistener);

                return builder.create();


            case DIALOG_ID_DATE_PICKER:
                //The datepicker code is from http://pulse7.net/android/date-picker-dialog-time-picker-dialog-android/
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(this.getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                //Set the user entered date in the ExerciseEntry
                                ((LogGameFragment) parent).setBasketBallGameDate(0, year, monthOfYear, dayOfMonth);



                            }


                        }, mYear, mMonth, mDay);

                return (dpd);
            case DIALOG_ID_TIME_PICKER:
                //The timepicker code is from http://pulse7.net/android/date-picker-dialog-time-picker-dialog-android/
                //Launch a timepicker alert dialog
                // Process to get Current Time
                final Calendar c2 = Calendar.getInstance();
                int mHour = c2.get(Calendar.HOUR_OF_DAY);
                int mMinute = c2.get(Calendar.MINUTE);

                TimePickerDialog tpd = new TimePickerDialog(this.getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                Calendar cal = Calendar.getInstance();

                                //Set the user-entered time in the exercise entry
                                ((LogGameFragment) parent).setBasketBallGameDate(1, hourOfDay, minute, cal.get(Calendar.SECOND));
                            }
                        }, mHour, mMinute, false);
                return (tpd);

            case DIALOG_ID_ASSISTS_ALERT:
                //Launch an alert dialog prompting user to enter Duration (numeric entry)
                AlertDialog.Builder durationBuilder = new AlertDialog.Builder(parent.getActivity());
                durationBuilder.setTitle(R.string.assist_dialog_title);

                EditText durationEditText = new EditText(this.getActivity());
                durationEditText.setId(R.id.assist_edit_text);

                //The setInputType parameters were taken from StackOverFlow
                durationEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                durationBuilder.setView(durationEditText);

                createOkCancelButtons(durationBuilder, 1);

                return durationBuilder.create();

            case DIALOG_ID_TWO_POINTS_ALERT:
                //Launch an alert dialog prompting user to enter Distance (numeric entry)
                AlertDialog.Builder distanceBuilder = new AlertDialog.Builder(parent.getActivity());
                distanceBuilder.setTitle(R.string.two_points_dialog_title);

                EditText distanceEditText = new EditText(this.getActivity());
                distanceEditText.setId(R.id.two_points_edit_text);


                distanceEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                distanceBuilder.setView(distanceEditText);

                createOkCancelButtons(distanceBuilder, 2);

                return distanceBuilder.create();

            case DIALOG_ID_THREE_POINTS_ALERT:
                //Launch an alert dialog prompting user to enter Calories (numeric entry)
                AlertDialog.Builder caloriesBuilder = new AlertDialog.Builder(parent.getActivity());
                caloriesBuilder.setTitle(R.string.three_points_dialog_title);

                EditText caloriesEditText = new EditText(this.getActivity());
                caloriesEditText.setId(R.id.three_points_edit_text);

                caloriesEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                caloriesBuilder.setView(caloriesEditText);

                createOkCancelButtons(caloriesBuilder, 3);

                return caloriesBuilder.create();

            case DIALOG_ID_SHOTS_ATTEMPTED_ALERT:
                //Launch an alert dialog prompting user to enter Heart Rate (numeric entry)
                AlertDialog.Builder heartrateBuilder = new AlertDialog.Builder(parent.getActivity());
                heartrateBuilder.setTitle(R.string.shots_attempted_dialog_title);

                EditText heartrateEditText = new EditText(this.getActivity());
                heartrateEditText.setId(R.id.shots_attempted_edit_text);

                heartrateEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                heartrateBuilder.setView(heartrateEditText);

                createOkCancelButtons(heartrateBuilder, 4);

                return heartrateBuilder.create();
            case DIALOG_ID_COMMENT_ALERT:
                //Launch an alert dialog prompting user to enter Comment (text entry)
                AlertDialog.Builder commentBuilder = new AlertDialog.Builder(parent.getActivity());
                commentBuilder.setTitle(R.string.comment_dialog_title);

                EditText commentEditText = new EditText(this.getActivity());
                commentEditText.setHint(R.string.comment_hint);
                commentEditText.setId(R.id.comment_edit_text);

                commentBuilder.setView(commentEditText);

                createOkCancelButtons(commentBuilder, 5);

                return commentBuilder.create();
            default:
                return null;
        }
    }

    //Method to properly format date info
    private String formatDate(Date date) {
        //I learned about SimpleDateFormat from StackOverflow
        SimpleDateFormat formatter = new SimpleDateFormat("H:mm:ss MMM d yyyy");
        return (formatter.format(date));
    }

    //Method to create blank ok and cancel buttons for alerts. Each dialog calls this method
    //and passes it the "field" field, which indicates which field in ExerciseEntry that
    //that specific dialog will set
    private void createOkCancelButtons(AlertDialog.Builder builder, final int field) {

        final Fragment parent = mParentFragment;

        builder.setPositiveButton(
                okButtonString,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int arg1) {
                        Dialog d = (Dialog) dialog;
                        String userEnteredText;

                        //TODO: Have something where if the user doesn't enter a value, alert them and
                        //dont let them save?

                        switch (field) {
                            case 1:
                                userEnteredText = ((EditText) d.findViewById(R.id.assist_edit_text)).getText().toString();
                                int assists;

                                try {
                                    assists = Integer.parseInt(userEnteredText);

                                } catch (Exception e) {
                                    //If the user entered nothing, make duration 0.

                                    assists = 0;
                                }
                                ((LogGameFragment)parent).setBasketBallGameAssists(assists);
                                break;
                            case 2:
                                userEnteredText = ((EditText) d.findViewById(R.id.two_points_edit_text)).getText().toString();
                                int twoPoints;

                                try {
                                    twoPoints = Integer.parseInt(userEnteredText);
                                } catch (Exception e) {
                                    twoPoints = 0;
                                }
                                ((LogGameFragment)parent).setBasketBallGameTwoPoints(twoPoints);
                                break;
                            case 3:
                                userEnteredText = ((EditText) d.findViewById(R.id.three_points_edit_text)).getText().toString();
                                int threePoints;

                                try {
                                    threePoints = Integer.parseInt(userEnteredText);
                                } catch (Exception e) {
                                    threePoints = 0;
                                }

                                ((LogGameFragment)parent).setBasketBallGameThreePoints(threePoints);
                                break;
                            case 4:
                                userEnteredText = ((EditText) d.findViewById(R.id.shots_attempted_edit_text)).getText().toString();
                                int shotsAttempted;
                                try {
                                    shotsAttempted = Integer.parseInt(userEnteredText);
                                } catch (Exception e) {
                                    shotsAttempted = 0;
                                }
                                ((LogGameFragment)parent).setBasketBallGameShotsAttempted(shotsAttempted);
                                break;
                            case 5:
                                userEnteredText = ((EditText) d.findViewById(R.id.comment_edit_text)).getText().toString();
                                ((LogGameFragment)parent).setBasketBallGameComment(userEnteredText);
                                break;
                            default:
                        }
                    }
                }
        );

        builder.setNegativeButton(
                cancelButtonString,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                    }
                }
        );
    }
}
