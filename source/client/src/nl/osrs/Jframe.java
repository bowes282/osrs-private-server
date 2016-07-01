package nl.osrs;
import java.net.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;

public class Jframe extends Client implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	
	public static void refreshFrameSize(boolean fixed, boolean resizable) {
		Insets insets = frame.getInsets();
		if (fixed) {
			frame.setResizable(false);
			frame.setPreferredSize(new Dimension(insets.left + insets.right + 765, insets.top + insets.bottom + 503));
		} else if (resizable) {
			frame.setResizable(true);
			frame.setPreferredSize(new Dimension(insets.left + insets.right + 781, insets.top + insets.bottom + 523));
			frame.setMinimumSize(new Dimension(insets.left + insets.right + 781, insets.top + insets.bottom + 523));
		}
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	public Jframe(String args[]) {
		super();
		try {
			signlink.startpriv(InetAddress.getByName(connectionAddress));
			initUI();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void initUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			JPopupMenu.setDefaultLightWeightPopupEnabled(false);
			frame = new JFrame(Client.clientName);
			frame.setLayout(new BorderLayout());
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JPanel gamePanel = new JPanel();
			gamePanel.setLayout(new BorderLayout());
			gamePanel.add(this);
			gamePanel.setBackground(Color.BLACK);
			frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
			frame.pack();
			frame.setResizable(false);
			if(Client.frameMode == Client.ScreenMode.RESIZABLE) {
				frame.setResizable(true);
			}
			init();
			refreshFrameSize(Client.frameMode == Client.ScreenMode.FIXED, Client.frameMode == Client.ScreenMode.RESIZABLE);
			frame.setVisible(true); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public URL getCodeBase() {
		try {
			return new URL("http://" + connectionAddress + "/cache");
		} catch (Exception e) {
			return super.getCodeBase();
		}
	}

	public URL getDocumentBase() {
		return getCodeBase();
	}

	public void loadError(String s) {
		System.out.println("loadError: " + s);
	}

	public String getParameter(String key) {
		return "";
	}

	public static void openUpWebSite(String url) {
		Desktop d = Desktop.getDesktop();
		try {
			d.browse(new URI(url)); 	
		} catch (Exception e) {
		}
	}

	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		try {
			if (cmd != null) {

			}
		} catch (Exception e) {
		}
	}
}