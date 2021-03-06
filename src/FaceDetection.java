import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

/**
 * The Class FaceDetection.
 * 
 * @author Shashank Sridhar
 */

public class FaceDetection extends javax.swing.JFrame {

	private DaemonThread myThread = null;
	int count = 0;
	VideoCapture webSource = null;
	Mat frame = new Mat();
	MatOfByte mem = new MatOfByte();
	CascadeClassifier faceDetector = new CascadeClassifier(FaceDetection.class
			.getResource("haarcascade_frontalface_alt.xml").getPath()
			.substring(1));
	MatOfRect faceDetections = new MatOfRect();

	class DaemonThread implements Runnable {

		protected volatile boolean runnable = false;

		@Override
		public void run() {
			synchronized (this) {
				while (runnable) {
					if (webSource.grab()) {
						try {
							webSource.retrieve(frame);
							Graphics g = jPanel1.getGraphics();
							faceDetector
									.detectMultiScale(frame, faceDetections);
							for (Rect rect : faceDetections.toArray()) {
								Imgproc.rectangle(frame, new Point(rect.x,
										rect.y), new Point(rect.x + rect.width,
										rect.y + rect.height), new Scalar(0,
										255, 0));
							}
							Imgcodecs.imencode(".bmp", frame, mem);
							Image im = ImageIO.read(new ByteArrayInputStream(
									mem.toArray()));
							BufferedImage buff = (BufferedImage) im;
							if (g.drawImage(buff, 0, 0, getWidth(),
									getHeight() - 150, 0, 0, buff.getWidth(),
									buff.getHeight(), null)) {
								if (runnable == false) {
									System.out.println("Paused ..... ");
									this.wait();
								}
							}
						} catch (Exception ex) {
							System.out.println("Error");
						}
					}
				}
			}
		}
	}

	/**
	 * Creates new form FaceDetection.
	 */

	public FaceDetection() {
		initComponents();
		System.out.println(FaceDetection.class
				.getResource("haarcascade_frontalface_alt.xml").getPath()
				.substring(1));
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0,
				Short.MAX_VALUE));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 376,
				Short.MAX_VALUE));

		jButton1.setText("Start");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		jButton2.setText("Pause");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addGap(24, 24, 24)
								.addComponent(jPanel1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE).addContainerGap())
				.addGroup(
						layout.createSequentialGroup().addGap(255, 255, 255)
								.addComponent(jButton1).addGap(86, 86, 86)
								.addComponent(jButton2)
								.addContainerGap(258, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jPanel1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jButton1)
												.addComponent(jButton2))
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		pack();
	}

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {

		// stop thread
		myThread.runnable = false;

		// activate start button
		jButton2.setEnabled(false);

		// deactivate stop button
		jButton1.setEnabled(true);

		// stop capturing from camera
		webSource.release();
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {

		// video capture from default camera
		webSource = new VideoCapture(0);

		// create object of threat class
		myThread = new DaemonThread();

		Thread t = new Thread(myThread);
		t.setDaemon(true);
		myThread.runnable = true;

		// start thread
		t.start();

		// deactivate start button
		jButton1.setEnabled(false);

		// activate stop button
		jButton2.setEnabled(true);

	}

	/**
	 * @param command
	 *            line arguments.
	 */

	public static void main(String args[]) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
					.getInstalledLookAndFeels()) {

				// Use Nimbus for look and feel
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(FaceDetection.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(FaceDetection.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(FaceDetection.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(FaceDetection.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
		}

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new FaceDetection().setVisible(true);
			}
		});
	}

	// variables declaration
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JPanel jPanel1;
	// end of variables declaration
}