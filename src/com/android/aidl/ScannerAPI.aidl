package com.android.aidl;
 
// Declare any non-default types here with import statements
 
/** Example service interface */
interface ScannerAPI {
   void	openScanner();
   void closeScanner();
   boolean  getScannerState() ;


   boolean	getTriggerLockState();
   boolean	lockTrigger() ;
   boolean	unlockTrigger() ;
   
   int getOutputMode();
   boolean	switchOutputMode(int mode);
   
   boolean	startDecode() ;
   boolean	stopDecode() ;
   
   
}