package cat.urv.gfa.loyalty.utils;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import it.unisa.dia.gas.jpbc.Pairing;

public class PairingsManager
{
    public static Pairing GeneratePairingParameters(final int rBits, final int qBits, final String path) {
        final TypeACurveGenerator parametersGenerator = new TypeACurveGenerator(rBits, qBits);
        final PairingParameters pairingParameters = parametersGenerator.generate();
        try {
            Throwable t = null;
            try {
                final FileWriter fw = new FileWriter(new File(path));
                try {
                    fw.write(pairingParameters.toString());
                }
                finally {
                    if (fw != null) {
                        fw.close();
                    }
                }
            }
            finally {
                if (t == null) {
                    final Throwable exception;
                    t = exception;
                }
                else {
                    final Throwable exception;
                    if (t != exception) {
                        t.addSuppressed(exception);
                    }
                }
            }
        }
        catch (IOException e) {
            System.out.println("File could not be created successfully.");
            return null;
        }
        return PairingFactory.getPairing(pairingParameters);
    }
    
    public static Pairing LoadPairingFromFile(final String path) {
        return PairingFactory.getPairing(path);
    }
    
    public static Element LoadGeneratorFromFile(final Field F, final String path) {
        try {
            final BufferedReader input = new BufferedReader(new FileReader(new File(path)));
            final String encoding = input.readLine();
            final Element g = Utils.ElementFromBase64(encoding, F);
            input.close();
            return g;
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
        catch (IOException e2) {
            System.out.println(e2.getLocalizedMessage());
            return null;
        }
    }
}
