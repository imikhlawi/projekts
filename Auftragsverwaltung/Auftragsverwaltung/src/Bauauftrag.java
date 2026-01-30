
public class Bauauftrag {
    private int id;
    private String auftraggeber;
    private String ort;
    private String adresse;
    private String beschreibung;
    private String startdatum;
    private String enddatum;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuftraggeber() {
        return auftraggeber;
    }

    public void setAuftraggeber(String auftrageeber) {
        this.auftraggeber = auftrageeber;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getStartdatum() {
        return startdatum;
    }

    public void setStartdatum(String startdatum) {
        this.startdatum = startdatum;
    }

    public String getEnddatum() {
        return enddatum;
    }

    public void setEnddatum(String enddatum) {
        this.enddatum = enddatum;
    }

    @Override
    public String toString()
    {
        return this.getAuftraggeber() + " - " + this.getBeschreibung();
    }
}
