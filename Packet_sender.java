import java.net.*;
import java.io.*;

public class Packet_sender {

    public static void main(String[] args) throws Exception{

        //try to connect to server - localhost @ port 8888
        Socket client = new Socket("localhost",8888);
        //if server is not listening - You will get Exception
        // java.net.ConnectException: Connection refused: connect

        String ip = "";
        String payload = "";
        //accept in put data
        if (args.length > 0){
            //read data
            ip = args[0];
            payload = args[1];
        }else{
            System.out.println("No arguments found");
        }

        //find source IP
        InetAddress localHost = InetAddress.getLocalHost();
        String localIPAddr = localHost.getHostAddress();

        //System.out.println(localIPAddr);

        //call encode 
        String codeword = encode(payload, localIPAddr, ip);

        //System.out.println(codeword); //print out what is being sent for testing

        //write to server using output stream
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        out.writeUTF(codeword);

        //close the connection
        client.close();
    }

    //encode function 
    //find checksum
    //stick everything together and send
    //returns the header + payload
    public static String encode(String payload, String sourceIP, String destinationIP) {
        //contains the component of the header
        String[] headerArr = new String[10];
      
        int totalHeaderLen;

        String srcIP;
        String destIP;

        String codedPayload;
        int codedPayloadLength;

        String checksum;

        Utilities helper = new Utilities();

        //convert payload string to hex
        codedPayload = helper.ASCIItoHex(payload);
        codedPayloadLength = codedPayload.length();

       // System.out.println(codedPayload);
        //System.out.println(codedPayloadLength);

        //check if payload is a multiple of 8, otherwise do padding
        if (codedPayloadLength % 8 != 0){
            int nextMultipleOfEight = ((codedPayloadLength + 7) / 8) * 8;
            for (int i = 0; i < nextMultipleOfEight - codedPayloadLength; i++){
                codedPayload = codedPayload + "0";
            }
        }
        
        //update length
        codedPayloadLength = codedPayload.length();

        //calculate total header length in octets
        totalHeaderLen = codedPayloadLength/2 + 20; //header is fixed at 20 octets
        String hexaHeaderLen = String.format("%04X", totalHeaderLen);
        
        //calculate IP in hex
        srcIP = helper.IPtoHex(sourceIP);
        destIP = helper.IPtoHex(destinationIP);

        //stick all together

        headerArr[0] = "4500";
        headerArr[1] = hexaHeaderLen; //header length
        headerArr[2] = "1C46";
        headerArr[3] = "4000";
        headerArr[4] = "4006";
        headerArr[5] = "0000"; //checksum
        headerArr[6] = srcIP.substring(0,4);
        headerArr[7] = srcIP.substring(4);
        headerArr[8] = destIP.substring(0,4);
        headerArr[9] = destIP.substring(4);

        //find checksum 
        checksum = helper.calculateChecksum(headerArr);

        //replace
        headerArr[5] = checksum;

        return String.join("", headerArr) + codedPayload;
    } 
}