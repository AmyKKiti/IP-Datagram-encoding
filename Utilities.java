public class Utilities {
    
    //converts ascii string into hexa string
   public String ASCIItoHex(String inputStr) {
      
      StringBuilder hex = new StringBuilder();
      for (int i = 0; i < inputStr.length(); i++) {
         hex.append(Integer.toHexString(inputStr.charAt(i)));
      }
      return hex.toString();
   }


   //converts a hexa string into ascii string
   public String HextoASCII(String inputStr) {
   
      StringBuilder output = new StringBuilder();
      for (int i = 0; i < inputStr.length(); i+=2) {
         String str = inputStr.substring(i, i+2);
         output.append((char)Integer.parseInt(str, 16));
      }
      return output.toString();
   }


   public String calculateChecksum(String[] headerHexArray){

      int sum = 0;
      int hexValueInDecimal;

      //in a loop, we add the sum of all hex 
      for (int i = 0; i< headerHexArray.length; i++){
         hexValueInDecimal = Integer.parseInt(headerHexArray[i], 16); //convert to decimal
         sum = sum + hexValueInDecimal;
      }

      //find carry
      int checksum = sum % 65536;

      //add carry
      if (checksum != sum){
         String hexCarry = Integer.toHexString(sum);
         char carry = hexCarry.charAt(0);
         checksum += Character.getNumericValue(carry);
         // System.out.println(carry);
      }
         
      //calculate the complement
      String hexChecksum = Integer.toHexString(checksum);
      StringBuilder onesCompl = new StringBuilder();
      
      for (int i = 0; i < hexChecksum.length(); i++) {
         char c = hexChecksum.charAt(i); //get one hex
         int decimalValue = Integer.parseInt(Character.toString(c), 16); //turn to decimal
         int complement = 15 - decimalValue;  //substract from FFFF
         String complementHex = Integer.toHexString(complement); //convert back to hex
         onesCompl.append(complementHex); //append to new string
      }
      
      return onesCompl.toString();
   }

     
   public String IPtoHex(String ipAddr){

      String[] octet = ipAddr.split("\\.");
      StringBuilder hexaIP = new StringBuilder();

      for (String s : octet) {
         int decimal = Integer.parseInt(s);
         String hex = String.format("%02X", decimal);
         hexaIP.append(hex);
      }

      return hexaIP.toString();
   }

   public String HextoIP(String hexAddr){

      StringBuilder ipAddr = new StringBuilder();

       for(int i = 0; i < hexAddr.length(); i = i + 2) {
           ipAddr.append(Integer.valueOf(hexAddr.substring(i, i+2), 16) + ".");
        }
        
        //concat last dot
        String ipString = ipAddr.substring(0, ipAddr.length()-1);
        return ipString;
   }

   //for debugging purposes
   public static void main(String[] args) {
      
      String hex = "434F4C554D424941";
      String str = "COLUMBIA";

      Utilities myUtility = new Utilities();

      System.out.println(myUtility.ASCIItoHex(str)); 
      System.out.println();
      System.out.println(myUtility.HextoASCII(hex)); 
      System.out.println();
      
      //integer to hexa, should output 3C9
      System.out.println(Integer.toHexString(969)); 
      System.out.println();

      //hexa to integer, should output 969
      System.out.println(Integer.parseInt("3C9",16));

      System.out.println(" ---------");

      //checksum test
      String[] check = {"4500", "001C", "1C46", "4000", "4006", "0000", "C0A8", "0003", "C0A8", "0001"};
      System.out.println(myUtility.calculateChecksum(check));
   
      System.out.println();
      System.out.println(myUtility.IPtoHex("192.168.0.1"));

      System.out.println();
      System.out.println(String.format("%04X", 40));

      System.out.println();
      System.out.println(myUtility.HextoIP("C0A80003"));
   }
}
