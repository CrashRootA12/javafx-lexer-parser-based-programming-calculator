package mainCalculatorPackage;

import java.util.ArrayList;


import java.util.Scanner;

import uNumberLibrary.UNumber;
import uNumberLibrary.UNumberWithSqrt;


/**
 * <p> Title: CalculatorValue Class. </p>
 * 
 * <p> Description: A component of a JavaFX demonstration application that performs computations </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2017 </p>
 * 
 * @author Lynn Robert Carter
 * @author JSGREWAL, Sumit and Shivam
 *         
 * @version 4.10	2018-01-15 The implementation of integer subtraction, multiplication and division is done.
 * @version 4.20    2018-02-02 The Square Root method initialized with change in error meassages.
 * @version 4.30	2018-02-25 Implementation of FSM to provide high quality error messages. 
 * @version 4.40 	2018-03-15 Implementation of error terms calculation and respective high quality error Messages. 
 * @version 4.50 	2018-04-13 Implementation of Estimated Measured Values and Error Terms with respect to digits of significance.
 * @version 5.10 	2018-10-04 Implementation of UNumber Library to provide unlimited precision. Currently the precision is set to 25 significant digits.
 * @version 5.20    2018-11-22 Implementation of Unit Support System with Signal Normalization as well as Inter-Conversion of Units of Same Physical Dimensions.
 * <strong>Version 5.20 is designed and implemented keeping Hohmann Transfer Equations and Respective Calculations in consideration.</strong>
 * @version 6.00	2019-02-24	Implemented in the Programmable Calculator V1
 * */
public class CalculatorValue extends UNumber {

	/**********************************************************************************************

	Attributes

	 **********************************************************************************************/

	// These are the major values that define a calculator value
	double measuredValue = 0;
	String errorMessage = "";
	double errorTerm = 0;
	String etErrorMessage = "";
	String unit = "";

	//These are the values which define the FSM Recognizer for Measured Value.
	public static String measuredValueErrorMessage = "";	// The alternate error message text
	public static String measuredValueInput = "";		// The input being processed
	public static int measuredValueIndexofError = -1;		// The index where the error was located
	private static int state = 0;						// The current state value
	private static int nextState = 0;					// The next state value
	public static boolean finalState = false;			// Is this state a final state
	private static String inputLine = "";				// The input line
	private static char currentChar;						// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if it is running


	//These are the values which define the FSM Recognizer for Error Term.
	public static String errorTermErrorMessage = "Error Term recognition has not been implements";
	public static String errorTermInput = "";			// The input being processed
	public static int errorTermIndexofError = -1;		// The index where the error was located

	//These are the values which will be used for providing results with unlimited precision

	UNumber uResultMV ;
	UNumber uResultET;

	/***
	 *This is the Lookup Table Which serves as a mapping between two units and the factors
	 *which needs to be multiplied in order to perform the mapped conversion.
	 ****/
	public static String[][] lookupTableForConversion ={
			{" ", "m", "km","miles","days","seconds","km/seconds","miles/seconds"},
			{"m", "1","0.001","0.000621371","-","-","-","-"},
			{"km", "1000","1","0.621371","-","-","-","-"}, // The factor should be multiplied by the unit at the end of 
			{"miles","1609.34","1.60934","1","-","-","-","-"}, // its row in order to perform conversion to the unit
			{"days",	"-","-","-","1","86400","-","-"}, // at the end of the column.
			{"seconds","-","-","-","0.00001157407","1","-","-"},
			{"km/seconds","-","-","-","-","-","1","0.621371"},
			{"miles/seconds","-","-","-","-","-","1.60934","1"},

	};
	/****
	 * This is the matrix which resembles the Lookup Table and maps the possibility of the conversion of units
	 * with the respective units.
	 * true means that the conversion defined by the index of the value is physically possible.
	 */
	public static boolean conversionPossibilityMapping[][] = {
			{false,false,false,false,false,false,false,false}, // Top Row- All False
			{false,true,true,true,false,false,false,false}, // metres row
			{false,true,true,true,false,false,false,false}, //km row
			{false,true,true,true,false,false,false,false}, //miles row
			{false,false,false,false,true,true,false,false}, //days row
			{false,false,false,false,true,true,false,false}, //seconds row
			{false,false,false,false,false,false,true,true}, //km/seconds row
			{false,false,false,false,false,false,true,true}, //miles/seconds row
	};
	public static ArrayList<String> theListOfUnits = new ArrayList<String>();
	{
		for (int r=0; r<lookupTableForConversion.length;r++) {
			for (int c =0; c<lookupTableForConversion[0].length;c++) {
				theListOfUnits.add(lookupTableForConversion[r][c]);
			}
		}
	}

	/**********************************************************************************************

	Constructors

	 **********************************************************************************************/

	/*****
	 * This is the default constructor
	 */
	public CalculatorValue() {
		measuredValue=0;
		errorTerm = 0;
		uResultMV = new UNumber(measuredValue);
		uResultET = new UNumber(errorTerm);
	}

	/******
	 * This constructor creates a calculator value based on two double values.
	 */

	public CalculatorValue(double v, double t ) {
		measuredValue = v;
		errorTerm = t;
		uResultMV = new UNumber(measuredValue);
		uResultET = new UNumber(errorTerm);
	}

