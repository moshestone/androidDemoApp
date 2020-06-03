package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.accessibility.AccessibilityEvent;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuBuilder.ItemInvoker;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.view.menu.MenuPresenter.Callback;
import androidx.appcompat.view.menu.MenuView;

public class ActionMenuView extends LinearLayoutCompat implements ItemInvoker, MenuView {
    static final int GENERATED_ITEM_PADDING = 4;
    static final int MIN_CELL_SIZE = 56;
    private static final String TAG = "ActionMenuView";
    private Callback mActionMenuPresenterCallback;
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    private MenuBuilder mMenu;
    MenuBuilder.Callback mMenuBuilderCallback;
    private int mMinCellSize;
    OnMenuItemClickListener mOnMenuItemClickListener;
    private Context mPopupContext;
    private int mPopupTheme;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;

    public interface ActionMenuChildView {
        boolean needsDividerAfter();

        boolean needsDividerBefore();
    }

    private static class ActionMenuPresenterCallback implements Callback {
        ActionMenuPresenterCallback() {
        }

        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        }

        public boolean onOpenSubMenu(MenuBuilder subMenu) {
            return false;
        }
    }

    public static class LayoutParams extends androidx.appcompat.widget.LinearLayoutCompat.LayoutParams {
        @ExportedProperty
        public int cellsUsed;
        @ExportedProperty
        public boolean expandable;
        boolean expanded;
        @ExportedProperty
        public int extraPixels;
        @ExportedProperty
        public boolean isOverflowButton;
        @ExportedProperty
        public boolean preventEdgeOffset;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams other) {
            super(other);
        }

        public LayoutParams(LayoutParams other) {
            super((android.view.ViewGroup.LayoutParams) other);
            this.isOverflowButton = other.isOverflowButton;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.isOverflowButton = false;
        }

        LayoutParams(int width, int height, boolean isOverflowButton2) {
            super(width, height);
            this.isOverflowButton = isOverflowButton2;
        }
    }

    private class MenuBuilderCallback implements MenuBuilder.Callback {
        MenuBuilderCallback() {
        }

        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            return ActionMenuView.this.mOnMenuItemClickListener != null && ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(item);
        }

        public void onMenuModeChange(MenuBuilder menu) {
            if (ActionMenuView.this.mMenuBuilderCallback != null) {
                ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(menu);
            }
        }
    }

    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBaselineAligned(false);
        float density = context.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int) (56.0f * density);
        this.mGeneratedItemPadding = (int) (4.0f * density);
        this.mPopupContext = context;
        this.mPopupTheme = 0;
    }

    public void setPopupTheme(int resId) {
        if (this.mPopupTheme != resId) {
            this.mPopupTheme = resId;
            if (resId == 0) {
                this.mPopupContext = getContext();
            } else {
                this.mPopupContext = new ContextThemeWrapper(getContext(), resId);
            }
        }
    }

    public int getPopupTheme() {
        return this.mPopupTheme;
    }

    public void setPresenter(ActionMenuPresenter presenter) {
        this.mPresenter = presenter;
        presenter.setMenuView(this);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.updateMenuView(false);
            if (this.mPresenter.isOverflowMenuShowing()) {
                this.mPresenter.hideOverflowMenu();
                this.mPresenter.showOverflowMenu();
            }
        }
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.mOnMenuItemClickListener = listener;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean wasFormatted = this.mFormatItems;
        boolean z = MeasureSpec.getMode(widthMeasureSpec) == 1073741824;
        this.mFormatItems = z;
        if (wasFormatted != z) {
            this.mFormatItemsWidth = 0;
        }
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (this.mFormatItems) {
            MenuBuilder menuBuilder = this.mMenu;
            if (!(menuBuilder == null || widthSize == this.mFormatItemsWidth)) {
                this.mFormatItemsWidth = widthSize;
                menuBuilder.onItemsChanged(true);
            }
        }
        int childCount = getChildCount();
        if (!this.mFormatItems || childCount <= 0) {
            for (int i = 0; i < childCount; i++) {
                LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
                lp.rightMargin = 0;
                lp.leftMargin = 0;
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        onMeasureExactFormat(widthMeasureSpec, heightMeasureSpec);
    }

    /* JADX WARNING: Removed duplicated region for block: B:131:0x0287  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02b5  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02bd  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x02bf  */
    private void onMeasureExactFormat(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize;
        int heightMode;
        boolean needsExpansion;
        boolean needsExpansion2;
        int heightSize;
        int visibleItemCount;
        int visibleItemCount2;
        boolean z;
        int heightMode2 = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize2 = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize2 = MeasureSpec.getSize(heightMeasureSpec);
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int heightPadding = getPaddingTop() + getPaddingBottom();
        int itemHeightSpec = getChildMeasureSpec(heightMeasureSpec, heightPadding, -2);
        int widthSize3 = widthSize2 - widthPadding;
        int i = this.mMinCellSize;
        int cellCount = widthSize3 / i;
        int cellSizeRemaining = widthSize3 % i;
        if (cellCount == 0) {
            setMeasuredDimension(widthSize3, 0);
            return;
        }
        int cellSize = i + (cellSizeRemaining / cellCount);
        int cellsRemaining = cellCount;
        int maxChildHeight = 0;
        int maxCellsUsed = 0;
        int expandableItemCount = 0;
        boolean hasOverflow = false;
        long smallestItemsAt = 0;
        int childCount = getChildCount();
        int heightSize3 = heightSize2;
        int maxChildHeight2 = 0;
        int i2 = widthPadding;
        int i3 = 0;
        while (i3 < childCount) {
            View child = getChildAt(i3);
            int cellCount2 = cellCount;
            int cellSizeRemaining2 = cellSizeRemaining;
            if (child.getVisibility() != 8) {
                boolean isGeneratedItem = child instanceof ActionMenuItemView;
                int visibleItemCount3 = maxChildHeight2 + 1;
                if (isGeneratedItem) {
                    int i4 = this.mGeneratedItemPadding;
                    visibleItemCount2 = visibleItemCount3;
                    z = false;
                    child.setPadding(i4, 0, i4, 0);
                } else {
                    visibleItemCount2 = visibleItemCount3;
                    z = false;
                }
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                lp.expanded = z;
                lp.extraPixels = z ? 1 : 0;
                lp.cellsUsed = z;
                lp.expandable = z;
                lp.leftMargin = z;
                lp.rightMargin = z;
                lp.preventEdgeOffset = isGeneratedItem && ((ActionMenuItemView) child).hasText();
                int cellsAvailable = lp.isOverflowButton ? 1 : cellsRemaining;
                boolean z2 = isGeneratedItem;
                int cellsUsed = measureChildForCells(child, cellSize, cellsAvailable, itemHeightSpec, heightPadding);
                maxCellsUsed = Math.max(maxCellsUsed, cellsUsed);
                int i5 = cellsAvailable;
                if (lp.expandable != 0) {
                    expandableItemCount++;
                }
                if (lp.isOverflowButton) {
                    hasOverflow = true;
                }
                cellsRemaining -= cellsUsed;
                int maxChildHeight3 = Math.max(maxChildHeight, child.getMeasuredHeight());
                if (cellsUsed == 1) {
                    int i6 = cellsUsed;
                    LayoutParams layoutParams = lp;
                    maxChildHeight = maxChildHeight3;
                    smallestItemsAt |= (long) (1 << i3);
                    maxChildHeight2 = visibleItemCount2;
                } else {
                    LayoutParams layoutParams2 = lp;
                    maxChildHeight = maxChildHeight3;
                    maxChildHeight2 = visibleItemCount2;
                }
            }
            i3++;
            int i7 = heightMeasureSpec;
            cellCount = cellCount2;
            cellSizeRemaining = cellSizeRemaining2;
        }
        int i8 = cellSizeRemaining;
        boolean centerSingleExpandedItem = hasOverflow && maxChildHeight2 == 2;
        boolean needsExpansion3 = false;
        while (true) {
            if (expandableItemCount <= 0 || cellsRemaining <= 0) {
                heightMode = heightMode2;
                widthSize = widthSize3;
                int i9 = heightPadding;
                needsExpansion = needsExpansion3;
                int i10 = expandableItemCount;
            } else {
                int minCells = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                long minCellsAt = 0;
                int minCellsItemCount = 0;
                int heightPadding2 = heightPadding;
                int i11 = 0;
                while (i11 < childCount) {
                    boolean needsExpansion4 = needsExpansion3;
                    LayoutParams lp2 = (LayoutParams) getChildAt(i11).getLayoutParams();
                    int expandableItemCount2 = expandableItemCount;
                    if (lp2.expandable != 0) {
                        if (lp2.cellsUsed < minCells) {
                            minCells = lp2.cellsUsed;
                            minCellsAt = 1 << i11;
                            minCellsItemCount = 1;
                        } else if (lp2.cellsUsed == minCells) {
                            minCellsAt |= 1 << i11;
                            minCellsItemCount++;
                        }
                    }
                    i11++;
                    expandableItemCount = expandableItemCount2;
                    needsExpansion3 = needsExpansion4;
                }
                needsExpansion = needsExpansion3;
                int expandableItemCount3 = expandableItemCount;
                smallestItemsAt |= minCellsAt;
                if (minCellsItemCount > cellsRemaining) {
                    heightMode = heightMode2;
                    widthSize = widthSize3;
                    break;
                }
                int minCells2 = minCells + 1;
                int i12 = 0;
                while (i12 < childCount) {
                    View child2 = getChildAt(i12);
                    LayoutParams lp3 = (LayoutParams) child2.getLayoutParams();
                    int minCellsItemCount2 = minCellsItemCount;
                    int heightMode3 = heightMode2;
                    int widthSize4 = widthSize3;
                    if ((minCellsAt & ((long) (1 << i12))) != 0) {
                        if (centerSingleExpandedItem && lp3.preventEdgeOffset && cellsRemaining == 1) {
                            int i13 = this.mGeneratedItemPadding;
                            child2.setPadding(i13 + cellSize, 0, i13, 0);
                        }
                        lp3.cellsUsed++;
                        lp3.expanded = true;
                        cellsRemaining--;
                    } else if (lp3.cellsUsed == minCells2) {
                        smallestItemsAt |= (long) (1 << i12);
                    }
                    i12++;
                    minCellsItemCount = minCellsItemCount2;
                    heightMode2 = heightMode3;
                    widthSize3 = widthSize4;
                }
                int i14 = widthSize3;
                int i15 = minCellsItemCount;
                needsExpansion3 = true;
                heightPadding = heightPadding2;
                expandableItemCount = expandableItemCount3;
            }
        }
        heightMode = heightMode2;
        widthSize = widthSize3;
        int i92 = heightPadding;
        needsExpansion = needsExpansion3;
        int i102 = expandableItemCount;
        boolean singleItem = !hasOverflow && maxChildHeight2 == 1;
        if (cellsRemaining <= 0 || smallestItemsAt == 0) {
        } else if (cellsRemaining < maxChildHeight2 - 1 || singleItem || maxCellsUsed > 1) {
            float expandCount = (float) Long.bitCount(smallestItemsAt);
            if (!singleItem) {
                if ((smallestItemsAt & 1) != 0) {
                    if (!((LayoutParams) getChildAt(0).getLayoutParams()).preventEdgeOffset) {
                        expandCount -= 0.5f;
                    }
                }
                if ((smallestItemsAt & ((long) (1 << (childCount - 1)))) != 0 && !((LayoutParams) getChildAt(childCount - 1).getLayoutParams()).preventEdgeOffset) {
                    expandCount -= 0.5f;
                }
            }
            int extraPixels = expandCount > 0.0f ? (int) (((float) (cellsRemaining * cellSize)) / expandCount) : 0;
            int i16 = 0;
            needsExpansion2 = needsExpansion;
            while (i16 < childCount) {
                boolean singleItem2 = singleItem;
                float expandCount2 = expandCount;
                if ((smallestItemsAt & ((long) (1 << i16))) != 0) {
                    View child3 = getChildAt(i16);
                    LayoutParams lp4 = (LayoutParams) child3.getLayoutParams();
                    if (child3 instanceof ActionMenuItemView) {
                        lp4.extraPixels = extraPixels;
                        lp4.expanded = true;
                        if (i16 == 0 && !lp4.preventEdgeOffset) {
                            lp4.leftMargin = (-extraPixels) / 2;
                        }
                        needsExpansion2 = true;
                    } else if (lp4.isOverflowButton) {
                        lp4.extraPixels = extraPixels;
                        lp4.expanded = true;
                        lp4.rightMargin = (-extraPixels) / 2;
                        needsExpansion2 = true;
                    } else {
                        if (i16 != 0) {
                            lp4.leftMargin = extraPixels / 2;
                        }
                        if (i16 != childCount - 1) {
                            lp4.rightMargin = extraPixels / 2;
                        }
                    }
                }
                i16++;
                singleItem = singleItem2;
                expandCount = expandCount2;
            }
            float f = expandCount;
            if (!needsExpansion2) {
                int i17 = 0;
                while (i17 < childCount) {
                    View child4 = getChildAt(i17);
                    LayoutParams lp5 = (LayoutParams) child4.getLayoutParams();
                    if (!lp5.expanded) {
                        visibleItemCount = maxChildHeight2;
                    } else {
                        visibleItemCount = maxChildHeight2;
                        child4.measure(MeasureSpec.makeMeasureSpec((lp5.cellsUsed * cellSize) + lp5.extraPixels, 1073741824), itemHeightSpec);
                    }
                    i17++;
                    maxChildHeight2 = visibleItemCount;
                }
                int visibleItemCount4 = maxChildHeight2;
            } else {
                int visibleItemCount5 = maxChildHeight2;
            }
            if (heightMode == 1073741824) {
                heightSize = maxChildHeight;
            } else {
                heightSize = heightSize3;
            }
            setMeasuredDimension(widthSize, heightSize);
        } else {
            boolean z3 = singleItem;
        }
        needsExpansion2 = needsExpansion;
        if (!needsExpansion2) {
        }
        if (heightMode == 1073741824) {
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    static int measureChildForCells(View child, int cellSize, int cellsRemaining, int parentHeightMeasureSpec, int parentHeightPadding) {
        View view = child;
        int i = cellsRemaining;
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int childHeightSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(parentHeightMeasureSpec) - parentHeightPadding, MeasureSpec.getMode(parentHeightMeasureSpec));
        ActionMenuItemView itemView = view instanceof ActionMenuItemView ? (ActionMenuItemView) view : null;
        boolean expandable = false;
        boolean hasText = itemView != null && itemView.hasText();
        int cellsUsed = 0;
        if (i > 0 && (!hasText || i >= 2)) {
            child.measure(MeasureSpec.makeMeasureSpec(cellSize * i, Integer.MIN_VALUE), childHeightSpec);
            int measuredWidth = child.getMeasuredWidth();
            cellsUsed = measuredWidth / cellSize;
            if (measuredWidth % cellSize != 0) {
                cellsUsed++;
            }
            if (hasText && cellsUsed < 2) {
                cellsUsed = 2;
            }
        }
        if (!lp.isOverflowButton && hasText) {
            expandable = true;
        }
        lp.expandable = expandable;
        lp.cellsUsed = cellsUsed;
        child.measure(MeasureSpec.makeMeasureSpec(cellsUsed * cellSize, 1073741824), childHeightSpec);
        return cellsUsed;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int i;
        int overflowWidth;
        int dividerWidth;
        boolean isLayoutRtl;
        int midVertical;
        int r;
        int l;
        ActionMenuView actionMenuView = this;
        if (!actionMenuView.mFormatItems) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }
        int childCount = getChildCount();
        int midVertical2 = (bottom - top) / 2;
        int dividerWidth2 = getDividerWidth();
        int overflowWidth2 = 0;
        int nonOverflowWidth = 0;
        int nonOverflowCount = 0;
        int widthRemaining = ((right - left) - getPaddingRight()) - getPaddingLeft();
        int i2 = 0;
        boolean isLayoutRtl2 = ViewUtils.isLayoutRtl(this);
        int i3 = 0;
        while (true) {
            i = 8;
            if (i3 >= childCount) {
                break;
            }
            View v = actionMenuView.getChildAt(i3);
            if (v.getVisibility() == 8) {
                midVertical = midVertical2;
                isLayoutRtl = isLayoutRtl2;
            } else {
                LayoutParams p = (LayoutParams) v.getLayoutParams();
                if (p.isOverflowButton) {
                    overflowWidth2 = v.getMeasuredWidth();
                    if (actionMenuView.hasSupportDividerBeforeChildAt(i3)) {
                        overflowWidth2 += dividerWidth2;
                    }
                    int height = v.getMeasuredHeight();
                    if (isLayoutRtl2) {
                        l = getPaddingLeft() + p.leftMargin;
                        r = l + overflowWidth2;
                    } else {
                        r = (getWidth() - getPaddingRight()) - p.rightMargin;
                        l = r - overflowWidth2;
                    }
                    isLayoutRtl = isLayoutRtl2;
                    int t = midVertical2 - (height / 2);
                    midVertical = midVertical2;
                    v.layout(l, t, r, t + height);
                    widthRemaining -= overflowWidth2;
                    i2 = 1;
                } else {
                    midVertical = midVertical2;
                    isLayoutRtl = isLayoutRtl2;
                    int size = v.getMeasuredWidth() + p.leftMargin + p.rightMargin;
                    nonOverflowWidth += size;
                    widthRemaining -= size;
                    if (actionMenuView.hasSupportDividerBeforeChildAt(i3)) {
                        nonOverflowWidth += dividerWidth2;
                    }
                    nonOverflowCount++;
                }
            }
            i3++;
            midVertical2 = midVertical;
            isLayoutRtl2 = isLayoutRtl;
        }
        int midVertical3 = midVertical2;
        boolean isLayoutRtl3 = isLayoutRtl2;
        if (childCount == 1 && i2 == 0) {
            View v2 = actionMenuView.getChildAt(0);
            int width = v2.getMeasuredWidth();
            int height2 = v2.getMeasuredHeight();
            int l2 = ((right - left) / 2) - (width / 2);
            int t2 = midVertical3 - (height2 / 2);
            v2.layout(l2, t2, l2 + width, t2 + height2);
            return;
        }
        int spacerCount = nonOverflowCount - (i2 ^ 1);
        int spacerSize = Math.max(0, spacerCount > 0 ? widthRemaining / spacerCount : 0);
        if (isLayoutRtl3) {
            int startRight = getWidth() - getPaddingRight();
            int i4 = 0;
            while (i4 < childCount) {
                View v3 = actionMenuView.getChildAt(i4);
                LayoutParams lp = (LayoutParams) v3.getLayoutParams();
                if (v3.getVisibility() == i) {
                    dividerWidth = dividerWidth2;
                    overflowWidth = overflowWidth2;
                } else if (lp.isOverflowButton) {
                    dividerWidth = dividerWidth2;
                    overflowWidth = overflowWidth2;
                } else {
                    int startRight2 = startRight - lp.rightMargin;
                    int width2 = v3.getMeasuredWidth();
                    int height3 = v3.getMeasuredHeight();
                    int t3 = midVertical3 - (height3 / 2);
                    dividerWidth = dividerWidth2;
                    overflowWidth = overflowWidth2;
                    v3.layout(startRight2 - width2, t3, startRight2, t3 + height3);
                    startRight = startRight2 - ((lp.leftMargin + width2) + spacerSize);
                }
                i4++;
                dividerWidth2 = dividerWidth;
                overflowWidth2 = overflowWidth;
                i = 8;
            }
            int i5 = overflowWidth2;
        } else {
            int i6 = overflowWidth2;
            int startLeft = getPaddingLeft();
            int i7 = 0;
            while (i7 < childCount) {
                View v4 = actionMenuView.getChildAt(i7);
                LayoutParams lp2 = (LayoutParams) v4.getLayoutParams();
                if (v4.getVisibility() != 8 && !lp2.isOverflowButton) {
                    int startLeft2 = startLeft + lp2.leftMargin;
                    int width3 = v4.getMeasuredWidth();
                    int height4 = v4.getMeasuredHeight();
                    int t4 = midVertical3 - (height4 / 2);
                    v4.layout(startLeft2, t4, startLeft2 + width3, t4 + height4);
                    startLeft = startLeft2 + lp2.rightMargin + width3 + spacerSize;
                }
                i7++;
                actionMenuView = this;
            }
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissPopupMenus();
    }

    public void setOverflowIcon(Drawable icon) {
        getMenu();
        this.mPresenter.setOverflowIcon(icon);
    }

    public Drawable getOverflowIcon() {
        getMenu();
        return this.mPresenter.getOverflowIcon();
    }

    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    public void setOverflowReserved(boolean reserveOverflow) {
        this.mReserveOverflow = reserveOverflow;
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        LayoutParams params = new LayoutParams(-2, -2);
        params.gravity = 16;
        return params;
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        if (p == null) {
            return generateDefaultLayoutParams();
        }
        LayoutParams result = p instanceof LayoutParams ? new LayoutParams((LayoutParams) p) : new LayoutParams(p);
        if (result.gravity <= 0) {
            result.gravity = 16;
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p != null && (p instanceof LayoutParams);
    }

    public LayoutParams generateOverflowButtonLayoutParams() {
        LayoutParams result = generateDefaultLayoutParams();
        result.isOverflowButton = true;
        return result;
    }

    public boolean invokeItem(MenuItemImpl item) {
        return this.mMenu.performItemAction(item, 0);
    }

    public int getWindowAnimations() {
        return 0;
    }

    public void initialize(MenuBuilder menu) {
        this.mMenu = menu;
    }

    public Menu getMenu() {
        if (this.mMenu == null) {
            Context context = getContext();
            MenuBuilder menuBuilder = new MenuBuilder(context);
            this.mMenu = menuBuilder;
            menuBuilder.setCallback(new MenuBuilderCallback());
            ActionMenuPresenter actionMenuPresenter = new ActionMenuPresenter(context);
            this.mPresenter = actionMenuPresenter;
            actionMenuPresenter.setReserveOverflow(true);
            ActionMenuPresenter actionMenuPresenter2 = this.mPresenter;
            Callback callback = this.mActionMenuPresenterCallback;
            if (callback == null) {
                callback = new ActionMenuPresenterCallback();
            }
            actionMenuPresenter2.setCallback(callback);
            this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
            this.mPresenter.setMenuView(this);
        }
        return this.mMenu;
    }

    public void setMenuCallbacks(Callback pcb, MenuBuilder.Callback mcb) {
        this.mActionMenuPresenterCallback = pcb;
        this.mMenuBuilderCallback = mcb;
    }

    public MenuBuilder peekMenu() {
        return this.mMenu;
    }

    public boolean showOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.showOverflowMenu();
    }

    public boolean hideOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.hideOverflowMenu();
    }

    public boolean isOverflowMenuShowing() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowMenuShowing();
    }

    public boolean isOverflowMenuShowPending() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowMenuShowPending();
    }

    public void dismissPopupMenus() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.dismissPopupMenus();
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasSupportDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0) {
            return false;
        }
        View childBefore = getChildAt(childIndex - 1);
        View child = getChildAt(childIndex);
        boolean result = false;
        if (childIndex < getChildCount() && (childBefore instanceof ActionMenuChildView)) {
            result = false | ((ActionMenuChildView) childBefore).needsDividerAfter();
        }
        if (childIndex > 0 && (child instanceof ActionMenuChildView)) {
            result |= ((ActionMenuChildView) child).needsDividerBefore();
        }
        return result;
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return false;
    }

    public void setExpandedActionViewsExclusive(boolean exclusive) {
        this.mPresenter.setExpandedActionViewsExclusive(exclusive);
    }
}
