package vidada.views.mediabrowsers.mediaBrowser.filter;



import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import vidada.model.ServiceProvider;
import vidada.model.libraries.IMediaLibraryService;
import vidada.model.libraries.MediaLibrary;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.OrderProperty;
import vidada.model.media.QueryBuilder;
import vidada.model.media.source.MediaSource;
import vidada.model.tags.ITagService;
import vidada.model.tags.Tag;
import vidada.views.ImageResources;
import vidada.views.tags.TagsPanel;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.EventListenerEx;
import archimedesJ.expressions.Predicate;
import archimedesJ.swing.JTextFieldHint;
import archimedesJ.swing.components.JMultiStateCheckBox.MultiCheckState;
import archimedesJ.util.IAsyncFilterable;

import com.db4o.query.Query;

/**
 * Represents the main filter panel
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class MediaFilterPanel extends JPanel implements IFilterProvider {


	private final ITagService tagService = ServiceProvider.Resolve(ITagService.class);
	private final IMediaLibraryService mediaLibraryService = ServiceProvider.Resolve(IMediaLibraryService.class);

	//private BinaryCombination tagComposition = BinaryCombination.AND;
	private final List<IAsyncFilterable<MediaItem>> filterListener = new ArrayList<IAsyncFilterable<MediaItem>>();


	private final JTextField txtSearch;
	private final JComboBox  cmbMediaType;
	private final JComboBox cmbSortOrder;
	private final TagsPanel tagspanel;

	private final JCheckBox ckboxReverseOrder;
	private final JCheckBox chckOnlyAvailable;

	private EventHandlerEx<EventArgs> filterChangedEvent = new EventHandlerEx<EventArgs>();
	/* (non-Javadoc)
	 * @see vidada.views.mediaBrowser.filter.IFilterProvider#getFilterChangedEvent()
	 */
	@Override
	public EventHandlerEx<EventArgs> getFilterChangedEvent() {return filterChangedEvent; }



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
					onFilterSelectionChanged();
				}
			});


			txtSearch.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent e) {
					onFilterSelectionChanged();
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					onFilterSelectionChanged();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					onFilterSelectionChanged();
				}
			});

		}else{
			//design mode
		}



		// MediaType Combobox

		for (MediaType type : MediaType.values()) {
			cmbMediaType.addItem(type);
		}

		cmbMediaType.setSelectedItem(MediaType.ANY);

		cmbMediaType.addActionListener(filterChangedlistener);

		ckboxReverseOrder.addActionListener(filterChangedlistener);

		cmbSortOrder.addActionListener(filterChangedlistener);

		for (OrderProperty prop : OrderProperty.values()) {
			cmbSortOrder.addItem(prop);
		}

		chckOnlyAvailable.setSelected(true);
		chckOnlyAvailable.addActionListener(filterChangedlistener);
	}

	private void refreshTags(){
		tagspanel.clearTags();
		if(tagService != null){
			List<Tag> allTags = tagService.getAllTags();
			Collections.sort(allTags);
			tagspanel.addTags(allTags);
		}
		//updateModelToView();
	}

	ActionListener filterChangedlistener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			onFilterSelectionChanged();
		}
	};


	/**
	 * Create the panel.
	 */
	public MediaFilterPanel() {


		this.setBorder(new EmptyBorder(10, 10, 0, 10));

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		JLabel lblNewLabel = new JLabel("Media Type");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);

		cmbMediaType = new JComboBox();
		GridBagConstraints gbc_cmbMediaType = new GridBagConstraints();
		gbc_cmbMediaType.insets = new Insets(0, 0, 5, 5);
		gbc_cmbMediaType.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbMediaType.gridx = 1;
		gbc_cmbMediaType.gridy = 0;
		add(cmbMediaType, gbc_cmbMediaType);

		JSeparator separator_2 = new JSeparator();
		separator_2.setOrientation(SwingConstants.VERTICAL);
		GridBagConstraints gbc_separator_2 = new GridBagConstraints();
		gbc_separator_2.fill = GridBagConstraints.VERTICAL;
		gbc_separator_2.insets = new Insets(0, 0, 5, 5);
		gbc_separator_2.gridx = 2;
		gbc_separator_2.gridy = 0;
		add(separator_2, gbc_separator_2);

		JLabel lblSorting = new JLabel("Sorting");
		GridBagConstraints gbc_lblSorting = new GridBagConstraints();
		gbc_lblSorting.insets = new Insets(0, 0, 5, 5);
		gbc_lblSorting.anchor = GridBagConstraints.EAST;
		gbc_lblSorting.gridx = 3;
		gbc_lblSorting.gridy = 0;
		add(lblSorting, gbc_lblSorting);

		cmbSortOrder = new JComboBox();
		GridBagConstraints gbc_cmbSortOrder = new GridBagConstraints();
		gbc_cmbSortOrder.insets = new Insets(0, 0, 5, 5);
		gbc_cmbSortOrder.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbSortOrder.gridx = 4;
		gbc_cmbSortOrder.gridy = 0;
		add(cmbSortOrder, gbc_cmbSortOrder);

		ckboxReverseOrder = new JCheckBox("Reverse Order");
		GridBagConstraints gbc_ckboxReverseOrder = new GridBagConstraints();
		gbc_ckboxReverseOrder.insets = new Insets(0, 0, 5, 5);
		gbc_ckboxReverseOrder.gridx = 5;
		gbc_ckboxReverseOrder.gridy = 0;
		add(ckboxReverseOrder, gbc_ckboxReverseOrder);

		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.VERTICAL;
		gbc_separator_1.insets = new Insets(0, 0, 5, 5);
		gbc_separator_1.gridx = 6;
		gbc_separator_1.gridy = 0;
		add(separator_1, gbc_separator_1);


		txtSearch = new JTextFieldHint(ImageResources.SEARCH_ICON_32, "search...");


		GridBagConstraints gbc_txtSearch = new GridBagConstraints();
		gbc_txtSearch.gridwidth = 2;
		gbc_txtSearch.insets = new Insets(0, 0, 5, 0);
		gbc_txtSearch.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSearch.gridx = 7;
		gbc_txtSearch.gridy = 0;
		add(txtSearch, gbc_txtSearch);
		txtSearch.setColumns(10);

		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.gridwidth = 9;
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.insets = new Insets(0, 0, 5, 0);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 1;
		add(separator, gbc_separator);

		tagspanel = new TagsPanel();
		GridBagConstraints gbc_tagspanel = new GridBagConstraints();
		gbc_tagspanel.insets = new Insets(0, 0, 5, 0);
		gbc_tagspanel.gridheight = 2;
		gbc_tagspanel.gridwidth = 9;
		gbc_tagspanel.fill = GridBagConstraints.BOTH;
		gbc_tagspanel.gridx = 0;
		gbc_tagspanel.gridy = 2;
		add(tagspanel, gbc_tagspanel);

		JSeparator separator_3 = new JSeparator();
		GridBagConstraints gbc_separator_3 = new GridBagConstraints();
		gbc_separator_3.insets = new Insets(0, 0, 5, 0);
		gbc_separator_3.gridwidth = 9;
		gbc_separator_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_3.gridx = 0;
		gbc_separator_3.gridy = 4;
		add(separator_3, gbc_separator_3);

		chckOnlyAvailable = new JCheckBox("Only available");
		GridBagConstraints gbc_chckOnlyAvailable = new GridBagConstraints();
		gbc_chckOnlyAvailable.anchor = GridBagConstraints.WEST;
		gbc_chckOnlyAvailable.gridwidth = 2;
		gbc_chckOnlyAvailable.insets = new Insets(0, 0, 0, 5);
		gbc_chckOnlyAvailable.gridx = 0;
		gbc_chckOnlyAvailable.gridy = 5;
		add(chckOnlyAvailable, gbc_chckOnlyAvailable);

		init();
	}

	public void register(IAsyncFilterable<MediaItem> filterable){
		filterListener.add(filterable);
	}


	protected void onFilterSelectionChanged(){
		System.out.println("filter changed!");
		filterChangedEvent.fireEvent(this, EventArgs.Empty);
	}

	@Override
	public void setCurrentResultSet(List<MediaItem> medias){

		Set<Tag> remainders = QueryBuilder.getRemainingTags(medias);

		for (Tag tag : tagspanel.getAllTags()) {

			if(remainders.contains(tag))
			{
				// if the remaining tags contain this tag
				// and if this tag was unavailable
				// make it available again
				if(tagspanel.getTagState(tag) == MultiCheckState.Unavaiable)
				{
					tagspanel.setTagState(tag, MultiCheckState.Unchecked);
				}
			}else{
				// this tag is not available
				tagspanel.setTagState(tag, MultiCheckState.Unavaiable);
			}
		}
	}



	/**
	 * Returns a search criteria from the current selected GUI settings
	 * This method will return immediately, as the criteria is not evaluated
	 * @return
	 */
	@Override
	public Query getCriteria(){ return buildCriteria(); }

	private final QueryBuilder queryBuilder = new QueryBuilder();


	private Query buildCriteria(){

		MediaType selectedtype = (MediaType)cmbMediaType.getSelectedItem();
		List<Tag> requiredTags = tagspanel.getTagsWithState(MultiCheckState.Checked);
		OrderProperty selectedOrder = (OrderProperty)cmbSortOrder.getSelectedItem();
		List<MediaLibrary> requiredMediaLibs = new ArrayList<MediaLibrary>();
		boolean reverse = ckboxReverseOrder.isSelected();


		requiredMediaLibs = mediaLibraryService.getAllLibraries();
		if(chckOnlyAvailable.isSelected())
		{
			for (MediaLibrary mediaLibrary : new ArrayList<MediaLibrary>(requiredMediaLibs)) {
				if(!mediaLibrary.isAvailable())
				{
					requiredMediaLibs.remove(mediaLibrary);
				}
			}
		}

		Query mediaDataCriteria = queryBuilder.buildMediadataCriteria(
				selectedtype,
				txtSearch.getText(),
				selectedOrder,
				requiredTags,
				requiredMediaLibs,
				reverse);

		//
		// build the query
		//
		return mediaDataCriteria;
	}

	public Predicate<MediaItem> getPostFilter(){

		if(chckOnlyAvailable.isSelected()){

			return new Predicate<MediaItem>() {
				@Override
				public boolean where(MediaItem value) {
					// filter out medias which have a no available source
					for (MediaSource s : value.getSources()) {
						if(s.isAvailable())
							return true;
					}
					return false;
				}
			};
		}

		return null;
	}

}
