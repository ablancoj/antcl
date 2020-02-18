package cat.urv.gfa.loyalty.apps;

import cat.urv.gfa.loyalty.model.Token;
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
import cat.urv.gfa.loyalty.protocol.Vendor;

public class SubmitApp
{
    private Vendor mVendor;
    
    public SubmitApp() throws IOException {
        System.out.print("Loading parameters...");
        final Pairing e = PairingsManager.LoadPairingFromFile("curve.params");
        final Element g = PairingsManager.LoadGeneratorFromFile(e.getG1(), "generator.params");
        final BufferedReader in = new BufferedReader(new FileReader(new File("vendor.config")));
        final String name = in.readLine();
        final Element sk = Utils.ElementFromBase64(in.readLine(), e.getZr());
        in.close();
        this.mVendor = new Vendor(name, e, g, sk);
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
            try {
                terminal.waitForCardPresent(0L);
            }
            catch (CardException e2) {
                System.err.println(e2.getLocalizedMessage());
            }
            Card card = null;
            try {
                card = terminal.connect("*");
            }
            catch (CardException e5) {
                System.err.println("Cannot open channel.");
            }
            final CardChannel channel = card.getBasicChannel();
            final VendorSession vs = new VendorSession(this.mVendor, channel);
            try {
                vs.select();
                final long t = System.currentTimeMillis();
                System.out.println("Request Token...");
                final Token tok = vs.requestToken();
                vs.verifyToken(tok);
                System.out.println("DONE in " + (System.currentTimeMillis() - t) + "ms.");
            }
            catch (CardException | IOException ex2) {
                final Exception ex;
                final Exception e3 = ex;
                System.err.println(e3.getLocalizedMessage());
            }
            try {
                terminal.waitForCardAbsent(0L);
            }
            catch (CardException e4) {
                System.err.println(e4.getLocalizedMessage());
            }
        }
    }
    
    public static void main(final String[] args) throws IOException {
        final SubmitApp submit = new SubmitApp();
        submit.run();
    }
}
