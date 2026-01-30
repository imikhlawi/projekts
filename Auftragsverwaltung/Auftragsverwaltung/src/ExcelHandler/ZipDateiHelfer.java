package ExcelHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class ZipDateiHelfer {

    private List<String> fileList;
    private List<String> dirList;
    private final String OUTPUT_ZIP_FILE;
    private final String SOURCE_FOLDER;
    private File SOURCE_FILE;
    private static final String FILE_SEP="/";

    public ZipDateiHelfer(String source, String target) {
        OUTPUT_ZIP_FILE=target;
        SOURCE_FOLDER=source;
        fileList = new ArrayList<String>();
        dirList = new ArrayList<String>();
        SOURCE_FILE=new File(SOURCE_FOLDER);
    }
    public void packEs()
    {
        generiereDateiListe(SOURCE_FILE);
        zipEs(OUTPUT_ZIP_FILE);
        for (String dir : this.dirList) {
            File deleteMe = new File(dir);
            deleteMe.delete();
        }
    }

    private void zipEs(String zipFile){

        byte[] buffer = new byte[1024];

        try{
            File zip = new File(zipFile);
            FileOutputStream fos = new FileOutputStream(zip);
            ZipOutputStream zos = new ZipOutputStream(fos);

            for(String file : this.fileList){

                ZipEntry ze= new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in =  new FileInputStream(SOURCE_FOLDER + FILE_SEP + file);
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
                File deleteMe = new File(SOURCE_FOLDER + File.separator + file);
                if(!deleteMe.isDirectory())
                    deleteMe.delete();

                zos.closeEntry();
            }

            zos.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    public static void kopiereZipDatei(InputStream resourceStream, File outputFile){
        byte[] buffer = new byte[1024];

        ZipInputStream zis = new ZipInputStream(resourceStream);
        try {
            ZipEntry entry= zis.getNextEntry();
            if(entry==null || entry.isDirectory()){
                System.err.println("Konnte zip-Datei nicht kopieren!");
                return;
            }
            FileOutputStream fos = new FileOutputStream(outputFile);

            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void entpacken(InputStream resourceStream, String outputFolder) throws FileAlreadyExistsException{
        byte[] buffer = new byte[1024];

        File folder = new File(outputFolder);
        if(!folder.exists())
            folder.mkdirs();
        else
            throw new FileAlreadyExistsException("Ein Ordner exisitiert bereits!");

        ZipInputStream zis = new ZipInputStream(resourceStream);
        ZipEntry entry;

        try {
            while((entry = zis.getNextEntry()) != null) {

                if(entry.isDirectory()){
                    new File(outputFolder + FILE_SEP + entry.getName()).mkdirs();
                    continue;
                }
                else{
                    File newFile = new File(outputFolder + FILE_SEP + entry.getName());
                    FileOutputStream fos = new FileOutputStream(newFile);

                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
            }
            zis.closeEntry();
            zis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void generiereDateiListe(File node){
        if(node.isFile())
            fileList.add(zipEintragErstellen(node.getPath().toString()).replaceAll("\\\\", FILE_SEP));
        if(node.isDirectory()){

            String[] subNote = node.list();

            for(String filename : subNote)
                generiereDateiListe(new File(node, filename));
            dirList.add(node.getPath().toString());
        }

    }

    private String zipEintragErstellen(String file){
        return file.substring(SOURCE_FOLDER.length()+1, file.length());
    }
}