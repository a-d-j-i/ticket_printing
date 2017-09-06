package ar.com.ticket_printer.printer;

import ar.com.ticket_printer.printer.LinuxSpool.LinuxCupsLib.CupsDest;
import ar.com.ticket_printer.printer.LinuxSpool.LinuxCupsLib.CupsOptions;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author adji
 */
public class LinuxSpool {

    public enum LinuxSpoolPrinterStatus {

        ATTRIBUTE_NOT_FOUND("Attribure not found", 0),
        PRINTER_NOT_FOUND("Printer not found", 1),
        IPP_PRINTER_IDLE("Printer is idle", 3),
        IPP_PRINTER_PROCESSING("Printer is working", 4),
        IPP_PRINTER_STOPPED("Printer is stopped", 5);
        static private final HashMap<Integer, LinuxSpoolPrinterStatus> revert = new HashMap<Integer, LinuxSpoolPrinterStatus>();

        static {
            for (LinuxSpoolPrinterStatus p : LinuxSpoolPrinterStatus.values()) {
                revert.put(p.stat, p);
            }
        }
        // they are flags, but I keep the first I find!!!

        static public LinuxSpoolPrinterStatus getStatus(int b) {
            return revert.get(b);
        }
        final private int stat;
        final private String desc;

        private LinuxSpoolPrinterStatus(String desc, int stat) {
            this.desc = desc;
            this.stat = stat;
        }

        public String getDesc() {
            return desc;
        }
    }
//    public interface LinuxCupsLib extends StdCallLibrary {

    public interface LinuxCupsLib extends com.sun.jna.Library {

        public static final String JNA_LIBRARY_NAME = "cups";
        public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(LinuxCupsLib.JNA_LIBRARY_NAME);
        public static final LinuxCupsLib INSTANCE = (LinuxCupsLib) Native.loadLibrary(LinuxCupsLib.JNA_LIBRARY_NAME, LinuxCupsLib.class);

        public static class CupsOptions extends Structure {

            public String name;
            public String value;

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(new String[]{"name", "value"});
            }

            public CupsOptions() {
            }

            public CupsOptions(Pointer pp) {
                super(pp);
                read();
            }

            public static class ByReference extends CupsDest implements Structure.ByReference {
            };

            public static class ByValue extends CupsDest implements Structure.ByValue {
            };
        }

        public static class CupsDest extends Structure {

            public String name;
            public String instance;
            public int is_default;
            public int num_options;
            //public cups_options.ByReference[] options;
            public Pointer options;

            public CupsDest() {
            }

            public CupsDest(Pointer pp) {
                super(pp);
                read();
            }

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(new String[]{"name", "instance", "is_default", "num_options", "options"});
            }

            public static class ByReference extends CupsDest implements Structure.ByReference {
            };

            public static class ByValue extends CupsDest implements Structure.ByValue {
            };
        }

        /*
         * extern int      cupsGetDests(cups_dest_t **dests);
         */
        int cupsGetDests(PointerByReference ppDests);
    }

    public static LinuxSpoolPrinterStatus getPrinterStatus(String name) {
        int size;
        PointerByReference p = new PointerByReference();

        size = LinuxCupsLib.INSTANCE.cupsGetDests(p);
        Pointer pp = p.getValue();
        CupsDest cd = new CupsDest(pp);

        if (size == 0) {
            return LinuxSpoolPrinterStatus.PRINTER_NOT_FOUND;
        }
        CupsDest[] cda = (CupsDest[]) cd.toArray(size);
        int i;
        for (i = 0; i < size; i++) {
            //System.out.format("xxx> %s\n", cda[i].toString(true));
            if (cda[i].name.equals(name)) {
                break;
            }
        }
        if (i >= size) {
            return LinuxSpoolPrinterStatus.PRINTER_NOT_FOUND;
        }

        CupsOptions co = new CupsOptions(cda[i].options);
        CupsOptions[] coa = (CupsOptions[]) co.toArray(cda[i].num_options);
        for (int j = 0; j < cda[i].num_options; j++) {
            if (coa[j].name.equals("printer-state")) {
                return LinuxSpoolPrinterStatus.getStatus(Integer.parseInt(coa[j].value));
            }
        }
        return LinuxSpoolPrinterStatus.ATTRIBUTE_NOT_FOUND;
    }

    static void refreshState(String name, PrinterStatus.PrinterStateListener listener) {
        LinuxSpoolPrinterStatus st = getPrinterStatus(name);
        switch (st) {
            case ATTRIBUTE_NOT_FOUND:
                listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_ERROR, "Attribute Not Found");
                break;
            case IPP_PRINTER_IDLE:
                listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_READY, "Ready");
                break;
            case IPP_PRINTER_PROCESSING:
                listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_PRINTING, "Printing");
                break;
            case IPP_PRINTER_STOPPED:
                listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_ERROR, "Printer Stopped");
                break;
            case PRINTER_NOT_FOUND:
                listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_ERROR, "Printer Not Found");
                break;
            default:
                listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_ERROR, String.format("Invalid code %s", st.name()));
                break;
        }
    }
}
