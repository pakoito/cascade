package com.futurice.cascade.active;

import android.support.annotation.NonNull;

import com.futurice.cascade.Async;
import com.futurice.cascade.i.IAction;
import com.futurice.cascade.i.IActionOne;
import com.futurice.cascade.i.IAltFuture;
import com.futurice.cascade.i.IThreadType;
import com.futurice.cascade.i.NotCallOrigin;
import com.futurice.cascade.util.RCLog;

/**
 * The on-cancelled action in a chain will be launched asynchonosly
 * <p>
 * Cancelled notifications are not consumed. All downchain items will also receive the
 * {@link #doOnCancelled(StateCancelled)} notification call synchronously.
 *
 * Cancellation may occur from any thread. In the event of concurrent cancellation, {@link #doOnCancelled(StateCancelled)}
 * will be called exactly one time.
 */
public class OnCancelledAltFuture<IN, OUT> extends RunnableAltFuture<IN, OUT> {
    /**
     * Constructor
     *
     * @param threadType the thread pool to run this command on
     * @param action     a function that receives one input and no return from
     */
    @SuppressWarnings("unchecked")
    public OnCancelledAltFuture(@NonNull IThreadType threadType,
                                @NonNull IActionOne<String> action) {
        super(threadType, (IActionOne<IN>) action);
    }

    @NotCallOrigin
    @Override // IAltFuture
    public void doOnCancelled(@NonNull final StateCancelled stateCancelled) throws Exception {
        RCLog.d(this, "Handling doOnCancelled(): " + stateCancelled);

        if (!this.mStateAR.compareAndSet(ZEN, stateCancelled) || (Async.USE_FORKED_STATE && !this.mStateAR.compareAndSet(FORKED, stateCancelled))) {
            RCLog.i(this, "Will not doOnCancelled() because IAltFuture state is already determined: " + mStateAR.get());
            return;
        }

        @SuppressWarnings("unchecked")
        final IAltFuture<?, String> altFuture = mThreadType
                .from(stateCancelled.getReason())
                .then((IAction<String>) this);

        final Exception e = forEachThen(af -> {
            af.doOnCancelled(stateCancelled);
        });

        if (e != null) {
            throw e;
        }
    }
}
