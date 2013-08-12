package vidada.views.mediabrowsers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import archimedesJ.swing.shapes.TriangleFactory;

@SuppressWarnings("serial")
public class HiddenSettingsBar extends JComponent{

	public HiddenSettingsBar(){
		setBackground(Color.LIGHT_GRAY);
		setBorder(BorderFactory.createLineBorder(Color.GRAY));
	}


	@Override
	public void paintComponent(Graphics g){

		super.paintComponent(g);

		Graphics2D g2=(Graphics2D)g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Rectangle bounds = g2.getClipBounds();

		float h = (float)bounds.width - 7f;

		Shape triangle = getTriangle(h);
		// Insets adjustment

		g2.translate(
				(float)bounds.width/2f-(h/2f),
				(float)bounds.height/2f-(h/2f));

		g2.setColor(Color.DARK_GRAY);
		g2.fill(triangle);

		g2.dispose();
	}

	private Shape triangleCache = null; 
	private float triSize = 0;

	public Shape getTriangle(float size){
		if(triangleCache == null || triSize != size)
		{
			triangleCache = TriangleFactory.createRightTriangle(size);
			triSize = size;
		}
		return triangleCache;
	}


}
