package cat.urv.gfa.loyalty.communication;

public class Constants
{
    public static final int MAX_LENGTH = 250;
    public static final int ZERO = 0;
    public static final byte[] AID_ANDROID;
    public static final int CLA = 0;
    public static final int INS_SELECT = 164;
    public static final int INS_STORE = 1;
    public static final int INS_RETRIEVE = 2;
    public static final int INS_MORE = 3;
    public static final int INS_SEND_PARAMS = 176;
    public static final int INS_SEND_GENERATOR = 177;
    public static final int INS_SEND_SK = 178;
    public static final int INS_SEND_SHARED_INFO = 192;
    public static final int INS_SEND_BLIND_SIG = 193;
    public static final int INS_REQ_TOKEN = 208;
    public static final int INS_CONT_VERIFY = 209;
    public static final int INS_STOP_VERIFY = 210;
    public static final int INS_CHALLENGE = 211;
    public static final int INS_TOKEN_OK = 212;
    public static final int INS_TOKEN_NOK = 213;
    public static final int P1_SELECT = 4;
    public static final int P2_SELECT = 4;
    public static final int SW1_OK = 144;
    public static final int SW1_NOK = 98;
    public static final int SW1_MORE = 128;
    public static final int SW1_LONG_RESPONSE = 112;
    public static final int SW2_OK = 0;
    
    static {
        AID_ANDROID = new byte[] { -16, 65, 76, 66, 69, 82, 84 };
    }
    
    private Constants() {
    }
}
