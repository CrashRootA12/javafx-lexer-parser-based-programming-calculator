package programManagementPackage;


import java.io.File;


import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Scanner;
import java.util.Stack;


import definitionPackage.DefinitionsUserInterface;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import mainCalculatorPackage.UserInterface;
import parser.ExpressionTreeBuilderEvaluator;


/***
 *<p> Title: ProgramsUserInterface Class. </p>
 *
 * <p> Description: The Java/FX-based user interface for the programmable calculator. The class works with String
 * objects and passes work to other classes to deal with all other aspects of the computation.</p>
 *
 * @author JSGREWAL
 * @version 1.00 2019-03-31 The first release
 * @version 2.00 2019-05-05 Updated to Support print and input statements
 *
 */
public class ProgramsUserInterface {
    /***
     * The enumeration for scenes present in the UI
     * @author JSGREWAL
     */
    private enum SceneID {
        CREATE,
        EDIT,
        HOME

    }

    /***
     * The enumeration for each element of measured value, error term and unit
     * @author JSGREWAL
     */
    public enum CalculatorValueElements {
        MEASURED_VALUE,
        ERROR_TERM,
        UNIT,
        NONE
    }
    public static   volatile  boolean inputIsAwaited = false;
    private DefinitionsUserInterface gui_definitions = null;
    private ManagePrograms perform = new ManagePrograms();
    private Stage the_primary_stage;
    private Scene the_primary_scene;
    private Scene the_create_scene;
    private final double PRIMARY_STAGE_WIDTH = 800;
    private final double PRIMARY_STAGE_HEIGHT = 700;
    private final double BUTTON_WIDTH = 40;
    private Label lbl_run_title = new Label("Run a Program");
    private String filePath = null;
    private static Button btn_input_enter = new Button("Enter");
    private Button btn_run_debug = new Button("Debug This Program");
    private static TextField console_input_field = new TextField();
    private Button btn_run_runAgain = new Button("Run Again");
    private TextArea txt_program_display = new TextArea();
    private ComboBox<String> cb_programList = new ComboBox<>();
    private TextField txt_fileAddress = new TextField();
    private FileChooser inputFileChooser = new FileChooser();
    private FileChooser outputFileChooser = new FileChooser();
    private boolean programsAreLoaded = false;
    private Label lbl_create_title = new Label("Create a program");
    private TextArea workspace_create = new TextArea();
    private TextField txt_create_programName = new TextField();
    private Label lbl_create_programName = new Label("Name");
    private Button btn_create_run = new Button("Run This Program");
    private Button btn_create_save = new Button("Save This Program");
    private Label lbl_edit_title = new Label("Edit The Program");
    private TextArea workspace_edit = new TextArea();
    private Button btn_edit_run = new Button("Run This Program");
    private Button btn_edit_save = new Button("Save This Program");
    private TextField txt_edit_programName = new TextField();
    private Label lbl_edit_programName = new Label("Name");
    private Scene theEditScene;
    private ExpressionTreeBuilderEvaluator expEvaluator = new ExpressionTreeBuilderEvaluator();
    private Stack<String> expStack = new Stack<>();
    private Stack<int[]> posStack = new Stack<>();
    private static ArrayList<String[]> valueStorage = new ArrayList<>();

