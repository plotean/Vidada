package vidada.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReferenceCounter<T> {

	Map<T, Integer> referenceOccurences = new HashMap<T, Integer>();

	public void incrementRef(T ref){
		Integer refCount = referenceOccurences.get(ref);
		if(refCount == null) refCount = 0;
		referenceOccurences.put(ref, refCount+1);
	}

	public void decrementRef(T ref){
		Integer refCount = referenceOccurences.get(ref);
		if(refCount == null || refCount == 1){
			referenceOccurences.remove(ref);
		}
	}

	public Set<T> getReferences(){
		return referenceOccurences.keySet();
	}
}
