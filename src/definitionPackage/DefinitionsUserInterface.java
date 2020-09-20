package definitionPackage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Scanner;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import mainCalculatorPackage.CalculatorValue;
import mainCalculatorPackage.UserInterface;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DefinitionsUserInterface  {
	/**
	 * <p>
	 * Title: DefinitionDemo - A component of the Programmable Calculator Project
	 * </p>
	 *
	 * <p>
	 * Description: A controller object class that implements an editable TableView UI
	 * </p>
	 *
	 * <p>
	 * Copyright: Copyright © 2019
	 * </p>
	 *
	 * @author Lynn Robert Carter, Jaskirat, Shivam, Sumit
	 * @version 1.00	Baseline version 2019-01-26
	 * @version 2.00 	Integrated the modified version with the programmable calculator 2019-02-24
	 *
	 */
	static ManageDefinitions perform = new ManageDefinitions();
	/**********************************************************************************************
	 * 
	 * The DefinitionDemo Class provides an experimental platform upon which the user interface
	 * for the a pop-up window implementation of the Variable / Constant Definition Table can be
	 * developed and experiments can be run without dealing with the rest of the Calculator.
	 * 
	 * The following are the primary followed by the secondary attributes for the DefinitionDemo
	 * Class
	 */

	private Label lbl_EditingGuidance = 					// A Label used to guide the user
			new Label("Editing a Table Cell!  When finished, press <enter> or <return> to commit the change.");

	private Label lbl_Editing = new Label("Jaskirat, Shivam and Sumit");

	private static boolean whenSorting = false;			// A flag to signal when to ignore case

	private final static ObservableList<Quantity> tableData =	// The list of values being defined
			FXCollections.observableArrayList(
					);

	public ComboBoxTableCell<Quantity, String> cb_unit = new ComboBoxTableCell<Quantity, String>();
	public ComboBoxTableCell<Quantity, String> cb_type = new ComboBoxTableCell<Quantity, String>();
	ObservableList<String> variable_units = FXCollections.observableArrayList(CalculatorValue.lookupTableForConversion[0]);
	{
		variable_units.remove(" ");
		variable_units.add("NO_UNIT");
	}
	/**********
	 * This inner class is used to define the various fields required by the variable/constant
	 * definitions.
	 * 
	 * @author lrcarter
	 *
	 */
	public static class Quantity {	
		private final SimpleStringProperty nameValue;			// The name of the value
		private final SimpleStringProperty isConstantValue;		// Specifies if this is a constant
		private final SimpleStringProperty measureValue;		// The measured value
		private final SimpleStringProperty errorTermValue;		// Error term, if there is one
		private final SimpleStringProperty unitsValue;			// Units, if there is one

		/*****
		 * This fully-specified constructor establishes all of the fields of a Quantity object
		 * 
		 * @param n - A String that specifies the name of the constant or variable
		 * @param c - A String that serves as a T or F flag as to where or not this is a constant
		 * @param m - A String that specifies the measured value / value, if there is one
		 * @param e - A String that specifies the error term, if there is one
		 * @param u - A String that specifies the units definition, if there is one
		 */
		public Quantity(String n, String c, String m, String e, String u) {
			this.nameValue = new SimpleStringProperty(n);
			this.isConstantValue = new SimpleStringProperty(c);
			this.measureValue = new SimpleStringProperty(m);
			this.errorTermValue = new SimpleStringProperty(e);
			this.unitsValue = new SimpleStringProperty(u);
		}

		/*****
		 * This getter gets the value of the variable / constant name field - If the whenSorting
		 * flag is true, this method return the String converted to lower case - otherwise, it 
		 * return the String as is
		 * 
		 * NOTE: Be very careful with the name, especially the capitalization as this code 
		 * generates method calls to these routines given the name of the field, it follows
		 * this naming pattern.
		 * 
		 * @return	String - of the name of the variable / constant
		 */
		public String getNameValue() {
			if (whenSorting)
				return nameValue.get().toLowerCase();
			else
				return nameValue.get();
		}

		/*****
		 * This Setter sets the value of the variable / constant name field
		 * 
		 * NOTE: Be very careful with the name, especially the capitalization as this code 
		 * generates method calls to these routines given the name of the field, it follows
		 * this naming pattern.
		 */
		public void setNameValue(String n) {
			nameValue.set(n);
		}

		/*****
		 * This getter gets the value of the isConstant flag field - If this field is true, the
		 * item being defined is a constant and the calculator will not be allowed to alter the
		 * value (but the calculator's user may editing the value of this item).
		 * 
		 * NOTE: Be very careful with the name, especially the capitalization as this code 
		 * generates method calls to these routines given the name of the field, it follows
		 * this naming pattern.
		 * 
		 * @return	String - Either a "T" or an "F" String
		 */
		public String getIsConstantValue() {
			return isConstantValue.get();
		}

		/*****
		 * This Setter sets the value of the isConstant flag field - If the parameter c starts
		 * with a "T" or a "t", the field is set to "T", else it is set to "F". 
		 * 
		 * NOTE: Be very careful with the name, especially the capitalization as this code 
		 * generates method calls to these routines given the name of the field, it follows
		 * this naming pattern.
		 * 
		 * @param c	String - The first letter is used to determine if this is a "T" or "F"
		 */
		public void setIsConstantValue(String c) {
			if (c.startsWith("T") || c.startsWith("t"))
				isConstantValue.set("T");
			else
				isConstantValue.set("F");
		}

		/*****
		 * This getter gets the value of the measureValue field.
		 * 
		 * NOTE: Be very careful with the name, especially the capitalization as this code 
		 * generates method calls to these routines given the name of the field, it follows
		 * this naming pattern.
		 * 
		 * @return	String - A String of the measuredValue specification is returned
		 */
		public String getMeasureValue() {
			return measureValue.get();
		}

		/*****
		 * This Setter sets the value of the measuredValue field 
		 * 
		 * NOTE: Be very careful with the name, especially the capitalization as this code 
		 * generates method calls to these routines given the name of the field, it follows
		 * this naming pattern.
		 * 
		 * @param m	String - The value is assumed to be a value numeric string. It must be
		 * checked before this routine is used.
		 */
		public void setMeasureValue(String m) {
			measureValue.set(m);
		}

		/*****
		 * This getter gets the value of the errorTerm field.
		 * 
		 * NOTE: Be very careful with the name, especially the capitalization as this code 
		 * generates method calls to these routines given the name of the field, it follows
		 * this naming pattern.
		 * 
		 * @return	String - A String of the errorTerm specification is returned
		 */
		public String getErrorTermValue() {
			return errorTermValue.get();
		}

		/*****
		 * This Setter sets the value of the errorTerm field 
		 * 
		 * NOTE: Be very careful with the name, especially the capitalization as this code 
		 * generates method calls to these routines given the name of the field, it follows
		 * this naming pattern.
		 * 
		 * @param e	String - The value is assumed to be a value numeric string. It must be
		 * checked before this routine is used.
		 */
		public void setErrorTermValue(String e) {
			errorTermValue.set(e);
		}

		/*****
		 * This getter gets the value of the units field.
		 * 
		 * NOTE: Be very careful with the name, especially the capitalization as this code 
		 * generates method calls to these routines given the name of the field, it follows
		 * this naming pattern.
		 * 
		 * @return	String - A String of the units specification is returned
		 */
		public String getUnitsValue() {
			return unitsValue.get();
		}

		/*****
		 * This Setter sets the value of the unitsValue field 
		 * 
		 * NOTE: Be very careful with the name, especially the capitalization as this code 
		 * generates method calls to these routines given the name of the field, it follows
		 * this naming pattern.
		 * 
		 * @param u	String - The value is assumed to be a value units string. It must be
		 * checked before this routine is used.
		 */
		public void setUnitsValue(String u) {
			unitsValue.set(u);
		}
	}

	/*****
	 * This method establishes, defines, and sets up the GUI widgets for the Definition Window,
	 * links these widgets to the appropriate action methods in support of the GUI, and ties this
	 * stage to the primary stage that roots the user interface.
	 * 
	 * @param primaryStage - This parameter is the link to the root stage for this application
	 * @throws IOException 
	 */
	public  DefinitionsUserInterface(Stage primaryStage) throws IOException {
		// Create new stage for this pop-up window
		Stage dialog = new Stage();

		// Make this window an Application Modal, which blocks all GUI requests to other windows
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setTitle("Variable / Constant Definition Table");
		dialog.initOwner(primaryStage);

		// Establish a new window pane and a TableView widget for that pane
		Pane thePane = new Pane();
		TableView<Quantity> table = new TableView<>();
		Scene dialogScene = new Scene(thePane, 700, 550);	// Define the window width and height

		// Establish a button to close this pop-up window
		Button btn_Close = new Button("Close");
		setupButton(btn_Close, 100, 20, 15);
		btn_Close.setOnAction((event) -> { dialog.close(); });

		// Establish a button to add a new row into the TableView into the set of definitions
		Button btn_Add = new Button("Add a new Item");
		setupButton(btn_Add, 150, 140, 15);		
		btn_Add.setOnAction((event) -> { 

			// Create a new row after last row in the table
			Quantity q = new Quantity("?", "?", "?", "?", "?");
			tableData.add(q);

			int row = tableData.size() - 1;

			// Select the row that was just created
			table.requestFocus();
			table.getSelectionModel().select(row);
			table.getFocusModel().focus(row);
		});

		// Establish a button to delete a row in the TableView into the set of definitions
		//		btn_Delete.setOnAction((event) -> {
		//			table.getItems().removeAll(table.getSelectionModel().getSelectedItem());
		//		});

		// If there is no data in the table, then disable the Delete Button else enable it

		Button btn_SaveAs = new Button("SaveAs");
		setupButton(btn_SaveAs, 100, 140, 500);		
		btn_SaveAs.setOnAction((event)->{try {
			launchSaveWindow();
		} catch (IOException e) {

			e.printStackTrace();
		}});

		Button btn_Save = new Button("Save");
		setupButton(btn_Save, 100, 310, 500);		
		btn_Save.setOnAction((event)->{try {
			saveDataToRepository(UserInterface.currentRepositoryFile);
		} catch (IOException e) {

			e.printStackTrace();
		}
		});

		// Make the table editable and position it in the pop-up window
		table.setEditable(true);
		table.setLayoutX(20);
		table.setLayoutY(60);

		//**********//
		// Define each of the columns in the table view and set up the handlers to support editing

		// This is the column that support the Name column. When the name of a definition is changed
		// this code will cause the table of data to be re-sorted and rearranged so the rows will 
		// shown in the table as sorted.
		TableColumn<Quantity, String> col_NameValue = new TableColumn<Quantity, String>("Variable/Constant\nName");
		col_NameValue.setMinWidth(130);
		col_NameValue.setCellValueFactory(new PropertyValueFactory<>("nameValue"));
		col_NameValue.setCellFactory(TextFieldTableCell.forTableColumn());

		// When one starts editing a Name column, a message is displayed giving guidance on how to
		// commit the change when done.
		col_NameValue.setOnEditStart((CellEditEvent<Quantity, String> t) -> {
			lbl_EditingGuidance.setVisible(true);
		});

		// When the user commits the change, the editing guidance message is once again hidden and
		// the system sorts the data in the table so the data will always appear sorted in the table
		col_NameValue.setOnEditCommit(
				(CellEditEvent<Quantity, String> t) -> {
					t.getTableView().getItems().get(
							t.getTablePosition().getRow()).setNameValue(t.getNewValue());
					whenSorting = true;
					tableData.sort(Comparator.comparing(Quantity::getNameValue));
					whenSorting = false;
					lbl_EditingGuidance.setVisible(false);
				});



		//**********//
		// This is the column that supports the IsConstantValue field.  
		TableColumn<Quantity, String> col_IsConstantValue = new TableColumn<Quantity, String>("Is a\nConstant");

		col_IsConstantValue.setMinWidth(75);
		String[] type = {"true","false"};
		ObservableList<String> constant_type = FXCollections.observableArrayList(type);

		col_IsConstantValue.setCellValueFactory(new PropertyValueFactory<>("isConstantValue"));
		col_IsConstantValue.setCellFactory(ComboBoxTableCell.forTableColumn(constant_type));

		// When one starts editing the IsConstantValue column, a message is displayed giving 
		// guidance on how to commit the change when done.
		col_IsConstantValue.setOnEditStart((CellEditEvent<Quantity, String> t) -> {
			lbl_EditingGuidance.setVisible(true);
		});	

		// When the user commits the change, the editing guidance message is once again hidden and
		// the system sorts the data in the table so the data will always appear sorted in the table
		col_IsConstantValue.setOnEditCommit(
				(CellEditEvent<Quantity, String> t) -> {
					t.getTableView().getItems().get(
							t.getTablePosition().getRow()).setIsConstantValue(t.getNewValue());
					lbl_EditingGuidance.setVisible(false);

				});

		//**********//
		// This is the column that supports the MeasureValue field.  
		TableColumn<Quantity, String> col_MeasureValue = new TableColumn<Quantity, String>("Measure or Value");
		col_MeasureValue.setMinWidth(175);
		col_MeasureValue.setCellValueFactory(new PropertyValueFactory<>("measureValue"));
		col_MeasureValue.setCellFactory(TextFieldTableCell.forTableColumn());

		// When one starts editing the MeasureValue column, a message is displayed giving 
		// guidance on how to commit the change when done.
		col_MeasureValue.setOnEditStart((CellEditEvent<Quantity, String> t) -> {
			lbl_EditingGuidance.setVisible(true);
		});	

		// When the user commits the change, the editing guidance message is once again hidden and
		// the system sorts the data in the table so the data will always appear sorted in the table
		col_MeasureValue.setOnEditCommit(
				(CellEditEvent<Quantity, String> t) -> {
					t.getTableView().getItems().get(
							t.getTablePosition().getRow()).setMeasureValue(t.getNewValue());
					lbl_EditingGuidance.setVisible(false);
				});

		//**********//
		// This is the column that supports the ErrorTermValue field.  
		TableColumn<Quantity, String> col_ErrorValue = new TableColumn<Quantity, String>("Error Term");
		col_ErrorValue.setMinWidth(100);
		col_ErrorValue.setCellValueFactory(new PropertyValueFactory<>("errorTermValue"));
		col_ErrorValue.setCellFactory(TextFieldTableCell.forTableColumn());

		// When one starts editing the ErrorTermValue column, a message is displayed giving 
		// guidance on how to commit the change when done.
		col_ErrorValue.setOnEditStart((CellEditEvent<Quantity, String> t) -> {
			lbl_EditingGuidance.setVisible(true);
		});			

		// When the user commits the change, the editing guidance message is once again hidden and
		// the system sorts the data in the table so the data will always appear sorted in the table
		col_ErrorValue.setOnEditCommit(
				(CellEditEvent<Quantity, String> t) -> {
					t.getTableView().getItems().get(
							t.getTablePosition().getRow()).setErrorTermValue(t.getNewValue());
					lbl_EditingGuidance.setVisible(false);
				});

		//**********//
		// This is the column that supports the UnitsValue field.  
		TableColumn<Quantity, String> col_UnitsValue = new TableColumn<Quantity, String>("Units");
		//ComboBox<String> cb_operand1 = new ComboBox<String>();


		col_UnitsValue.setMinWidth(100);


		col_UnitsValue.setCellValueFactory(new PropertyValueFactory<>("unitsValue"));  
		col_UnitsValue.setCellFactory(ComboBoxTableCell.forTableColumn(variable_units));

		// When one starts editing the UnitsValue column, a message is displayed giving 
		// guidance on how to commit the change when done.
		col_UnitsValue.setOnEditStart((CellEditEvent<Quantity, String> t) -> {
			lbl_EditingGuidance.setVisible(true);
		});			

		// When the user commits the change, the editing guidance message is once again hidden and
		// the system sorts the data in the table so the data will always appear sorted in the table
		col_UnitsValue.setOnEditCommit(
				(CellEditEvent<Quantity, String> t) -> {
					t.getTableView().getItems().get(
							t.getTablePosition().getRow()).setUnitsValue(t.getNewValue());
					lbl_EditingGuidance.setVisible(false);
				});

		//**********//
		// This is the column that supports the UnitsValue field.  

		TableColumn<Quantity, Quantity> del_UnitsValue = new TableColumn<Quantity, Quantity> ("");
		del_UnitsValue.setMinWidth(30);

		del_UnitsValue.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		del_UnitsValue.setCellFactory(param -> new TableCell<Quantity, Quantity>() {
			private final Button deleteButton = new Button("Remove");


			protected void updateItem(Quantity person, boolean empty) {
				super.updateItem(person, empty);

				if (person == null) {
					setGraphic(null);
					return;
				}

				setGraphic(deleteButton);
				deleteButton.setOnAction(event -> tableData.remove(person));
			}
		});




		//**********//
		// The follow sets up the editing guidance text,. positions it below the table, sets the
		// text red, and hides the text so it is only shown during the edit process.
		lbl_EditingGuidance.setMinWidth(600);
		lbl_EditingGuidance.setLayoutX(20);
		lbl_EditingGuidance.setLayoutY(470);
		lbl_EditingGuidance.setTextFill(Color.RED);
		lbl_EditingGuidance.setVisible(false);

		lbl_Editing.setMinWidth(600);
		lbl_Editing.setLayoutX(90);
		lbl_Editing.setLayoutY(470);
		lbl_Editing.setTextFill(Color.RED);
		lbl_Editing.setVisible(false);

		// The right-most three columns are grouped into a single column as they define the value
		// elements of the definition.
		TableColumn<Quantity, String> col_ValueGroup = new TableColumn<Quantity, String>("Value");
		col_ValueGroup.getColumns().add(col_MeasureValue);
		col_ValueGroup.getColumns().add(col_ErrorValue);
		col_ValueGroup.getColumns().add(col_UnitsValue);
		col_ValueGroup.getColumns().add(del_UnitsValue);
		// As we are setting up the GUI, we begin by sorting the data with which we start
		whenSorting = true;
		tableData.sort(Comparator.comparing(Quantity::getNameValue));
		whenSorting = false;
		System.out.println(UserInterface.currentRepositoryFile.getAbsolutePath());
		readDataFromRepository(UserInterface.currentRepositoryFile);
		// This loads the data from the ObservableList into the table, so the TableView code can
		// display it and provide all of the functions that it provides
		table.setItems(tableData);

		// This calls add the three major column titles into the table.  Notice that the right most
		// column is a composite of the three value fields (measure, error term, and units)
		table.getColumns().add(col_NameValue);
		table.getColumns().add(col_IsConstantValue);
		table.getColumns().add(col_ValueGroup);

		// With all of the GUI elements defined and initialized, we add them to the window pane
		thePane.getChildren().addAll(btn_Close,btn_Save, btn_Add, btn_SaveAs,table, lbl_EditingGuidance, lbl_Editing);

		// We set the scene into dialog for this window
		dialog.setScene(dialogScene);

		// We show the completed window to the user, making it possible for the user to start
		// clicking on the various GUI widgets in order to make things happen.
		dialog.show();       
	}

	/****
	 * The following method launches the file-chooser for choosing (or creating) the repository.
	 * @throws IOException
	 */
	private void launchSaveWindow() throws IOException {
		FileChooser saveWindow = new FileChooser();
		saveWindow.getExtensionFilters().add(new ExtensionFilter("Text File","*.txt"));
		File saveFile =saveWindow.showSaveDialog(null);
		saveDataToRepository(saveFile);
	}

	/*****
	 * The setupButton method is used to factor out recurring text in order to speed the coding and
	 * make it easier to read the code.
	 * 
	 * @param b	- Button that specifies which button is being set up
	 * @param w - int that specifies the minimum width of the button
	 * @param x - int that specifies the left edge of the button in the window
	 * @param y - int that specifies the upper edge of the button in the window
	 */
	private void setupButton(Button b, int w, int x, int y) {
		b.setMinWidth(w);
		b.setAlignment(Pos.BASELINE_CENTER);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	/****
	 * The following method reads the repository and loads it up to the dictionary so that it can be used
	 * in the programmable calculator
	 * @param theDataFile The Repository File
	 * @throws IOException
	 */
	private void readDataFromRepository(File theDataFile) throws IOException {
		tableData.clear();
		Scanner fileReader = new Scanner(theDataFile);
		perform.readTheData(fileReader);
		fileReader.close();
		for (int ndx=0; ndx<perform.getDictionaryTotalElements();ndx++) {
			System.out.println("This data" +" is recieved at index"+ " "+ndx);
			String[] recievedData = perform.getTheDataAtIndex(ndx);

			Quantity newQuantity = new Quantity(recievedData[0],recievedData[1],recievedData[2],recievedData[3],recievedData[4]);
			tableData.add(newQuantity);
		}
	}

	/******
	 * The following method uses the table-view to save the data directly into the repository (desired file).
	 * @param theDataFile The File in which the data is to be saved.
	 * @throws IOException
	 */
	private  void saveDataToRepository(File theDataFile) throws IOException {
		/************* Writing to Dictionary ***********************/

		if (theDataFile==null) return;
		FileWriter writer = new FileWriter(theDataFile);
		writer.write("CalculatorValues\n");
		for (int ndx=0; ndx<tableData.size();ndx++) {
			writer.write(tableData.get(ndx).getNameValue()+"\n");
			writer.write(tableData.get(ndx).getMeasureValue()+"\n");
			writer.write(tableData.get(ndx).getErrorTermValue()+"\n");
			writer.write(tableData.get(ndx).getUnitsValue()+"\n");
			writer.write(tableData.get(ndx).getIsConstantValue()+"\n");
		}
		writer.close();
		readDataFromRepository(theDataFile);
	}
	/***
	 * The following method adds a new entry to the dictionary and saves it directly to the repository to 
	 * prevent data-losses.
	 * @param name Name of the Value
	 * @param mv Measured-Value
	 * @param et Error-Term
	 * @param un Unit
	 * @param c Is it a constant or variable?
	 * @throws IOException
	 */
	public void addToTableView(String name, String mv, String et, String un, String c) throws IOException {
		tableData.add(new Quantity(name, c, mv, et, un));
		saveDataToRepository(UserInterface.currentRepositoryFile);
	}
	
	/****
	 * This method searches for an entry in the dictionary and fetches its definitions.
	 * @param nameToSearch The entry to be searched
	 * @return fetchedDefinition The returned definition
	 * @throws IOException
	 */
	public static String fetchDefinition(String nameToSearch) throws IOException {
		String fetchedDefinition = perform.fetchDefinition(nameToSearch);

		return  fetchedDefinition;


	}



}

