package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.appcompat.R;
import androidx.core.view.GravityCompat;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.ViewCompat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class LinearLayoutCompat extends ViewGroup {
    public static final int HORIZONTAL = 0;
    private static final int INDEX_BOTTOM = 2;
    private static final int INDEX_CENTER_VERTICAL = 0;
    private static final int INDEX_FILL = 3;
    private static final int INDEX_TOP = 1;
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    public static final int SHOW_DIVIDER_END = 4;
    public static final int SHOW_DIVIDER_MIDDLE = 2;
    public static final int SHOW_DIVIDER_NONE = 0;
    public static final int VERTICAL = 1;
    private static final int VERTICAL_GRAVITY_COUNT = 4;
    private boolean mBaselineAligned;
    private int mBaselineAlignedChildIndex;
    private int mBaselineChildTop;
    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerPadding;
    private int mDividerWidth;
    private int mGravity;
    private int[] mMaxAscent;
    private int[] mMaxDescent;
    private int mOrientation;
    private int mShowDividers;
    private int mTotalLength;
    private boolean mUseLargestChild;
    private float mWeightSum;

    @Retention(RetentionPolicy.SOURCE)
    public @interface DividerMode {
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int gravity;
        public float weight;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.gravity = -1;
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.LinearLayoutCompat_Layout);
            this.weight = a.getFloat(R.styleable.LinearLayoutCompat_Layout_android_layout_weight, 0.0f);
            this.gravity = a.getInt(R.styleable.LinearLayoutCompat_Layout_android_layout_gravity, -1);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.gravity = -1;
            this.weight = 0.0f;
        }

        public LayoutParams(int width, int height, float weight2) {
            super(width, height);
            this.gravity = -1;
            this.weight = weight2;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams p) {
            super(p);
            this.gravity = -1;
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
            this.gravity = -1;
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.gravity = -1;
            this.weight = source.weight;
            this.gravity = source.gravity;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {
    }

    public LinearLayoutCompat(Context context) {
        this(context, null);
    }

    public LinearLayoutCompat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearLayoutCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mBaselineAligned = true;
        this.mBaselineAlignedChildIndex = -1;
        this.mBaselineChildTop = 0;
        this.mGravity = 8388659;
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.LinearLayoutCompat, defStyleAttr, 0);
        int index = a.getInt(R.styleable.LinearLayoutCompat_android_orientation, -1);
        if (index >= 0) {
            setOrientation(index);
        }
        int index2 = a.getInt(R.styleable.LinearLayoutCompat_android_gravity, -1);
        if (index2 >= 0) {
            setGravity(index2);
        }
        boolean baselineAligned = a.getBoolean(R.styleable.LinearLayoutCompat_android_baselineAligned, true);
        if (!baselineAligned) {
            setBaselineAligned(baselineAligned);
        }
        this.mWeightSum = a.getFloat(R.styleable.LinearLayoutCompat_android_weightSum, -1.0f);
        this.mBaselineAlignedChildIndex = a.getInt(R.styleable.LinearLayoutCompat_android_baselineAlignedChildIndex, -1);
        this.mUseLargestChild = a.getBoolean(R.styleable.LinearLayoutCompat_measureWithLargestChild, false);
        setDividerDrawable(a.getDrawable(R.styleable.LinearLayoutCompat_divider));
        this.mShowDividers = a.getInt(R.styleable.LinearLayoutCompat_showDividers, 0);
        this.mDividerPadding = a.getDimensionPixelSize(R.styleable.LinearLayoutCompat_dividerPadding, 0);
        a.recycle();
    }

    public void setShowDividers(int showDividers) {
        if (showDividers != this.mShowDividers) {
            requestLayout();
        }
        this.mShowDividers = showDividers;
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public int getShowDividers() {
        return this.mShowDividers;
    }

    public Drawable getDividerDrawable() {
        return this.mDivider;
    }

    public void setDividerDrawable(Drawable divider) {
        if (divider != this.mDivider) {
            this.mDivider = divider;
            boolean z = false;
            if (divider != null) {
                this.mDividerWidth = divider.getIntrinsicWidth();
                this.mDividerHeight = divider.getIntrinsicHeight();
            } else {
                this.mDividerWidth = 0;
                this.mDividerHeight = 0;
            }
            if (divider == null) {
                z = true;
            }
            setWillNotDraw(z);
            requestLayout();
        }
    }

    public void setDividerPadding(int padding) {
        this.mDividerPadding = padding;
    }

    public int getDividerPadding() {
        return this.mDividerPadding;
    }

    public int getDividerWidth() {
        return this.mDividerWidth;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mDivider != null) {
            if (this.mOrientation == 1) {
                drawDividersVertical(canvas);
            } else {
                drawDividersHorizontal(canvas);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void drawDividersVertical(Canvas canvas) {
        int bottom;
        int count = getVirtualChildCount();
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (!(child == null || child.getVisibility() == 8 || !hasDividerBeforeChildAt(i))) {
                drawHorizontalDivider(canvas, (child.getTop() - ((LayoutParams) child.getLayoutParams()).topMargin) - this.mDividerHeight);
            }
        }
        if (hasDividerBeforeChildAt(count) != 0) {
            View child2 = getVirtualChildAt(count - 1);
            if (child2 == null) {
                bottom = (getHeight() - getPaddingBottom()) - this.mDividerHeight;
            } else {
                bottom = child2.getBottom() + ((LayoutParams) child2.getLayoutParams()).bottomMargin;
            }
            drawHorizontalDivider(canvas, bottom);
        }
    }

    /* access modifiers changed from: 0000 */
    public void drawDividersHorizontal(Canvas canvas) {
        int position;
        int position2;
        int count = getVirtualChildCount();
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (!(child == null || child.getVisibility() == 8 || !hasDividerBeforeChildAt(i))) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (isLayoutRtl) {
                    position2 = child.getRight() + lp.rightMargin;
                } else {
                    position2 = (child.getLeft() - lp.leftMargin) - this.mDividerWidth;
                }
                drawVerticalDivider(canvas, position2);
            }
        }
        if (hasDividerBeforeChildAt(count) != 0) {
            View child2 = getVirtualChildAt(count - 1);
            if (child2 != null) {
                LayoutParams lp2 = (LayoutParams) child2.getLayoutParams();
                if (isLayoutRtl) {
                    position = (child2.getLeft() - lp2.leftMargin) - this.mDividerWidth;
                } else {
                    position = child2.getRight() + lp2.rightMargin;
                }
            } else if (isLayoutRtl) {
                position = getPaddingLeft();
            } else {
                position = (getWidth() - getPaddingRight()) - this.mDividerWidth;
            }
            drawVerticalDivider(canvas, position);
        }
    }

    /* access modifiers changed from: 0000 */
    public void drawHorizontalDivider(Canvas canvas, int top) {
        this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, top, (getWidth() - getPaddingRight()) - this.mDividerPadding, this.mDividerHeight + top);
        this.mDivider.draw(canvas);
    }

    /* access modifiers changed from: 0000 */
    public void drawVerticalDivider(Canvas canvas, int left) {
        this.mDivider.setBounds(left, getPaddingTop() + this.mDividerPadding, this.mDividerWidth + left, (getHeight() - getPaddingBottom()) - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }

    public boolean isBaselineAligned() {
        return this.mBaselineAligned;
    }

    public void setBaselineAligned(boolean baselineAligned) {
        this.mBaselineAligned = baselineAligned;
    }

    public boolean isMeasureWithLargestChildEnabled() {
        return this.mUseLargestChild;
    }

    public void setMeasureWithLargestChildEnabled(boolean enabled) {
        this.mUseLargestChild = enabled;
    }

    public int getBaseline() {
        if (this.mBaselineAlignedChildIndex < 0) {
            return super.getBaseline();
        }
        int childCount = getChildCount();
        int i = this.mBaselineAlignedChildIndex;
        if (childCount > i) {
            View child = getChildAt(i);
            int childBaseline = child.getBaseline();
            if (childBaseline != -1) {
                int childTop = this.mBaselineChildTop;
                if (this.mOrientation == 1) {
                    int majorGravity = this.mGravity & 112;
                    if (majorGravity != 48) {
                        if (majorGravity == 16) {
                            childTop += ((((getBottom() - getTop()) - getPaddingTop()) - getPaddingBottom()) - this.mTotalLength) / 2;
                        } else if (majorGravity == 80) {
                            childTop = ((getBottom() - getTop()) - getPaddingBottom()) - this.mTotalLength;
                        }
                    }
                }
                return ((LayoutParams) child.getLayoutParams()).topMargin + childTop + childBaseline;
            } else if (this.mBaselineAlignedChildIndex == 0) {
                return -1;
            } else {
                throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
            }
        } else {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
        }
    }

    public int getBaselineAlignedChildIndex() {
        return this.mBaselineAlignedChildIndex;
    }

    public void setBaselineAlignedChildIndex(int i) {
        if (i < 0 || i >= getChildCount()) {
            StringBuilder sb = new StringBuilder();
            sb.append("base aligned child index out of range (0, ");
            sb.append(getChildCount());
            sb.append(")");
            throw new IllegalArgumentException(sb.toString());
        }
        this.mBaselineAlignedChildIndex = i;
    }

    /* access modifiers changed from: 0000 */
    public View getVirtualChildAt(int index) {
        return getChildAt(index);
    }

    /* access modifiers changed from: 0000 */
    public int getVirtualChildCount() {
        return getChildCount();
    }

    public float getWeightSum() {
        return this.mWeightSum;
    }

    public void setWeightSum(float weightSum) {
        this.mWeightSum = Math.max(0.0f, weightSum);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mOrientation == 1) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasDividerBeforeChildAt(int childIndex) {
        boolean z = false;
        if (childIndex == 0) {
            if ((this.mShowDividers & 1) != 0) {
                z = true;
            }
            return z;
        } else if (childIndex == getChildCount()) {
            if ((this.mShowDividers & 4) != 0) {
                z = true;
            }
            return z;
        } else if ((this.mShowDividers & 2) == 0) {
            return false;
        } else {
            boolean hasVisibleViewBefore = false;
            int i = childIndex - 1;
            while (true) {
                if (i < 0) {
                    break;
                } else if (getChildAt(i).getVisibility() != 8) {
                    hasVisibleViewBefore = true;
                    break;
                } else {
                    i--;
                }
            }
            return hasVisibleViewBefore;
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x03da  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x03dc  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x03e3  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x03ec  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x045f  */
    /* JADX WARNING: Removed duplicated region for block: B:191:? A[RETURN, SYNTHETIC] */
    public void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        int count;
        int childState;
        int heightMode;
        int delta;
        int delta2;
        float totalWeight;
        int heightMode2;
        int delta3;
        int baselineChildIndex;
        float weightSum;
        boolean matchWidthLocally;
        boolean allFillParent;
        int alternativeMaxWidth;
        int weightedMaxWidth;
        int heightSize;
        int alternativeMaxWidth2;
        int childState2;
        int count2;
        int heightMode3;
        int childState3;
        int alternativeMaxWidth3;
        int i;
        int weightedMaxWidth2;
        LayoutParams lp;
        int count3;
        View child;
        int largestChildHeight;
        int i2;
        int weightedMaxWidth3;
        int oldHeight;
        int i3 = widthMeasureSpec;
        int i4 = heightMeasureSpec;
        this.mTotalLength = 0;
        int count4 = getVirtualChildCount();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode4 = MeasureSpec.getMode(heightMeasureSpec);
        int baselineChildIndex2 = this.mBaselineAlignedChildIndex;
        boolean useLargestChild = this.mUseLargestChild;
        boolean skippedMeasure = false;
        int weightedMaxWidth4 = 0;
        float totalWeight2 = 0.0f;
        int measuredWidth = 0;
        int i5 = 0;
        boolean matchWidth = false;
        int maxWidth = 0;
        int i6 = 0;
        int weightedMaxWidth5 = 0;
        boolean allFillParent2 = true;
        while (true) {
            int weightedMaxWidth6 = i6;
            if (i5 < count4) {
                View child2 = getVirtualChildAt(i5);
                if (child2 == null) {
                    this.mTotalLength += measureNullChild(i5);
                    count2 = count4;
                    heightMode3 = heightMode4;
                    i6 = weightedMaxWidth6;
                } else {
                    int largestChildHeight2 = weightedMaxWidth5;
                    if (child2.getVisibility() == 8) {
                        i5 += getChildrenSkipCount(child2, i5);
                        count2 = count4;
                        i6 = weightedMaxWidth6;
                        weightedMaxWidth5 = largestChildHeight2;
                        heightMode3 = heightMode4;
                    } else {
                        if (hasDividerBeforeChildAt(i5)) {
                            this.mTotalLength += this.mDividerHeight;
                        }
                        LayoutParams lp2 = (LayoutParams) child2.getLayoutParams();
                        float totalWeight3 = totalWeight2 + lp2.weight;
                        if (heightMode4 == 1073741824 && lp2.height == 0 && lp2.weight > 0.0f) {
                            int totalLength = this.mTotalLength;
                            int i7 = i5;
                            this.mTotalLength = Math.max(totalLength, lp2.topMargin + totalLength + lp2.bottomMargin);
                            skippedMeasure = true;
                            alternativeMaxWidth3 = measuredWidth;
                            childState3 = maxWidth;
                            count2 = count4;
                            weightedMaxWidth2 = weightedMaxWidth6;
                            largestChildHeight = largestChildHeight2;
                            i = i7;
                            count3 = 1073741824;
                            heightMode3 = heightMode4;
                            lp = lp2;
                            child = child2;
                        } else {
                            int i8 = i5;
                            if (lp2.height != 0 || lp2.weight <= 0.0f) {
                                oldHeight = Integer.MIN_VALUE;
                            } else {
                                lp2.height = -2;
                                oldHeight = 0;
                            }
                            int oldHeight2 = oldHeight;
                            i = i8;
                            int largestChildHeight3 = largestChildHeight2;
                            heightMode3 = heightMode4;
                            lp = lp2;
                            count2 = count4;
                            weightedMaxWidth2 = weightedMaxWidth6;
                            count3 = 1073741824;
                            View child3 = child2;
                            alternativeMaxWidth3 = measuredWidth;
                            childState3 = maxWidth;
                            measureChildBeforeLayout(child2, i, widthMeasureSpec, 0, heightMeasureSpec, totalWeight3 == 0.0f ? this.mTotalLength : 0);
                            if (oldHeight2 != Integer.MIN_VALUE) {
                                lp.height = oldHeight2;
                            }
                            int childHeight = child3.getMeasuredHeight();
                            int totalLength2 = this.mTotalLength;
                            child = child3;
                            this.mTotalLength = Math.max(totalLength2, totalLength2 + childHeight + lp.topMargin + lp.bottomMargin + getNextLocationOffset(child));
                            if (useLargestChild) {
                                largestChildHeight = Math.max(childHeight, largestChildHeight3);
                            } else {
                                largestChildHeight = largestChildHeight3;
                            }
                        }
                        if (baselineChildIndex2 >= 0) {
                            i2 = i;
                            if (baselineChildIndex2 == i2 + 1) {
                                this.mBaselineChildTop = this.mTotalLength;
                            }
                        } else {
                            i2 = i;
                        }
                        if (i2 >= baselineChildIndex2 || lp.weight <= 0.0f) {
                            boolean matchWidthLocally2 = false;
                            if (widthMode != count3 && lp.width == -1) {
                                matchWidth = true;
                                matchWidthLocally2 = true;
                            }
                            int margin = lp.leftMargin + lp.rightMargin;
                            int measuredWidth2 = child.getMeasuredWidth() + margin;
                            int maxWidth2 = Math.max(weightedMaxWidth4, measuredWidth2);
                            int childState4 = View.combineMeasuredStates(childState3, child.getMeasuredState());
                            boolean allFillParent3 = allFillParent2 && lp.width == -1;
                            if (lp.weight > 0.0f) {
                                int i9 = margin;
                                weightedMaxWidth3 = Math.max(weightedMaxWidth2, matchWidthLocally2 ? margin : measuredWidth2);
                            } else {
                                weightedMaxWidth3 = weightedMaxWidth2;
                                int i10 = margin;
                                alternativeMaxWidth3 = Math.max(alternativeMaxWidth3, matchWidthLocally2 ? margin : measuredWidth2);
                            }
                            int childrenSkipCount = getChildrenSkipCount(child, i2) + i2;
                            weightedMaxWidth5 = largestChildHeight;
                            allFillParent2 = allFillParent3;
                            i6 = weightedMaxWidth3;
                            totalWeight2 = totalWeight3;
                            measuredWidth = alternativeMaxWidth3;
                            i5 = childrenSkipCount;
                            weightedMaxWidth4 = maxWidth2;
                            maxWidth = childState4;
                        } else {
                            throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
                        }
                    }
                }
                i5++;
                int i11 = widthMeasureSpec;
                int i12 = heightMeasureSpec;
                heightMode4 = heightMode3;
                count4 = count2;
            } else {
                int weightedMaxWidth7 = i5;
                int largestChildHeight4 = weightedMaxWidth5;
                int alternativeMaxWidth4 = measuredWidth;
                int count5 = count4;
                int heightMode5 = heightMode4;
                int weightedMaxWidth8 = weightedMaxWidth6;
                int i13 = 8;
                if (this.mTotalLength > 0) {
                    count = count5;
                    if (hasDividerBeforeChildAt(count)) {
                        this.mTotalLength += this.mDividerHeight;
                    }
                } else {
                    count = count5;
                }
                if (useLargestChild) {
                    heightMode = heightMode5;
                    if (heightMode == Integer.MIN_VALUE || heightMode == 0) {
                        this.mTotalLength = 0;
                        int i14 = 0;
                        while (i14 < count) {
                            View child4 = getVirtualChildAt(i14);
                            if (child4 == null) {
                                this.mTotalLength += measureNullChild(i14);
                                childState2 = maxWidth;
                            } else if (child4.getVisibility() == i13) {
                                i14 += getChildrenSkipCount(child4, i14);
                                childState2 = maxWidth;
                            } else {
                                LayoutParams lp3 = (LayoutParams) child4.getLayoutParams();
                                int totalLength3 = this.mTotalLength;
                                childState2 = maxWidth;
                                this.mTotalLength = Math.max(totalLength3, totalLength3 + largestChildHeight4 + lp3.topMargin + lp3.bottomMargin + getNextLocationOffset(child4));
                            }
                            i14++;
                            maxWidth = childState2;
                            i13 = 8;
                        }
                        childState = maxWidth;
                    } else {
                        childState = maxWidth;
                    }
                } else {
                    childState = maxWidth;
                    heightMode = heightMode5;
                }
                this.mTotalLength += getPaddingTop() + getPaddingBottom();
                int i15 = heightMeasureSpec;
                int largestChildHeight5 = largestChildHeight4;
                int heightSizeAndState = View.resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumHeight()), i15, 0);
                int heightSize2 = heightSizeAndState & ViewCompat.MEASURED_SIZE_MASK;
                int delta4 = heightSize2 - this.mTotalLength;
                if (skippedMeasure) {
                    totalWeight = totalWeight2;
                    int i16 = heightSize2;
                    int i17 = weightedMaxWidth8;
                } else if (delta4 == 0 || totalWeight2 <= 0.0f) {
                    int alternativeMaxWidth5 = Math.max(alternativeMaxWidth4, weightedMaxWidth8);
                    if (!useLargestChild || heightMode == 1073741824) {
                        alternativeMaxWidth = alternativeMaxWidth5;
                        int i18 = heightSize2;
                        int i19 = weightedMaxWidth8;
                    } else {
                        int i20 = 0;
                        while (i20 < count) {
                            float totalWeight4 = totalWeight2;
                            View child5 = getVirtualChildAt(i20);
                            if (child5 != null) {
                                alternativeMaxWidth2 = alternativeMaxWidth5;
                                heightSize = heightSize2;
                                if (child5.getVisibility() == 8) {
                                    weightedMaxWidth = weightedMaxWidth8;
                                } else {
                                    LayoutParams lp4 = (LayoutParams) child5.getLayoutParams();
                                    float childExtra = lp4.weight;
                                    if (childExtra > 0.0f) {
                                        LayoutParams layoutParams = lp4;
                                        float f = childExtra;
                                        weightedMaxWidth = weightedMaxWidth8;
                                        child5.measure(MeasureSpec.makeMeasureSpec(child5.getMeasuredWidth(), 1073741824), MeasureSpec.makeMeasureSpec(largestChildHeight5, 1073741824));
                                    } else {
                                        float f2 = childExtra;
                                        weightedMaxWidth = weightedMaxWidth8;
                                    }
                                }
                            } else {
                                alternativeMaxWidth2 = alternativeMaxWidth5;
                                heightSize = heightSize2;
                                weightedMaxWidth = weightedMaxWidth8;
                            }
                            i20++;
                            alternativeMaxWidth5 = alternativeMaxWidth2;
                            totalWeight2 = totalWeight4;
                            heightSize2 = heightSize;
                            weightedMaxWidth8 = weightedMaxWidth;
                        }
                        alternativeMaxWidth = alternativeMaxWidth5;
                        int i21 = heightSize2;
                        int i22 = weightedMaxWidth8;
                    }
                    delta = widthMeasureSpec;
                    int i23 = heightMode;
                    int i24 = largestChildHeight5;
                    int heightMode6 = delta4;
                    boolean z = useLargestChild;
                    int i25 = baselineChildIndex2;
                    alternativeMaxWidth4 = alternativeMaxWidth;
                    delta2 = childState;
                    if (!allFillParent2 && widthMode != 1073741824) {
                        weightedMaxWidth4 = alternativeMaxWidth4;
                    }
                    setMeasuredDimension(View.resolveSizeAndState(Math.max(weightedMaxWidth4 + getPaddingLeft() + getPaddingRight(), getSuggestedMinimumWidth()), delta, delta2), heightSizeAndState);
                    if (!matchWidth) {
                        forceUniformWidth(count, i15);
                        return;
                    }
                    return;
                } else {
                    totalWeight = totalWeight2;
                    int i26 = heightSize2;
                    int i27 = weightedMaxWidth8;
                }
                float weightSum2 = this.mWeightSum;
                if (weightSum2 <= 0.0f) {
                    weightSum2 = totalWeight;
                }
                this.mTotalLength = 0;
                int i28 = 0;
                int delta5 = delta4;
                delta2 = childState;
                while (i28 < count) {
                    View child6 = getVirtualChildAt(i28);
                    int largestChildHeight6 = largestChildHeight5;
                    boolean useLargestChild2 = useLargestChild;
                    if (child6.getVisibility() == 8) {
                        heightMode2 = heightMode;
                        delta3 = delta5;
                        baselineChildIndex = baselineChildIndex2;
                        int delta6 = widthMeasureSpec;
                    } else {
                        LayoutParams lp5 = (LayoutParams) child6.getLayoutParams();
                        float childExtra2 = lp5.weight;
                        if (childExtra2 > 0.0f) {
                            baselineChildIndex = baselineChildIndex2;
                            int share = (int) ((((float) delta5) * childExtra2) / weightSum2);
                            float weightSum3 = weightSum2 - childExtra2;
                            float f3 = childExtra2;
                            delta3 = delta5 - share;
                            int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight() + lp5.leftMargin + lp5.rightMargin, lp5.width);
                            if (lp5.height != 0) {
                                heightMode2 = heightMode;
                            } else if (heightMode != 1073741824) {
                                heightMode2 = heightMode;
                            } else {
                                heightMode2 = heightMode;
                                child6.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(share > 0 ? share : 0, 1073741824));
                                int i29 = share;
                                delta2 = View.combineMeasuredStates(delta2, child6.getMeasuredState() & InputDeviceCompat.SOURCE_ANY);
                                weightSum2 = weightSum3;
                            }
                            int childHeight2 = child6.getMeasuredHeight() + share;
                            if (childHeight2 < 0) {
                                childHeight2 = 0;
                            }
                            int i30 = share;
                            child6.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(childHeight2, 1073741824));
                            delta2 = View.combineMeasuredStates(delta2, child6.getMeasuredState() & InputDeviceCompat.SOURCE_ANY);
                            weightSum2 = weightSum3;
                        } else {
                            heightMode2 = heightMode;
                            float f4 = childExtra2;
                            int delta7 = delta5;
                            baselineChildIndex = baselineChildIndex2;
                            int delta8 = widthMeasureSpec;
                            delta3 = delta7;
                        }
                        int margin2 = lp5.leftMargin + lp5.rightMargin;
                        int measuredWidth3 = child6.getMeasuredWidth() + margin2;
                        weightedMaxWidth4 = Math.max(weightedMaxWidth4, measuredWidth3);
                        if (widthMode != 1073741824) {
                            weightSum = weightSum2;
                            if (lp5.width == -1) {
                                matchWidthLocally = true;
                                int alternativeMaxWidth6 = Math.max(alternativeMaxWidth4, !matchWidthLocally ? margin2 : measuredWidth3);
                                if (!allFillParent2) {
                                    boolean z2 = matchWidthLocally;
                                    if (lp5.width == -1) {
                                        allFillParent = true;
                                        int totalLength4 = this.mTotalLength;
                                        int alternativeMaxWidth7 = alternativeMaxWidth6;
                                        this.mTotalLength = Math.max(totalLength4, totalLength4 + child6.getMeasuredHeight() + lp5.topMargin + lp5.bottomMargin + getNextLocationOffset(child6));
                                        allFillParent2 = allFillParent;
                                        weightSum2 = weightSum;
                                        alternativeMaxWidth4 = alternativeMaxWidth7;
                                    }
                                }
                                allFillParent = false;
                                int totalLength42 = this.mTotalLength;
                                int alternativeMaxWidth72 = alternativeMaxWidth6;
                                this.mTotalLength = Math.max(totalLength42, totalLength42 + child6.getMeasuredHeight() + lp5.topMargin + lp5.bottomMargin + getNextLocationOffset(child6));
                                allFillParent2 = allFillParent;
                                weightSum2 = weightSum;
                                alternativeMaxWidth4 = alternativeMaxWidth72;
                            }
                        } else {
                            weightSum = weightSum2;
                        }
                        matchWidthLocally = false;
                        int alternativeMaxWidth62 = Math.max(alternativeMaxWidth4, !matchWidthLocally ? margin2 : measuredWidth3);
                        if (!allFillParent2) {
                        }
                        allFillParent = false;
                        int totalLength422 = this.mTotalLength;
                        int alternativeMaxWidth722 = alternativeMaxWidth62;
                        this.mTotalLength = Math.max(totalLength422, totalLength422 + child6.getMeasuredHeight() + lp5.topMargin + lp5.bottomMargin + getNextLocationOffset(child6));
                        allFillParent2 = allFillParent;
                        weightSum2 = weightSum;
                        alternativeMaxWidth4 = alternativeMaxWidth722;
                    }
                    i28++;
                    largestChildHeight5 = largestChildHeight6;
                    useLargestChild = useLargestChild2;
                    baselineChildIndex2 = baselineChildIndex;
                    delta5 = delta3;
                    heightMode = heightMode2;
                }
                int i31 = largestChildHeight5;
                boolean z3 = useLargestChild;
                int heightMode7 = delta5;
                int i32 = baselineChildIndex2;
                delta = widthMeasureSpec;
                this.mTotalLength += getPaddingTop() + getPaddingBottom();
                weightedMaxWidth4 = alternativeMaxWidth4;
                setMeasuredDimension(View.resolveSizeAndState(Math.max(weightedMaxWidth4 + getPaddingLeft() + getPaddingRight(), getSuggestedMinimumWidth()), delta, delta2), heightSizeAndState);
                if (!matchWidth) {
                }
            }
        }
    }

    private void forceUniformWidth(int count, int heightMeasureSpec) {
        int uniformMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.width == -1) {
                    int oldHeight = lp.height;
                    lp.height = child.getMeasuredHeight();
                    measureChildWithMargins(child, uniformMeasureSpec, 0, heightMeasureSpec, 0);
                    lp.height = oldHeight;
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0547  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x057f  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0636  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x063e  */
    public void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        int count;
        int childState;
        int descent;
        int maxHeight;
        int widthMode;
        int count2;
        int widthSizeAndState;
        int delta;
        int childState2;
        int widthMode2;
        int alternativeMaxHeight;
        float totalWeight;
        int largestChildWidth;
        int maxHeight2;
        int widthMode3;
        int count3;
        boolean useLargestChild;
        int widthSizeAndState2;
        int count4;
        int delta2;
        int alternativeMaxHeight2;
        boolean allFillParent;
        int delta3;
        int alternativeMaxHeight3;
        int largestChildWidth2;
        int widthSize;
        int alternativeMaxHeight4;
        int maxHeight3;
        int i;
        int i2;
        int count5;
        int largestChildWidth3;
        boolean baselineAligned;
        int childState3;
        int alternativeMaxHeight5;
        int weightedMaxHeight;
        int widthMode4;
        int count6;
        int largestChildWidth4;
        LayoutParams lp;
        int largestChildWidth5;
        int margin;
        int weightedMaxHeight2;
        int oldWidth;
        int alternativeMaxHeight6;
        int i3 = widthMeasureSpec;
        int i4 = heightMeasureSpec;
        this.mTotalLength = 0;
        int count7 = getVirtualChildCount();
        int widthMode5 = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (this.mMaxAscent == null || this.mMaxDescent == null) {
            this.mMaxAscent = new int[4];
            this.mMaxDescent = new int[4];
        }
        int[] maxAscent = this.mMaxAscent;
        int[] maxDescent = this.mMaxDescent;
        maxAscent[3] = -1;
        maxAscent[2] = -1;
        maxAscent[1] = -1;
        maxAscent[0] = -1;
        maxDescent[3] = -1;
        maxDescent[2] = -1;
        maxDescent[1] = -1;
        maxDescent[0] = -1;
        boolean baselineAligned2 = this.mBaselineAligned;
        int useLargestChild2 = this.mUseLargestChild;
        boolean isExactly = widthMode5 == 1073741824;
        int i5 = 0;
        int childState4 = 0;
        float totalWeight2 = 0.0f;
        int childHeight = 0;
        int childState5 = 0;
        boolean skippedMeasure = false;
        boolean matchHeight = false;
        boolean allFillParent2 = true;
        int weightedMaxHeight3 = 0;
        int maxHeight4 = 0;
        while (i5 < count7) {
            View child = getVirtualChildAt(i5);
            if (child == null) {
                int largestChildWidth6 = childState5;
                this.mTotalLength += measureNullChild(i5);
                baselineAligned = baselineAligned2;
                count5 = count7;
                childState5 = largestChildWidth6;
                largestChildWidth3 = widthMode5;
            } else {
                int largestChildWidth7 = childState5;
                int weightedMaxHeight4 = maxHeight4;
                if (child.getVisibility() == 8) {
                    i5 += getChildrenSkipCount(child, i5);
                    baselineAligned = baselineAligned2;
                    childState5 = largestChildWidth7;
                    maxHeight4 = weightedMaxHeight4;
                    count5 = count7;
                    largestChildWidth3 = widthMode5;
                } else {
                    if (hasDividerBeforeChildAt(i5)) {
                        this.mTotalLength += this.mDividerWidth;
                    }
                    LayoutParams lp2 = (LayoutParams) child.getLayoutParams();
                    float totalWeight3 = totalWeight2 + lp2.weight;
                    if (widthMode5 == 1073741824 && lp2.width == 0 && lp2.weight > 0.0f) {
                        if (isExactly) {
                            alternativeMaxHeight6 = weightedMaxHeight3;
                            this.mTotalLength += lp2.leftMargin + lp2.rightMargin;
                        } else {
                            alternativeMaxHeight6 = weightedMaxHeight3;
                            int totalLength = this.mTotalLength;
                            this.mTotalLength = Math.max(totalLength, lp2.leftMargin + totalLength + lp2.rightMargin);
                        }
                        if (baselineAligned2) {
                            int freeSpec = MeasureSpec.makeMeasureSpec(0, 0);
                            child.measure(freeSpec, freeSpec);
                            lp = lp2;
                            childState3 = childHeight;
                            baselineAligned = baselineAligned2;
                            largestChildWidth4 = largestChildWidth7;
                            weightedMaxHeight = weightedMaxHeight4;
                            alternativeMaxHeight5 = alternativeMaxHeight6;
                            count5 = count7;
                            largestChildWidth3 = widthMode5;
                            count6 = -1;
                            widthMode4 = childState4;
                        } else {
                            skippedMeasure = true;
                            lp = lp2;
                            childState3 = childHeight;
                            baselineAligned = baselineAligned2;
                            largestChildWidth4 = largestChildWidth7;
                            weightedMaxHeight = weightedMaxHeight4;
                            alternativeMaxHeight5 = alternativeMaxHeight6;
                            count5 = count7;
                            largestChildWidth3 = widthMode5;
                            count6 = -1;
                            widthMode4 = childState4;
                        }
                    } else {
                        int alternativeMaxHeight7 = weightedMaxHeight3;
                        if (lp2.width != 0 || lp2.weight <= 0.0f) {
                            oldWidth = Integer.MIN_VALUE;
                        } else {
                            lp2.width = -2;
                            oldWidth = 0;
                        }
                        int largestChildWidth8 = largestChildWidth7;
                        LayoutParams lp3 = lp2;
                        weightedMaxHeight = weightedMaxHeight4;
                        int oldWidth2 = oldWidth;
                        alternativeMaxHeight5 = alternativeMaxHeight7;
                        childState3 = childHeight;
                        largestChildWidth3 = widthMode5;
                        widthMode4 = childState4;
                        baselineAligned = baselineAligned2;
                        count5 = count7;
                        count6 = -1;
                        measureChildBeforeLayout(child, i5, widthMeasureSpec, totalWeight3 == 0.0f ? this.mTotalLength : 0, heightMeasureSpec, 0);
                        int oldWidth3 = oldWidth2;
                        if (oldWidth3 != Integer.MIN_VALUE) {
                            lp = lp3;
                            lp.width = oldWidth3;
                        } else {
                            lp = lp3;
                        }
                        int childWidth = child.getMeasuredWidth();
                        if (isExactly) {
                            this.mTotalLength += lp.leftMargin + childWidth + lp.rightMargin + getNextLocationOffset(child);
                        } else {
                            int totalLength2 = this.mTotalLength;
                            this.mTotalLength = Math.max(totalLength2, totalLength2 + childWidth + lp.leftMargin + lp.rightMargin + getNextLocationOffset(child));
                        }
                        if (useLargestChild2 == true) {
                            largestChildWidth4 = Math.max(childWidth, largestChildWidth8);
                        } else {
                            largestChildWidth4 = largestChildWidth8;
                        }
                    }
                    boolean matchHeightLocally = false;
                    if (heightMode != 1073741824 && lp.height == count6) {
                        matchHeight = true;
                        matchHeightLocally = true;
                    }
                    int margin2 = lp.topMargin + lp.bottomMargin;
                    int childHeight2 = child.getMeasuredHeight() + margin2;
                    int childState6 = View.combineMeasuredStates(childState3, child.getMeasuredState());
                    if (baselineAligned) {
                        int childBaseline = child.getBaseline();
                        if (childBaseline != count6) {
                            int index = ((((lp.gravity < 0 ? this.mGravity : lp.gravity) & 112) >> 4) & -2) >> 1;
                            margin = margin2;
                            maxAscent[index] = Math.max(maxAscent[index], childBaseline);
                            largestChildWidth5 = largestChildWidth4;
                            maxDescent[index] = Math.max(maxDescent[index], childHeight2 - childBaseline);
                        } else {
                            margin = margin2;
                            largestChildWidth5 = largestChildWidth4;
                        }
                    } else {
                        margin = margin2;
                        largestChildWidth5 = largestChildWidth4;
                    }
                    int margin3 = Math.max(widthMode4, childHeight2);
                    boolean allFillParent3 = allFillParent2 && lp.height == -1;
                    if (lp.weight > 0.0f) {
                        weightedMaxHeight2 = Math.max(weightedMaxHeight, matchHeightLocally ? margin : childHeight2);
                    } else {
                        int weightedMaxHeight5 = weightedMaxHeight;
                        alternativeMaxHeight5 = Math.max(alternativeMaxHeight5, matchHeightLocally ? margin : childHeight2);
                        weightedMaxHeight2 = weightedMaxHeight5;
                    }
                    i5 += getChildrenSkipCount(child, i5);
                    allFillParent2 = allFillParent3;
                    childHeight = childState6;
                    totalWeight2 = totalWeight3;
                    childState5 = largestChildWidth5;
                    weightedMaxHeight3 = alternativeMaxHeight5;
                    childState4 = margin3;
                    maxHeight4 = weightedMaxHeight2;
                }
            }
            i5++;
            int i6 = widthMeasureSpec;
            baselineAligned2 = baselineAligned;
            widthMode5 = largestChildWidth3;
            count7 = count5;
        }
        boolean baselineAligned3 = baselineAligned2;
        int count8 = count7;
        int widthMode6 = widthMode5;
        int weightedMaxHeight6 = maxHeight4;
        int weightedMaxHeight7 = weightedMaxHeight3;
        int childState7 = childHeight;
        int widthMode7 = childState4;
        int largestChildWidth9 = childState5;
        if (this.mTotalLength > 0) {
            count = count8;
            if (hasDividerBeforeChildAt(count)) {
                this.mTotalLength += this.mDividerWidth;
            }
        } else {
            count = count8;
        }
        if (maxAscent[1] == -1 && maxAscent[0] == -1 && maxAscent[2] == -1 && maxAscent[3] == -1) {
            childState = childState7;
            descent = widthMode7;
        } else {
            childState = childState7;
            descent = Math.max(widthMode7, Math.max(maxAscent[3], Math.max(maxAscent[0], Math.max(maxAscent[1], maxAscent[2]))) + Math.max(maxDescent[3], Math.max(maxDescent[0], Math.max(maxDescent[1], maxDescent[2]))));
        }
        if (useLargestChild2 != 0) {
            widthMode = widthMode6;
            if (widthMode == Integer.MIN_VALUE || widthMode == 0) {
                this.mTotalLength = 0;
                int i7 = 0;
                while (i7 < count) {
                    View child2 = getVirtualChildAt(i7);
                    if (child2 == null) {
                        this.mTotalLength += measureNullChild(i7);
                        maxHeight3 = descent;
                        i2 = i7;
                    } else if (child2.getVisibility() == 8) {
                        i = i7 + getChildrenSkipCount(child2, i7);
                        maxHeight3 = descent;
                        i7 = i + 1;
                        descent = maxHeight3;
                    } else {
                        LayoutParams lp4 = (LayoutParams) child2.getLayoutParams();
                        if (isExactly) {
                            maxHeight3 = descent;
                            i2 = i7;
                            this.mTotalLength += lp4.leftMargin + largestChildWidth9 + lp4.rightMargin + getNextLocationOffset(child2);
                        } else {
                            maxHeight3 = descent;
                            i2 = i7;
                            int totalLength3 = this.mTotalLength;
                            this.mTotalLength = Math.max(totalLength3, totalLength3 + largestChildWidth9 + lp4.leftMargin + lp4.rightMargin + getNextLocationOffset(child2));
                        }
                    }
                    i = i2;
                    i7 = i + 1;
                    descent = maxHeight3;
                }
                maxHeight = descent;
                int i8 = i7;
            } else {
                maxHeight = descent;
            }
        } else {
            maxHeight = descent;
            widthMode = widthMode6;
        }
        this.mTotalLength += getPaddingLeft() + getPaddingRight();
        int widthSizeAndState3 = View.resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumWidth()), widthMeasureSpec, 0);
        int widthSize2 = widthSizeAndState3 & ViewCompat.MEASURED_SIZE_MASK;
        int delta4 = widthSize2 - this.mTotalLength;
        if (skippedMeasure) {
            totalWeight = totalWeight2;
            int i9 = largestChildWidth9;
            int i10 = widthSize2;
            largestChildWidth = weightedMaxHeight7;
        } else if (delta4 == 0 || totalWeight2 <= 0.0f) {
            int alternativeMaxHeight8 = Math.max(weightedMaxHeight7, weightedMaxHeight6);
            if (useLargestChild2 == 0 || widthMode == 1073741824) {
                alternativeMaxHeight3 = alternativeMaxHeight8;
                int i11 = largestChildWidth9;
                int i12 = widthSize2;
            } else {
                int i13 = 0;
                while (i13 < count) {
                    float totalWeight4 = totalWeight2;
                    View child3 = getVirtualChildAt(i13);
                    if (child3 != null) {
                        alternativeMaxHeight4 = alternativeMaxHeight8;
                        widthSize = widthSize2;
                        if (child3.getVisibility() == 8) {
                            largestChildWidth2 = largestChildWidth9;
                        } else {
                            LayoutParams lp5 = (LayoutParams) child3.getLayoutParams();
                            float childExtra = lp5.weight;
                            if (childExtra > 0.0f) {
                                LayoutParams layoutParams = lp5;
                                float f = childExtra;
                                largestChildWidth2 = largestChildWidth9;
                                child3.measure(MeasureSpec.makeMeasureSpec(largestChildWidth9, 1073741824), MeasureSpec.makeMeasureSpec(child3.getMeasuredHeight(), 1073741824));
                            } else {
                                largestChildWidth2 = largestChildWidth9;
                                float f2 = childExtra;
                            }
                        }
                    } else {
                        alternativeMaxHeight4 = alternativeMaxHeight8;
                        largestChildWidth2 = largestChildWidth9;
                        widthSize = widthSize2;
                    }
                    i13++;
                    alternativeMaxHeight8 = alternativeMaxHeight4;
                    totalWeight2 = totalWeight4;
                    widthSize2 = widthSize;
                    largestChildWidth9 = largestChildWidth2;
                }
                alternativeMaxHeight3 = alternativeMaxHeight8;
                int i14 = largestChildWidth9;
                int i15 = widthSize2;
            }
            delta = heightMeasureSpec;
            count2 = count;
            widthSizeAndState = widthSizeAndState3;
            int i16 = weightedMaxHeight6;
            alternativeMaxHeight = alternativeMaxHeight3;
            childState2 = maxHeight;
            int maxHeight5 = useLargestChild2;
            int i17 = childState;
            int childState8 = widthMode;
            widthMode2 = i17;
            if (!allFillParent2 && heightMode != 1073741824) {
                childState2 = alternativeMaxHeight;
            }
            setMeasuredDimension(widthSizeAndState | (-16777216 & widthMode2), View.resolveSizeAndState(Math.max(childState2 + getPaddingTop() + getPaddingBottom(), getSuggestedMinimumHeight()), delta, widthMode2 << 16));
            if (!matchHeight) {
                forceUniformHeight(count2, widthMeasureSpec);
                return;
            }
            int i18 = widthMeasureSpec;
            int i19 = count2;
            return;
        } else {
            totalWeight = totalWeight2;
            int i20 = largestChildWidth9;
            int i21 = widthSize2;
            largestChildWidth = weightedMaxHeight7;
        }
        float weightSum = this.mWeightSum;
        if (weightSum <= 0.0f) {
            weightSum = totalWeight;
        }
        maxAscent[3] = -1;
        maxAscent[2] = -1;
        maxAscent[1] = -1;
        maxAscent[0] = -1;
        maxDescent[3] = -1;
        maxDescent[2] = -1;
        maxDescent[1] = -1;
        maxDescent[0] = -1;
        this.mTotalLength = 0;
        int i22 = 0;
        int delta5 = delta4;
        int maxHeight6 = -1;
        int childState9 = childState;
        while (i22 < count) {
            int weightedMaxHeight8 = weightedMaxHeight6;
            View child4 = getVirtualChildAt(i22);
            if (child4 != null) {
                useLargestChild = useLargestChild2;
                count3 = count;
                if (child4.getVisibility() == 8) {
                    widthMode3 = widthMode;
                    widthSizeAndState2 = widthSizeAndState3;
                    count4 = delta5;
                    int delta6 = heightMeasureSpec;
                } else {
                    LayoutParams lp6 = (LayoutParams) child4.getLayoutParams();
                    float childExtra2 = lp6.weight;
                    if (childExtra2 > 0.0f) {
                        int share = (int) ((((float) delta5) * childExtra2) / weightSum);
                        float weightSum2 = weightSum - childExtra2;
                        float f3 = childExtra2;
                        widthSizeAndState2 = widthSizeAndState3;
                        int delta7 = delta5 - share;
                        int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom() + lp6.topMargin + lp6.bottomMargin, lp6.height);
                        if (lp6.width == 0 && widthMode == 1073741824) {
                            child4.measure(MeasureSpec.makeMeasureSpec(share > 0 ? share : 0, 1073741824), childHeightMeasureSpec);
                            widthMode3 = widthMode;
                        } else {
                            int childWidth2 = child4.getMeasuredWidth() + share;
                            if (childWidth2 < 0) {
                                childWidth2 = 0;
                            }
                            widthMode3 = widthMode;
                            child4.measure(MeasureSpec.makeMeasureSpec(childWidth2, 1073741824), childHeightMeasureSpec);
                        }
                        childState9 = View.combineMeasuredStates(childState9, child4.getMeasuredState() & ViewCompat.MEASURED_STATE_MASK);
                        weightSum = weightSum2;
                        delta2 = delta7;
                    } else {
                        widthMode3 = widthMode;
                        widthSizeAndState2 = widthSizeAndState3;
                        delta2 = delta5;
                        int delta8 = heightMeasureSpec;
                    }
                    if (isExactly) {
                        this.mTotalLength += child4.getMeasuredWidth() + lp6.leftMargin + lp6.rightMargin + getNextLocationOffset(child4);
                    } else {
                        int totalLength4 = this.mTotalLength;
                        this.mTotalLength = Math.max(totalLength4, child4.getMeasuredWidth() + totalLength4 + lp6.leftMargin + lp6.rightMargin + getNextLocationOffset(child4));
                    }
                    boolean matchHeightLocally2 = heightMode != 1073741824 && lp6.height == -1;
                    int margin4 = lp6.topMargin + lp6.bottomMargin;
                    int childHeight3 = child4.getMeasuredHeight() + margin4;
                    maxHeight6 = Math.max(maxHeight6, childHeight3);
                    float weightSum3 = weightSum;
                    int alternativeMaxHeight9 = Math.max(largestChildWidth, matchHeightLocally2 ? margin4 : childHeight3);
                    if (allFillParent2) {
                        alternativeMaxHeight2 = alternativeMaxHeight9;
                        if (lp6.height == -1) {
                            allFillParent = true;
                            if (!baselineAligned3) {
                                int childBaseline2 = child4.getBaseline();
                                allFillParent2 = allFillParent;
                                if (childBaseline2 != -1) {
                                    int gravity = (lp6.gravity < 0 ? this.mGravity : lp6.gravity) & 112;
                                    int index2 = ((gravity >> 4) & -2) >> 1;
                                    int i23 = gravity;
                                    maxAscent[index2] = Math.max(maxAscent[index2], childBaseline2);
                                    delta3 = delta2;
                                    maxDescent[index2] = Math.max(maxDescent[index2], childHeight3 - childBaseline2);
                                } else {
                                    delta3 = delta2;
                                }
                            } else {
                                allFillParent2 = allFillParent;
                                delta3 = delta2;
                            }
                            weightSum = weightSum3;
                            largestChildWidth = alternativeMaxHeight2;
                            count4 = delta3;
                        }
                    } else {
                        alternativeMaxHeight2 = alternativeMaxHeight9;
                    }
                    allFillParent = false;
                    if (!baselineAligned3) {
                    }
                    weightSum = weightSum3;
                    largestChildWidth = alternativeMaxHeight2;
                    count4 = delta3;
                }
            } else {
                count3 = count;
                widthMode3 = widthMode;
                widthSizeAndState2 = widthSizeAndState3;
                count4 = delta5;
                useLargestChild = useLargestChild2;
                int delta9 = heightMeasureSpec;
            }
            i22++;
            int i24 = widthMeasureSpec;
            delta5 = count4;
            widthSizeAndState3 = widthSizeAndState2;
            useLargestChild2 = useLargestChild;
            count = count3;
            weightedMaxHeight6 = weightedMaxHeight8;
            widthMode = widthMode3;
        }
        count2 = count;
        int i25 = widthMode;
        widthSizeAndState = widthSizeAndState3;
        int i26 = weightedMaxHeight6;
        int delta10 = delta5;
        boolean z = useLargestChild2;
        delta = heightMeasureSpec;
        this.mTotalLength += getPaddingLeft() + getPaddingRight();
        if (maxAscent[1] == -1 && maxAscent[0] == -1 && maxAscent[2] == -1 && maxAscent[3] == -1) {
            maxHeight2 = maxHeight6;
        } else {
            maxHeight2 = Math.max(maxHeight6, Math.max(maxAscent[3], Math.max(maxAscent[0], Math.max(maxAscent[1], maxAscent[2]))) + Math.max(maxDescent[3], Math.max(maxDescent[0], Math.max(maxDescent[1], maxDescent[2]))));
        }
        int maxHeight7 = delta10;
        alternativeMaxHeight = largestChildWidth;
        widthMode2 = childState9;
        childState2 = maxHeight2;
        childState2 = alternativeMaxHeight;
        setMeasuredDimension(widthSizeAndState | (-16777216 & widthMode2), View.resolveSizeAndState(Math.max(childState2 + getPaddingTop() + getPaddingBottom(), getSuggestedMinimumHeight()), delta, widthMode2 << 16));
        if (!matchHeight) {
        }
    }

    private void forceUniformHeight(int count, int widthMeasureSpec) {
        int uniformMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
        for (int i = 0; i < count; i++) {
            View child = getVirtualChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.height == -1) {
                    int oldWidth = lp.width;
                    lp.width = child.getMeasuredWidth();
                    measureChildWithMargins(child, widthMeasureSpec, 0, uniformMeasureSpec, 0);
                    lp.width = oldWidth;
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public int getChildrenSkipCount(View child, int index) {
        return 0;
    }

    /* access modifiers changed from: 0000 */
    public int measureNullChild(int childIndex) {
        return 0;
    }

    /* access modifiers changed from: 0000 */
    public void measureChildBeforeLayout(View child, int childIndex, int widthMeasureSpec, int totalWidth, int heightMeasureSpec, int totalHeight) {
        measureChildWithMargins(child, widthMeasureSpec, totalWidth, heightMeasureSpec, totalHeight);
    }

    /* access modifiers changed from: 0000 */
    public int getLocationOffset(View child) {
        return 0;
    }

    /* access modifiers changed from: 0000 */
    public int getNextLocationOffset(View child) {
        return 0;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        if (this.mOrientation == 1) {
            layoutVertical(l, t, r, b);
        } else {
            layoutHorizontal(l, t, r, b);
        }
    }

    /* access modifiers changed from: 0000 */
    public void layoutVertical(int left, int top, int right, int bottom) {
        int childTop;
        int paddingLeft;
        int gravity;
        int childLeft;
        int paddingLeft2 = getPaddingLeft();
        int width = right - left;
        int childRight = width - getPaddingRight();
        int childSpace = (width - paddingLeft2) - getPaddingRight();
        int count = getVirtualChildCount();
        int i = this.mGravity;
        int majorGravity = i & 112;
        int minorGravity = i & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if (majorGravity == 16) {
            childTop = getPaddingTop() + (((bottom - top) - this.mTotalLength) / 2);
        } else if (majorGravity != 80) {
            childTop = getPaddingTop();
        } else {
            childTop = ((getPaddingTop() + bottom) - top) - this.mTotalLength;
        }
        int i2 = 0;
        while (i2 < count) {
            View child = getVirtualChildAt(i2);
            if (child == null) {
                childTop += measureNullChild(i2);
                paddingLeft = paddingLeft2;
            } else if (child.getVisibility() != 8) {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int gravity2 = lp.gravity;
                if (gravity2 < 0) {
                    gravity = minorGravity;
                } else {
                    gravity = gravity2;
                }
                int layoutDirection = ViewCompat.getLayoutDirection(this);
                int absoluteGravity = GravityCompat.getAbsoluteGravity(gravity, layoutDirection) & 7;
                if (absoluteGravity == 1) {
                    childLeft = ((((childSpace - childWidth) / 2) + paddingLeft2) + lp.leftMargin) - lp.rightMargin;
                } else if (absoluteGravity != 5) {
                    childLeft = lp.leftMargin + paddingLeft2;
                } else {
                    childLeft = (childRight - childWidth) - lp.rightMargin;
                }
                if (hasDividerBeforeChildAt(i2) != 0) {
                    childTop += this.mDividerHeight;
                }
                int childTop2 = childTop + lp.topMargin;
                int i3 = layoutDirection;
                int i4 = gravity;
                int gravity3 = childTop2 + getLocationOffset(child);
                paddingLeft = paddingLeft2;
                LayoutParams lp2 = lp;
                setChildFrame(child, childLeft, gravity3, childWidth, childHeight);
                i2 += getChildrenSkipCount(child, i2);
                childTop = childTop2 + childHeight + lp2.bottomMargin + getNextLocationOffset(child);
            } else {
                paddingLeft = paddingLeft2;
            }
            i2++;
            paddingLeft2 = paddingLeft;
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00c5  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00c9  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x011a  */
    public void layoutHorizontal(int left, int top, int right, int bottom) {
        int childLeft;
        int dir;
        int start;
        int[] maxAscent;
        int[] maxDescent;
        int paddingTop;
        int count;
        int height;
        int layoutDirection;
        int childBaseline;
        int gravity;
        int gravity2;
        int gravity3;
        int childTop;
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        int paddingTop2 = getPaddingTop();
        int height2 = bottom - top;
        int childBottom = height2 - getPaddingBottom();
        int childSpace = (height2 - paddingTop2) - getPaddingBottom();
        int count2 = getVirtualChildCount();
        int i = this.mGravity;
        int majorGravity = i & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        int minorGravity = i & 112;
        boolean baselineAligned = this.mBaselineAligned;
        int[] maxAscent2 = this.mMaxAscent;
        int[] maxDescent2 = this.mMaxDescent;
        int layoutDirection2 = ViewCompat.getLayoutDirection(this);
        int absoluteGravity = GravityCompat.getAbsoluteGravity(majorGravity, layoutDirection2);
        if (absoluteGravity == 1) {
            childLeft = getPaddingLeft() + (((right - left) - this.mTotalLength) / 2);
        } else if (absoluteGravity != 5) {
            childLeft = getPaddingLeft();
        } else {
            childLeft = ((getPaddingLeft() + right) - left) - this.mTotalLength;
        }
        if (isLayoutRtl) {
            start = count2 - 1;
            dir = -1;
        } else {
            start = 0;
            dir = 1;
        }
        int i2 = 0;
        while (i2 < count2) {
            int childIndex = start + (dir * i2);
            boolean isLayoutRtl2 = isLayoutRtl;
            View child = getVirtualChildAt(childIndex);
            if (child == null) {
                childLeft += measureNullChild(childIndex);
                layoutDirection = layoutDirection2;
                maxDescent = maxDescent2;
                maxAscent = maxAscent2;
                paddingTop = paddingTop2;
                height = height2;
                count = count2;
            } else {
                int i3 = i2;
                layoutDirection = layoutDirection2;
                if (child.getVisibility() != 8) {
                    int childWidth = child.getMeasuredWidth();
                    int childHeight = child.getMeasuredHeight();
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    if (baselineAligned) {
                        height = height2;
                        if (lp.height != -1) {
                            childBaseline = child.getBaseline();
                            gravity = lp.gravity;
                            if (gravity >= 0) {
                                gravity2 = minorGravity;
                            } else {
                                gravity2 = gravity;
                            }
                            gravity3 = gravity2 & 112;
                            count = count2;
                            if (gravity3 != 16) {
                                childTop = ((((childSpace - childHeight) / 2) + paddingTop2) + lp.topMargin) - lp.bottomMargin;
                            } else if (gravity3 == 48) {
                                int childTop2 = lp.topMargin + paddingTop2;
                                childTop = childBaseline != -1 ? childTop2 + (maxAscent2[1] - childBaseline) : childTop2;
                            } else if (gravity3 != 80) {
                                childTop = paddingTop2;
                            } else {
                                int childTop3 = (childBottom - childHeight) - lp.bottomMargin;
                                childTop = childBaseline != -1 ? childTop3 - (maxDescent2[2] - (child.getMeasuredHeight() - childBaseline)) : childTop3;
                            }
                            if (hasDividerBeforeChildAt(childIndex) != 0) {
                                childLeft += this.mDividerWidth;
                            }
                            int childLeft2 = childLeft + lp.leftMargin;
                            paddingTop = paddingTop2;
                            int childIndex2 = childIndex;
                            int i4 = i3;
                            int i5 = childBaseline;
                            LayoutParams lp2 = lp;
                            maxDescent = maxDescent2;
                            maxAscent = maxAscent2;
                            setChildFrame(child, childLeft2 + getLocationOffset(child), childTop, childWidth, childHeight);
                            i2 = i4 + getChildrenSkipCount(child, childIndex2);
                            childLeft = childLeft2 + childWidth + lp2.rightMargin + getNextLocationOffset(child);
                        }
                    } else {
                        height = height2;
                    }
                    childBaseline = -1;
                    gravity = lp.gravity;
                    if (gravity >= 0) {
                    }
                    gravity3 = gravity2 & 112;
                    count = count2;
                    if (gravity3 != 16) {
                    }
                    if (hasDividerBeforeChildAt(childIndex) != 0) {
                    }
                    int childLeft22 = childLeft + lp.leftMargin;
                    paddingTop = paddingTop2;
                    int childIndex22 = childIndex;
                    int i42 = i3;
                    int i52 = childBaseline;
                    LayoutParams lp22 = lp;
                    maxDescent = maxDescent2;
                    maxAscent = maxAscent2;
                    setChildFrame(child, childLeft22 + getLocationOffset(child), childTop, childWidth, childHeight);
                    i2 = i42 + getChildrenSkipCount(child, childIndex22);
                    childLeft = childLeft22 + childWidth + lp22.rightMargin + getNextLocationOffset(child);
                } else {
                    maxDescent = maxDescent2;
                    maxAscent = maxAscent2;
                    paddingTop = paddingTop2;
                    height = height2;
                    count = count2;
                    int paddingTop3 = childIndex;
                    i2 = i3;
                }
            }
            i2++;
            isLayoutRtl = isLayoutRtl2;
            layoutDirection2 = layoutDirection;
            height2 = height;
            count2 = count;
            paddingTop2 = paddingTop;
            maxDescent2 = maxDescent;
            maxAscent2 = maxAscent;
        }
    }

    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }

    public void setOrientation(int orientation) {
        if (this.mOrientation != orientation) {
            this.mOrientation = orientation;
            requestLayout();
        }
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setGravity(int gravity) {
        if (this.mGravity != gravity) {
            if ((8388615 & gravity) == 0) {
                gravity |= GravityCompat.START;
            }
            if ((gravity & 112) == 0) {
                gravity |= 48;
            }
            this.mGravity = gravity;
            requestLayout();
        }
    }

    public int getGravity() {
        return this.mGravity;
    }

    public void setHorizontalGravity(int horizontalGravity) {
        int gravity = horizontalGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        int i = this.mGravity;
        if ((8388615 & i) != gravity) {
            this.mGravity = (-8388616 & i) | gravity;
            requestLayout();
        }
    }

    public void setVerticalGravity(int verticalGravity) {
        int gravity = verticalGravity & 112;
        int i = this.mGravity;
        if ((i & 112) != gravity) {
            this.mGravity = (i & -113) | gravity;
            requestLayout();
        }
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        int i = this.mOrientation;
        if (i == 0) {
            return new LayoutParams(-2, -2);
        }
        if (i == 1) {
            return new LayoutParams(-1, -2);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(LinearLayoutCompat.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(LinearLayoutCompat.class.getName());
    }
}
