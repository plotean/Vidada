package vidada.server.rest.streaming;


class HeaderRange {

	public static HeaderRange parse(String range){
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
		return new HeaderRange(startFrom, endAt);
	}

	public long from;
	public long to;

	public HeaderRange(long from, long to){
		this.from = from;
		this.to = to;
	}
}
