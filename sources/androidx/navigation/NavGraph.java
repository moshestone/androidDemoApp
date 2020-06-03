package androidx.navigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import androidx.collection.SparseArrayCompat;
import androidx.navigation.common.R;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class NavGraph extends NavDestination implements Iterable<NavDestination> {
    final SparseArrayCompat<NavDestination> mNodes = new SparseArrayCompat<>();
    private int mStartDestId;
    private String mStartDestIdName;

    public NavGraph(Navigator<? extends NavGraph> navGraphNavigator) {
        super(navGraphNavigator);
    }

    public void onInflate(Context context, AttributeSet attrs) {
        super.onInflate(context, attrs);
        TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.NavGraphNavigator);
        setStartDestination(a.getResourceId(R.styleable.NavGraphNavigator_startDestination, 0));
        this.mStartDestIdName = getDisplayName(context, this.mStartDestId);
        a.recycle();
    }

    /* access modifiers changed from: 0000 */
    public DeepLinkMatch matchDeepLink(Uri uri) {
        DeepLinkMatch bestMatch = super.matchDeepLink(uri);
        Iterator it = iterator();
        while (it.hasNext()) {
            DeepLinkMatch childBestMatch = ((NavDestination) it.next()).matchDeepLink(uri);
            if (childBestMatch != null && (bestMatch == null || childBestMatch.compareTo(bestMatch) > 0)) {
                bestMatch = childBestMatch;
            }
        }
        return bestMatch;
    }

    public final void addDestination(NavDestination node) {
        if (node.getId() != 0) {
            NavDestination existingDestination = (NavDestination) this.mNodes.get(node.getId());
            if (existingDestination != node) {
                if (node.getParent() == null) {
                    if (existingDestination != null) {
                        existingDestination.setParent(null);
                    }
                    node.setParent(this);
                    this.mNodes.put(node.getId(), node);
                    return;
                }
                throw new IllegalStateException("Destination already has a parent set. Call NavGraph.remove() to remove the previous parent.");
            }
            return;
        }
        throw new IllegalArgumentException("Destinations must have an id. Call setId() or include an android:id in your navigation XML.");
    }

    public final void addDestinations(Collection<NavDestination> nodes) {
        for (NavDestination node : nodes) {
            if (node != null) {
                addDestination(node);
            }
        }
    }

    public final void addDestinations(NavDestination... nodes) {
        for (NavDestination node : nodes) {
            if (node != null) {
                addDestination(node);
            }
        }
    }

    public final NavDestination findNode(int resid) {
        return findNode(resid, true);
    }

    /* access modifiers changed from: 0000 */
    public final NavDestination findNode(int resid, boolean searchParents) {
        NavDestination destination = (NavDestination) this.mNodes.get(resid);
        if (destination != null) {
            return destination;
        }
        if (!searchParents || getParent() == null) {
            return null;
        }
        return getParent().findNode(resid);
    }

    public final Iterator<NavDestination> iterator() {
        return new Iterator<NavDestination>() {
            private int mIndex = -1;
            private boolean mWentToNext = false;

            public boolean hasNext() {
                return this.mIndex + 1 < NavGraph.this.mNodes.size();
            }

            public NavDestination next() {
                if (hasNext()) {
                    this.mWentToNext = true;
                    SparseArrayCompat<NavDestination> sparseArrayCompat = NavGraph.this.mNodes;
                    int i = this.mIndex + 1;
                    this.mIndex = i;
                    return (NavDestination) sparseArrayCompat.valueAt(i);
                }
                throw new NoSuchElementException();
            }

            public void remove() {
                if (this.mWentToNext) {
                    ((NavDestination) NavGraph.this.mNodes.valueAt(this.mIndex)).setParent(null);
                    NavGraph.this.mNodes.removeAt(this.mIndex);
                    this.mIndex--;
                    this.mWentToNext = false;
                    return;
                }
                throw new IllegalStateException("You must call next() before you can remove an element");
            }
        };
    }

    public final void addAll(NavGraph other) {
        Iterator<NavDestination> iterator = other.iterator();
        while (iterator.hasNext()) {
            NavDestination destination = (NavDestination) iterator.next();
            iterator.remove();
            addDestination(destination);
        }
    }

    public final void remove(NavDestination node) {
        int index = this.mNodes.indexOfKey(node.getId());
        if (index >= 0) {
            ((NavDestination) this.mNodes.valueAt(index)).setParent(null);
            this.mNodes.removeAt(index);
        }
    }

    public final void clear() {
        Iterator<NavDestination> iterator = iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    /* access modifiers changed from: 0000 */
    public String getDisplayName() {
        return getId() != 0 ? super.getDisplayName() : "the root navigation";
    }

    public final int getStartDestination() {
        return this.mStartDestId;
    }

    public final void setStartDestination(int startDestId) {
        this.mStartDestId = startDestId;
        this.mStartDestIdName = null;
    }

    /* access modifiers changed from: 0000 */
    public String getStartDestDisplayName() {
        if (this.mStartDestIdName == null) {
            this.mStartDestIdName = Integer.toString(this.mStartDestId);
        }
        return this.mStartDestIdName;
    }
}
