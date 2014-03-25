package vidada.viewsFX.filters;

import vidada.client.viewmodel.tags.TagViewModel;
import vidada.model.tags.Tag;
import vidada.model.tags.TagFactory;
import vidada.viewsFX.controls.TagItPanel;

/**
 * Created by IsNull on 25.03.14.
 */
public class AdvancedTagItPanel extends TagItPanel<TagViewModel> {

    public AdvancedTagItPanel(){
        this.setTagModelFactory(text -> {
            Tag tag = TagFactory.instance().createTag(text);
            return new TagViewModel(tag);
        });
    }

}
