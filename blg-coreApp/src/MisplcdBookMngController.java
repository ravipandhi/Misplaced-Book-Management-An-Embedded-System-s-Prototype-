


import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;
import com.pi4j.io.serial.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MisplcdBookMngController {

    
    static String[] EPCs = new String[5];  //Temporary Array for storing EPC codes of a particular row
    static int x = 0;  
    static DataAccessObject callee = new DataAccessObject();  //for dealing with database functions
    static String rack;  
    static String row;   //for selecting the area
    static String position;
    static int flag=0;
    static int correctposition = 0;
    final static GpioController gpio = GpioFactory.getInstance();//Creating a Readable Input button
    final static GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_UP);
   
    public static void main(String[] args) throws SQLException, IllegalStateException, IOException, InterruptedException {
    
        int i = Integer.parseInt(args[0]);  //rack no 
        int j = Integer.parseInt(args[1]);  //row no

        rack = "Rack-" + i;
        row = "Row-" + j;
        position =""+rack+","+row;    //formatting according to database
        System.out.println("Selected position is "+position);
        System.out.println("Main method started");
        ListenGPIOExample();   
    }

    public static void ScanAndStoreTemorarily() throws IllegalStateException, IOException {
        
        final Serial serial = SerialFactory.createInstance(); //creates an instance of serial implemenation
        String comPort = "/dev/ttyAMA0";    //using AMA0 port (Rx pin of Raspberry PI)
        try {
            serial.open(comPort, 9600);     //baud rate 9600
        } catch (Exception ex) {
            System.out.println("Couldn't Connect " + ex);
        }
        System.out.println("Start scanning the cards now!");
        
        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.LOW) {
                    System.out.println(" " + event.getPin() + " " + event.getState());        
                        System.out.println("Stop Scanning");
                        System.exit(0);  
                } else if (event.getState() == PinState.HIGH) {
                    System.out.println(" " + event.getPin() + " " + event.getState());
                }
            }
        });
        
        SerialDataEventListener obj= new SerialDataEventListener() {  //A listener which listens to serial port
               
            @Override   
            public void dataReceived(SerialDataEvent sde) {  
                
                byte[] data=null;
                String dat = null;  
                try{
                    data=sde.getBytes();
                    dat=new String(data);  //For conversion of byte array to string
                }catch(Exception ex){System.out.println("Data not received"+ex);}
                System.out.println(dat);                //Printing the EPC codes
                EPCs[x]=dat;                            //Assigning the value to a temporary array
                x++;           
            }
        };                                                                  
        
        serial.addListener(obj);
        
	for(;;)
        {
            System.out.println("Waiting for tag scanning");
            if(x==5)    // Stop when number of books are exhausted
            {
                serial.removeListener(obj);
		break;
            }
        }
        serial.close();    
    }

    private static void CompareWithDatabase(String[] a) throws SQLException, IOException {
        
        Boolean b;
        int j,flag = 0;
        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.LOW) {
                    System.out.println(" " + event.getPin() + " " + event.getState());
                        System.out.println("Stop Scanning");
                        System.exit(0);
                    
                } else if (event.getState() == PinState.HIGH) {
                    System.out.println(" " + event.getPin() + " " + event.getState());
                }
            }
        });
            for(j=0;j<a.length;j++)
            {    
            
                System.out.println(a[j]);
             
                System.out.println(position);
                int CheckCorrectness = callee.CheckExistence(a[j], position);  //Checking for the position correctness
                if (CheckCorrectness==0) {    
                    flag=1;
                    String CurrentPosition=""+position+",Number-"+j;    //Number indicates the position of the book in a row.
                    ResultSet NameBook=callee.RetrieveFromDatabase("Name","Position", a[j]);  //Getting the correct position of the misplaced book     
                    String BookName=NameBook.getString("Name");    
                    String CorrectBookPosition=NameBook.getString("Position");
                    FeedToFile(BookName,CurrentPosition,CorrectBookPosition,"MisplacedRecords"); //Feeding to file and giving it a name
                }
                else
                {
                    System.out.println("The record exists");
                }
            }
            if(flag==0)
            {
                System.out.println("All the books are in the correct position");
                correctposition=1;
            }
            callee.CloseConnection();
    }

    private static void FeedToFile(String BookName, String CurrentPosition, String CorrectBookPosition, String FileName) throws IOException {
        
        
        
        FileName=""+FileName+".txt";
        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
//                System.out.println("GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
                if (event.getState() == PinState.LOW) {
                    System.out.println(" " + event.getPin() + " " + event.getState());
                    
                        System.out.println("Stop Scanning");
                        System.exit(0);
                    
                } else if (event.getState() == PinState.HIGH) {
                    System.out.println(" " + event.getPin() + " " + event.getState());
                    
                }
            }

        });
        FileController fileHandle = new FileController(FileName);  //WriteFile class that handles file output
        fileHandle.writeToFile(BookName,CurrentPosition,CorrectBookPosition);    
        System.out.println("File written");
    }
    
    private static void ListenGPIOExample() throws InterruptedException {

        System.out.println("GPIO Listening started.");
        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.LOW) {
                    System.out.println(" " + event.getPin() + " " + event.getState());
                    if (flag == 0) {
                        System.out.println("Start Scanning");
                        try {
                            ScanAndStoreTemorarily();
                        } catch (Exception ex) {
                            System.out.println("Exception in scanning: "+ex);
                        } 
                        callee.ConnectJDBC();    //After scanning, the database connection is made 
                        try {
                           
                            CompareWithDatabase(EPCs);      // the temporary row array needs to be compared with database for finding misplaced books and their correct location
                            //      System.out.println("Back after all the work");
                        } catch (Exception ex) {
                            System.out.println("Exception in comparing with database method : "+ex);
                        } 

                        if (correctposition == 0) {
                            FileSender sender = new FileSender();
                            try {
                                sender.sendFile();
                            } catch (IOException ex) {
                                System.out.println("Error in sending file method: "+ex);
                            }
                        }
                        System.exit(0);
                    } else {
                        System.out.println("Scanning stopped");
                        System.exit(0);
                    }
                } else if (event.getState() == PinState.HIGH) {
                    System.out.println(" " + event.getPin() + " " + event.getState());
                    flag++;
                }
            }

        });

        System.out.println(" ... complete the GPIO #02 circuit and see the listener feedback here in the console.");

        for (;;) {
           
        }

    }


}
