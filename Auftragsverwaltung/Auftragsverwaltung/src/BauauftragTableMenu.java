import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;



public class BauauftragTableMenu extends JPopupMenu {
    private JTable table;
    private BauauftragModel model;
    private MitarbeiterModel mitarbeiterModel;

    /**
     * bereitet das ContextMenü für das die Auftragstabelle vor
     */
    public BauauftragTableMenu(JTable table, BauauftragModel model, MitarbeiterModel mitarbeiterModel)
    {
        this.table = table;
        this.model = model;
        this.mitarbeiterModel = mitarbeiterModel;
        JMenuItem add = new JMenuItem("Auftrag hinzufügen");
        JMenuItem del = new JMenuItem("Auftrag entfernen");


        add.addActionListener((ActionEvent event)->
        {
            var neuerAuftrag = new Bauauftrag();
            neuerAuftrag.setId(this.model.getItems().size()+1);
            neuerAuftrag.setAuftraggeber("Auftraggeber");
            neuerAuftrag.setBeschreibung("Auftrag");
            neuerAuftrag.setOrt("Ort");
            neuerAuftrag.setAdresse("Adresse");
            neuerAuftrag.setStartdatum("01.01.1980");
            neuerAuftrag.setEnddatum("01.01.1980");
            this.model.getItems().add(neuerAuftrag);
            this.model.fireTableDataChanged();
        });

        del.addActionListener((ActionEvent event)->
        {
            List<Bauauftrag> auftraege = new ArrayList<>();



            for(var item : table.getSelectedRows()) {
                var ausgewaehlterAuftrag = this.model.getItems().get(item);

                boolean should_Remove = true;
                for(var mitarbeiter : mitarbeiterModel.getItems()) {
                    for(var id : mitarbeiter.getAuftraege().split(";"))
                    {
                        if(id != null && id.length() > 0 && ausgewaehlterAuftrag.getId() == Integer.parseInt(id) )
                        {
                            JOptionPane.showMessageDialog(null, "Ein zugewiesener Auftrag kann nicht gelöscht werden!", "Fehler", JOptionPane.ERROR_MESSAGE);
                            should_Remove=false;
                            break;
                        }
                    }
                }
                if(should_Remove)
                    auftraege.add(ausgewaehlterAuftrag);

            }

            this.model.getItems().removeAll(auftraege);
            this.model.fireTableDataChanged();
        });

        this.add(add);
        this.add(del);

    }
}
