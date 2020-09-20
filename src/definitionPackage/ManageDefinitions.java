package definitionPackage;
import java.io.FileWriter;


import java.io.IOException;

import java.util.Scanner;


import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import sjs.api.dictionary.DictEntry;
import sjs.api.dictionary.Dictionary;

public class ManageDefinitions {
	Dictionary theDictionary = null;
	public ManageDefinitions() {

	}
	/**********
	 * This private method reads the data from the data file and places it into a data structure
	 * for later processing, should the user decide to do that.  (Recall that the input has already
	 * been scanned by the function fileContentsAreValid(), so redundant checks are not needed.)
	 * 
	 * @param in	The parameter is a Scanner object that is set up to read the input file
	 */
	public void readTheData(Scanner in) {
		try {
			theDictionary = new Dictionary();


			// Read in the dictionary and store what is read in the Dictionary object
			theDictionary.defineDictionary(in);
		}
		catch(Exception e) {
			infoBox("Need to Enter valid file for this calculator", "Error", "File contents are not Valid");
		}
	}
	/***
	 * If the file do not have valid contents, then this method shows error warnings.
	 * @param infoMessage
	 * @param titleBar
	 * @param headerMessage
	 */
	public void infoBox(String infoMessage, String titleBar, String headerMessage)
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(titleBar);
		alert.setHeaderText(headerMessage);
		alert.setContentText(infoMessage);
		ButtonType close = new ButtonType("Close");
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(close);
		alert.close();
	}

	/***
	 * This method is used to get the data at specific index.
	 * @param ndx
	 * @return
	 */
	public String[] getTheDataAtIndex(int ndx) {

		DictEntry currentEntry = theDictionary.getDictEntry(ndx);

		String theWord = currentEntry.getWord();

		String theDefinition = currentEntry.getDefinition();

		Scanner defScanner = new Scanner(theDefinition);
		String mv = defScanner.nextLine();
		String et = defScanner.nextLine();
		String unit = defScanner.nextLine();
		defScanner.close();
		String isConstant = Boolean.toString(currentEntry.getNature());
		String[] theDataSet= {theWord,isConstant,mv,et,unit};
		return theDataSet; 
	}
	/***
	 * This method is used to write the data directly to a file using FileWriter
	 * @param writer
	 * @throws IOException
	 */
	public void writeTheData(FileWriter writer) throws IOException {
		for (int ndxOfSmallest=0;ndxOfSmallest<theDictionary.getNumEntries()-1;ndxOfSmallest++)
			for (int theRest = ndxOfSmallest+1;theRest<theDictionary.getNumEntries();theRest++)
				if (theDictionary.getDictEntry(ndxOfSmallest).getWord().compareTo(
						theDictionary.getDictEntry(theRest).getWord()
						)>=0) {
					DictEntry temp = theDictionary.getDictEntry(ndxOfSmallest);
					theDictionary.setDictEntry(ndxOfSmallest, theDictionary.getDictEntry(theRest));
					theDictionary.setDictEntry(theRest, temp);
				}

		for (int ndx=0;ndx<theDictionary.getNumEntries();ndx++) {
			DictEntry de = theDictionary.getDictEntry(ndx);
			writer.write(de.getWord()+"\n");
			writer.write(de.getDefinition());
			writer.write(Boolean.toString(de.getNature()));
		}
		writer.close();
	}
	/***
	 * Getter for Number of Elements In Dictionary
	 * @return
	 */
	public int getDictionaryTotalElements() {
		return theDictionary.getNumEntries();
	}
	/***
	 * This method adds entry to the dictionary
	 * @param name
	 * @param mv
	 * @param et
	 * @param unit
	 * @param constant
	 */
	public void addEntry(String name, String mv, String et, String unit, boolean constant) {
		theDictionary.addEntry(name,mv,et,unit,constant);
	}
	/***
	 * This method is used to fetch the definition of a specific term.
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public String fetchDefinition(String str) throws IOException{
		theDictionary.setSearchString(str);
		DictEntry theEntry= theDictionary.findNextEntry();
		if (theEntry==null) return "NOT_FOUND";
		return theEntry.getDefinition();


	}

}

