package com.microsoft.hsg.android.hvsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.microsoft.hsg.HVException;
import com.microsoft.hsg.android.simplexml.HealthVaultApp;
import com.microsoft.hsg.android.simplexml.client.HealthVaultClient;
import com.microsoft.hsg.android.simplexml.client.RequestCallback;
import com.microsoft.hsg.android.simplexml.methods.getthings3.request.ThingRequestGroup2;
import com.microsoft.hsg.android.simplexml.methods.getthings3.request.ThingSectionSpec2;
import com.microsoft.hsg.android.simplexml.methods.getthings3.response.ThingResponseGroup2;
import com.microsoft.hsg.android.simplexml.things.thing.AbstractThing;
import com.microsoft.hsg.android.simplexml.things.thing.Thing2;
import com.microsoft.hsg.android.simplexml.things.thing.ThingKey;
import com.microsoft.hsg.android.simplexml.things.types.base.CodableValue;
import com.microsoft.hsg.android.simplexml.things.types.base.CodedValue;
import com.microsoft.hsg.android.simplexml.things.types.base.GeneralMeasurement;
import com.microsoft.hsg.android.simplexml.things.types.dates.ApproxDateTime;
import com.microsoft.hsg.android.simplexml.things.types.medication.Medication;
import com.microsoft.hsg.android.simplexml.things.types.types.Record;
import com.microsoft.hsg.android.simplexml.things.types.weight.Weight;

import java.util.ArrayList;
import java.util.List;

public class MedicationActivity extends Activity {
    private HealthVaultApp service;
    private HealthVaultClient hvClient;
    private Record currentRecord;
    EditText name;
    EditText strength;
    EditText dosage;
    EditText howOften;
    EditText reasonTaken;
    EditText startDate;
    EditText endDate;
    Spinner strengthSpinner;
    Spinner dosageSpinner;
    Spinner howSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        service = HealthVaultApp.getInstance();
        hvClient = new HealthVaultClient();

        name = (EditText)findViewById(R.id.medication_name_text);
        strength = (EditText)findViewById(R.id.medication_strength_text);
        dosage = (EditText)findViewById(R.id.medication_dosage_text);
        howOften = (EditText)findViewById(R.id.medication_how_text);
        reasonTaken = (EditText)findViewById(R.id.medication_reason_text);
        startDate = (EditText)findViewById(R.id.medication_start_date_text);
        endDate = (EditText)findViewById(R.id.medication_end_date_text);
        strengthSpinner = (Spinner)findViewById(R.id.medication_strength_spinner);
        dosageSpinner = (Spinner)findViewById(R.id.medication_dosage_spinner);
        howSpinner = (Spinner)findViewById(R.id.medication_how_spinner);


        Button submitButton = (Button)findViewById(R.id.medication_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitData();
            }
        });
        ArrayAdapter<CharSequence> strengthAdapter = ArrayAdapter.createFromResource(this,
                R.array.medication_strength_array, android.R.layout.simple_spinner_item);
        strengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        strengthSpinner.setAdapter(strengthAdapter);

        ArrayAdapter<CharSequence> dosageAdapter = ArrayAdapter.createFromResource(this,
                R.array.medication_dosage_array, android.R.layout.simple_spinner_item);
        dosageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dosageSpinner.setAdapter(dosageAdapter);

        ArrayAdapter<CharSequence> howAdapter = ArrayAdapter.createFromResource(this,
                R.array.medication_how_array, android.R.layout.simple_spinner_item);
        howAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        howSpinner.setAdapter(howAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        hvClient.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentRecord = HealthVaultApp.getInstance().getCurrentRecord();
    }

    @Override
    protected void onStop() {
        hvClient.stop();
        super.onStop();
    }

    public void submitData(){
        List<Record> records = HealthVaultApp.getInstance().getRecordList();
        final Record record = records.get(0);
        final ArrayList<ThingSectionSpec2> thingArray = new ArrayList<ThingSectionSpec2>();
        final ThingKey medKey = new ThingKey(Medication.ThingType, null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ThingResponseGroup2 response2 = record.getThings(ThingRequestGroup2.thingTypeQuery(Medication.ThingType));

                Medication medObject = new Medication();
                CodableValue medName = new CodableValue();
                medName.setText(name.getText().toString());
                medObject.setName(medName);
                GeneralMeasurement input = new GeneralMeasurement();
                input.setDisplay(dosage.getText().toString() + " " + dosageSpinner.getSelectedItem().toString());
                medObject.setDose(input);
                input.setDisplay(strength.getText().toString() + " " + strengthSpinner.getSelectedItem().toString());
                medObject.setStrength(input);
                medName.setText(reasonTaken.getText().toString());
                medObject.setIndication(medName);
                ApproxDateTime date = new ApproxDateTime();
                date.setDescriptive(startDate.getText().toString());
                medObject.setDateStarted(date);
                date.setDescriptive(endDate.getText().toString());
                medObject.setDateStarted(date);
                hvClient.asyncRequest(
                        currentRecord.putThingDataAsync(medObject), new MedCallback(1));
                Log.e("ThingType medication", medObject.getDose().getDisplay() + "   " + currentRecord.getName() );


            }
        }).start();
    }

    public class MedCallback<Object> implements RequestCallback {
        public final static int RenderWeights = 0;
        public final static int PutWeights = 1;

        private int event;

        public MedCallback(int event) {
        }

        @Override
        public void onError(HVException exception) {
        }

        @Override
        public void onSuccess(java.lang.Object obj) {
        }
    }



}