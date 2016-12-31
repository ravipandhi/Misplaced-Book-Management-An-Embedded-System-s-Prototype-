/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author Ravi
 */
public class RequestingWindow implements Initializable {

    @FXML
    private Label label;

    @FXML
    TextField name = new TextField();
    @FXML
    TextField author = new TextField();
    @FXML
    TextField publication = new TextField();
    private ObservableList<ObservableList> data;
    private TableView tableview;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    public void buildData(ResultSet rs){
          Connection c = null ;
          Statement st = null;
          data = FXCollections.observableArrayList();
          try{
      
            /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {                                                                                              
                        return new SimpleStringProperty(param.getValue().get(j).toString());                        
                    }                    
                });
               
                tableview.getColumns().addAll(col); 
                System.out.println("Column ["+i+"] ");
            }

            /********************************
             * Data added to ObservableList *
             ********************************/
            while(rs.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added "+row );
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            tableview.setItems(data);
          }catch(Exception e){
              e.printStackTrace();
              System.out.println("Error on Building Data");             
          }
      }
    public void getgui(ResultSet rs)
    {
        tableview = new TableView();
        buildData(rs);
        Stage stage = new Stage();
        Scene scene = new Scene(tableview);        

        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void locateButtonPressed(ActionEvent event) throws IOException, SQLException {
        
        boolean nameIsEmpty = name.getText().isEmpty();
        boolean authorIsEmpty = author.getText().isEmpty();
        boolean PublicationIsEmpty = publication.getText().isEmpty();
        String nameEntered = name.getText();
        String authorEntered = author.getText();
        String publicationEntered = publication.getText();
        ObservableList<ObservableList> data = null;
        TableView tableview = null;
        
        if (nameIsEmpty && authorIsEmpty && PublicationIsEmpty) {

            Parent root1 = FXMLLoader.load(getClass().getResource("NoFieldsEntered.fxml"));
            Scene EmptyFieldsscene = new Scene(root1);
            Stage EmptyFieldsStage = new Stage();
            EmptyFieldsStage.setScene(EmptyFieldsscene);
            EmptyFieldsStage.show();

        } else {

            
            DatabaseHandler Callee = new DatabaseHandler();
            Callee.ConnectJDBC();

            if (!nameIsEmpty) {
                if (!authorIsEmpty) {
                    if (!PublicationIsEmpty) {
                        ResultSet MatchingData = Callee.RetrieveFromDatabase(nameEntered, authorEntered, publicationEntered);
                        System.out.println("Three fields entered");
               
                        getgui(MatchingData);
                  
                    } else {
                        ResultSet MatchingData = Callee.RetrieveFromDatabase("Name", nameEntered, "Author", authorEntered);
                        System.out.println("Name and Author entered");
                     
                        getgui(MatchingData);
                    }
                } else {
                    if (!PublicationIsEmpty) {
                        ResultSet MatchingData = Callee.RetrieveFromDatabase("Name", nameEntered, "Publication", publicationEntered);
                       System.out.println("name and publication entered");
                       getgui(MatchingData);
                   
                    } else {
                        ResultSet MatchingData = Callee.RetrieveFromDatabase("Name", nameEntered);
                        System.out.println("Name entered");
                        getgui(MatchingData);
                    }
                }

            } else {
                if (!authorIsEmpty) {
                    if (!PublicationIsEmpty) {
                        System.out.println("author and publication entered");
                        ResultSet MatchingData = Callee.RetrieveFromDatabase("Author", authorEntered, "Publication", publicationEntered);
                        getgui(MatchingData);
                    } else {
                        ResultSet MatchingData = Callee.RetrieveFromDatabase("Author", authorEntered);
                        System.out.println("Author entered");
                        getgui(MatchingData);
                      
                    }
                } else {
                    if (!PublicationIsEmpty) {
                        ResultSet MatchingData = Callee.RetrieveFromDatabase("Publication", publicationEntered);
                        System.out.println("Publication enterrd");
                        getgui(MatchingData);
                     
                    }
                }
            }
            Callee.CloseConnection();
        }
    }

}
