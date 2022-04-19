import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;



public class builder {
    static List<String> fileList = new ArrayList<String>();
    static List<String> JavaBuilt = new ArrayList<String>();
    static List<String> vars = new ArrayList<String>();
    static List<String> forLoop = new ArrayList<String>();
    static String fileName;

    public void getFile() throws IOException {
        //gets ready to take in the file.
        String FileDir = "src/Files/";
        Scanner myObj = new Scanner(System.in);
        System.out.println("Please type math-operations.py, for-loop-sum.py, or area-Triangle.py");

        //removes letter case.
        String inputFile = myObj.nextLine();
        inputFile = inputFile.toLowerCase();

        //loop to check if file user entered exists or not.
        File InFile = new File(FileDir+inputFile);
        while(!InFile.exists()){
            System.out.println("The file '" + inputFile + "' does not exists. Please try again.");
            System.out.println("Please type math-operations.py, for-loop-sum.py, or area-Triangle.py");

            inputFile = myObj.nextLine();
            inputFile = inputFile.toLowerCase();

            InFile = new File(FileDir+inputFile);
        }

        //reads the file and puts in in array list. each line in the code is an entry in the that array list.
        BufferedReader reader = new BufferedReader(new FileReader(InFile));
        String line;
        while ((line = reader.readLine()) != null) {
            fileList.add(line);
        }
        reader.close();

        //gets the file name from the input
        fileName = inputFile.substring(0, inputFile.length() - 3);

        //removes comments and empty lines
        fileList = clean(fileList);
        fileList.removeAll(Arrays.asList("", null));

        build();
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

    //the main builder
    public void build(){
        //some basic variables to keep track of some items.
        int openCount = 0;
        int spaceCount = 0;
        boolean hasFor = false;
        int forIndex = 0;

        //creates the file name and main for the function
        JavaBuilt.add("public class "+ fileName +" {");
        openCount = openCount +1;
        JavaBuilt.add("public static void main(String[] args){");
        openCount = openCount +1;

        //begin looping threw each line in the input file.
        for (String line:fileList) {
            //check the number of spaces before the first letter. This help with what is inside or out side of the loop.
            for (char c : line.toCharArray()) {
                if (c == ' ') {
                    spaceCount++;
                }
                else {
                    break;
                }
            }
            //if it has a higher space count then it it added to the loop list.
            if(spaceCount > 0){
                forLoop.add(line.trim());
                continue;
            }
            //calls build input method if an input was used.
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
            //makes which line is the start of the for loop and waits until all lines are checked.
            else if(line.contains("for") && line.contains("range")){
                hasFor = true;
                forIndex = fileList.indexOf(line);
            }
            //calls build for print method if an print was used.
            else if(line.contains("print")){
                addPrint(line);
                continue;
            }
            //checks if the other times are variables and creates them.
            else{
                checkVar(line);
            }
        }
        //calls build for loop method if an loop with a range was used.
        if(hasFor){
            String loop = fileList.get(forIndex);
            buildForLoop(loop);
            JavaBuilt.add("}");
        }
        //closes any brackets that were left opened
        for(int i =0; i<openCount; i++){
            JavaBuilt.add("}");
        }
    }

    //build the for loop when needed
    public void buildForLoop(String line){
        String[] splited = line.split("\\s+");
        String startValue = splited[1];
        String range = "";

        //find at what point does the loop end.
        for (String subWord:splited) {
            if(subWord.contains("range")) {
                subWord = subWord.replaceAll("[^a-zA-Z0-9\\s+]", " ");
                String[] rangeWord = subWord.split("\\s+");
                range = rangeWord[1];
            }
        }
        //added the loop to tranlated list
        JavaBuilt.add("for(int "+startValue +"=0;"+startValue+"<"+range+";"+startValue+"++){");
        //loops threw the items in the forLoop list and builds those items.
        for (String Subline:forLoop) {
            if(Subline.contains("print")){
                addPrint(Subline);
                continue;
            }
            else{
                checkVar(Subline);
            }
        }
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
            vars.add(variable.trim());
        }
        if(splited[3].equals("input")){
            type = "String";
            vars.add(type);
        }
        else {
            type = splited[2].trim();
        }
        int index = Arrays.asList(splited).indexOf("input");
        outMessage = splited[index+2];
        outMessage = outMessage.replaceAll("'", "\"");

        JavaBuilt.add("System.out.println("+outMessage+");");

        String typeUpper = type.substring(0, 1).toUpperCase() + type.substring(1);
        JavaBuilt.add(type + " " + variable +" = myObj.next"+typeUpper+ "();");
        vars.add(type.trim());
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
        String type = "";
        String rest = "";
        String[] splited = line.split("\\s+");
        if(!vars.contains(splited[0])){
            String check = splited[2];
            check = check.replaceAll("[^a-zA-Z0-9\\s+]", "");
            if(vars.contains(check)){
                int index = vars.indexOf(check);
                type = vars.get(index+1);
                vars.add(splited[0]);
                vars.add(type);
                for(int i = 1; i<splited.length; i++){
                    rest = rest +" "+splited[i];
                }
            }
            else{
                if(isNumeric(splited[2])){
                    type = "float";
                    for(int i = 1; i<splited.length; i++){
                        rest = rest +" "+splited[i];
                    }
                    vars.add(splited[0].trim());
                    vars.add(type);
                }
            }
        }
        else{
            for(int i = 1; i<splited.length; i++){
                rest = rest +" "+splited[i];
            }
        }
        JavaBuilt.add(type +" "+splited[0] +" "+rest+";");
    }
    //used to check if the variable is a string or number.
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            float d = Float.parseFloat(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
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
}