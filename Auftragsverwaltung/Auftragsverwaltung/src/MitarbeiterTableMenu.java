import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class MitarbeiterTableMenu extends JPopupMenu {
    private JTable table;
    private MitarbeiterModel model;
    private BauauftragModel auftragModel;

    /**
     * konvertiert eine id zu einem Bauauftrag
     */
    private Bauauftrag holeBauauftrag(String id)
    {
        Bauauftrag auftragZuPruefen = null;
        for(var auftragZuChecken : auftragModel.getItems())
        {
            if(auftragZuChecken.getId() == Integer.parseInt(id))
            {
                auftragZuPruefen = auftragZuChecken;
                break;
            }
        }
        return auftragZuPruefen;
    }

    /**
     * prüft ob es Konflikte zwischen zwei Daten gibt
     */
    private boolean gibtEsKonflikte(Date start, Date end, Date start2, Date end2)
    {
        return start.equals(start2) ||
                start.equals(end2) ||
                end.equals(start2) ||
                end.equals(end2) ||
                (start.after(start2) && start.before(end2)) ||
                (end.after(start2) && end.before(end2));
    }

    /**
     * bereitet das Contextmenü für die Mitarbeitertabelle vor
     */
    public MitarbeiterTableMenu(JTable table, MitarbeiterModel model, BauauftragModel auftragModel)
    {
        this.table = table;
        this.model = model;
        this.auftragModel = auftragModel;
        JMenuItem mitarbeiterHinzufuegen = new JMenuItem("Mitarbeiter hinzufügen");
        JMenuItem mitarbeiterEntfernen = new JMenuItem("Mitarbeiter entfernen");
        JMenuItem bildSetzen = new JMenuItem("Bild setzen");
        JMenuItem bildExportieren = new JMenuItem("Bild exportieren");
        JMenuItem auftraegeZuweisen = new JMenuItem("Aufträge zuweisen");

        mitarbeiterHinzufuegen.addActionListener((ActionEvent event)->
        {
            var neuerMitarbeiter = new Mitarbeiter();
            neuerMitarbeiter.setName("Neuer Mitarbeiter");
            neuerMitarbeiter.setId(this.model.getItems().size()+1);
            neuerMitarbeiter.setEinstellungsdatum("01.01.1980");
            neuerMitarbeiter.setBeruf("Neuer Beruf");
            neuerMitarbeiter.setJahresgehalt("0");
            neuerMitarbeiter.setBildBase64("");
            this.model.getItems().add(neuerMitarbeiter);
            this.model.fireTableDataChanged();
        });

        mitarbeiterEntfernen.addActionListener((ActionEvent event)->
        {
            List<Mitarbeiter> mitarbeiter = new ArrayList<>();

            for(var item : table.getSelectedRows())
                mitarbeiter.add(this.model.getItems().get(item));

            this.model.getItems().removeAll(mitarbeiter);
            this.model.fireTableDataChanged();
        });

        auftraegeZuweisen.addActionListener((ActionEvent event)->
        {
            DefaultListModel<String> auswahlModel = new DefaultListModel<String>();

            var checkBoxList = new JList( auswahlModel );
            checkBoxList.setCellRenderer(new CheckboxRenderer());

            // erstelle Listeneinträge
            for(var item : auftragModel.getItems())
                auswahlModel.addElement(item.getId() + "; " + item.getAuftraggeber() + " - " + item.getBeschreibung());

            checkBoxList.setModel(auswahlModel);
            checkBoxList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            checkBoxList.setVisible(true);


            // wähle Listeneinträge aus, die bereits vorhanden sind (Aufträge die zu einem Mitarbeiter gehören)
            int index = 0;
            List<Integer> indexes = new ArrayList<Integer>();
            for(var item : auftragModel.getItems()) {
                if(table.getSelectedRow() != -1 &&
                        this.model.getItems().get(table.getSelectedRow())  != null &&
                        this.model.getItems().get(table.getSelectedRow()).getAuftraege() != null) {
                    for (var selected : this.model.getItems().get(table.getSelectedRow()).getAuftraege().split(";")) {
                        if (selected.length() > 0 && item.getId() == Integer.parseInt(selected)) {
                                indexes.add(index);
                        }
                    }
                }
                index++;
            }
            var selectedItems = new int [indexes.size()];
            for(int i=0;i<indexes.size();i++)
                selectedItems[i] = indexes.get(i);
            checkBoxList.setSelectedIndices(selectedItems);


            JOptionPane.showMessageDialog(this, checkBoxList, "Aufträge zuweisen", JOptionPane.QUESTION_MESSAGE);

            String neueAufträge = "";
            List<String> cachedIds = new ArrayList<String>();
            for(var i : checkBoxList.getSelectedValuesList()) {
                String id = (String) i;
                boolean add = true, cached = false;
                var idgefiltertAktuellerAuftrag = id.split(";");
                if(cachedIds.contains(idgefiltertAktuellerAuftrag[0]))
                    continue;
                var auftrag = holeBauauftrag(idgefiltertAktuellerAuftrag[0]);
                try {
                    var mitarbeiter = this.model.getItems().get(table.getSelectedRow());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                    // prüfe nochmal ob Termine überschneiden
                    for (var j : checkBoxList.getSelectedValuesList()) {
                        String eintragJ = (String) j;
                        var id_gefiltert = eintragJ.split(";");
                        Bauauftrag auftragZuPruefen = holeBauauftrag(id_gefiltert[0]);
                        if (auftragZuPruefen.getId() != auftrag.getId()) {
                            // hole aktuellen Auftrag zur Id
                            var startDatum_aktuellerAuftrag = sdf.parse(auftrag.getStartdatum());
                            var endDatum_aktuellerAuftrag = sdf.parse(auftrag.getEnddatum());

                            var startDatum_auftragListe = sdf.parse(auftragZuPruefen.getStartdatum());
                            var endDate_auftragListe = sdf.parse(auftragZuPruefen.getEnddatum());

                            // prüfe ob es Terminkonflikte gibt
                            if (gibtEsKonflikte(startDatum_aktuellerAuftrag, endDatum_aktuellerAuftrag, startDatum_auftragListe, endDate_auftragListe)) {
                                JOptionPane.showMessageDialog(null, "Die Termine überschneiden sich und werden entfernt!", "Fehler", JOptionPane.ERROR_MESSAGE);
                                cachedIds.add(idgefiltertAktuellerAuftrag[0]);
                                cachedIds.add(id_gefiltert[0]);
                                add = false;
                                break;
                            }
                        }
                    }

                    // prüfe ob Einstellungstermin vor dem Auftragsbeginn ist
                    var einstellungsDatum = sdf.parse(mitarbeiter.getEinstellungsdatum());
                    var startDatum = sdf.parse(auftrag.getStartdatum());
                    if (einstellungsDatum.after(startDatum)) {
                        JOptionPane.showMessageDialog(null, "Das Einstellungsdatum kann nicht nach dem Auftragsbeginn sein!", "Fehler", JOptionPane.ERROR_MESSAGE);
                        add = false;
                        continue;
                    }
                    if(!add)
                        continue;
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if (add)
                    neueAufträge += idgefiltertAktuellerAuftrag[0] + ";";

            }



            // entferne das letzte Semikolon
            if(neueAufträge.length() > 0)
                neueAufträge = neueAufträge.substring(0,neueAufträge.length() - 1);

            if (neueAufträge != this.model.getItems().get(table.getSelectedRow()).getAuftraege()) {
                this.model.getItems().get(table.getSelectedRow()).setAuftraege(neueAufträge);
                this.model.fireTableDataChanged();
            }

        });

        bildSetzen.addActionListener((ActionEvent event)->
        {

            // konvertiere Bild in base64 um es zu laden und zu speichern
            var selectedRow = table.getSelectedRow();
            if(selectedRow == -1)
                return;

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Bild importieren");
            if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File path =  fileChooser.getSelectedFile();

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(fileChooser.getSelectedFile().getAbsolutePath());
                byte[] arr = new byte[(int)path.length()];
                fileInputStream.read(arr);
                fileInputStream.close();
                byte[] encoded = Base64.getEncoder().encode(arr);
                this.model.getItems().get(selectedRow).setBildBase64(new String(encoded));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.model.fireTableDataChanged();
        });

        bildExportieren.addActionListener((ActionEvent event)->
        {
            // konvertiere Bild in base64 um es zu laden und zu speichern
            var selectedRow = table.getSelectedRow();
            if(selectedRow == -1)
                return;

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Bild exportieren");
            if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }


            byte[] decoded = Base64.getDecoder().decode(this.model.getItems().get(selectedRow).getBildBase64().getBytes());
            File path =  fileChooser.getSelectedFile();

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(fileChooser.getSelectedFile().getAbsolutePath());
                fileOutputStream.write(decoded);
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        this.add(mitarbeiterHinzufuegen);
        this.add(mitarbeiterEntfernen);
        this.add(auftraegeZuweisen);
        this.add(bildSetzen);
        this.add(bildExportieren);
    }
}
