package ExcelHandler;

import java.io.File;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.text.NumberFormat;
import java.util.ArrayList;


public class ExcelSchreiber {

    public final static String TEMPLATE_FILE_NAME="excel_vorlage";

    private boolean saved;
    private final String FILE_NAME;
    private ArrayList<String> sharedStrs;
    private int sharedStrsCount=0;
    private ArrayList<String> sheets;
    private PrintWriter sharedStrings;
    private PrintWriter contentTypes;
    private PrintWriter app;
    private PrintWriter workBookRels;
    private PrintWriter workBook;
    private NumberFormat doubleFormatter;
    private NumberFormat intFormatter;
    private static final String FILE_SEP="/";

    public ExcelSchreiber(String fName) throws IOException{

        if(fName.substring(fName.length()-5,fName.length()).equals(".xlsx"))
            FILE_NAME = fName.substring(0,fName.length()-5);
        else
            FILE_NAME = fName;

        File f = new File(FILE_NAME+".xlsx");
        if(f.exists()) {throw new FileAlreadyExistsException("Es gibt bereits eine Datei mit dem selben Namen!"); }

        saved=false;
        erstelleDokument();
        sharedStrs=new ArrayList<String>();
        sheets=new ArrayList<String>();

        contentTypes = new PrintWriter(new FileOutputStream(new File(FILE_NAME+"//[Content_Types].xml"),true));
        app = new PrintWriter(new FileOutputStream(new File(FILE_NAME+"//docProps//app.xml"),true));
        workBookRels = new PrintWriter(new FileOutputStream(new File(FILE_NAME+"//xl//_rels//workbook.xml.rels"),true));
        workBook = new PrintWriter(new FileOutputStream(new File(FILE_NAME+"//xl//workbook.xml"),true));
        sharedStrings = new PrintWriter(new FileOutputStream(new File(FILE_NAME+"//xl//sharedStrings.xml"),true));

    }

    private void erstelleDokument() throws IOException{
        InputStream resourceStream = ExcelSchreiber.class.getResourceAsStream(TEMPLATE_FILE_NAME+".zip");
        ZipDateiHelfer.entpacken(resourceStream, FILE_NAME);

    }
    public void schreibeArbeitsblatt(String[][] data, String sheetName) throws IOException {

        if(saved)
            throw new IllegalStateException("Das Arbeitsblatt wurde bereits gespeichert!");

        if(sheetName==null)
            sheetName="Sheet"+sheets.size();
        else{
            int sheetNum=1;
            String sheetTemp=sheetName;
            while(sheets.contains(sheetTemp)){
                sheetTemp=sheetName+"("+sheetNum+")";
                sheetNum++;
            }
            sheetName=sheetTemp;
        }
        int maxCol=0;
        for(int i=0;i<data.length;i++){
            if(data[i].length>maxCol)
                maxCol=data[i].length;
        }
        Sheet sheet = new Sheet(this,sheetName,data.length,maxCol);
        for(int i=0;i<data.length;i++)
            sheet.zeileHinzufuegen(data[i]);
        sheet.blattFertigstellen();
    }

    private void dokumentFertigstellen(){


        app.print(sheets.size()+"</vt:i4></vt:variant></vt:vector></HeadingPairs>" +
                "<TitlesOfParts><vt:vector size=\"" +sheets.size()
                +"\" baseType=\"lpstr\">");

        int sheetNum=1;
        for(String sheetName : sheets){
            app.println( "<vt:lpstr>"+sheetName+"</vt:lpstr>");

            workBook.print("<sheet name=\""+sheetName+"\" sheetId=\""+sheetNum+"\" r:id=\"rId"+sheetNum+"\"/>");


            contentTypes.println("<Override PartName=\"/xl/worksheets/sheet"+sheetNum+".xml\" "+
                    "ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>");

            workBookRels.println("<Relationship Target=\"worksheets/sheet"+sheetNum+".xml\" " +
                    "Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" " +
                    "Id=\"rId"+sheetNum+"\"/>");

            sheetNum++;
        }
        contentTypes.print("</Types>");

        app.print("</vt:vector></TitlesOfParts><LinksUpToDate>false</LinksUpToDate>" +
                "<SharedDoc>false</SharedDoc><HyperlinksChanged>false</HyperlinksChanged>" +
                "<AppVersion>14.0300</AppVersion></Properties>");

        workBook.print("</sheets><calcPr calcId=\"145621\"/></workbook>");

        String[] type = { "theme","styles","sharedStrings"};
        String[] target = {type[0]+"//"+type[0]+"1",type[1],type[2]};
        for(int i=0;i<type.length;i++)
            workBookRels.print("<Relationship  Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/"+type[i]+"\" " +
                    "Target=\""+target[i]+".xml\" Id=\"rId"+(sheets.size()+i+1)+"\"/>");
        workBookRels.print("</Relationships>");

        sharedStrings.println("count=\""+sharedStrsCount+"\" uniqueCount=\""+sharedStrs.size()+"\">");
        for(String shared : sharedStrs)
            sharedStrings.println("<si><t>"+shared+"</t></si>");
        sharedStrings.print("</sst>");

    }

