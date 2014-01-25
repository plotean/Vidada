package vidada.views.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import vidada.model.ServiceProvider;
import vidada.model.media.IMediaService;
import vidada.model.media.MediaItem;
import vidada.model.media.source.IMediaSource;
import archimedesJ.expressions.Predicate;
import archimedesJ.util.Lists;

public class DuplicateManagerPanel extends JPanel {

	private final JTree tree;

	public DuplicateManagerPanel() {
		setLayout(new BorderLayout(0, 0));

		JPanel header = new JPanel();
		add(header, BorderLayout.NORTH);
		header.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnNewButton = new JButton("Find Duplicates");
		header.add(btnNewButton);

		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);

		JLabel lblStatus = new JLabel("Status");
		panel.add(lblStatus);

		tree = new JTree();
		JScrollPane scrollPane = new JScrollPane(tree);
		add(scrollPane, BorderLayout.CENTER);

		findDuplicates();
	}

	private void findDuplicates(){
		IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);

		List<MediaItem> multipleSources = Lists.filter(mediaService.getAllMediaData(), new Predicate<MediaItem>() {
			@Override
			public boolean where(MediaItem media) {
				return media.getSources().size() > 1;
			}
		});


		DefaultMutableTreeNode root =
				new DefaultMutableTreeNode("Medias with multiple sources");

		buildVisualTree(root, multipleSources);


		tree.setModel(new DefaultTreeModel(root));
	}

	private void buildVisualTree(DefaultMutableTreeNode root, List<MediaItem> multipleSources){
		DefaultMutableTreeNode node;

		for (MediaItem mediaItem : multipleSources) {
			node = new DefaultMutableTreeNode(mediaItem.getFilename());
			for (IMediaSource source : mediaItem.getSources()) {
				node.add(new DefaultMutableTreeNode(source.getResourceLocation()));
			}
			root.add(node);
		}
	}


}