	/*****
	 * This constructor creates a calculator value from a string... Due to the nature
	 * of the input, there is a high probability that the input has errors, so the 
	 * routine returns the value with the error message value set to empty or the string 
	 * of an error message.
	 */
	public CalculatorValue(String s) {
		measuredValue = 0;
		errorTerm =0;
		if (s.length() == 0) {							     	// If there is nothing there,
			errorMessage = "***Error*** Input is empty";		// signal an error
			etErrorMessage ="No Input ";
			return;												
		}
		// If the first character is a plus sign, ignore it.
		int start = 0;										// Start at character position zero
		boolean negative = false;							// Assume the value is not negative
		/****
		 * Here switch case is used because if-else decision statement will perform the statements
		 * for n number of times but switch case will limit it to only one time.
		 * 
		 */
		switch(s.charAt(start)) {
		case 1: start++; break;                             //If the first character is positive.
		case 2: start++; negative =true; break;             // If the first character is negative.
		}

		// See if the user-entered string can be converted into an double value
		Scanner tempScanner = new Scanner(s.substring(start));// Create scanner for the digits
		if (!tempScanner.hasNextDouble()) {					// See if the next token is a valid
			errorMessage = "***Error*** Invalid value"; 		
			tempScanner.close();								
			return;												
		}

		// Convert the user-entered string to a integer value and see if something else is following it
		measuredValue = tempScanner.nextDouble();


		// Convert the value and check to see
		if (tempScanner.hasNext()) {							// that there is nothing else is 
			errorMessage = "***Error*** Excess data"; 		// following the value.  If so, it
			tempScanner.close();								// is an error.  Therefore we must
			measuredValue = 0;
			errorTerm=0;
			// return a zero value.
			return;													
		}

		if(negative) { 										// Return the proper value based
			measuredValue = -measuredValue;					// on the state of the flag that
			errorTerm=-errorTerm;
		}
		tempScanner.close();
		errorMessage = "";
		uResultMV = new UNumber(measuredValue);
		uResultET = new UNumber(errorTerm);
	}

	/*****
	 * This constructor creates a calculator value from two strings... Due to the nature
	 * of the input, there is a high probability that the input has errors, so the 
	 * routine returns the value with the error message value set to empty or the string 
	 * of an error message.
	 * @param unit 
	 */
	public CalculatorValue(String s, String e, String u) {
		unit = u; 
		measuredValue = 0;
		errorTerm =0;
		if (s.length() == 0) {							     	// If there is nothing there,
			errorMessage = "***Error*** Input is empty";		// signal an error
			return;												
		}
		if (e.length()==0) {
			etErrorMessage="***Error*** Input is empty";
			return;
		}
		// If the first character is a plus sign, ignore it.
		int start = 0;										// Start at character position zero
		boolean negative = false;							// Assume the value is not negative
		boolean enegative = false;
		/****
		 * Here switch case is used because if-else decision statement will perform the statements
		 * for n number of times but switch case will limit it to only one time.
		 * 
		 */
		switch(s.charAt(start)) {
		case 1: start++; break;                             //If the first character is positive.
		case 2: start++; negative =true; break;             // If the first character is negative.
		}
		switch(e.charAt(start)) {
		case 1: start++; break;                             //If the first character is positive.
		case 2: start++; enegative =true; break;             // If the first character is negative.
		}


		// See if the user-entered string can be converted into an double value
		Scanner tempScanner = new Scanner(s.substring(start));// Create scanner for the digits
		Scanner tempScanner2 = new Scanner(e.substring(start));
		if (!tempScanner.hasNextDouble()) {					// See if the next token is a valid
			errorMessage = "***Error*** Invalid value"; 		

			tempScanner.close();								
			tempScanner2.close();
			return;												
		}
		if (!tempScanner2.hasNextDouble()) {
			etErrorMessage = "***Error*** Invalid value";
			tempScanner.close();								
			tempScanner2.close();
			return;
		}


		// Convert the user-entered string to a integer value and see if something else is following it
		measuredValue = tempScanner.nextDouble();
		errorTerm = tempScanner2.nextDouble();

		// Convert the value and check to see
		if (tempScanner.hasNext()&&tempScanner2.hasNext()) {							// that there is nothing else is 
			errorMessage = "***Error*** Excess data"; 		// following the value.  If so, it
			etErrorMessage = "***Error*** Excess Data";
			tempScanner2.close();
			tempScanner.close();								// is an error.  Therefore we must
			measuredValue = 0;
			errorTerm=0;
			// return a zero value.
			return;													
		}

		if(negative) { 										// Return the proper value based
			measuredValue = -measuredValue;					// on the state of the flag that
		}
		if (enegative) {
			errorTerm = -errorTerm;
		}
		tempScanner.close();
		tempScanner2.close();
		errorMessage = "";
		etErrorMessage="";
		uResultMV = new UNumber(measuredValue);
		uResultET = new UNumber(errorTerm);
	}

	public CalculatorValue (CalculatorValue v) {
		measuredValue = v.measuredValue;
		errorMessage = v.errorMessage;
		errorTerm = v.errorTerm;
		etErrorMessage= v.etErrorMessage;

	}

	/**********************************************************************************************

	Getters and Setters

	 **********************************************************************************************/

	/*****
	 * This is the start of the getters and setters
	 * 
	 * Get the error messages
	 */
	public String getErrorMessage(){
		return errorMessage;
	}

	/*****
	 * Set the current value of a calculator error message to a specific string
	 */
	public void setErrorMessage(String p){
		errorMessage = p;
	}
	public void setETErrorMessage(String l) {
		etErrorMessage = l;
	}


	/**********************************************************************************************

	The toString() Method

	 **********************************************************************************************/

	/*****
	 * This is the default toString method for UNumber Attribute-supporting Calculator Values
	 *
	 */
	public String getMV() {
		return String.valueOf(measuredValue);
	}
	public String getET() {
		return String.valueOf(errorTerm);
	}
	public String mvToString() {
		return uResultMV.toString();
	}
	public String etToString() {
		return uResultET.toString();
	}
	
	

	public String debugToString() {
		return "";
	}

	/**********************************************************************************************

	The computation methods

	 **********************************************************************************************/


