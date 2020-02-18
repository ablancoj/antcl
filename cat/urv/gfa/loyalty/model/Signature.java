package cat.urv.gfa.loyalty.model;

import java.io.IOException;
import it.unisa.dia.gas.plaf.jpbc.util.io.Base64;
import it.unisa.dia.gas.jpbc.Field;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.Element;

public class Signature
{
    public static String KEY_SIGNATURE;
    private Element mSignature;
    
    static {
        Signature.KEY_SIGNATURE = "s";
    }
    
    public Signature(final Element signature) {
        this.mSignature = signature;
    }
    
    public Signature(final String input, final Pairing p) {
        final JSONObject json = (JSONObject)JSONValue.parse(input);
        final String signature64 = json.get(Signature.KEY_SIGNATURE);
        this.mSignature = this.ElementFromBase64(signature64, p.getG1());
    }
    
    public String toJSON() {
        final JSONObject json = new JSONObject();
        json.put(Signature.KEY_SIGNATURE, this.ElementToBase64(this.mSignature));
        return json.toString();
    }
    
    public Element getSignature() {
        return this.mSignature;
    }
    
    public String getSignatureBase64() {
        return this.ElementToBase64(this.mSignature);
    }
    
    private Element ElementFromBase64(final String input, final Field F) {
        Element r = null;
        try {
            r = F.newElementFromBytes(Base64.decode(input)).getImmutable();
        }
        catch (IOException e) {
            return null;
        }
        return r;
    }
    
    private String ElementToBase64(final Element input) {
        return Base64.encodeBytes(input.toBytes());
    }
}
