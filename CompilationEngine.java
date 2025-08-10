import java.io.*;
public class CompilationEngine {
    //String currentToken; // Holds the current token, for ease of use
    //String currentTokenType; // Holds the current token's type, for ease of use
    BufferedWriter writer; // A writer that writes to the output file (XML in our case)
    JackTokenizer tokenizer; // Provides "tokenizing" services
    boolean ind; // as in the Tokrnizer


    public CompilationEngine(File inputFile, File outputFile) throws IOException{
        this.tokenizer = new JackTokenizer(inputFile);
        this.writer = new BufferedWriter(new FileWriter(outputFile));
        this.ind = true;
    }


    public void compileClass() throws IOException{
        // Using the tokenizer's advance() to accuire the first token (the class token):

        // "class ..."
        tokenizer.advance();
        writer.write("<class>\n");
        writer.write("<keyword> class </keyword>\n");

        // ...clasename
        tokenizer.advance();
        writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");

        // "{"
        tokenizer.advance();
        writer.write("<symbol> { </symbol>\n");

        // Now we need to compile the class level variables:
        compileCLassVarsDec(); // to implement

        // Any sub-routines:
        compileSubRoutine(); // to implement

        // Closing "}"
        writer.write("<symbol> } </symbol>\n");

        writer.write("</class>\n");
        writer.close();
    }

    public void compileCLassVarsDec() throws IOException{ //Tested
        // Accuiring first token:
        tokenizer.advance();
        
        
        // Traverins the input:
        while (tokenizer.keyWord().equals("static") || tokenizer.keyWord().equals("field")) // Expected first tokens
        {
            writer.write("<classVarDec>\n");
            writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>\n");
            tokenizer.advance();
            if (tokenizer.tokenType().equals("IDENTIFIER")) {
                writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            }
            else {
                writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>\n");
            }
            tokenizer.advance();
            writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenizer.advance();
            while (tokenizer.symbol() == ',') {
                writer.write("<symbol> , </symbol>\n");
                tokenizer.advance();
                writer.write(("<identifier> " + tokenizer.identifier() + " </identifier>\n"));
                tokenizer.advance();
            }
            writer.write("<symbol> ; </symbol>\n");
            tokenizer.advance();
            writer.write("</classVarDec>\n");
        }
        if (tokenizer.keyWord().equals("function") || tokenizer.keyWord().equals("method") || tokenizer.keyWord().equals("constructor")) {
            tokenizer.decrementPointer();
            return;
        }
    }


    public void compileSubRoutine() throws IOException{
        boolean flag = false;
        
        // Accuiring the next token:
        tokenizer.advance();

        // If this function was called at absence of subroutines:
        if (tokenizer.symbol() == '}' && tokenizer.tokenType().equals("SYMBOL")) 
        {
            return; // Nothing to parse, compileClass() will manage    
        }

        // If we've reached here we HAVE subroutines to compile:

        //If ANY subroutine:
        if (ind && (tokenizer.keyWord().equals("function")) || tokenizer.keyWord().equals("method") || tokenizer.keyWord().equals("constructor")) 
        {
            this.ind = false;
            writer.write("<subroutineDec>\n"); // starting the subroutine "block"
            flag = true;
        }

        if (tokenizer.keyWord().equals("function") || tokenizer.keyWord().equals("method") || tokenizer.keyWord().equals("constructor")) //without the "ind" flag!!!!
        {
            this.ind = true;
            writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>\n"); 
            tokenizer.advance(); // Next token
        }
        
        // "case" identifier
        if (tokenizer.tokenType().equals("IDENTIFIER")) 
        {
            writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n" ); 
            tokenizer.advance(); // Next token   
        }

        // "case" keyword after identifier
        else if (tokenizer.tokenType().equals("KEYWORD")) 
        {
            writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>\n" );    
            tokenizer.advance(); // Next token
        }


        // "case" identifier
        if (tokenizer.tokenType().equals("IDENTIFIER")) 
        {
            writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenizer.advance(); // Next token
        }


        //"case" "("
        if (tokenizer.symbol() =='(') 
        {
            writer.write("<symbol> ( </symbol>\n");
            writer.write("<parameterList>\n");
            compileParameterList(); // to implement
            writer.write("</parameterList>\n");
            writer.write("<symbol> ) </symbol>\n");    
        }
        compileSubRoutineBody(); // to implement
        if (flag) // Any subroutines left: 
        {
            writer.write("</subroutineBody>\n");
            writer.write("</subroutineDec>\n");  
            flag = true;
        }
        compileSubRoutine();
    }




