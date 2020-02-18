package cat.urv.gfa.loyalty.protocol;

import cat.urv.gfa.loyalty.model.Signature;
import cat.urv.gfa.loyalty.model.SecretInformation;
import cat.urv.gfa.loyalty.model.Token;
import java.io.IOException;
import cat.urv.gfa.loyalty.utils.Utils;
import cat.urv.gfa.loyalty.model.SharedInformation;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.PairingPreProcessing;
import it.unisa.dia.gas.jpbc.Pairing;

public class Vendor
{
    private Pairing e;
    private PairingPreProcessing ppp;
    private Field G1;
    private Field Zq;
    private Element g;
    private Element sk;
    private Element pk;
    private String vendorId;
    
    public Vendor(final String id, final Pairing e, final Element g, final Element sk) {
        this.vendorId = id;
        this.e = e;
        this.g = g.getImmutable();
        this.ppp = e.getPairingPreProcessingFromElement(g);
        this.G1 = e.getG1();
        this.Zq = e.getZr();
        this.sk = sk;
        this.pk = g.powZn(sk).getImmutable();
    }
    
    public SharedInformation getSharedInfo(final String value, final String expiry) {
        final SharedInformation c = new SharedInformation(this.vendorId, value, expiry);
        return c;
    }
    
    public Element sign(final Element u, final SharedInformation c) throws IOException {
        Element exp = Utils.hash(String.valueOf(c.getIdentifier()) + c.getValue() + c.getExpiryDate(), this.Zq);
        exp = exp.add(this.sk).getImmutable();
        exp = exp.invert().getImmutable();
        final Element v = u.powZn(exp);
        return v;
    }
    
    public String verify(final Token token) {
        final SharedInformation c = token.getSharedInformation();
        final SecretInformation m = token.getSecretInformation();
        final Signature s = token.getSignature();
        final String concatC = String.valueOf(c.getIdentifier()) + c.getValue() + c.getExpiryDate();
        final String concatCM = String.valueOf(concatC) + Utils.ElementToBase64(m.getAlpha()) + Utils.ElementToBase64(m.getCommitment());
        final Element e1 = this.e.pairing(this.g.powZn(Utils.hash(concatC, this.Zq)).mul(this.pk), s.getSignature());
        final Element e2 = this.ppp.pairing(Utils.hash(concatCM, this.G1));
        return e1.equals(e2) ? "ok" : "stop";
    }
    
    public Element zkpTwo() {
        return this.Zq.newRandomElement().getImmutable();
    }
    
    public String zkpVerify(final Token token, final Element sc, final Element r, final Element test) {
        final SharedInformation c = token.getSharedInformation();
        final SecretInformation m = token.getSecretInformation();
        final String concatCA = String.valueOf(c.getIdentifier()) + c.getValue() + c.getExpiryDate() + m.getAlphaBase64();
        final Element h = Utils.hash(concatCA, this.G1);
        final Element tl = h.powZn(r);
        final Element tr = test.mul(m.getCommitment().powZn(sc));
        return tl.equals(tr) ? "Accept" : "Reject";
    }
    
    public Field getG1() {
        return this.G1;
    }
    
    public Field getZq() {
        return this.Zq;
    }
    
    public Pairing getPairing() {
        return this.e;
    }
}
