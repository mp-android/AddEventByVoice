package net.madroom.aebv;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

public class MainActivity extends Activity implements OnClickListener {
    public static final int DATE_FORMAT_FLAG =
            DateUtils.FORMAT_SHOW_DATE |
            DateUtils.FORMAT_SHOW_YEAR |
            DateUtils.FORMAT_ABBREV_ALL;

    public static final int TIME_FORMAT_FLAG =
            DateUtils.FORMAT_SHOW_TIME;

    private static final int REQUEST_CODE_TITLE_RECOGNIZER = 1;
    private static final int REQUEST_CODE_DESCRIPTION_RECOGNIZER = 2;
    private static final int REQUEST_CODE_LOCATION_RECOGNIZER = 3;

    Context mContext;

    Calendar mBeginCalendar;
    Calendar mEndCalendar;

    Button mBeginDateButton;
    Button mBeginTimeButton;
    Button mEndDateButton;
    Button mEndTimeButton;
    CheckBox mAlldayCheckbox;

    DatePickerDialog mBeginDatePickerDialog;
    TimePickerDialog mBeginTimePickerDialog;
    DatePickerDialog mEndDatePickerDialog;
    TimePickerDialog mEndTimePickerDialog;

    EditText mTitleField;
    ImageButton mTitleRecognizerButton;
    EditText mDescriptionField;
    ImageButton mDescriptionRecognizerButton;
    EditText mLocationField;
    ImageButton mLocationRecognizerButton;

