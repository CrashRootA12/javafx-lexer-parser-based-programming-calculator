
package mainCalculatorPackage;



/**
 * <p> Title: BusinessLogic Class. </p>
 * 
 * <p> Description: The code responsible for performing the calculator business logic functions. 
 * This method deals with CalculatorValues and performs actions on them.  The class expects data
 * from the User Interface to arrive as Strings and returns Strings to it.  This class calls the
 * CalculatorValue class to do computations and this class knows nothing about the actual 
 * representation of CalculatorValues, that is the responsibility of the CalculatorValue class and
 * the classes it calls.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2017 </p>
 * 
 * @author Lynn Robert Carter, JSGREWAL, Sumit, Shivam
 * 
 * @version 4.00	2014-10-18 The JavaFX-based GUI implementation of a long integer calculator 
 * @version 4.10	2018-01-15 The implementation of integer subtraction, multiplication and division is done.
 * @version 4.20	2018-02-02 The Square Root method initialized with change in error meassages.
 * @version 4.30	2018-02-25 Implementation of FSM to provide high quality error messages. 
 * @version 4.40 	2018-03-15 Implementation of error terms calculation and respective high quality error Messages. 
 * @version 4.50 	2018-04-13 Implementation of Estimated Measured Values and Error Terms with respect to digits of significance.
 * @version 5.10 	2018-10-04 Implementation of UNumber Library to provide unlimited precision. Currently the precision is set to 25 significant digits.
 * @version 5.20    2018-11-22 Implementation of Unit Support System with Signal Normalization as well as Inter-Conversion of Units of Same Physical Dimensions.
 * <strong>Version 5.20 is designed and implemented keeping Hohmann Transfer Equations and Respective Calculations in consideration.</strong>
 * @version 6.00	2019-02-24 Updated for supporting the Programmable Calculator UI
 * ***/
public class BusinessLogic {

	/**********************************************************************************************

	Attributes

	 **********************************************************************************************/

	// These are the major calculator values 
	private CalculatorValue operand1 = new CalculatorValue(0,0);
	private CalculatorValue operand2 = new CalculatorValue(0,0);
	private CalculatorValue errorTerm1 =  new CalculatorValue(0,0);
	private CalculatorValue errorTerm2 =  new CalculatorValue(0,0);
	private CalculatorValue result = new CalculatorValue(0,0);
	private CalculatorValue resultErrorTerm = new CalculatorValue(0,0);
	private String operand1ErrorMessage = "";
	private boolean operand1Defined = false;
	private String operand2ErrorMessage = "";
	private boolean operand2Defined = false;
	private String resultErrorMessage = "";
	private String resultErrorTermErrorMessage = "";
	private boolean errorTerm1Defined = false;
	private boolean errorTerm2Defined = false;
	private String errorTerm1ErrorMessage = "";
	private String errorTerm2ErrorMessage = "";



	/**********************************************************************************************

	Constructors

	 **********************************************************************************************/

	/**********
	 * This method initializes all of the elements of the business logic aspect of the calculator.
	 * There is no special computational initialization required, so the initialization of the
	 * attributes above are all that are needed.
	 */
	public BusinessLogic() {
	}

	/**********************************************************************************************

	Getters and Setters

	 **********************************************************************************************/

	/**********
	 * This public method takes an input String, checks to see if there is a non-empty input string.
	 * If so, it places the converted CalculatorValue into operand1, any associated error message
	 * into operand1ErrorMessage, and sets flags accordingly.
	 * 
	 * @param value
	 * @return	True if the set did not generate an error; False if there was invalid input
	 */
	public boolean setOperand1(String value, String errorTermS, String unit) throws NumberFormatException {
		operand1Defined = false;		// Assume the operand will not be defined
		errorTerm1Defined=false;
		if (value.length() <= 0 || errorTermS.length()<=0) {						// See if the input is empty. If so no error
			operand1ErrorMessage = "";					// message, but the operand is not defined.
			errorTerm1ErrorMessage="";
			return true;									// Return saying there was no error.
		}
		try {
			operand1 = new CalculatorValue(value ,errorTermS,unit);
		}
		catch(NumberFormatException Ex) {
			Ex.printStackTrace();
		}
		// If there was input text, try to convert it
		operand1ErrorMessage = operand1.getErrorMessage();	// into a CalculatorValue and see if it
		errorTerm1ErrorMessage=errorTerm1.getErrorMessage();
		if (operand1ErrorMessage.length() > 0 && errorTerm1ErrorMessage.length()>0) 			// worked. If there is a non-empty error 
			return false;								// message, signal there was a problem.
		operand1Defined = true;							// Otherwise, set the defined flag and
		errorTerm1Defined=true;
		return true;										// signal that the set worked
	}


