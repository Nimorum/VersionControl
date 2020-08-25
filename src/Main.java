
import org.VVC.FileFinder;
import org.VVC.View;
import org.VVC.XmlController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if(args.length==0){
            View view=new View();
        }else {
            if (args[0].equals("create")) {
                System.out.println("create");
                List<File> fl = FileFinder.listAllFiles("./");
                XmlController.createXML(fl,"test description");
            }
            if(args[0].equals("ignore")){
                List<File>flist=new ArrayList<>();
                flist.add(new File("./.vvc/VVCIgnore.xml"));
                flist.add(new File("./.vvc"));
                flist.add(new File("./startVVC.bat"));
                flist.add(new File("./VersionControl.jar"));
                flist.add(new File("./.vvc/VVCConfig.xml"));
                XmlController.createIgnoreList(flist);
            }
            if(args[0].equals("backup")){
                XmlController.commit(XmlController.getModified(),"secondBackup");
                //FileFinder.backupFiles(XmlController.getModified(),"initialBackup");
            }
        }
    }
}
