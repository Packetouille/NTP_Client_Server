import java.io.*;
import java.net.*;
import java.util.*;

public class UDPServerThreads22 {

  public class UDPClientHandler1 implements Runnable {
   
   DatagramSocket ds;
   String sentence;
   InetAddress address;
   int port;
   long cstarttime;
   long cendtime;
   
   public UDPClientHandler1(DatagramSocket ds, String sentence, InetAddress address, int port, long cstarttime) {
    this.ds=ds;
	this.sentence=sentence;
    this.address=address;
    this.port=port;
	this.cstarttime=cstarttime;
   }
 
    public void run() {   
     byte[] sendData=new byte[1024];
     try{
      String threadName =
      Thread.currentThread().getName();
      String message="in HandleClient";
      System.out.format("%s: %s%n", threadName, message); //in thread run()
      String capitalizedSentence=new String(sentence.toUpperCase());
      sendData=capitalizedSentence.getBytes();
      DatagramPacket sendPacket=
        new DatagramPacket(sendData, sendData.length, address, port);
      ds.send(sendPacket);
      System.out.println("in thread after send data to client");
	  System.out.println("IPAddress="+address+" port="+port);
      long cendtime = System.currentTimeMillis();
      System.out.println("time="+(cendtime-cstarttime));
     }
     catch (IOException e) {}
    }
 }

  public void nonStatic(DatagramSocket ds, String udpmessage, InetAddress address, int port, long stime) {
   Thread t = new Thread(new UDPClientHandler1(ds,udpmessage,address,port,stime));
     t.start();  
  }
  
 public static void main(String args[]) throws Exception
  {  
    UDPServerThreads22 udpserver= new UDPServerThreads22();
    try {
     DatagramSocket serverSocket=new DatagramSocket(9876);
     byte[] receiveData=new byte[1024];
     int count=0;
     while(true)
     {
      DatagramPacket receivePacket=
       new DatagramPacket(receiveData, receiveData.length);
      serverSocket.receive(receivePacket); //ntp request
      String udpmessage=new String(receivePacket.getData());
      InetAddress address=receivePacket.getAddress();
      int port=receivePacket.getPort();
	  long cstarttime = System.currentTimeMillis();
      udpserver.nonStatic(serverSocket,udpmessage,address,port,cstarttime);//thread
      count++;
      System.out.println("after start thread "+count);    
     }
   }
   catch (IOException e) {} 
 }
}