	/**********
	 * This public method takes an input String, checks to see if there is a non-empty input string.
	 * If so, it places the converted CalculatorValue into operand2, any associated error message
	 * into operand1ErrorMessage, and sets flags accordingly.
	 * 
	 * The logic of this method is the same as that for operand1 above.
	 * 
	 * @param value
	 * @return	True if the set did not generate an error; False if there was invalid input
	 */
	public boolean setOperand2(String value, String errorTermS,String unit) throws NumberFormatException {			// The logic of this method is exactly the
		operand2Defined = false;		// Assume the operand will not be defined
		errorTerm2Defined=false;
		if (value.length() <= 0 || errorTermS.length()<=0) {						// See if the input is empty. If so no error
			operand2ErrorMessage = "";					// message, but the operand is not defined.
			errorTerm2ErrorMessage="";
			return true;									// Return saying there was no error.
		}
		try {
			operand2 = new CalculatorValue(value ,errorTermS,unit);
		}
		catch(NumberFormatException Ex) {
			Ex.printStackTrace();
		}
		// If there was input text, try to convert it
		operand2ErrorMessage = operand2.getErrorMessage();	// into a CalculatorValue and see if it
		errorTerm2ErrorMessage=errorTerm2.getErrorMessage();
		if (operand2ErrorMessage.length() > 0 && errorTerm2ErrorMessage.length()>0) 			// worked. If there is a non-empty error 
			return false;								// message, signal there was a problem.
		operand2Defined = true;							// Otherwise, set the defined flag and
		errorTerm2Defined=true;
		return true;										// signal that the set worked
	}





	/**********
	 * This public method takes an input String, checks to see if there is a non-empty input string.
	 * If so, it places the converted CalculatorValue into result, any associated error message
	 * into resuyltErrorMessage, and sets flags accordingly.
	 * 
	 * The logic of this method is similar to that for operand1 above. (There is no defined flag.)
	 * 
	 * @param value
	 * @return	True if the set did not generate an error; False if there was invalid input
	 */
	public boolean setResult(String value) {				// The logic of this method is similar to
		if (value.length() <= 0) {						// that for operand1, above.
			operand2ErrorMessage = "";
			return true;
		}
		result = new CalculatorValue(value);
		resultErrorMessage = operand2.getErrorMessage();
		if (operand2ErrorMessage.length() > 0)
			return false;
		return true;
	}

	/**********
	 * This public method takes an input String, checks to see if there is a non-empty input string.
	 * If so, it places the converted CalculatorValue into result, any associated error message
	 * into resultErrorTermErrorMessage, and sets flags accordingly.
	 * 
	 * The logic of this method is similar to that for operand1 above. (There is no defined flag.)
	 * 
	 * @param errorTermS
	 * @return	True if the set did not generate an error; False if there was invalid input
	 */

	public boolean setResultErrorTerm (String errorTermS) {
		if (errorTermS.length()<=0) {
			errorTerm2ErrorMessage = "";
			return true;
		}

		resultErrorTerm = new CalculatorValue(0,Double.parseDouble(errorTermS));
		resultErrorTermErrorMessage = errorTerm2.getErrorMessage();

		if (errorTerm2ErrorMessage.length()>0)
			return false;
		return true;

	}

	/**********
	 * This public setter sets the String explaining the current error in operand1.
	 * 
	 * @return
	 */
	public void setOperand1ErrorMessage(String m) {
		operand1ErrorMessage = m;
		return;
	}

	/**********
	 * This public getter fetches the String explaining the current error in operand1, it there is one,
	 * otherwise, the method returns an empty String.
	 * 
	 * @return and error message or an empty String
	 */
	public String getOperand1ErrorMessage() {
		return operand1ErrorMessage;
	}

	/**********
	 * This public setter sets the String explaining the current error into operand1.
	 * 
	 * @return
	 */
	public void setOperand2ErrorMessage(String m) {
		operand2ErrorMessage = m;
		return;
	}

