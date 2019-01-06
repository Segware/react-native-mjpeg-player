package com.mjpeg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/*
 * I don't really understand and want to know what the hell it does!
 * Maybe one day I will refactor it ;-)
 * <p/>
 * https://code.google.com/archive/p/android-camera-axis
 */
public class MjpegInputStreamDefault extends MjpegInputStream {
    private static final String TAG = MjpegInputStream.class.getSimpleName();

    private final byte[] SOI_MARKER = {(byte) 0xFF, (byte) 0xD8};
    private final byte[] EOF_MARKER = {(byte) 0xFF, (byte) 0xD9};
    private final String CONTENT_LENGTH = "Content-Length";
    private final static int HEADER_MAX_LENGTH = 100;
    private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;
    private int mContentLength = -1;

    private OnFrameCapturedListener onFrameCapturedListener;

    private String date = "";
    private String time = "";

    // no more accessible
    MjpegInputStreamDefault(InputStream in) {
        super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
    }

    private int getEndOfSeqeunce(DataInputStream in, byte[] sequence) throws IOException {
        int seqIndex = 0;
        byte c;
        for (int i = 0; i < FRAME_MAX_LENGTH; i++) {
            c = (byte) in.readUnsignedByte();
            if (c == sequence[seqIndex]) {
                seqIndex++;
                if (seqIndex == sequence.length) {
                    return i + 1;
                }
            } else {
                seqIndex = 0;
            }
        }
        return -1;
    }

    private int getStartOfSequence(DataInputStream in, byte[] sequence) throws IOException {
        int end = getEndOfSeqeunce(in, sequence);
        return (end < 0) ? (-1) : (end - sequence.length);
    }

    private int parseContentLength(byte[] headerBytes) throws IOException, NumberFormatException {
        ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
        Properties props = new Properties();
        props.load(headerIn);
        return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
    }

    private void setDateTime(byte[] header) throws UnsupportedEncodingException {
        String headerString = new String(header, "UTF-8");
        String[] headerContent = headerString.split(("\r\n"));
        String frameDate = headerContent[headerContent.length - 2];
        String frameTime = headerContent[headerContent.length - 1];
        String[] date = frameDate.split(":");
        String[] time = frameTime.split(":");
        if(date[0].equals("DGF-FrameDate") && time[0].equals("DGF-FrameTime")){
            time[1] = time[1].substring(1, time[1].length()-4);
            date[1] = date[1].substring(1, date[1].length());
            if(!this.date.equals(date[1]) || !this.time.equals(time[1])){
                this.date = date[1];
                this.time = time[1];
            }
        }
    }

    public String getDate(){
        return this.date;
    }

    public String getTime(){
        return this.time;
    }

    // no more accessible
    Bitmap readMjpegFrame(int frameWidth, int frameHeight) throws IOException {
        mark(FRAME_MAX_LENGTH);
        int headerLen = getStartOfSequence(this, SOI_MARKER);
        reset();
        byte[] header = new byte[headerLen];
        readFully(header);
        setDateTime(header);
        try {
            mContentLength = parseContentLength(header);
        } catch (NumberFormatException nfe) {
            mContentLength = getEndOfSeqeunce(this, EOF_MARKER);
        }
        reset();
        byte[] frameData = new byte[mContentLength];
        skipBytes(headerLen);
        readFully(frameData);
        Bitmap bmp = BitmapFactory.decodeStream(new ByteArrayInputStream(frameData));
        return Bitmap.createScaledBitmap(bmp, frameWidth, frameHeight, true);
    }
}
