import java.net.*;
import java.io.*;

public class Packet_receiver extends Thread {

    public static void main(String[] args) throws Exception {
        System.out.println("Server Listening on 8888");
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            //server timeout 60 minutes
            serverSocket.setSoTimeout(1000 * 60 * 60);

            //Below method waits until client socket tries to connect
            Socket server = serverSocket.accept();

            //Read from client using input stream
            DataInputStream in = new DataInputStream(server.getInputStream());

            //converts data input stream to string
            String data = in.readUTF();

            //split the header and the payload
            String header = data.substring(0, 40);
            String payload = data.substring(40);
      
            //System.out.println("\n"+ payload);

            //split header into each part
            String[] headerArr = new String[10];

            //split into 10 chunks
            for (int i = 0; i < 10; i++){
                int start = i * 4;
                int end = start + 4;
                headerArr[i] = header.substring(start, end);
            }

            //call decode
            decode(headerArr, payload);

            //close the connection
            server.close();
        }
    }

    //decode function
    //does checksum
    //if not good, discard
    //otherwise decode payload
    //print payload with lengths
    public static void decode(String[] header, String payload) {
        
        String totalHeaderLen = header[1];
        String srcIP = header[6] + header[7];

        Utilities helper = new Utilities();

        //to make the checksum wrong, edit one thing
        //header[0] = "0000";

        //checksum
        int calculatedChecksum;
        calculatedChecksum = Integer.parseInt(helper.calculateChecksum(header));

        //error if checksum is not zero
        if (calculatedChecksum != 0){
            System.out.println("La v\u00E9rification de la somme de contr\u00F4le montre que le paquet re\u00E7u est corrompu. Paquet jet\u00E9!");
            return;
        }

        //else it is valid

        //parse ip address
        srcIP = helper.HextoIP(srcIP);

        //total length in octets
        int totalLengthOctets = Integer.parseInt(totalHeaderLen, 16);

        //parse payload length
        //find payload length = substract header length from total length
        int payloadLengthOctets = totalLengthOctets - 20;
        int payloadLengthBits = payloadLengthOctets * 8;

        //decode payload
        String decodedPayload = helper.HextoASCII(payload);

        //output
        StringBuilder output = new StringBuilder();
        output.append("Reçoit le flux de données et imprime à l'écran les données reçues avec le message suivant:\n");
        output.append("Les données reçues de " + srcIP + " sont " + decodedPayload + "\n");
        output.append("Les données ont " + payloadLengthBits + " bits ou " + payloadLengthOctets + " octets. La longueur totale du paquet est de " + totalLengthOctets + " octets.\n");
        output.append("La v\u00E9rification de la somme de contr\u00F4le confirme que le paquet re\u00E7u est authentique.\r");
    
        System.out.println(output.toString());
    }
}
		