package androidx.navigation;

import android.os.Bundle;
import androidx.navigation.Navigator.Extras;
import androidx.navigation.Navigator.Name;
import java.util.ArrayDeque;
import java.util.Iterator;

@Name("navigation")
public class NavGraphNavigator extends Navigator<NavGraph> {
    private static final String KEY_BACK_STACK_IDS = "androidx-nav-graph:navigator:backStackIds";
    private ArrayDeque<Integer> mBackStack = new ArrayDeque<>();
    private final NavigatorProvider mNavigatorProvider;

    public NavGraphNavigator(NavigatorProvider navigatorProvider) {
        this.mNavigatorProvider = navigatorProvider;
    }

    public NavGraph createDestination() {
        return new NavGraph(this);
    }

    public NavDestination navigate(NavGraph destination, Bundle args, NavOptions navOptions, Extras navigatorExtras) {
        int startId = destination.getStartDestination();
        if (startId != 0) {
            NavDestination startDestination = destination.findNode(startId, false);
            if (startDestination != null) {
                if (navOptions == null || !navOptions.shouldLaunchSingleTop() || !isAlreadyTop(destination)) {
                    this.mBackStack.add(Integer.valueOf(destination.getId()));
                }
                return this.mNavigatorProvider.getNavigator(startDestination.getNavigatorName()).navigate(startDestination, startDestination.addInDefaultArgs(args), navOptions, navigatorExtras);
            }
            String dest = destination.getStartDestDisplayName();
            StringBuilder sb = new StringBuilder();
            sb.append("navigation destination ");
            sb.append(dest);
            sb.append(" is not a direct child of this NavGraph");
            throw new IllegalArgumentException(sb.toString());
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("no start destination defined via app:startDestination for ");
        sb2.append(destination.getDisplayName());
        throw new IllegalStateException(sb2.toString());
    }

    private boolean isAlreadyTop(NavGraph destination) {
        if (this.mBackStack.isEmpty()) {
            return false;
        }
        int topDestId = ((Integer) this.mBackStack.peekLast()).intValue();
        NavGraph current = destination;
        while (current.getId() != topDestId) {
            NavDestination startDestination = current.findNode(current.getStartDestination());
            if (!(startDestination instanceof NavGraph)) {
                return false;
            }
            current = (NavGraph) startDestination;
        }
        return true;
    }

    public boolean popBackStack() {
        return this.mBackStack.pollLast() != null;
    }

    public Bundle onSaveState() {
        Bundle b = new Bundle();
        int[] backStack = new int[this.mBackStack.size()];
        int index = 0;
        Iterator it = this.mBackStack.iterator();
        while (it.hasNext()) {
            int index2 = index + 1;
            backStack[index] = ((Integer) it.next()).intValue();
            index = index2;
        }
        b.putIntArray(KEY_BACK_STACK_IDS, backStack);
        return b;
    }

    public void onRestoreState(Bundle savedState) {
        if (savedState != null) {
            int[] backStack = savedState.getIntArray(KEY_BACK_STACK_IDS);
            if (backStack != null) {
                this.mBackStack.clear();
                for (int destId : backStack) {
                    this.mBackStack.add(Integer.valueOf(destId));
                }
            }
        }
    }
}
