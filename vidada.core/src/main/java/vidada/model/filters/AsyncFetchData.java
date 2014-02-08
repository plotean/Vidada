package vidada.model.filters;

import java.util.Collection;

import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.expressions.Predicate;
import archimedesJ.threading.CancelableTask;
import archimedesJ.threading.CancellationTokenSource.CancellationToken;
import archimedesJ.threading.CancellationTokenSource.OperationCanceledException;
import archimedesJ.util.Lists;

/**
 * Represents a cancelable task which executes a query
 * @author IsNull
 *
 * @param <T>
 */
public abstract class AsyncFetchData<T> extends CancelableTask<Collection<T>>{


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

	private EventHandlerEx<CancelTokenEventArgs<Collection<T>>> fetchingCompleteEvent = new  EventHandlerEx<CancelTokenEventArgs<Collection<T>>>();

	/**
	 * Raised when the fetching of the data has been completed
	 * @return
	 */
	public IEvent<CancelTokenEventArgs<Collection<T>>> getFetchingCompleteEvent(){return fetchingCompleteEvent; }


	private Predicate<T> postFilter;

	public AsyncFetchData(CancellationToken token){
		super(token);
	}

	public void setPostFilter(Predicate<T> filter){
		this.postFilter = filter;
	}

	@Override
	public Collection<T> runCancelable(CancellationToken token) throws OperationCanceledException {

		Collection<T> fetchedData = null;

		token.ThrowIfCancellationRequested();

		if(!token.isCancellationRequested())
		{
			try {
				fetchedData = fetchData(token);

				if(postFilter != null){
					System.out.println("applying post filter to media datas...");
					fetchedData = Lists.filter(fetchedData, postFilter);
					System.out.println("filter done: item count is " + fetchedData.size());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fetchingCompleteEvent.fireEvent(this, new CancelTokenEventArgs<Collection<T>>(fetchedData, token));
		return fetchedData;
	}

	protected abstract Collection<T> fetchData(CancellationToken token) throws OperationCanceledException; 
}
