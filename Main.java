import cat.urv.gfa.loyalty.model.Token;
import cat.urv.gfa.loyalty.model.SharedInformation;
import java.util.Iterator;
import javax.smartcardio.CardChannel;
import javax.smartcardio.Card;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import java.util.List;
import java.util.concurrent.ExecutionException;
import cat.urv.gfa.loyalty.utils.CategoryPath;
import java.io.IOException;
import java.io.File;
import cat.urv.gfa.loyalty.communication.VendorSession;
import cat.urv.gfa.loyalty.protocol.Vendor;
import cat.urv.gfa.loyalty.utils.PairingsManager;
import java.io.FileNotFoundException;
import cat.urv.gfa.loyalty.utils.Setup;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardException;
import javax.smartcardio.TerminalFactory;

public class Main
{
    public static boolean SETUP;
    public static boolean ISSUE;
    public static boolean VERIFY;
    public static String PRODUCT;
    
    static {
        Main.SETUP = true;
        Main.ISSUE = true;
        Main.VERIFY = true;
        Main.PRODUCT = "Inception Movie";
    }
    
    public static void main(final String[] args) {
        System.out.print("Initializing Vendor...");
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
        try {
            Setup.main(null);
        }
        catch (FileNotFoundException e2) {
            System.err.println("Setup failed.");
            System.err.println(e2.getLocalizedMessage());
            System.exit(-1);
        }
        final Pairing e3 = PairingsManager.LoadPairingFromFile("curve.params");
        final Element g = PairingsManager.LoadGeneratorFromFile(e3.getG1(), "generator.params");
        final Element sk = e3.getZr().newRandomElement().getImmutable();
        final Vendor vendor = new Vendor("v", e3, g, sk);
        System.out.println("DONE.");
        System.out.println("Start Client setup...");
        try {
            terminal.waitForCardPresent(0L);
        }
        catch (CardException e4) {
            System.err.println(e4.getLocalizedMessage());
            System.exit(-1);
        }
        Card card = null;
        try {
            card = terminal.connect("*");
        }
        catch (CardException e7) {
            System.err.println("Cannot open channel.");
            System.exit(-1);
        }
        CardChannel channel = card.getBasicChannel();
        VendorSession vs = new VendorSession(vendor, channel);
        try {
            vs.select();
            if (Main.SETUP) {
                System.out.println("\t*Sending public parameters.");
                vs.sendPublicParameters(new File("curve.params"));
                System.out.println("\t*Sending generator.");
                vs.sendGenerator(new File("generator.params"));
                System.out.println("\t*Sending keys.");
                vs.sendSecretKey();
            }
        }
        catch (CardException | IOException ex4) {
            final Exception ex;
            final Exception e5 = ex;
            System.err.println(e5.getLocalizedMessage());
            System.exit(-1);
        }
        System.out.println("DONE");
        try {
            terminal.waitForCardAbsent(0L);
        }
        catch (CardException e6) {
            System.err.println(e6.getLocalizedMessage());
        }
        try {
            terminal.waitForCardPresent(0L);
        }
        catch (CardException e6) {
            System.err.println(e6.getLocalizedMessage());
        }
        card = null;
        try {
            card = terminal.connect("*");
        }
        catch (CardException e8) {
            System.err.println("Cannot open channel.");
        }
        channel = card.getBasicChannel();
        vs = new VendorSession(vendor, channel);
        try {
            vs.select();
            if (Main.ISSUE) {
                final List<String> path = CategoryPath.getPath(Main.PRODUCT);
                final long t = System.currentTimeMillis();
                System.out.println("Generating Token...");
                for (final String s : path) {
                    final SharedInformation sharedInfo = vendor.getSharedInfo(s, "20151231");
                    System.out.println("\t*Send shared information.");
                    final Element u = vs.sendSharedInfo(sharedInfo);
                    System.out.println("\t*Signing.");
                    vs.sign(sharedInfo, u);
                }
                System.out.println("DONE in " + (System.currentTimeMillis() - t) + "ms.");
            }
        }
        catch (CardException | IOException | ExecutionException | InterruptedException ex5) {
            final Exception ex2;
            final Exception e5 = ex2;
            System.err.println(e5.getLocalizedMessage());
        }
        try {
            terminal.waitForCardAbsent(0L);
        }
        catch (CardException e6) {
            System.err.println(e6.getLocalizedMessage());
        }
        try {
            terminal.waitForCardPresent(0L);
        }
        catch (CardException e6) {
            System.err.println(e6.getLocalizedMessage());
        }
        card = null;
        try {
            card = terminal.connect("*");
        }
        catch (CardException e8) {
            System.err.println("Cannot open channel.");
        }
        channel = card.getBasicChannel();
        vs = new VendorSession(vendor, channel);
        try {
            vs.select();
            if (Main.VERIFY) {
                final long t2 = System.currentTimeMillis();
                System.out.println("Request Token...");
                final Token tok = vs.requestToken();
                vs.verifyToken(tok);
                System.out.println("DONE in " + (System.currentTimeMillis() - t2) + "ms.");
            }
        }
        catch (CardException | IOException ex6) {
            final Exception ex3;
            final Exception e5 = ex3;
            System.err.println(e5.getLocalizedMessage());
        }
        try {
            terminal.waitForCardAbsent(0L);
        }
        catch (CardException e6) {
            System.err.println(e6.getLocalizedMessage());
        }
    }
    
    public static String byteArrayToHex(final byte[] a) {
        final StringBuilder sb = new StringBuilder(a.length * 2);
        for (final byte b : a) {
            sb.append(String.format("%02X ", b & 0xFF));
        }
        return sb.toString();
    }
}
