package vidada.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import archimedesJ.swing.components.JImageIconPanel;


@SuppressWarnings("serial")
public class VidadaSplash extends JFrame {

	public VidadaSplash(){
		setUndecorated(true);


		setBackground(Color.DARK_GRAY);

		setContentPane(new JPanel());
		BorderLayout borderLayout = new BorderLayout(0,0);
		getContentPane().setLayout(borderLayout);


		JPanel centerContent = new JPanel();
		centerContent.setBackground(getBackground());
		centerContent.setBorder(new EmptyBorder(10, 10, 10, 10));
		BoxLayout layout = new BoxLayout(centerContent, BoxLayout.Y_AXIS);
		centerContent.setLayout(layout);


		JLabel header = new JLabel("Vidada - your media manager");
		header.setFont(new Font(null, Font.BOLD, 18));
		header.setForeground(Color.WHITE);
		header.setHorizontalAlignment(JLabel.CENTER);
		centerContent.add(header);

		JLabel info = new JLabel("We are loading...");
		//info.setFont(new Font(, Font.BOLD, 18));
		info.setForeground(Color.WHITE);
		info.setHorizontalAlignment(JLabel.CENTER);
		centerContent.add(info);

		getContentPane().add(centerContent, BorderLayout.CENTER);

		if(ImageResources.LOGO_ICON != null){
			JImageIconPanel logo = new JImageIconPanel(ImageResources.LOGO_ICON);
			logo.setBackground(getBackground());
			logo.setBorder(new EmptyBorder(10, 10, 10, 10));
			getContentPane().add(logo, BorderLayout.WEST);
		}

		// make the frame half the height and width
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screenSize.width;
		this.setSize(width/4, width/10);

		this.setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		VidadaSplash splash = new VidadaSplash();
		splash.setVisible(true);
	}


}
