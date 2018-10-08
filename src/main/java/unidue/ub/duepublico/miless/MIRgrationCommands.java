package unidue.ub.duepublico.miless;

import java.io.IOException;

import org.jdom2.JDOMException;
import org.mycore.access.MCRAccessException;
import org.mycore.common.MCRPersistenceException;
import org.mycore.datamodel.common.MCRActiveLinkException;
import org.mycore.frontend.cli.MCRAbstractCommands;
import org.mycore.frontend.cli.annotation.MCRCommandGroup;
import org.xml.sax.SAXException;

@MCRCommandGroup(name = "MIRgration Commands")
public class MIRgrationCommands extends MCRAbstractCommands {

    @org.mycore.frontend.cli.annotation.MCRCommand(syntax = "mirgrate document {0}",
        help = "Migrate metadata, derivates and files of document from miless-based DuEPublico server",
        order = 10)
    public static void mirgrateDocument(String documentID) throws MCRPersistenceException, MCRAccessException,
        MCRActiveLinkException, JDOMException, IOException, SAXException {
        MIRgrator mirgrator = new MIRgrator(documentID);
        try {
            mirgrator.mirgrate();
        } catch (MIRgrationException ex) {
            if (mirgrator.getErrors().isEmpty()) {
                throw ex;
            } else {
                for (String error : mirgrator.getErrors()) {
                    System.out.println(error);
                }
            }
        }
    }
}