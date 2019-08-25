package com.huari.tools;

public class MyTools {

    public static String toCountString(String s, int count) {
        String t = null;
        try {
            byte[] b = s.getBytes();
            byte[] c = new byte[count];
            if (b.length < count) {
                for (int i = 0; i < b.length; i++) {
                    c[i] = b[i];
                }
                for (int n = b.length; n < count; n++) {
                    c[n] = '\0';
                }
            } else {
                for (int i = 0; i < count; i++) {
                    c[i] = b[i];
                }
            }
            t = new String(c);
        } catch (Exception e) {
            System.out.println("toCountString（）中出现异常");
        }
        return t;
    }

    public static byte[] int2ByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    public static double byteToDouble(byte[] b) {

        long l;
        l = b[0];
        l &= 0xff;
        l |= ((long) b[1] << 8);
        l &= 0xffff;
        l |= ((long) b[2] << 16);
        l &= 0xffffff;
        l |= ((long) b[3] << 24);
        l &= 0xffffffffl;
        l |= ((long) b[4] << 32);
        l &= 0xffffffffffl;

        l |= ((long) b[5] << 40);
        l &= 0xffffffffffffl;
        l |= ((long) b[6] << 48);
        l &= 0xffffffffffffffl;

        l |= ((long) b[7] << 56);

        return Double.longBitsToDouble(l);
    }

    public static long fourBytesToLong(byte[] b) {
        int intValue = 0;
        long f = 0;
        int c = (b[0] & 0xff) << 24;
        if (c < 0) {
            f = (long) (c + Math.pow(2, 32));
        } else {
            f = c;
        }
        for (int i = 1; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue + f;
    }

    public static int fourBytesToInt(byte[] b) {
        int intValue = 0;
        int c = (b[0] & 0xff) << 24;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
    }

    public static int bytesToIntLittle(byte[] src) {
        int value;
        value = ((src[0] & 0xFF)
                | ((src[1] & 0xFF) << 8)
                | ((src[2] & 0xFF) << 16)
                | ((src[3] & 0xFF) << 24));
        return value;
    }


    public static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }

    public static byte[] hexToByteArray(String inHex){//16进制转byte
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=hexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }



    public static String bytesToHex(byte[] bytes) {//bytes转16进制
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    public static int nifourBytesToInt(byte[] a) {
        int intValue = 0;
        byte[] b = new byte[a.length];
        for (int i = 0; i < 4; i++) {
            b[i] = a[3 - i];
        }
        int c = (b[0] & 0xff) << 24;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
    }

    public static byte[] getPartByteArray(byte[] b, int start, int stop) {
        byte[] c = new byte[stop - start + 1];
        for (int i = start; i <= stop; i++) {
            c[i - start] = b[i];
        }
        return c;
    }

    public static byte[] nigetPartByteArray(byte[] b, int start, int stop) {
        byte[] c = new byte[stop - start + 1];
        for (int i = stop; i >= start; i--) {
            c[stop - i] = b[i];
        }
        return c;
    }

    public static byte[] nigetPartByteArrayfan(byte[] b, int start, int stop) {
        byte[] c = new byte[stop - start + 1];
        for (int i = start; i <= stop; i++) {
            c[stop - i] = b[i];
        }
        return c;
    }

    public static byte[] reversebytes(byte[] a) {
        byte[] b = new byte[a.length];
        int l = a.length;
        for (int i = 0; i < l; i++) {
            b[i] = a[l - 1 - i];
        }
        return b;
    }

    public static short twoBytesToShort(byte[] b) {
        return (short) (((b[0] & 0xff) << 8) | (b[1] & 0xff));
    }

}
