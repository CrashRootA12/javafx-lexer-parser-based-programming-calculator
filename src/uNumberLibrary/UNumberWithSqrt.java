package uNumberLibrary;
/***
 * <p> Title: UNumber With Square Root Class </p>
 * <p> Description: This class is sub-class which inherits all the properties of UNumber Class
 * and contains new methods which compute the square root of Ultimate Precision UNumbers. </p>
 * 
 * 
 * @author JSGREWAL, Lynn Robert Carter
 * @version 1.00 Implementation of Newton Raphson Method for Calculating Square Roots.
 * @version 1.01 <b>Resubmission Update</b> Updated the program to show iteration status of Newton Raphson Method with Convergence Speed.
 *
 */
public class UNumberWithSqrt extends UNumber {
	/*******************************************
	 *  Constructors of Super Class
	 *******************************************/
	public UNumberWithSqrt() {
		super();
	}

	public UNumberWithSqrt(int v) {
		super(v);
	}

	public UNumberWithSqrt(long v) {
		super(v);

	}

	public UNumberWithSqrt(String str, int dec, boolean sign) {
		super(str, dec, sign);

	}

	public UNumberWithSqrt(String str, int dec, boolean sign, int size) {
		super(str, dec, sign, size);
	}

	public UNumberWithSqrt(UNumber that) {
		super(that);

	}

	public UNumberWithSqrt(UNumber that, int size) {
		super(that, size);

	}

	public UNumberWithSqrt(UNumber that, UNumber another) {
		super(that, another);

	}

	public UNumberWithSqrt(double v) {
		super(v);

	}
	/*****
	 * This private method counts how many digits are the same between two estimates
	 * @author Lynn Robert Carter
	 */
	private static int howManyDigitsMatch(UNumber newGuess, UNumber oldGuess, int maxMatchingDigits) {
		// If the characteristics are not the same, the digits in the mantissa do not matter
		if (newGuess.getCharacteristic() != oldGuess.getCharacteristic()) return 0;

		// The characteristic is the same, so fetch the mantissas so we can compare them
		String newGuessStr = newGuess.getMantissa();
		String oldGuessStr = oldGuess.getMantissa();
		// Set the upper limit;
		int maxIterations = maxMatchingDigits;

		/* No need to do this because we are working with the mantissa, so there are not decimal points
	for (int ndx = 0; ndx<15; ndx++) {
		if (newGuessStr.charAt(ndx) == '.') {
			String start = newGuessStr.substring(0, ndx);
			String rest = newGuessStr.substring(ndx+1);
			newGuessStr = start + rest;
			break;
		}
	}

	for (int ndx = 0; ndx<maxMatchingDigits; ndx++) 
		if (oldGuessStr.charAt(ndx) == '.') {
			String start = oldGuessStr.substring(0, ndx);
			String rest = oldGuessStr.substring(ndx+1);
			oldGuessStr = start + rest;
			break;
		}
		 */

		// Loop through the digits as long as they match
		for (int ndx = 0; ndx < maxIterations; ndx++) {
			if (oldGuessStr.charAt(ndx) != newGuessStr.charAt(ndx)) return ndx;
		}

		// If the loop completes, we consider all 15 to match
		return maxMatchingDigits;
	}
	/***
	 * This Public Method Calculates square root of a Given UNumber by Using Newton Raphson Method
	 * @author JSGREWAL
	 * @param uno The UNumber whose square root is to be calculated.
	 * @return result The Square root of the given number.
	 */
	public UNumber sqrt(UNumber uno ) {

		int digitsMatch = 0;	//Initialize the variable which shows number of matching digits.
		UNumber nextEstimates = new UNumber(uno); //Initialize a Copy UNumber
		nextEstimates = new UNumber(nextEstimates, 24); //Set the size of the copy
		//The First Estimate requires division of given UNumber by Two 
		UNumber uTwo= new UNumber(2.0); //hence create UNumber for Two
		uTwo = new UNumber(uTwo, 24) ;//according to size of input UNumber

		nextEstimates.div(uTwo);//This divides the UNumber to its half

		UNumber firstEstimate; // Initialize the first estimate
		/***
		 * The following do-while loop calculates consecutive estimates
		 * until atleast 25 digits match between the two consecutive estimates. 
		 * Do-While loop is used because the program inside it needs to be executed atleast once.
		 */

		do{	//Execute the below code atleast once

		
			firstEstimate = nextEstimates; //Set up the first estimate
			UNumber copyUno = new UNumber(uno); //Create another copy of entered UNumber
			copyUno = new UNumber(copyUno, 24); //of the same size 
			/***************************************************
			 * The Newton Raphson Method For Calculating Square Root
			 */
			copyUno.div(firstEstimate); //Divide the entered value by the previous estimate
			copyUno.add(firstEstimate); //and add the same estimate to the resultant
			copyUno.div(uTwo);//Then divide the final resultant by two
			/******************************************************/
			nextEstimates = copyUno; //The result will act as previous estimate for next estimate
			digitsMatch = howManyDigitsMatch(nextEstimates, firstEstimate, 24);	// Determine how many digits match
			/******************************************************
			 *Show User the Status of Each Iteration with the Convergence Speed of Each Iteration
			 ****/
			
			/******************************************************/
		}while (digitsMatch < 24); //The estimation continues atleast 25 digits match
		/* After atleast matching of 25 digits */
		return nextEstimates; //Return the final estimate as the square root.

	}


}
