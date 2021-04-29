package DMS;
//importing the needed google api libraries
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
//importing the apache poi and pdfbox libraries
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
//importing the needed java libraries
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Emails {

    private static String nomore = "";

    //prints the id and the content of all messages in the inbox
    public static void getEmails(Gmail service, String user){
        try{
            ListMessagesResponse response = service.users().messages().list(user).execute();
            List<Message> messages = new ArrayList<Message>();
            if(response.getMessages() != null){
                while (response.getMessages() != null) {
                    messages.addAll(response.getMessages());
                    if (response.getNextPageToken() != null) {
                        String pageToken = response.getNextPageToken();
                        response = service.users().messages().list(user).setPageToken(pageToken).execute();
                    } else {
                        break;
                    }
                }
            }else{
                nomore = "There is no emails left to download.";
            }


            // the body did not came throw
            if(messages.size() != 0){
                for (Message message : messages) {
                    Connection conn = DMSUIController.getConnection();
                    String msgid = message.getId();
                    Message msg = service.users().messages().get(user, msgid).execute();
                    String body = msg.getSnippet();
                    String sender = msg.getPayload().getHeaders().get(6).getValue();
                    if (sender.length()>200){
                        int index_of_first = sender.indexOf(" of ")+4;
                        sender = sender.substring(index_of_first,sender.length());
                        int index_of_last = sender.indexOf(" ");
                        sender = sender.substring(0,index_of_last);
                    }
                    MessagePart messagePart = msg.getPayload();
                    String subject = null;
                    String filename = null;
                    String att_text = null;
                    String type = null;
                    String attId = null;
                    String receiver = null;
                    String dir = null;
                    //getting the date of the email
                    long internaldate = msg.getInternalDate();
                    Date email_date = new Date(internaldate);
                    DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                    String stringdate = df.format(email_date);
                    //ends here
                    if (messagePart != null) {
                        List<MessagePartHeader> headers = messagePart.getHeaders();
                        for (MessagePartHeader header : headers) {
                            //find the subject header.
                            if (header.getName().equals("To")) {
                                receiver = header.getValue().trim();
                                break;
                            }
                        }
                        for (MessagePartHeader header : headers) {
                            //find the subject header.
                            if (header.getName().equals("Subject")) {
                                subject = header.getValue().trim();
                                break;
                            }
                        }
                        if (subject.contains("Security alert") || subject.contains("Google") || subject.contains("Account")){
                            service.users().messages().delete(user,msgid).execute();
                        }
                        else{
                            List<MessagePart> parts = messagePart.getParts();
                            for (MessagePart part : parts){
                                if ((part.getFilename() != null && part.getFilename().length() > 0)) {
                                    filename = part.getFilename();
                                    attId = part.getBody().getAttachmentId();
                                    MessagePartBody attachPart;
                                    FileOutputStream fileOutFile;
                                    attachPart = service.users().messages().attachments().get(user, part.getPartId(), attId).execute();
                                    byte[] fileByteArray = Base64.decodeBase64(attachPart.getData());
                                    dir = "F:\\20202\\DMS\\~\\emails\\"+filename;
                                    File files = new File(dir);
                                    if (files.exists()){
                                        System.out.print("");

                                    }else{
                                        fileOutFile = new FileOutputStream(dir);
                                        fileOutFile.write(fileByteArray);
                                        fileOutFile.close();
                                    }
                                    if(dir.contains(".docx")){
                                        XWPFDocument docx = new XWPFDocument(new FileInputStream(dir));
                                        XWPFWordExtractor textExtractor = new XWPFWordExtractor(docx);
                                        att_text = textExtractor.getText();
                                        type = "Word File";
                                    }
                                    else if (dir.contains(".png") ||
                                            dir.contains(".PNG") ||
                                            dir.contains(".jpeg") ||
                                            dir.contains(".JPEG")||
                                            dir.contains(".jpg") ||
                                            dir.contains(".JPG")){
                                        type = "Image File";
                                    }
                                    else if(dir.contains(".pdf")||dir.contains(".PDF")){
                                        PDDocument document = PDDocument.load(new FileInputStream(dir));
                                        PDFTextStripper pdfStripper = new PDFTextStripper();
                                        att_text = pdfStripper.getText(document);
                                        type = "PDF File";
                                        document.close();
                                    }
                                    insert_email(msgid,sender,receiver,subject,body,stringdate);


                                    String query = "INSERT INTO attachment(name,contant,file,type,email_id) VALUES (?,?,?,?,?)";
                                    PreparedStatement pstmt = conn.prepareStatement(query);
                                    FileInputStream fis = new FileInputStream(dir);
                                    pstmt.setString(1,filename);
                                    pstmt.setString(2,att_text);
                                    pstmt.setBinaryStream(3,fis);
                                    pstmt.setString(4,type);
                                    pstmt.setString(5,msgid);
                                    pstmt.execute();
                                    conn.close();
                                    files.delete();
                                    break;

                                }else{
                                    insert_email(msgid,sender,receiver,subject,body,stringdate);
                                }
                            }
                        }
                    }
                    service.users().messages().delete(user,msgid).execute();
                }
            }

        }catch(IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    //insert data in the database method
    public static void insert_email(String msgid, String sender, String receiver, String subject, String body, String date) throws SQLException {
        String insert_email_query = "INSERT INTO email VALUES ('"+msgid+"', '"+sender+"', '"+receiver+"', '"+subject+"', '"+body+"', '"+date+"');";
        Connection conn = null;
        Statement stm;
        stm = conn.createStatement();
        stm.executeUpdate(insert_email_query);
        conn.close();
    }

    public String getNoE(){
        return nomore;
    }
}
