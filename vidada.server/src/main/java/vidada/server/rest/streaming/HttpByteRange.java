package vidada.server.rest.streaming;


class HttpByteRange {

	public static HttpByteRange parse(String range){
		long startFrom = 0, endAt = -1;
		if(range != null){
			if(range.contains("=")){
				range = range.split("=")[1];
			}
			String rangeDef = range.trim();
			String[] rangeValues = rangeDef.split("-");

			if(rangeValues.length > 0){
				startFrom = Long.parseLong(rangeValues[0].trim());
			}

			if(rangeValues.length > 1){
				String endStr = rangeValues[1].trim();
				if(!endStr.isEmpty()) endAt = Long.parseLong(endStr);
			}
		}
		startFrom = Math.max(startFrom, 0);
		endAt = Math.max(endAt, 0);
		return new HttpByteRange(startFrom, endAt);
	}

	public long from;
	public long to;

	/**
	 * Creates a new byte range.
	 * @param from Range start index
	 * @param to Range end index
	 */
	public HttpByteRange(long from, long to){
		this.from = from;
		this.to = to;
	}

	/**
	 * Returns the lenght of this byte range
	 * @return
	 */
	public long getRangeLenght(){
		return this.to - this.from;
	}
	
	/**
	 * Returns a Content-Range conform byte range declaration
	 * @param totalLenght
	 * @return
	 */
	public String toByteRange(long totalLenght){
		return "bytes " + from + "-" + to + "/" + totalLenght;
	}
	
	@Override
	public String toString(){
		return from + "-" + to;
	}
}
