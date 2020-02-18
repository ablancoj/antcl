package cat.urv.gfa.loyalty.model;

import org.json.simple.JSONValue;
import org.json.simple.JSONObject;

public class SharedInformation
{
    public static String KEY_IDENTIFIER;
    public static String KEY_VALUE;
    public static String KEY_EXPIRY;
    private String mIdentifier;
    private String mValue;
    private String mExpiry;
    private String mConcat;
    
    static {
        SharedInformation.KEY_IDENTIFIER = "id";
        SharedInformation.KEY_VALUE = "v";
        SharedInformation.KEY_EXPIRY = "ex";
    }
    
    public SharedInformation(final String id, final String val, final String expiry) {
        this.mIdentifier = id;
        this.mValue = val;
        this.mExpiry = expiry;
        this.mConcat = String.valueOf(id) + val + expiry;
    }
    
    public SharedInformation(final String input) {
        final JSONObject json = (JSONObject)JSONValue.parse(input);
        this.mIdentifier = json.get(SharedInformation.KEY_IDENTIFIER);
        this.mValue = json.get(SharedInformation.KEY_VALUE);
        this.mExpiry = json.get(SharedInformation.KEY_EXPIRY);
        this.mConcat = String.valueOf(this.mIdentifier) + this.mValue + this.mExpiry;
    }
    
    public String toJSON() {
        final JSONObject json = new JSONObject();
        json.put(SharedInformation.KEY_IDENTIFIER, this.mIdentifier);
        json.put(SharedInformation.KEY_VALUE, this.mValue);
        json.put(SharedInformation.KEY_EXPIRY, this.mExpiry);
        return json.toString();
    }
    
    public String getIdentifier() {
        return this.mIdentifier;
    }
    
    public String getValue() {
        return this.mValue;
    }
    
    public String getExpiryDate() {
        return this.mExpiry;
    }
    
    public String getConcat() {
        return this.mConcat;
    }
}