    /***
     * The constructor of the Programmable Calculator User Interface
     */
    public ProgramsUserInterface() {
        the_primary_stage = new Stage();
        the_primary_stage.setTitle("The Programmable Calculator");
        Pane root1 = new Pane();
        Button btn_load = new Button("Load from File");
        setupButtonUI(btn_load, "Arial", 24, BUTTON_WIDTH, Pos.BASELINE_CENTER, 260, 125);
        Button btn_p_create = new Button("Create a Program");
        setupButtonUI(btn_p_create, "Arial", 24, BUTTON_WIDTH, Pos.BASELINE_CENTER, 30, 100);
        Button btn_p_edit = new Button("Edit a Program");
        setupButtonUI(btn_p_edit, "Arial", 24, BUTTON_WIDTH, Pos.BASELINE_CENTER, 30, 150);
        Button btn_p_run = new Button("Run a Program");
        setupButtonUI(btn_p_run, "Arial", 24, BUTTON_WIDTH, Pos.BASELINE_CENTER, 30, 200);
        Label lbl_p_title = new Label("The Programmable Calculator");
        setupLabelUI(lbl_p_title, "Arial", 34, 20, Pos.BASELINE_CENTER, 200, 10);
        setupTextUI(txt_fileAddress, "Arial", 18, 400, Pos.BASELINE_CENTER, 260, 180, false);
        cb_programList.setLayoutX(510);
        cb_programList.setLayoutY(230);
        cb_programList.setVisible(false);
        txt_program_display.setLayoutX(30);
        txt_program_display.setLayoutY(310);
        txt_program_display.setEditable(false);
        txt_program_display.setFont(Font.font("Calibri", 14));
        Button btn_loadDictionary = new Button("Load Dictionary");
        setupButtonUI(btn_loadDictionary, "Arial", 18, BUTTON_WIDTH, Pos.BASELINE_CENTER, 580, 310);
        root1.getChildren().addAll(
                btn_loadDictionary,
                txt_fileAddress, lbl_p_title, btn_p_create, btn_p_edit, btn_p_run, btn_load, cb_programList
                //,btn_run_debug,btn_run_runAgain
                , txt_program_display
        );
        btn_loadDictionary.setOnAction(e -> callDefinitionsUI());
        btn_load.setOnAction((event) -> {
            try {
                loadThePrograms();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        btn_p_run.setOnAction((event) -> scene_constructor_run(perform.getProgramByName(
                cb_programList.getSelectionModel().getSelectedItem()), SceneID.HOME));
        btn_p_create.setOnAction((event) -> scene_constructor_create());
        btn_p_edit.setOnAction((event) -> scene_constructor_edit());
        cb_programList.setOnAction((event) -> display());
        txt_program_display.setPromptText("Selected Program Will Be Displayed Here");
        the_primary_scene = new Scene(root1, PRIMARY_STAGE_WIDTH, PRIMARY_STAGE_HEIGHT);
        the_primary_stage.setScene(the_primary_scene);
        the_primary_stage.initStyle(StageStyle.DECORATED);
        the_primary_stage.show();
    }

    /***
     * The following method creates a scene which the user can use to create and save his or her own program.
     */
    private void scene_constructor_create() {
        workspace_create.clear();
        Pane root3 = new Pane();
        setupLabelUI(lbl_create_title, "Arial", 34, 20, Pos.BASELINE_CENTER, 200, 10);
        workspace_create.setLayoutX(145);
        workspace_create.setLayoutY(70);
        workspace_create.setMinHeight(300);
        workspace_create.setPromptText("Write the Program Here");
        Button backButton = new Button("<<Back");
        setupButtonUI(backButton, "Arial", 18, 38, Pos.BASELINE_CENTER, 10, PRIMARY_STAGE_HEIGHT - 30);
        setupButtonUI(btn_create_run, "Arial", 24, BUTTON_WIDTH, Pos.BASELINE_CENTER, 145, 450);
        setupButtonUI(btn_create_save, "Arial", 24, BUTTON_WIDTH, Pos.BASELINE_CENTER, 450, 450);
        setupTextUI(txt_create_programName, "Arial", 18, 500, Pos.BASELINE_CENTER, 145, 380, true);
        setupLabelUI(lbl_create_programName, "Arial", 18, 38, Pos.BASELINE_CENTER, 10, 380);
        root3.getChildren().addAll(btn_create_save, lbl_create_title, workspace_create, backButton, btn_create_run,
                txt_create_programName, lbl_create_programName);
        backButton.setOnAction((event) -> the_primary_stage.setScene(the_primary_scene));
        btn_create_save.setOnAction((event) -> {
            String name = txt_create_programName.getText();
            String program = workspace_create.getText();
            perform.addEntry(name, program);
            try {
                saveToRepository();
            } catch (IOException e) {

                e.printStackTrace();
            }
        });
        btn_create_run.setOnAction(e -> scene_constructor_run(workspace_create.getText(), SceneID.CREATE));
        txt_create_programName.clear();
        the_create_scene = new Scene(root3, PRIMARY_STAGE_WIDTH, PRIMARY_STAGE_WIDTH);
        the_primary_stage.setScene(the_create_scene);
    }

    /***
     * The following method constructs the scene which the user will use for editing the programs from repository.
     */
    private void scene_constructor_edit() {
        workspace_edit.clear();
        txt_edit_programName.clear();
        Pane the_root_4 = new Pane();
        String name = cb_programList.getSelectionModel().getSelectedItem();
        txt_edit_programName.setText(name);
        String def = perform.getProgramByName(name);
        workspace_edit.setText(def);
        setupLabelUI(lbl_edit_title, "Arial", 34, 20, Pos.BASELINE_CENTER, 200, 10);
        workspace_edit.setLayoutX(145);
        workspace_edit.setLayoutY(70);
        workspace_edit.setMinHeight(300);
        workspace_edit.setPromptText("Write the Program Here");
        Button backButton = new Button("<<Back");

        setupButtonUI(backButton, "Arial", 18, 38, Pos.BASELINE_CENTER, 10, PRIMARY_STAGE_HEIGHT - 30);
        setupButtonUI(btn_edit_run, "Arial", 24, BUTTON_WIDTH, Pos.BASELINE_CENTER, 145, 450);
        setupButtonUI(btn_edit_save, "Arial", 24, BUTTON_WIDTH, Pos.BASELINE_CENTER, 450, 450);
        setupTextUI(txt_edit_programName, "Arial", 18, 500, Pos.BASELINE_CENTER, 145, 380, true);
        setupLabelUI(lbl_edit_programName, "Arial", 18, 38, Pos.BASELINE_CENTER, 10, 380);
        the_root_4.getChildren().addAll(btn_edit_save, lbl_edit_title, workspace_edit, backButton, btn_edit_run,
                txt_edit_programName, lbl_edit_programName);
        backButton.setOnAction((event) -> the_primary_stage.setScene(the_primary_scene));
        btn_edit_run.setOnAction(e -> scene_constructor_run(workspace_edit.getText(), SceneID.EDIT));
        btn_edit_save.setOnAction((event) -> {
            String n = txt_edit_programName.getText();
            String program = workspace_edit.getText();
            perform.addEntry(n, program);
            try {
                saveToRepository();
            } catch (IOException e) {

                e.printStackTrace();
            }
        });

        theEditScene = new Scene(the_root_4, PRIMARY_STAGE_WIDTH, PRIMARY_STAGE_WIDTH);
        the_primary_stage.setScene(theEditScene);

    }

    /***
     * The following methods creates the scene which will be used for executing and debugging the programs.
     * @param the_program The pre-entered program
     * @param id Scene Caller ID
     */
    private void scene_constructor_run(String the_program, SceneID id) {
        ToggleGroup rbPair = new ToggleGroup();
        RadioButton rb_Console = new RadioButton("Console");
        RadioButton rb_Workspace = new RadioButton("Workspace");
        RadioButton rb_Debug = new RadioButton("Debug Console");
        console_input_field.setEditable(false);
        btn_input_enter.setDisable(true);
        TextArea run_console = new TextArea();
        TextArea run_workspace = new TextArea();
        TextArea debug_console = new TextArea();
        run_console.setPromptText("CONSOLE");
        run_workspace.setPromptText("WORKSPACE");
        run_console.setLayoutX(145);
        run_console.setLayoutY(110);
        run_workspace.setText(the_program);
        run_console.setEditable(false);
        debug_console.setEditable(false);
        run_workspace.setLayoutX(145);
        run_workspace.setLayoutY(110);
        debug_console.setLayoutX(145);
        debug_console.setLayoutY(110);
        Label lbl_run_enterInput = new Label("Enter Input: ");
        rb_Console.setOnAction((event) -> {
            run_workspace.setVisible(false);
            run_console.setVisible(true);
            debug_console.setVisible(false);
            console_input_field.setVisible(true);
            lbl_run_enterInput.setVisible(true);
            btn_input_enter.setVisible(true);
        });
        rb_Workspace.setOnAction((event) -> {
            run_workspace.setVisible(true);
            run_console.setVisible(false);
            debug_console.setVisible(false);
            console_input_field.setVisible(false);
            lbl_run_enterInput.setVisible(false);
            btn_input_enter.setVisible(false);
        });
        rb_Debug.setOnAction(e -> {
            debug_console.setVisible(true);
            run_workspace.setVisible(false);
            run_console.setVisible(false);
            console_input_field.setVisible(false);
            lbl_run_enterInput.setVisible(false);
            btn_input_enter.setVisible(false);
        });

    btn_input_enter.setOnAction(e->{
        run_console.appendText("Input received is: "+console_input_field.getText()+"\n");
        inputIsAwaited = false;
        console_input_field.setDisable(true);
        btn_input_enter.setDisable(true);

    });
        setupLabelUI(lbl_run_enterInput, "Arial", 18, 38, Pos.BASELINE_CENTER, 10,
                run_console.getHeight() + 310);
        setupTextUI(console_input_field, "Arial", 18, 500, Pos.BASELINE_CENTER, 145,
                run_console.getHeight() + 310, true);

        rb_Console.setToggleGroup(rbPair);
        rb_Workspace.setToggleGroup(rbPair);
        rb_Debug.setToggleGroup(rbPair);
        rb_Console.setLayoutX(145);
        rb_Console.setLayoutY(80);
        rb_Workspace.setLayoutX(225);
        rb_Workspace.setLayoutY(80);
        rb_Debug.setVisible(false);
        rb_Debug.setLayoutX(325);
        rb_Debug.setLayoutY(80);
        rb_Console.setSelected(true);
        setupLabelUI(lbl_run_title, "Arial", 34, 20, Pos.BASELINE_CENTER, 200, 10);
        Button backButton = new Button("<<Back");
        setupButtonUI(backButton, "Arial", 18, 38, Pos.BASELINE_CENTER, 10, PRIMARY_STAGE_HEIGHT - 30);
        setupButtonUI(btn_run_debug, "Arial", 18, 38, Pos.BASELINE_CENTER, 145,
                run_console.getHeight() + 350);
        backButton.setOnAction((event) -> {
            if (id.equals(SceneID.HOME)) {
                the_primary_stage.setScene(the_primary_scene);
            } else if (id.equals(SceneID.CREATE)) {
                workspace_create.setText(run_workspace.getText());
                the_primary_stage.setScene(the_create_scene);
            } else if (id.equals(SceneID.EDIT)) {
                workspace_edit.setText(run_workspace.getText());
                the_primary_stage.setScene(theEditScene);
            }
        });
        setupButtonUI(btn_input_enter, "Arial", 18, 38, Pos.BASELINE_LEFT, 170 + 485,
                run_console.getHeight() + 310);
        setupButtonUI(btn_run_runAgain, "Arial", 18, 38, Pos.BASELINE_LEFT, 170 + 200,
                run_console.getHeight() + 350);

        btn_run_runAgain.setOnAction(e -> {
            run_console.clear();
            runTheProgram(run_workspace.getText(), run_console, debug_console);
            rb_Console.setSelected(true);
            run_workspace.setVisible(false);
            run_console.setVisible(true);
            console_input_field.setVisible(true);
            lbl_run_enterInput.setVisible(true);
            btn_input_enter.setVisible(true);
        });
        btn_run_debug.setOnAction(e -> {
            runTheProgram(run_workspace.getText(), run_console, debug_console);
            rb_Debug.setSelected(true);
            rb_Debug.setVisible(true);
            debug_console.setVisible(true);
        run_workspace.setVisible(false);
            btn_input_enter.setVisible(false);
        run_console.setVisible(false);
        console_input_field.setVisible(false);
        lbl_run_enterInput.setVisible(false);
    });
        Pane root2 = new Pane();
        root2.getChildren().addAll(debug_console, rb_Debug, rb_Console, rb_Workspace, run_workspace,
                run_console, backButton,
                lbl_run_enterInput, btn_run_debug, btn_run_runAgain, lbl_run_title, console_input_field,btn_input_enter);
        Scene the_run_scene = new Scene(root2, PRIMARY_STAGE_WIDTH, PRIMARY_STAGE_HEIGHT);
        the_primary_stage.setScene(the_run_scene);
        runTheProgram(run_workspace.getText(), run_console, debug_console);
    }

    /***
     * Sub-routine which takes input and solve multi-line expressions.
     * @param expressions The Expression(s) to be executed
     * @param run_console The Console
     * @param debug_console The Debug Console
     */
    private void runTheProgram(String expressions, TextArea run_console, TextArea debug_console) {
        run_console.clear();
        debug_console.clear();
        expStack.clear();
        posStack.clear();
        Scanner codeScanner = new Scanner(expressions);
        while (codeScanner.hasNext()) {
            //			manageParenthesis(expressions);
            expressions = codeScanner.nextLine();
            performOperations(CalculatorValueElements.NONE,run_console,debug_console,expressions);
            performOperations(CalculatorValueElements.MEASURED_VALUE, run_console, debug_console, expressions);
            performOperations(CalculatorValueElements.ERROR_TERM, run_console, debug_console, expressions);


            //			for (int i =0; i<sizeOfStack;i++) {
            //				String exp = expStack.pop();
            //				int[] coordinatePair = posStack.pop();
            //				solution = expEvaluator.solveExpression(exp, run_console, debug_console);
            //				String originalSubstring = theCopy.substring(coordinatePair[0],coordinatePair[1]+1);
            //				String newSubString = solution;
            //				theCopy.replace(originalSubstring, newSubString);
            //			}

        }
        codeScanner.close();
    }

    /***
     * This routine handles all the expression solving related to measured values and error terms.
     * @param element Tells what is going to be calculated--MV or ET or None
     * @param run_console The Console
     * @param debug_console The Debug Console
     * @param current_expression The Expression
     */
    private void performOperations(CalculatorValueElements element, TextArea run_console, TextArea debug_console,
                                   String current_expression) {

        StringBuilder revisedExpression = new StringBuilder();
        if (element.equals(CalculatorValueElements.MEASURED_VALUE))
            run_console.appendText("\n---------------------------\nCalculating Measured Values\n-------" +
                    "--------------------");
        else if (element.equals(CalculatorValueElements.ERROR_TERM))
            run_console.appendText("\n---------------------------\nCalculating Error Terms Values\n--------------" +
                    "-------------");
        for (int i = 0; i < current_expression.length(); i++) {
            if (!current_expression.startsWith("print") && !current_expression.startsWith("input")
                    && Character.isAlphabetic(current_expression.charAt(i))) {
                if (element.equals(CalculatorValueElements.MEASURED_VALUE))
                    revisedExpression.append(fetch_definition(current_expression.charAt(i),
                            CalculatorValueElements.MEASURED_VALUE));
                else if (element.equals(CalculatorValueElements.ERROR_TERM)) {
                    revisedExpression.append(fetch_definition(current_expression.charAt(i),
                            CalculatorValueElements.ERROR_TERM));
                }
            } else {
                if (element.equals(CalculatorValueElements.ERROR_TERM) &&
                        Character.isDigit(current_expression.charAt(i)))
                    revisedExpression.append("0.0");
                else revisedExpression.append(current_expression.charAt(i));
            }
        }
        current_expression = revisedExpression.toString();

        int indexOfLB = current_expression.indexOf('(');
        int indexOfRB = current_expression.lastIndexOf(')');

        String newExp = current_expression;

        if (!(indexOfLB == -1 || indexOfRB == -1)) {
            String expressionInsideBracket = current_expression.substring(indexOfLB + 1, indexOfRB);
            String expressionBeforeBracket = current_expression.substring(0, indexOfLB);
            String expressionAfterBracket = current_expression.substring(indexOfRB + 1);
            String sol_inB = expEvaluator.solveExpression(expressionInsideBracket, run_console, debug_console);
            newExp = expressionBeforeBracket + sol_inB + expressionAfterBracket;
        }
        expEvaluator.solveExpression(newExp, run_console, debug_console);
    }

	/*

	 private void manageParenthesis(String expressions) {
	 LinkedList<Integer> leftBracketIndexList = new LinkedList<Integer>();
	 for (int i =0; i<expressions.length();i++) {
	 if (expressions.charAt(i)=='(') leftBracketIndexList.add(i);
	 }
	 System.out.println(leftBracketIndexList.size());
	 LinkedList<Integer> rightBracketIndexList = new LinkedList<Integer>();
	 LinkedList<int[]> bracketPair = new LinkedList<int[]>();
	 for (int i =0; i<expressions.length();i++) {
	 if (expressions.charAt(i)==')') rightBracketIndexList.add(i);
	 }
	 System.out.println(rightBracketIndexList.size());
	 if (leftBracketIndexList.size()!=rightBracketIndexList.size()) {
	 System.out.println("Unbalanced Parenthesis!");
	 return;
	 }
	 Iterator<Integer> it_left = leftBracketIndexList.iterator();
	 Iterator<Integer> it_right = rightBracketIndexList.descendingIterator();
	 while (it_left.hasNext()&&it_right.hasNext()) {
	 int[] thePair = {it_left.next(),it_right.next()};
	 bracketPair.add(thePair);
	 }
	 Iterator<int[]> it_brackets = bracketPair.iterator();
	 while (it_brackets.hasNext()) {
	 int[] theRecievedPair = it_brackets.next();
	 String theExpression = expressions.substring(theRecievedPair[0]+1, theRecievedPair[1]);
	 expStack.push(theExpression);
	 posStack.push(theRecievedPair);
	 }
	 }
	 */

    /***
     * This method loads the program to the programmable calculator from repository file.
     * @throws FileNotFoundException File Not Found Exception
     */
    private void loadThePrograms() throws IOException {
        cb_programList.getItems().clear();
        File theInputFile = inputFileChooser.showOpenDialog(the_primary_stage);
        if (theInputFile == null) return;
        Scanner scnr_input = new Scanner(theInputFile);
        perform.loadTheDictionary(scnr_input);
        String allPrograms = perform.getAllProgramNames();
        Scanner scnr_progNames = new Scanner(allPrograms);
        while (scnr_progNames.hasNext()) {
            cb_programList.getItems().add(scnr_progNames.next());
        }
        scnr_progNames.close();
        String file_Address = theInputFile.getAbsolutePath();
        txt_fileAddress.setText(file_Address);
        cb_programList.getSelectionModel().selectFirst();
        cb_programList.setVisible(true);
        programsAreLoaded = true;
        display();

    }

    /***
     * The following routine displays the programs from the file on the User Interface
     */
    private void display() {
        txt_program_display.clear();
        if (programsAreLoaded) {
            String currentProgramName = cb_programList.getSelectionModel().getSelectedItem();
            txt_program_display.appendText("Program Name: " + currentProgramName +
                    "\n------------------------\n");
            txt_program_display.appendText(perform.getProgramByName(currentProgramName) +
                    "\n------------------------\n");
        }
    }

    /***
     * The following routine saves the created program to user-desired destination through the use of FileChooser.
     * @throws IOException if File Not Found
     */
    private void saveToRepository() throws IOException {
        outputFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(".txt",
                "*.txt"));
        File theOutputFile = outputFileChooser.showSaveDialog(the_primary_stage);
        if (theOutputFile == null) return;
        FileWriter writer = new FileWriter(theOutputFile);
        String allPrograms = perform.getAllProgramNames();
        Scanner scnr_allPrograms = new Scanner(allPrograms);
        writer.write("Dictionary\n");
        while (scnr_allPrograms.hasNext()) {
            String theProgramName = scnr_allPrograms.next();
            String theProgram = perform.getProgramByName(theProgramName);
            writer.write(theProgramName);
            writer.write(theProgram);
        }
        scnr_allPrograms.close();
        writer.close();
    }

    /**********
     * Private local method to initialize the standard fields for a button
     */
    private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }

    /**********
     * Private local method to initialize the standard fields for a label
     */
    private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    /**********
     * Private local method to initialize the standard fields for a text field
     */
    private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e) {
        t.setFont(Font.font(ff, f));
        t.setMinWidth(w);
        t.setMaxWidth(w);
        t.setAlignment(p);
        t.setLayoutX(x);
        t.setLayoutY(y);
        t.setEditable(e);
    }

