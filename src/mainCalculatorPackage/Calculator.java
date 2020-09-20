
package mainCalculatorPackage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/*******
 * <p> Title: Calculator Class. </p>
 * 
 * <p> Description: A JavaFX demonstration application and baseline for a sequence of projects </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2016 </p>
 * 
 * 
 * @author Lynn Robert Carter, JSGREWAL, Sumit, Shivam
 * 
 * @version 4.0	 2017-10-16 The mainline of a JavaFX-based GUI implementation of a long integer calculator
 * @version 4.10 2017-11-17 The implementation of subtraction, product and quotient with handling of division by zero Exception
 * @version 4.20 30-01-2018 Square root feature added, converted into double calculator.
 * @version 4.30 2018-02-25 Implementation of FSM to provide high quality error messages. 
 * @version 4.40 2018-03-15 Implementation of error terms calculation and respective high quality error Messages. 
 * @version 4.50 2018-04-13 Implementation of Estimated Measured Values and Error Terms with respect to digits of significance.
 * @version 5.10 2018-10-04 Implementation of UNumber Library to provide unlimited precision. Currently the precision is set to 25 significant digits.
 * @version 5.20 2018-11-22 Implementation of Unit Support System with Signal Normalization as well as Inter-Conversion of Units of Same Physical Dimensions.
 * <strong>Version 5.20 is designed and implemented keeping Hohmann Transfer Equations and Respective Calculations in consideration.</strong>
 * @version 6.00 2019-02-24 Implementation of the Programmable Calculator Application
 */

public class Calculator extends Application {

	public final static double WINDOW_WIDTH = 1280;
	public final static double WINDOW_HEIGHT = 650;

	public UserInterface theGUI;

	/**********
	 * This is the start method that is called once the application has been loaded into memory and is 
	 * ready to get to work.
	 * 
	 * In designing this application I have elected to IGNORE all opportunities for automatic layout
	 * support and instead have elected to manually position each GUI element and its properties in
	 * order to exercise complete control over the user interface look and feel.
	 * 
	 */
	@Override
	public void start(Stage theStage) throws Exception {

		theStage.setTitle("Jaskirat Singh Grewal, Sumit Singh, Shivam Singhal");// Label the stage (a window)

		Pane theRoot = new Pane();							// Create a pane within the window

		theGUI = new UserInterface(theRoot);					// Create the Graphical User Interface

		Scene theScene = new Scene(theRoot, WINDOW_WIDTH, WINDOW_HEIGHT);	// Create the scene

		theStage.setScene(theScene);							// Set the scene on the stage
		theStage.initStyle(StageStyle.DECORATED); // and style it decorated
		theStage.show();										// Show the stage to the user

		// When the stage is shown to the user, the pane within the window is visible.  This means that the
		// labels, fields, and buttons of the Graphical User Interface (GUI) are visible and it is now 
		// possible for the user to select input fields and enter values into them, click on buttons, and 
		// read the labels, the results, and the error messages.
	}



	/*******************************************************************************************************/

	/*******************************************************************************************************
	 * This is the method that launches the JavaFX application
	 * 
	 */
	public static void main(String[] args) {						// This method may not be required
		launch(args);											// for all JavaFX applications using
	}															// other IDEs.
}
