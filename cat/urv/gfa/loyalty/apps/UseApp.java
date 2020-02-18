package cat.urv.gfa.loyalty.apps;

import cat.urv.gfa.loyalty.model.SharedInformation;
import java.util.Iterator;
import javax.smartcardio.CardChannel;
import javax.smartcardio.Card;
import cat.urv.gfa.loyalty.communication.VendorSession;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardException;
import javax.smartcardio.TerminalFactory;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import cat.urv.gfa.loyalty.utils.CategoryPath;
import cat.urv.gfa.loyalty.utils.Utils;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import cat.urv.gfa.loyalty.utils.PairingsManager;
import java.util.List;
import cat.urv.gfa.loyalty.protocol.Vendor;

public class UseApp
{
    public static final String PRODUCT_NAME = "Inception Movie";
    public static final String EXPIRY_DATE = "20151231";
    private Vendor mVendor;
    private List<String> products;
    
    public UseApp(final String productName) throws IOException, ExecutionException, InterruptedException {
        System.out.print("Loading parameters...");
        final Pairing e = PairingsManager.LoadPairingFromFile("curve.params");
        final Element g = PairingsManager.LoadGeneratorFromFile(e.getG1(), "generator.params");
        final BufferedReader in = new BufferedReader(new FileReader(new File("vendor.config")));
        final String name = in.readLine();
        final Element sk = Utils.ElementFromBase64(in.readLine(), e.getZr());
        in.close();
        this.mVendor = new Vendor(name, e, g, sk);
        System.out.println("DONE.");
        this.products = CategoryPath.getPath(productName);
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
                System.out.println("Generating Token...");
                for (final String s : this.products) {
                    final SharedInformation sharedInfo = this.mVendor.getSharedInfo(s, "20151231");
                    System.out.println("\t*Send shared information.");
                    final Element u = vs.sendSharedInfo(sharedInfo);
                    System.out.println("\t*Signing.");
                    vs.sign(sharedInfo, u);
                    System.out.println("\t*Done.");
                }
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
    
    public static void main(final String[] args) throws IOException, ExecutionException, InterruptedException {
        String product = "Inception Movie";
        if (args != null && args[0] != null) {
            product = args[0];
        }
        final UseApp use = new UseApp(product);
        use.run();
    }
}
