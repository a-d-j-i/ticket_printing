package ar.com.ticket_printer;

import ar.com.ticket_printer.printer.PrinterStatus;
import ar.com.ticket_printer.printer.PrinterStatus.PrinterStateListener;
import static ar.com.ticket_printer.printer.PrinterStatus.refreshState;
import ar.com.ticket_printer.printer.TicketPrinter;
import static ar.com.ticket_printer.printer.TicketPrinter.MM;
import java.io.File;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.event.PrintJobEvent;

/**
 *
 * @author adji
 */
public class ExampleApp {

    //static final String somePrinterName = "CUSTOM_Engineering_TL80";
    public static final String SOME_PRINTER_NAME = "NII_W2K203DPI_USB";
    public static final Map<String, PrintService> PRINTERS = new HashMap<>();
    public static final PrintService SOME_PRINTER = getSomePrinter();

    static PrintService getSomePrinter() {
        // Get available printers and choose one PrintService.
        PrintService[] prnSvcs;
        prnSvcs = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService p : prnSvcs) {
            PRINTERS.put(p.getName(), p);
            debug("Found a printer " + p.getName());
            //p.addPrintServiceAttributeListener(new MyPrintServiceAttributeListener());
        }
        return PRINTERS.get(SOME_PRINTER_NAME);
    }

    static private void debug(String message, Object... args) {
        //Log some debug.
        System.out.println(String.format(message, args));
    }

    public static void checkPrinterStatus(final PrintService service) {
        if (service != null) {
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        refreshState(service, new PrinterStateListener() {
                            @Override
                            public void setState(PrinterStatus.PRINTER_STATE state, String msg) {
                                debug("setState " + state + " msg " + msg);
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            return;
                        }
                    }
                }
            }.start();
        }
    }

    public static void print(PrintService service) throws Exception {
        // Read the html
        String resourceDir = FileSystems.getDefault().getPath("src/main/res").toAbsolutePath().toString();
        Path path = FileSystems.getDefault().getPath(resourceDir, "example_html_file.html");
        debug("HTML path is: " + path.toAbsolutePath().toString());
        String html = new String(Files.readAllBytes(path), "UTF-8");

        // Printer status.
        TicketPrinter.PrinterStatusListener statusListener = new TicketPrinter.PrinterStatusListener() {
            @Override
            public void onStartPrinting() {
                debug("onStartPrinting");
            }

            @Override
            public void onException(Exception ex) {
                debug("onException " + ex);
            }

            @Override
            public void printDataTransferCompleted(PrintJobEvent pje) {
                debug("printDataTransferCompleted " + pje);
            }

            @Override
            public void printJobCompleted(PrintJobEvent pje) {
                debug("printJobCompleted " + pje);
            }

            @Override
            public void printJobFailed(PrintJobEvent pje) {
                debug("printJobFailed " + pje);
            }

            @Override
            public void printJobCanceled(PrintJobEvent pje) {
                debug("printJobCanceled " + pje);
            }

            @Override
            public void printJobNoMoreEvents(PrintJobEvent pje) {
                debug("printJobNoMoreEvents " + pje);
            }

            @Override
            public void printJobRequiresAttention(PrintJobEvent pje) {
                debug("printJobRequiresAttention " + pje);
            }

        };

        // base url is used to search for extra resources like images (use with care!!!).
        URL u = new File(resourceDir).toURI().toURL();
        debug("RESOURCE IN " + u.toString());
        TicketPrinter.print(service, html, statusListener, 60 * MM, 80 * MM, u);
    }

    
    
    public static void main(String[] args) throws Exception {
        checkPrinterStatus(SOME_PRINTER);
        print(SOME_PRINTER);
    }
}