	/*******************************************************************************************************
	 * The following methods implement computation on the calculator values.  These routines assume that the
	 * caller has verified that things are okay for the operation to take place.  These methods understand
	 * the technical details of the values and their reputations, hiding those details from the business 
	 * logic and user interface modules.
	 * 
	 * UNumber Library is used for providing unlimited precision
	 * Since we do not yet support units, we don't recognize any errors.
	 */


	/*****
	 * Addition Feature: Formal method
	 * @param v
	 */
	public void add(CalculatorValue v) { 
		UNumber unsizedFirstValue = new UNumber(measuredValue);  // Create UNumber object for first operand
		UNumber firstValue = new UNumber(unsizedFirstValue,25); // of size of 25 significant digits and
		UNumber unsizedSecondValue = new UNumber (v.measuredValue); // do the same for 
		UNumber secondValue = new UNumber (unsizedSecondValue,25); //second operand
		firstValue.add(secondValue); //Add both operands to
		uResultMV = new UNumber(firstValue);  //get the result

	}

	/*****
	 * Subtraction Feature: Formal method
	 * @param v
	 */
	public void sub(CalculatorValue v) {
		UNumber unsizedFirstValue = new UNumber(measuredValue); //Create UNumber object for first operand
		UNumber firstValue = new UNumber(unsizedFirstValue,25); // of size of 25 significant digits and
		UNumber unsizedSecondValue = new UNumber (v.measuredValue); // do the same for 
		UNumber secondValue = new UNumber (unsizedSecondValue,25); //second operand
		firstValue.sub(secondValue); //Subtract both operands to 
		uResultMV = new UNumber(firstValue); // get the result
	}

	/*****
	 * Multiplication Feature: Formal Method
	 * @param v
	 */

	public void mpy(CalculatorValue v) {
		UNumber unsizedFirstValue = new UNumber(measuredValue); //Create UNumber object for first operand 
		UNumber firstValue = new UNumber(unsizedFirstValue,25); // of size 25 significant digits and
		UNumber unsizedSecondValue = new UNumber (v.measuredValue); // do the same for 
		UNumber secondValue = new UNumber (unsizedSecondValue,25); //second operand
		firstValue.mpy(secondValue);//Multiply both operands to 
		uResultMV = new UNumber(firstValue); //get the result

	}

	/*****
	 * Division Feature: Formal Method
	 * @param v
	 */

	public void div(CalculatorValue v) {

		UNumber unsizedFirstValue = new UNumber(measuredValue); //Create UNumber object for first operand
		UNumber firstValue = new UNumber(unsizedFirstValue,25); // of size 25 significant digits and 
		UNumber unsizedSecondValue = new UNumber (v.measuredValue); //do the same for
		UNumber secondValue = new UNumber (unsizedSecondValue,25); // second operand
		firstValue.div(secondValue); //Divide both operands to
		uResultMV = new UNumber(firstValue); //get the result

	}

	/*****
	 *Square Root Feature : Formal Method
	 * @param v
	 */
	public void sqrt(CalculatorValue v){                       

		UNumber unsizedfirstValue = new UNumberWithSqrt(measuredValue); //Create UNumber object for first operand
		UNumberWithSqrt firstValue = new UNumberWithSqrt(unsizedfirstValue,25); // of size 25 significant digits and 
		UNumber rootedValue =firstValue.sqrt(firstValue); //calculate the square root for
		uResultMV = new UNumber(rootedValue);		//getting the result
	}


	/***********
	 * Add Error Terms : Formal Method
	 * Uses UpperBound-LowerBound Algorithm
	 * @param v
	 */

	public void addET(CalculatorValue v) {

		UNumber utwo = new UNumber(2.0);  // UNumber object for two
		utwo = new UNumber(utwo, 25); // of size  of 25 significant digits 
		UNumber unsizedFirstValue = new UNumber(measuredValue); //UNumber object for first operand
		UNumber firstValue = new UNumber(unsizedFirstValue, 25); // of size of 25 significant digits
		UNumber copyFirstValue = new UNumber(firstValue); //Copy of first value
		UNumber unsizedFirstValueET = new UNumber(errorTerm); //UNumber object for first error term of
		UNumber firstValueET = new UNumber(unsizedFirstValueET, 25); //25 significant digits
		firstValue.sub(firstValueET); //Calculate the lowerbound of first operand
		UNumber lowerBound1 = firstValue;
		copyFirstValue.add(firstValueET); //Calculate the upperbound of first operand
		UNumber upperBound1 = copyFirstValue;
		UNumber unsizedSecondValue = new UNumber(v.measuredValue);//UNumber object for second operand 
		UNumber secondValue = new UNumber(unsizedSecondValue, 25); // of 25 significant digits 
		UNumber copySecondValue = new UNumber(secondValue);//Copy of second value
		UNumber unsizedSecondValueET = new UNumber(v.errorTerm); //Unumber object for second error term
		UNumber secondValueET = new UNumber(unsizedSecondValueET, 25); // of 25 significant digits
		secondValue.sub(secondValueET); //Calculate the lowerbound of second operand
		UNumber lowerBound2 = secondValue;
		copySecondValue.add(secondValueET);//Calculate the upperbound of second operand
		UNumber upperBound2 = copySecondValue;
		lowerBound1.add(lowerBound2); //Add the lowerbounds
		UNumber addedLowerBounds = lowerBound1;
		upperBound1.add(upperBound2); //Add the upperbounds
		UNumber addedUpperBounds = upperBound1;
		addedUpperBounds.sub(addedLowerBounds); //Subtract lowerbounds from upperbounds
		UNumber range = addedUpperBounds;//to get the range
		range.div(utwo);  // Divide the range by two to get
		uResultET = new UNumber(range); //resultant error term
		errorMessage = "";




	}
	/******************************************************
	 * The following subtraction method uses the similar algorithm as above addition method does.
	 */
	/***********
	 * Subtract Error Terms : Formal Method
	 * Uses UpperBound-LowerBound Algorithm
	 * @param v
	 */

