package net.teamfruit.discord.keeprole;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import sx.blah.discord.util.RateLimitException;

public class RetryRunner {
	private static final ListeningScheduledExecutorService executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadScheduledExecutor());

	public static void retry(final Callable<Boolean> task, final int retrycount) {
		Futures.addCallback(executor.submit(task), new FutureCallback<Boolean>() {
			@Override
			public void onSuccess(final Boolean result) {
				if (result!=null&&result)
					return;
				runRetry();
			}

			@Override
			public void onFailure(final Throwable t) {
				try {
					throw t;
				} catch (final RateLimitException e) {
					runRetry();
				} catch (final Throwable e) {
					e.printStackTrace();
				}
			}

			private void runRetry() {
				executor.schedule(() -> {
					retry(task, retrycount-1);
				}, 100, TimeUnit.MILLISECONDS);
			}
		}, MoreExecutors.directExecutor());
	}

	public static void retry(final Callable<Boolean> task) {
		retry(task, 4);
	}
}
