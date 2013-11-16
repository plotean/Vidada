package vidada.views.tags;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import vidada.model.tags.TagState;
import archimedesJ.swing.components.JMultiStateCheckBox.MultiCheckState;

public final class TagStateWrapper {

	private static Map<MultiCheckState, TagState> viewMap; 
	private static Map<TagState, MultiCheckState> modelMap = new HashMap<TagState, MultiCheckState>() 
			{{
				put(TagState.Allowed,    MultiCheckState.Unchecked);
				put(TagState.Required,    MultiCheckState.Checked);
				put(TagState.Blocked,    MultiCheckState.ReverseChecked);
				put(TagState.Indeterminate,    MultiCheckState.Indeterminate);
				put(TagState.Unavaiable,    MultiCheckState.Disabled);
			}};

			static {
				for (Entry<TagState, MultiCheckState> entry : modelMap.entrySet()) {
					viewMap.put(entry.getValue(), entry.getKey());
				}
			}

			public static TagState toModelState(MultiCheckState multiCheckState){
				TagState state = viewMap.get(multiCheckState);
				return state != null ? state : TagState.None;
			}

			public static MultiCheckState toViewState(TagState multiCheckState){
				MultiCheckState state = modelMap.get(multiCheckState);
				return state != null ? state : MultiCheckState.Unchecked;
			}
}