	public void subET(CalculatorValue v) {

		UNumber utwo = new UNumber(2.0);  //UNumber object for 2 
		utwo = new UNumber(utwo, 25);  // of 25 significant digits
		UNumber unsizedFirstValue = new UNumber(measuredValue); //UNumber object for first operand
		UNumber firstValue = new UNumber(unsizedFirstValue, 25); // of 25 significant digits
		UNumber copyFirstValue = new UNumber(firstValue); //Copy of first operand
		UNumber unsizedFirstValueET = new UNumber(errorTerm); //UNumber object for first operand of 2
		UNumber firstValueET = new UNumber(unsizedFirstValueET, 25); //25 significant digits
		firstValue.sub(firstValueET); //Lowerbound of first operand
		UNumber lowerBound1 = firstValue;
		copyFirstValue.add(firstValueET); //Upper bound of first operand
		UNumber upperBound1 = copyFirstValue; 
		UNumber unsizedSecondValue = new UNumber(v.measuredValue); //UNumber object for second operand
		UNumber secondValue = new UNumber(unsizedSecondValue, 25); // of 25 significant digits
		UNumber copySecondValue = new UNumber(secondValue); //Copy of second operand
		UNumber unsizedSecondValueET = new UNumber(v.errorTerm); //UNumber object of second error term 
		UNumber secondValueET = new UNumber(unsizedSecondValueET, 25); // of size 25 significant digits
		secondValue.sub(secondValueET); //Lowerbound of second operand
		UNumber lowerBound2 = secondValue;
		copySecondValue.add(secondValueET); //Upperbound of second operand
		UNumber upperBound2 = copySecondValue;
		lowerBound1.add(lowerBound2); //Add the lower bounds
		UNumber subtractedLowerBounds = lowerBound1; 
		upperBound1.add(upperBound2); // Add the upperbounds
		UNumber subtractedUpperBounds = upperBound1;
		subtractedUpperBounds.sub(subtractedLowerBounds); //Subtract the lowerbound from upperbound to get the 
		UNumber range = subtractedUpperBounds; //range
		range.div(utwo); //Divide the range by two to get the 
		uResultET = new UNumber(range); //resultant error term
		errorMessage = "";



	}
	/***********
	 * Multiply Error Terms : Formal Method
	 * Uses Error Fraction Algorithm
	 * @param v
	 */

	public void mpyET(CalculatorValue v) {
		/******** Initializing the UNumber Objects****************/
		UNumber unsizedFirstValue = new UNumber (measuredValue);
		UNumber unsizedSecondValue = new UNumber(v.measuredValue);
		UNumber unsizedFirstValueET = new UNumber (errorTerm);
		UNumber unsizedSecondValueET = new UNumber (v.errorTerm);
		
		UNumber firstValue = new UNumber(unsizedFirstValue,25);
		UNumber secondValue = new UNumber(unsizedSecondValue,25);
		UNumber	firstValueET = new UNumber(unsizedFirstValueET,25);
		UNumber secondValueET = new UNumber(unsizedSecondValueET, 25);

		UNumber copyFirstValue = new UNumber(firstValue);
		UNumber copyFirstValueET = new UNumber (firstValueET);
		UNumber copySecondValue = new UNumber(secondValue,25);
		UNumber copySecondValueET = new UNumber(secondValueET,25);
		/********* Calculating the Error Term************************/
		copyFirstValue.mpy(copySecondValue);
		UNumber mvProduct = new UNumber(copyFirstValue); //Product of measured values
		copyFirstValue = new UNumber(firstValue);
		copyFirstValueET.div(copyFirstValue); // The error fraction of first operand
		UNumber errorFraction1 = new UNumber(copyFirstValueET);
		copyFirstValueET = new UNumber (firstValueET);
		copySecondValueET.div(copySecondValue); //The error fraction of second operand
		UNumber errorFraction2 = new UNumber(copySecondValueET);
		copySecondValueET = new UNumber(secondValueET,25);
		errorFraction1.add(errorFraction2); //Add the error fractions
		UNumber addedEF = new UNumber(errorFraction1);
		mvProduct.mpy(addedEF); // Multiply the error fraction by product of measured values to get the
		uResultET=new UNumber(mvProduct); //resultant error terms


	}
	/******************************************************
	 * The following division method uses the similar algorithm as above addition method does.
	 */
	
	/***********
	 * Divide Error Terms : Formal Method 
	 * Uses Error Fraction Algorithm
	 * @param v
	 */

	public void divET(CalculatorValue v) {
		/*********************** Initializing UNumber Objects****************/
		UNumber unsizedFirstValue = new UNumber (measuredValue);
		UNumber unsizedSecondValue = new UNumber(v.measuredValue);
		UNumber unsizedFirstValueET = new UNumber (errorTerm);
		UNumber unsizedSecondValueET = new UNumber (v.errorTerm);

		UNumber firstValue = new UNumber(unsizedFirstValue,25);
		UNumber secondValue = new UNumber(unsizedSecondValue,25);
		UNumber	firstValueET = new UNumber(unsizedFirstValueET,25);
		UNumber secondValueET = new UNumber(unsizedSecondValueET, 25);

		UNumber copyFirstValue = new UNumber(firstValue);
		UNumber copyFirstValueET = new UNumber (firstValueET);
		UNumber copySecondValue = new UNumber(secondValue,25);
		UNumber copySecondValueET = new UNumber(secondValueET,25);
		/***********Calculating Error Terms*******************/
		copyFirstValue.div(copySecondValue); 
		UNumber mvQuotient = new UNumber(copyFirstValue); //Quotient of measured values
		//Calculate and add the error fractions
		copyFirstValue = new UNumber(firstValue);
		copyFirstValueET.div(copyFirstValue);
		UNumber errorFraction1 = new UNumber(copyFirstValueET);
		copyFirstValueET = new UNumber (firstValueET);
		copySecondValueET.div(copySecondValue);
		UNumber errorFraction2 = new UNumber(copySecondValueET);
		copySecondValueET = new UNumber(secondValueET,25);
		errorFraction1.add(errorFraction2);
		UNumber addedEF = new UNumber(errorFraction1);
		
		mvQuotient.mpy(addedEF);// Multiply the resultant error fraction to quotient of measured values
		uResultET=new UNumber(mvQuotient); //to get the result


	}
	/***********
	 * Square Root of  Error Terms : Power Term Method
	 */

