package cat.urv.gfa.loyalty.model;

import java.io.IOException;
import it.unisa.dia.gas.plaf.jpbc.util.io.Base64;
import it.unisa.dia.gas.jpbc.Field;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.Element;

public class SecretInformation
{
    public static String KEY_ALPHA;
    public static String KEY_COMMITMENT;
    private Element mAlpha;
    private Element mCommitment;
    
    static {
        SecretInformation.KEY_ALPHA = "a";
        SecretInformation.KEY_COMMITMENT = "c";
    }
    
    public SecretInformation(final Element alpha, final Element commitment) {
        this.mAlpha = alpha;
        this.mCommitment = commitment;
    }
    
    public SecretInformation(final String input, final Pairing p) {
        final JSONObject json = (JSONObject)JSONValue.parse(input);
        final String alpha64 = json.get(SecretInformation.KEY_ALPHA);
        final String commitment64 = json.get(SecretInformation.KEY_COMMITMENT);
        this.mAlpha = this.ElementFromBase64(alpha64, p.getZr());
        this.mCommitment = this.ElementFromBase64(commitment64, p.getG1());
    }
    
    public String toJSON() {
        final JSONObject json = new JSONObject();
        json.put(SecretInformation.KEY_ALPHA, this.ElementToBase64(this.mAlpha));
        json.put(SecretInformation.KEY_COMMITMENT, this.ElementToBase64(this.mCommitment));
        return json.toString();
    }
    
    public Element getAlpha() {
        return this.mAlpha;
    }
    
    public String getAlphaBase64() {
        return this.ElementToBase64(this.mAlpha);
    }
    
    public Element getCommitment() {
        return this.mCommitment;
    }
    
    public String getCommitmentBase64() {
        return this.ElementToBase64(this.mCommitment);
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
