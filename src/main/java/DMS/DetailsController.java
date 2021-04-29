package DMS;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class DetailsController implements Initializable {
    @FXML
    private TextArea dfilecontent;
    @FXML
    private Label dsubject;
    @FXML
    private Label dfrom;
    @FXML
    private Label ddate;
    @FXML
    private Label dfilename;

    String email_id;
    Connection conn;
    Statement stm = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conn = DMSUIController.getConnection();
        dfilecontent.setEditable(false);
        email_id = DMSUIController.getEid();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT attachment.id, email.eid, email.from, email.subject, email.date, attachment.name, attachment.contant, attachment.type, attachment.email_id FROM email INNER JOIN attachment ON email.eid = attachment.email_id WHERE email.eid = '" + email_id + "';");
            while (rs.next()) {
                dsubject.setText(dsubject.getText() + rs.getString("subject"));
                dfrom.setText(dfrom.getText() + rs.getString("from"));
                ddate.setText(ddate.getText() + rs.getDate("date"));
                dfilename.setText(dfilename.getText() + rs.getString("name"));
                dfilecontent.setText(rs.getString("contant"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    private void downloadFile(ActionEvent actionEvent) throws SQLException, IOException {
        if (conn != null) {
            String query = "SELECT name, file FROM attachment WHERE email_id = '" + email_id + "'";
            stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(query);
            InputStream in = null;
            Blob blob = null;
            if (rs.next()){
                blob = rs.getBlob("file");
            }
            in = blob.getBinaryStream();
            int available = in.available();
            byte[] bt = new byte[available];
            in.read(bt);

            FileOutputStream fout = new FileOutputStream("C:\\Users\\LENOVO\\Downloads\\"+rs.getString("name"));
            DataOutputStream dout = new DataOutputStream(fout);
            dout.write(bt,0,bt.length);
            fout.close();

        }
    }
}
