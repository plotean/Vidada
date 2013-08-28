package vidada.model.tags.autoTag;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vidada.model.media.MediaItem;
import vidada.model.media.source.FileMediaSource;
import vidada.model.media.source.MediaSource;
import vidada.model.tags.Tag;
import vidada.model.tags.TagKeyoword;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.util.Debug;
import archimedesJ.util.Lists;

/**
 * KeywordBasedTagGuesser
 * @author IsNull
 *
 */
public class KeywordBasedTagGuesser  implements ITagGuessingStrategy {

	private static String splitRegEx = "\\W|_";	
	private static String splitPathRegex = "/|\\\\";

	private List<Tag> tags;

	/**
	 * Creates a new 
	 * @param tagService
	 */
	public KeywordBasedTagGuesser(List<Tag> tags){
		this.tags = tags;
		System.out.println("KeywordBasedTagGuesser using tag-set:");
		Debug.printAll(tags);

		if(tags.isEmpty())
			throw new NotSupportedException("tags: KeywordBasedTagGuesser needs at least one tag in the search set.");

	}

	@Override
	public Set<Tag> guessTags(MediaItem media) {
		Set<Tag> matchingTags = new HashSet<Tag>();

		for (Tag tag : tags) {
			if(doesMediaMatchTag(media, tag)){
				System.out.println("media " + media + " --> matches Tag: " + tag);
				matchingTags.add(tag);
			}
		}

		return matchingTags;
	}

	private boolean doesMediaMatchTag(MediaItem media, Tag tag){

		MediaSource source = media.getSource();
		if(source instanceof FileMediaSource)
		{

			// TODO Optimize / cache tokens

			FileMediaSource fileSource = (FileMediaSource)source;

			String path = fileSource.getAbsoluteFilePath().toString();
			try {
				path = URLDecoder.decode(path, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace(); // should never happen
			}
			String absolutePathString = path.toLowerCase();

			System.out.println("path: " + absolutePathString);

			//split the path in single tokens
			String[] tokens = absolutePathString.split(splitRegEx);
			//split the path in node tokens
			String[] pathTokens = absolutePathString.split(splitPathRegex);
			//combine the tokens to provide one single source file
			String [] combinedTokens = Lists.concat(tokens, pathTokens);

			Debug.printAll("combinedTokens:", combinedTokens);

			for (TagKeyoword keyword : tag.getKeyWords()) {
				for (int i = 0; i < combinedTokens.length; i++) {
					if(keyword.isMatch(combinedTokens[i]))
					{
						return true;
					}
				}
			}

		}else
			System.err.println("KeywordBasedTagGuesser: skipped media as source is not compatible: " + source);

		return false;
	}



}
