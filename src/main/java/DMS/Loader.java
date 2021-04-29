package DMS;

import java.util.Date;

public class Loader {
    private int id;
    private String eid;
    private String subject;
    private String from;
    private String to;
    private String body;
    private Date date;
    private String name;
    private String type;

    public Loader(int id,String eid,String subject, String from, String to, String body, Date date, String name,  String type) {
        this.id = id;
        this.eid = eid;
        this.subject = subject;
        this.from = from;
        this.to = to;
        this.body = body;
        this.date = date;
        this.name = name;
        this.type = type;
    }

    public String getEid() {
        return eid;
    }

    public int getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getBody() {
        return body;
    }

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