	/**********
	 * This public getter fetches the String explaining the current error in operand2, it there is one,
	 * otherwise, the method returns an empty String.
	 * 
	 * @return and error message or an empty String
	 */
	public String getOperand2ErrorMessage() {
		return operand2ErrorMessage;
	}

	/**********
	 * This public setter sets the String explaining the current error in the result.
	 * 
	 * @return
	 */
	public void setResultErrorMessage(String m) {
		resultErrorMessage = m;
		return;
	}

	/**********
	 * This public getter fetches the String explaining the current error in the result, it there is one,
	 * otherwise, the method returns an empty String.
	 * 
	 * @return and error message or an empty String
	 */
	public String getResultErrorMessage() {
		return resultErrorMessage;
	}
	/**********
	 * This public getter fetches the String explaining the current error in the error term result, it there is one,
	 * otherwise, the method returns an empty String.
	 * 
	 * @return and error message or an empty String
	 */
	public String getResultErrorTermErrorMessage() {
		return resultErrorTermErrorMessage;
	}

	/**********
	 * This public getter fetches the defined attribute for operand1. You can't use the lack of an error 
	 * message to know that the operand is ready to be used. An empty operand has no error associated with 
	 * it, so the class checks to see if it is defined and has no error before setting this flag true.
	 * 
	 * @return true if the operand is defined and has no error, else false
	 */
	public boolean getOperand1Defined() {
		return operand1Defined;
	}

	/**********
	 * This public getter fetches the defined attribute for operand2. You can't use the lack of an error 
	 * message to know that the operand is ready to be used. An empty operand has no error associated with 
	 * it, so the class checks to see if it is defined and has no error before setting this flag true.
	 * 
	 * @return true if the operand is defined and has no error, else false
	 */
	public boolean getOperand2Defined() {
		return operand2Defined;
	}
	/**********
	 * This public getter fetches the defined attribute for ErrorTerm 1. You can't use the lack of an error 
	 * message to know that the error term is ready to be used. An empty error term has no error associated with 
	 * it, so the class checks to see if it is defined and has no error before setting this flag true.
	 * 
	 * @return true if the error term  is defined and has no error, else false
	 */

	public boolean getErrorTerm1Defined() {
		return errorTerm1Defined;
	}
	/**********
	 * This public getter fetches the defined attribute for ErrorTerm 2. You can't use the lack of an error 
	 * message to know that the error term is ready to be used. An empty error term has no error associated with 
	 * it, so the class checks to see if it is defined and has no error before setting this flag true.
	 * 
	 * @return true if the error term  is defined and has no error, else false
	 * **/
	public boolean getErrorTerm2Defined() {
		return errorTerm2Defined;
	}
	/**********************************************************************************************

	The toString() Method

	 **********************************************************************************************/

	/**********
	 * This toString method invokes the toString method of the result type (CalculatorValue is this 
	 * case) to convert the value from its hidden internal representation into a String, which can be
	 * manipulated directly by the BusinessLogic and the UserInterface classes.
	 */
	public String toString() {
		return result.toString();

	}


	/**********
	 * This public toString method is used to display all the values of the BusinessLogic class in a
	 * textual representation for debugging purposes.
	 * 
	 * @return a String representation of the class
	 */
	public String debugToString() {
		String r = "\n******************\n*\n* Business Logic\n*\n******************\n";
		r += "operand1 = " + operand1.toString() + "\n";
		r += "     operand1ErrorMessage = " + operand1ErrorMessage+ "\n";
		r += "     operand1Defined = " + operand1Defined+ "\n";
		r += "operand2 = " + operand2.toString() + "\n";
		r += "     operand2ErrorMessage = " + operand2ErrorMessage+ "\n";
		r += "     operand2Defined = " + operand2Defined+ "\n";
		r += "result = " + result.toString() + "\n";
		r += "     resultErrorMessage = " + resultErrorMessage+ "\n";
		r += "*******************\n\n";
		return r;
	}


	/**********************************************************************************************

	Business Logic Operations : Addition, Subtraction, Multiplication and Division 

	 **********************************************************************************************/

