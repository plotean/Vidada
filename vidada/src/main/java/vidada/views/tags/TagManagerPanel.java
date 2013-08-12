package vidada.views.tags;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import vidada.model.ServiceProvider;
import vidada.model.tags.ITagService;
import vidada.model.tags.Tag;
import vidada.views.ImageResources;
import vidada.views.dialoges.ManageTagDialog;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;
import archimedesJ.expressions.Predicate;
import archimedesJ.swing.JImageButton;
import archimedesJ.swing.components.InputBoxDialog;

@SuppressWarnings("serial")
public class TagManagerPanel  extends JPanel{

	private DefaultListModel tagListModel = new DefaultListModel();
	private JList taglist;
	private TagEditPanel editTagPanel;

	private final ITagService tagService = ServiceProvider.Resolve(ITagService.class);

	private Action deleteSelectedTagsAction;
	private Action  addNewTagsAction;
	private Action editTagAction;

	private void init(){

		List<Tag> alltags = tagService.getAllTags();
		for (Tag tag : alltags) {
			tagListModel.addElement(tag);
		}

		tagService.getTagAddedEvent().add(new EventListenerEx<EventArgsG<Tag>>() {

			@Override
			public void eventOccured(Object sender, EventArgsG<Tag> eventArgs) {
				tagListModel.addElement(eventArgs.getValue());
			}
		});

		tagService.getTagRemovedEvent().add(new EventListenerEx<EventArgsG<Tag>>() {

			@Override
			public void eventOccured(Object sender, EventArgsG<Tag> eventArgs) {
				tagListModel.removeElement(eventArgs.getValue());
			}
		});




		//
		// Create new Tag
		//
		addNewTagsAction = new AbstractAction("", ImageResources.ADD_ICON_32) {
			@Override
			public void actionPerformed(ActionEvent evt) {    	

				Window owner = (Window) SwingUtilities.getRoot(TagManagerPanel.this);
				InputBoxDialog inputBox = new InputBoxDialog(owner, new Predicate<String>() {
					@Override
					public boolean where(String value) {
						return value != null && value.length() > 0;
					}
				});
				inputBox.setTitle("Create a new Tag");
				inputBox.setDescription("Enter a new Tag name.<br>You can even create multiple Tags by seperating names by a comma.");
				inputBox.setVisible(true);

				if(inputBox.isOk())
				{
					Set<Tag> tags = tagService.createTags(inputBox.getText());
					tagService.addTags(tags);
				}

			}
		};
		addNewTagsAction.putValue(Action.SHORT_DESCRIPTION, "Create new Tag");

		//
		// Edit selected Tag
		//
		editTagAction = new AbstractAction("", ImageResources.EDIT_ICON_32) {
			@Override
			public void actionPerformed(ActionEvent evt) {    	

				Tag tag = (Tag) taglist.getSelectedValue();
				if(tag != null){
					Window owner = (Window) SwingUtilities.getRoot(TagManagerPanel.this);

					ManageTagDialog manageTag = new ManageTagDialog(owner, tag);
					manageTag.setVisible(true);

					tagService.update(tag);
				}

			}
		};
		editTagAction.putValue(Action.SHORT_DESCRIPTION, "Edit the selected Tag");



		//
		// Delete selected Tags
		//
		deleteSelectedTagsAction = new AbstractAction("", ImageResources.DELETE_ICON_32) {
			// This method is called when the button is pressed
			@Override
			public void actionPerformed(ActionEvent evt) {    	

				for (Object tag : taglist.getSelectedValues()) {
					tagService.removeTag((Tag)tag);
				}

			}
		};
		deleteSelectedTagsAction.putValue(Action.SHORT_DESCRIPTION, "Delete selected Tags");
	}


	private void updateState() {

		Tag selectedTag = (Tag)taglist.getSelectedValue();

		deleteSelectedTagsAction.setEnabled(selectedTag != null);
		editTagAction.setEnabled(selectedTag != null);
		editTagPanel.setEnabled(selectedTag != null);

		editTagPanel.setDataContext(selectedTag);
	}



	public TagManagerPanel(){

		init();

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		JButton btnAdd = new JImageButton(addNewTagsAction);

		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.insets = new Insets(0, 0, 5, 5);
		gbc_btnAdd.gridx = 0;
		gbc_btnAdd.gridy = 1;
		add(btnAdd, gbc_btnAdd);

		JButton btnNewButton = new JImageButton(deleteSelectedTagsAction);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.WEST;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 1;
		add(btnNewButton, gbc_btnNewButton);

		taglist = new JList(tagListModel);
		taglist.setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
		GridBagConstraints gbc_taglist = new GridBagConstraints();
		gbc_taglist.gridheight = 7;
		gbc_taglist.gridwidth = 2;
		gbc_taglist.insets = new Insets(0, 0, 0, 5);
		gbc_taglist.fill = GridBagConstraints.BOTH;
		gbc_taglist.gridx = 0;
		gbc_taglist.gridy = 2;
		add(new JScrollPane(taglist), gbc_taglist);

		editTagPanel = new TagEditPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridheight = 7;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 2;
		gbc_panel.gridy = 2;
		add(editTagPanel, gbc_panel);

		updateState();

		taglist.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateState();
			}
		});

	}


}
