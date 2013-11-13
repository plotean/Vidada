package vidada.model.filters;

import java.util.List;

import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.expressions.Predicate;
import archimedesJ.threading.CancelableTask;
import archimedesJ.threading.CancellationTokenSource.CancellationToken;
import archimedesJ.threading.CancellationTokenSource.OperationCanceledException;
import archimedesJ.util.Lists;

import com.db4o.query.Query;

/**
 * Represents a cancelable task which executes a query
 * @author IsNull
 *
 * @param <T>
 */
public class AsyncFetchData<T> extends CancelableTask<List<T>>{


	public static class CancelTokenEventArgs<T>  extends EventArgsG<T>
	{
		private final CancellationToken token;

		public CancelTokenEventArgs(T val, CancellationToken token) {
			super(val);
			this.token = token;
		}

		public CancellationToken getToken() {
			return token;
		}
	}

	private EventHandlerEx<CancelTokenEventArgs<List<T>>> fetchingCompleteEvent = new  EventHandlerEx<CancelTokenEventArgs<List<T>>>();

	/**
	 * Raised when the fetching of the data has been completed
	 * @return
	 */
	public IEvent<CancelTokenEventArgs<List<T>>> getFetchingCompleteEvent(){return fetchingCompleteEvent; }

	private final Query query;
	private Predicate<T> postFilter;

	public AsyncFetchData(Query query, CancellationToken token){
		super(token);
		this.query = query;
	}

	public void setPostFilter(Predicate<T> filter){
		this.postFilter = filter;
	}

	@Override
	public List<T> runCancelable(CancellationToken token) throws OperationCanceledException {

		List<T> fetchedData = null;

		token.ThrowIfCancellationRequested();

		if(!token.isCancellationRequested())
		{
			try {
				System.out.println("fetching media datas...");
				fetchedData = query.execute();
				fetchedData = Lists.toList(fetchedData);
				System.out.println("fetched " + fetchedData.size() + "items" );
				if(postFilter != null){
					System.out.println("applying post filter to media datas...");
					fetchedData = Lists.filter(fetchedData, postFilter);
					System.out.println("filter done: item count is " + fetchedData.size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		token.ThrowIfCancellationRequested();

		fetchingCompleteEvent.fireEvent(this, new CancelTokenEventArgs<List<T>>(fetchedData, token));

		return fetchedData;
	}

}
