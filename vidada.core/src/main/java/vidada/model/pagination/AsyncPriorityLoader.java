package vidada.model.pagination;

import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AsyncPriorityLoader {

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	private final Object tasksLock = new Object();
	private final Stack<Runnable> tasks = new Stack<Runnable>();


	public AsyncPriorityLoader(){ }


	public void loadHighPriority(Runnable runnable){
		synchronized (tasksLock) {
			tasks.push(runnable);
		}
		executorService.execute(topTaskExecuter);
	}

	/**
	 * Executes the current top task when invoket
	 */
	private final Runnable topTaskExecuter = new Runnable() {
		@Override
		public void run() {
			Runnable task = null;
			synchronized (tasksLock) {
				if(!tasks.isEmpty()){
					task = tasks.pop();
				}
			}
			task.run();
		}
	};

}
