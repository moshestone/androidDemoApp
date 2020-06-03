package androidx.navigation.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import java.lang.ref.WeakReference;

class ToolbarOnDestinationChangedListener extends AbstractAppBarOnDestinationChangedListener {
    private final WeakReference<Toolbar> mToolbarWeakReference;

    ToolbarOnDestinationChangedListener(Toolbar toolbar, AppBarConfiguration configuration) {
        super(toolbar.getContext(), configuration);
        this.mToolbarWeakReference = new WeakReference<>(toolbar);
    }

    public void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
        if (((Toolbar) this.mToolbarWeakReference.get()) == null) {
            controller.removeOnDestinationChangedListener(this);
        } else {
            super.onDestinationChanged(controller, destination, arguments);
        }
    }

    /* access modifiers changed from: protected */
    public void setTitle(CharSequence title) {
        ((Toolbar) this.mToolbarWeakReference.get()).setTitle(title);
    }

    /* access modifiers changed from: protected */
    public void setNavigationIcon(Drawable icon, int contentDescription) {
        Toolbar toolbar = (Toolbar) this.mToolbarWeakReference.get();
        if (toolbar != null) {
            toolbar.setNavigationIcon(icon);
            toolbar.setNavigationContentDescription(contentDescription);
        }
    }
}
