import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class builder {
    static List<String> fileList = new ArrayList<String>();
    static List<String> JavaBuilt = new ArrayList<String>();
    static List<String> vars = new ArrayList<String>();
    static String grammer;
    static String fileName;
    public void getFile() throws IOException {
        Path grammerFile = Path.of("src/Files/grammer");
        grammer = Files.readString(grammerFile);

        String InFile = "src/Files/for-loop-sum.py";

        //gets ready to take in the file.
        ////String FileDir = "src/Files/";
        ////Scanner myObj = new Scanner(System.in);
        ////System.out.println("Please type math-operations.py, for-loop-sum.py, or area-Triangle.py");

        //////removes letter case.
        ////String inputFile = myObj.nextLine();
        ////inputFile = inputFile.toLowerCase();

        //////loop to check if file user entered exists or not.
        ////File InFile = new File(FileDir+inputFile);
        ////while(!InFile.exists()){
        ////    System.out.println("The file '" + inputFile + "' does not exists. Please try again.");
        ////    System.out.println("Please type math-operations.py, for-loop-sum.py, or area-Triangle.py");

        ////    inputFile = myObj.nextLine();
        ////    inputFile = inputFile.toLowerCase();

        ////    InFile = new File(FileDir+inputFile);
        ////}

        //reads the file and puts in in array list. each line in the code is an entry in the that array list.
        BufferedReader reader = new BufferedReader(new FileReader(InFile));
        String line;
        while ((line = reader.readLine()) != null) {
            fileList.add(line);
        }
        reader.close();

        //gets the file name from the input
        fileName = InFile.substring(0, InFile.length() - 3); //only this will be used and instead of infile it will use inputFile
        fileName = fileName.substring(10, fileName.length());

        //removes comments and empty lines
        fileList = clean(fileList);
        fileList.removeAll(Arrays.asList("", null));

        build();

        //System.out.println("~~~~~~~~~~");
        //Testt(fileList.get(0));
    }
    //removes comments and returns a cleaned file.
    public List<String> clean(List<String> list){
        for (String line: list) {
            if(line.contains("#")){
                int index = list.indexOf(line);
                line = line.split("#")[0];
                list.set(index, line);
            }
        }
        return list;
    }

    public void build(){
        int openCount = 0;
        JavaBuilt.add("public class "+ fileName +" {");
        openCount = openCount +1;
        JavaBuilt.add("public static void main(String[] args){");
        openCount = openCount +1;
        for (String line:fileList) {
            if(line.contains("input")){
                buildInput(line);
                if(!JavaBuilt.contains("myObj.close();")){
                    JavaBuilt.add("myObj.close();");
                }
                else if(JavaBuilt.contains("myObj.close();")){
                    int r = JavaBuilt.indexOf("myObj.close();");
                    JavaBuilt.remove(r);
                    JavaBuilt.add("myObj.close();");
                }
                continue;
            }
            else if(line.contains("for")&&line.contains("range")){
                //check the spacing of the next line
                //buildForLoop();
                JavaBuilt.add(line);
            }
            else if(line.contains("print")){
                addPrint(line);
                continue;
            }
            else{
                JavaBuilt.add("this is has NOT BEEN CHECK");
                JavaBuilt.add(line);
                //checkVar(line);

            }

        }

        //closes any brackets that were left opened
        for(int i =0; i<openCount; i++){
            JavaBuilt.add("}");
        }

    }

    //build the for loop when needed
    public void buildForLoop(){
        int openCount = 0;
    }

    //builds input when needed
    public void buildInput(String line){
        String imp = "import java.util.Scanner;";
        String scan ="Scanner myObj = new Scanner(System.in);";
        String variable = "";
        String type;
        String outMessage;

        if(!JavaBuilt.contains(imp)) {
            JavaBuilt.add(0, imp);
        }
        if(!JavaBuilt.contains(scan)){
            JavaBuilt.add(scan);
        }
        String[] splited = line.split("(?<=[-+*/=()])|(?=[-+*/=()])");
        if(splited[1].equals("=")) {
            variable = splited[0];
            vars.add(variable);
        }
        if(splited[3].equals("input")){
            type = "String";
            vars.add(type);
        }
        else {
            type = splited[2];
        }
        int index = Arrays.asList(splited).indexOf("input");
        outMessage = splited[index+2];
        outMessage = outMessage.replaceAll("'", "\"");

        JavaBuilt.add("System.out.println("+outMessage+");");

        String typeUpper = type.substring(0, 1).toUpperCase() + type.substring(1);
        JavaBuilt.add(type + " " + variable +" = myObj.next"+typeUpper+ "();");
        vars.add(type);
    }

    //build print when needed
    public void addPrint(String line){
        String[] splited = line.split("(?<=[-+*/=()])|(?=[-+*/=()])");
        String printString = "System.out.println(";
        for (String word: splited) {
            word = word.trim();
            if(word.equals("print") || word.equals("(")){
                continue;
            }
            else if(word.indexOf('\'') >= 0){
                word = word.replaceAll("'", "\"");
                printString = printString +  word;
                continue;
            }
            else if(word.equals(")")){
                break;
            }
            else {
                printString = printString + " " + word;
            }
        }
        printString = printString + ");";
        JavaBuilt.add(printString);
    }
    //adds variable and check if its a new or already declared.
    public void checkVar(String line){
        String[] splited = line.split("(?<=[-+*/=()])|(?=[-+*/=()])");
        for (String word:splited) {

        }
        //JavaBuilt.add(line);

    }
    //displays both the original and the translated file
    public void display(){
        System.out.println("~The following is the original file. \n");
        for (String line:fileList) {
            System.out.println(line);
        }
        System.out.println("\n~The following is the translate file.\n");
        for (String line:JavaBuilt) {
            System.out.println(line);
        }
    }

    public void Testt(String line){
        String[] splited = line.split("(?<=[-+*/=()])|(?=[-+*/=()])");
        for (String word: splited) {
            System.out.println(word);
        }
        //for (String i: vars){
        //    System.out.println(i);
        //}
    }
}