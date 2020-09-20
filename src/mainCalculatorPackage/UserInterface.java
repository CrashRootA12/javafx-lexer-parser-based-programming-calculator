package mainCalculatorPackage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import programManagementPackage.ProgramsUserInterface;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import definitionPackage.DefinitionsUserInterface;

/**
 * <p> Title: UserInterface Class. </p>
 * 
 * <p> Description: The Java/FX-based user interface for the calculator. The class works with String
 * objects and passes work to other classes to deal with all other aspects of the computation.</p>
 * 
 * <p> Copyright: Lynn Robert Carter Â© 2017 </p>
 * 
 * @author Lynn Robert Carter, JSGREWAL
 * @author Sumit, Shivam
 * @version 4.00 2017-10-17 The JavaFX-based GUI for the implementation of a calculator
 * @version 4.10 2018-01-15 The implementation of integer subtraction, multiplication and division is done.
 * @version 4.20 2018-01-30 Square root feature added, converted it into floating values calculator with error messages modified.
 * @version 4.30 2018-02-25 Implementation of FSM to provide high quality error messages. 
 * @version 4.40 2018-03-15 Implementation of error terms calculation and respective high quality error Messages. 
 * @version 4.50 2018-04-13 Implementation of Estimated Measured Values and Error Terms with respect to digits of significance.
 * @version 5.10 2018-10-04 Implementation of UNumber Library to provide unlimited precision. Currently the precision is set to 25 significant digits.
 * @version 5.20    2018-11-22 Implementation of Unit Support System with Signal Normalization as well as Inter-Conversion of Units of Same Physical Dimensions.
 * <strong>Version 5.20 is designed and implemented keeping Hohmann Transfer Equations and Respective Calculations in consideration.</strong>
 * @version 6.0 2019-02-24 Initial Implementation of Programmable Calculator using the Dictionary API.
 * @version 7.0 2019-03-31 Integrated the Expression Solver and Builder.
 */

public class UserInterface {


	/**********************************************************************************************

Attributes

	 **********************************************************************************************/

	/* Constants used to parameterize the graphical user interface.  We do not use a layout manager for
   this application. Rather we manually control the location of each graphical element for exact
   control of the look and feel. */
	private final double BUTTON_WIDTH = 40;
	private final double BUTTON_OFFSET = BUTTON_WIDTH / 2;
	private Button button_Def = new Button("Manage Variables and Constants");
	// These are the application values required by the user interface
	private Label label_UNumberCalculator = new Label("Science And Engineering Calculator with Units");
	private Label label_Operand1 = new Label(" First operand ");
	private TextField text_Operand1 = new TextField();
	private Label label_Operand2 = new Label(" Second operand");
	private TextField text_Operand2 = new TextField();
	private Label label_Result = new Label("Result ");
	public static TextField text_Result = new TextField();
	private static TextField text_ErrorTerm1 = new TextField();
	private TextField text_ErrorTerm2 = new TextField();
	public static TextField text_ErrorTermResult = new TextField();
	public static File currentRepositoryFile = null;
	public DefinitionsUserInterface defGUI=null;
	private Button button_LaunchProgCalc = new Button("Launch Programmable Calculator");
	//Plus-Minus Symbols
	private Label label_plusMinusOperand1 = new Label ("\u00B1");
	private Label label_plusMinusOperand2 = new Label ("\u00B1");
	private Label label_plusMinusResult = new Label ("\u00B1");

	private Button button_Add = new Button("+");
	private Button button_Sub = new Button("-");
	private Button button_Mpy = new Button("\u00D7"); // The multiply symbol: \u00D7
	private Button button_Div = new Button("\u00F7"); // The divide symbol: \u00F7
	private Button button_Sqrt = new Button("\u221A");               //The square root  symbol: \u221A
	private Button button_saveResult = new Button("Save Result");

	//Initialize the Error Messages Here

	private Label label_errOperand1 = new Label("");                // Label to display specific 
	private Label label_errOperand2 = new Label("");                // error messages
	private Label label_errOperand1UpSpecific = new Label("");               // Label to display a error message 
	private Label label_errOperand2UpSpecific = new Label("");             // when user tries to perform any function 
	private Label label_errResult = new Label("");

	private Text operand1ErrPart1 = new Text();                     
	private Text operand1ErrPart2 = new Text();

	private Text operand2ErrPart1 = new Text();
	private Text operand2ErrPart2 = new Text();

	private Label label_errOperand1ETerm = new Label("");                 
	private Label label_errOperand2ETerm = new Label(""); 

	private Text errOperand2_ETPart1 = new Text();
	private Text errOperand2_ETPart2 = new Text();
	private Text errOperand1_ETPart1 = new Text();
	private Text errOperand1_ETPart2= new Text();

	//Initialize the combo-boxes here for unit-support
	private ComboBox<String> cb_operand1 = new ComboBox<String>();
	private ComboBox<String> cb_operand2 = new ComboBox<String>();
	private ComboBox<String> cb_result = new ComboBox<String>();
	/**
	 * Text Flow is a Layout to use Rich Text Values.
	 * The TextFlow uses the text and the font of each Text node inside of it plus it own width and 
	 * text alignment to determine the location for each child.
	 * The x and y properties of the Text node are ignored since the location of the node is determined by the parent. 
	 */
	private TextFlow err1;
	private TextFlow err2;
	private TextFlow err1et;
	private TextFlow err2et;

	private double buttonSpace; // This is the white space between the operator buttons.

	/* This is the link to the business logic */
	public BusinessLogic perform = new BusinessLogic();
	private String thesub;
	private String theprod;
	private String thediv;
	private String thesqrt;    
	/*** These boolean values will be used by unit-support methods ***/
	private boolean addIsSelected = false;
	private boolean subIsSelected = false;
	private boolean mpyIsSelected = false;
	private boolean divIsSelected = false;
	private boolean sqrtIsSelected = false;
	private  boolean firstTime = true;
	private List<String> list_LengthUnits = new Vector<String>();
	private List<String> list_VelocityUnits = new Vector<String>();
	private List<String> list_TimeUnits = new Vector<String>();
	private String previouslySelectedResultantUnit= null;
	private Label label_Units= new Label("Units");
	String filePath = null;
	String measuredValue = "0";
	String errorTerm = "0";
	String unit = "0";
	String measuredValue2 = "0";
	String errorTerm2 = "0";
	String unit2 = "";

	/**********************************************************************************************
		Constructors
	 **********************************************************************************************/

	/**********
	 * This method initializes all of the elements of the graphical user interface. These assignments
	 * determine the location, size, font, color, and change and event handlers for each GUI object.
	 */

