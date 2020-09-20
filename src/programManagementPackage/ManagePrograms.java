package programManagementPackage;
import java.util.Scanner;



import sjs.api.dictionary.Dictionary;
/***
 * The Business Logic Class Linking The Programmable Calculator and The Dictionary API
 * @author JSGREWAL
 * @version 1.00 2019-03-31 The First Release
 *
 */
public class ManagePrograms {
	// The Dictionary Object
private Dictionary theDictionary = new Dictionary();
/**
 * The routine for loading the dictionary
 * @param dictReader
 */
public void loadTheDictionary(Scanner dictReader) {
	theDictionary.defineDictionary(dictReader);
}
/***
 * The routine for fetching all program names
 * @return
 */
public String getAllProgramNames() {
	return theDictionary.getAllProgramNames();
}
/***
 * The routine for fetching the program details
 * @param name The name of program
 * @return The Detail of Program
 */
public String getProgramByName(String name) {
	 theDictionary.setSearchString(name);
return theDictionary.findNextEntry().getDefinition();
}
/***
 * The routine for adding entry to the dictionary object
 * @param name Name of Program
 * @param def Definition of Program
 */
public void addEntry(String name, String def) {
	theDictionary.addEntry(name, def);
}

}
