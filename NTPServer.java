import java.net.*;  
import java.io.*;  
import java.util.*;  
import java.lang.*;  
    
public class NTPServer {      
    private int NTPPort = 32000;  
    private byte[] NTPData = new byte[48];      
    // byte[] NTPData2;   
    private long seventyOffset;    //offset (in ms) between 1900 and 1970  
    private long transmitMillis;  
    
    //Offsets in NTPData for each timestamp  
    private final byte referenceOffset = 16;  
    private final byte originateOffset = 24;  
    private final byte receiveOffset = 32;  
    private final byte transmitOffset = 40;  
    private long transmitTimestamp;  
              
    private DatagramPacket NTPPacket;  
    private DatagramSocket NTPSocket;
    public static int count = 0;
        
    public NTPServer() {  
        try {  
            System.out.println("Server started!");
                            
            seventyOffset = 70*365; //days in 70 years  
            seventyOffset += 17;    //add days for leap years between 1900 and 1970  
            seventyOffset *= 24;    //hours in a day  
            seventyOffset *= 60;    //minutes in an hour  
            seventyOffset *= 60;    //seconds in a minute  
            seventyOffset *= 1000;  //milliseconds in a second    
                
            NTPPacket = new DatagramPacket(NTPData,NTPData.length);  
            NTPSocket = new DatagramSocket(NTPPort);

            while(true){       
                System.out.println("I'm back here!!!!");
                Thread myThread = new Thread();
                System.out.println("Thread: " + myThread.getName() + " | " + count);
                NTPSocket.receive(NTPPacket);
                System.out.println("I'm stuck here!!!");
                String rcvd = "from address:" + NTPPacket.getAddress() + ",port:" + NTPPacket.getPort();  
                System.out.println(rcvd);
                NTPData = NTPPacket.getData();   
                transmitTimestamp = toLong(transmitOffset);                  
                initPacket();  
                DatagramPacket echo = new DatagramPacket(NTPData,NTPData.length,NTPPacket.getAddress(),NTPPacket.getPort());  
                
                NTPSocket.send(echo);
                count++;
                System.out.println("I'm Here!!!!!!");
            }
        } catch (SocketException e) {  
            System.err.println("Can't open socket");  
            System.exit(1);  
        } catch (IOException e) {  
            System.err.println("Communication error!");  
            e.printStackTrace();  
        }  
    }

    // public class UDPServerThreads22 {
    //     public class UDPClientHandler1 implements Runnable {
    //         DatagramSocket ds;
    //         String sentence;
    //         InetAddress address;
    //         int port;
    //         long cstarttime;
    //         long cendtime;
         
    //         public UDPClientHandler1(DatagramSocket ds, String sentence, InetAddress address, int port, long cstarttime) {
    //             this.ds = ds;
    //             this.sentence = sentence;
    //             this.address = address;
    //             this.port = port;
    //             this.cstarttime = cstarttime;
    //         }
       
    //         public void run() {   
    //             byte[] sendData=new byte[1024];
    //             try{
    //                 String threadName = Thread.currentThread().getName();
    //                 String message = "in HandleClient";
    //                 System.out.format("%s: %s%n", threadName, message); //in thread run()
    //                 String capitalizedSentence = new String(sentence.toUpperCase());
    //                     sendData=capitalizedSentence.getBytes();
    //                 DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
    //                 ds.send(sendPacket);
    //                 System.out.println("in thread after send data to client");
    //                 System.out.println("IPAddress="+address+" port="+port);
    //                 long cendtime = System.currentTimeMillis();
    //                 System.out.println("time="+(cendtime-cstarttime));
    //             }catch (IOException e) {}
    //         }
    //     }

    //     public void nonStatic(DatagramSocket ds, String udpmessage, InetAddress address, int port, long stime) {
    //         Thread t = new Thread(new UDPClientHandler1(ds,udpmessage,address,port,stime));
    //         t.start();  
    //     }
    // }
    
    private void setOrigTime() {  
        toBytes(transmitTimestamp,originateOffset);             
    }  
    
    private void setReferenceTime() {  
        toBytes(transmitTimestamp,referenceOffset);   
    }  

    private void setReceiveTime() {  
        GregorianCalendar startCal = new GregorianCalendar();  
        long startMillis = startCal.getTimeInMillis();  
        transmitMillis = startMillis + seventyOffset;  
        toBytes(transmitMillis,receiveOffset);  
    }  
                
    private void setTransmitTime() {  
        GregorianCalendar startCal = new GregorianCalendar();  
        long startMillis = startCal.getTimeInMillis();  
        transmitMillis = startMillis + seventyOffset;  
        toBytes(transmitMillis,transmitOffset);  
    }  
                
                
    public void toBytes(long n, int offset) {  
        long intPart = 0;  
        long fracPart = 0;  
        intPart = n/1000;  
        fracPart = ((n % 1000) / 1000) * 0X100000000L;  
                
        NTPData[offset+0] = (byte)(intPart >>> 24);  
        NTPData[offset+1] = (byte)(intPart >>> 16);  
        NTPData[offset+2] = (byte)(intPart >>> 8);  
        NTPData[offset+3] = (byte)(intPart);  
                
        NTPData[offset+4] = (byte)(fracPart >>> 24);  
        NTPData[offset+5] = (byte)(fracPart >>> 16);  
        NTPData[offset+6] = (byte)(fracPart >>> 8);  
        NTPData[offset+7] = (byte)(fracPart);  
                
    }  
                
    public long toLong(int offset) {
        long intPart = ((((long) NTPData[offset+3]) & 0xFF)) + 
            ((((long) NTPData[offset+2]) & 0xFF) << 8) +  
            ((((long) NTPData[offset+1]) & 0xFF) << 16) +  
            ((((long) NTPData[offset+0]) & 0xFF) << 24);          
        long fracPart = ((((long) NTPData[offset+7]) & 0xFF)) +  
            ((((long) NTPData[offset+6]) & 0xFF) << 8) +  
            ((((long) NTPData[offset+5]) & 0xFF) << 16) +  
            ((((long) NTPData[offset+4]) & 0xFF) << 24);  
        long millisLong = (intPart * 1000) + (fracPart * 1000)/0X100000000L;  
                
        return millisLong;  
    }                     
        
    private void initPacket() {  
        try {
            NTPData[0] = 0x1C;  
            for(int i=1; i<16; i++) {
                NTPData[i] = 0;  
            }  
                    
            setReferenceTime();       
            setOrigTime();  
            setReceiveTime();  
            setTransmitTime();  
        } catch (Exception e) {}  
    }              
    public static void main(String[] args) throws SocketException {  
        // TODO: Add your code here         

        new NTPServer();
    }    
}  