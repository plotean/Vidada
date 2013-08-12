package vidada.views.mediabrowsers.mediaFileBrowser;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.List;

import archimedesJ.geometry.Size;
import archimedesJ.util.OSValidator;
import archimedesJ.util.images.ScalrEx;

public class FolderThumbImageComposer {

	private int insetTop;
	private int contentPlaceholderWidth;
	private int contentPlaceholderHeigth;
	private int thumbWidth;
	private int thumbHeigth;

	public FolderThumbImageComposer() {
	}

	public Image createImageComposition(List<Image> images, Size size, float insetTopBottom, int minBorderInset) {

		BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		Graphics2D g2 = (Graphics2D) g;
		int m = OSValidator.isHDPI() ? 2 : 1;

		insetTop = (int) ((float) size.height * insetTopBottom);
		insetTop = Math.max(insetTop, minBorderInset * m);

		contentPlaceholderWidth = size.width;
		contentPlaceholderHeigth = size.height - 2 * insetTop;
		thumbWidth = contentPlaceholderWidth / 2;
		thumbHeigth = contentPlaceholderHeigth / 2;

		// Apply Responsive behavior:
		// At a smaller folder zoom level we just want to use half the folder
		// height for images
		if (size.width < 200 * m) {
			thumbHeigth = contentPlaceholderHeigth;
			composeHalfSize(images, g2);
		} else {
			composeFullSize(images, g2);
		}

		return image;

	}

	private void composeHalfSize(List<Image> images, Graphics2D g2) {

		if (images.size() == 1) {

			Image thumb = images.get(0);
			thumb = ScalrEx.rescaleImage(thumb, contentPlaceholderWidth, contentPlaceholderHeigth);
			g2.drawImage(thumb, 0, insetTop, contentPlaceholderWidth, contentPlaceholderHeigth, null);

		} else {
			for (int i = 0; i < 2; i++) {
				Image thumb = images.get(i);
				thumb = ScalrEx.rescaleImage(thumb, thumbWidth, thumbHeigth);
				drawMiniTHumb(g2, thumb, Position.values()[i]);
			}
		}
		g2.dispose();
	}

	private void composeFullSize(List<Image> images, Graphics2D g2) {

		if (images.size() == 1) {

			Image thumb = images.get(0);
			thumb = ScalrEx.rescaleImage(thumb, contentPlaceholderWidth, contentPlaceholderHeigth);
			g2.drawImage(thumb, 0, insetTop, contentPlaceholderWidth, contentPlaceholderHeigth, null);

		} else if (images.size() == 2) {

			for (int i = 0; i < 2; i++) {
				Image thumb = images.get(i);
				thumb = ScalrEx.rescaleImage(thumb, thumbWidth, thumbHeigth);
				drawMiniTHumb(g2, thumb, Position.values()[i]);
			}

		} else if (images.size() == 3) {

			for (int i = 0; i < 3; i++) {
				Image thumb = images.get(i);
				thumb = ScalrEx.rescaleImage(thumb, thumbWidth, thumbHeigth);
				drawMiniTHumb(g2, thumb, Position.values()[i]);
			}

		} else {
			for (int i = 0; i < 4; i++) {
				Image thumb = images.get(i);
				thumb = ScalrEx.rescaleImage(thumb, thumbWidth, thumbHeigth);
				drawMiniTHumb(g2, thumb, Position.values()[i]);
			}
		}
		g2.dispose();
	}

	private void drawMiniTHumb(Graphics2D g2, Image image, Position position) {

		switch (position) {
		case TopLeft:
			g2.drawImage(image, 0, insetTop, thumbWidth, thumbHeigth, (ImageObserver) null);
			break;
		case TopRight:
			g2.drawImage(image, thumbWidth, insetTop, thumbWidth, thumbHeigth, (ImageObserver) null);
			break;

		case BottomLeft:
			g2.drawImage(image, 0, insetTop + thumbHeigth, thumbWidth, thumbHeigth, (ImageObserver) null);
			break;

		case BottomRight:
			g2.drawImage(image, thumbWidth, insetTop + thumbHeigth, thumbWidth, thumbHeigth, (ImageObserver) null);
			break;

		default:
			break;
		}

	}

	private enum Position {
		TopLeft, TopRight, BottomRight, BottomLeft
	}

}
