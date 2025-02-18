package com.example.ex34_configureddialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity is the main entry point of the application that allows the user to input parameters
 * for generating arithmetic or geometric sequences. The user can view the generated terms, their sum,
 * and reset or cancel the data entry.
 *
 * @author Noa Zohar <nz2020@bs.amalnet.k12.il>
 * @version 1.0
 * @since 18/02/2025
 *
 *
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    /**
     * AlertDialog.Builder used to create various types of dialog boxes in the app.
     */
    private AlertDialog.Builder adb;

    /**
     * Layout for the custom dialog.
     */
    private LinearLayout mydialog;

    /**
     * EditText views to allow the user to input sequence data (first term, difference/ratio).
     */
    private EditText etSeq, etStart, etStep;

    /**
     * ListView that will display the generated terms of the sequence.
     */
    private ListView lvTerms;

    /**
     * TextViews to display the current first term, difference/ratio, sequence type, index, and sum.
     */
    private TextView tvFirst, tvDiffOrMul, tvSeqType, tvIndex, tvSum;

    /**
     * Variables to store the first term, common difference (for arithmetic sequences), and common ratio (for geometric sequences).
     */
    private double firstTerm, diff, ratio;

    /**
     * Array to hold the terms of the sequence for display in the ListView.
     */
    private String[] terms = new String[20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvFirst = findViewById(R.id.tvFirst);
        tvDiffOrMul = findViewById(R.id.tvDiffOrMul);
        tvSeqType = findViewById(R.id.tvSeqType);
        tvIndex = findViewById(R.id.tvIndex);
        tvSum = findViewById(R.id.tvSum);
        lvTerms = findViewById(R.id.lvTerms);
    }

    /**
     * Helper function to return a string representation of a term with scientific notation or plain number format.
     * If the number is too large or small, it will be converted to scientific notation.
     *
     * @param term The term to be formatted.
     * @return The formatted term as a string.
     */
    public static String differentView(double term) {
        if (term % 1 == 0 && term < 10000 && term > -10000) {
            return String.valueOf((int) term);
        }
        if (term >= 10000 || term <= -10000) {
            int exponent = 0;
            double coefficient = term;

            while (Math.abs(coefficient) >= 10000) {
                coefficient /= 10;
                exponent++;
            }
            return String.format("%d * 10^%d", (int) coefficient, exponent);
        }
        int exponent = 0;
        double coefficient = term;
        if (Math.abs(term) >= 1) {
            while (Math.abs(coefficient) >= 10) {
                coefficient /= 10;
                exponent++;
            }
        } else {
            while (Math.abs(coefficient) < 1) {
                coefficient *= 10;
                exponent--;
            }
        }
        return String.format("%.3f * 10^%d", coefficient, exponent);
    }

    /**
     * Helper function to validate user input.
     *
     * @param st The input string to validate.
     * @return True if the input is valid; false otherwise.
     */
    public boolean check(String st) {
        return st.equals("+") || st.equals("+.") || st.equals("-") || st.equals("-.") || st.equals(".") || st.isEmpty();
    }

    /**
     * Method to display the data entry dialog where the user inputs the sequence's type, first term,
     * and difference or ratio.
     *
     * @param view The view triggering the dialog.
     */
    public void dataEnter(View view) {
        mydialog = (LinearLayout) getLayoutInflater().inflate(R.layout.my_dialog, null);
        etSeq = mydialog.findViewById(R.id.etSeq);
        etStart = mydialog.findViewById(R.id.etStart);
        etStep = mydialog.findViewById(R.id.etStep);

        adb = new AlertDialog.Builder(this);
        adb.setView(mydialog);
        adb.setTitle("Data Enter");
        adb.setMessage("Please enter: series's type & first term & difference or ratio");
        adb.setPositiveButton("Enter", myclick);
        adb.setNegativeButton("Cancel", myclick);
        adb.setNeutralButton("Reset", myclick);

        adb.show();
    }

    /**
     * OnClickListener for handling dialog actions (enter, cancel, reset).
     */
    DialogInterface.OnClickListener myclick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                String str1 = etSeq.getText().toString();
                String str2 = etStart.getText().toString();
                String str3 = etStep.getText().toString();

                if (check(str1) || (!str1.equals("0") && !str1.equals("1")) || check(str2) || check(str3)) {
                    Toast.makeText(MainActivity.this, "Invalid input. Please enter again.", Toast.LENGTH_SHORT).show();
                } else {
                    int type = Integer.parseInt(str1);
                    firstTerm = Double.parseDouble(str2);
                    if (type == 0) {
                        diff = Double.parseDouble(str3);
                        tvSeqType.setText("d = ");
                        tvDiffOrMul.setText(String.valueOf(diff));
                        tvFirst.setText(String.valueOf(firstTerm));
                        generateArithmeticSeries();
                    } else {
                        ratio = Double.parseDouble(str3);
                        tvSeqType.setText("q = ");
                        tvDiffOrMul.setText(String.valueOf(ratio));
                        tvFirst.setText(String.valueOf(firstTerm));
                        generateGeometricSeries();
                    }

                    ArrayAdapter<String> adp = new ArrayAdapter<>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, terms);
                    lvTerms.setAdapter(adp);
                    lvTerms.setOnItemClickListener(MainActivity.this);
                }
            }

            if (which == DialogInterface.BUTTON_NEGATIVE) {
                dialog.cancel();
            }

            if (which == DialogInterface.BUTTON_NEUTRAL) {
                resetData();
            }
        }
    };

    /**
     * Resets all data and clears all fields, including sequence terms, and updates the UI.
     */
    public void resetData() {
        etSeq.setText("");
        etStart.setText("");
        etStep.setText("");

        tvFirst.setText("");
        tvDiffOrMul.setText("");
        tvSeqType.setText("");
        tvIndex.setText("");
        tvSum.setText("");

        for (int i = 0; i < terms.length; i++) {
            terms[i] = "";
        }

        ArrayAdapter<String> adp = new ArrayAdapter<>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, terms);
        lvTerms.setAdapter(adp);

        Toast.makeText(this, "All data has been reset!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles the selection of a term from the ListView. It updates the index and calculates the sum.
     *
     * @param adapterView The adapter view where the item was clicked.
     * @param view The view of the clicked item.
     * @param pos The position of the clicked item in the list.
     * @param id The row id of the clicked item.
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        tvIndex.setText(String.valueOf(pos + 1));

        String sum;
        if (etSeq.getText().toString().equals("0")) {
            sum = differentView(sumArithmetic(pos + 1));
        } else {
            sum = differentView(sumGeometric(pos + 1));
        }
        tvSum.setText(sum);
    }

    /**
     * Generates the terms for an arithmetic sequence and stores them in the terms array.
     */
    public void generateArithmeticSeries() {
        for (int i = 0; i < 20; i++) {
            double term = firstTerm + i * diff;
            terms[i] = differentView(term);
        }
    }

    /**
     * Generates the terms for a geometric sequence and stores them in the terms array.
     */
    public void generateGeometricSeries() {
        for (int i = 0; i < 20; i++) {
            double term = firstTerm * Math.pow(ratio, i);
            terms[i] = differentView(term);
        }
    }

    /**
     * Calculates the sum of the first n terms of an arithmetic sequence.
     *
     * @param n The number of terms.
     * @return The sum of the first n terms.
     */
    public double sumArithmetic(int n) {
        return n * (2 * firstTerm + (n - 1) * diff) / 2;
    }

    /**
     * Calculates the sum of the first n terms of a geometric sequence.
     *
     * @param n The number of terms.
     * @return The sum of the first n terms.
     */
    public double sumGeometric(int n) {
        if (ratio == 1) {
            return firstTerm * n;
        }
        return firstTerm * (Math.pow(ratio, n) - 1) / (ratio - 1);
    }

    /**
     * Inflates the menu and adds the option for credits.
     *
     * @param menu The menu to inflate.
     * @return True if the menu was successfully inflated.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.creditsmain, menu);
        return true;
    }

    /**
     * Handles item selection from the options menu.
     *
     * @param item The selected menu item.
     * @return True if the menu item was successfully handled.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuCredits) {
            Intent si = new Intent(this, mainCredits.class);
            startActivity(si);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
