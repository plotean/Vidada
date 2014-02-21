package vidada.model.jobs;

/**
 * A job represents an asynchronous running task.
 * @author IsNull
 *
 */
public class Job {

	private int id;


	public Job(int id){
		this.id = id;
	}


	public int getId(){
		return id;
	}

	protected void setId(int id){
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Job other = (Job) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
