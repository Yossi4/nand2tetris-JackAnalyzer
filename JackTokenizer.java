import java.io.*;
import java.util.*;

public class JackTokenizer {
    // Current fields, nmay add more later:

    private Scanner in; // Scanner of reading lines from .jack files
    private static ArrayList<String> keyWordsArray; // Will store all keyWords once constrcuor is called
    private static String symbols; // "Super-string" that contains all possible symbols
    private static String operations; // "super-string" that contains all possible op's
    public ArrayList<String> tokens; // array of type String for storing all tokens
    private String currentLine; // contains all the tokens in the CURRENT line.
    private String currentTokenType; //contains the type of the CURRENT token.
    private String keyWordType; // contains the type of the keybord.
    private char SymbolType; // contains the type of the symbol.
    private String Identifier; // contains the value of identifier. 
    private String StringValue; //contains the value of the string. 
    private int IntValue; // contains the value of the integer.
    private int pointer; // index of the token in the array list of tokens.
    private boolean ind; // indicator for moving between tokens.


    // Initializing the Keywors:
    static {
        keyWordsArray = new ArrayList<String>();
        keyWordsArray.add("class");
        keyWordsArray.add("constructor");
        keyWordsArray.add("function");
        keyWordsArray.add("method");
        keyWordsArray.add("field");
        keyWordsArray.add("static");
        keyWordsArray.add("var");
        keyWordsArray.add("int");
        keyWordsArray.add("char");
        keyWordsArray.add("boolean");
        keyWordsArray.add("void");
        keyWordsArray.add("true");
        keyWordsArray.add("false");
        keyWordsArray.add("null");
        keyWordsArray.add("this");
        keyWordsArray.add("do");
        keyWordsArray.add("if");
        keyWordsArray.add("else");
        keyWordsArray.add("while");
        keyWordsArray.add("return");
        keyWordsArray.add("let");
        operations = "+-*/&|<>=";
        symbols = "{}()[].,;+-*/&|<>=-~";
    
    }

    // Constructor + initialiizer for the tokens:
    public JackTokenizer (File file) throws IOException{
        in = new Scanner(new FileReader(file));

        currentLine = ""; // Going to be populated:
        while (in.hasNextLine()) 
        {
            String thisLine = in.nextLine();
            while (thisLine.equals("") || containsComments(thisLine)) // either empty or has ANY comments
            {
                if (containsComments(thisLine)) //"case 1"
                {
                    thisLine = eliminateComments(thisLine); 
                }
                if (thisLine.trim().equals("")) 
                {
                    if (in.hasNextLine()) 
                    {
                        thisLine = in.nextLine();    
                    }    
                    else break;
                }
            } // End of inner while
            currentLine += thisLine.trim();   
        } //End of main while
        
        //Populating the arrays:
        tokens = new ArrayList<String>();
        while (currentLine.length() > 0) {
            while (currentLine.charAt(0) == ' ') {
                currentLine = currentLine.substring(1);
            }
         
            for (int i = 0; i < keyWordsArray.size(); i++) {
                if (currentLine.startsWith(keyWordsArray.get(i).toString())) {
                    String keyword = keyWordsArray.get(i).toString();
                    tokens.add(keyword);
                    currentLine = currentLine.substring(keyword.length());
                }

            }
            
            if (symbols.contains(currentLine.substring(0, 1))) {
                char symbol = currentLine.charAt(0);
                tokens.add(Character.toString(symbol));
                currentLine = currentLine.substring(1); // Increamenting
            }

            else if (Character.isDigit(currentLine.charAt(0))) {
                String value = currentLine.substring(0, 1);
                currentLine = currentLine.substring(1);
                while (Character.isDigit(currentLine.charAt(0))) {
                    value += currentLine.substring(0, 1);
                    currentLine = currentLine.substring(1); // "Increamenting"
                }
                tokens.add(value);

            }

            else if (currentLine.substring(0, 1).equals("\"")) {
                currentLine = currentLine.substring(1);
                String str = "\"";
                while ((currentLine.charAt(0) != '\"')) {
                    str += currentLine.charAt(0);
                    currentLine = currentLine.substring(1); // "Increamenting"
                }
                str = str + "\"";
                tokens.add(str);
                currentLine = currentLine.substring(1); // "Increamenting"

            }

            else if (Character.isLetter(currentLine.charAt(0)) || (currentLine.substring(0, 1).equals("_"))) {
                String strIdentifier = currentLine.substring(0, 1);
                currentLine = currentLine.substring(1);
                while ((Character.isLetter(currentLine.charAt(0))) || (currentLine.substring(0, 1).equals("_"))) {
                    strIdentifier += currentLine.substring(0, 1);
                    currentLine = currentLine.substring(1);// "Increamenting"
                }
                tokens.add(strIdentifier);
            }

            ind = true; // For later use
            pointer = 0;
        }
    } // End of constructor

    // Helper func:
    private boolean containsComments(String str) {
        boolean contains = false;
        if (str.contains("//") || str.contains("/*") || str.startsWith(" *")) {
            contains = true;
        }
        return contains;
    }

    // Helper func:
    private String eliminateComments(String str) {
        String NoComments = str;
        if (containsComments(str)) {
            int dev;
            
            
            if (str.startsWith(" *")) {
                dev = str.indexOf("*");
            } else if (str.contains("/*")) {
                dev = str.indexOf("/*");
            } else {
                dev = str.indexOf("//");
            }
            
            
            NoComments = str.substring(0, dev).trim(); // Eliminating starting and trialing white spaces

        }
        return NoComments;
    }

    public boolean hasMoreTokens() throws IOException{
        return pointer < tokens.size(); // Meaning we have more tokens in the array => more tokens generaly
    }


    public void advance() throws IOException{
        if (hasMoreTokens() && !ind) 
        {
            pointer ++; // For accuiring the next token (potentialy)
        }
        else if (ind) 
        {
            ind = false;    
        }

        String thisToken = tokens.get(pointer); // Accuiring the current token
        if(keyWordsArray.contains(thisToken)){
            currentTokenType = "KEYWORD";
            keyWordType = thisToken;
        }
        else if (symbols.contains(thisToken)){
            SymbolType = thisToken.charAt(0);
            currentTokenType = "SYMBOL"; 
        }
        else if (Character.isDigit(thisToken.charAt(0))){
            IntValue = Integer.parseInt(thisToken);
            currentTokenType = "INT_CONST";
        }
        else if (thisToken.substring(0, 1).equals("\"")){
            currentTokenType = "STRING_CONST";
            StringValue = thisToken.substring(1, thisToken.length() - 1);
        }
        else if ((Character.isLetter(thisToken.charAt(0))) || (thisToken.charAt(0) == '_')) {
            currentTokenType = "IDENTIFIER";
            Identifier = thisToken;
        }
    
    else {
        return; // for completeness
    }
    }

    public String tokenType(){
        return this.currentTokenType;
    }

    public String keyWord(){
        return this.keyWordType;
    }

    public char symbol(){
        return this.SymbolType;
    }
    
    public String identifier(){
        return this.Identifier;
    }

    public int intVal(){
        return this.IntValue;
    }

    public String stringVal(){
        return this.StringValue;
    }

    // For ease of the tokens pointer handling
    public void decrementPointer() {
        if (pointer > 0) {
            pointer--;
        }
    }

    // To distinguish between operational and non-operatinal symbols:
    public boolean isOp() {
        for (int i = 0; i < operations.length(); i++) {
            if (operations.charAt(i) == SymbolType) {
                return true;
            }
        }
        return false;
    }



}
