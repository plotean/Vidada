package vidada.views.tags;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import vidada.model.ServiceProvider;
import vidada.model.tags.ITagService;
import vidada.model.tags.Tag;
import vidada.model.tags.TagKeyoword;
import vidada.views.ImageResources;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;
import archimedesJ.expressions.Predicate;
import archimedesJ.swing.JImageButton;
import archimedesJ.swing.components.InputBoxDialog;
import archimedesJ.util.DocumentListenerAggregate;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class TagEditPanel extends JPanel{

	private final ITagService tagService = ServiceProvider.Resolve(ITagService.class);

	private Tag tag;

	private JTextField txtTagName;
	private JList tagKeywordList;
	private JButton btnRemoveKeyword;
	private JButton btnNewButton;

	private DefaultListModel allKeywords = new DefaultListModel();

	AbstractAction addTagKeywordAction;
	AbstractAction removeTagKeywordAction;


	public TagEditPanel(){
		setLayout(new BorderLayout(0, 0));

		defineActions();

		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("214px:grow"),},
				new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,}));
		{
			JLabel lblTagName = new JLabel("Tag Name");
			headerPanel.add(lblTagName, "2, 1, left, fill");
		}
		{
			txtTagName = new JTextField();
			headerPanel.add(txtTagName, "4, 1, 2, 1, fill, center");
			txtTagName.setColumns(10);
		}

		JPanel keywordsPanel = new JPanel();
		keywordsPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		{
			JLabel lblNewLabel = new JLabel("Tag keywords (used for AutoTag)");
			keywordsPanel.add(lblNewLabel, "2, 2, 5, 1");
		}
		{
			btnNewButton = new JImageButton(addTagKeywordAction);
			keywordsPanel.add(btnNewButton, "2, 4");
		}
		{
			btnRemoveKeyword = new JImageButton(removeTagKeywordAction);
			keywordsPanel.add(btnRemoveKeyword, "4, 4");
		}
		{
			JLabel lblNewLabel_1 = new JLabel(" ");
			keywordsPanel.add(lblNewLabel_1, "6, 4");
		}
		{
			tagKeywordList = new JList(allKeywords);
			tagKeywordList.setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
			keywordsPanel.add(tagKeywordList, "2, 6, 5, 1, fill, fill");
		}

		add(headerPanel, BorderLayout.NORTH);
		add(keywordsPanel, BorderLayout.CENTER);

		defineActions();

		tagKeywordList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				btnNewButton.setEnabled(tag != null);
				btnRemoveKeyword.setEnabled(tag != null && tagKeywordList.getSelectedValue() != null);
			}
		});

		txtTagName.getDocument().addDocumentListener(new DocumentListenerAggregate() {
			@Override
			public void documentChanged(DocumentEvent e) {
				tag.setName(txtTagName.getText());
				tagService.update(tag);
			}
		});

		setDataContext(tag);
	}



	private void defineActions(){

		//
		// Assign a new keyword to this Tag
		//
		addTagKeywordAction = new AbstractAction("", ImageResources.ADD_ICON_32) {

			@Override
			public void actionPerformed(ActionEvent evt) {

				InputBoxDialog inputBox = new InputBoxDialog((Window)TagEditPanel.this.getTopLevelAncestor(), new Predicate<String>() {
					@Override
					public boolean where(String value) {
						return value != null && value.length() > 0;
					}
				});
				inputBox.setTitle("Add a new keyword");
				inputBox.setDescription("Enter the keyword(s) you whish to add. Keywords define what the AutoTag Feature will match.");
				inputBox.setVisible(true);

				if(inputBox.isOk())
				{
					tag.addKeyword(inputBox.getText());
					tagService.update(tag);
				}
			}

		};
		addTagKeywordAction.putValue(Action.SHORT_DESCRIPTION, "Assign a new keyword to this Tag");

		//
		// Remove the selected keyword from this Tag
		//
		removeTagKeywordAction = new AbstractAction("", ImageResources.DELETE_ICON_32) {

			@Override
			public void actionPerformed(ActionEvent evt) {
				TagKeyoword selectedKeyoword = (TagKeyoword) tagKeywordList.getSelectedValue();
				tag.removeKeyword(selectedKeyoword);
				removeTagKeywordAction.setEnabled(false);
			}
		};
		removeTagKeywordAction.putValue(Action.SHORT_DESCRIPTION, "Remove the selected keyword from this Tag");

	}


	/**
	 * Set the tag
	 * @param mytag
	 */
	public void setDataContext(Tag mytag){

		if(this.tag != null)
		{
			// unregister old event handlers
			tag.getKeywordAddedEvent().remove(keywordAddedListener);
			tag.getKeywordRemovedEvent().remove(keywordRemovedListener);	
		}

		this.tag = mytag;

		removeTagKeywordAction.setEnabled(tag != null && tagKeywordList.getSelectedValue() != null);
		addTagKeywordAction.setEnabled(tag != null);
		txtTagName.setEnabled(tag != null);
		tagKeywordList.setEnabled(tag != null);
		btnNewButton.setEnabled(tag != null);
		btnRemoveKeyword.setEnabled(tag != null && tagKeywordList.getSelectedValue() != null);

		if(tag != null)
		{
			if(txtTagName != null)
				txtTagName.setText(tag.getName());

			// fill in existing data
			allKeywords.clear();
			for (TagKeyoword keyoword : tag.getKeyWords()) {
				allKeywords.addElement(keyoword);
			}


			// register event handlers
			tag.getKeywordAddedEvent().add(keywordAddedListener);
			tag.getKeywordRemovedEvent().add(keywordRemovedListener);
		}

	}

	private EventListenerEx<EventArgsG<TagKeyoword>> keywordAddedListener = new EventListenerEx<EventArgsG<TagKeyoword>>() {
		@Override
		public void eventOccured(Object sender, EventArgsG<TagKeyoword> eventArgs) {
			System.out.println("keyword " + eventArgs.getValue() + " added!");
			allKeywords.addElement(eventArgs.getValue());
		}
	};
	private EventListenerEx<EventArgsG<TagKeyoword>> keywordRemovedListener = new EventListenerEx<EventArgsG<TagKeyoword>>() {
		@Override
		public void eventOccured(Object sender, EventArgsG<TagKeyoword> eventArgs) {
			allKeywords.removeElement(eventArgs.getValue());
		}
	};


}
