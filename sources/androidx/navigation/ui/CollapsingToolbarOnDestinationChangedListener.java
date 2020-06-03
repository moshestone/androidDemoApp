package androidx.navigation.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import java.lang.ref.WeakReference;

class CollapsingToolbarOnDestinationChangedListener extends AbstractAppBarOnDestinationChangedListener {
    private final WeakReference<CollapsingToolbarLayout> mCollapsingToolbarLayoutWeakReference;
    private final WeakReference<Toolbar> mToolbarWeakReference;

    CollapsingToolbarOnDestinationChangedListener(CollapsingToolbarLayout collapsingToolbarLayout, Toolbar toolbar, AppBarConfiguration configuration) {
        super(collapsingToolbarLayout.getContext(), configuration);
        this.mCollapsingToolbarLayoutWeakReference = new WeakReference<>(collapsingToolbarLayout);
        this.mToolbarWeakReference = new WeakReference<>(toolbar);
    }

    public void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
        Toolbar toolbar = (Toolbar) this.mToolbarWeakReference.get();
        if (((CollapsingToolbarLayout) this.mCollapsingToolbarLayoutWeakReference.get()) == null || toolbar == null) {
            controller.removeOnDestinationChangedListener(this);
        } else {
            super.onDestinationChanged(controller, destination, arguments);
        }
    }

    /* access modifiers changed from: protected */
    public void setTitle(CharSequence title) {
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) this.mCollapsingToolbarLayoutWeakReference.get();
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(title);
        }
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