	public void sqrtET() {
		/********** Initializing UNumber Objects*************/
		UNumber unsizedMV = new UNumber(measuredValue);
		UNumber mV = new UNumber(unsizedMV,25);
		UNumberWithSqrt copyMV = new UNumberWithSqrt (mV);
	
		UNumber unsizedET = new UNumber(errorTerm);
		UNumber eT = new UNumber(unsizedET,25);
		UNumber copyET = new UNumber(eT);
		/*********Calculating Error Terms*******************/
		UNumber power =copyMV.sqrt(copyMV); //Get the Power Term by taking square root of measured value
		copyET.div(copyMV);  // and divide it by measured value
		UNumber powErrorFraction = new UNumber(copyET); // to get the error fraction
		UNumber half = new UNumber(0.5); 
		half.mpy(powErrorFraction); //Divide the error fraction by two and multiply it
		half.mpy(power); // by power term to get
		uResultET = new UNumber(half); //the resultant error term
	}



	/*******************************
	 * This is starting of Finite State Machine Recognizer which will recognize the Inputs in The Fields.
	 * The state transition diagram of this recognizer resembles the one in MeasuredValue.java of W7D2 Study Hall Excercise.
	 * @param input
	 * @param currentCharNdx
	 * @return Error Messages on the basis of Nature of Input
	 * 
	 */

