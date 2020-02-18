import javax.smartcardio.CardChannel;
import javax.smartcardio.Card;
import java.util.List;
import cat.urv.gfa.loyalty.protocol.Vendor;
import cat.urv.gfa.loyalty.communication.VendorSession;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.io.IOException;
import javax.smartcardio.CardException;

public class StoreApp
{
    public static void main(final String[] args) throws CardException, IOException {
        final String option = args[0];
        final String file = args[1];
        if (option.equals("-s")) {
            store(file, args[2]);
        }
        else if (option.equals("-r")) {
            retrieve(file);
        }
    }
    
    public static void store(final String file, final String data) throws CardException, IOException {
        final TerminalFactory factory = TerminalFactory.getDefault();
        final List<CardTerminal> terminals = factory.terminals().list();
        final CardTerminal terminal = terminals.get(0);
        terminal.waitForCardPresent(0L);
        final Card card = terminal.connect("*");
        final CardChannel channel = card.getBasicChannel();
        final VendorSession vs = new VendorSession(null, channel);
        vs.select();
        vs.store(data.getBytes());
        System.out.println("Data stored");
        terminal.waitForCardAbsent(0L);
    }
    
    public static void retrieve(final String file) throws CardException, IOException {
        final TerminalFactory factory = TerminalFactory.getDefault();
        final List<CardTerminal> terminals = factory.terminals().list();
        final CardTerminal terminal = terminals.get(0);
        terminal.waitForCardPresent(0L);
        final Card card = terminal.connect("*");
        final CardChannel channel = card.getBasicChannel();
        final VendorSession vs = new VendorSession(null, channel);
        vs.select();
        final byte[] data = vs.retrieve();
        System.out.println(new String(data));
        terminal.waitForCardAbsent(0L);
    }
    
    public static void printHelp() {
        System.out.println("storeapp [-s|-r] file data");
    }
}
