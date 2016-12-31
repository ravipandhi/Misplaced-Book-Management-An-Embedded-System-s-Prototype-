
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class FileController {

    private String path;
    private boolean append_to_file = false;

    public FileController(String file_path) {
        path = file_path;

    }
    public void writeToFile(String name, String CurrentPosition, String CorrectPosition) throws IOException {

        File idea = new File(path);  //
        Boolean flag = false;
        if (!idea.exists()) {                //File does not exist, and a new one has to be created
            System.out.println("File does not exist");
            append_to_file = false;
            flag = true;
        } else {
            System.out.println("File exists");
            append_to_file = true;
        }
        
        System.out.println(path);
        System.out.println(append_to_file);
        FileWriter Filewrite = new FileWriter(path, append_to_file);  // stream of characters to file
        PrintWriter print_line = new PrintWriter(Filewrite);   //  writes formated strings to output stream

        if (flag == true) {
            System.out.println("Setting the layout");
            print_line.print("Misplaced Records:");
            print_line.println();
            print_line.print("Name                          ");  //space required for neatness
            print_line.print("Current Position              ");  
            print_line.print("Correct Position");
            print_line.println();
            System.out.println("One record inserted");
        }
        
        System.out.println("Inserting Records Now");
        int nameLength = name.length();
        int CurrentPositionLength = CurrentPosition.length();
        int CorrectPositionLength = CorrectPosition.length();
        print_line.print(name);
        int NumberOfSpaces = 30 - nameLength;
        String format = String.format("%" + NumberOfSpaces + "s", "");
        print_line.print(format);
        print_line.print(CurrentPosition);
        NumberOfSpaces = 30 - CurrentPositionLength;
        format = String.format("%" + NumberOfSpaces + "s", "");  //Inserting the number of spaces reqired
        print_line.print(format);
        print_line.print(CorrectPosition);
        print_line.println();
        System.out.println("Record Inserted");
        System.out.println(""+name+","+CurrentPosition+","+CorrectPosition);
        print_line.close();
        Filewrite.close();
      
        
    }
}