	private static String displayInput(String input, int currentCharNdx) {
		// Display the entire input line
		String result = input + "\n";

		// Display a line with enough spaces so the up arrow point to the point of an error
		for (int ndx=0; ndx < currentCharNdx; ndx++) result += " ";

		// Add the up arrow to the end of the second line
		return result + "\u21EB";				// A Unicode up arrow with a base
	}
	/***********************************************************************************************
	 * This routine shifts the control to the next character until end of input string is reached
	 * 
	 *********************************/
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			currentChar = ' ';
			running = false;
		}
	}

	/**********
	 * This method is a mechanical transformation of a Finite State Machine diagram into a Java
	 * method for Measured Values.
	 * 
	 * @param input		The input string for the Finite State Machine
	 * @return			An output string that is empty if every things is okay or it will be
	 * 						a string with a help description of the error follow by two lines
	 * 						that shows the input line follow by a line with an up arrow at the
	 *						point where the error was found.
	 * 
	 */
	public static String checkMeasureValue(String input) {
		if(input.length() <= 0) return "";
		// The following are the local variable used to perform the Finite State Machine simulation
		state = 0;							// This is the FSM state number
		inputLine = input;					// This is the input provided by user
		currentCharNdx = 0;					// The index of the current character
		currentChar = input.charAt(0);		// The current character from the above indexed position

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state

		measuredValueInput = input;			// Set up the alternate result copy of the input
		running = true;						// Start the loop


		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
		while (running) {
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			switch (state) {
			case 0: 
				// State 0 has three valid transitions.  Each is addressed by an if statement.

				// This is not a final state
				finalState = false;

				// If the current character is in the range from 1 to 9, it transitions to state 1
				if (currentChar >= '0' && currentChar <= '9') {
					nextState = 1;
					break;
				}
				// If the current character is a decimal point, it transitions to state 3
				else if (currentChar == '.') {
					nextState = 3;
					break;					
				}

				else if(currentChar =='-') {
					nextState = 0;
					break;
				}

				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;

			case 1: 
				// State 1 has three valid transitions.  Each is addressed by an if statement.

				// This is a final state
				finalState = true;

				// In state 1, if the character is 0 through 9, it is accepted and we stay in this
				// state
				if (currentChar >= '0' && currentChar <= '9') {
					nextState = 1;
					break;
				}

				// If the current character is a decimal point, it transitions to state 2
				else if (currentChar == '.') {
					nextState = 2;
					break;
				}
				// If the current character is an E or an e, it transitions to state 5
				else if (currentChar == 'E' || currentChar == 'e') {
					nextState = 5;
					break;
				}
				// If it is none of those characters, the FSM halts
				else
					running = false;

				// The execution of this state is finished
				break;			

			case 2: 
				// State 2 has two valid transitions.  Each is addressed by an if statement.

				// This is a final state
				finalState = true;

				// If the current character is in the range from 1 to 9, it transitions to state 1
				if (currentChar >= '0' && currentChar <= '9') {
					nextState = 2;
					break;
				}
				// If the current character is an 'E' or 'e", it transitions to state 5
				else if (currentChar == 'E' || currentChar == 'e') {
					nextState = 5;
					break;
				}

				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;

			case 3:
				// State 3 has only one valid transition.  It is addressed by an if statement.

				// This is not a final state
				finalState = false;

				// If the current character is in the range from 1 to 9, it transitions to state 1
				if (currentChar >= '0' && currentChar <= '9') {
					nextState = 4;
					break;
				}

				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;

			case 4: 
				// State 4 has two valid transitions.  Each is addressed by an if statement.

				// This is a final state
				finalState = true;

				// If the current character is in the range from 1 to 9, it transitions to state 4
				if (currentChar >= '0' && currentChar <= '9') {
					nextState = 4;
					break;
				}
				// If the current character is an 'E' or 'e", it transitions to state 5
				else if (currentChar == 'E' || currentChar == 'e') {
					nextState = 5;
					break;
				}

				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;

			case 5: 
				//State 5 has two valid transitions.  Each is addressed by an if statement.


				finalState = false;

				// If the current character is in the range from 1 to 9, it transitions to state 4
				if (currentChar >= '0' && currentChar <= '9') {
					nextState = 7;
					break;
				}
				// If the current character is an 'E' or 'e", it transitions to state 5
				else if (currentChar == '+' || currentChar == '-') {
					nextState = 6;
					break;
				}

				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;

			case 6: 
				//State 6 has one valid transitions.  It is addressed by an if statement.


				finalState = false;

				// If the current character is in the range from 1 to 9, it transitions to state 4
				if (currentChar >= '0' && currentChar <= '9') {
					nextState = 7;
					break;
				}

				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;

			case 7: 
				//State 7 has one valid transitions.  It is addressed by an if statement.

				// This is a final state
				finalState = true;

				// If the current character is in the range from 1 to 9, it transitions to state 4
				if (currentChar >= '0' && currentChar <= '9') {
					nextState = 7;
					break;
				}

				// If it is none of those characters, the FSM halts
				else 
					running = false;

				// The execution of this state is finished
				break;

			}

			if (running) {
				// When the processing of a state has finished, the FSM proceeds to the next character
				// in the input and if there is one, it fetches that character and updates the 
				// currentChar.  If there is no next character the currentChar is set to a blank.
				moveToNextCharacter();

				// Move to the next state
				state = nextState;

			}
			// Should the FSM get here, the loop starts again

		}

		measuredValueIndexofError = currentCharNdx;		// Copy the index of the current character;

		// When the FSM halts, we must determine if the situation is an error or not.  That depends
		// of the current state of the FSM and whether or not the whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states.
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			measuredValueIndexofError = currentCharNdx;		// Copy the index of the current character;
			measuredValueErrorMessage = "The first character must be a digit or a decimal point.";
			return "The first character must be a \"+\" sign, digit a decimal point.";

		case 1:
			// State 1 is a final state, so we must see if the whole string has been consumed.
			if (currentCharNdx<input.length()) {
				// If not all of the string has been consumed, we point to the current character
				// in the input line and specify what that character must be in order to move
				// forward.
				measuredValueErrorMessage = "This character may only be an \"E\", an \"e\", a digit, "
						+ "a \".\", or it must be the end of the input.\n";
				return measuredValueErrorMessage + displayInput(input, currentCharNdx);
			}
			else {
				measuredValueIndexofError = -1;
				measuredValueErrorMessage = "";
				return measuredValueErrorMessage;
			}

		case 2:
		case 4:
			// States 2 and 4 are the same.  They are both final states with only one possible
			// transition forward, if the next character is an E or an e.
			if (currentCharNdx<input.length()) {
				measuredValueErrorMessage = "This character may only be an \"E\", an \"e\", a digit or it must"
						+ " be the end of the input.\n";
				return measuredValueErrorMessage + displayInput(input, currentCharNdx);
			}
			// If there is no more input, the input was recognized.
			else {
				measuredValueIndexofError = -1;
				measuredValueErrorMessage = "";
				return measuredValueErrorMessage;
			}
		case 3:
		case 6:
			// States 3, and 6 are the same. None of them are final states and in order to
			// move forward, the next character must be a digit.
			measuredValueErrorMessage = "This character may only be a digit.\n";
			return measuredValueErrorMessage + displayInput(input, currentCharNdx);

		case 7:
			// States 7 is similar to states 3 and 6, but it is a final state, so it must be
			// processed differently. If the next character is not a digit, the FSM stops with an
			// error.  We must see here if there are no more characters. If there are no more
			// characters, we accept the input, otherwise we return an error
			if (currentCharNdx<input.length()) {
				measuredValueErrorMessage = "This character may only be a digit.\n";
				return measuredValueErrorMessage + displayInput(input, currentCharNdx);
			}
			else {
				measuredValueIndexofError = -1;
				measuredValueErrorMessage = "";
				return measuredValueErrorMessage;
			}

		case 5:
			// State 5 is not a final state.  In order to move forward, the next character must be
			// a digit or a plus or a minus character.
			measuredValueErrorMessage = "This character may only be a digit, a plus, or minus "
					+ "character.\n";
			return measuredValueErrorMessage + displayInput(input, currentCharNdx);


		default:
			return "";
		}
	}

	/**********
	 * This method is a mechanical transformation of a Finite State Machine diagram into a Java
	 * method for Error Terms.
	 * 
	 * @param input		The input string for the Finite State Machine
	 * @return			An output string that is empty if every things is okay or it will be
	 * 						a string with a help description of the error follow by two lines
	 * 						that shows the input line follow by a line with an up arrow at the
	 *						point where the error was found.
	 */	
	public static String checkErrorTerm(String input) {
		if(input.length() <= 0) return "";
		// The following are the local variable used to perform the Finite State Machine simulation
		state = 0;							// This is the FSM state number
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		currentChar = input.charAt(0);		// The current character from the above indexed position

		// The Finite State Machines continues until the end of the input is reached or at some
		// state the current character does not match any valid transition to a next state

		errorTermInput = input;			// Set up the alternate result copy of the input
		running = true;						// Start the loop
	

		// The Finite State Machines continues until the end of the input is reached or at some
		// state the current character does not match any valid transition to a next state
		while (running) {
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			switch (state) {
			case 0:
				// State 0 has three valid transitions.  Each is addressed by an if statement.

				// This is not a final state
				finalState = false;

				// If the current character is in the range from 1 to 9, it transitions to state 1
				if (currentChar >= '1' && currentChar <= '9') {
					nextState = 1;
					break;
				}
				// If the current character is a decimal point, it transitions to state 3
				else if (currentChar == '.') {
					nextState = 3;
					break;
				}
				// If the current character is 0, it transitions to state 8
				else if (currentChar == '0') {
					nextState = 8;
					break;
				}
				// If it is none of those characters, the FSM halts
				else
					running = false;

				// The execution of this state is finished
				break;

			case 1:
				// State 1 has three valid transitions.  Each is addressed by an if statement.

				// This is a final state
				finalState = true;

				// If the character is 0, it is accepted and we stay in same state.
				if (currentChar == '0') {
					break;
				}

				// If the current character is a decimal point, it transitions to state 2
				else if (currentChar == '.') {
					nextState = 2;
					break;
				}
				// If the current character is an E or an e, it transitions to state 5
				else if (currentChar == 'E' || currentChar == 'e') {
					nextState = 5;
					break;
				}
				// If it is none of those characters, the FSM halts
				else
					running = false;

				// The execution of this state is finished
				break;

			case 2:
				// State 2 has one valid transition.

				// This is a final state
				finalState = true;

				// If the current character is in the range from 0 to 9, it transitions to state 5
				if (currentChar == 'E' || currentChar == 'e') {
					nextState = 5;
					break;
				}

				// If it is none of those characters, the FSM halts
				else
					running = false;

				// The execution of this state is finished
				break;

			case 3:
				// State 3 has two valid transitions.  Each of them is addressed by an if statement.

				// This is not a final state
				finalState = false;

				// If the current character is in the range from 1 to 9, it transitions to state 4
				if (currentChar >= '1' && currentChar <= '9') {
					nextState = 4;
					break;
				}
				// If the character is 0, it is accepted and we stay in same state.
				else if (currentChar == '0') {
					break;
				}
				// If it is none of those characters, the FSM halts
				else
					running = false;

				// The execution of this state is finished
				break;

			case 4:
				// State 4 has one valid transition.

				// This is a final state
				finalState = true;

				// If the current character is an 'E' or 'e", it transitions to state 5
				if (currentChar == 'E' || currentChar == 'e') {
					nextState = 5;
					break;
				}

				// If it is none of those characters, the FSM halts
				else
					running = false;

				// The execution of this state is finished
				break;

			case 5:
				// State 5 has two valid transitions.  Each is addressed by an if statement.

				// This is a final state
				finalState = false;

				// If the current character is in the range from 0 to 9, it transitions to state 7
				if (currentChar >= '0' && currentChar <= '9') {
					nextState = 7;
					break;
				}
				// If the current character is an '+' or '-", it transitions to state 6
				else if (currentChar == '+' || currentChar == '-') {
					nextState = 6;
					break;
				}

				// If it is none of those characters, the FSM halts
				else
					running = false;

				// The execution of this state is finished
				break;

			case 6:
				// State 6 has one valid transition.

				// This is a final state
				finalState = false;

				// If the current character is in the range from 0 to 9, it transitions to state 7
				if (currentChar >= '0' && currentChar <= '9') {
					nextState = 7;
					break;
				}

				// If it is none of those characters, the FSM halts
				else
					running = false;

				// The execution of this state is finished
				break;

			case 7:
				// State 7 has one valid transition.

				// This is a final state
				finalState = true;

				// If the current character is in the range from 0 to 9, it remains in the same state.
				if (currentChar >= '0' && currentChar <= '9') {
					break;
				}
				// If it is none of those characters, the FSM halts
				else
					running = false;

				// The execution of this state is finished
				break;
			case 8:
				// State 8 has one valid transition.

				// This is not a final state
				finalState = false;

				// If the current character is a decimal point, it transitions to state 9
				if (currentChar == '.') {
					nextState = 9;
					break;
				}
				// If it is none of those characters, the FSM halts
				else
					running = false;

				// The execution of this state is finished
				break;
			case 9:
				// State 9 has two valid transitions.  Each of them is addressed by an if statement.

				// This is not a final state
				finalState = false;

				// If the current character is in the range from 1 to 9, it transitions to state 4
				if (currentChar >= '1' && currentChar <= '9') {
					nextState = 4;
					break;
				}
				// If the character is 0, it is accepted and we stay in same state.
				else if (currentChar == '0') {
					break;
				}
				// If it is none of those characters, the FSM halts
				else
					running = false;

				// The execution of this state is finished
				break;
			}

			if (running) {
				
				// When the processing of a state has finished, the FSM proceeds to the next character
				// in the input and if there is one, it fetches that character and updates the
				// currentChar.  If there is no next character the currentChar is set to a blank.
				moveToNextCharacter();

				// Move to the next state
				state = nextState;

			}
			// Should the FSM get here, the loop starts again

		}

		errorTermIndexofError = currentCharNdx;		// Copy the index of the current character;

		// When the FSM halts, we must determine if the situation is an error or not.  That depends
		// of the current state of the FSM and whether or not the whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states.
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			errorTermIndexofError = currentCharNdx;		// Copy the index of the current character;
			errorTermErrorMessage = "The first character must be a digit or a decimal point.";
			return "The first character must be a digit or a decimal point.";

		case 1:
			// State 1 is a final state, so we must see if the whole string has been consumed.
			if (currentCharNdx<input.length()) {
				// If not all of the string has been consumed, we point to the current character
				// in the input line and specify what that character must be in order to move
				// forward.
				errorTermErrorMessage = "This character may only be an \"E\", an \"e\", 0, "
						+ "a decimal, or it must be the end of the input.\n";
				return errorTermErrorMessage + displayInput(input, currentCharNdx);
			}
			else {
				errorTermIndexofError = -1;
				errorTermErrorMessage = "";
				return errorTermErrorMessage;
			}

		case 2:
			// States 2 and 4 are the same.  They are both final states with only one possible
			// transition forward, if the next character is an E or an e.
			if (currentCharNdx<input.length()) {
				errorTermErrorMessage = "This character may only be an \"E\", an \"e\", or it must"
						+ " be the end of the input.\n";
				return errorTermErrorMessage + displayInput(input, currentCharNdx);
			}
			// If there is no more input, the input was recognized.
			else {
				errorTermIndexofError = -1;
				errorTermErrorMessage = "";
				return errorTermErrorMessage;
			}
		case 4:
			// States 2 and 4 are the same.  They are both final states with only one possible
			// transition forward, if the next character is an E or an e.
			if (currentCharNdx<input.length()) {
				errorTermErrorMessage = "This character may only be an \"E\", an \"e\", or it must"
						+ " be the end of the input.\n";
				return errorTermErrorMessage + displayInput(input, currentCharNdx);
			}
			// If there is no more input, the input was recognized.
			else {
				errorTermIndexofError = -1;
				errorTermErrorMessage = "";
				return errorTermErrorMessage;
			}
		case 3:
			// States 3, and 6 are the same. None of them are final states and in order to
			// move forward, the next character must be a digit.
			errorTermErrorMessage = "This character may only be a digit.\n";
			return errorTermErrorMessage + displayInput(input, currentCharNdx);
		case 6:
			// States 3, and 6 are the same. None of them are final states and in order to
			// move forward, the next character must be a digit.
			errorTermErrorMessage = "This character may only be a digit.\n";
			return errorTermErrorMessage + displayInput(input, currentCharNdx);

		case 7:
			// States 7 is similar to states 3 and 6, but it is a final state, so it must be
			// processed differently. If the next character is not a digit, the FSM stops with an
			// error.  We must see here if there are no more characters. If there are no more
			// characters, we accept the input, otherwise we return an error
			if (currentCharNdx<input.length()) {
				errorTermErrorMessage = "This character may only be a digit.\n";
				return errorTermErrorMessage + displayInput(input, currentCharNdx);
			}
			else {
				errorTermIndexofError = -1;
				errorTermErrorMessage = "";
				return errorTermErrorMessage;
			}

		case 8:
			errorTermErrorMessage = "This character may only be a decimal.\n";
			return errorTermErrorMessage + displayInput(input, currentCharNdx);

		case 9:
			errorTermErrorMessage = "This character may only be a digit.\n";
			return errorTermErrorMessage + displayInput(input, currentCharNdx);

		case 5:
			// State 5 is not a final state.  In order to move forward, the next character must be
			// a digit or a plus or a minus character.
			errorTermErrorMessage = "This character may only be a digit, a plus, or minus "
					+ "character.\n";
			return errorTermErrorMessage + displayInput(input, currentCharNdx);
		default:
			return "";
		}
	}
