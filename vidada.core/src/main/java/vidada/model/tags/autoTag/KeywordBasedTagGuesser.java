package vidada.model.tags.autoTag;

import vidada.model.media.MediaItem;
import vidada.model.media.source.MediaSource;
import vidada.model.tags.Tag;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This Tag-Guessing strategy finds matching tags based on keywords.
 *
 * @author IsNull
 *
 */
public class KeywordBasedTagGuesser  implements ITagGuessingStrategy {

	private static final String splitRegEx = "\\W|_";
	private static final String splitPathRegex = "/|\\\\";
    private static final int MAX_RECOMBINATION_DEEPTH = 4;

	private Collection<Tag> tags;

	/**
	 * Creates a new KeywordBasedTagGuesser
	 * @param tags
	 */
	public KeywordBasedTagGuesser(Collection<Tag> tags){
		this.tags = tags;
	}

	@Override
	public Set<Tag> guessTags(MediaItem media) {
		Set<Tag> matchingTags = new HashSet<Tag>();

		final Set<String> possibleTags = getPossibleTagStrings(media);

		for (Tag tag : tags) {
			if(possibleTags.contains(tag.getName())){
				matchingTags.add(tag);
			}
		}

		return matchingTags;
	}

    /**
     * Returns all possible keywords (tokens) derived from this media item.
     * @param media
     * @return
     */
    private static Set<String> getPossibleTagStrings(MediaItem media) {
        MediaSource source = media.getSource();
        String path = source.getResourceLocation().toString();
        try {
            path = URLDecoder.decode(path, "utf-8");
            return  getPossibleTagStrings(path);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(); // should never happen
        }
        return new HashSet<String>();
    }

    /**
     * Returns all possible String tokens which might be a Tag
     * @return
     */
	private static Set<String> getPossibleTagStrings(String path){

        Set<String> possibleTags = new HashSet<String>();

        path = path.toLowerCase();

        //split the path in node tokens
        String[] pathParts = path.split(splitPathRegex);

        for(String pathPart : pathParts ){
            if(pathPart.isEmpty()) continue;

            //split the part in single words
            String[] words = pathPart.split(splitRegEx);

            // each word can be a tag
            for (String word : words)
                possibleTags.add(word);
            
            // Recombine ranges

            recombineTags(words, possibleTags, MAX_RECOMBINATION_DEEPTH);
        }

		return possibleTags;
	}

    /**
     * Recombine neighbour words into connected tags.
     *
     * Example: "Game of Thrones" will generate:
     * ----------------
     * game.of
     * game.of.thrones
     * of.thrones
     * ----------------
     * @param words The base words to recombine
     * @param store The Set to which a recombination shall be appended
     * @param maxDeepness Recombination max length
     */
    private static void recombineTags(String[] words, Set<String> store, int maxDeepness){
        String combined;
        for (int i = 0; i < words.length; i++) {
            combined = words[i];
            if(!combined.isEmpty()) {
                for (int j = i + 1; j < words.length; j++) {

                    if(j-i > maxDeepness) break;

                    if(!words[j].isEmpty()) {
                        combined += "." + words[j];
                        store.add(combined);
                    }
                }
            }
        }
    }
    

    public static void main(String[] args){
        // Just some fantasy file path
        String samplePath = "/root/action/Game of Thrones (Season 1) [action super.hero 720p] cool.avi";
        System.out.println("Analyzing path: " + samplePath);

        Set<String> tags = getPossibleTagStrings(samplePath);

        for(String tag : tags ){
           System.out.println(tag);
        }
    }

}
