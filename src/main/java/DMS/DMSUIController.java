package DMS;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;

public class DMSUIController implements Initializable {

    private static String Eid;
    @FXML
    private TableView<Loader> tv;
    @FXML
    private TableColumn<Loader,Integer> no;
    @FXML
    private TableColumn<Loader,String> subject;
    @FXML
    private TableColumn<Loader,String> sender;
    @FXML
    private TableColumn<Loader,String> recever;
    @FXML
    private TableColumn<Loader,String> body;
    @FXML
    private TableColumn<Loader, Date> date;
    @FXML
    private TableColumn<Loader,String> name;
    @FXML
    private TableColumn<Loader,String> type;
    @FXML
    private TableColumn<Loader,String> eid;
    @FXML
    private TextField txtsearch;
    @FXML
    private RadioButton rbsubject;
    @FXML
    private RadioButton rbsender;
    @FXML
    private RadioButton rbattname;
    @FXML
    private RadioButton rbattcontent;
    @FXML
    private RadioButton rbdate;
    @FXML
    private DatePicker dpdate;
    @FXML
    private RadioButton rbbody;
    @FXML
    private Label noE;
    @FXML
    private ProgressBar prbar;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            showData();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static Connection getConnection(){
        Connection conn;
        try {
            conn =DriverManager.getConnection("jdbc:mysql://localhost/DMS","root","Ali.toro1");
            return conn;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static ObservableList<Loader> getEmailList(String query) throws SQLException {
        ObservableList<Loader> emailList = FXCollections.observableArrayList();
        Connection conn = getConnection();
        Statement stm;
        ResultSet rs;

        try {
            stm = conn.createStatement();
            rs = stm.executeQuery(query);
            Loader load;

            while (rs.next()){
                load = new Loader(rs.getInt("id"),rs.getString("eid"),rs.getString("subject"),rs.getString("from"),rs.getString("to"), rs.getString("body"),rs.getDate("date"), rs.getString("name"),  rs.getString("type"));
                emailList.add(load);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        conn.close();
        return emailList;
    }

    public void dataViewPub(){
        no.setCellValueFactory(new PropertyValueFactory<Loader,Integer>("id"));
        subject.setCellValueFactory(new PropertyValueFactory<Loader,String>("subject"));
        sender.setCellValueFactory(new PropertyValueFactory<Loader,String>("from"));
        recever.setCellValueFactory(new PropertyValueFactory<Loader,String>("to"));
        body.setCellValueFactory(new PropertyValueFactory<Loader,String>("body"));
        date.setCellValueFactory(new PropertyValueFactory<Loader,Date>("date"));
        name.setCellValueFactory(new PropertyValueFactory<Loader,String>("name"));
        type.setCellValueFactory(new PropertyValueFactory<Loader,String>("type"));
        eid.setCellValueFactory(new PropertyValueFactory<Loader,String>("eid"));
    }

    public void showData() throws SQLException {
        ObservableList<Loader> emailList = getEmailList("SELECT attachment.id, email.eid, email.from, email.to, email.subject, email.body, email.date, attachment.name, attachment.type,attachment.email_id FROM email INNER JOIN attachment ON email.eid = attachment.email_id;");
        dataViewPub();
        tv.setItems(emailList);
    }

    public void LM() throws IOException, SQLException, GeneralSecurityException {
        Main mail = new Main();
        mail.emil_conn();
        showData();

        Emails e = new Emails();
        noE.setText(e.getNoE());
    }

    public void search() throws SQLException {
        ObservableList<Loader> searchList;
        String searchVal = txtsearch.getText();


        LocalDate d = dpdate.getValue();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");


        if (rbsubject.isSelected()){
            searchList = getEmailList("SELECT attachment.id, email.eid, email.from, email.to, email.subject, email.body, email.date, attachment.name, attachment.type,attachment.email_id FROM email INNER JOIN attachment ON email.eid = attachment.email_id WHERE email.subject LIKE '%"+searchVal+"%';");
            dataViewPub();
            tv.setItems(searchList);
        }
        if (rbsender.isSelected()){
            searchList = getEmailList("SELECT attachment.id, email.eid, email.from, email.to, email.subject, email.body, email.date, attachment.name, attachment.type,attachment.email_id FROM email INNER JOIN attachment ON email.eid = attachment.email_id WHERE email.from LIKE '%"+searchVal+"%';");
            dataViewPub();
            tv.setItems(searchList);
        }
        if (rbattname.isSelected()){
            searchList = getEmailList("SELECT attachment.id, email.eid, email.from, email.to, email.subject, email.body, email.date, attachment.name, attachment.type,attachment.email_id FROM email INNER JOIN attachment ON email.eid = attachment.email_id WHERE attachment.name LIKE '%"+searchVal+"%';");
            dataViewPub();
            tv.setItems(searchList);
        }
        if (rbattcontent.isSelected()){
            searchList = getEmailList("SELECT attachment.id, email.eid, email.from, email.to, email.subject, email.body, email.date, attachment.name, attachment.type,attachment.email_id FROM email INNER JOIN attachment ON email.eid = attachment.email_id WHERE attachment.contant LIKE '%"+searchVal+"%';");
            dataViewPub();
            tv.setItems(searchList);
        }
        if (rbbody.isSelected()){
            searchList = getEmailList("SELECT attachment.id, email.eid, email.from, email.to, email.subject, email.body, email.date, attachment.name, attachment.type,attachment.email_id FROM email INNER JOIN attachment ON email.eid = attachment.email_id WHERE email.body LIKE '%"+searchVal+"%';");
            dataViewPub();
            tv.setItems(searchList);
        }
        if (rbdate.isSelected()){
            searchList = getEmailList("SELECT attachment.id, email.eid, email.from, email.to, email.subject, email.body, email.date, attachment.name, attachment.type,attachment.email_id FROM email INNER JOIN attachment ON email.eid = attachment.email_id WHERE email.date LIKE '%"+d.format(dateTimeFormatter)+"%';");
            dataViewPub();
            tv.setItems(searchList);
        }
    }

    @FXML
    public void rdsubject() {
        rbattcontent.setSelected(false);
        rbattname.setSelected(false);
        rbdate.setSelected(false);
        rbsender.setSelected(false);
        rbbody.setSelected(false);
        dpdate.setVisible(false);
    }

    @FXML
    private void rdsender() {
        rbattcontent.setSelected(false);
        rbattname.setSelected(false);
        rbdate.setSelected(false);
        rbsubject.setSelected(false);
        rbbody.setSelected(false);
        dpdate.setVisible(false);
    }

    @FXML
    private void rdattname() {
        rbattcontent.setSelected(false);
        rbsubject.setSelected(false);
        rbdate.setSelected(false);
        rbsender.setSelected(false);
        rbbody.setSelected(false);
        dpdate.setVisible(false);
    }

    @FXML
    private void rdattcontent() {
        rbsubject.setSelected(false);
        rbattname.setSelected(false);
        rbdate.setSelected(false);
        rbsender.setSelected(false);
        rbbody.setSelected(false);
        dpdate.setVisible(false);
    }

    @FXML
    private void rdbody() {
        rbsubject.setSelected(false);
        rbattname.setSelected(false);
        rbdate.setSelected(false);
        rbsender.setSelected(false);
        rbattcontent.setSelected(false);
        dpdate.setVisible(false);
    }

    @FXML
    private void rddate() {
        rbattcontent.setSelected(false);
        rbattname.setSelected(false);
        rbsubject.setSelected(false);
        rbsender.setSelected(false);
        dpdate.setVisible(true);
        rbbody.setSelected(false);
    }

    public void doubleClick(){
        tv.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Loader loader;
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    loader = tv.getSelectionModel().getSelectedItem();
                    Eid = loader.getEid();
                    Node node = ((Node) event.getTarget()).getParent();
                    TableRow row;
                    if (node instanceof TableRow) {
                        row = (TableRow) node;
                    } else {
                        // clicking on text part
                        try {
                            allData();
                        } catch (IOException | SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });
    }

    public static String getEid(){
        return Eid;
    }

    public void allData() throws IOException, SQLException {
        Parent root = FXMLLoader.load(getClass().getResource("/details.fxml"));


        Scene details = new Scene(root);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Document Management System");
        stage.setScene(details);

        stage.show();


    }
}