/*** UNIT SUPPORT ***/	
	
	/*****
	 * The following method converts the resultant values according to the unit change governed by choice of the user.
	 * Unlike performNormalization() method, this routine uses UNumber library to compute the resultant values.
	 * @param lastUnit Unit that is supposed to be converted. 
	 * @param newUnit	Changed / Converted Unit
	 */
	public static void convertResultantUnit(String lastUnit, String newUnit) {
		UNumber conversionFactor = new UNumber();  // Initialize UNumber value for the conversion factor
		int index_last = -2; int index_new = -2; // Indexes for both the units

		// Check if lastUnit is listed in the Lookup Table or not
		for (int i = 0; i<lookupTableForConversion.length;i++) {
			if (lookupTableForConversion[i][0].equals(lastUnit)) index_last = i;
		}
		// Check if the new unit is listed in the Lookup Table or not
		for (int j =0; j<lookupTableForConversion.length;j++) {
			if (lookupTableForConversion[0][j].equals(newUnit)) index_new = j;
		}
		// If both the units are present in the lookup table and the conversion is physically correct
		if (index_last>=0&&index_new>=0&&conversionPossibilityMapping[index_last][index_new]) {
			String formattedResult = null; //String which will be used to save the resultant value
			boolean negativeDetected = false;  // Boolean condition to check if the value is negative or not
			if (UserInterface.text_Result.getText().charAt(0)=='-') { // If the result is negative
				negativeDetected = true; // flag the boolean value as true
				formattedResult=UserInterface.text_Result.getText().substring(1); // and ignore the minus symbol in the formatted result
			}
			else formattedResult=UserInterface.text_Result.getText(); // If it is positive, save it as the formatted result
			
			// Calculating the characteristics by knowing the index of decimal point
			int characteristic = formattedResult.indexOf('.'); 
			int characteristicET = UserInterface.text_ErrorTermResult.getText().indexOf('.');
			
			// Fetching the factor from the lookup table
			conversionFactor = new UNumber (Double.parseDouble(lookupTableForConversion[index_last][index_new]));
			
			// Defining UNumber values for both the result and resultant error term
			UNumber theResult = new UNumber(formattedResult,characteristic,true);
			UNumber theResultET = new UNumber(UserInterface.text_ErrorTermResult.getText(),characteristicET,true);
			
			// Multiplying both with the conversion factor to get the new resultant values
			theResult.mpy(conversionFactor); theResultET.mpy(conversionFactor);
			
			// If the value was initially negative, place a '-' symbol before the calculated result and display it. 
			// If the value was initially positive, then display the calculated value directly.
			if (negativeDetected) UserInterface.text_Result.setText('-'+theResult.toString()); 
			else UserInterface.text_Result.setText(theResult.toString()); 
			UserInterface.text_ErrorTermResult.setText(theResultET.toString());
		}
	}
}
