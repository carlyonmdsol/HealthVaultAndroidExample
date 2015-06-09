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
import com.microsoft.hsg.android.simplexml.things.types.dates.ApproxDate;
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
        Medication medObject = new Medication();
        CodableValue medName = new CodableValue();
        GeneralMeasurement input = new GeneralMeasurement();
        GeneralMeasurement inputStrength = new GeneralMeasurement();
        CodableValue inputReason = new CodableValue();
        ApproxDateTime inputStartDate = new ApproxDateTime();
        ApproxDateTime inputEndDate = new ApproxDateTime();

        medName.setText(name.getText().toString());
        medObject.setName(medName);


        input.setDisplay(dosage.getText().toString() + " " + dosageSpinner.getSelectedItem().toString());
        medObject.setDose(input);

        inputStrength.setDisplay(strength.getText().toString() + " " + strengthSpinner.getSelectedItem().toString());
        medObject.setStrength(inputStrength);

        //Sets the "Reason for taking" category
        inputReason.setText(reasonTaken.getText().toString());
        medObject.setIndication(inputReason);

        inputStartDate.setDescriptive(startDate.getText().toString());
        medObject.setDateStarted(inputStartDate);

        inputEndDate.setDescriptive(endDate.getText().toString());
        medObject.setDateDiscontinued(inputEndDate);

        hvClient.asyncRequest(
                currentRecord.putThingDataAsync(medObject), new MedCallback(1));
        Log.e("ThingType medication", medObject.getDose().getDisplay() + "   " + currentRecord.getName() );



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
