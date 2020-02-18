package cat.urv.gfa.loyalty.communication;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import cat.urv.gfa.loyalty.model.Token;
import cat.urv.gfa.loyalty.model.SharedInformation;
import it.unisa.dia.gas.jpbc.Element;
import cat.urv.gfa.loyalty.utils.Utils;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import cat.urv.gfa.loyalty.protocol.Vendor;
import javax.smartcardio.CardChannel;

public class VendorSession
{
    private CardChannel mCardChannel;
    private Vendor mVendor;
    
    public VendorSession(final Vendor v, final CardChannel cardChannel) {
        this.mCardChannel = cardChannel;
        this.mVendor = v;
    }
    
    public void select() throws CardException {
        CommandAPDU apdu = null;
        ResponseAPDU response = null;
        apdu = new CommandAPDU(0, 164, 4, 4, Constants.AID_ANDROID);
        response = this.mCardChannel.transmit(apdu);
        if (response.getSW1() != 144) {
            throw new CardException("Select failed. RESPONSE: " + byteArrayToHex(response.getBytes()));
        }
    }
    
    public void store(final byte[] data) throws CardException, IOException {
        this.sendApdu(this.mCardChannel, (byte)0, (byte)1, data);
    }
    
    public byte[] retrieve() throws CardException, IOException {
        return this.sendApdu(this.mCardChannel, (byte)0, (byte)2, null);
    }
    
    public byte[] sendPublicParameters(final File params) throws IOException, CardException {
        final BufferedReader input = new BufferedReader(new FileReader(params));
        String buffer = "";
        String in;
        while ((in = input.readLine()) != null) {
            buffer = String.valueOf(buffer) + in + "\n";
        }
        input.close();
        final byte[] data = buffer.getBytes();
        final byte[] response = this.sendApdu(this.mCardChannel, (byte)0, (byte)(-80), data);
        return response;
    }
    
    public byte[] sendGenerator(final File generator) throws IOException, CardException {
        final BufferedReader input = new BufferedReader(new FileReader(generator));
        String buffer = "";
        String in;
        while ((in = input.readLine()) != null) {
            buffer = String.valueOf(buffer) + in + "\n";
        }
        input.close();
        final byte[] data = buffer.getBytes();
        final byte[] response = this.sendApdu(this.mCardChannel, (byte)0, (byte)(-79), data);
        return response;
    }
    
    public void sendSecretKey() throws CardException {
        final Element sk = this.mVendor.getZq().newRandomElement();
        final byte[] data = Utils.ElementToBase64(sk).getBytes();
        CommandAPDU apdu = null;
        ResponseAPDU response = null;
        apdu = new CommandAPDU(0, 178, 0, 0, data);
        response = this.mCardChannel.transmit(apdu);
        if (response.getSW1() == 98) {
            throw new CardException("Error sending secret key.");
        }
    }
    
    public Element sendSharedInfo(final SharedInformation sharedInfo) throws CardException, IOException {
        final byte[] data = sharedInfo.toJSON().getBytes();
        final byte[] response = this.sendApdu(this.mCardChannel, (byte)0, (byte)(-64), data);
        final String sData = new String(response);
        final Element blindedMessage = Utils.ElementFromBase64(sData, this.mVendor.getG1());
        return blindedMessage;
    }
    
    public void sign(final SharedInformation sharedInfo, final Element blindedMessage) throws IOException, CardException {
        final Element blindSignature = this.mVendor.sign(blindedMessage, sharedInfo);
        final byte[] data = Utils.ElementToBase64(blindSignature).getBytes();
        this.sendApdu(this.mCardChannel, (byte)0, (byte)(-63), data);
    }
    
    public Token requestToken() throws CardException, IOException {
        final byte[] data = this.sendApdu(this.mCardChannel, (byte)0, (byte)(-48), null);
        final Token t = new Token(new String(data), this.mVendor.getPairing());
        return t;
    }
    
    public Element verifyToken(final Token t) throws CardException, IOException {
        final String result = this.mVendor.verify(t);
        if (result.equals("ok")) {
            System.out.println("Signature verification OK");
            final byte[] response = this.sendApdu(this.mCardChannel, (byte)0, (byte)(-47), null);
            return Utils.ElementFromBase64(new String(response), this.mVendor.getG1());
        }
        System.out.println("Signature verification Failed");
        final byte[] response = this.sendApdu(this.mCardChannel, (byte)0, (byte)(-46), null);
        return null;
    }
    
    private byte[] sendApdu(final CardChannel channel, final byte CLA, final byte INS, final byte[] data) throws CardException, IOException {
        ResponseAPDU response = null;
        if (data == null) {
            final CommandAPDU apdu = new CommandAPDU(CLA, INS, 0, 0);
            response = channel.transmit(apdu);
        }
        else {
            int length = data.length;
            for (int totalFragments = (int)Math.ceil(length / 250.0), currentFragment = 0; currentFragment < totalFragments; ++currentFragment) {
                final int toSend = Math.min(length, 250);
                final int from = currentFragment * 250;
                final int to = from + toSend;
                final byte[] fragment = Arrays.copyOfRange(data, from, to);
                final CommandAPDU apdu = new CommandAPDU(CLA, INS, currentFragment + 1, totalFragments, fragment);
                response = channel.transmit(apdu);
                length -= toSend;
                if (response.getSW1() != 128) {
                    break;
                }
            }
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (response.getSW1() == 112) {
            baos.write(response.getData());
            response = channel.transmit(new CommandAPDU(0, 3, 0, 0));
        }
        if (response.getSW1() == 144) {
            baos.write(response.getData());
        }
        if (response.getSW1() == 98) {
            return null;
        }
        return baos.toByteArray();
    }
    
    public static String byteArrayToHex(final byte[] a) {
        final StringBuilder sb = new StringBuilder(a.length * 2);
        for (final byte b : a) {
            sb.append(String.format("%02X ", b & 0xFF));
        }
        return sb.toString();
    }
}
