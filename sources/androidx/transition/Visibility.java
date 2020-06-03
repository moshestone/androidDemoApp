package androidx.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.res.TypedArrayUtils;
import androidx.transition.Transition.TransitionListener;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

public abstract class Visibility extends Transition {
    public static final int MODE_IN = 1;
    public static final int MODE_OUT = 2;
    private static final String PROPNAME_PARENT = "android:visibility:parent";
    private static final String PROPNAME_SCREEN_LOCATION = "android:visibility:screenLocation";
    static final String PROPNAME_VISIBILITY = "android:visibility:visibility";
    private static final String[] sTransitionProperties = {PROPNAME_VISIBILITY, PROPNAME_PARENT};
    private int mMode = 3;

    private static class DisappearListener extends AnimatorListenerAdapter implements TransitionListener, AnimatorPauseListenerCompat {
        boolean mCanceled = false;
        private final int mFinalVisibility;
        private boolean mLayoutSuppressed;
        private final ViewGroup mParent;
        private final boolean mSuppressLayout;
        private final View mView;

        DisappearListener(View view, int finalVisibility, boolean suppressLayout) {
            this.mView = view;
            this.mFinalVisibility = finalVisibility;
            this.mParent = (ViewGroup) view.getParent();
            this.mSuppressLayout = suppressLayout;
            suppressLayout(true);
        }

        public void onAnimationPause(Animator animation) {
            if (!this.mCanceled) {
                ViewUtils.setTransitionVisibility(this.mView, this.mFinalVisibility);
            }
        }

        public void onAnimationResume(Animator animation) {
            if (!this.mCanceled) {
                ViewUtils.setTransitionVisibility(this.mView, 0);
            }
        }

        public void onAnimationCancel(Animator animation) {
            this.mCanceled = true;
        }

        public void onAnimationRepeat(Animator animation) {
        }

        public void onAnimationStart(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
            hideViewWhenNotCanceled();
        }

        public void onTransitionStart(Transition transition) {
        }

        public void onTransitionEnd(Transition transition) {
            hideViewWhenNotCanceled();
            transition.removeListener(this);
        }

        public void onTransitionCancel(Transition transition) {
        }

        public void onTransitionPause(Transition transition) {
            suppressLayout(false);
        }

        public void onTransitionResume(Transition transition) {
            suppressLayout(true);
        }

        private void hideViewWhenNotCanceled() {
            if (!this.mCanceled) {
                ViewUtils.setTransitionVisibility(this.mView, this.mFinalVisibility);
                ViewGroup viewGroup = this.mParent;
                if (viewGroup != null) {
                    viewGroup.invalidate();
                }
            }
            suppressLayout(false);
        }

