package vidada.views.media;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;

import org.jdesktop.swingx.JXTaskPane;

import vidada.model.ServiceProvider;
import vidada.model.media.MediaItem;
import vidada.model.tags.ITagService;
import vidada.model.tags.Tag;
import vidada.views.tags.TagsPanel;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.services.ISelectionManager;
import archimedesJ.swing.components.JMultiStateCheckBox.MultiCheckState;
import archimedesJ.swing.components.starrating.StarListener;
import archimedesJ.swing.components.starrating.StarRater;
import archimedesJ.util.DocumentListenerAggregate;

/**
 * This panel holds all Media Details and provides the ability to edit them
 * This media panel is able to handle multiple selected items
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class ExpandableMediaDetail extends JPanel {

	private static final String DefaultTitle = "Media Detail";

	private final ITagService tagService = ServiceProvider.Resolve(ITagService.class);
	private final ISelectionManager<MediaItem> mediaDataSelectionManager;

	// Controls
	private final TagsPanel tagspanel;
	private final StarRater starRaterPanel;
	private final JLabel lblOpened;
	private final JTextField txtFileName;
	private final JXTaskPane taskPane;


	private IMediaDetailModel currentDetailModel;

	/**
	 * Inits this panel - register events
	 */
	private void init() {

		if(tagService != null)
		{
			refreshTags();

			tagService.getTagsChangedEvent().add(new EventListenerEx<EventArgs>() {
				@Override
				public void eventOccured(Object sender, EventArgs eventArgs) {
					refreshTags();
				}
			});


			tagspanel.TagStateChangedEvent.add(new EventListenerEx<EventArgs>() {
				@Override
				public void eventOccured(Object sender, EventArgs eventArgs) {
					updateTagsToModel();
				}
			});

			starRaterPanel.addStarListener(new StarListener() {
				@Override
				public void handleSelection(int selection) {

					if(currentDetailModel != null)
					{
						currentDetailModel.setRating(starRaterPanel.getSelection());
						currentDetailModel.persist();
					}
				}
			});

			txtFileName.getDocument().addDocumentListener(new DocumentListenerAggregate() {
				@Override
				public void documentChanged(DocumentEvent e) {
					if(currentDetailModel != null && !inUpdateModelToView)
					{
						currentDetailModel.setFileName(txtFileName.getText());
						currentDetailModel.persist();
					}
				}
			});


			mediaDataSelectionManager.getSelectionChanged().add(new EventListenerEx<EventArgs>() {
				@Override
				public void eventOccured(Object sender, EventArgs eventArgs) {

					if(mediaDataSelectionManager.hasSelection())
					{
						List<MediaItem> selectedMedias  = mediaDataSelectionManager.getSelection();

						if(selectedMedias.size() == 1)
						{
							MediaItem mediaData = selectedMedias.get(0);
							// one item selected
							if(mediaData != null)
							{
								System.out.println("selected Media: " + mediaData);
								System.out.println("selected Media: " + mediaData.getFilehash());
								setDataContext(new MediaDetailModel(mediaData));
							}
						}else{
							// multiple items selected
							setDataContext(new MultiMediaDetailModel(selectedMedias));
						}

					}else {
						setDataContext(null);
					}
				}
			});

		}else{
			// design mode
			tagspanel.addTag(new Tag("Action"));
			tagspanel.addTag(new Tag("Cool"));
			tagspanel.addTag(new Tag("Fake"));
		}

		setDataContext(null);
	}



	private void setDataContext(IMediaDetailModel data){

		System.out.println("MediaDetail.setDataContext: " + data);

		// remove old event listeners
		if(currentDetailModel != null)
		{
			currentDetailModel.getTagsChanged().remove(modelTagsChangedListener);
		}

		currentDetailModel = data;


		if(data == null)
		{
			//tagspanel.setTagsState(MultiCheckState.Unchecked);
			tagspanel.setEnabled(false);
			txtFileName.setEnabled(false);
			lblOpened.setEnabled(false);
			starRaterPanel.setEnabled(false);

			txtFileName.setText("");
			lblOpened.setText("");
			starRaterPanel.setSelection(0);
			setTile(DefaultTitle);


		}else{
			currentDetailModel.getTagsChanged().add(modelTagsChangedListener);
			tagspanel.setEnabled(true);
			txtFileName.setEnabled(true);
			lblOpened.setEnabled(true);
			starRaterPanel.setEnabled(true);

			updateModelToView();
		}
	}


	EventListenerEx<EventArgs> modelTagsChangedListener = new EventListenerEx<EventArgs>() {
		@Override
		public void eventOccured(Object sender, EventArgs eventArgs) {
			updateModelToView();
		}
	};

	private void refreshTags(){
		tagspanel.clearTags();
		if(tagService != null){
			List<Tag> allTags = tagService.getAllTags();
			Collections.sort(allTags);
			tagspanel.addTags(allTags);
		}
		updateModelToView();
	}

	/**
	 * Updates the View according to the model
	 */
	private void updateModelToView(){

		inUpdateModelToView = true;

		if(!ignoreModelUpdate && currentDetailModel != null)
		{

			System.out.println("MediaDetail:updateModelToView");

			setTile(DefaultTitle + " " + currentDetailModel.getTitle());

			//
			// updates the tags in the view
			//

			for (Tag tag : tagspanel.getAllTags()) {
				MultiCheckState state = currentDetailModel.getState(tag);
				tagspanel.setTagState(tag, state);
			}

			starRaterPanel.setSelection(currentDetailModel.getRating());

			txtFileName.setText(currentDetailModel.getFilename());

			lblOpened.setText(currentDetailModel.getOpened() + " times");

			lblSize.setText(currentDetailModel.getResolution());

			txtAdded.setText(currentDetailModel.getAddedDate());

		}

		inUpdateModelToView = false;
	}

	private volatile boolean ignoreModelUpdate = false;
	private volatile boolean inUpdateModelToView = false;
	private JLabel lblSize;
	private JLabel lblSize_1;
	private JLabel lblAdded;
	private JLabel txtAdded;

	/**
	 * Updates the Model according to the set Tags in this view
	 */
	private void updateTagsToModel(){
		if(currentDetailModel != null)
		{
			System.out.println("MediaDetail:updateTagsToModel");
			ignoreModelUpdate = true;

			List<Tag> newSetTags = tagspanel.getTagsWithState(MultiCheckState.Checked);
			List<Tag> newUnSetTags = tagspanel.getTagsWithState(MultiCheckState.Unchecked);

			for (Tag tag : newSetTags) {
				currentDetailModel.setState(tag, MultiCheckState.Checked);
			}

			for (Tag tag : newUnSetTags) {
				currentDetailModel.setState(tag, MultiCheckState.Unchecked);
			}

			currentDetailModel.persist();

			ignoreModelUpdate = false;
		}
	}



	private void setTile(String newTitle){
		taskPane.setTitle(newTitle);
	}


	public ExpandableMediaDetail() {
		this(null);
	}


	public ExpandableMediaDetail(ISelectionManager<MediaItem> selectionManager){
		mediaDataSelectionManager = selectionManager;

		setLayout(new BorderLayout(0, 0));

		taskPane = new JXTaskPane();
		taskPane.setTitle(DefaultTitle);
		add(taskPane, BorderLayout.CENTER);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		taskPane.getContentPane().setLayout(gridBagLayout);

		JLabel lblFileName = new JLabel("File Name");
		lblFileName.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblFileName = new GridBagConstraints();
		gbc_lblFileName.anchor = GridBagConstraints.WEST;
		gbc_lblFileName.insets = new Insets(0, 0, 5, 5);
		gbc_lblFileName.gridx = 0;
		gbc_lblFileName.gridy = 0;
		taskPane.getContentPane().add(lblFileName, gbc_lblFileName);

		txtFileName = new JTextField();
		txtFileName.setEditable(true);
		txtFileName.setMaximumSize(new Dimension(100, 50));
		GridBagConstraints gbc_txtFileName = new GridBagConstraints();
		gbc_txtFileName.insets = new Insets(0, 0, 5, 5);
		gbc_txtFileName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFileName.gridx = 1;
		gbc_txtFileName.gridy = 0;
		taskPane.getContentPane().add(txtFileName, gbc_txtFileName);
		txtFileName.setColumns(10);

		lblSize_1 = new JLabel("Resolution");
		lblSize_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblSize_1 = new GridBagConstraints();
		gbc_lblSize_1.anchor = GridBagConstraints.EAST;
		gbc_lblSize_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblSize_1.gridx = 2;
		gbc_lblSize_1.gridy = 0;
		taskPane.getContentPane().add(lblSize_1, gbc_lblSize_1);

		lblSize = new JLabel("");
		GridBagConstraints gbc_lblSize = new GridBagConstraints();
		gbc_lblSize.anchor = GridBagConstraints.WEST;
		gbc_lblSize.insets = new Insets(0, 0, 5, 0);
		gbc_lblSize.gridx = 3;
		gbc_lblSize.gridy = 0;
		taskPane.getContentPane().add(lblSize, gbc_lblSize);

		JLabel lblHeadOpened = new JLabel("Opened");
		lblHeadOpened.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblHeadOpened = new GridBagConstraints();
		gbc_lblHeadOpened.anchor = GridBagConstraints.EAST;
		gbc_lblHeadOpened.insets = new Insets(0, 0, 5, 5);
		gbc_lblHeadOpened.gridx = 0;
		gbc_lblHeadOpened.gridy = 1;
		taskPane.getContentPane().add(lblHeadOpened, gbc_lblHeadOpened);

		lblOpened = new JLabel("2");
		GridBagConstraints gbc_lblOpened = new GridBagConstraints();
		gbc_lblOpened.insets = new Insets(0, 0, 5, 5);
		gbc_lblOpened.anchor = GridBagConstraints.WEST;
		gbc_lblOpened.gridx = 1;
		gbc_lblOpened.gridy = 1;
		taskPane.getContentPane().add(lblOpened, gbc_lblOpened);

		lblAdded = new JLabel("Added");
		lblAdded.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblAdded = new GridBagConstraints();
		gbc_lblAdded.anchor = GridBagConstraints.EAST;
		gbc_lblAdded.insets = new Insets(0, 0, 5, 5);
		gbc_lblAdded.gridx = 2;
		gbc_lblAdded.gridy = 1;
		taskPane.getContentPane().add(lblAdded, gbc_lblAdded);

		txtAdded = new JLabel();
		txtAdded.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_txtAdded = new GridBagConstraints();
		gbc_txtAdded.anchor = GridBagConstraints.WEST;
		gbc_txtAdded.insets = new Insets(0, 0, 5, 0);
		gbc_txtAdded.gridx = 3;
		gbc_txtAdded.gridy = 1;
		taskPane.getContentPane().add(txtAdded, gbc_txtAdded);

		JLabel lblRating = new JLabel("Rating");
		lblRating.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblRating = new GridBagConstraints();
		gbc_lblRating.anchor = GridBagConstraints.EAST;
		gbc_lblRating.insets = new Insets(0, 0, 5, 5);
		gbc_lblRating.gridx = 0;
		gbc_lblRating.gridy = 2;
		taskPane.getContentPane().add(lblRating, gbc_lblRating);

		starRaterPanel = new StarRater();
		GridBagConstraints gbc_starRaterPanel = new GridBagConstraints();
		gbc_starRaterPanel.anchor = GridBagConstraints.WEST;
		gbc_starRaterPanel.insets = new Insets(0, 0, 5, 5);
		gbc_starRaterPanel.gridx = 1;
		gbc_starRaterPanel.gridy = 2;
		taskPane.getContentPane().add(starRaterPanel, gbc_starRaterPanel);

		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.gridwidth = 4;
		gbc_separator.insets = new Insets(0, 0, 5, 0);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 3;
		taskPane.getContentPane().add(separator, gbc_separator);

		tagspanel = new TagsPanel();
		GridBagConstraints gbc_fakepanel = new GridBagConstraints();
		gbc_fakepanel.gridwidth = 4;
		gbc_fakepanel.fill = GridBagConstraints.BOTH;
		gbc_fakepanel.gridx = 0;
		gbc_fakepanel.gridy = 4;
		taskPane.getContentPane().add(tagspanel, gbc_fakepanel);
		tagspanel.setBackground(null);

		init();
	}


}
