/**
 * interface to invensense demokit standard firmware.
 */
package pers.hal42.android;

import android.bluetooth.BluetoothDevice;

import pers.hal42.android.Logger;

import java.io.IOException;


public class RfcommDevice implements Runnable {
//  public static boolean lograw = false;

  public Logger log;

  public Snooper logFilter = null;

  //bluetooth connection wrapper
  public Connection tooth;

  //thread control, visible for debug
  public boolean beRunning;

  //cached address from tooth
  protected String btAddress;
  //cached bluetooth streams:
  private java.io.InputStream reader;
  protected java.io.OutputStream writer;


  public boolean wraps(BluetoothDevice btDevice) {
    return btAddress.equals(btDevice.getAddress());
  }

  public interface Snooper {
    /**
     * if false is returned then the associated update will NOT be logged locally
     */
    boolean updated(int read);
  }

  public interface Connection {
    boolean open(Runnable sensor);  //clue to open read and write streams

    //@return reader stream
    java.io.InputStream reader() throws IOException;

    java.io.OutputStream writer() throws IOException;

    String addressString();
  }


  public String addressString() {
    if (btAddress != null) {
      return btAddress;
    }
    if (tooth != null) {
      return tooth.addressString();
    }
    return "no address";
  }


  public void disconnect() {
    //kill reader thread
    beRunning = false;
    //close writer at will
    if (writer != null) {
      try {
        writer.close();
      } catch (java.io.IOException e) {
        e.printStackTrace();
      }
    }
  }

  public RfcommDevice(Connection dent) {
    if (dent != null) {
      tooth = dent;
      btAddress = tooth.addressString();
    }
    log = new Logger(addressString());
  }


  //it is expected that this will be called by the tooth on connection
  @Override
  public void run() {
    try {
      reader = tooth.reader();
      if (reader == null) {
        return;
      }
      writer = tooth.writer();
    } catch (IOException e) {
      log.e(e.getMessage());
    }
    beRunning = true;


    while (beRunning) {
      try {
        int one = reader.read();
        if (one < 0) {
          beRunning = false; //bail out on end-0f-stream
          break;
        }
        //todo: display 'one' in text window
        logFilter.updated(one);
      } catch (java.io.IOException e1) {
        e1.printStackTrace();
      }
    }

    //we don't own 'out' so we can't close it, it might be shared.
    try {
      reader.close();
      if (writer != null) {
        writer.close();
      }
    } catch (java.io.IOException e) {
      e.printStackTrace();
    }
    reader = null;
    writer = null;
  } /* run */

  public boolean connectLogger(Snooper logToothDroid) {
    logFilter = logToothDroid;
    return tooth != null && tooth.open(this);
  } /* connectLogger */


  public boolean sendCommand(int asciiCommandCode) {
    if (writer == null) {
      return false;
    }
    try {
      writer.write(asciiCommandCode);
      return true;
    } catch (java.io.IOException e) {
      log.e(e.getMessage());
      return false;
    }
  }
}
