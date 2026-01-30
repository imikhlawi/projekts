import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;


public class MitarbeiterModel extends AbstractTableModel {

    private String[] spaltenNamen = { "Bild", "Name" ,"Beruf", "Einstellungsdatum", "Jahresgehalt", "Aufträge" };
    private List<Mitarbeiter> mitarbeiterList;

    public MitarbeiterModel(List<Mitarbeiter> mitarbeiterList)
    {
        this.mitarbeiterList = mitarbeiterList;
    }

    public List<Mitarbeiter> getItems()
    {
        return mitarbeiterList;
    }

    public boolean isCellEditable(int row, int col)
    {
        return (col != 5) && (col != 0);
    }


    @Override
    public void setValueAt(Object value, int row, int col) {
        var val = (String)value;
        switch(col)
        {
            case 0:
                break;
            case 1:
                if(val.length() == 0)
                {
                    JOptionPane.showMessageDialog(null, "Name darf nicht null sein", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                mitarbeiterList.get(row).setName(val);
                break;
            case 2:
                if(val.length() == 0)
                {
                    JOptionPane.showMessageDialog(null, "Beruf darf nicht null sein", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                mitarbeiterList.get(row).setBeruf(val);
                break;
            case 3:
                if(val.length() == 0)
                {
                    JOptionPane.showMessageDialog(null, "Einstellungsdatum darf nicht null sein", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                final String DATE_FORMAT = "dd.MM.yyyy";
                try {
                    DateFormat df = new SimpleDateFormat(DATE_FORMAT);
                    df.setLenient(false);
                    df.parse(val);
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(null, "Einstellungsdatum muss ein gültiges Datum beeinhalten", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                mitarbeiterList.get(row).setEinstellungsdatum(val);
                break;
            case 4:
                if(val.length() == 0)
                {
                    JOptionPane.showMessageDialog(null, "Jahresgehalt darf nicht null sein", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(!val.matches("-?\\d+(\\.\\d+)?"))
                {
                    JOptionPane.showMessageDialog(null, "Jahresgehalt muss eine numerische Zahl sein!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                mitarbeiterList.get(row).setJahresgehalt(val);

                break;
            case 5:
                //mitarbeiterList.get(row).setAuftraege(val);
                break;
            default:
               break;
        }
        fireTableCellUpdated(row, col);
        return;
    }

    @Override
    public String getColumnName(int col) {
        return spaltenNamen[col];
    }

    @Override
    public int getRowCount() {
        return mitarbeiterList.size();
    }

    @Override
    public int getColumnCount() {
        return spaltenNamen.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex)
        {
            case 0:
                String bildBase64 = mitarbeiterList.get(rowIndex).getBildBase64();
                return (bildBase64 != null && bildBase64.length() > 0) ? Base64.getDecoder().decode(bildBase64) : " ";
            case 1:
                return mitarbeiterList.get(rowIndex).getName();
            case 2:
                return mitarbeiterList.get(rowIndex).getBeruf();
            case 3:
                return mitarbeiterList.get(rowIndex).getEinstellungsdatum();
            case 4:
                return mitarbeiterList.get(rowIndex).getJahresgehalt();
            case 5:
                return mitarbeiterList.get(rowIndex).getAuftraege();
            default:
                break;
        }
        return "";
    }
}