    public void speichern(){

        if(saved)
            throw new IllegalStateException("Das Document kann nicht geöffnet werden. Es ist bereits gespeichert!");

        dokumentFertigstellen();
        sharedStrings.close();
        contentTypes.close();
        app.close();
        workBookRels.close();
        workBook.close();

        ZipDateiHelfer zipper = new ZipDateiHelfer(FILE_NAME,FILE_NAME+".zip");
        zipper.packEs();
        File excelDoc = new File(FILE_NAME+".zip");
        File name = new File(FILE_NAME+".xlsx");
        excelDoc.renameTo(name);

        saved=true;
    }

    public void doubleTypFestlegen(NumberFormat nFormat){
        doubleFormatter  = nFormat;
    }
    public void intTypFestlegen(NumberFormat nFormat){
        intFormatter  = nFormat;
    }
    public int anzahlBlaetter(){
        return sheets.size();
    }
    public String getDateinamen(){
        return FILE_NAME;
    }
    public boolean istGespeichert(){
        return saved;
    }


    private class Sheet {

        private ExcelSchreiber mDoc;
        private PrintWriter sheetWriter;
        private int numRows=0;
        public static final String EOF="</sheetData>" +
                "<pageMargins left=\"0.7\" right=\"0.7\" top=\"0.75\" bottom=\"0.75\" header=\"0.3\" footer=\"0.3\"/>" +
                "</worksheet>";
        public static final String POST_DIM = "\"/><sheetViews><sheetView tabSelected=\"1\" workbookViewId=\"0\"/></sheetViews>" +
                "<sheetFormatPr defaultRowHeight=\"15\" x14ac:dyDescent=\"0.25\"/><sheetData>";

        public Sheet(ExcelSchreiber doc, String sheetName, int row, int cols) throws IOException{
            mDoc=doc;
            mDoc.sheets.add(sheetName);
            int sheetNum=mDoc.anzahlBlaetter();
            File mFile = new File(doc.getDateinamen()+FILE_SEP+"xl"+FILE_SEP+"worksheets"+FILE_SEP+"sheet"+sheetNum+".xml");
            InputStream resourceStream = ExcelSchreiber.class.getResourceAsStream(TEMPLATE_FILE_NAME+".zip");
            if(sheetNum>1)
                ZipDateiHelfer.kopiereZipDatei(resourceStream, mFile);
            sheetWriter = new PrintWriter(new FileOutputStream(mFile,true));
            sheetWriter.println(ExcelWerkzeug.excelIntZuSpalte(cols)+row+POST_DIM);
        }
        public void blattFertigstellen(){
            sheetWriter.print(EOF);
            sheetWriter.close();
        }


        private String zeilenStart(int rowNum, int numCols){
            return "<row r=\""+rowNum+"\" spans=\"1:"+numCols+"\" x14ac:dyDescent=\"0.25\">";
        }
        private String zeilenEnde(){
            return "</row>";
        }

        public void zeileHinzufuegen(String[] data){

            numRows++;
            String row= zeilenStart(numRows,data.length);
            for(int i=0;i<data.length;i++){
                if(data[i]==null || data[i].equals(""))
                    continue;
                row+="<c r=\""+ ExcelWerkzeug.excelIntZuSpalte(i+1)+numRows+"\"";
                if(ExcelWerkzeug.istEsInt(data[i])){
                    if(intFormatter!=null)
                        row+="><v>"+intFormatter.format(Integer.parseInt(data[i]));
                    else
                        row+="><v>"+Integer.parseInt(data[i]);
                }
                else if(ExcelWerkzeug.istEsDouble(data[i])){
                    if(doubleFormatter!=null)
                        row+="><v>"+doubleFormatter.format(Double.parseDouble(data[i]));
                    else
                        row+="><v>"+Double.parseDouble(data[i]);

                }
                else{
                    mDoc.sharedStrsCount++;
                    row+=" t=\"s\"><v>";
                    ArrayList<String> shared = mDoc.sharedStrs;
                    if(shared.contains(data[i]))
                        row+=shared.indexOf(data[i]);
                    else{
                        row+=shared.size();
                        shared.add(data[i]);
                    }
                }
                row+="</v></c>";
            }
            row+= zeilenEnde();
            sheetWriter.println(row);
        }

    }
}