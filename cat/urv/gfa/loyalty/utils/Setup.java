package cat.urv.gfa.loyalty.utils;

import java.io.FileNotFoundException;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import java.io.PrintWriter;
import java.io.File;

public class Setup
{
    public static final String PARAMETERS_FILE = "curve.params";
    public static final String GENERATOR_FILE = "generator.params";
    public static final String VENDOR_FILE = "vendor.config";
    public static final int R_BITS = 160;
    public static final int Q_BITS = 512;
    
    public static void main(final String[] args) throws FileNotFoundException {
        final String vendorName = args[0];
        final File eParams = new File("curve.params");
        final File gParams = new File("generator.params");
        final File vConfig = new File("vendor.config");
        Pairing pairing = null;
        if (eParams.exists()) {
            pairing = PairingsManager.LoadPairingFromFile("curve.params");
        }
        else {
            pairing = PairingsManager.GeneratePairingParameters(160, 512, "curve.params");
        }
        Element generator = null;
        if (!gParams.exists()) {
            generator = pairing.getG1().newRandomElement().getImmutable();
            final String encoding = Utils.ElementToBase64(generator);
            final PrintWriter pw = new PrintWriter(gParams);
            pw.println(encoding);
            pw.close();
        }
        if (!vConfig.exists()) {
            final Element sk = pairing.getZr().newRandomElement().getImmutable();
            final String encoding2 = Utils.ElementToBase64(sk);
            final PrintWriter pw2 = new PrintWriter(vConfig);
            pw2.println(vendorName);
            pw2.println(encoding2);
            pw2.close();
        }
    }
}
