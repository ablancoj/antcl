package cat.urv.gfa.loyalty.utils;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.io.IOException;
import it.unisa.dia.gas.plaf.jpbc.util.io.Base64;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

public class Utils
{
    public static Element ElementFromBase64(final String input, final Field F) {
        Element r = null;
        try {
            r = F.newElementFromBytes(Base64.decode(input)).getImmutable();
        }
        catch (IOException e) {
            return null;
        }
        return r;
    }
    
    public static String ElementToBase64(final Element input) {
        return Base64.encodeBytes(input.toBytes());
    }
    
    public static Element hash(final String s, final Field F) {
        final byte[] h = hash(s);
        final Element e = F.newElement().setFromHash(h, 0, h.length);
        return e.getImmutable();
    }
    
    public static byte[] hash(final String s) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md.digest(s.getBytes());
    }
}