	public UserInterface(Pane theRoot) {
		// There are six gaps. Compute the button space accordingly.
		buttonSpace = Calculator.WINDOW_WIDTH / 6;

		// Label theScene with the name of the calculator, centered at the top of the pane
		setupLabelUI(label_UNumberCalculator, "Arial", 24, Calculator.WINDOW_WIDTH, Pos.CENTER, 0, 10);

		// Label the first operand just above it, left aligned
		setupLabelUI(label_Operand1, "Arial", 18, Calculator.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 70);

		// Establish the first text input operand field and when anything changes in operand 1,
		// process both fields to ensure that we are ready to perform as soon as possible.
		setupTextUI(text_Operand1, "Arial", 18, 400, Pos.BASELINE_LEFT, 180, 70, true);
		text_Operand1.textProperty().addListener((observable, oldValue, newValue) -> {try {
			setOperand1();
		} catch (IOException e) {

			e.printStackTrace();
		} });
		// Move focus to the error term when the user presses the enter (return) key
		text_Operand1.setOnAction((event) -> { text_ErrorTerm1.requestFocus();});

		//Plus Minus Label for Operand 1
		setupLabelUI(label_plusMinusOperand1, "Cambria Math", 28, 200, Pos.BASELINE_LEFT, 620, 70 );

		//Error Term Text Field for Operand 1
		setupTextUI(text_ErrorTerm1, "Arial", 18, 340, Pos.BASELINE_LEFT, 670, 70, true);
		text_ErrorTerm1.textProperty().addListener((observable, oldValue, newValue) -> {try {
			setOperand1();
		} catch (IOException e) {

			e.printStackTrace();
		}  });

		// Move focus to the operand 2  when the user presses the enter (return) key
		text_ErrorTerm1.setOnAction((event) -> { text_Operand2.requestFocus();});

		// Establish an error message for the first operand just above it with, left aligned
		// Label the Second operand just above it, left aligned
		setupLabelUI(label_Operand2, "Arial", 18, Calculator.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 160);

		// Establish the second text input operand field and when anything changes in operand 2,
		// process both fields to ensure that we are ready to perform as soon as possible.
		setupTextUI(text_Operand2, "Arial", 18, 400, Pos.BASELINE_LEFT, 180, 160, true);
		text_Operand2.textProperty().addListener((observable, oldValue, newValue) -> {try {
			setOperand2();
		} catch (IOException e) {

			e.printStackTrace();
		} });
		// Move the focus to the error term 2 when the user presses the enter (return) key
		text_Operand2.setOnAction((event) -> { text_ErrorTerm2.requestFocus(); });

		//Error Term Text Field for Operand 2
		setupTextUI(text_ErrorTerm2, "Arial", 18, 340, Pos.BASELINE_LEFT, 670, 160, true);
		text_ErrorTerm2.textProperty().addListener((observable, oldValue, newValue) -> {try {
			setOperand2();
		} catch (IOException e) {

			e.printStackTrace();
		}});
		// Move focus to the result when the user presses the enter (return) key
		text_ErrorTerm2.setOnAction((event) -> { text_Result.requestFocus(); });

		//Plus Minus Label for Operand 2
		setupLabelUI(label_plusMinusOperand2, "Cambria Math", 28, 200, Pos.BASELINE_LEFT, 620, 160);

		// Label the result just above the result output field, left aligned
		setupLabelUI(label_Result, "Arial", 18, Calculator.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 10, 300);

		// Establish the result output field.  It is not editable, so the text can be selected and copied, 
		// but it cannot be altered by the user.  The text is left aligned.
		setupTextUI(text_Result, "Arial", 18, 400, Pos.BASELINE_LEFT, 180, 300, false);

		//Error Term Result
		setupTextUI(text_ErrorTermResult, "Arial", 18, 340, Pos.BASELINE_LEFT, 670, 300, false);

		//Plus Minus Label for Result
		setupLabelUI(label_plusMinusResult, "Cambria Math", 28, 200, Pos.BASELINE_LEFT, 620, 300);

		setupLabelUI(label_Units," Arial",18,Calculator.WINDOW_WIDTH-10,Pos.BASELINE_LEFT,850+170,40);
		// Establish the ADD "+" button, position it, and link it to methods to accomplish its work
		setupButtonUI(button_Add, "Symbol", 32, BUTTON_WIDTH, Pos.BASELINE_LEFT, 1 * buttonSpace-BUTTON_OFFSET, 230);
		button_Add.setOnAction((event) -> {initiateSum();});

		// Establish the SUB "-" button, position it, and link it to methods to accomplish its work
		setupButtonUI(button_Sub, "Symbol", 32, BUTTON_WIDTH, Pos.BASELINE_LEFT, 2 * buttonSpace-BUTTON_OFFSET, 230);
		button_Sub.setOnAction((event) -> { initiateDifference();});

		// Establish the MPY "x" button, position it, and link it to methods to accomplish its work
		setupButtonUI(button_Mpy, "Symbol", 32, BUTTON_WIDTH, Pos.BASELINE_LEFT, 3 * buttonSpace-BUTTON_OFFSET, 230);
		button_Mpy.setOnAction((event) -> { initiateProduct();});

		// Establish the DIV "/" button, position it, and link it to methods to accomplish its work
		setupButtonUI(button_Div, "Symbol", 32, BUTTON_WIDTH, Pos.BASELINE_LEFT, 4 * buttonSpace-BUTTON_OFFSET, 230);
		button_Div.setOnAction((event) -> { initiateQuotient();});

		// Establish the SQRT button, and link it to methods to accomplish its work
		setupButtonUI(button_Sqrt, "Symbol", 32, BUTTON_WIDTH, Pos.BASELINE_LEFT, 5 * buttonSpace-BUTTON_OFFSET, 230 );
		button_Sqrt.setOnAction((event) -> {initiateSqrt();});


		/*********************************************************************************
		 *  Setting Up Error Messages
		 * 
		 *********************************************************************************/

		// Establish an error message for the first operand just above it , left aligned
		setupLabelUI(label_errOperand1UpSpecific, "Arial", 18, Calculator.WINDOW_WIDTH-10, Pos.BASELINE_LEFT, 180, 45);
		label_errOperand1UpSpecific.setTextFill(Color.RED);

		setupButtonUI(button_Def, "Symbol", 32, BUTTON_WIDTH, Pos.BASELINE_LEFT, 3 * buttonSpace-BUTTON_OFFSET,350);
		button_Def.setOnAction((event) -> { callDefinitionsUI(); });

		setupButtonUI(button_LaunchProgCalc,"Symbol",32,BUTTON_WIDTH,Pos.BASELINE_LEFT,3*buttonSpace-BUTTON_OFFSET,450);
		button_LaunchProgCalc.setOnAction((event)->{launchProgrammableCalculator();});
		setupButtonUI(button_saveResult, "Symbol", 32, BUTTON_WIDTH, Pos.BASELINE_LEFT, buttonSpace-BUTTON_OFFSET, 350);
		button_saveResult.setOnAction((event)->{
			launchSaveResultPopup();
		});

		//FSM Specific Error Message for Operand 1 Error Term
		label_errOperand1ETerm.setTextFill(Color.RED);
		label_errOperand1ETerm.setAlignment(Pos.BASELINE_RIGHT);
		setupLabelUI(label_errOperand1ETerm, "Arial", 14,Calculator.WINDOW_WIDTH-150-10, Pos.BASELINE_LEFT, 300, 118);
		//FSM Specific Error Message for Operand 2 Error Term
		label_errOperand2ETerm.setTextFill(Color.RED);
		label_errOperand2ETerm.setAlignment(Pos.BASELINE_RIGHT);
		setupLabelUI(label_errOperand2ETerm, "Arial", 14, Calculator.WINDOW_WIDTH-160, Pos.BASELINE_LEFT, 295,208);

		//FSM Specific Error Message for Operand 1
		label_errOperand1.setTextFill(Color.RED);
		label_errOperand1.setAlignment(Pos.BASELINE_RIGHT);
		setupLabelUI(label_errOperand1, "Arial", 14,  Calculator.WINDOW_WIDTH-150-10, Pos.BASELINE_LEFT, 22, 120);

		//FSM Specific Error Message for Operand 1
		label_errOperand2.setTextFill(Color.RED);
		label_errOperand2.setAlignment(Pos.BASELINE_RIGHT);
		setupLabelUI(label_errOperand2, "Arial", 14, Calculator.WINDOW_WIDTH-150-10, Pos.BASELINE_LEFT, 22, 210);
		label_errOperand2.setTextFill(Color.RED);

		//Establish an FSM Supported Error Message Text Flow Element for Operand 1 Error Term
		errOperand1_ETPart1.setFill(Color.RED);
		errOperand1_ETPart1.setFont(Font.font("Arial", FontPosture.REGULAR, 18));
		errOperand1_ETPart2.setFill(Color.RED);
		errOperand1_ETPart2.setFont(Font.font("Arial", FontPosture.REGULAR, 24));
		err1et =  new TextFlow(errOperand1_ETPart1, errOperand1_ETPart2);

		//Establish an FSM Supported Error Message Text Flow Element for Operand 2 Error Term
		errOperand2_ETPart1.setFill(Color.RED);
		errOperand2_ETPart1.setFont(Font.font("Arial", FontPosture.REGULAR, 18));
		errOperand2_ETPart2.setFill(Color.RED);
		errOperand2_ETPart2.setFont(Font.font("Arial", FontPosture.REGULAR, 24));
		err2et =  new TextFlow(errOperand2_ETPart1, errOperand2_ETPart2);
		//Aligning the Text Flow Elements
		err1et.setMinWidth(Calculator.WINDOW_WIDTH-10);
		err1et.setLayoutX(510+170);
		err1et.setLayoutY(100);
		err2et.setMinWidth(Calculator.WINDOW_WIDTH-10);
		err2et.setLayoutX(510+170);
		err2et.setLayoutY(190);
		//Establish an FSM Supported Error Message Text Flow Element for Operand 1
		operand1ErrPart1.setFill(Color.RED);
		operand1ErrPart1.setFont(Font.font("Arial", FontPosture.REGULAR, 18));
		operand1ErrPart2.setFill(Color.RED);
		operand1ErrPart2.setFont(Font.font("Arial", FontPosture.REGULAR, 24));
		err1 = new TextFlow(operand1ErrPart1, operand1ErrPart2);
		err1.setMinWidth(Calculator.WINDOW_WIDTH-60); 
		err1.setLayoutX(22+170);  
		err1.setLayoutY(100);

		//Establish an FSM Supported Error Message Text Flow Element for Operand 2
		operand2ErrPart1.setFill(Color.RED);
		operand2ErrPart1.setFont(Font.font("Arial", FontPosture.REGULAR, 18));
		operand2ErrPart2.setFill(Color.RED);
		operand2ErrPart2.setFont(Font.font("Arial", FontPosture.REGULAR, 24));
		err2 = new TextFlow(operand2ErrPart1, operand2ErrPart2);
		err2.setMinWidth(Calculator.WINDOW_WIDTH-10); 
		err2.setLayoutX(22+170);  
		err2.setLayoutY(190); 

		//Establish three combo-boxes for the unit-support.
		cb_operand1.setLayoutX(850+170); cb_operand1.setLayoutY(70); cb_operand1.setMinWidth(100); cb_operand1.setMinHeight(30);
		cb_operand2.setLayoutX(850+170); cb_operand2.setLayoutY(160); cb_operand2.setMinWidth(100); cb_operand2.setMinHeight(30);
		cb_result.setLayoutX(850+170); cb_result.setLayoutY(300); cb_result.setMinWidth(100); cb_result.setMinHeight(30);

		cb_result.setOnAction((event)->{changeResultantUnit();});
		cb_result.setDisable(true);
		insertUnitsToCombobox(cb_operand1); insertUnitsToCombobox(cb_operand2);
		cb_operand1.getSelectionModel().select("NO_UNIT"); cb_operand2.getSelectionModel().select("NO_UNIT");

		// Place all of the just-initialized GUI elements into the pane
		theRoot.getChildren().addAll(label_UNumberCalculator, label_Operand1, text_Operand1, label_errOperand1, 
				label_Operand2, text_Operand2, label_errOperand2, label_Result, text_Result, label_errResult, 
				button_Add, button_Sub, button_Mpy, button_Div, button_Sqrt,  label_Units,text_ErrorTerm1, 
				label_plusMinusOperand1, label_plusMinusOperand2,text_ErrorTerm2, text_ErrorTermResult, 
				label_plusMinusResult, label_errOperand2UpSpecific,label_errOperand1UpSpecific,
				label_errOperand1ETerm, label_errOperand2ETerm,err1,err2,err1et,err2et,cb_operand1,cb_operand2,
				cb_result,button_Def,button_saveResult,button_LaunchProgCalc);

	}