    /****
     * This method setup the DefinitionUI for retrieving the variables and constants.
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
        setupTextUI(text_FilePath, "Calibri", 18, 500, Pos.BASELINE_LEFT, 40 + 25, 50, true);
        Button button_Browse = new Button("Browse");
        setupButtonUI(button_Browse, "Calibri", 18, 100, Pos.BASELINE_CENTER, 222 + 85, 100);
        Button button_Launch = new Button("Launch");
        setupButtonUI(button_Launch, "Calibri", 18, 100, Pos.BASELINE_CENTER, 340 + 115, 100);
        Button button_LoadLastFile = new Button("Load Last File");
        setupButtonUI(button_LoadLastFile, "Calibri", 18, 100, Pos.BASELINE_CENTER, 80 + 25, 100);
        Pane browserPane = new Pane();
        browserPane.getChildren().addAll(label_Load, label_File, text_FilePath, button_Browse, button_Launch,
                button_LoadLastFile);
        Scene browserScene = new Scene(browserPane, 600, 200);
        Stage browserStage = new Stage();
        button_Launch.setDisable(true);
        button_Browse.setOnAction((event) -> {
            UserInterface.currentRepositoryFile = repositoryBrowser.showOpenDialog(browserStage);
            if (UserInterface.currentRepositoryFile != null) {
                filePath = UserInterface.currentRepositoryFile.getAbsolutePath();
                text_FilePath.setText(filePath);
                button_Launch.setDisable(false);
            } else button_Launch.setDisable(true);
        });
        button_Launch.setOnAction((event) -> {
            try {
                gui_definitions = new DefinitionsUserInterface(null);
                browserStage.hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
		/*
		 Load Last file
		 */
        button_LoadLastFile.setOnAction((event) -> {
            try {
                if (filePath == null) {
                    show_alert();
                    return;
                }
                gui_definitions = new DefinitionsUserInterface(null);
                browserStage.hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        browserStage.setScene(browserScene);
        browserStage.setTitle("Load the Repository");
        browserStage.show();
    }

    /***
     * This routine shows alert when the repository file is not previously loaded.
     */
    private void show_alert() {
        Alert alert = new Alert(AlertType.WARNING); // Initialize a warning-type alert box
        // According to the passed parameter, set the content of the warning
        alert.setContentText("Repository File Not Loaded Previously.");

        // The width of the alert is 1000
        alert.setWidth(1000);
        // Show the alert (warning) box
        alert.show();
    }

    /***
     * Definition Fetcher Routine gives the Measured Value, Error Term and Unit to the caller.
     * @param character The Variable Name
     * @param element Specifier of what needs to be returned.
     * @return The desired element of the variable.
     */
    private String fetch_definition(char character, CalculatorValueElements element) {
        String str_var = Character.toString(character);
        String fetched_definition;
        String mv = "";
        String et = "";
        String unit = "";
        try {
            fetched_definition = DefinitionsUserInterface.fetchDefinition(str_var);
            Scanner definitionScanner = new Scanner(fetched_definition);
            if (definitionScanner.hasNextLine()) mv = definitionScanner.nextLine();
            if (definitionScanner.hasNextLine()) et = definitionScanner.nextLine();
            if (definitionScanner.hasNextLine()) unit = definitionScanner.nextLine();
            definitionScanner.close();
        } catch (IOException e) {
            System.err.println("Error! Definition Can't Be Fetched");
            e.printStackTrace();
        }
        String[] theArray = {mv, et, unit};
        valueStorage.add(theArray);
        if (element.equals(CalculatorValueElements.MEASURED_VALUE)) return mv;
        else if (element.equals(CalculatorValueElements.ERROR_TERM)) return et;
        else if (element.equals(CalculatorValueElements.UNIT)) return unit;
        return null;
    }

    /***
     * This routine signifies that the "input" statement is being processed and the thread is waiting for user to enter
     * awaited input.
     */
    public static  void  readyToTakeInput() {
        console_input_field.setEditable(true);
        btn_input_enter.setDisable(false);

    }


}
