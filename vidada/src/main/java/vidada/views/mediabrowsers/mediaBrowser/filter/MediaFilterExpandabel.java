package vidada.views.mediabrowsers.mediaBrowser.filter;

import java.awt.BorderLayout;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;

import vidada.model.media.MediaItem;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.expressions.Predicate;

import com.db4o.query.Query;

public class MediaFilterExpandabel extends JPanel implements IFilterProvider{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final MediaFilterPanel filterpanelJPanel;
	final JXTaskPane taskPane = new JXTaskPane();
	private String title = "Media Filter";

	@Override
	public EventHandlerEx<EventArgs> getFilterChangedEvent() {return filterpanelJPanel.getFilterChangedEvent(); }

	public MediaFilterExpandabel() {
		setLayout(new BorderLayout(0, 0));
		taskPane.getContentPane().setBackground(SystemColor.window);


		taskPane.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) { }

			@Override
			public void mousePressed(MouseEvent e) { }

			@Override
			public void mouseExited(MouseEvent e) { }

			@Override
			public void mouseEntered(MouseEvent e) {
				taskPane.setCollapsed(false);
			}

			@Override
			public void mouseClicked(MouseEvent e) { }
		});

		taskPane.setTitle(title);
		add(taskPane, BorderLayout.CENTER);

		filterpanelJPanel = new MediaFilterPanel();
		taskPane.getContentPane().setLayout(new BorderLayout(0,0));
		taskPane.getContentPane().add(filterpanelJPanel, BorderLayout.CENTER);
		taskPane.setBackground(filterpanelJPanel.getBackground());
	}


	public void setCollapsed(boolean collapsed){
		taskPane.setCollapsed(collapsed);
	}

	/**
	 * Returns the current filter criteria
	 * @return
	 */
	@Override
	public Query getCriteria(){
		return filterpanelJPanel.getCriteria();
	}

	public boolean isCollapsed() {
		return taskPane.isCollapsed();
	}

	@Override
	public void setCurrentResultSet(List<MediaItem> medias){
		filterpanelJPanel.setCurrentResultSet(medias);
		taskPane.setTitle(title + " (" + medias.size() + ")");
	}

	public Predicate<MediaItem> getPostFilter() {
		return filterpanelJPanel.getPostFilter();
	}




}
