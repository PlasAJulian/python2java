import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class builder {
    static List<String> fileList = new ArrayList<String>();
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

        for (String i:fileList) {
            System.out.println(i);
        }
    }
}