    public void compileParameterList() throws IOException{
        tokenizer.advance();
        // "(" has already been inserted by compileSubRoutine() so we must have accuired a keyword/identifier

        while(!tokenizer.tokenType().equals("SYMBOL") || !(tokenizer.symbol() == ')'))
        {
            if (tokenizer.tokenType().equals("IDENTIFIER")) 
            {
                writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
                tokenizer.advance();    
            }
            else if (tokenizer.tokenType().equals("KEYWORD")) 
            {
                writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>\n");
                tokenizer.advance();    
            }
            else if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ',') 
            {
                writer.write("<symbol> , </symbol>\n"); //between parametes commas
                tokenizer.advance();    
            }
        }

    }

    public void compileSubRoutineBody() throws IOException {
        tokenizer.advance();
        if (tokenizer.symbol() == '{') // if we've encountered an actual subroutine
        {
            writer.write("<subroutineBody>\n");
            writer.write("<symbol> { </symbol>\n");
            tokenizer.advance();
        }
        while (tokenizer.keyWord().equals("var") && tokenizer.tokenType().equals("KEYWORD")) 
        {
            writer.write("<varDec>\n");
            tokenizer.decrementPointer(); 
            compileVarDec();// to implement
            writer.write("</varDec>\n");    
        }// End of while
        writer.write("<statements>\n");
        compileStatements(); // to implement
        writer.write("</statements>\n");
        writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
    }



    // Compiling variable declarations:
    public void compileVarDec() throws IOException{
        tokenizer.advance();
        if (tokenizer.keyWord().equals("var") && tokenizer.tokenType().equals("KEYWORD"))   
        {
            writer.write("<keyword> var </keyword>\n");
            tokenizer.advance();    
        }
        if (tokenizer.tokenType().equals("IDENTIFIER")) {
            writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenizer.advance();
        }
        else if (tokenizer.tokenType().equals("KEYWORD")) {
            writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>\n");
            tokenizer.advance();
        }
        if (tokenizer.tokenType().equals("IDENTIFIER")) {
            writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenizer.advance();
        }
        if ((tokenizer.tokenType().equals("SYMBOL")) && (tokenizer.symbol() == ',')) {
            writer.write("<symbol> , </symbol>\n");
            tokenizer.advance();
            writer.write(("<identifier> " + tokenizer.identifier() + " </identifier>\n"));
            tokenizer.advance();
        }
        if ((tokenizer.tokenType().equals("SYMBOL")) && (tokenizer.symbol() == ';')) {
            writer.write("<symbol> ; </symbol>\n");
            tokenizer.advance();
        }
    }


    public void compileStatements() throws IOException{
        if (tokenizer.symbol() == '}' && tokenizer.tokenType().equals("SYMBOL")) 
        {
            return;    
        }
        else if (tokenizer.keyWord().equals("do") && tokenizer.tokenType().equals("KEYWORD")) 
        {
            writer.write("<doStatement>\n");
            comlipeDo(); // to implement
            writer.write("</doStatement>\n");    
        }
        else if (tokenizer.keyWord().equals("let") && tokenizer.tokenType().equals("KEYWORD")) 
        {
            writer.write("<letStatement>\n");
            compileLet(); // to implement
            writer.write("</letStatement>\n");    
        }
        else if (tokenizer.keyWord().equals("while") && tokenizer.tokenType().equals("KEYWORD")) 
        {
            writer.write("<whileStatement>\n");
            compileWhile(); // to implement
            writer.write("</whileStatement>\n");    
        }
        else if (tokenizer.keyWord().equals("if") && tokenizer.tokenType().equals("KEYWORD")) 
        {
            writer.write("<ifStatement>\n");
            compileIf(); // to implement
            writer.write("</ifStatement>\n");    
        }
        else if (tokenizer.keyWord().equals("return") && tokenizer.tokenType().equals("KEYWORD")) 
        {
            writer.write("<returnStatement>\n");
            compileReturn(); // to implement
            writer.write("</returnStatement>\n");    
        }
        tokenizer.advance();
        compileStatements(); // If there are any more statements
    }


    public void comlipeDo() throws IOException{
        if (tokenizer.keyWord().equals("do")) {
            writer.write("<keyword> do </keyword>\n");
        }

        // Calling compileCall():
        compileCall(); // to impelemnt
        tokenizer.advance();
        writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
    }

    public void compileCall() throws IOException{
        tokenizer.advance();
        writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
        tokenizer.advance();
            if ((tokenizer.tokenType().equals("SYMBOL")) && (tokenizer.symbol() == '.')) {
                writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                tokenizer.advance();
                writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
                tokenizer.advance();
                writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                writer.write("<expressionList>\n");
                compileExpressionList(); // to implement
                writer.write("</expressionList>\n");
                tokenizer.advance();
                writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
            }
            else if ((tokenizer.tokenType().equals("SYMBOL")) && (tokenizer.symbol() == '(')) {
                writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                writer.write("<expressionList>\n");
                compileExpressionList(); // to implement
                writer.write("</expressionList>\n");
                tokenizer.advance();
                writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
            }
    }

    public void compileLet() throws IOException{
        writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>\n");
        tokenizer.advance();
            writer.write("<identifier> " + tokenizer.identifier() + " </identifier>\n");
            tokenizer.advance();
            if ((tokenizer.tokenType().equals("SYMBOL")) && (tokenizer.symbol() == '[')) {
                writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                compileExpression(); // to implement
                tokenizer.advance();
                if ((tokenizer.tokenType().equals("SYMBOL")) && ((tokenizer.symbol() == ']'))) {
                    writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                }
                tokenizer.advance();
            }
            writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
            compileExpression();
            writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
            tokenizer.advance();
    }

    public void compileWhile() throws IOException{
        writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>\n");
        tokenizer.advance();
        writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
        compileExpression();
        tokenizer.advance();
        writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
        tokenizer.advance();
        writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
        writer.write("<statements>\n");
        compileStatements();
        writer.write("</statements>\n");
        writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n"); 
    }

    public void compileReturn() throws IOException{
        writer.write("<keyword> return </keyword>\n");
        tokenizer.advance();
            if (!((tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ';'))) // "end"
            {
                tokenizer.decrementPointer();
                compileExpression();
            }
            if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ';') 
            {
                writer.write("<symbol> ; </symbol>\n");
            }
    }

    public void compileIf() throws IOException{
        writer.write("<keyword> if </keyword>\n");
        tokenizer.advance();
            writer.write("<symbol> ( </symbol>\n");
            
            compileExpression();
            writer.write("<symbol> ) </symbol>\n");
            tokenizer.advance();
            writer.write("<symbol> { </symbol>\n");
            tokenizer.advance();
            writer.write("<statements>\n");
            
            compileStatements();
            writer.write("</statements>\n");
            writer.write("<symbol> } </symbol>\n");
            tokenizer.advance();
            
            
            if (tokenizer.tokenType().equals("KEYWORD") && tokenizer.keyWord().equals("else")) {
                writer.write("<keyword> else </keyword>\n");
                tokenizer.advance();
                writer.write("<symbol> { </symbol>\n");
                tokenizer.advance();
                writer.write("<statements>\n");
                compileStatements();
                writer.write("</statements>\n");
                writer.write("<symbol> } </symbol>\n");
            } 
            else {
                tokenizer.decrementPointer(); // "going bcack"
            }
    }

    public void compileExpression() throws IOException{
        writer.write("<expression>\n");
        compileTerm(); // to implement
        while (true) {
            tokenizer.advance();
            if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.isOp()) {
                if (tokenizer.symbol() == '<') {
                    writer.write("<symbol> &lt; </symbol>\n");
                } 
                else if (tokenizer.symbol() == '>') {
                    writer.write("<symbol> &gt; </symbol>\n");
                } 
                else if (tokenizer.symbol() == '&') {
                    writer.write("<symbol> &amp; </symbol>\n");
                } 
                else {
                    writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                }
                compileTerm();
            } 
            else {
                tokenizer.decrementPointer();
                break;
            }
        }
        writer.write("</expression>\n"); 
    }

    public void compileTerm() throws IOException{
        writer.write("<term>\n");
        tokenizer.advance();
        if (tokenizer.tokenType().equals("IDENTIFIER")) {
            String prev = tokenizer.identifier();
            tokenizer.advance();
            if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == '[') {
                writer.write("<identifier> " + prev + " </identifier>\n");
                writer.write("<symbol> [ </symbol>\n");
                compileExpression();
                tokenizer.advance();
                writer.write("<symbol> ] </symbol>\n");
            }
            else if (tokenizer.tokenType().equals("SYMBOL") && (tokenizer.symbol() == '(' || tokenizer.symbol() == '.')) {
                tokenizer.decrementPointer();
                tokenizer.decrementPointer();
                compileCall();
            } 
            else {
                writer.write("<identifier> " + prev + " </identifier>\n");
                tokenizer.decrementPointer();
            }
        } 
        else {
            if (tokenizer.tokenType().equals("INT_CONST")) {
                writer.write("<integerConstant> " + tokenizer.intVal() + " </integerConstant>\n");
            }
            else if (tokenizer.tokenType().equals("STRING_CONST")) {
                writer.write("<stringConstant> " + tokenizer.stringVal() + " </stringConstant>\n");
            }
            else if (tokenizer.tokenType().equals("KEYWORD") && (tokenizer.keyWord().equals("this")
             || tokenizer.keyWord().equals("null")
             || tokenizer.keyWord().equals("false") 
             || tokenizer.keyWord().equals("true"))) {
                writer.write("<keyword> " + tokenizer.keyWord() + " </keyword>\n");
            }
            else if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == '(') {
                writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                compileExpression();
                tokenizer.advance();
                writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
            }
            else if (tokenizer.tokenType().equals("SYMBOL") && (tokenizer.symbol() == '-' || tokenizer.symbol() == '~')) {
                writer.write("<symbol> " + tokenizer.symbol() + " </symbol>\n");
                compileTerm();
            }
        }
        writer.write("</term>\n"); 
    }


    public void compileExpressionList() throws IOException{
        tokenizer.advance();
        if (tokenizer.symbol() == ')' && tokenizer.tokenType().equals("SYMBOL")) {
            tokenizer.decrementPointer();
        } 
        else {
            tokenizer.decrementPointer();
            compileExpression();
        }
        while (true) {
            tokenizer.advance();
            if (tokenizer.tokenType().equals("SYMBOL") && tokenizer.symbol() == ',') {
                try {
                    writer.write("<symbol> , </symbol>\n");
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
                compileExpression();
            } 
            else {
                tokenizer.decrementPointer();
                break;
            }
        }
    }





















    public static void main(String[] args) throws IOException {
        // Test case: Create a simple .jack file and a corresponding output file
        try{
        File inputFile = new File("/Users/yossipeleg/Documents/Nand2Tetris/project10/test.jack");
        File outputFile = new File("output.xml");

        CompilationEngine engine = new CompilationEngine(inputFile, outputFile);
        

        System.out.println("Testing compileClass... ");
        engine.compileClass();
        System.out.println("Review output!");
        }
        catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }


    }







    









}
