
public class Mitarbeiter {
    private int id;
    private String name;
    private String beruf;
    private String einstellungsdatum;
    private String jahresgehalt;
    private String auftraege;

    private String bildBase64;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return (name == "null") ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeruf() {
        return (beruf == "null") ? "" : beruf;
    }

    public void setBeruf(String beruf) {
        this.beruf = beruf;
    }

    public String getEinstellungsdatum() {
        return (einstellungsdatum == "null") ? "" : einstellungsdatum;
    }

    public void setEinstellungsdatum(String einstellungsdatum) {
        this.einstellungsdatum = einstellungsdatum;
    }

    public String getJahresgehalt() {
       return  (jahresgehalt == "null") ? "" : jahresgehalt;
    }

    public void setJahresgehalt(String jahresgehalt) {
        this.jahresgehalt = jahresgehalt;
    }

    public String getAuftraege() {
        return (auftraege == "null") ? "" : auftraege;
    }

    public void setAuftraege(String auftraege) {
        this.auftraege = auftraege;
    }

    public String getBildBase64() {
        return bildBase64;
    }

    public void setBildBase64(String bildBase64) {
        this.bildBase64 = bildBase64;
    }
}
