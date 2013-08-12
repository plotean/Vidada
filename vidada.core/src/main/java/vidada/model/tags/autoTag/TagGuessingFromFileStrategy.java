package vidada.model.tags.autoTag;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import vidada.model.media.MediaItem;
import vidada.model.media.source.FileMediaSource;
import vidada.model.media.source.MediaSource;
import vidada.model.tags.Tag;

/**
 * Strategy finds potential matching tags by analyzing a file path and name.
 * @author IsNull
 *
 */
public class TagGuessingFromFileStrategy implements ITagGuessingStrategy  {

	private Map<String, Tag> avaiableTagsMap = new HashMap<String, Tag>();
	private static String splitRegEx = "\\W|_";

	/**
	 * Creates a new Tag-guesser with the given tags
	 * @param avaiableTags
	 */
	private TagGuessingFromFileStrategy(List<Tag> avaiableTags){
		for (Tag tag : avaiableTags) {
			avaiableTagsMap.put(tag.getName().toLowerCase(), tag);
		}
	}

	/*
	 * test
	 */
	public static void main(String[] args) {



		// Test cases - some crazy paths
		String absolutePathString = (new File("E:\\Movies\\Woot and Cool\\Way\\mods.action\\downloads_dir\\My collector fun-med-avata-sole.wmv")).toString();
		String absolutePathString2 = (new File("/Movies/Woot and Cool/Way/mods.action/downloads_dir/My collector fun-med-avata-sole.wmv")).toString();

		String[] tokens = absolutePathString.split(splitRegEx);
		String[] tokens2 = absolutePathString2.split(splitRegEx);

		for (String token : tokens) {
			System.out.println(token);
		}
		System.out.println("*nix path:");
		for (String token : tokens2) {
			System.out.println(token);
		}
	}


	@Override
	public Set<Tag> guessTags(MediaItem media){

		Set<Tag> matchingTags = new HashSet<Tag>();

		MediaSource source = media.getSource();
		if(source instanceof FileMediaSource)
		{
			FileMediaSource fileSource = (FileMediaSource)source;
				
			String absolutePathString = fileSource.getAbsoluteFilePath().toString().toLowerCase();

			String[] tokens = absolutePathString.split(splitRegEx);
			Tag currentTag;
			for (String token : tokens) {
				currentTag = avaiableTagsMap.get(token);
				if(currentTag != null)
					matchingTags.add(currentTag);
			}
		}

		return matchingTags;
	}



}
