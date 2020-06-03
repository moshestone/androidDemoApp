package androidx.navigation;

import android.os.Bundle;
import androidx.navigation.Navigator.Extras;
import androidx.navigation.Navigator.Name;

@Name("NoOp")
class NoOpNavigator extends Navigator<NavDestination> {
    NoOpNavigator() {
    }

    public NavDestination createDestination() {
        return new NavDestination((Navigator<? extends NavDestination>) this);
    }

    public NavDestination navigate(NavDestination destination, Bundle args, NavOptions navOptions, Extras navigatorExtras) {
        return destination;
    }

    public boolean popBackStack() {
        return true;
    }
}
