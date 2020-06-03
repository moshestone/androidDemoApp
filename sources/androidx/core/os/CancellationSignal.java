package androidx.core.os;

import android.os.Build.VERSION;

public final class CancellationSignal {
    private boolean mCancelInProgress;
    private Object mCancellationSignalObj;
    private boolean mIsCanceled;
    private OnCancelListener mOnCancelListener;

    public interface OnCancelListener {
        void onCancel();
    }

    public boolean isCanceled() {
        boolean z;
        synchronized (this) {
            z = this.mIsCanceled;
        }
        return z;
    }

    public void throwIfCanceled() {
        if (isCanceled()) {
            throw new OperationCanceledException();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0014, code lost:
        if (r1 == null) goto L_0x001c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r1.onCancel();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x001a, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x001c, code lost:
        if (r2 == null) goto L_0x0036;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0022, code lost:
        if (android.os.Build.VERSION.SDK_INT < 16) goto L_0x0036;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0024, code lost:
        ((android.os.CancellationSignal) r2).cancel();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x002b, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        r6.mCancelInProgress = false;
        notifyAll();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0032, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0036, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        r6.mCancelInProgress = false;
        notifyAll();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x003c, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x003e, code lost:
        return;
     */
    public void cancel() {
        synchronized (this) {
            Object obj = null;
            try {
                if (!this.mIsCanceled) {
                    this.mIsCanceled = true;
                    this.mCancelInProgress = true;
                    OnCancelListener listener = this.mOnCancelListener;
                    try {
                        Object obj2 = this.mCancellationSignalObj;
                    } catch (Throwable th) {
                        Throwable th2 = th;
                        Object obj3 = obj;
                        OnCancelListener onCancelListener = listener;
                        th = th2;
                        while (true) {
                            try {
                                break;
                            } catch (Throwable th3) {
                                th = th3;
                            }
                        }
                        throw th;
                    }
                    try {
                    } catch (Throwable th4) {
                        OnCancelListener onCancelListener2 = listener;
                        th = th4;
                        OnCancelListener onCancelListener3 = onCancelListener2;
                        while (true) {
                            break;
                        }
                        throw th;
                    }
                }
            } catch (Throwable th5) {
                th = th5;
                Object obj4 = obj;
                while (true) {
                    break;
                }
                throw th;
            }
        }
    }

    public void setOnCancelListener(OnCancelListener listener) {
        synchronized (this) {
            waitForCancelFinishedLocked();
            if (this.mOnCancelListener != listener) {
                this.mOnCancelListener = listener;
                if (this.mIsCanceled) {
                    if (listener != null) {
                        listener.onCancel();
                    }
                }
            }
        }
    }

    public Object getCancellationSignalObject() {
        Object obj;
        if (VERSION.SDK_INT < 16) {
            return null;
        }
        synchronized (this) {
            if (this.mCancellationSignalObj == null) {
                android.os.CancellationSignal cancellationSignal = new android.os.CancellationSignal();
                this.mCancellationSignalObj = cancellationSignal;
                if (this.mIsCanceled) {
                    cancellationSignal.cancel();
                }
            }
            obj = this.mCancellationSignalObj;
        }
        return obj;
    }

    private void waitForCancelFinishedLocked() {
        while (this.mCancelInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }
}
