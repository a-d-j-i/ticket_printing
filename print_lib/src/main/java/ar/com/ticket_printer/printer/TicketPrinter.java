package ar.com.ticket_printer.printer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.event.PrintJobListener;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

/**
 *
 * @author adji
 */
public class TicketPrinter {

    static private void debug(String message, Object... args) {
        //Log some debug.
        System.out.println(String.format(message, args));
    }

    public interface PrinterStatusListener extends PrintJobListener {

        public void onStartPrinting();

        public void onException(Exception ex);
    }

    public static final double INCH = 72;
    public static final double MM = INCH / 25.4;

    public static JEditorPane loadHtml(String body, URL baseUrl) throws IOException, BadLocationException {
        HTMLEditorKit kit = new HTMLEditorKit() {
            @Override
            public ViewFactory getViewFactory() {
                return new HTMLEditorKit.HTMLFactory() {
                    @Override
                    public View create(Element elem) {
                        View v = super.create(elem);
                        if ((v != null) && (v instanceof ImageView)) {
                            ((ImageView) v).setLoadsSynchronously(true);
                        }
                        return v;
                    }
                };
            }
        };
        HTMLDocument doc = (HTMLDocument) (kit.createDefaultDocument());
        if (baseUrl != null) {
            doc.setBase(baseUrl);
        }
        Reader fin = new StringReader(body);
        kit.read(fin, doc, 0);
        fin.close();
        final JEditorPane editor = new JEditorPane();
        editor.setEditorKit(kit);
        editor.setDocument(doc);
        editor.setEditable(false);
        return editor;
    }

    static public void print(PrintService service, String body, PrinterStatusListener statusListener, double paperWidth, double paperHeight, URL baseUrl) {
        JEditorPane editor = null;
        try {
            editor = loadHtml(body, baseUrl);
        } catch (IOException | BadLocationException ex) {
            if (statusListener != null) {
                statusListener.onException(ex);
            }
            return;
        }

        // This can be used to calculate dinamically the size of the html and
        // then scale the print output to that.
        /*VirtualGraphics vg = new VirtualGraphics();
        try {
            editor.getPrintable(null, null).print(vg, pageFormat, 0);
        } catch (PrinterException ex) {
            if (statusListener != null) {
                statusListener.onException(ex);
            }
        }
        debug("VirtualGraphics RESULT : %f %f, desired %f %f [mm]",
                vg.getWidthLimit() / MM, vg.getHeightLimit() / MM, paperWidth / MM, paperHeight / MM);
        // I have a huge ticket, adjust tu calculated size
        paperWidth = Math.max(paperWidth, vg.getWidthLimit());
        paperHeight = Math.max(paperHeight, vg.getHeightLimit());
         */
        Paper paper = new Paper();
        paper.setSize(paperWidth, paperHeight);
        paper.setImageableArea(0, 0, paperWidth, paperHeight);
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPaper(paper);
        debugPageFormat("PAGE FORMAT", pageFormat);

        /* TODO: check if validate is usefull 
            java.awt.print.PrinterJob pj = java.awt.print.PrinterJob.getPrinterJob();
            try {
                pj.setPrintService(service);
            } catch (PrinterException ex) {
                if (statusListener != null) {
                    statusListener.onException(ex);
                }
            }
            PageFormat postPageFormat = pj.validatePage(pageFormat);
            debugPageFormat("POS VALIDATE", postPageFormat);
            double desiredX = postPageFormat.getImageableWidth();
            double desiredY = Math.max(paperHeight, postPageFormat.getImageableHeight());
            final double scalex = desiredX / postPageFormat.getImageableWidth();
            final double scaley = desiredY / postPageFormat.getImageableHeight();
         */
        final double scalex = 1;
        final double scaley = 1;
        debug("CALCULATED SCALE %f %f, DESIRED %f %f", scalex, scaley, paperWidth / MM, paperHeight / MM);
        final JEditorPane finalEditor = editor;
        finalEditor.setLocation(0, 0);
        finalEditor.setSize((int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());
        Book book = new Book();
        book.append(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex >= 1) {
                    return Printable.NO_SUCH_PAGE;
                }
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.scale(scalex, scaley);
                g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
                finalEditor.printAll(g2d);
                return Printable.PAGE_EXISTS;
            }
        }, pageFormat);

        Doc docc = new SimpleDoc(book, DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
        if (statusListener != null) {
            statusListener.onStartPrinting();
        }
        if (service != null) {
            DocPrintJob printJob = service.createPrintJob();
            if (statusListener != null) {
                printJob.addPrintJobListener(statusListener);
            }
            try {
                printJob.print(docc, null);
            } catch (PrintException ex) {
                if (statusListener != null) {
                    statusListener.onException(ex);
                }
            }
        } else {
            JFrame frame = new JFrame("Main print frame");
            //to check the size
            frame.setSize((int) paperWidth, (int) paperHeight);
            frame.setBackground(Color.black);
            frame.add(editor);
            frame.pack();
            frame.setVisible(true);
        }
    }

    private static void debugPageFormat(String msg, PageFormat pageFormat) {
        debug("%s %f %f imageable %f %f imageableXY %f %f paper %f %f pagerImageable %f %f [mm]",
                msg,
                pageFormat.getWidth() / MM, pageFormat.getHeight() / MM,
                pageFormat.getImageableWidth() / MM, pageFormat.getImageableHeight() / MM,
                pageFormat.getImageableX() / MM, pageFormat.getImageableY() / MM,
                pageFormat.getPaper().getWidth() / MM, pageFormat.getPaper().getHeight() / MM,
                pageFormat.getPaper().getImageableWidth() / MM, pageFormat.getPaper().getImageableHeight() / MM);

    }
}
