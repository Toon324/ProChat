package proc;

import java.io.IOException;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoProcessorException;
import javax.media.NotRealizedError;
import javax.media.Processor;
import javax.media.control.FormatControl;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;

//Author: Cody
/*
public class VoiceCall implements ControllerListener {

	private MediaLocator locator;
	private String ipAddress;
	private String port;

	private Processor processor = null;
	private DataSink rtptransmitter = null;
	private javax.media.protocol.DataSource dataOutput = null;

	public VoiceCall(MediaLocator locator, String ipAddress, String port) {
		this.locator = locator;
		this.ipAddress = ipAddress;
		this.port = port;
		try {
			File f = new File("output.wav");
			f.createNewFile();
			dataOutput = Manager.createDataSource(new URL("file:/C:/Users/Cody/Programming/Programming/ProChat/proChat/output.wav"));
			 System.out.println("Scanning for devices ...");  
		        Vector deviceList = CaptureDeviceManager.getDeviceList(null);  
		         
		        System.out.format("%1$s devices detected.\n", deviceList.size());  
		        for (Object device : deviceList)  
		        {  
		            System.out.println(device);  
		        }  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public synchronized String start() {
		createProcessor();
		String s = createTransmitter();
		processor.start();
		return s;
	}

	public void stop() {
		processor.stop();
		processor.close();
		processor = null;
		rtptransmitter.close();
		rtptransmitter = null;
	}

	private String createProcessor() {
		try {
			javax.media.protocol.DataSource ds = Manager
					.createDataSource(locator);
			processor = Manager.createProcessor(ds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		processor.addControllerListener(this);
		processor.configure();
		
		
		return "processed";
	}

	private String createTransmitter() {
		String rtpURL = "rtp://" + ipAddress + ":" + port + "/audio";
		MediaLocator outputLocator = new MediaLocator(rtpURL);
		try {
			System.out.println("DataOutput: " + dataOutput.getContentType());
			rtptransmitter = Manager.createDataSink(Manager.createDataSource(locator), outputLocator);
			rtptransmitter.open();
			rtptransmitter.start();
			dataOutput.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtpURL;
	}
	
	public static void main(String [] args) {
        VoiceCall vt = new VoiceCall(new MediaLocator("javasound://0"),"129.89.185.120", "2222");	
        String result = vt.start();
        System.out.println("result: " + result);
        try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        vt.stop();
    }

	/* (non-Javadoc)
	 * @see javax.media.ControllerListener#controllerUpdate(javax.media.ControllerEvent)
	 */
/*
	@Override
	public void controllerUpdate(ControllerEvent event) {
		System.out.println("Event: " + event);
		if (event.toString().contains("ConfigureComplete")) {
			TrackControl [] tracks = processor.getTrackControls();
			// Search through the tracks for a Audio track
			for (int i = 0; i < tracks.length; i++) {
			    Format format = tracks[i].getFormat();
			    if (  tracks[i].isEnabled() &&  format instanceof AudioFormat) {
			        AudioFormat ulawFormat =   new AudioFormat(AudioFormat.DVI_RTP);                                                       
				tracks[i].setFormat (ulawFormat);				
			    } else
				tracks[i].setEnabled(false);
			}
			
			// Set the output content descriptor to RAW_RTP
			ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
			processor.setContentDescriptor(cd);

			dataOutput = processor.getDataOutput();
		}
	}
}
*/

public class VoiceCall {
	
	public VoiceCall() {
		
	}
	
	public void call() {
		 //final String urlStr = URLUtils.createUrlStr(new File("samplemedia/gulp2.wav"));//"file://samplemedia/gulp2.wav";
	    Format format;

	    format = new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1);
	    //format = new AudioFormat(AudioFormat.ULAW_RTP, 8000.0, 8, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
	    //format = new AudioFormat(BonusAudioFormatEncodings.ALAW_RTP, 8000, 8, 1);
	    //format = new AudioFormat(BonusAudioFormatEncodings.SPEEX_RTP, 8000, 8, 1, -1, AudioFormat.SIGNED);
	    //format = new AudioFormat(BonusAudioFormatEncodings.ILBC_RTP, 8000.0, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);

	    CaptureDeviceInfo di = null;
	            //Set to true if you want to transmit audio from capture device, like microphone.
	    if (true)
	    {
	        // First find a capture device that will capture linear audio
	        // data at 8bit 8Khz
	        AudioFormat captureFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 8, 1);

	        Vector devices = CaptureDeviceManager.getDeviceList(null);



	        if (devices.size() > 0)
	        {
	            di = (CaptureDeviceInfo) devices.elementAt(0);
	        } else
	        {
	            System.err.println("No capture devices");
	            // exit if we could not find the relevant capturedevice.
	            System.exit(-1);

	        }
	    }

	    // Create a processor for this capturedevice & exit if we
	    // cannot create it
	    Processor processor = null;
	    try
	    {
	        //processor = Manager.createProcessor(new MediaLocator(urlStr));
	                    processor = Manager.createProcessor(di.getLocator());
	    } catch (IOException e)
	    {
	        e.printStackTrace();
	        System.exit(-1);
	    } catch (NoProcessorException e)
	    {
	        e.printStackTrace();
	        System.exit(-1);
	    }

	    // configure the processor
	    processor.configure();

	    while (processor.getState() != Processor.Configured)
	    {
	        try
	        {
	            Thread.sleep(10);
	        } catch (InterruptedException e)
	        {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }

	    processor.setContentDescriptor(new ContentDescriptor(ContentDescriptor.RAW_RTP));

	    TrackControl track[] = processor.getTrackControls();

	    boolean encodingOk = false;

	    // Go through the tracks and try to program one of them to
	    // output g.711 data.

	    for (int i = 0; i < track.length; i++)
	    {
	        if (!encodingOk && track[i] instanceof FormatControl)
	        {
	            if (((FormatControl) track[i]).setFormat(format) == null)
	            {

	                track[i].setEnabled(false);
	            } else
	            {
	                encodingOk = true;
	            }
	        } else
	        {
	            // we could not set this track to g.711, so disable it
	            track[i].setEnabled(false);
	        }
	    }

	    // At this point, we have determined where we can send out
	    // g.711 data or not.
	    // realize the processor
	    if (encodingOk)
	    {
	        if (!new net.sf.fmj.ejmf.toolkit.util.StateWaiter(processor).blockingRealize())
	        {
	            System.err.println("Failed to realize");
	            return;
	        }

	        // get the output datasource of the processor and exit
	        // if we fail
	        javax.media.protocol.DataSource ds = null;

	        try
	        {
	            ds = processor.getDataOutput();
	        } catch (NotRealizedError e)
	        {
	            e.printStackTrace();
	            System.exit(-1);
	        }

	        // hand this datasource to manager for creating an RTP
	        // datasink our RTP datasink will multicast the audio
	        try
	        {
	            String url = "rtp://192.168.1.99:49150/audio/1";

	            MediaLocator m = new MediaLocator(url);

	            DataSink d = Manager.createDataSink(ds, m);
	            d.open();
	            d.start();

	            System.out.println("Starting processor");
	            processor.start();
	            Thread.sleep(30000);
	        } catch (Exception e)
	        {
	            e.printStackTrace();
	            System.exit(-1);
	        }
	    }
	}
	
	public static void main(String[] args) {
		VoiceCall vc = new VoiceCall();
		vc.call();
	}
}
