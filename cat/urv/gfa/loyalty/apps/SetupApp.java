package cat.urv.gfa.loyalty.apps;

import javax.smartcardio.CardChannel;
import javax.smartcardio.Card;
import java.util.List;
import cat.urv.gfa.loyalty.communication.VendorSession;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardException;
import javax.smartcardio.TerminalFactory;
import java.io.IOException;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import cat.urv.gfa.loyalty.utils.Utils;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import cat.urv.gfa.loyalty.utils.PairingsManager;
import java.io.FileNotFoundException;
import cat.urv.gfa.loyalty.utils.Setup;
import cat.urv.gfa.loyalty.protocol.Vendor;

public class SetupApp
{
    public static final String VENDOR_NAME = "v";
    private Vendor mVendor;
    
    public SetupApp(final String vName) throws IOException {
        System.out.print("Initializing parameters...");
        try {
            Setup.main(new String[] { vName });
        }
        catch (FileNotFoundException e1) {
            System.err.println("FAILED.");
            System.err.println(e1.getLocalizedMessage());
            System.exit(-1);
        }
        System.out.println("DONE.");
        System.out.print("Loading parameters...");
        final Pairing e2 = PairingsManager.LoadPairingFromFile("curve.params");
        final Element g = PairingsManager.LoadGeneratorFromFile(e2.getG1(), "generator.params");
        final BufferedReader in = new BufferedReader(new FileReader(new File("vendor.config")));
        final String name = in.readLine();
        final Element sk = Utils.ElementFromBase64(in.readLine(), e2.getZr());
        in.close();
        this.mVendor = new Vendor(name, e2, g, sk);
        System.out.println("DONE.");
    }
    
    public void run() {
        System.out.print("Initializing Card Reader...");
        final TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = null;
        try {
            terminals = factory.terminals().list();
        }
        catch (CardException e1) {
            System.err.println("Cannot get list of terminals.");
            System.err.println(e1.getLocalizedMessage());
            System.exit(-1);
        }
        final CardTerminal terminal = terminals.get(0);
        System.out.println("DONE.");
        while (true) {
            System.out.println("Start Client setup...");
            try {
                terminal.waitForCardPresent(0L);
            }
            catch (CardException e2) {
                System.err.println(e2.getLocalizedMessage());
                System.exit(-1);
            }
            Card card = null;
            try {
                card = terminal.connect("*");
            }
            catch (CardException e5) {
                System.err.println("Cannot open channel.");
                System.exit(-1);
            }
            final CardChannel channel = card.getBasicChannel();
            final VendorSession vs = new VendorSession(this.mVendor, channel);
            try {
                vs.select();
                System.out.println("\t*Sending public parameters.");
                vs.sendPublicParameters(new File("curve.params"));
                System.out.println("\t*Sending generator.");
                vs.sendGenerator(new File("generator.params"));
                System.out.println("\t*Sending keys.");
                vs.sendSecretKey();
            }
            catch (CardException | IOException ex2) {
                final Exception ex;
                final Exception e3 = ex;
                System.err.println(e3.getLocalizedMessage());
                System.exit(-1);
            }
            System.out.println("DONE");
            try {
                terminal.waitForCardAbsent(0L);
            }
            catch (CardException e4) {
                System.err.println(e4.getLocalizedMessage());
            }
        }
    }
    
    public static void main(final String[] args) throws IOException {
        final SetupApp setup = new SetupApp("v");
        setup.run();
    }
}