	/**********
	 * Private local method to initialize the standard fields for a label
	 */
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y); 
	}

	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y); 
		t.setEditable(e);
	}

	/**********
	 * Private local method to initialize the standard fields for a button
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y); 
	}


	/**********************************************************************************************

		User Interface Actions

	 **********************************************************************************************/

	/********************
	 * enterValues() routine sets the values of empty operands to zero and empty error term fields to 5 
	 ****/
	private void enterValues() {
		if (text_ErrorTerm1.getText().length()==0) text_ErrorTerm1.setText("5");
		if (text_ErrorTerm2.getText().length()==0) text_ErrorTerm2.setText("5");
	}

	/*****************************************************************************************
	 * Action Callers
	 * The Following Methods are executed upon clicking on the respective buttons.
	 ***************************************************************************************/
	/***
	 * This method is called upon pressing of addition button.
	 */
	private void initiateSum() {

		enterValues(); //Enter the values of empty operands to zero and empty error term fields to 5

		addIsSelected = true;
		subIsSelected=false;
		mpyIsSelected=false;
		divIsSelected=false;
		sqrtIsSelected=false;
		if (normalizationIsRequired(cb_operand1.getSelectionModel().getSelectedItem(), cb_operand2.getSelectionModel().getSelectedItem())) {

			if (!conversionIsPossible(cb_operand1.getSelectionModel().getSelectedItem(), cb_operand2.getSelectionModel().getSelectedItem()))  {
				showWarningAlert(1);
				return; 
			}
			else {
				performNormalization(cb_operand1.getSelectionModel().getSelectedItem(), cb_operand2.getSelectionModel().getSelectedItem());
			}
		} 

		if (text_Operand1.getText().equals("0")&&text_Operand2.getText().equals("0")) {
			text_Result.setText("0");
			addErrorTerms();
			fetchResultantUnit();
			return;
		}
		{
			addOperands();
			addErrorTerms();
			fetchResultantUnit();
		}
	}

	/***
	 * This method is called upon pressing of subtraction button.
	 */
	private void initiateDifference() {
		enterValues(); //Enter the values of empty operands to zero and empty error term fields to 5
		if (normalizationIsRequired(cb_operand1.getSelectionModel().getSelectedItem(), cb_operand2.getSelectionModel().getSelectedItem())) {

			if (!conversionIsPossible(cb_operand1.getSelectionModel().getSelectedItem(), cb_operand2.getSelectionModel().getSelectedItem()))  {
				showWarningAlert(0);
				return; 
			}
			else {

				performNormalization(cb_operand1.getSelectionModel().getSelectedItem(), cb_operand2.getSelectionModel().getSelectedItem());
			}
		} 
		addIsSelected = false;
		subIsSelected=true;
		mpyIsSelected=false;
		divIsSelected=false;
		sqrtIsSelected=false;
		{
			if (measuredValue.equals("0")&&text_Operand2.getText().equals("0")) {
				text_Result.setText("0");
			}
			else subOperands();
			subErrorTerms();
			fetchResultantUnit();
		}

	}

	/***
	 * This method is called upon pressing of multiplication button.
	 */
	private void initiateProduct() {
		enterValues(); //Enter the values of empty operands to zero and empty error term fields to 5
		addIsSelected = false;
		subIsSelected=false;
		mpyIsSelected=true;
		divIsSelected=false;
		sqrtIsSelected=false;
		if(theyHaveSameDimensions(cb_operand1.getSelectionModel().getSelectedItem(), cb_operand2.getSelectionModel().getSelectedItem())) {
			performNormalization(cb_operand1.getSelectionModel().getSelectedItem(), cb_operand2.getSelectionModel().getSelectedItem());
		} 
		{
			mpyOperands();
			mpyErrorTerms();
			fetchResultantUnit();
		}
	}

	/***
	 * This method is called upon pressing of division button.
	 */
	private void initiateQuotient() {
		enterValues();//Enter the values of empty operands to zero and empty error term fields to 5
		addIsSelected = false;
		subIsSelected=false;
		mpyIsSelected=false;
		divIsSelected=true;
		sqrtIsSelected=false;
		/*** If the second operand is zero, then tell the user that division by zero is not possible ***/
		if (Double.parseDouble(measuredValue2)==0 || Double.parseDouble(measuredValue2)==0) {
			text_Result.setText("Can't Divide by Zero"); text_ErrorTermResult.setText("Can't Divide By Zero");
			return;
		}
		if(theyHaveSameDimensions(cb_operand1.getSelectionModel().getSelectedItem(), cb_operand2.getSelectionModel().getSelectedItem())) {
			performNormalization(cb_operand1.getSelectionModel().getSelectedItem(), cb_operand2.getSelectionModel().getSelectedItem());
		} 
		// Else perform the division
		{
			divOperands();
			divErrorTerms();
			fetchResultantUnit();
		}
	}

	/***
	 * This method is called upon pressing of square root button.
	 */
	private void initiateSqrt() {
		enterValues();//Enter the values of empty operands to zero and empty error term fields to 5
		addIsSelected = false;
		subIsSelected=false;
		mpyIsSelected=false;
		divIsSelected=false;
		sqrtIsSelected=true;
		cb_operand2.getSelectionModel().clearSelection();
		{

			text_Operand2.clear(); 
			text_ErrorTerm2.clear();
		}
		if (Double.parseDouble(measuredValue)<0 || Double.parseDouble(errorTerm)<0) {
			text_Result.setText("Invalid Input");
			text_ErrorTermResult.setText("Invalid Input");
		}
		else 
		{
			if (measuredValue.equals("0")) {
				text_Result.setText("0");
				text_ErrorTermResult.setText("0");
			}
			else {
				sqrtOperand();
				sqrtErrorTerm();
			}
			fetchResultantUnit();
		}
	}


	/************
	 * Set Operands set the value of operands as combination of their measured values and error terms
	 * as they could be used as attributes to a single object in CalculatorValue Constructor.
	 */
	/**********
	 * Setting the Formal Values from Operand 1 and Error Term 1 Fields
	 * @throws IOException 
	 */
	private void setOperand1() throws IOException  {
		/*** Clear the current error messages ***/
		errOperand1_ETPart1.setText("");
		errOperand1_ETPart2.setText("");
		label_Result.setText("Result"); 
		label_errResult.setText("");
		label_errOperand1.setText("");
		label_errOperand1UpSpecific.setText("");
		operand1ErrPart1.setText("");
		operand1ErrPart2.setText("");
		label_errOperand1ETerm.setText("");
		fsmErrorMessageField1();
		measuredValue = text_Operand1.getText();
		errorTerm = text_ErrorTerm1.getText();
		unit = cb_operand1.getSelectionModel().getSelectedItem();
		if(filePath!=null)
		{
			System.out.println("filePathNotEqualsNull");	
			System.out.println(filePath);
			if(DefinitionsUserInterface.fetchDefinition(measuredValue) != "NOT_FOUND" && text_Operand1.getText().length() !=0) {

				Scanner def = new Scanner(DefinitionsUserInterface.fetchDefinition(measuredValue));
				measuredValue = def.nextLine();
				errorTerm = def.nextLine();
				unit = def.nextLine();
				text_ErrorTerm1.setText(errorTerm);
				cb_operand1.getSelectionModel().select(unit);
				def.close();
				errOperand1_ETPart1.setText("");
				errOperand1_ETPart2.setText("");
				label_Result.setText("Result"); 
				label_errResult.setText("");
				label_errOperand1.setText("");
				label_errOperand1UpSpecific.setText("");
				operand1ErrPart1.setText("");
				operand1ErrPart2.setText("");
				label_errOperand1ETerm.setText("");
			}
		}

		unitsAreChanged();

		/*** Check for error messages from Mechanical FSM Recognizer ***/
		if (text_Operand1.getText().length()!=0&&text_ErrorTerm1.getText().length()!=0)
		{ //Once both the attributes required for a CalculatorValue object are present

			if (perform.setOperand1(measuredValue,errorTerm,unit)) { 
				//Create the CalculatorValue object and clear 

				label_errOperand1.setText(""); //all the error messages
				label_errOperand1UpSpecific.setText("");
				operand1ErrPart1.setText("");
				operand1ErrPart2.setText("");

				if (text_Operand2.getText().length() == 0) 
					label_errOperand2.setText(""); 
			}

		}

	}

	/*** 
	 * Setting the Formal Values from Operand 2 and ErrorTerm 2 Fields
	 * @throws IOException 
	 */

	private void setOperand2() throws IOException  {
		/*** Clear the current error messages ***/
		errOperand2_ETPart1.setText("");
		errOperand2_ETPart2.setText("");
		label_Result.setText("Result"); 
		label_errResult.setText("");
		label_errOperand2.setText("");
		label_errOperand2UpSpecific.setText("");
		operand2ErrPart1.setText("");
		operand2ErrPart2.setText("");
		label_errOperand2ETerm.setText("");
		fsmErrorMessageField2();
		measuredValue2 = text_Operand2.getText();
		errorTerm2 = text_ErrorTerm2.getText();
		unit2 = cb_operand2.getSelectionModel().getSelectedItem();
		if(filePath!=null)
		{
			if(DefinitionsUserInterface.fetchDefinition(measuredValue2) != "NOT_FOUND" && text_Operand2.getText().length() !=0) {
				Scanner def = new Scanner(DefinitionsUserInterface.fetchDefinition(measuredValue2));
				measuredValue2 = def.nextLine();
				errorTerm2 = def.nextLine();
				text_ErrorTerm2.setText(errorTerm2);
				unit2 = def.nextLine();
				cb_operand2.getSelectionModel().select(unit2);
				def.close();
				errOperand2_ETPart1.setText("");
				errOperand2_ETPart2.setText("");
				label_Result.setText("Result"); 
				label_errResult.setText("");
				label_errOperand2.setText("");
				label_errOperand2UpSpecific.setText("");
				operand2ErrPart1.setText("");
				operand2ErrPart2.setText("");
				label_errOperand2ETerm.setText("");

			}
		}

		unitsAreChanged();

		/*** Check for error messages from Mechanical FSM Recognizer ***/
		if (text_Operand2.getText().length()!=0&&text_ErrorTerm2.getText().length()!=0)
		{ //Once both the attributes required for a CalculatorValue object are present

			if (perform.setOperand2(measuredValue2,errorTerm2,unit2)) { 
				//Create the CalculatorValue object and clear 
				label_errOperand2.setText(""); //all the error messages
				label_errOperand2UpSpecific.setText("");
				operand2ErrPart1.setText("");
				operand2ErrPart2.setText("");

				if (text_Operand1.getText().length() == 0) 
					label_errOperand1.setText(""); 
			}

		}

	}

	/*******
	 * This method call the FSM Recognizer program from the Calculator Value Class to 
	 * recognize errors in Operand1 Field and Error Term 1 Field.
	 */

	private void fsmErrorMessageField1() {
		String errMessage = CalculatorValue.checkMeasureValue(text_Operand1.getText());
		if (errMessage != "") {
			label_errOperand1.setText(CalculatorValue.measuredValueErrorMessage);;
			if (CalculatorValue.measuredValueIndexofError<=-1) return;
			String input = CalculatorValue.measuredValueInput;
			operand1ErrPart1.setText(input.substring(0,CalculatorValue.measuredValueIndexofError));
			operand1ErrPart2.setText("\u21EB");

		}

		String eErrorMessage = CalculatorValue.checkErrorTerm(text_ErrorTerm1.getText());
		if (eErrorMessage!="") {
			label_errOperand1ETerm.setText(CalculatorValue.errorTermErrorMessage);
			if (CalculatorValue.errorTermIndexofError<=-1) return;
			String input = CalculatorValue.errorTermInput;
			errOperand1_ETPart1.setText(input.substring(0,CalculatorValue.errorTermIndexofError));
			errOperand1_ETPart2.setText("\u21EB");
		}
	}
	/*******
	 * This method call the FSM Recognizer program from the Calculator Value Class to 
	 * recognize errors in Operand2 Field and Error Term 2 Field.
	 * 
	 */
	private void fsmErrorMessageField2() {
		String errMessage = CalculatorValue.checkMeasureValue(text_Operand2.getText());
		if (errMessage != "") {
			label_errOperand2.setText(CalculatorValue.measuredValueErrorMessage);;
			if (CalculatorValue.measuredValueIndexofError<=-1) return;
			String input = CalculatorValue.measuredValueInput;
			operand2ErrPart1.setText(input.substring(0,CalculatorValue.measuredValueIndexofError));
			operand2ErrPart2.setText("\u21EB");
		}
		String eErrMessage = CalculatorValue.checkErrorTerm(text_ErrorTerm2.getText());
		if (eErrMessage!="") {
			label_errOperand2ETerm.setText(CalculatorValue.errorTermErrorMessage);
			if (CalculatorValue.errorTermIndexofError<=-1) return;
			String input = CalculatorValue.errorTermInput;
			errOperand2_ETPart1.setText(input.substring(0,CalculatorValue.errorTermIndexofError));
			errOperand2_ETPart2.setText("\u21EB");
		}
	}



	/**********
	 * This method is called when an binary operation button has been pressed. It assesses if there are issues 
	 * with either of the binary operands or they are not defined. If not return false (there are no issues)
	 * 
	 * @return True if there are any issues that should keep the calculator from doing its work.
	 */
	private boolean binaryOperandIssues() {
		String errorMessage1 = perform.getOperand1ErrorMessage(); // Fetch the error messages, if there are any
		String errorMessage2 = perform.getOperand2ErrorMessage();
		if (errorMessage1.length() > 0) { // Check the first.  If the string is not empty
			label_errOperand1UpSpecific.setText(errorMessage1); // there's an error message, so display it.
			if (errorMessage2.length() > 0) { // Check the second and display it if there is
				label_errOperand2UpSpecific.setText(errorMessage2); // and error with the second as well.
				return true; // Return true when both operands have errors
			}
			else {
				return true; // Return true when only the first has an error
			}
		}
		else if (errorMessage2.length() > 0) { // No error with the first, so check the second
			label_errOperand2UpSpecific.setText(errorMessage2); // operand. If non-empty string, display the error
			return true; // message and return true... the second has an error
		} // Signal there are issues

		// If the code reaches here, neither the first nor the second has an error condition. The following code
		// check to see if the operands are defined.
		if (!perform.getOperand1Defined()) { // Check to see if the first operand is defined
			label_errOperand1UpSpecific.setText("No value found"); // If not, this is an issue for a binary operator
			if (!perform.getOperand2Defined()) { // Now check the second operand. It is is also
				label_errOperand2UpSpecific.setText("No value found"); // not defined, then two messages should be displayed
				return true; // Signal there are issues
			}
			return true;
		} else if (!perform.getOperand2Defined()) { // If the first is defined, check the second. Both
			label_errOperand2UpSpecific.setText("No value found"); // operands must be defined for a binary operator.
			return true; // Signal there are issues
		}

		return false; // Signal there are no issues with the operands
	}


	/*******************************************************************************************************
	 * This portion of the class defines the actions that take place when the various calculator
	 * buttons (add, subtract, multiply, divide and square root) are pressed.
	 */

	/**********
	 * This is the add routine for both the operands and their error terms.
	 */

	private void addOperands(){
		// Check to see if both operands are defined and valid
		if (binaryOperandIssues()) // If there are issues with the operands, return
			return; // without doing the computation

		// If the operands are defined and valid, request the business logic method to do the addition and return the
		// result as a String. If there is a problem with the actual computation, an empty string is returned
		String theAnswer = perform.addition(); // Call the business logic add method
		label_errResult.setText(""); // Reset any result error messages from before
		if (theAnswer.length() > 0) { // Check the returned String to see if it is okay
			text_Result.setText(theAnswer); // If okay, display it in the result field and

		}
		else { // Some error occurred while doing the addition.
			text_Result.setText(""); // Do not display a result if there is an error. 
			label_Result.setText("Result"); // Reset the result label if there is an error.
			label_errResult.setText(perform.getResultErrorMessage()); // Display the error message.
		}
	}
	private void addErrorTerms() {

		String theAnswer = perform.errorTermsAddition();
		if (theAnswer.length()>0) {
			text_ErrorTermResult.setText(theAnswer);//Display the result if it is valid
		}

		else {
			text_ErrorTermResult.setText("");
		}
		if(errorTerm.equals("")&& text_ErrorTerm2.getText().equals(""))
		{ text_ErrorTermResult.setText("0.5");
		}
	}

	/**********
	 * This is the subtract routine for both the operands and their error terms.
	 * 
	 */
	private void subOperands(){
		// Check to see if both the operands are valid or not
		if (binaryOperandIssues())
			return;
		thesub = perform.subtraction();
		label_errResult.setText("");// No possible arithmetic errors, hence label the error message on the result field to be empty.
		if (thesub.length()>0) {
			text_Result.setText(thesub); 
		}
		else {
			text_Result.setText("");
			label_Result.setText("Result");
			label_errResult.setText(perform.getResultErrorMessage());
		}

	}

	private void subErrorTerms() {


		String theAnswer = perform.errorTermsSubtraction();
		if (theAnswer.length()>0) {
			text_ErrorTermResult.setText(theAnswer); //Display the result if it is valid

		}
		else {
			text_ErrorTermResult.setText("");
		}
		if(errorTerm.equals("")&& text_ErrorTerm2.getText().equals(""))
		{ text_ErrorTermResult.setText("0.5");
		}
	}

	/**********
	 * This is the multiply routine for both the operands and their error terms.
	 * 
	 */
	private void mpyOperands(){
		//Check if both the operands are valid
		if (binaryOperandIssues())
			return;
		theprod = perform.multiplication(); // Perform the operation
		label_errResult.setText(""); // No possible arithmetic errors, hence label the error message on result field to be empty.
		if (theprod.length()>0) {
			text_Result.setText(theprod);  //Display the result if it is valid
		}
		else {
			label_Result.setText(""); 
			text_Result.setText("Result");
			label_errResult.setText(perform.getResultErrorMessage());
		}
	}
	private void mpyErrorTerms() {

		String theAnswer = perform.errorTermsMultiplication(); //Perform the Operation
		if (theAnswer.length()>0) {
			text_ErrorTermResult.setText(theAnswer); //Display the result if it is valid
		}
		else {
			text_ErrorTermResult.setText("");
		}
	}



	/**********
	 * This is the divide routine for both the operands and the error terms.
	 * If the divisor is zero, the divisor is declared to be invalid.
	 * 
	 */

	private void divOperands(){
		//Check if both the operands are valid

		thediv = perform.division();                                                  //Perform the operation.
		label_errResult.setText("");
		if (thediv.length()>0) {

			text_Result.setText(thediv);                                           // Display the result if it is valid.
		}

		else {
			label_Result.setText("Quotient"); //It is general assumption that division by zero will give a result of no length.
			text_Result.setText(""); 

		}
	}
	private void divErrorTerms() {

		String theAnswer = perform.errorTermsDivision();
		if (theAnswer.length()>0) {
			text_ErrorTermResult.setText(theAnswer); //Display the result if it is valid
		}
		else {
			text_ErrorTermResult.setText("");
		}
	}

	/***
	 * This is the routine of Square Root feature for both the operand and the error terms.
	 * If the operand or error term is negative then it will be declared as invalid.
	 */

	private void sqrtOperand() {

		thesqrt = perform.squareroot(); //Perform the operation.
		text_Result.setText(thesqrt); //When the result is valid, display it.

	}
	private void sqrtErrorTerm() {

		String theAnswer=perform.errorTermsSquareroot();  //Perform the Operation.
		text_ErrorTermResult.setText(theAnswer);//Display the result if it is valid

	}

	/********************UNIT RELATED METHOD****************/
	/************************************************************************************
	 * This portion of the class defines the actions which take place in order to mantain 
	 * the units of the calculations.
	 */

	/************************
	 * This routine fetches various units from locally defined arrays and provide it to
	 * the comboboxes which the user will use for his calculations.
	 * @param c Combobox of Type String
	 */
	private void insertUnitsToCombobox(ComboBox<String> c) {
		// Units of the Physical Dimension: Length  (Required for Calculating Hohmann Transfer )
		String[] lengths = {
				"km","miles","m"
		};
		// Units of the Physical Dimension: Time  (Required for Calculating Hohmann Transfer )
		String[] time = {
				"days","seconds"
		};

		// Units of the Physical Quantity: Velocity  (Required for Calculating Hohmann Transfer )
		String[] velocity = {"km/seconds","m/seconds"};
		// The following unit is necessary for critical space mission use
		String gravityConstant = "km3/seconds2"; 
		// The pseudo-unit that user can use to represent a scalar value without any unit
		String noUnit = "NO_UNIT";
		// Initializing the while loop counter
		int ndx=0; 
		/******************************** Adding to The Comboboxes ************************************/
		while (ndx<lengths.length) {
			c.getItems().add(lengths[ndx++]);

		}
		// Reset the counter
		ndx=0;
		while (ndx<time.length) {
			c.getItems().add(time[ndx++]);
		}
		// Reset the counter
		ndx=0;
		while (ndx<velocity.length) {
			c.getItems().add(velocity[ndx++]);
		}

		c.getItems().add(gravityConstant);
		c.getItems().add(noUnit);
		// All the lists need to be updated only once so
		if (firstTime) { // for the very first time
			ndx=0; // when the program launches
			//Add all the units to specific lists
			while (ndx<lengths.length) {
				list_LengthUnits.add(lengths[ndx++]);
			}
			ndx=0;
			while (ndx<time.length) {
				list_TimeUnits.add(time[ndx++]);
			}
			ndx=0;
			while (ndx<velocity.length) {
				list_VelocityUnits.add(velocity[ndx++]);
			}

		}
		// firstTime is set to zero so that the list does not gets updated again
		firstTime=false;

	}
	/*********
	 * The following routine takes the input units and the operation into consideration to
	 * calculate the resultant unit.
	 */
	private void fetchResultantUnit() {
		String firstUnit = cb_operand1.getSelectionModel().getSelectedItem();
		String secondUnit = cb_operand2.getSelectionModel().getSelectedItem();
		String resultantUnit =null;
		// In case of addition, the units must be same (after normalization), hence the resultant will have same units
		if (addIsSelected || subIsSelected) {
			resultantUnit = secondUnit;
		}
		// If the operation selected is multiplication
		else if (mpyIsSelected) {
			//If both the units are same 
			if (firstUnit.equals(secondUnit) ) {
				// If both the operands have no units then the resultant will also not have any unit
				if (firstUnit.equals("NO_UNIT")) resultantUnit = "NO_UNIT";
				// If both the operands are same then the unit will get squared
				else resultantUnit = secondUnit+"\u00B2"; // \u00B2 means superscripted 2
			}
			// If they have same dimensions, then the post-normalization, firstUnit will equal to secondUnit 
			// Resultant unit will get squared
			else if (theyHaveSameDimensions(firstUnit,secondUnit)) resultantUnit = secondUnit+"\u00B2";
			// If only one operand has unit then resultant unit will be same as that unit 
			else if (firstUnit.equals("NO_UNIT") || secondUnit.equals("NO_UNIT")) {
				if (firstUnit.equals("NO_UNIT")) resultantUnit = secondUnit;
				else resultantUnit = firstUnit;
			}
			// In case both the units are different, place a 'x' symbol between both of them
			else resultantUnit = firstUnit+"\u00D7"+secondUnit;
		}
		// If the operation selected is division
		else if (divIsSelected) {
			// If both the units are same, then the resultant will have no unit
			if (firstUnit.equals(secondUnit) ) {
				resultantUnit = "NO_UNIT";
			}
			// If only one operand has unit then the resultant will vary accordingly.
			else if (firstUnit.equals("NO_UNIT") || secondUnit.equals("NO_UNIT")) {
				// If numerator has no unit then the resultant will have unit equal to inverse of unit of second operand
				if (firstUnit.equals("NO_UNIT")) resultantUnit = secondUnit+"\u207B\u2081";  // \u207B\u2081 means superscripted -1 
				else resultantUnit = firstUnit; // else the resultant unit will be the same as unit of numerator
			}
			// If both the values have same dimensions, then post normalization the units will be same, hence  the quotient will be 
			// having no unit.
			else if (theyHaveSameDimensions(firstUnit,secondUnit)) resultantUnit="NO_UNIT";
			// If both the numerator and denominator have unique units, then place a '÷' symbol between them 
			else {
				resultantUnit = firstUnit+"\u00F7"+secondUnit;
				//resultantUnit = firstUnit+"/"+secondUnit; // Uncomment this line to place a '/' symbol instead of '÷' symbol.
			}
		}
		// If the selected operation is square-root
		else if (sqrtIsSelected) {
			// Place a '√' symbol before the unit of the operand
			resultantUnit = "\u221A"+firstUnit;
		}
		// Clear the combobox
		cb_result.getItems().clear();
		// If the resultant unit belongs to Physical Dimension: Length, then it can be converted into all the units of the same dimension
		// hence add all the units of length dimension to the list.
		if (list_LengthUnits.indexOf(resultantUnit)!=-1) {
			cb_result.getItems().addAll(list_LengthUnits);
			cb_result.setDisable(false);
		}
		// If the resultant unit belongs to Physical Dimension: Time, then it can be converted into all the units of the same dimension
		// hence add all the units of length dimension to the list.
		else  if (list_TimeUnits.indexOf(resultantUnit)!=-1) {
			cb_result.getItems().addAll(list_TimeUnits);

			cb_result.setDisable(false);
		}
		// If the resultant unit belongs to Physical Quality: Velocity, then it can be converted into all the units of the same dimension
		// hence add all the units of length dimension to the list.
		else  if (list_VelocityUnits.indexOf(resultantUnit)!=-1) {
			cb_result.getItems().addAll(list_VelocityUnits);
			cb_result.setDisable(false);

		}
		// If the resultant unit is unique, then it can't be converted.
		else {
			cb_result.getItems().add(resultantUnit); // Hence add the unit to the combobox
			cb_result.setDisable(true); // but disable it so that user can't convert it.
			cb_result.setOpacity(0.8); // Set opacity to 0.8 so that the unit will still be visible easily
		}
		// Select the resultant unit from the combobox
		cb_result.getSelectionModel().select(resultantUnit);
		// Store this unit so that further conversion could be done
		previouslySelectedResultantUnit = resultantUnit;

	}
	/***
	 * The following routine clears the result fields whenever the user changes the operand's units
	 * so that the calculation could be performed again
	 */
	private void unitsAreChanged() {
		text_Result.clear(); text_ErrorTermResult.clear(); cb_result.getSelectionModel().clearSelection();
	}
	/***
	 * The following routine displays a warning message when two operands having incompatible units (different physical dimensions)
	 * are supposed to be added or subtracted.
	 * @param i Programmer defined integer parameter: Only passing 1 will display alert regarding addition, otherwise alert regarding 
	 * subtraction will be displayed. 
	 */
	private void showWarningAlert(int i) {
		Alert alert = new Alert(AlertType.WARNING); // Initialize a warning-type alert box
		// According to the passed parameter, set the content of the warning
		if (i==1) alert.setContentText("Adding a "+cb_operand1.getSelectionModel().getSelectedItem()+" value and "+cb_operand2.getSelectionModel().getSelectedItem()+" value is not possible!");
		else if (i==0) {
			alert.setContentText("Subtracting a "+cb_operand1.getSelectionModel().getSelectedItem()+" value and "+cb_operand2.getSelectionModel().getSelectedItem()+" value is not possible!"); 
		}
		else if (i==3) {
			alert.setContentText("Repository File Not Loaded");
		} 
		else if (i==4) {
			alert.setContentText("Repository File Not Loaded Previously.");
		}
		// The width of the alert is 1000
		alert.setWidth(1000);
		// Show the alert (warning) box
		alert.show();
	}

	// These integer values will be used for conversion of units.
	int indexOfOldValue = -2; int indexOfNewValue = -2;
	/*******
	 * The following routine takes two units as parameters and tells if they are inter-convertible or not.
	 * @param unit1 Unit-One
	 * @param unit2 Unit-Two
	 * @return true if passed units are inter-convertible and false if they aren't.
	 */
	private boolean conversionIsPossible(String unit1, String unit2) {
		// Check if the first unit is present in the LookupTable or not
		for (int i = 0; i<CalculatorValue.lookupTableForConversion.length;i++) {
			if (CalculatorValue.lookupTableForConversion[i][0]==unit1) indexOfOldValue=i;
		}
		// Check if the second unit is present in the LookupTable or not
		for (int i = 0; i<CalculatorValue.lookupTableForConversion.length;i++) {
			if (CalculatorValue.lookupTableForConversion[0][i]==unit2) indexOfNewValue=i;
		}
		// If any of the units are not in lookup table then the conversion is not possible
		if (indexOfOldValue<0 || indexOfNewValue<0) return false;
		// If both the units are present in the lookup table
		if (indexOfOldValue>=0&&indexOfNewValue>=0) {
			// Use the units-interconversion possibility mapping array for knowing if the proposed
			// conversion is dimensionally accurate or not.
			boolean isConversionDimensionallyAccurate = CalculatorValue.conversionPossibilityMapping[indexOfOldValue][indexOfNewValue];
			return isConversionDimensionallyAccurate;
		}
		// If the control reaches here, then the units aren't interconvertible at all.
		return false;
	}
	/******
	 * The following method takes two units as input and then check if they are interconvertible or not.
	 * If the units are interconvertible then the first unit is converted into second unit by multiplying
	 * the operand and error term with the factor given by the appropriate index recieved from the lookup
	 * table. This method returns a two-elements containing 1-D array which stores the pair of the converted operand's
	 * measured value and error term. 
	 * Since the input is taken in form of a double value and output is calculated using UNumber Library, this
	 * method works on Double values so that they can be further passed to the controller class for calculations using UNumber Library.
	 * @param firstUnit The first unit (which will be converted in case both the units are interconvertible)
	 * @param secondUnit The second unit
	 * @return Array {Converted Measured Value, Converted Error Term} 
	 */
	private double[] performNormalization(String firstUnit, String secondUnit) {
		// Fill fields with default values if they are empty
		enterValues();
		double theConversionFactor = 0; // The conversion factor
		double firstOperand = Double.parseDouble(measuredValue); // The first operand's measured value
		double firstET = Double.parseDouble(errorTerm); // The first operand's error term
		// Check if the proposed conversion is physically possible
		if (conversionIsPossible(firstUnit, secondUnit)) { // If possible, then fetch the conversion factor from
			theConversionFactor = Double.parseDouble(CalculatorValue.lookupTableForConversion[indexOfOldValue][indexOfNewValue]); // the lookup table
		} else theConversionFactor=1; // Else, set the factor as 1 (no-conversion)
		// New Operand will be given by multiplying the measured value by the conversion factor
		double newOperand = firstOperand * theConversionFactor;
		// New Error Term will be given by multiplying the error term by the conversion factor
		double newErrorTerm = firstET * theConversionFactor;
		// Place both the values to an array so that they can be worked upon for the further calculations
		double[] thePair = {newOperand,newErrorTerm};
		return thePair;

	}

	/****
	 * The following routine is called when the user desires to change the unit in the Result's respective
	 * checkbox. Changing units will lead to the conversion of the resultant values according to the unit conversion proposed.
	 */
	private void changeResultantUnit() {
		// Get the unit selected by the user lately
		String newUnit = cb_result.getSelectionModel().getSelectedItem();
		// if the resultant's operand and error term fields are not empty then convert them.
		if (!(text_Result.getText().isEmpty())&&!(text_ErrorTermResult.getText().isEmpty()))
			CalculatorValue.convertResultantUnit(previouslySelectedResultantUnit,newUnit);
		previouslySelectedResultantUnit = newUnit; // Save this unit as the last unit so that further conversions can be done whenever needed
	}

	/*****
	 * The following method tells if the passed units belong to same dimension or not.
	 * @param unit1 First Unit
	 * @param unit2 Second Unit
	 * @return true if the units belong to same dimension, else return false.
	 */
	private boolean theyHaveSameDimensions(String unit1,String unit2) {
		// If both the units are same, then they definitely belong to same dimension
		if (unit1.equals(unit2)) return true;
		int ndx_row = -2; int ndx_col = -2;
		for (int i =0; i<CalculatorValue.lookupTableForConversion.length;i++) {
			if (CalculatorValue.lookupTableForConversion[i][0].equals(unit1)) ndx_row = i;
		}
		for (int i =0; i<CalculatorValue.lookupTableForConversion.length;i++) {
			if (CalculatorValue.lookupTableForConversion[0][i].equals(unit2)) ndx_col = i;
		}
		if (ndx_row>=0&&ndx_col>=0&&CalculatorValue.conversionPossibilityMapping[ndx_row][ndx_col]) {
			return true;
		}
		return false;
	}
	/******
	 * The routine tells if normalization is needed for addition / subtraction or not.
	 * @param unit1 Unit of first operand
	 * @param unit2 Unit of second operand
	 * @return true if normalization is required else return false
	 */
	private boolean normalizationIsRequired(String unit1, String unit2) {
		if (!unit1.equals(unit2)) return true;
		return false;
	} 

	/****
	 * This method setup the DefinitionUI
	 */

	private void callDefinitionsUI() {
		FileChooser repositoryBrowser = new FileChooser();
		repositoryBrowser.setTitle("Browse The Repository");
		repositoryBrowser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("txt", "*.txt")
				);
		Label label_Load = new Label("Load The Repository");
		setupLabelUI(label_Load, "Calibri", 30, 200, Pos.BASELINE_CENTER, 10, 10);
		Label label_File = new Label("File");
		setupLabelUI(label_File, "Calibri", 18, 100, Pos.BASELINE_CENTER, 0, 50);
		TextField text_FilePath = new TextField();
		setupTextUI(text_FilePath,"Calibri",18,500,Pos.BASELINE_CENTER,40+25,50,true);
		Button button_Browse = new Button("Browse");
		setupButtonUI(button_Browse, "Calibri", 18, 100, Pos.BASELINE_CENTER, 222+85,100); 
		Button button_Launch = new Button("Launch");
		setupButtonUI(button_Launch, "Calibri", 18, 100, Pos.BASELINE_CENTER, 340+115,100);
		Button button_LoadLastFile = new Button("Load Last File");
		setupButtonUI(button_LoadLastFile, "Calibri", 18, 100, Pos.BASELINE_CENTER, 80+25,100);
		Pane browserPane = new Pane();
		browserPane.getChildren().addAll(label_Load,label_File,text_FilePath,button_Browse,button_Launch,button_LoadLastFile);
		Scene browserScene = new Scene(browserPane,600,200);
		Stage browserStage = new Stage();
		button_Launch.setDisable(true);
		button_Browse.setOnAction((event)->{
			currentRepositoryFile = repositoryBrowser.showOpenDialog(browserStage); 
			if (currentRepositoryFile!=null) 
			{
				filePath = currentRepositoryFile.getAbsolutePath();
				text_FilePath.setText(filePath);
				button_Launch.setDisable(false);
			} else button_Launch.setDisable(true);
		});
		button_Launch.setOnAction((event)->{
			try {

				defGUI= new DefinitionsUserInterface(null);
				browserStage.hide();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		/**
		 *Load Last file
		 */
		button_LoadLastFile.setOnAction((event) ->{
			try {
				if (filePath==null) {showWarningAlert(4);return;}
				defGUI = new DefinitionsUserInterface(null);
				browserStage.hide();
			} catch (IOException e) {

				e.printStackTrace();
			}
		});
		browserStage.setScene(browserScene);
		browserStage.setTitle("Load the Repository");
		browserStage.show();
	}

	/****
	 * This method is called whenever the user wants to save the result(s) in the loaded text file
	 */

	private void launchSaveResultPopup() {
		Stage saveResultStage = new Stage();
		Pane saveResultPane = new Pane();
		Scene saveResultScene = new Scene(saveResultPane,800,400);
		Label lbl_saveResName = new Label("Name:");
		Button btn_saveAsVar = new Button("Save as Variable");
		Button btn_saveAsConst = new Button ("Save as Constant");
		setupLabelUI(lbl_saveResName, "Calibri", 18, 80, Pos.BASELINE_LEFT, 40, 50);
		Label lbl_saveResDetails = new Label("Details: ");
		setupLabelUI(lbl_saveResDetails, "Calibri", 18, 80, Pos.BASELINE_LEFT, 40, 100);
		TextField fld_resName = new TextField();
		setupTextUI(fld_resName, "Calibri", 16, 500, Pos.BASELINE_CENTER, 120, 50, true);
		TextArea txt_resDetails = new TextArea();
		txt_resDetails.setText("Measured Value: "+text_Result.getText()+"\nError Term: "
				+text_ErrorTermResult.getText()+"\nUnit: "+cb_result.getSelectionModel().getSelectedItem());
		txt_resDetails.setLayoutX(120); txt_resDetails.setEditable(false);
		txt_resDetails.setLayoutY(100); txt_resDetails.setFont(Font.font("Calibri", 16));
		setupButtonUI(btn_saveAsConst, "Calibri", 18, 80, Pos.BASELINE_LEFT, 200, 350);
		btn_saveAsConst.setOnAction((event)->{try {
			saveTheResult(
					fld_resName.getText(),text_Result.getText(),text_ErrorTermResult.getText(),cb_result.getSelectionModel().getSelectedItem(),"True"
					);
		} catch (IOException e) {

			e.printStackTrace();
		}
		saveResultStage.close();
		});
		btn_saveAsVar.setOnAction((event)->{try {
			saveTheResult(
					fld_resName.getText(),text_Result.getText(),text_ErrorTermResult.getText(),cb_result.getSelectionModel().getSelectedItem(),"False"
					);
		} catch (IOException e) {

			e.printStackTrace();
		}
		saveResultStage.close();
		});
		setupButtonUI(btn_saveAsVar, "Calibri", 18, 80, Pos.BASELINE_LEFT, 350, 350);
		saveResultPane.getChildren().addAll(fld_resName,txt_resDetails,lbl_saveResName,lbl_saveResDetails,btn_saveAsConst,btn_saveAsVar);
		saveResultStage.setTitle("Save The Result As Constant or Variable");
		saveResultStage.setScene(saveResultScene);
		saveResultStage.show();
	}
	private void saveTheResult(String name, String mv, String et, String un, String c) throws IOException {
		if (currentRepositoryFile==null) { showWarningAlert(3); return;}
		defGUI.addToTableView(name, mv, et, un, c);
	}

	private void launchProgrammableCalculator() {
		new ProgramsUserInterface();
	}
}
