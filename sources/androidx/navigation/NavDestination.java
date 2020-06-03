package androidx.navigation;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import androidx.collection.SparseArrayCompat;
import androidx.navigation.common.R;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class NavDestination {
    private static final HashMap<String, Class> sClasses = new HashMap<>();
    private SparseArrayCompat<NavAction> mActions;
    private HashMap<String, NavArgument> mArguments;
    private ArrayList<NavDeepLink> mDeepLinks;
    private int mId;
    private String mIdName;
    private CharSequence mLabel;
    private final String mNavigatorName;
    private NavGraph mParent;

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.CLASS)
    public @interface ClassType {
        Class value();
    }

    static class DeepLinkMatch implements Comparable<DeepLinkMatch> {
        private final NavDestination mDestination;
        private final boolean mIsExactDeepLink;
        private final Bundle mMatchingArgs;

        DeepLinkMatch(NavDestination destination, Bundle matchingArgs, boolean isExactDeepLink) {
            this.mDestination = destination;
            this.mMatchingArgs = matchingArgs;
            this.mIsExactDeepLink = isExactDeepLink;
        }

        /* access modifiers changed from: 0000 */
        public NavDestination getDestination() {
            return this.mDestination;
        }

        /* access modifiers changed from: 0000 */
        public Bundle getMatchingArgs() {
            return this.mMatchingArgs;
        }

        public int compareTo(DeepLinkMatch other) {
            if (this.mIsExactDeepLink && !other.mIsExactDeepLink) {
                return 1;
            }
            if (this.mIsExactDeepLink || !other.mIsExactDeepLink) {
                return this.mMatchingArgs.size() - other.mMatchingArgs.size();
            }
            return -1;
        }
    }

    protected static <C> Class<? extends C> parseClassFromName(Context context, String name, Class<? extends C> expectedClassType) {
        if (name.charAt(0) == '.') {
            StringBuilder sb = new StringBuilder();
            sb.append(context.getPackageName());
            sb.append(name);
            name = sb.toString();
        }
        Class clazz = (Class) sClasses.get(name);
        if (clazz == null) {
            try {
                clazz = Class.forName(name, true, context.getClassLoader());
                sClasses.put(name, clazz);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (expectedClassType.isAssignableFrom(clazz)) {
            return clazz;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(name);
        sb2.append(" must be a subclass of ");
        sb2.append(expectedClassType);
        throw new IllegalArgumentException(sb2.toString());
    }

    static String getDisplayName(Context context, int id) {
        try {
            return context.getResources().getResourceName(id);
        } catch (NotFoundException e) {
            return Integer.toString(id);
        }
    }

    public final Map<String, NavArgument> getArguments() {
        HashMap<String, NavArgument> hashMap = this.mArguments;
        if (hashMap == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(hashMap);
    }

    public NavDestination(Navigator<? extends NavDestination> navigator) {
        this(NavigatorProvider.getNameForNavigator(navigator.getClass()));
    }

    public NavDestination(String navigatorName) {
        this.mNavigatorName = navigatorName;
    }

    public void onInflate(Context context, AttributeSet attrs) {
        TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.Navigator);
        setId(a.getResourceId(R.styleable.Navigator_android_id, 0));
        this.mIdName = getDisplayName(context, this.mId);
        setLabel(a.getText(R.styleable.Navigator_android_label));
        a.recycle();
    }

    /* access modifiers changed from: 0000 */
    public final void setParent(NavGraph parent) {
        this.mParent = parent;
    }

    public final NavGraph getParent() {
        return this.mParent;
    }

    public final int getId() {
        return this.mId;
    }

    public final void setId(int id) {
        this.mId = id;
        this.mIdName = null;
    }

    /* access modifiers changed from: 0000 */
    public String getDisplayName() {
        if (this.mIdName == null) {
            this.mIdName = Integer.toString(this.mId);
        }
        return this.mIdName;
    }

    public final void setLabel(CharSequence label) {
        this.mLabel = label;
    }

    public final CharSequence getLabel() {
        return this.mLabel;
    }

    public final String getNavigatorName() {
        return this.mNavigatorName;
    }

    public final void addDeepLink(String uriPattern) {
        if (this.mDeepLinks == null) {
            this.mDeepLinks = new ArrayList<>();
        }
        this.mDeepLinks.add(new NavDeepLink(uriPattern));
    }

    /* access modifiers changed from: 0000 */
    public DeepLinkMatch matchDeepLink(Uri uri) {
        ArrayList<NavDeepLink> arrayList = this.mDeepLinks;
        if (arrayList == null) {
            return null;
        }
        DeepLinkMatch bestMatch = null;
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            NavDeepLink deepLink = (NavDeepLink) it.next();
            Bundle matchingArguments = deepLink.getMatchingArguments(uri, getArguments());
            if (matchingArguments != null) {
                DeepLinkMatch newMatch = new DeepLinkMatch(this, matchingArguments, deepLink.isExactDeepLink());
                if (bestMatch == null || newMatch.compareTo(bestMatch) > 0) {
                    bestMatch = newMatch;
                }
            }
        }
        return bestMatch;
    }

    /* access modifiers changed from: 0000 */
    public int[] buildDeepLinkIds() {
        ArrayDeque<NavDestination> hierarchy = new ArrayDeque<>();
        NavDestination current = this;
        do {
            NavGraph parent = current.getParent();
            if (parent == 0 || parent.getStartDestination() != current.getId()) {
                hierarchy.addFirst(current);
            }
            current = parent;
        } while (current != 0);
        int[] deepLinkIds = new int[hierarchy.size()];
        int index = 0;
        Iterator it = hierarchy.iterator();
        while (it.hasNext()) {
            int index2 = index + 1;
            deepLinkIds[index] = ((NavDestination) it.next()).getId();
            index = index2;
        }
        return deepLinkIds;
    }

    /* access modifiers changed from: 0000 */
    public boolean supportsActions() {
        return true;
    }

    public final NavAction getAction(int id) {
        SparseArrayCompat<NavAction> sparseArrayCompat = this.mActions;
        NavAction destination = sparseArrayCompat == null ? null : (NavAction) sparseArrayCompat.get(id);
        if (destination != null) {
            return destination;
        }
        if (getParent() != null) {
            return getParent().getAction(id);
        }
        return null;
    }

    public final void putAction(int actionId, int destId) {
        putAction(actionId, new NavAction(destId));
    }

    public final void putAction(int actionId, NavAction action) {
        if (!supportsActions()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot add action ");
            sb.append(actionId);
            sb.append(" to ");
            sb.append(this);
            sb.append(" as it does not support actions, indicating that it is a terminal destination in your navigation graph and will never trigger actions.");
            throw new UnsupportedOperationException(sb.toString());
        } else if (actionId != 0) {
            if (this.mActions == null) {
                this.mActions = new SparseArrayCompat<>();
            }
            this.mActions.put(actionId, action);
        } else {
            throw new IllegalArgumentException("Cannot have an action with actionId 0");
        }
    }

    public final void removeAction(int actionId) {
        SparseArrayCompat<NavAction> sparseArrayCompat = this.mActions;
        if (sparseArrayCompat != null) {
            sparseArrayCompat.delete(actionId);
        }
    }

    public final void addArgument(String argumentName, NavArgument argument) {
        if (this.mArguments == null) {
            this.mArguments = new HashMap<>();
        }
        this.mArguments.put(argumentName, argument);
    }

    public final void removeArgument(String argumentName) {
        HashMap<String, NavArgument> hashMap = this.mArguments;
        if (hashMap != null) {
            hashMap.remove(argumentName);
        }
    }

    /* access modifiers changed from: 0000 */
    public Bundle addInDefaultArgs(Bundle args) {
        Bundle defaultArgs = new Bundle();
        HashMap<String, NavArgument> hashMap = this.mArguments;
        if (hashMap != null) {
            for (Entry<String, NavArgument> argument : hashMap.entrySet()) {
                ((NavArgument) argument.getValue()).putDefaultValue((String) argument.getKey(), defaultArgs);
            }
        }
        if (args == null && defaultArgs.isEmpty()) {
            return null;
        }
        if (args != null) {
            defaultArgs.putAll(args);
            HashMap<String, NavArgument> hashMap2 = this.mArguments;
            if (hashMap2 != null) {
                for (Entry<String, NavArgument> argument2 : hashMap2.entrySet()) {
                    if (!((NavArgument) argument2.getValue()).verify((String) argument2.getKey(), args)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Wrong argument type for '");
                        sb.append((String) argument2.getKey());
                        sb.append("' in argument bundle. ");
                        sb.append(((NavArgument) argument2.getValue()).getType().getName());
                        sb.append(" expected.");
                        throw new IllegalArgumentException(sb.toString());
                    }
                }
            }
        }
        return defaultArgs;
    }
}
