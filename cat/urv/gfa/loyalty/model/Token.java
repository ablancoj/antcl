package cat.urv.gfa.loyalty.model;

import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import it.unisa.dia.gas.jpbc.Pairing;

public class Token
{
    public static String KEY_SHARED_INFORMATION;
    public static String KEY_SECRET_INFORMATION;
    public static String KEY_SIGNATURE;
    private SharedInformation mSharedInformation;
    private SecretInformation mSecretInformation;
    private Signature mSignature;
    
    static {
        Token.KEY_SHARED_INFORMATION = "pi";
        Token.KEY_SECRET_INFORMATION = "si";
        Token.KEY_SIGNATURE = "s";
    }
    
    public Token(final SharedInformation c, final SecretInformation m, final Signature s) {
        this.mSharedInformation = c;
        this.mSecretInformation = m;
        this.mSignature = s;
    }
    
    public Token(final String input, final Pairing p) {
        final JSONObject json = (JSONObject)JSONValue.parse(input);
        this.mSharedInformation = new SharedInformation(json.get(Token.KEY_SHARED_INFORMATION));
        this.mSecretInformation = new SecretInformation(json.get(Token.KEY_SECRET_INFORMATION), p);
        this.mSignature = new Signature(json.get(Token.KEY_SIGNATURE), p);
    }
    
    public SharedInformation getSharedInformation() {
        return this.mSharedInformation;
    }
    
    public SecretInformation getSecretInformation() {
        return this.mSecretInformation;
    }
    
    public Signature getSignature() {
        return this.mSignature;
    }
    
    public String toJSON() {
        final JSONObject json = new JSONObject();
        json.put(Token.KEY_SHARED_INFORMATION, this.mSharedInformation.toJSON());
        json.put(Token.KEY_SECRET_INFORMATION, this.mSecretInformation.toJSON());
        json.put(Token.KEY_SIGNATURE, this.mSignature.toJSON());
        return json.toString();
    }
}