        private void suppressLayout(boolean suppress) {
            if (this.mSuppressLayout && this.mLayoutSuppressed != suppress) {
                ViewGroup viewGroup = this.mParent;
                if (viewGroup != null) {
                    this.mLayoutSuppressed = suppress;
                    ViewGroupUtils.suppressLayout(viewGroup, suppress);
                }
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    private static class VisibilityInfo {
        ViewGroup mEndParent;
        int mEndVisibility;
        boolean mFadeIn;
        ViewGroup mStartParent;
        int mStartVisibility;
        boolean mVisibilityChange;

        VisibilityInfo() {
        }
    }

    public Visibility() {
    }

    public Visibility(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, Styleable.VISIBILITY_TRANSITION);
        int mode = TypedArrayUtils.getNamedInt(a, (XmlResourceParser) attrs, "transitionVisibilityMode", 0, 0);
        a.recycle();
        if (mode != 0) {
            setMode(mode);
        }
    }

    public void setMode(int mode) {
        if ((mode & -4) == 0) {
            this.mMode = mode;
            return;
        }
        throw new IllegalArgumentException("Only MODE_IN and MODE_OUT flags are allowed");
    }

    public int getMode() {
        return this.mMode;
    }

    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    private void captureValues(TransitionValues transitionValues) {
        int visibility = transitionValues.view.getVisibility();
        transitionValues.values.put(PROPNAME_VISIBILITY, Integer.valueOf(visibility));
        transitionValues.values.put(PROPNAME_PARENT, transitionValues.view.getParent());
        int[] loc = new int[2];
        transitionValues.view.getLocationOnScreen(loc);
        transitionValues.values.put(PROPNAME_SCREEN_LOCATION, loc);
    }

    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public boolean isVisible(TransitionValues values) {
        boolean z = false;
        if (values == null) {
            return false;
        }
        View parent = (View) values.values.get(PROPNAME_PARENT);
        if (((Integer) values.values.get(PROPNAME_VISIBILITY)).intValue() == 0 && parent != null) {
            z = true;
        }
        return z;
    }

    private VisibilityInfo getVisibilityChangeInfo(TransitionValues startValues, TransitionValues endValues) {
        VisibilityInfo visInfo = new VisibilityInfo();
        visInfo.mVisibilityChange = false;
        visInfo.mFadeIn = false;
        String str = PROPNAME_PARENT;
        String str2 = PROPNAME_VISIBILITY;
        if (startValues == null || !startValues.values.containsKey(str2)) {
            visInfo.mStartVisibility = -1;
            visInfo.mStartParent = null;
        } else {
            visInfo.mStartVisibility = ((Integer) startValues.values.get(str2)).intValue();
            visInfo.mStartParent = (ViewGroup) startValues.values.get(str);
        }
        if (endValues == null || !endValues.values.containsKey(str2)) {
            visInfo.mEndVisibility = -1;
            visInfo.mEndParent = null;
        } else {
            visInfo.mEndVisibility = ((Integer) endValues.values.get(str2)).intValue();
            visInfo.mEndParent = (ViewGroup) endValues.values.get(str);
        }
        if (startValues == null || endValues == null) {
            if (startValues == null && visInfo.mEndVisibility == 0) {
                visInfo.mFadeIn = true;
                visInfo.mVisibilityChange = true;
            } else if (endValues == null && visInfo.mStartVisibility == 0) {
                visInfo.mFadeIn = false;
                visInfo.mVisibilityChange = true;
            }
        } else if (visInfo.mStartVisibility == visInfo.mEndVisibility && visInfo.mStartParent == visInfo.mEndParent) {
            return visInfo;
        } else {
            if (visInfo.mStartVisibility != visInfo.mEndVisibility) {
                if (visInfo.mStartVisibility == 0) {
                    visInfo.mFadeIn = false;
                    visInfo.mVisibilityChange = true;
                } else if (visInfo.mEndVisibility == 0) {
                    visInfo.mFadeIn = true;
                    visInfo.mVisibilityChange = true;
                }
            } else if (visInfo.mEndParent == null) {
                visInfo.mFadeIn = false;
                visInfo.mVisibilityChange = true;
            } else if (visInfo.mStartParent == null) {
                visInfo.mFadeIn = true;
                visInfo.mVisibilityChange = true;
            }
        }
        return visInfo;
    }

    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        VisibilityInfo visInfo = getVisibilityChangeInfo(startValues, endValues);
        if (!visInfo.mVisibilityChange || (visInfo.mStartParent == null && visInfo.mEndParent == null)) {
            return null;
        }
        if (visInfo.mFadeIn) {
            return onAppear(sceneRoot, startValues, visInfo.mStartVisibility, endValues, visInfo.mEndVisibility);
        }
        return onDisappear(sceneRoot, startValues, visInfo.mStartVisibility, endValues, visInfo.mEndVisibility);
    }

