package com.zebra.adc.decoder;


public class DecodeResult {

   private String a = "";
   private byte[] scanResult = null;
   private String c = "";
   private Integer d = Integer.valueOf(0);
   private Long e = Long.valueOf(0L);
   
   public DecodeResult(byte[] var1, String var3) {
      this.scanResult = var1;
      this.c = var3;
     
     

   }
 
   public  byte[] getDecodeResult(){
	   
	   return  scanResult;
   }

 
   public String getBarcodeData() {
      return this.a;
   }

   public String getSymbology() {
      return this.c;
   }

}