	/**********
	 * The goal of this class is to support a wide array of different data representations 
	 * without requiring a change to this class, user interface class, or the Calculator class.
	 * 
	 * These methods assume the operands are defined and valid. It replaces the left operand with the 
	 * result of the computation and it leaves an error message, if there is one, in a String variable
	 * set aside for that purpose.
	 * 
	 * This method does not take advantage or know any detail of the representation!  All of that is
	 * hidden from this class by the CalculatorValue class and any other classes that it may use.
	 * 
	 * 
	 */

	/**********
	 * This is the addition method that performs operation on the operands and returns the  value of the result in String form.
	 * The method is called from the CalculatorValue class.
	 * @return a String representation of Sum.
	 *******/

	public String addition() {
		result = new CalculatorValue(operand1); 
		result.add(operand2);
		String finalValue=result.mvToString();
		return finalValue;
	}
	/**********
	 * This is the addition method that performs operation on the error terms and returns the  value of the result in String form.
	 * The method is called from the CalculatorValue class.
	 * @return a String representation of Sum.
	 *******/
	public String errorTermsAddition() { 
		resultErrorTerm = new CalculatorValue(operand1);
		resultErrorTerm.addET(operand2);
		String finalValue = resultErrorTerm.etToString();
		return finalValue;
	}


	/**********
	 * This is the subtraction method that performs operation on the operands and returns the  value of the result in String form.
	 * The method is called from the CalculatorValue class.
	 * @return a String representation of Difference.
	 *******/
	public String subtraction() {
		result = new CalculatorValue(operand1);
		result.sub(operand2);
		String finalVal = result.mvToString();
		return finalVal;
	}
	/**********
	 * This is the subtraction method that performs operation on the error terms and returns the  value of the result in String form.
	 * The method is called from the CalculatorValue class.
	 * @return a String representation of Difference.
	 ***/
	public String errorTermsSubtraction() {
		resultErrorTerm = new CalculatorValue(operand1);
		resultErrorTerm.subET(operand2);
		String finalValue = resultErrorTerm.etToString();
		return finalValue;
	}


	/*************
	 * This is the multiplication method that performs operation on the operands and returns the String value of their product.
	 * The method is called from CalculatorValue Class.
	 * @return The string value of Product.
	 *  */
	public String multiplication() {
		result = new CalculatorValue(operand1);
		result.mpy(operand2);
		String finalVal = result.mvToString();
		return finalVal;	
	}
	/**********
	 * This is the multiplication method that performs operation on the error terms and returns the  value of the result in String form.
	 * The method is called from the CalculatorValue class.
	 * @return a String representation of product.
	 *******/
	public String errorTermsMultiplication() {
		resultErrorTerm = new CalculatorValue(operand1);
		resultErrorTerm.mpyET(operand2);
		String finalValue = resultErrorTerm.etToString();
		return finalValue;
	}
	/******************
	 * This is the division method that performs operation on the operands and returns the String value of their quotient.
	 * This method is called from CalculatorValue class.
	 * 
	 * @return The string value of Quotient.
	 */
	public String division() {
		result = new CalculatorValue(operand1);                  
		result.div(operand2); 
		String finalVal = result.mvToString();
		return finalVal;

	}
	/******************
	 * This is the division method that performs operation on the error terms and returns the String value of their quotient.
	 * This method is called from CalculatorValue class.
	 * @return The string value of Quotient.
	 */
	public String errorTermsDivision() {
		resultErrorTerm = new CalculatorValue(operand1);
		resultErrorTerm.divET(operand2);     
		String divETResult = resultErrorTerm.etToString();
		String finalValue = divETResult;
		return finalValue;
	}

	/****
	 * This is the Square Root operation which performs the operation on the first operand and return the square root of operand.
	 * This method is called from CalculatorValue class.
	 * 
	 * @return The string value of the Square Root.
	 */
	public String squareroot() { 
		result = new CalculatorValue(operand1);
		result.sqrt(operand1);
		String finalVal = result.mvToString();
		return finalVal;	
	}

	/****
	 * This is the Square Root operation which performs the operation on the first operand's error term and return the square root of operand.
	 * This method is called from CalculatorValue class.
	 * 
	 * @return The string value of the Square Root.
	 */
	public String errorTermsSquareroot() {
		resultErrorTerm = new CalculatorValue(operand1);
		resultErrorTerm.sqrtET();
		String finalValue = resultErrorTerm.etToString();
		return finalValue;
	}

}



