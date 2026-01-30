package ExcelHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExcelWerkzeug {

    public static boolean istEsInt(String s) {
        if (s == null)
            return false;
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    public static boolean istEsDouble(String s) {
        if (s == null)
            return false;
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    public static String excelIntZuSpalte(int num) {

        String result = "";
        while (num > 0) {
            num--;
            int remainder = num % 26;
            char digit = (char) (remainder + 65);
            result = digit + result;
            num = (num - remainder) / 26;
        }
        return result;
    }
    public static int excelSpalteZuInt(String col){
        char[] colLetters = col.toCharArray();
        int result=0;
        for(int i=0;i<colLetters.length;i++){
            int letter = (int)(colLetters[i]-64);
            if(letter<1||letter>26)
                return -1;
            result+=letter*Math.pow(26, colLetters.length-(i+1));
        }
        return result;
    }
    public static void kopiereDatei(File from, File to ) throws IOException {
        Files.copy( from.toPath(), to.toPath() );
    }
    public boolean erstelleLeeresDokument(String docName){
        return (new File("new_excel")).mkdirs();
    }
    public static String holeDateienInhalt(BufferedReader reader){
        String fContents="";
        String line;
        try {
            while((line=reader.readLine())!=null){
                fContents += line;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fContents;
    }
    public static String[] sucheTreffer(Pattern p, String s){
        Matcher matcher = p.matcher(s);
        int ind=0;
        ArrayList<String> matches = new ArrayList<String>();

        while (matcher.find(ind)) {

            for(int i=1;i<matcher.groupCount()+1;i++){
                if(matcher.group(i)!=null){
                    ind=matcher.end(i);
                    matches.add(matcher.group(i));
                }
            }
        }
        return matches.toArray(new String[0]);
    }
}