    Button mAddButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.main_layout);

        mContext = getApplicationContext();

        /**
         * Begin
         */
        mBeginDateButton = (Button)findViewById(R.id.begin_date_button);
        mBeginDateButton.setOnClickListener(this);

        mBeginTimeButton = (Button)findViewById(R.id.begin_time_button);
        mBeginTimeButton.setOnClickListener(this);

        mBeginCalendar = Calendar.getInstance();
        mBeginCalendar.set(
                mBeginCalendar.get(Calendar.YEAR),
                mBeginCalendar.get(Calendar.MONTH),
                mBeginCalendar.get(Calendar.DAY_OF_MONTH),
                mBeginCalendar.get(Calendar.HOUR_OF_DAY)+1,
                0);

        mBeginDatePickerDialog =
                new DatePickerDialog(this, mBeginOnDateSetListener,
                        mBeginCalendar.get(Calendar.YEAR),
                        mBeginCalendar.get(Calendar.MONTH),
                        mBeginCalendar.get(Calendar.DAY_OF_MONTH));

        mBeginTimePickerDialog =
                new TimePickerDialog(this, mBeginOnTimeSetListener,
                        mBeginCalendar.get(Calendar.HOUR_OF_DAY),
                        mBeginCalendar.get(Calendar.MINUTE),
                        true);

        /**
         * End
         */
        mEndDateButton = (Button)findViewById(R.id.end_date_button);
        mEndDateButton.setOnClickListener(this);

        mEndTimeButton = (Button)findViewById(R.id.end_time_button);
        mEndTimeButton.setOnClickListener(this);

        mEndCalendar = Calendar.getInstance();
        mEndCalendar.set(
                mBeginCalendar.get(Calendar.YEAR),
                mBeginCalendar.get(Calendar.MONTH),
                mBeginCalendar.get(Calendar.DAY_OF_MONTH),
                mBeginCalendar.get(Calendar.HOUR_OF_DAY)+1,
                0);

        mEndDatePickerDialog =
                new DatePickerDialog(this, mEndOnDateSetListener,
                        mEndCalendar.get(Calendar.YEAR),
                        mEndCalendar.get(Calendar.MONTH),
                        mEndCalendar.get(Calendar.DAY_OF_MONTH));
        mEndTimePickerDialog =
                new TimePickerDialog(this, mEndOnTimeSetListener,
                        mEndCalendar.get(Calendar.HOUR_OF_DAY),
                        mEndCalendar.get(Calendar.MINUTE),
                        true);

        /**
         * refresh
         */
        refreshBeginDateButton();
        refreshBeginTimeButton();
        refreshEndDateButton();
        refreshEndTimeButton();

        /**
         * Other
         */
        mTitleField = (EditText)findViewById(R.id.title_field);
        mTitleRecognizerButton = (ImageButton)findViewById(R.id.title_recognizer_button);
        mTitleRecognizerButton.setOnClickListener(this);

        mDescriptionField = (EditText)findViewById(R.id.description_field);
        mDescriptionRecognizerButton = (ImageButton)findViewById(R.id.description_recognizer_button);
        mDescriptionRecognizerButton.setOnClickListener(this);

        mLocationField = (EditText)findViewById(R.id.location_field);
        mLocationRecognizerButton = (ImageButton)findViewById(R.id.location_recognizer_button);
        mLocationRecognizerButton.setOnClickListener(this);

        mAddButton = (Button)findViewById(R.id.add_button);
        mAddButton.setOnClickListener(this);

        mAlldayCheckbox = (CheckBox)findViewById(R.id.allday_checkbox);

        mAlldayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBeginTimeButton.setEnabled(!mAlldayCheckbox.isChecked());
                mEndTimeButton.setEnabled(!mAlldayCheckbox.isChecked());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            final ArrayList<String> results;
            final int size;

            switch (requestCode) {
            case REQUEST_CODE_TITLE_RECOGNIZER:
                results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                size = results.size();

                if(size==0) {
                    // TODO: show toast.
                } else if(size==1) {
                    mTitleField.setText(results.get(0));
                    mTitleField.setSelection(mTitleField.getText().length());
                } else {
                    final CharSequence[] items = new CharSequence[size];
                    for (int i = 0; i< size; i++) {
                        items[i] = results.get(i);
                    }

                    new AlertDialog.Builder(this)
                    .setTitle(R.string.event_title)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            mTitleField.setText(results.get(item));
                            mTitleField.setSelection(mTitleField.getText().length());
                        }
                    })
                    .create().show();
                }
                break;

            case REQUEST_CODE_DESCRIPTION_RECOGNIZER:
                results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                size = results.size();

                if(size==0) {
                    // TODO: show toast.
                } else if(size==1) {
                    mDescriptionField.append(results.get(0)+"\n");
                    mDescriptionField.setSelection(mDescriptionField.getText().length());
                } else {
                    final CharSequence[] items = new CharSequence[size];
                    for (int i = 0; i< size; i++) {
                        items[i] = results.get(i);
                    }

                    new AlertDialog.Builder(this)
                    .setTitle(R.string.event_description)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            mDescriptionField.append(results.get(item)+"\n");
                            mDescriptionField.setSelection(mDescriptionField.getText().length());
                        }
                    })
                    .create().show();
                }
                break;

            case REQUEST_CODE_LOCATION_RECOGNIZER:
                results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                size = results.size();

                if(size==0) {
                    // TODO: show toast.
                } else if(size==1) {
                    mLocationField.setText(results.get(0));
                    mLocationField.setSelection(mLocationField.getText().length());
                } else {
                    final CharSequence[] items = new CharSequence[size];
                    for (int i = 0; i< size; i++) {
                        items[i] = results.get(i);
                    }

                    new AlertDialog.Builder(this)
                    .setTitle(R.string.event_location)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            mLocationField.setText(results.get(item));
                            mLocationField.setSelection(mLocationField.getText().length());
                        }
                    })
                    .create().show();
                }
                break;

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /************************************************************
     * refresh button text.
     ***********************************************************/
    /**
     * Begin
     */
    private void refreshBeginDateButton() {
        final String date = DateUtils.formatDateTime(
                mContext,
                mBeginCalendar.getTimeInMillis(),
                DATE_FORMAT_FLAG);
        mBeginDateButton.setText(date);
        compareBeginWithEnd();
    }
    private void refreshBeginTimeButton() {
        final String time = DateUtils.formatDateTime(
                mContext,
                mBeginCalendar.getTimeInMillis(),
                TIME_FORMAT_FLAG);
        mBeginTimeButton.setText(time);
        compareBeginWithEnd();
    }
    private void compareBeginWithEnd() {
        if(mEndCalendar.getTimeInMillis() <= mBeginCalendar.getTimeInMillis()) {
            mEndCalendar.set(
                    mBeginCalendar.get(Calendar.YEAR),
                    mBeginCalendar.get(Calendar.MONTH),
                    mBeginCalendar.get(Calendar.DAY_OF_MONTH),
                    mBeginCalendar.get(Calendar.HOUR_OF_DAY)+1,
                    0);

            refreshEndDateButton();
            refreshEndTimeButton();
        }
    }
    /**
     * End
     */
    private void refreshEndDateButton() {
        final String date = DateUtils.formatDateTime(
                mContext,
                mEndCalendar.getTimeInMillis(),
                DATE_FORMAT_FLAG);
        mEndDateButton.setText(date);
    }
    private void refreshEndTimeButton() {
        final String time = DateUtils.formatDateTime(
                mContext,
                mEndCalendar.getTimeInMillis(),
                TIME_FORMAT_FLAG);
        mEndTimeButton.setText(time);
    }
    /************************************************************
     * OnDateSetListener / OnTimeSetListener
     ***********************************************************/
    /**
     * Begin
     */
    final DatePickerDialog.OnDateSetListener mBeginOnDateSetListener =
            new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mBeginCalendar.set(
                    year,
                    monthOfYear,
                    dayOfMonth,
                    mBeginCalendar.get(Calendar.HOUR_OF_DAY),
                    mBeginCalendar.get(Calendar.MINUTE));
            refreshBeginDateButton();
        }
    };
    final TimePickerDialog.OnTimeSetListener mBeginOnTimeSetListener =
            new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mBeginCalendar.set(
                    mBeginCalendar.get(Calendar.YEAR),
                    mBeginCalendar.get(Calendar.MONTH),
                    mBeginCalendar.get(Calendar.DAY_OF_MONTH),
                    hourOfDay,
                    minute);
            refreshBeginTimeButton();
        }
    };
    /**
     * End
     */
    final DatePickerDialog.OnDateSetListener mEndOnDateSetListener =
            new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mEndCalendar.set(
                    year,
                    monthOfYear,
                    dayOfMonth,
                    mEndCalendar.get(Calendar.HOUR_OF_DAY),
                    mEndCalendar.get(Calendar.MINUTE));
            refreshEndDateButton();
        }
    };
    final TimePickerDialog.OnTimeSetListener mEndOnTimeSetListener =
            new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mEndCalendar.set(
                    mEndCalendar.get(Calendar.YEAR),
                    mEndCalendar.get(Calendar.MONTH),
                    mEndCalendar.get(Calendar.DAY_OF_MONTH),
                    hourOfDay,
                    minute);
            refreshEndTimeButton();
        }
    };
    /************************************************************
     * implements method
     ***********************************************************/
    @Override
    public void onClick(View v) {
        Intent i;

        switch (v.getId()) {
        case R.id.begin_date_button:
            mBeginDatePickerDialog.show();
            break;

        case R.id.begin_time_button:
            mBeginTimePickerDialog.show();
            break;

        case R.id.end_date_button:
            mEndDatePickerDialog.show();
            break;

        case R.id.end_time_button:
            mEndTimePickerDialog.show();
            break;

        case R.id.title_recognizer_button:
            mTitleField.requestFocus();

            i = new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            i.putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    getString(R.string.event_title));

            startActivityForResult(i, REQUEST_CODE_TITLE_RECOGNIZER);

            break;

        case R.id.description_recognizer_button:
            mDescriptionField.requestFocus();

            i = new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            i.putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    getString(R.string.event_description));

            startActivityForResult(i, REQUEST_CODE_DESCRIPTION_RECOGNIZER);

            break;

        case R.id.location_recognizer_button:
            mLocationField.requestFocus();

            i = new Intent(
                    RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            i.putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    getString(R.string.event_location));

            startActivityForResult(i, REQUEST_CODE_LOCATION_RECOGNIZER);

            break;

        case R.id.add_button:
            i = new Intent(Intent.ACTION_EDIT);
            i.setType("vnd.android.cursor.item/event");
            i.putExtra("title", mTitleField.getText().toString().trim());
            i.putExtra("description", mDescriptionField.getText().toString().trim());
            i.putExtra("eventLocation", mLocationField.getText().toString().trim());

            i.putExtra("beginTime", mBeginCalendar.getTimeInMillis());
            i.putExtra("endTime", mEndCalendar.getTimeInMillis());
            i.putExtra("allDay", mAlldayCheckbox.isChecked());

            startActivity(i);

            break;

        }
    }

}