    public Animator onAppear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        if ((this.mMode & 1) != 1 || endValues == null) {
            return null;
        }
        if (startValues == null) {
            View endParent = (View) endValues.view.getParent();
            if (getVisibilityChangeInfo(getMatchedTransitionValues(endParent, false), getTransitionValues(endParent, false)).mVisibilityChange) {
                return null;
            }
        }
        return onAppear(sceneRoot, endValues.view, startValues, endValues);
    }

    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:54:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0119  */
    public Animator onDisappear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        ViewGroup viewGroup = sceneRoot;
        TransitionValues transitionValues = startValues;
        TransitionValues transitionValues2 = endValues;
        if ((this.mMode & 2) != 2) {
            return null;
        }
        View startView = transitionValues != null ? transitionValues.view : null;
        View endView = transitionValues2 != null ? transitionValues2.view : null;
        View overlayView = null;
        View viewToKeep = null;
        if (endView == null) {
            int i = endVisibility;
        } else if (endView.getParent() == null) {
            int i2 = endVisibility;
        } else {
            if (endVisibility == 4) {
                viewToKeep = endView;
            } else if (startView == endView) {
                viewToKeep = endView;
            } else {
                overlayView = this.mCanRemoveViews ? startView : TransitionUtils.copyViewImage(viewGroup, startView, (View) startView.getParent());
            }
            int finalVisibility = endVisibility;
            if (overlayView != null || transitionValues == null) {
                View view = endView;
                if (viewToKeep != null) {
                    return null;
                }
                int originalVisibility = viewToKeep.getVisibility();
                ViewUtils.setTransitionVisibility(viewToKeep, 0);
                Animator animator = onDisappear(viewGroup, viewToKeep, transitionValues, transitionValues2);
                if (animator != null) {
                    DisappearListener disappearListener = new DisappearListener(viewToKeep, finalVisibility, true);
                    animator.addListener(disappearListener);
                    AnimatorUtils.addPauseListener(animator, disappearListener);
                    addListener(disappearListener);
                } else {
                    ViewUtils.setTransitionVisibility(viewToKeep, originalVisibility);
                }
                return animator;
            }
            int[] screenLoc = (int[]) transitionValues.values.get(PROPNAME_SCREEN_LOCATION);
            int screenX = screenLoc[0];
            int screenY = screenLoc[1];
            int[] loc = new int[2];
            viewGroup.getLocationOnScreen(loc);
            overlayView.offsetLeftAndRight((screenX - loc[0]) - overlayView.getLeft());
            overlayView.offsetTopAndBottom((screenY - loc[1]) - overlayView.getTop());
            final ViewGroupOverlayImpl overlay = ViewGroupUtils.getOverlay(sceneRoot);
            overlay.add(overlayView);
            Animator animator2 = onDisappear(viewGroup, overlayView, transitionValues, transitionValues2);
            if (animator2 == null) {
                overlay.remove(overlayView);
                View view2 = startView;
                View view3 = endView;
            } else {
                View view4 = startView;
                View view5 = endView;
                final View endView2 = overlayView;
                animator2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        overlay.remove(endView2);
                    }
                });
            }
            return animator2;
        }
        if (endView != null) {
            overlayView = endView;
        } else if (startView != null) {
            if (startView.getParent() == null) {
                overlayView = startView;
            } else if (startView.getParent() instanceof View) {
                View startParent = (View) startView.getParent();
                if (!getVisibilityChangeInfo(getTransitionValues(startParent, true), getMatchedTransitionValues(startParent, true)).mVisibilityChange) {
                    overlayView = TransitionUtils.copyViewImage(viewGroup, startView, startParent);
                } else if (startParent.getParent() == null) {
                    int id = startParent.getId();
                    if (!(id == -1 || viewGroup.findViewById(id) == null || !this.mCanRemoveViews)) {
                        overlayView = startView;
                    }
                }
            }
        }
        int finalVisibility2 = endVisibility;
        if (overlayView != null) {
        }
        View view6 = endView;
        if (viewToKeep != null) {
        }
    }

    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        return null;
    }

    public boolean isTransitionRequired(TransitionValues startValues, TransitionValues newValues) {
        boolean z = false;
        if (startValues == null && newValues == null) {
            return false;
        }
        if (!(startValues == null || newValues == null)) {
            Map<String, Object> map = newValues.values;
            String str = PROPNAME_VISIBILITY;
            if (map.containsKey(str) != startValues.values.containsKey(str)) {
                return false;
            }
        }
        VisibilityInfo changeInfo = getVisibilityChangeInfo(startValues, newValues);
        if (changeInfo.mVisibilityChange && (changeInfo.mStartVisibility == 0 || changeInfo.mEndVisibility == 0)) {
            z = true;
        }
        return z;
    }
}
