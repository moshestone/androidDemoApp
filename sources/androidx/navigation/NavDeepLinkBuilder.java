package androidx.navigation;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.TaskStackBuilder;
import androidx.navigation.Navigator.Extras;
import java.util.ArrayDeque;
import java.util.Iterator;

public final class NavDeepLinkBuilder {
    private Bundle mArgs;
    private final Context mContext;
    private int mDestId;
    private NavGraph mGraph;
    private final Intent mIntent;

    private static class PermissiveNavigatorProvider extends NavigatorProvider {
        private final Navigator<NavDestination> mDestNavigator = new Navigator<NavDestination>() {
            public NavDestination createDestination() {
                return new NavDestination("permissive");
            }

            public NavDestination navigate(NavDestination destination, Bundle args, NavOptions navOptions, Extras navigatorExtras) {
                throw new IllegalStateException("navigate is not supported");
            }

            public boolean popBackStack() {
                throw new IllegalStateException("popBackStack is not supported");
            }
        };

        PermissiveNavigatorProvider() {
            addNavigator(new NavGraphNavigator(this));
        }

        public Navigator<? extends NavDestination> getNavigator(String name) {
            try {
                return super.getNavigator(name);
            } catch (IllegalStateException e) {
                return this.mDestNavigator;
            }
        }
    }

    public NavDeepLinkBuilder(Context context) {
        this.mContext = context;
        if (context instanceof Activity) {
            Context context2 = this.mContext;
            this.mIntent = new Intent(context2, context2.getClass());
        } else {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(this.mContext.getPackageName());
            this.mIntent = launchIntent != null ? launchIntent : new Intent();
        }
        this.mIntent.addFlags(268468224);
    }

    NavDeepLinkBuilder(NavController navController) {
        this(navController.getContext());
        this.mGraph = navController.getGraph();
    }

    public NavDeepLinkBuilder setComponentName(Class<? extends Activity> activityClass) {
        return setComponentName(new ComponentName(this.mContext, activityClass));
    }

    public NavDeepLinkBuilder setComponentName(ComponentName componentName) {
        this.mIntent.setComponent(componentName);
        return this;
    }

    public NavDeepLinkBuilder setGraph(int navGraphId) {
        return setGraph(new NavInflater(this.mContext, new PermissiveNavigatorProvider()).inflate(navGraphId));
    }

    public NavDeepLinkBuilder setGraph(NavGraph navGraph) {
        this.mGraph = navGraph;
        if (this.mDestId != 0) {
            fillInIntent();
        }
        return this;
    }

    public NavDeepLinkBuilder setDestination(int destId) {
        this.mDestId = destId;
        if (this.mGraph != null) {
            fillInIntent();
        }
        return this;
    }

    private void fillInIntent() {
        NavDestination node = null;
        ArrayDeque<NavDestination> possibleDestinations = new ArrayDeque<>();
        possibleDestinations.add(this.mGraph);
        while (!possibleDestinations.isEmpty() && node == null) {
            NavDestination destination = (NavDestination) possibleDestinations.poll();
            if (destination.getId() == this.mDestId) {
                node = destination;
            } else if (destination instanceof NavGraph) {
                Iterator it = ((NavGraph) destination).iterator();
                while (it.hasNext()) {
                    possibleDestinations.add((NavDestination) it.next());
                }
            }
        }
        if (node != null) {
            this.mIntent.putExtra("android-support-nav:controller:deepLinkIds", node.buildDeepLinkIds());
            return;
        }
        String dest = NavDestination.getDisplayName(this.mContext, this.mDestId);
        StringBuilder sb = new StringBuilder();
        sb.append("navigation destination ");
        sb.append(dest);
        sb.append(" is unknown to this NavController");
        throw new IllegalArgumentException(sb.toString());
    }

    public NavDeepLinkBuilder setArguments(Bundle args) {
        this.mArgs = args;
        this.mIntent.putExtra("android-support-nav:controller:deepLinkExtras", args);
        return this;
    }

    public TaskStackBuilder createTaskStackBuilder() {
        if (this.mIntent.getIntArrayExtra("android-support-nav:controller:deepLinkIds") != null) {
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this.mContext).addNextIntentWithParentStack(new Intent(this.mIntent));
            for (int index = 0; index < taskStackBuilder.getIntentCount(); index++) {
                taskStackBuilder.editIntentAt(index).putExtra(NavController.KEY_DEEP_LINK_INTENT, this.mIntent);
            }
            return taskStackBuilder;
        } else if (this.mGraph == null) {
            throw new IllegalStateException("You must call setGraph() before constructing the deep link");
        } else {
            throw new IllegalStateException("You must call setDestination() before constructing the deep link");
        }
    }

    public PendingIntent createPendingIntent() {
        int requestCode = 0;
        Bundle bundle = this.mArgs;
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = this.mArgs.get(key);
                requestCode = (requestCode * 31) + (value != null ? value.hashCode() : 0);
            }
        }
        return createTaskStackBuilder().getPendingIntent((requestCode * 31) + this.mDestId, 134217728);
    }
}
