package vidada.views.tags;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import vidada.model.ServiceProvider;
import vidada.model.media.IMediaService;
import vidada.model.tags.ITagService;
import archimedesJ.threading.IProgressListener;
import archimedesJ.threading.ProgressEventArgs;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class AutoTagPanel extends JPanel{

	private final IMediaService mediaDataService = ServiceProvider.Resolve(IMediaService.class);
	private final ITagService tagService = ServiceProvider.Resolve(ITagService.class);

	private Action autoApplyTagsAction;

	private void defineActions(){
		//
		// Automatically apply matching Tags to your videos.
		//
		autoApplyTagsAction = new AbstractAction("Auto Tag") {
			@Override
			public void actionPerformed(ActionEvent evt) {    	
				startTagGuesser();
			}
		};
		autoApplyTagsAction.putValue(Action.SHORT_DESCRIPTION, "Automatically apply matching Tags to your viedos.");

	}


	public AutoTagPanel(){

		defineActions();

		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		JLabel lblSettings = new JLabel("Settings");
		lblSettings.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		add(lblSettings, "2, 2, 3, 1");

		JCheckBox chckbxAutotagAfterImport = new JCheckBox("AutoTag after import");
		chckbxAutotagAfterImport.setSelected(true);
		chckbxAutotagAfterImport.setEnabled(false);
		add(chckbxAutotagAfterImport, "4, 4, 3, 1");

		JCheckBox chckbxNewCheckBox = new JCheckBox("Scan full path\n");
		chckbxNewCheckBox.setSelected(true);
		chckbxNewCheckBox.setEnabled(false);
		add(chckbxNewCheckBox, "4, 6, 3, 1");

		JLabel lblAutotag = new JLabel("AutoTag");
		lblAutotag.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		add(lblAutotag, "2, 18");

		JButton btnAutoApplyTags = new JButton(autoApplyTagsAction);
		btnAutoApplyTags.setText("AutoTag Now");
		add(btnAutoApplyTags, "4, 20");

		progressBar = new JProgressBar();
		add(progressBar, "6, 20, fill, default");

	}
	JProgressBar progressBar;


	private void startTagGuesser(){


		final IProgressListener progressUpdater = new IProgressListener(){
			@Override
			public void currentProgress(final ProgressEventArgs progressInfo) {

				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						if(progressInfo.isIndeterminant())
						{
							progressBar.setIndeterminate(progressInfo.isIndeterminant());
						}else{
							progressBar.setIndeterminate(false);
							progressBar.setValue((int)progressInfo.getProgressInPercent());
						}
					}
				});
			}
		};


		SwingWorker<Boolean, Object> tagGuessWorker = new SwingWorker<Boolean, Object>(){

			@Override
			protected Boolean doInBackground() throws Exception {
				updateAllMediadataTags(progressUpdater);
				return true;
			}
		};

		tagGuessWorker.execute();
	}

	/**
	 * Updates all media tags
	 * @param progressListener
	 */
	private void updateAllMediadataTags(IProgressListener progressListener){

		throw new NotImplementedException();
		/*
		List<MediaItem> allMediaDatas = mediaDataService.getAllMediaData();
		int allMediaDataSize = allMediaDatas.size();

		ITagGuessingStrategy tagGuesser = tagService.createTagGuesser();

		System.out.println("automatically guessing tags for (" + allMediaDataSize + ") medias - " + tagGuesser);

		List<MediaItem> updatedMedias = new ArrayList<MediaItem>(allMediaDataSize);
		MediaItem mediaData;
		for (int i = 0; i < allMediaDataSize; i++) {
			mediaData = allMediaDatas.get(i);

			if( AutoTagSupport.updateTags(tagGuesser, mediaData) ){
				updatedMedias.add(mediaData);
				System.out.println("added tags to media " + mediaData);
			}
			int progress = (int)(100d / allMediaDataSize * (double)i);
			progressListener.currentProgress(new ProgressEventArgs(progress, ""));
		}
		mediaDataService.update(updatedMedias);

		progressListener.currentProgress(new ProgressEventArgs(100, ""));
		 */
	}

}
