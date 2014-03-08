package vidada.model.media.source;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import vidada.model.entities.IdEntity;
import archimedesJ.io.locations.ResourceLocation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents a media source. This can be a local file, a web resource, smb shared resource
 * or anything other which can deliver a stream
 * 
 * @author IsNull
 *
 */
@JsonTypeInfo(  
		use = JsonTypeInfo.Id.NAME,  
		include = JsonTypeInfo.As.PROPERTY,  
		property = "type")  
@JsonSubTypes({  
	@Type(value = MediaSourceLocal.class, name = "MediaSourceLocal")
})  
@XmlRootElement
@Entity
public abstract class MediaSource extends IdEntity {

	@Transient
	private ResourceLocation location;

	/**
	 * Gets the media resource location 
	 * @return
	 */
	public ResourceLocation getResourceLocation(){
		return location;
	}

	/**
	 * Sets the media resource location 
	 * @return
	 */
	protected void setResourceLocation(ResourceLocation location){
		this.location = location;
	}


	/**
	 * Is the resource available?
	 * @return
	 */
	public boolean isAvailable(){
		ResourceLocation absolutePath = getResourceLocation();
		return absolutePath != null && absolutePath.exists();
	}
}