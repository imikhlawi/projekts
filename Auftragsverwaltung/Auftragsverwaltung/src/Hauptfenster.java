import ExcelHandler.ExcelLeser;
import ExcelHandler.ExcelSchreiber;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hauptfenster extends JFrame{
    private JTabbedPane tabPane;
    private JPanel mainPane;
    private JTable auftraegeListe;
    private JTable mitarbeiterListe;
    private JTextArea arbeitGeberFeld;
    private JTextArea adresseFeld;
    private JTextArea ortFeld;
    private JTextArea beschreibungFeld;
    private JTextArea startDatumFeld;
    private JTextArea endDatumFeld;
    private JList mitarbeiterUebersichtsListe;
    private JButton zurueckKnopf;
    private JButton weiterKnopf;
    private JTextField suchFeldMitarbeiter;
    private JTextField suchTextAuftraege;

    private List<Bauauftrag> auftraege = new ArrayList<Bauauftrag>();
    private List<Mitarbeiter> mitarbeiter = new ArrayList<>();

    private MitarbeiterModel mitarbeiterModel = new MitarbeiterModel(mitarbeiter);
    private BauauftragModel bauauftragModel = new BauauftragModel(auftraege);

    private int aktuellerEintrag = 0;

    public Hauptfenster() {
        initializeGUI();
    }

    private void initializeGUI() {
        // Initialisiere alle GUI-Komponenten
        mainPane = new JPanel();
        tabPane = new JTabbedPane();
        auftraegeListe = new JTable();
        mitarbeiterListe = new JTable();
        arbeitGeberFeld = new JTextArea();
        adresseFeld = new JTextArea();
        ortFeld = new JTextArea();
        beschreibungFeld = new JTextArea();
        startDatumFeld = new JTextArea();
        endDatumFeld = new JTextArea();
        mitarbeiterUebersichtsListe = new JList<>();
        zurueckKnopf = new JButton("<<");
        weiterKnopf = new JButton(">>");
        suchFeldMitarbeiter = new JTextField();
        suchTextAuftraege = new JTextField();

        // Layout für mainPane
        mainPane.setLayout(new java.awt.BorderLayout());
        mainPane.add(tabPane, java.awt.BorderLayout.CENTER);

        // Übersichts-Tab
        JPanel uebersichtPanel = new JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        
        // Navigation
        JPanel navPanel = new JPanel(new java.awt.FlowLayout());
        zurueckKnopf.setEnabled(false);
        navPanel.add(zurueckKnopf);
        navPanel.add(weiterKnopf);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        uebersichtPanel.add(navPanel, gbc);

        // Mitarbeiterliste links
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 6; gbc.fill = java.awt.GridBagConstraints.BOTH; gbc.weightx = 0.3; gbc.weighty = 1.0;
        uebersichtPanel.add(new JScrollPane(mitarbeiterUebersichtsListe), gbc);

        // Labels und Felder
        gbc.gridheight = 1; gbc.weightx = 0.1; gbc.weighty = 0.0; gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1; gbc.gridy = 1; uebersichtPanel.add(new JLabel("Arbeitgeber"), gbc);
        gbc.gridx = 2; gbc.weightx = 0.6; arbeitGeberFeld.setEditable(false); uebersichtPanel.add(new JScrollPane(arbeitGeberFeld), gbc);
        
        gbc.weightx = 0.1; gbc.gridx = 1; gbc.gridy = 2; uebersichtPanel.add(new JLabel("Adresse"), gbc);
        gbc.gridx = 2; gbc.weightx = 0.6; adresseFeld.setEditable(false); uebersichtPanel.add(new JScrollPane(adresseFeld), gbc);
        
        gbc.weightx = 0.1; gbc.gridx = 1; gbc.gridy = 3; uebersichtPanel.add(new JLabel("Ort"), gbc);
        gbc.gridx = 2; gbc.weightx = 0.6; ortFeld.setEditable(false); uebersichtPanel.add(new JScrollPane(ortFeld), gbc);
        
        gbc.weightx = 0.1; gbc.gridx = 1; gbc.gridy = 4; uebersichtPanel.add(new JLabel("Beschreibung"), gbc);
        gbc.gridx = 2; gbc.weightx = 0.6; beschreibungFeld.setEditable(false); uebersichtPanel.add(new JScrollPane(beschreibungFeld), gbc);
        
        gbc.weightx = 0.1; gbc.gridx = 1; gbc.gridy = 5; uebersichtPanel.add(new JLabel("Startdatum"), gbc);
        gbc.gridx = 2; gbc.weightx = 0.6; startDatumFeld.setEditable(false); uebersichtPanel.add(new JScrollPane(startDatumFeld), gbc);
        
        gbc.weightx = 0.1; gbc.gridx = 1; gbc.gridy = 6; uebersichtPanel.add(new JLabel("Enddatum"), gbc);
        gbc.gridx = 2; gbc.weightx = 0.6; endDatumFeld.setEditable(false); uebersichtPanel.add(new JScrollPane(endDatumFeld), gbc);

        // Aufträge-Tab
        JPanel auftraegePanel = new JPanel(new java.awt.BorderLayout());
        JPanel suchPanelAuftraege = new JPanel(new java.awt.FlowLayout());
        suchPanelAuftraege.add(new JLabel("Suchen:"));
        suchPanelAuftraege.add(suchTextAuftraege);
        auftraegePanel.add(suchPanelAuftraege, java.awt.BorderLayout.NORTH);
        auftraegePanel.add(new JScrollPane(auftraegeListe), java.awt.BorderLayout.CENTER);

        // Mitarbeiter-Tab
        JPanel mitarbeiterPanel = new JPanel(new java.awt.BorderLayout());
        JPanel suchPanelMitarbeiter = new JPanel(new java.awt.FlowLayout());
        suchPanelMitarbeiter.add(new JLabel("Suchen:"));
        suchPanelMitarbeiter.add(suchFeldMitarbeiter);
        mitarbeiterPanel.add(suchPanelMitarbeiter, java.awt.BorderLayout.NORTH);
        mitarbeiterPanel.add(new JScrollPane(mitarbeiterListe), java.awt.BorderLayout.CENTER);

        // Tabs hinzufügen
        tabPane.addTab("Übersicht", uebersichtPanel);
        tabPane.addTab("Aufträge", auftraegePanel);
        tabPane.addTab("Mitarbeiter", mitarbeiterPanel);
    }

    /**
     * lädt die Aufträge
     */
    public void ladeAuftraege()
    {
        Bauauftrag testAuftrag = new Bauauftrag();
        testAuftrag.setAuftraggeber("Hochschule Bochum");
        testAuftrag.setAdresse("Am Hochschulcampus 1");
        testAuftrag.setOrt("Bochum");
        testAuftrag.setStartdatum("01.01.2022");
        testAuftrag.setEnddatum("01.01.2023");
        testAuftrag.setBeschreibung("Neues Gebäude");
        testAuftrag.setId(1);
        auftraege.add(testAuftrag);

        testAuftrag = new Bauauftrag();
        testAuftrag.setAuftraggeber("Ruhr Universität Bochum");
        testAuftrag.setAdresse("Universitätsstraße 150");
        testAuftrag.setOrt("Bochum");
        testAuftrag.setStartdatum("01.01.2022");
        testAuftrag.setEnddatum("01.01.2023");
        testAuftrag.setBeschreibung("Sanierungsarbeiten");
        testAuftrag.setId(2);
        auftraege.add(testAuftrag);

        auftraegeListe.setModel(bauauftragModel);
        auftraegeListe.setRowHeight(30);
        BauauftragTableMenu menu = new BauauftragTableMenu(auftraegeListe, bauauftragModel, mitarbeiterModel);
        auftraegeListe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e))
                    menu.show(e.getComponent(),e.getX(),e.getY());

            }
        });

        TableRowSorter<BauauftragModel> rowSorter = new TableRowSorter<>(bauauftragModel);
        auftraegeListe.setRowSorter(rowSorter);

        suchTextAuftraege.getDocument().addDocumentListener(new DocumentListener(){

            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = suchTextAuftraege.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = suchTextAuftraege.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        });
    }

    /**
     * lädt die Mitarbeiter
     */
    public void ladeMitarbeiter()
    {
        Mitarbeiter testMitarbeiter = new Mitarbeiter();
        testMitarbeiter.setName("Beispiel Mitarbeiter 1");
        testMitarbeiter.setId(1);
        testMitarbeiter.setAuftraege("2");
        testMitarbeiter.setJahresgehalt("34000");
        testMitarbeiter.setEinstellungsdatum("01.01.2018");
        testMitarbeiter.setBeruf("Dachdecker");
        testMitarbeiter.setBildBase64("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");
        mitarbeiter.add(testMitarbeiter);


        testMitarbeiter = new Mitarbeiter();
        testMitarbeiter.setName("Beispiel Mitarbeiter 2");
        testMitarbeiter.setId(2);
        testMitarbeiter.setAuftraege("1");
        testMitarbeiter.setJahresgehalt("50000");
        testMitarbeiter.setEinstellungsdatum("01.01.2016");
        testMitarbeiter.setBeruf("Architekt");
        testMitarbeiter.setBildBase64("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=");
        mitarbeiter.add(testMitarbeiter);

        mitarbeiterListe.setModel(mitarbeiterModel);
        mitarbeiterListe.getColumnModel().getColumn(0).setCellRenderer(new BildRenderer());
        mitarbeiterListe.setRowHeight(30);

        MitarbeiterTableMenu menu = new MitarbeiterTableMenu(mitarbeiterListe, mitarbeiterModel, bauauftragModel);

        mitarbeiterListe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e))
                    menu.show(e.getComponent(),e.getX(),e.getY());
            }
        });

        TableRowSorter<MitarbeiterModel> rowSorter = new TableRowSorter<>(mitarbeiterModel);
        mitarbeiterListe.setRowSorter(rowSorter);

        suchFeldMitarbeiter.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = suchFeldMitarbeiter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = suchFeldMitarbeiter.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        });


    }

    /**
     * lädt das Menü für das Exportieren und Importieren
     */
    public void ladeMenu()
    {
        JMenuBar menuBar;
        JMenu menu, exportierenMenu, importierenMenu;
        JMenuItem exportierenAlsCSVMenuItem, importierenAlsCSVMenuItem, exportierenAlsExcelMenuItem, importierenAlsExcelMenuItem;


        menuBar = new JMenuBar();
        menu = new JMenu("Auftragsverwaltung");
        exportierenMenu = new JMenu("Exportieren");
        importierenMenu = new JMenu("Importieren");
        exportierenAlsCSVMenuItem = new JMenuItem("CSV");
        importierenAlsCSVMenuItem = new JMenuItem("CSV");
        exportierenAlsExcelMenuItem = new JMenuItem("Excel");
        importierenAlsExcelMenuItem = new JMenuItem("Excel");

        exportierenAlsExcelMenuItem.addActionListener(e -> {
            try {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Mitarbeiter und Aufträge exportieren");
                File file = null;
                if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                ExcelSchreiber excelSchreiber = new ExcelSchreiber(fileChooser.getSelectedFile().getPath());
                List<String[]> zeilenExcel = new ArrayList<String[]>();

                zeilenExcel.add(new String[]{"Id", "Bild", "Name", "Beruf", "Einstellungsdatum", "Jahresgehalt", "Aufträge"});

                for (var aktuellerMitarbeiter : mitarbeiter)
                    zeilenExcel.add(new String[]
                            {
                             String.valueOf(aktuellerMitarbeiter.getId()),
                                    aktuellerMitarbeiter.getBildBase64(),
                             aktuellerMitarbeiter.getName(),
                             aktuellerMitarbeiter.getBeruf(),
                             aktuellerMitarbeiter.getEinstellungsdatum(),
                             aktuellerMitarbeiter.getJahresgehalt(),
                             aktuellerMitarbeiter.getAuftraege()
                            });


                zeilenExcel.add(new String[]{});
                //Id,Auftraggeber,Adresse,Ort,Beschreibung,Startdatum,Enddatum
                zeilenExcel.add(new String[]{"Id", "Auftraggeber", "Adresse", "Ort", "Beschreibung", "Startdatum", "Enddatum"});

                for (var aktuellerAuftrag : auftraege)
                    zeilenExcel.add(new String[]
                            {
                                    String.valueOf(aktuellerAuftrag.getId()),
                                    aktuellerAuftrag.getAuftraggeber(),
                                    aktuellerAuftrag.getAdresse(),
                                    aktuellerAuftrag.getOrt(),
                                    aktuellerAuftrag.getBeschreibung(),
                                    aktuellerAuftrag.getStartdatum(),
                                    aktuellerAuftrag.getEnddatum()
                            });


                excelSchreiber.schreibeArbeitsblatt(zeilenExcel.toArray(new String[][]{}), "Daten");
                excelSchreiber.speichern();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        exportierenAlsCSVMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Mitarbeiter und Aufträge exportieren");
            File file = null;
            if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            file = fileChooser.getSelectedFile();

            if (file == null)
                return;

            FileWriter fw = null;
            try {
                fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write("Id,Bild,Name,Beruf,Beginn,Jahresgehalt,Aufträge");
                bw.newLine();
                for (int i = 0; i < mitarbeiter.size(); i++) {
                    var aktuellerMitarbeiter = mitarbeiter.get(i);
                    bw.write(aktuellerMitarbeiter.getId() + "," +
                            (aktuellerMitarbeiter.getBildBase64() == "null" ? "" : aktuellerMitarbeiter.getBildBase64()) + "," +
                            (aktuellerMitarbeiter.getName() == "null" ? "" : aktuellerMitarbeiter.getName()) + "," +
                            (aktuellerMitarbeiter.getBeruf() == "null" ? "" : aktuellerMitarbeiter.getBeruf()) + "," +
                            (aktuellerMitarbeiter.getEinstellungsdatum() == "null" ? "" : aktuellerMitarbeiter.getEinstellungsdatum()) + "," +
                            (aktuellerMitarbeiter.getJahresgehalt() == "null" ? "" : aktuellerMitarbeiter.getJahresgehalt()) + "," +
                            (aktuellerMitarbeiter.getAuftraege() == "null" ? "" : aktuellerMitarbeiter.getAuftraege()));
                    bw.newLine();
                }
                bw.write("\nId,Auftraggeber,Adresse,Ort,Beschreibung,Startdatum,Enddatum");
                bw.newLine();
                for (int i = 0; i < auftraege.size(); i++) {
                    var aktuellerAuftrag = auftraege.get(i);
                    bw.write(aktuellerAuftrag.getId() + "," +
                            (aktuellerAuftrag.getAuftraggeber() == "null" ? "" : aktuellerAuftrag.getAuftraggeber()) + "," +
                            (aktuellerAuftrag.getAdresse() == "null" ? "" : aktuellerAuftrag.getAdresse()) + "," +
                            (aktuellerAuftrag.getOrt() == "null" ? "" : aktuellerAuftrag.getOrt()) + "," +
                            (aktuellerAuftrag.getBeschreibung() == "null" ? "" : aktuellerAuftrag.getBeschreibung()) + "," +
                            (aktuellerAuftrag.getStartdatum() == "null" ? "" : aktuellerAuftrag.getStartdatum()) + "," +
                            (aktuellerAuftrag.getEnddatum() == "null" ? "" : aktuellerAuftrag.getEnddatum()));
                    bw.newLine();
                }
                bw.close();
                fw.close();
            } catch (IOException IOe) {
                throw new RuntimeException(IOe);
            }

        });

        importierenAlsCSVMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Mitarbeiter und Aufträge exportieren");
            File file = null;
            if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            file = fileChooser.getSelectedFile();
            mitarbeiter.clear();
            auftraege.clear();
            try(BufferedReader br = new BufferedReader(new FileReader(file))) {


                boolean leseMitarbeiter = false;
                boolean leseAufträge = false;
                for(String line; (line = br.readLine()) != null; ) {
                    if(line.contains("Id,Bild,Name,Beruf,Beginn,Jahresgehalt,Aufträge"))
                    {
                        leseMitarbeiter  =true;
                        continue;
                    }


                    if(line.contains("Id,Auftraggeber,Adresse,Ort,Beschreibung,Startdatum,Enddatum") && leseMitarbeiter)
                    {
                        leseMitarbeiter=false;
                        leseAufträge=true;
                        continue;
                    }

                    if(leseMitarbeiter)
                    {
                        var mitarbeiterDaten = line.split(",");
                        var neuerMitarbeiter = new Mitarbeiter();
                        //Id,Bild,Name,Beruf,Beginn,Jahresgehalt,Aufträge
                        if(mitarbeiterDaten.length == 7) {
                            neuerMitarbeiter.setId((mitarbeiterDaten[0].equalsIgnoreCase("null")  ? 0 : Integer.parseInt(mitarbeiterDaten[0])));
                            neuerMitarbeiter.setBildBase64((mitarbeiterDaten[1].equalsIgnoreCase("null") ? "" : mitarbeiterDaten[1]));
                            neuerMitarbeiter.setName((mitarbeiterDaten[2].equalsIgnoreCase("null") ? "" : mitarbeiterDaten[2]));
                            neuerMitarbeiter.setBeruf((mitarbeiterDaten[3].equalsIgnoreCase("null") ? "" : mitarbeiterDaten[3]));
                            neuerMitarbeiter.setEinstellungsdatum((mitarbeiterDaten[4].equalsIgnoreCase("null") ? "" : mitarbeiterDaten[4]));
                            neuerMitarbeiter.setJahresgehalt((mitarbeiterDaten[5].equalsIgnoreCase("null") ? "" : mitarbeiterDaten[5]));
                            neuerMitarbeiter.setAuftraege((mitarbeiterDaten[6].equalsIgnoreCase("null")  ? "" : mitarbeiterDaten[6]));
                            mitarbeiter.add(neuerMitarbeiter);
                        }
                    }

                    if(leseAufträge)
                    {
                        var auftragsDaten = line.split(",");
                        var neuerAuftrag = new Bauauftrag();
                        //Id,Auftraggeber,Adresse,Ort,Beschreibung,Startdatum,Enddatum

                        if(auftragsDaten.length == 7) {
                            neuerAuftrag.setId(auftragsDaten[0].equalsIgnoreCase("null") ? 0 : Integer.parseInt(auftragsDaten[0]));
                            neuerAuftrag.setAuftraggeber(auftragsDaten[1].equalsIgnoreCase("null") ? "" : auftragsDaten[1]);
                            neuerAuftrag.setAdresse(auftragsDaten[2].equalsIgnoreCase("null") ? "" : auftragsDaten[2]);
                            neuerAuftrag.setOrt(auftragsDaten[3].equalsIgnoreCase("null") ? "" : auftragsDaten[3]);
                            neuerAuftrag.setBeschreibung(auftragsDaten[4].equalsIgnoreCase("null") ? "" : auftragsDaten[4]);
                            neuerAuftrag.setStartdatum(auftragsDaten[5].equalsIgnoreCase("null") ? "" : auftragsDaten[5]);
                            neuerAuftrag.setEnddatum(auftragsDaten[6].equalsIgnoreCase("null") ? "" : auftragsDaten[6]);
                            auftraege.add(neuerAuftrag);
                        }
                    }
                }
            } catch (IOException IOe) {
                System.out.println("Fehler: " + IOe.getStackTrace());
            }
            bauauftragModel.fireTableDataChanged();
            mitarbeiterModel.fireTableDataChanged();
            aktualisiereUebersicht();
        });

        importierenAlsExcelMenuItem.addActionListener(e -> {
            try {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Mitarbeiter und Aufträge exportieren");
                File file = null;
                if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                ExcelLeser excelLeser = new ExcelLeser(fileChooser.getSelectedFile().getAbsolutePath());
                List<String[]> zeilenExcel = Arrays.asList( excelLeser.leseArbeitsblatt(1));
                boolean leseMitarbeiter = true;
                boolean leseAuftraege = false;


                mitarbeiter.clear();
                auftraege.clear();

                for(int i=1;i<zeilenExcel.size();i++)
                {
                    if(zeilenExcel.get(i)[1] != null &&
                            zeilenExcel.get(i)[1].equalsIgnoreCase("Auftraggeber"))
                    {
                        leseMitarbeiter = false;
                        leseAuftraege=true;
                        continue;
                    }
                    if(leseMitarbeiter && zeilenExcel.get(i)[0] != null &&
                            !zeilenExcel.get(i)[0].equalsIgnoreCase("null"))
                    {
                        // "Id,Bild, Name,Beruf,Beginn,Jahresgehalt,Aufträge"

                        var neuerMitarbeiter = new Mitarbeiter();
                        neuerMitarbeiter.setId(Integer.parseInt(zeilenExcel.get(i)[0]));
                        neuerMitarbeiter.setBildBase64(zeilenExcel.get(i)[1]);
                        neuerMitarbeiter.setName(zeilenExcel.get(i)[2]);
                        neuerMitarbeiter.setBeruf(zeilenExcel.get(i)[3]);
                        neuerMitarbeiter.setEinstellungsdatum(zeilenExcel.get(i)[4]);
                        neuerMitarbeiter.setJahresgehalt(zeilenExcel.get(i)[5]);
                        neuerMitarbeiter.setAuftraege(zeilenExcel.get(i)[6]);

                        mitarbeiter.add(neuerMitarbeiter);
                    }

                    if(leseAuftraege &&  zeilenExcel.get(i)[0] != null )
                    {
                        //Id,Auftraggeber,Adresse,Ort,Beschreibung,Startdatum,Enddatum
                        var neuerAuftrag = new Bauauftrag();
                        neuerAuftrag.setId(Integer.parseInt(zeilenExcel.get(i)[0]));
                        neuerAuftrag.setAuftraggeber(zeilenExcel.get(i)[1]);
                        neuerAuftrag.setAdresse(zeilenExcel.get(i)[2]);
                        neuerAuftrag.setOrt(zeilenExcel.get(i)[3]);
                        neuerAuftrag.setBeschreibung(zeilenExcel.get(i)[4]);
                        neuerAuftrag.setStartdatum(zeilenExcel.get(i)[5]);
                        neuerAuftrag.setEnddatum(zeilenExcel.get(i)[6]);
                        auftraege.add(neuerAuftrag);
                    }
                }

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            bauauftragModel.fireTableDataChanged();
            mitarbeiterModel.fireTableDataChanged();
            aktualisiereUebersicht();
        });

        exportierenMenu.add(exportierenAlsCSVMenuItem);
        exportierenMenu.add(exportierenAlsExcelMenuItem);

        importierenMenu.add(importierenAlsCSVMenuItem);
        importierenMenu.add(importierenAlsExcelMenuItem);

        menu.add(exportierenMenu);
        menu.add(importierenMenu);
        menuBar.add(menu);

        this.setJMenuBar(menuBar);
    }

    /**
     * lädt die Übersicht
     */
    public void ladeUebersicht()
    {
        zurueckKnopf.addActionListener(e -> {
            aktuellerEintrag = aktuellerEintrag == 0 ? 0 : --aktuellerEintrag;

            aktualisiereUebersicht();
        });
        weiterKnopf.addActionListener(e -> {
            aktuellerEintrag = aktuellerEintrag+1 == auftraege.size() ? aktuellerEintrag : ++aktuellerEintrag;

            aktualisiereUebersicht();
        });

        aktualisiereUebersicht();
    }

    /**
     * lädt die Übersicht erneut
     */
    public void aktualisiereUebersicht() {
        zurueckKnopf.setEnabled(aktuellerEintrag != 0);
        weiterKnopf.setEnabled(aktuellerEintrag != auftraege.size()-1);
        zurueckKnopf.repaint();
        weiterKnopf.repaint();
        if (auftraege.size() == 0)
            return;
        adresseFeld.setText(auftraege.get(aktuellerEintrag).getAdresse());
        ortFeld.setText(auftraege.get(aktuellerEintrag).getOrt());
        arbeitGeberFeld.setText(auftraege.get(aktuellerEintrag).getAuftraggeber());
        beschreibungFeld.setText(auftraege.get(aktuellerEintrag).getBeschreibung());
        startDatumFeld.setText(auftraege.get(aktuellerEintrag).getStartdatum());
        endDatumFeld.setText(auftraege.get(aktuellerEintrag).getEnddatum());



        List<String> zugewieseneMitarbeiter = new ArrayList<String>();
        for (var item : mitarbeiter) {
            if(item.getAuftraege() != null) {
                for (var selected : item.getAuftraege().split(";")) {
                    if (selected.length() > 0 && auftraege.get(aktuellerEintrag).getId() == Integer.parseInt(selected))
                        zugewieseneMitarbeiter.add(item.getName() + " - " + item.getBeruf());

                }
            }
        }

        DefaultListModel<String> standardMitarbeiterModel = new DefaultListModel<String>();

        for(var zugewiesnerMitarbeiter : zugewieseneMitarbeiter)
            standardMitarbeiterModel.addElement(zugewiesnerMitarbeiter);

        mitarbeiterUebersichtsListe.setModel(standardMitarbeiterModel);
        mitarbeiterUebersichtsListe.setCellRenderer(new DefaultListCellRenderer());
        mitarbeiterUebersichtsListe.setVisible(true);

    }

    // Einstiegspunkt
    public static void main(String[] args) throws InterruptedException {

        Hauptfenster h = new Hauptfenster();
        h.setContentPane(h.mainPane);
        h.ladeAuftraege();
        h.ladeMitarbeiter();
        h.ladeMenu();
        h.ladeUebersicht();
        h.setTitle("Auftragsverwaltung");
        h.setSize(500,500);
        h.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        h.setVisible(true);


    }
}
