package androidx.navigation.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavController.OnDestinationChangedListener;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions.Builder;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import java.lang.ref.WeakReference;
import java.util.Set;

public final class NavigationUI {
    private NavigationUI() {
    }

    public static boolean onNavDestinationSelected(MenuItem item, NavController navController) {
        Builder builder = new Builder().setLaunchSingleTop(true).setEnterAnim(R.anim.nav_default_enter_anim).setExitAnim(R.anim.nav_default_exit_anim).setPopEnterAnim(R.anim.nav_default_pop_enter_anim).setPopExitAnim(R.anim.nav_default_pop_exit_anim);
        if ((item.getOrder() & 196608) == 0) {
            builder.setPopUpTo(findStartDestination(navController.getGraph()).getId(), false);
        }
        try {
            navController.navigate(item.getItemId(), null, builder.build());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean navigateUp(NavController navController, DrawerLayout drawerLayout) {
        return navigateUp(navController, new AppBarConfiguration.Builder(navController.getGraph()).setDrawerLayout(drawerLayout).build());
    }

    public static boolean navigateUp(NavController navController, AppBarConfiguration configuration) {
        DrawerLayout drawerLayout = configuration.getDrawerLayout();
        NavDestination currentDestination = navController.getCurrentDestination();
        Set<Integer> topLevelDestinations = configuration.getTopLevelDestinations();
        if (drawerLayout != null && currentDestination != null && matchDestinations(currentDestination, topLevelDestinations)) {
            drawerLayout.openDrawer((int) GravityCompat.START);
            return true;
        } else if (navController.navigateUp()) {
            return true;
        } else {
            if (configuration.getFallbackOnNavigateUpListener() != null) {
                return configuration.getFallbackOnNavigateUpListener().onNavigateUp();
            }
            return false;
        }
    }

    public static void setupActionBarWithNavController(AppCompatActivity activity, NavController navController) {
        setupActionBarWithNavController(activity, navController, new AppBarConfiguration.Builder(navController.getGraph()).build());
    }

    public static void setupActionBarWithNavController(AppCompatActivity activity, NavController navController, DrawerLayout drawerLayout) {
        setupActionBarWithNavController(activity, navController, new AppBarConfiguration.Builder(navController.getGraph()).setDrawerLayout(drawerLayout).build());
    }

    public static void setupActionBarWithNavController(AppCompatActivity activity, NavController navController, AppBarConfiguration configuration) {
        navController.addOnDestinationChangedListener(new ActionBarOnDestinationChangedListener(activity, configuration));
    }

    public static void setupWithNavController(Toolbar toolbar, NavController navController) {
        setupWithNavController(toolbar, navController, new AppBarConfiguration.Builder(navController.getGraph()).build());
    }

    public static void setupWithNavController(Toolbar toolbar, NavController navController, DrawerLayout drawerLayout) {
        setupWithNavController(toolbar, navController, new AppBarConfiguration.Builder(navController.getGraph()).setDrawerLayout(drawerLayout).build());
    }

    public static void setupWithNavController(Toolbar toolbar, final NavController navController, final AppBarConfiguration configuration) {
        navController.addOnDestinationChangedListener(new ToolbarOnDestinationChangedListener(toolbar, configuration));
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NavigationUI.navigateUp(navController, configuration);
            }
        });
    }

    public static void setupWithNavController(CollapsingToolbarLayout collapsingToolbarLayout, Toolbar toolbar, NavController navController) {
        setupWithNavController(collapsingToolbarLayout, toolbar, navController, new AppBarConfiguration.Builder(navController.getGraph()).build());
    }

    public static void setupWithNavController(CollapsingToolbarLayout collapsingToolbarLayout, Toolbar toolbar, NavController navController, DrawerLayout drawerLayout) {
        setupWithNavController(collapsingToolbarLayout, toolbar, navController, new AppBarConfiguration.Builder(navController.getGraph()).setDrawerLayout(drawerLayout).build());
    }

    public static void setupWithNavController(CollapsingToolbarLayout collapsingToolbarLayout, Toolbar toolbar, final NavController navController, final AppBarConfiguration configuration) {
        navController.addOnDestinationChangedListener(new CollapsingToolbarOnDestinationChangedListener(collapsingToolbarLayout, toolbar, configuration));
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NavigationUI.navigateUp(navController, configuration);
            }
        });
    }

    public static void setupWithNavController(final NavigationView navigationView, final NavController navController) {
        navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                if (handled) {
                    ViewParent parent = navigationView.getParent();
                    if (parent instanceof DrawerLayout) {
                        ((DrawerLayout) parent).closeDrawer((View) navigationView);
                    } else {
                        BottomSheetBehavior bottomSheetBehavior = NavigationUI.findBottomSheetBehavior(navigationView);
                        if (bottomSheetBehavior != null) {
                            bottomSheetBehavior.setState(5);
                        }
                    }
                }
                return handled;
            }
        });
        final WeakReference<NavigationView> weakReference = new WeakReference<>(navigationView);
        navController.addOnDestinationChangedListener(new OnDestinationChangedListener() {
            public void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
                NavigationView view = (NavigationView) weakReference.get();
                if (view == null) {
                    navController.removeOnDestinationChangedListener(this);
                    return;
                }
                Menu menu = view.getMenu();
                int size = menu.size();
                for (int h = 0; h < size; h++) {
                    MenuItem item = menu.getItem(h);
                    item.setChecked(NavigationUI.matchDestination(destination, item.getItemId()));
                }
            }
        });
    }

    static BottomSheetBehavior findBottomSheetBehavior(View view) {
        LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            ViewParent parent = view.getParent();
            if (parent instanceof View) {
                return findBottomSheetBehavior((View) parent);
            }
            return null;
        }
        Behavior behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
        if (!(behavior instanceof BottomSheetBehavior)) {
            return null;
        }
        return (BottomSheetBehavior) behavior;
    }

    public static void setupWithNavController(BottomNavigationView bottomNavigationView, final NavController navController) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {
                return NavigationUI.onNavDestinationSelected(item, navController);
            }
        });
        final WeakReference<BottomNavigationView> weakReference = new WeakReference<>(bottomNavigationView);
        navController.addOnDestinationChangedListener(new OnDestinationChangedListener() {
            public void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
                BottomNavigationView view = (BottomNavigationView) weakReference.get();
                if (view == null) {
                    navController.removeOnDestinationChangedListener(this);
                    return;
                }
                Menu menu = view.getMenu();
                int size = menu.size();
                for (int h = 0; h < size; h++) {
                    MenuItem item = menu.getItem(h);
                    if (NavigationUI.matchDestination(destination, item.getItemId())) {
                        item.setChecked(true);
                    }
                }
            }
        });
    }

    static boolean matchDestination(NavDestination destination, int destId) {
        NavDestination currentDestination = destination;
        while (currentDestination.getId() != destId && currentDestination.getParent() != null) {
            currentDestination = currentDestination.getParent();
        }
        return currentDestination.getId() == destId;
    }

    static boolean matchDestinations(NavDestination destination, Set<Integer> destinationIds) {
        NavDestination currentDestination = destination;
        while (!destinationIds.contains(Integer.valueOf(currentDestination.getId()))) {
            currentDestination = currentDestination.getParent();
            if (currentDestination == null) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=androidx.navigation.NavGraph, code=androidx.navigation.NavDestination, for r3v0, types: [androidx.navigation.NavDestination, androidx.navigation.NavGraph] */
    static NavDestination findStartDestination(NavDestination graph) {
        NavDestination startDestination = graph;
        while (startDestination instanceof NavGraph) {
            NavGraph parent = (NavGraph) startDestination;
            startDestination = parent.findNode(parent.getStartDestination());
        }
        return startDestination;
    }
}
