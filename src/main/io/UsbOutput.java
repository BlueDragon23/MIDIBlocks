package io;

import com.fazecast.jSerialComm.SerialPort;

import MIDIBlocks.MusicManager;
import MIDIBlocks.Note;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class UsbOutput {

    private MusicManager musicManager;
    
    private SerialPort comPort;
    private OutputStream out;
    
    private boolean isConnected;

    // set the speed of the serial port
    public static int speed = 19200;

    

    public UsbOutput(MusicManager musicManager) {
        this.musicManager = musicManager;
        tryConnection();
    }

    public boolean isWorking() {
        return isConnected;
    }
    
    public boolean tryConnection() {
        SerialPort[] commPorts = SerialPort.getCommPorts();
        if (commPorts.length > 0) {
            comPort = commPorts[0];
            isConnected = comPort.openPort();
            if (isConnected) {
                comPort.setBaudRate(speed);
                out = comPort.getOutputStream();
                hardwareConfig();
            }
        } else {
            isConnected = false;
        }
        return isConnected;
    }

    /**
     * Output a note over serial
     * 
     * @param note
     */
    public void playNote(Note note) {
        ArrayList<Byte> data = getBytes(note);
        byte[] bytes = new byte[data.size()];
        int i = 0;
        for (Byte b : data) {
            bytes[i++] = b.byteValue();
        }
        /* Write to serial */
        try {
            out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert a list of notes to their Byte representation
     * 
     * @param queue
     * @return
     */
    public ArrayList<Byte> getBytes(ArrayList<Note> queue) {
        ArrayList<Byte> bytes = new ArrayList<>();
        Byte message;
        byte bits;
        for (Note note : queue) {
            int statusBool = note.getStatus() == Note.NOTE_ON ? 1 : 0;
            bits = (byte) (statusBool * 128 + note.getPitch());
            message = new Byte(bits);
            bytes.add(message);
        }
        return bytes;
    }

    /**
     * Convert a single note to a list containing a single Byte
     * 
     * @param note
     * @return
     */
    public ArrayList<Byte> getBytes(Note note) {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(note);
        return getBytes(notes);
    }

    /**
     * Return an array of Bytes representing a hardware configuration message
     * 
     * @return
     */
    public void hardwareConfig() {
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 255;
        int i = 1;
        if (musicManager.getKeyboard() == null) {
            return;
        }
        for (Integer num : musicManager.getKeyboard().getFirstEightNotes()) {
            bytes[i++] = ((Integer) (num + 0b10000000)).byteValue();
        }
        bytes[9] = (byte) 255;
        for (int j = 0; j < 10; j++) {
            System.out.println(0xff & bytes[j]);
        }
        try {
            out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
