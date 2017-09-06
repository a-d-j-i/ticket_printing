package ar.com.ticket_printer.printer;

import com.sun.jna.Platform;
import javax.print.PrintService;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.QueuedJobCount;
import javax.print.event.PrintJobAttributeEvent;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintServiceAttributeEvent;
import javax.print.event.PrintServiceAttributeListener;

/**
 *
 * @author adji
 */
public class PrinterStatus {

    static private void debug(String message, Object... args) {
        //Log some debug.
    }

    // read timeout in ms
    public enum PRINTER_STATE {

        PRINTER_READY,
        PRINTER_PRINTING,
        PRINTER_NOT_ACCEPTING_JOBS,
        PRINTER_ERROR;
    };

    class MyPrintJobAttributeListener implements PrintJobAttributeListener {

        @Override
        public void attributeUpdate(PrintJobAttributeEvent pjae) {
            PrintJobAttributeSet attrs = pjae.getAttributes();
            if (attrs != null) {
                Attribute a = attrs.get(QueuedJobCount.class);
                if (a != null) {
                    QueuedJobCount q = (QueuedJobCount) a;
                    debug("MyPrintJobAttributeListener 0: %s %d", pjae.getPrintJob().getPrintService().getName(), q.getValue());
                }
            }
        }
    }

    class MyPrintServiceAttributeListener implements PrintServiceAttributeListener {

        @Override
        public void attributeUpdate(PrintServiceAttributeEvent psae) {
            PrintServiceAttributeSet attrs = psae.getAttributes();
            if (attrs != null) {
                Attribute a = attrs.get(QueuedJobCount.class);
                if (a != null) {
                    QueuedJobCount q = (QueuedJobCount) a;
                    debug("MyPrintJobAttributeListener 0: %s %d", psae.getPrintService().getName(), q.getValue());
                }
            }
        }
    }

    public interface PrinterStateListener {

        public void setState(PRINTER_STATE state, String msg);

    }

    static public void refreshState(PrintService service, PrinterStateListener listener) {
        AttributeSet att = service.getAttributes();
        if (att != null && att.get(PrinterIsAcceptingJobs.class) != null) {
            if (!att.get(PrinterIsAcceptingJobs.class).equals(PrinterIsAcceptingJobs.ACCEPTING_JOBS)) {
                listener.setState(PRINTER_STATE.PRINTER_NOT_ACCEPTING_JOBS, "Printer not accepting jobs");
                return;
            }
        }
        if (Platform.isWindows()) {
            WinSpool.refreshState(service.getName(), listener);
        } else {
            LinuxSpool.refreshState(service.getName(), listener);
        }
    }

    static public boolean needCheck(PRINTER_STATE printerState) {
        if (printerState == null) {
            return false;
        }
        return (printerState != PRINTER_STATE.PRINTER_READY && printerState != PRINTER_STATE.PRINTER_PRINTING);
    }

}
