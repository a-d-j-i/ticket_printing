package ar.com.ticket_printer.printer;

import ar.com.ticket_printer.printer.WinSpool.WinspoolLib.PRINTER_INFO_2;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.INT_PTR;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class WinSpool {

    public enum WinSpoolPrinterAttributes {

        PRINTER_ATTRIBUTE_QUEUED(0x00000001),
        PRINTER_ATTRIBUTE_DIRECT(0x00000002),
        PRINTER_ATTRIBUTE_DEFAULT(0x00000004),
        PRINTER_ATTRIBUTE_SHARED(0x00000008),
        PRINTER_ATTRIBUTE_NETWORK(0x00000010),
        PRINTER_ATTRIBUTE_HIDDEN(0x00000020),
        PRINTER_ATTRIBUTE_LOCAL(0x00000040),
        PRINTER_ATTRIBUTE_ENABLE_DEVQ(0x00000080),
        PRINTER_ATTRIBUTE_KEEPPRINTEDJOBS(0x00000100),
        PRINTER_ATTRIBUTE_DO_COMPLETE_FIRST(0x00000200),
        PRINTER_ATTRIBUTE_WORK_OFFLINE(0x00000400),
        PRINTER_ATTRIBUTE_ENABLE_BIDI(0x00000800),
        PRINTER_ATTRIBUTE_RAW_ONLY(0x00001000),
        PRINTER_ATTRIBUTE_PUBLISHED(0x00002000),
        PRINTER_ATTRIBUTE_FAX(0x00004000),
        PRINTER_ATTRIBUTE_TS(0x00008000),
        PRINTER_ATTRIBUTE_PUSHED_USER(0x00020000),
        PRINTER_ATTRIBUTE_PUSHED_MACHINE(0x00040000),
        PRINTER_ATTRIBUTE_MACHINE(0x00080000),
        PRINTER_ATTRIBUTE_FRIENDLY_NAME(0x00100000),
        PRINTER_ATTRIBUTE_TS_GENERIC_DRIVER(0x00200000);

        static public Set<WinSpoolPrinterAttributes> getAttributesBits(int b) {
            EnumSet<WinSpoolPrinterAttributes> hat = EnumSet.noneOf(WinSpoolPrinterAttributes.class);
            for (WinSpoolPrinterAttributes a : WinSpoolPrinterAttributes.values()) {
                if ((b & a.attr) != 0) {
                    hat.add(a);
                }
            }
            return hat;
        }
        final private int attr;

        private WinSpoolPrinterAttributes(int stat) {
            this.attr = stat;
        }
    }

    public enum WinSpoolPrinterStatus {

        //PRINTER_STATUS_READY("The printer is ready", 0),
        PRINTER_STATUS_BUSY("The printer is busy", 0x00000200),
        PRINTER_STATUS_DOOR_OPEN("The printer door is open", 0x00400000),
        PRINTER_STATUS_ERROR("The printer is in an error state.", 0x00000002),
        PRINTER_STATUS_INITIALIZING("The printer is initializing.", 0x00008000),
        PRINTER_STATUS_IO_ACTIVE("The printer is in an active input/output state", 0x00000100),
        PRINTER_STATUS_MANUAL_FEED("The printer is in a manual feed state.", 0x00000020),
        PRINTER_STATUS_NO_TONER("The printer is out of toner.", 0x00040000),
        PRINTER_STATUS_NOT_AVAILABLE("The printer is not available for printing.", 0x00001000),
        PRINTER_STATUS_OFFLINE("The printer is offline.", 0x00000080),
        PRINTER_STATUS_OUT_OF_MEMORY("The printer has run out of memory.", 0x00200000),
        PRINTER_STATUS_OUTPUT_BIN_FULL("The printer's output bin is full.", 0x00000800),
        PRINTER_STATUS_PAGE_PUNT("The printer cannot print the current page.", 0x00080000),
        PRINTER_STATUS_PAPER_JAM("Paper is jammed in the printer", 0x00000008),
        PRINTER_STATUS_PAPER_OUT("The printer is out of paper.", 0x00000010),
        PRINTER_STATUS_PAPER_PROBLEM("The printer has a paper problem.", 0x00000040),
        PRINTER_STATUS_PAUSED("The printer is paused.", 0x00000001),
        PRINTER_STATUS_PENDING_DELETION("The printer is being deleted.", 0x00000004),
        PRINTER_STATUS_POWER_SAVE("The printer is in power save mode.", 0x01000000),
        PRINTER_STATUS_PRINTING("The printer is printing.", 0x00000400),
        PRINTER_STATUS_PROCESSING("The printer is processing a print job.", 0x00004000),
        PRINTER_STATUS_SERVER_UNKNOWN("The printer status is unknown.", 0x00800000),
        PRINTER_STATUS_TONER_LOW("The printer is low on toner.", 0x00020000),
        PRINTER_STATUS_USER_INTERVENTION("The printer has an error that requires the user to do something.", 0x00100000),
        PRINTER_STATUS_WAITING("The printer is waiting.", 0x00002000),
        PRINTER_STATUS_WARMING_UP("The printer is warming up.", 0x00010000),
        PRINTER_STATUS_APP_ERROR("An application error calling getStatus.", 0xFFFFFFFF);

        static public Set<WinSpoolPrinterStatus> getStatusBits(int b) {
            EnumSet<WinSpoolPrinterStatus> hs = EnumSet.noneOf(WinSpoolPrinterStatus.class);
            for (WinSpoolPrinterStatus s : WinSpoolPrinterStatus.values()) {
                if ((b & s.stat) != 0) {
                    hs.add(s);
                }
            }
            return hs;
        }
        final private int stat;
        final private String desc;

        private WinSpoolPrinterStatus(String desc, int stat) {
            this.desc = desc;
            this.stat = stat;
        }

        public String getDesc() {
            return desc;
        }
    }

    public interface WinspoolLib extends com.sun.jna.win32.StdCallLibrary {

        WinspoolLib INSTANCE = (WinspoolLib) Native.loadLibrary("Winspool.drv", WinspoolLib.class, W32APIOptions.UNICODE_OPTIONS);

        boolean GetPrinter(HANDLE hPrinter, int Level, Pointer pPrinter, int cbBuf, IntByReference pcbNeeded);

        boolean OpenPrinter(String pPrinterName, HANDLEByReference phPrinter, Pointer pDefault);

        void ClosePrinter(HANDLE hPrinter);

        public static class PRINTER_INFO_2 extends Structure {

            public String pServerName;
            public String pPrinterName;
            public String pShareName;
            public String pPortName;
            public String pDriverName;
            public String pComment;
            public String pLocation;
            public INT_PTR pDevMode;
            public String pSepFile;
            public String pPrintProcessor;
            public String pDatatype;
            public String pParameters;
            public INT_PTR pSecurityDescriptor;
            public int Attributes;
            public int Priority;
            public int DefaultPriority;
            public int StartTime;
            public int UntilTime;
            public int Status;
            public int cJobs;
            public int AveragePPM;

            protected List<String> getFieldOrder() {
                return Arrays.asList(new String[]{"pServerName", "pPrinterName", "pShareName", "pPortName",
                            "pDriverName", "pComment", "pLocation", "pDevMode", "pSepFile", "pPrintProcessor",
                            "pDatatype", "pParameters", "pSecurityDescriptor", "Attributes", "Priority", "DefaultPriority",
                            "StartTime", "UntilTime", "Status", "cJobs", "AveragePPM"});
            }

            public PRINTER_INFO_2() {
            }

            public PRINTER_INFO_2(int size) {
                super(new Memory(size));
            }
        }
        int PRINTER_ENUM_DEFAULT = 0x00000001;
        int PRINTER_ENUM_LOCAL = 0x00000002;
        int PRINTER_ENUM_CONNECTIONS = 0x00000004;
        int PRINTER_ENUM_FAVORITE = 0x00000004;
        int PRINTER_ENUM_NAME = 0x00000008;
        int PRINTER_ENUM_REMOTE = 0x00000010;
        int PRINTER_ENUM_SHARED = 0x00000020;
        int PRINTER_ENUM_NETWORK = 0x00000040;
        int PRINTER_ENUM_EXPAND = 0x00004000;
        int PRINTER_ENUM_CONTAINER = 0x00008000;
        int PRINTER_ENUM_ICONMASK = 0x00ff0000;
        int PRINTER_ENUM_ICON1 = 0x00010000;
        int PRINTER_ENUM_ICON2 = 0x00020000;
        int PRINTER_ENUM_ICON3 = 0x00040000;
        int PRINTER_ENUM_ICON4 = 0x00080000;
        int PRINTER_ENUM_ICON5 = 0x00100000;
        int PRINTER_ENUM_ICON6 = 0x00200000;
        int PRINTER_ENUM_ICON7 = 0x00400000;
        int PRINTER_ENUM_ICON8 = 0x00800000;
        int PRINTER_ENUM_HIDE = 0x01000000;
    }

    static void refreshState(String name, PrinterStatus.PrinterStateListener listener) {
        PRINTER_INFO_2 pi;
        IntByReference pcbNeeded = new IntByReference();
        IntByReference pcReturned = new IntByReference();
        HANDLEByReference pHandle = new HANDLEByReference();

        if (!WinspoolLib.INSTANCE.OpenPrinter(name, pHandle, null)) {
            WinspoolLib.INSTANCE.ClosePrinter(pHandle.getValue());
            listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_ERROR, "WinspoolLib Error calling OpenPrinter");
            return;
        }

        WinspoolLib.INSTANCE.GetPrinter(pHandle.getValue(), 2, null, 0, pcbNeeded);
        if (pcbNeeded.getValue() <= 0) {
            WinspoolLib.INSTANCE.ClosePrinter(pHandle.getValue());
            listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_ERROR, "WinspoolLib Error calling GetPrinter pcbNedeed <= 0");
            return;
        }


        PRINTER_INFO_2 pinfo2 = new PRINTER_INFO_2(pcbNeeded.getValue());
        if (!WinspoolLib.INSTANCE.GetPrinter(pHandle.getValue(), 2, pinfo2.getPointer(), pcbNeeded.getValue(), pcReturned)) {
            WinspoolLib.INSTANCE.ClosePrinter(pHandle.getValue());
            listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_ERROR, "WinspoolLib Error calling GetPrinter");
            return;
        }

        pinfo2.read();
        pi = (PRINTER_INFO_2) pinfo2;
        if (pi == null) {
            WinspoolLib.INSTANCE.ClosePrinter(pHandle.getValue());
            listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_ERROR, "WinspoolLib pi is null");
            return;
        }
        WinspoolLib.INSTANCE.ClosePrinter(pHandle.getValue());

        Set hat = WinSpoolPrinterAttributes.getAttributesBits(pi.Attributes);
        if (hat.contains(WinSpoolPrinterAttributes.PRINTER_ATTRIBUTE_WORK_OFFLINE)) {
            listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_ERROR, hat.toString());
            return;
        }

        Set hs = WinSpoolPrinterStatus.getStatusBits(pi.Status);
        if (hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_SERVER_UNKNOWN)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_PENDING_DELETION)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_NOT_AVAILABLE)) {
            listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_ERROR, hs.toString());
            return;
        }

        if (hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_OFFLINE)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_DOOR_OPEN)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_ERROR)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_OUT_OF_MEMORY)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_NO_TONER)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_OUTPUT_BIN_FULL)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_PAPER_JAM)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_PAPER_OUT)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_PAPER_PROBLEM)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_PAUSED)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_POWER_SAVE)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_OUTPUT_BIN_FULL)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_TONER_LOW)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_USER_INTERVENTION)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_OUTPUT_BIN_FULL)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_PAGE_PUNT)) {
            listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_ERROR, hs.toString());
        } else if (hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_BUSY)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_WARMING_UP)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_INITIALIZING)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_IO_ACTIVE)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_MANUAL_FEED)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_PRINTING)
                || hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_PROCESSING)) {
            listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_PRINTING, "Printing " + hs.toString());
        } else {
            //if (hs.contains(WinSpoolPrinterStatus.PRINTER_STATUS_WAITING)) {
            listener.setState(PrinterStatus.PRINTER_STATE.PRINTER_READY, "Ready");
        }
    }
}
