package com.ziqi.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.os.Handler;

public class ThreadUtils {

	public final static String TAG = "ThreadUtils";

	private volatile static ThreadUtils instance;

	private Context context;

	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

	private ExecutorService service;

	private Handler mHandler;

	private ThreadUtils(Context context) {
		super();
		this.context = context;
		service = Executors.newFixedThreadPool(CPU_COUNT * 2);
		mHandler = new Handler(context.getMainLooper());
	}

	public static ThreadUtils getInstance(Context context) {
		ThreadUtils tmp = instance;
		if (tmp == null) {
			synchronized (TAG) {
				tmp = instance;
				if (tmp == null) {
					tmp = new ThreadUtils(context.getApplicationContext());
					instance = tmp;
				}
			}
		}
		return tmp;
	}

	public void pushRunnable(Callable<?> runnable) {
		Future<?> submit = service.submit(runnable);
	}

	public void pushRunnable(Runnable runnable) {
		Future<?> submit = service.submit(runnable);
	}

	public void pushRunnable(final Task asyncTask) {
		service.submit(new Runnable() {

			@Override
			public void run() {
				// 执行非ui线程
				asyncTask.doInBackground(context);
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// 告诉主线程
						asyncTask.onPostExecute(context);
					}
				}, 100);
			}
		});
	}

	public Handler getHandler() {
		return mHandler;
	}

	public interface Task {
		public void doInBackground(Context context);

		public void onPostExecute(Context context);
	}
}
