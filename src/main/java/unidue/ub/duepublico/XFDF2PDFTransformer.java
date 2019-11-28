package unidue.ub.duepublico;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.fdf.FDFDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.mycore.common.content.MCRByteContent;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRSourceContent;
import org.mycore.common.content.transformer.MCRContentTransformer;
import org.mycore.frontend.MCRFrontendUtil;
import org.xml.sax.SAXException;

/**
 * Reads a PDF file from the /xfdf/f/@href specified in the given XFDF document,
 * fills the form fields with the XFDF document given as source input,
 * returns the resulting PDF.
 *
 * @author Frank L\u00FCtzenkirchen
 */
public class XFDF2PDFTransformer extends MCRContentTransformer {

    private static Logger LOGGER = LogManager.getLogger();

    @Override
    public MCRContent transform(MCRContent source) throws IOException {
        MCRContent sourceXFDF = source.getReusableCopy();

        try {
            Namespace ns_xfdf = Namespace.getNamespace("xfdf", "http://ns.adobe.com/xfdf/");
            Element xfdf = sourceXFDF.asXML().getRootElement();
            String href = xfdf.getChild("f", ns_xfdf).getAttributeValue("href");

            String baseURL = MCRFrontendUtil.getBaseURL();
            if (href.startsWith(baseURL)) {
                href = "webapp:" + href.substring(baseURL.length());
            }
            LOGGER.info("will read PDF from URI " + href);

            MCRContent sourcePDF = MCRSourceContent.getInstance(href);
            PDDocument pdf = PDDocument.load(sourcePDF.getInputStream());

            PDAcroForm form = pdf.getDocumentCatalog().getAcroForm();
            FDFDocument fdf = FDFDocument.loadXFDF(sourceXFDF.getInputStream());
            form.importFDF(fdf);
            LOGGER.info("imported XFDF into PDF from " + href);

            MCRContent result = pdf2content(pdf);
            result.setMimeType("application/pdf");
            return result;
        } catch (JDOMException | TransformerException | SAXException ex) {
            throw new IOException(ex);
        }
    }

    // Tried to use PDStream, but could'nt get it working
    private MCRContent pdf2content(PDDocument pdf) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pdf.save(baos);
        pdf.close();
        baos.flush();
        byte[] result = baos.toByteArray();
        return new MCRByteContent(result);
    }

    @Override
    public String getMimeType() throws Exception {
        return "application/pdf";
    }

    @Override
    public String getFileExtension() throws Exception {
        return "pdf";
    }
}
