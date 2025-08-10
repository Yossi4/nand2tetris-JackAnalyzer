import java.io.*;
import java.util.*;


// Main module that drives the whole compilation proccess and logic
public class Main {

    // No need for constructor


    //method to get all .jack files with a given directory path:
    public static ArrayList<File> getJackFilesArray (File dir)
    {
        File [] jackJiles = dir.listFiles();
        ArrayList<File> out = new ArrayList<>();
        if (jackJiles == null) // meaning no .jack files in the supplied directiry
        {
            return null;
        }        
        for (File file : jackJiles) {
            if (file.getName().endsWith(".jack")) 
            {
                out.add(file);    
            }
        }
        return out;
    }


    // handling the main logic in void main:
    public static void main(String[] args) throws IOException {
        if (args.length != 1){
            System.out.println("Please provide a jack file or a jack directory.");
        }
        else {
            String fileInName = args[0];
            File input = new File(fileInName);
            String fileOutPath = "";
            File out;
            ArrayList<File> jackFiles = new ArrayList<File>();
            if (input.isFile()) {
                String path = input.getAbsolutePath();
                if (!path.endsWith(".jack")) {
                    throw new IllegalArgumentException(".jack file is required!");
                }
                jackFiles.add(input);
            } 
            else if (input.isDirectory()) {
                jackFiles = getJackFilesArray(input);
                if (jackFiles.size() == 0) {
                    throw new IllegalArgumentException("No .jack files found in the supplied directory.");
                }
            }
            for (File file: jackFiles) {
                fileOutPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + ".xml";
                out = new File(fileOutPath);
                CompilationEngine compilationEngine = new CompilationEngine(file,out);
                compilationEngine.compileClass();
            }
        }
    }



}
