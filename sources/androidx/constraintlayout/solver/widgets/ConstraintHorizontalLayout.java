package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor.Strength;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type;

public class ConstraintHorizontalLayout extends ConstraintWidgetContainer {
    private ContentAlignment mAlignment = ContentAlignment.MIDDLE;

    public enum ContentAlignment {
        BEGIN,
        MIDDLE,
        END,
        TOP,
        VERTICAL_MIDDLE,
        BOTTOM,
        LEFT,
        RIGHT
    }

    public ConstraintHorizontalLayout() {
    }

    public ConstraintHorizontalLayout(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public ConstraintHorizontalLayout(int width, int height) {
        super(width, height);
    }

    /* JADX WARNING: type inference failed for: r0v4 */
    /* JADX WARNING: Multi-variable type inference failed */
    public void addToSolver(LinearSystem system) {
        Strength strength;
        if (this.mChildren.size() != 0) {
            ConstraintHorizontalLayout constraintHorizontalLayout = this;
            int mChildrenSize = this.mChildren.size();
            for (int i = 0; i < mChildrenSize; i++) {
                ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
                if (constraintHorizontalLayout != this) {
                    widget.connect(Type.LEFT, (ConstraintWidget) constraintHorizontalLayout, Type.RIGHT);
                    constraintHorizontalLayout.connect(Type.RIGHT, widget, Type.LEFT);
                } else {
                    Strength strength2 = Strength.STRONG;
                    if (this.mAlignment == ContentAlignment.END) {
                        strength = Strength.WEAK;
                    } else {
                        strength = strength2;
                    }
                    widget.connect(Type.LEFT, (ConstraintWidget) constraintHorizontalLayout, Type.LEFT, 0, strength);
                }
                widget.connect(Type.TOP, (ConstraintWidget) this, Type.TOP);
                widget.connect(Type.BOTTOM, (ConstraintWidget) this, Type.BOTTOM);
                constraintHorizontalLayout = widget;
            }
            if (constraintHorizontalLayout != this) {
                Strength strength3 = Strength.STRONG;
                if (this.mAlignment == ContentAlignment.BEGIN) {
                    strength3 = Strength.WEAK;
                }
                constraintHorizontalLayout.connect(Type.RIGHT, (ConstraintWidget) this, Type.RIGHT, 0, strength3);
            }
        }
        super.addToSolver(system);
    }
}
