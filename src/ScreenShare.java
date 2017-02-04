import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class ScreenShare {

	public static void main(String[] args) {
		System.out.println("Welcome to Screen Sharer");
		System.out.println("To set up server, type server [port]");
		System.out
				.println("To Set up client, type client [server-addr] [port]");

		new ScreenShare().interactive();

	}
	
	// move Mouse per five second while in client mode
	private boolean mouseMove;

	private void interactive() {
		Scanner s = new Scanner(System.in);
		while (true) {
			try {
				InetAddress addr = InetAddress.getByName("localhost");
				System.out.print(addr.getCanonicalHostName() + ">>> ");
				if (s.hasNext()) {
					intepretCommand(s.nextLine());
				}

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * Intepret a given command
	 * 
	 * @param cmd
	 */
	private void intepretCommand(String cmd) {
		StringTokenizer tokenizer = new StringTokenizer(cmd);
		String commandToken = tokenizer.nextToken();
		if (commandToken.equals("server")) {
			String port = tokenizer.nextToken();
			server(Integer.parseInt(port));
		} else if (commandToken.equals("client")) {
			String serverAddr = tokenizer.nextToken();
			String port = tokenizer.nextToken();
			client(serverAddr, Integer.parseInt(port));
		} else if (commandToken.equals("close")){
			close();
		} else if (commandToken.equals("mousemove")){
			this.mouseMove= true;
		} else if (commandToken.equals("nomousemove")){
			this.mouseMove= false;
		} else {
			System.out.println("Unrecognized Command");
		}

	}

	private void client(String serverAddr, int port) {
		JFrame frame = new JFrame();
		ImagePanel panel = new ImagePanel();
		frame.setResizable(true);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		try {
			BufferedImage image;
			Robot r = new Robot();
			long startTime = System.currentTimeMillis();
			Random rand = new Random();
			while(true){
				if(this.mouseMove && System.currentTimeMillis() - startTime > 5000){
					r.mouseMove(rand.nextInt(200), rand.nextInt(200));
					startTime = System.currentTimeMillis();
				}

				socket = new Socket(serverAddr, port);
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				image = ImageIO.read(inputStream);
				panel.setImg(image);
				panel.repaint();
				socket.close();
			}


		} catch (IOException e) {
			e.printStackTrace();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	ServerSocket  server;
	Socket socket;
	
	private void close(){
		try {
			socket.close();
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void server(int port) {
		try {
			server = new ServerSocket(port);
			Robot r = new Robot();

			while(true){
				try{
					socket = server.accept();
					InetAddress addr = socket.getInetAddress();
					System.out.println(
							"Received Connection From " + addr.getCanonicalHostName()
									+ " at " + addr.getHostAddress());
					ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
					BufferedImage img;
					img = r.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
					Point mouse = MouseInfo.getPointerInfo().getLocation();
					Graphics g = img.getGraphics();
					g.setColor(Color.BLACK);
					g.fillRect(mouse.x, mouse.y, 30, 30);
					ImageIO.write(img, "jpg", outstream);
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}  catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 

	}
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  

}
