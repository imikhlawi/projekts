import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;


public class BauauftragModel extends AbstractTableModel {

    private String[] spaltenNamen = { "Auftraggeber", "Ort", "Adresse", "Beschreibung", "Startdatum", "Enddatum" };
    private List<Bauauftrag> auftragListe;

    public BauauftragModel(List<Bauauftrag> auftragListe) {
        this.auftragListe = auftragListe;
    }


    public List<Bauauftrag> getItems()
    {
        return auftragListe;
    }

    public boolean isCellEditable(int row, int col) {
        return true;
    }

    public void setValueAt(Object value, int row, int col) {
        final String DATE_FORMAT = "dd.MM.yyyy";
        var val = (String)value;
        switch(col)
        {
            case 0:
                if(val.length() == 0)
                {
                    JOptionPane.showMessageDialog(null, "Auftraggeber darf nicht null sein", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                auftragListe.get(row).setAuftraggeber(val);
                break;
            case 1:
                if(val.length() == 0)
                {
                    JOptionPane.showMessageDialog(null, "Ort darf nicht null sein", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                auftragListe.get(row).setOrt(val);
                break;
            case 2:
                if(val.length() == 0)
                {
                    JOptionPane.showMessageDialog(null, "Adresse darf nicht null sein", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                auftragListe.get(row).setAdresse(val);
                break;
            case 3:
                if(val.length() == 0)
                {
                    JOptionPane.showMessageDialog(null, "Beschreibung darf nicht null sein", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                auftragListe.get(row).setBeschreibung(val);
                break;
            case 4:

                if(val.length() == 0)
                {
                    JOptionPane.showMessageDialog(null, "Startdatum darf nicht null sein", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    DateFormat df = new SimpleDateFormat(DATE_FORMAT);
                    df.setLenient(false);
                    var startDate = df.parse(val);
                    var endDateStr = auftragListe.get(row).getEnddatum();
                    if(endDateStr != null && endDateStr.length() > 0 &&
                            df.parse(endDateStr).before(startDate))
                    {
                        JOptionPane.showMessageDialog(null, "Enddatum kann nicht vor dem Startdatum sein ", "Fehler", JOptionPane.ERROR_MESSAGE);
                        return;
                    }


                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(null, "Startdatum muss ein gültiges Datum beeinhalten", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }



                auftragListe.get(row).setStartdatum(val);
                break;
            case 5:
                if(val.length() == 0)
                {
                    JOptionPane.showMessageDialog(null, "Enddatum darf nicht null sein", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    DateFormat df = new SimpleDateFormat(DATE_FORMAT);
                    df.setLenient(false);
                    df.parse(val);
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(null, "Enddatum muss ein gültiges Datum beeinhalten", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                auftragListe.get(row).setEnddatum(val);
                break;
            default:break;
        }
        fireTableCellUpdated(row, col);
    }

    @Override
    public String getColumnName(int col) {
        return spaltenNamen[col];
    }

    @Override
    public int getRowCount() {
        return auftragListe.size();
    }

    @Override
    public int getColumnCount() {
        return spaltenNamen.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch(col)
        {
            case 0:
                return auftragListe.get(row).getAuftraggeber();
            case 1:
                return auftragListe.get(row).getOrt();
            case 2:
                return auftragListe.get(row).getAdresse();
            case 3:
                return auftragListe.get(row).getBeschreibung();
            case 4:
                return auftragListe.get(row).getStartdatum();
            case 5:
                return auftragListe.get(row).getEnddatum();
            default:break;
        }
        return "";
    }
}
