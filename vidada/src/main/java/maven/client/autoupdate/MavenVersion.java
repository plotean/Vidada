package maven.client.autoupdate;



/**
 * Represents a version in the Maven world.
 * Immutable implementation.
 * 
 * <Major>.<Minor>.<Patch>-<Qualifier>
 * Example: 0.1.2-BETA
 * 
 * @author IsNull
 *
 */
public final class MavenVersion implements Comparable<MavenVersion> {

	public static final MavenVersion INVLAID = new MavenVersion(-1, -1, -1, "INVALID");

	/**
	 * Thrown when the version format is invalid
	 * @author IsNull
	 *
	 */
	public static class VersionFormatException extends Exception {
		private static final long serialVersionUID = 1L;
		public VersionFormatException(String message){
			super(message);
		}
		public VersionFormatException(String message, Throwable t){
			super(message, t);
		}
	}

	/**
	 * Parses a maven version: <Major>.<Minor>.<Patch>-<Qualifier>
	 * Example: 0.1.2-BETA
	 * @param version
	 * @return
	 */
	public static MavenVersion parse(String version) throws VersionFormatException{
		try{
			String[] parts = version.trim().split("[\\.|-]");
			int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
			int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
			int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
			String qualifier = parts.length > 3 ? parts[3] : null;
			return new MavenVersion(major, minor, patch, qualifier);
		}catch(Exception e){
			throw new VersionFormatException("Invalid Maven-Version Format: '" + version + "'",e);
		}
	}

	private final int major;
	private final int minor;
	private final int patch;
	private final String qualifier;

	public MavenVersion(int major, int minor, int patch, String qualifier) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.qualifier = qualifier;
	}


	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getPatch() {
		return patch;
	}

	public String getQualifier() {
		return qualifier;
	}


    /**
     * Returns true if this version is valid.
     * A version must not contain any negative numbers.
     * @return
     */
    public boolean isValid(){
        return getMajor() >= 0 && getMinor() >= 0 && getPatch() >= 0;
    }

	/**
	 * Is this version newer than the given one?
	 * @param o
	 * @return
	 */
	public boolean isNewerThan(MavenVersion o){
		return this.compareTo(o) > 0;
	}

	/**
	 * Compares this Version against the given one.
	 * 
	 * @param o
	 * @return
	 */
	@Override
	public int compareTo(MavenVersion o) {
		if(getMajor() == o.getMajor()){
			if(getMinor() == o.getMinor()){
				// Note that qualifiers do not influence order
				return Integer.compare(getPatch(), o.getPatch());
			}else
				return Integer.compare(getMinor(), o.getMinor());
		}else 
			return Integer.compare(getMajor(), o.getMajor());
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + major;
		result = prime * result + minor;
		result = prime * result + patch;
		result = prime * result
				+ ((qualifier == null) ? 0 : qualifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MavenVersion other = (MavenVersion) obj;
		if (major != other.major)
			return false;
		if (minor != other.minor)
			return false;
		if (patch != other.patch)
			return false;
		if (qualifier == null) {
			if (other.qualifier != null)
				return false;
		} else if (!qualifier.equals(other.qualifier))
			return false;
		return true;
	}

	@Override
	public String toString(){
		return major + "." + minor + "." + patch 
				+ ( (qualifier != null && !qualifier.isEmpty()) 
						? "-" + qualifier 
								: "");
	}
}
