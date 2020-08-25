package org.VVC;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.StAXEventBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XmlController {

    public static Document createXML(List<File>fileList,String description){
        Element root=new Element("VVC");
        Document doc=new Document();
        Element date=new Element("xml_info");
        date.setAttribute("date", getDateFormat(LocalDateTime.now()));
        date.setAttribute("description",description);
        root.addContent(date);

        for (File f:fileList){
            BasicFileAttributes attr = null;
            try {
                attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(f.isDirectory())continue;
            Element file=new Element("File");
            file.setAttribute("name",f.getName());
            file.setAttribute("path",f.getAbsolutePath());

            if(attr!=null) {
                file.setAttribute("created", attr.creationTime().toString());
                file.setAttribute("updateTime", attr.lastModifiedTime().toString());
                file.setAttribute("size",Long.toString(attr.size()));
            }
            root.addContent(file);
        }

        doc.setRootElement(root);
        saveXMLConfig(doc,"./.vvc");
        return doc;
    }

    private static Document getDocument(String filePath){
        Document document = null;
        try
        {
            XMLInputFactory factory = XMLInputFactory.newFactory();
            XMLEventReader reader = factory.createXMLEventReader(new FileReader(filePath));
            StAXEventBuilder builder = new StAXEventBuilder();
            document = builder.build(reader);
        }
        catch (JDOMException | IOException | XMLStreamException e)
        {
            System.out.println("cant Find File");
            //e.printStackTrace();
        }
        return document;
    }

    public static List<File> getModified(){
        List<File>flist=new ArrayList<>();
        Document doc=getDocument("./.vvc/VVCConfig.xml");
        if(doc==null){
            System.out.println("cml document do not exist");
            return null;
        }
        Element root=doc.getRootElement();
        List<Element> filesInXML=root.getChildren("File");

        for (File f:FileFinder.listAllUnIgnoredFiles()){
            if(f.isDirectory())continue;
            Element thisElement=null;
            for (Element el:filesInXML){
                if(el.getAttributeValue("name").equals(f.getName())&&el.getAttributeValue("path").equals(f.getAbsolutePath())){
                    thisElement=el;
                }
            }

            if(thisElement!=null){
                FileAttributes atr=new FileAttributes(thisElement.getAttributeValue("name"),
                        thisElement.getAttributeValue("size"),thisElement.getAttributeValue("created"),
                        thisElement.getAttributeValue("updateTime"),
                        thisElement.getAttributeValue("path"));

                if(!FileFinder.CompareFiles(atr,f)){
                    System.out.println(f.getName()+" failed comparison");
                    flist.add(f);
                }
            }else {
                System.out.println(f.getName()+" fail to find file");
                flist.add(f);
            }
        }

        for (File file:flist){
            System.out.println("Updated File "+file.getName());
        }
        return flist;
    }

    public static String localPath(String fullPath){
        if(fullPath==null)return null;
        String[]split=fullPath.split("\\.\\\\");
        if(split.length>1) {
            return split[1];
        }
        return null;
    }

    public static void createIgnoreList(List<File>filesToIgnore){

        Element root=new Element("VVCIgnore");
        for (File file:filesToIgnore){
            BasicFileAttributes attr = null;
            try {
                attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(file.isDirectory())continue;
            Element element=new Element("File");
            element.setAttribute("name",file.getName());
            element.setAttribute("path",file.getAbsolutePath());

            if(attr!=null) {
                element.setAttribute("created", attr.creationTime().toString());
                element.setAttribute("updateTime", attr.lastModifiedTime().toString());
                element.setAttribute("size",Long.toString(attr.size()));
            }
            root.addContent(element);
        }

        Document doc=new Document();
        doc.setRootElement(root);
        XMLOutputter outter=new XMLOutputter();
        outter.setFormat(Format.getPrettyFormat());
        try {
            File dir=new File(".vvc");
            if(!dir.exists()){
                dir.mkdir();
                Files.setAttribute(dir.toPath(), "dos:hidden", true);
            }
            outter.output(doc, new FileWriter(new File(".vvc/VVCIgnore.xml")));

        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static List<File>getIgnoreList(){

        Document doc = getDocument("./.vvc/VVCIgnore.xml");
        if(doc==null){
            System.out.println("noIgnoredList");
            return null;
        }
        List<Element> elementList=doc.getRootElement().getChildren("File");
        List<File>ignoreList=new ArrayList<>();

        for (File f:FileFinder.listAllFiles("./")){
            Element thisEl=null;
            for (Element el:elementList){
                if(el.getAttributeValue("name").equals(f.getName())){
                    ignoreList.add(f);
                }
            }
        }
        return ignoreList;
    }

    public static void commit(List<File>fileList,String description){
        System.out.println(fileList.size());
        if(fileList.size()==0)return;

        Document doc=getDocument("./.vvc/VVCConfig.xml");
        Element root= doc.getRootElement();
        Element commit=new Element("commit");
        commit.setAttribute("date",getDateFormat(LocalDateTime.now()));
        commit.setAttribute("description",description);
        commit.setAttribute("path",commit.getAttributeValue("date")+commit.getAttributeValue("description"));
        root.addContent(commit);

        for (File f:fileList){
           doc = updateXMLFile(f,doc);
        }

        saveXMLConfig(doc,"./.vvc");
        FileFinder.backupFiles(fileList,
                commit.getAttributeValue("date")+commit.getAttributeValue("description"));

        saveXMLConfig(doc,"./.vvc/"+commit.getAttributeValue("date")+commit.getAttributeValue("description"));

    }

    static Document updateXMLFile(File f,Document doc){

        Element root=doc.getRootElement();
        List<Element>elementList=root.getChildren("File");
        boolean fileUpdated=false;
        for (Element e:elementList){
            BasicFileAttributes attr = null;

            try {
                attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
            } catch (IOException exeption) {
                exeption.printStackTrace();
            }

            if(f.getName().equals(e.getAttributeValue("name"))){
                if(f.getAbsolutePath().equals(e.getAttributeValue("path"))){

                    fileUpdated=true;
                    //e.removeAttribute ("updateTime");
                    if(attr!=null) {
                        e.getAttribute("updateTime").setValue(attr.lastModifiedTime().toString());
                        e.getAttribute("created").setValue(attr.creationTime().toString());
                        e.getAttribute("size").setValue(Long.toString(attr.size()));
                    }
                }
            }
        }
        if(!fileUpdated){
            System.out.println("cant find attribute of that file creating new");
            //create file in XML
            BasicFileAttributes attr = null;

            try {
                attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Element file=new Element("File");
            file.setAttribute("name",f.getName());
            file.setAttribute("path",f.getAbsolutePath());

            if(attr!=null) {
                file.setAttribute("created", attr.creationTime().toString());
                file.setAttribute("updateTime", attr.lastModifiedTime().toString());
                file.setAttribute("size",Long.toString(attr.size()));
            }
            root.addContent(file);
        }
        return doc;
    }

    static String getDateFormat(LocalDateTime time){
        String date=DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss_").format(time);
        return date;
    }

    public static void saveXMLConfig(Document doc,String path){
        System.out.println("saving XML");
        XMLOutputter outter=new XMLOutputter();
        outter.setFormat(Format.getPrettyFormat());
        try {
            File dir=new File(".vvc");
            if(!dir.exists()){
                dir.mkdir();
                Files.setAttribute(dir.toPath(), "dos:hidden", true);
            }
            outter.output(doc, new FileWriter(new File(path+"/VVCConfig.xml")));

        }catch (Exception e){
            System.out.println(e);
        }
    }

}
