package ExcelHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class ExcelLeser {

    private final String FILE_NAME;
    private HashMap<String,Integer> worksheets;
    private static final String FILE_SEP="/";

    public ExcelLeser(String fName) throws IOException{
        if(fName.substring(fName.length()-5,fName.length()).equals(".xlsx"))
            FILE_NAME = fName;
        else
            FILE_NAME = fName+".xlsx";

        arbeitsBlattVorbereiten();
    }

    private HashMap<Integer,String> geteiltenStrings(String fileContents){

        HashMap<Integer,String> stringMap = new HashMap<Integer,String>();
        Pattern p = Pattern.compile("<si>.*?<t.*?>(.*?)</t>.*?</si>");

        String[] sharedStrs = ExcelWerkzeug.sucheTreffer(p,fileContents);
        for(int i=0;i<sharedStrs.length;i++)
            stringMap.put(i, sharedStrs[i]);
        return stringMap;
    }

    private HashMap<String,Integer> arbeitsBlattMap(String fileContents){

        HashMap<String, Integer> stringMap = new HashMap<String, Integer>();
        Pattern p = Pattern.compile("<sheet.*?name=\"(.*?)\".*?/>");

        String[] worksheetMacthes = ExcelWerkzeug.sucheTreffer(p,fileContents);
        for(int i=0;i<worksheetMacthes.length;i++)
            stringMap.put(worksheetMacthes[i], i+1);
        return stringMap;
    }

    private void arbeitsBlattVorbereiten() throws IOException{
        File file = new File(FILE_NAME);
        ZipFile zipFile = new ZipFile(file);
        ZipEntry workbookZip =  zipFile.getEntry("xl"+FILE_SEP+"workbook.xml");

        InputStream input = zipFile.getInputStream(workbookZip);
        BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        String workbook = ExcelWerkzeug.holeDateienInhalt(br);
        worksheets = arbeitsBlattMap(workbook);
        zipFile.close();
    }
    private static String[][] leseArbeitsblattXML(String fContents, HashMap<Integer,String> sharedStrs){

        String regVal = "<v>(.*?)</v>";
        String regRow = "<c r=\"([A-Z]*?)(\\d*?)\"";
        String regCell = regRow+"[^/]*?>.*?"+regVal;
        String regCellShared = regRow+"\\s*?(?:s=\"\\d*?\")??\\s*?(t=\"s(?:tr)??\").*?"+regVal;
        String finalReg="(?:"+"(?:"+regCellShared+")"+"|"+"(?:"+regCell+")"+")";
        Pattern valPattern = Pattern.compile(finalReg);
        Pattern dimPattern = Pattern.compile("<dimension ref=\"[A-Z]*?\\d*?:([A-Z]*?)(\\d*?)\"");

        Matcher dimMatcher = dimPattern.matcher(fContents);
        int numRows=0;
        int numCols=0;
        if(dimMatcher.find());{
            numRows=Integer.parseInt(dimMatcher.group(2));
            numCols= ExcelWerkzeug.excelSpalteZuInt(dimMatcher.group(1));
        }
        String[][] data = new String[numRows][numCols];
        Matcher valMatcher = valPattern.matcher(fContents);
        int ind=0;

        while (valMatcher.find(ind)) {

            if(valMatcher.group(1)!=null){
                int row=Integer.parseInt(valMatcher.group(2));
                int col= ExcelWerkzeug.excelSpalteZuInt(valMatcher.group(1));
                String val = valMatcher.group(4);
                data[row-1][col-1]=
                        valMatcher.group(3).equals("t=\"s\"") ?
                                sharedStrs.get(Integer.parseInt(val)) : val;
                ind=valMatcher.end(4);
            }
            else{

                int row=Integer.parseInt(valMatcher.group(6));
                int col= ExcelWerkzeug.excelSpalteZuInt(valMatcher.group(5));
                data[row-1][col-1]=valMatcher.group(7);
                ind=valMatcher.end(7);
            }
        }
        return data;

    }

    public String[][] leseArbeitsblatt(int sheetNum) throws IOException{
        File file = new File(FILE_NAME);
        String strFile="";
        String sheetFile="";
        ZipFile zipFile = new ZipFile(file);
        ZipEntry sheet =  zipFile.getEntry("xl"+FILE_SEP+"worksheets"+FILE_SEP+"sheet"+sheetNum+".xml");
        ZipEntry sharedStrs =  zipFile.getEntry("xl"+FILE_SEP+"sharedStrings.xml");

        InputStream input = zipFile.getInputStream(sheet);
        BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        sheetFile = ExcelWerkzeug.holeDateienInhalt(br);

        InputStream input2 = zipFile.getInputStream(sharedStrs);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(input2, "UTF-8"));
        strFile = ExcelWerkzeug.holeDateienInhalt(br2);

        zipFile.close();

        HashMap<Integer,String> sharedStrings = geteiltenStrings(strFile);
        return leseArbeitsblattXML(sheetFile,sharedStrings);
    }

    public String[][] leseArbeitsblatt(String sheetName) throws IOException{
        int sheetNum = worksheets.get(sheetName);
        return leseArbeitsblatt(sheetNum);
    }
    public int anzahlArbeitsblaetter(){
        return worksheets.size();
    }
    public String[] blaetterNamen(){
        return worksheets.keySet().toArray(new String[anzahlArbeitsblaetter()]);
    }
}