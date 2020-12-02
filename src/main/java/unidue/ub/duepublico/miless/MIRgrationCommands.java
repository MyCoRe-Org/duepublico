package unidue.ub.duepublico.miless;

import java.io.BufferedWriter;
import java.io.FileWriter;
import org.mycore.frontend.cli.MCRAbstractCommands;
import org.mycore.frontend.cli.annotation.MCRCommandGroup;

@MCRCommandGroup(name = "MIRgration Commands")
public class MIRgrationCommands extends MCRAbstractCommands {

	private static final String INPUT_OUTPUT_PATH = "/home/tomcat4mir/MigrationLehrmaterial/";

	private static final String OUTPUT_MIRGRATED = "migratedLog.txt";

	@org.mycore.frontend.cli.annotation.MCRCommand(syntax = "mirgrate document {0} testing {1} force {2}", help = "Migrate metadata, derivates and files of document from miless-based DuEPublico server.", order = 10)
	public static void mirgrateDocument(String documentID, String justTesting, String force) {

		boolean bJustTesting = "true".equals(justTesting);
		boolean bForce = "true".equals(force);

		MIRgrator mirgrator = new MIRgrator(documentID);
		mirgrator.setJustTesting(bJustTesting);
		mirgrator.setIgnoreMetadataConversionErrors(bForce);
		mirgrator.run();

		BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(INPUT_OUTPUT_PATH + OUTPUT_MIRGRATED, true));
			output.newLine();

			if (!(mirgrator.getErrors().isEmpty() || bJustTesting)) {

				output.append("----Could not Migrate: " + documentID);

				for (String error : mirgrator.getErrors()) {
					output.newLine();
					output.append("----Errorlogger: " + error);
				}

				output.newLine();
				output.append(
						"----Unprocessed command: mirgrate document " + documentID + " testing false force false");

			} else {
				output.append("Successfully migrated document with id: " + documentID);
			}

			output.close();
		} catch (Exception e) {

		}
	}
}
