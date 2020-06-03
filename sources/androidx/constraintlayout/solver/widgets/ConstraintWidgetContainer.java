package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.Metrics;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor.Type;
import androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConstraintWidgetContainer extends WidgetContainer {
    private static final boolean DEBUG = false;
    static final boolean DEBUG_GRAPH = false;
    private static final boolean DEBUG_LAYOUT = false;
    private static final int MAX_ITERATIONS = 8;
    private static final boolean USE_SNAPSHOT = true;
    int mDebugSolverPassCount = 0;
    public boolean mGroupsWrapOptimized = false;
    private boolean mHeightMeasuredTooSmall = false;
    ChainHead[] mHorizontalChainsArray = new ChainHead[4];
    int mHorizontalChainsSize = 0;
    public boolean mHorizontalWrapOptimized = false;
    private boolean mIsRtl = false;
    private int mOptimizationLevel = 7;
    int mPaddingBottom;
    int mPaddingLeft;
    int mPaddingRight;
    int mPaddingTop;
    public boolean mSkipSolver = false;
    private Snapshot mSnapshot;
    protected LinearSystem mSystem = new LinearSystem();
    ChainHead[] mVerticalChainsArray = new ChainHead[4];
    int mVerticalChainsSize = 0;
    public boolean mVerticalWrapOptimized = false;
    public List<ConstraintWidgetGroup> mWidgetGroups = new ArrayList();
    private boolean mWidthMeasuredTooSmall = false;
    public int mWrapFixedHeight = 0;
    public int mWrapFixedWidth = 0;

    public void fillMetrics(Metrics metrics) {
        this.mSystem.fillMetrics(metrics);
    }

    public ConstraintWidgetContainer() {
    }

    public ConstraintWidgetContainer(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public ConstraintWidgetContainer(int width, int height) {
        super(width, height);
    }

    public void setOptimizationLevel(int value) {
        this.mOptimizationLevel = value;
    }

    public int getOptimizationLevel() {
        return this.mOptimizationLevel;
    }

    public boolean optimizeFor(int feature) {
        if ((this.mOptimizationLevel & feature) == feature) {
            return USE_SNAPSHOT;
        }
        return false;
    }

    public String getType() {
        return "ConstraintLayout";
    }

    public void reset() {
        this.mSystem.reset();
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mPaddingTop = 0;
        this.mPaddingBottom = 0;
        this.mWidgetGroups.clear();
        this.mSkipSolver = false;
        super.reset();
    }

    public boolean isWidthMeasuredTooSmall() {
        return this.mWidthMeasuredTooSmall;
    }

    public boolean isHeightMeasuredTooSmall() {
        return this.mHeightMeasuredTooSmall;
    }

    public boolean addChildrenToSolver(LinearSystem system) {
        addToSolver(system);
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            if (widget instanceof ConstraintWidgetContainer) {
                DimensionBehaviour horizontalBehaviour = widget.mListDimensionBehaviors[0];
                DimensionBehaviour verticalBehaviour = widget.mListDimensionBehaviors[1];
                if (horizontalBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    widget.setHorizontalDimensionBehaviour(DimensionBehaviour.FIXED);
                }
                if (verticalBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    widget.setVerticalDimensionBehaviour(DimensionBehaviour.FIXED);
                }
                widget.addToSolver(system);
                if (horizontalBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    widget.setHorizontalDimensionBehaviour(horizontalBehaviour);
                }
                if (verticalBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    widget.setVerticalDimensionBehaviour(verticalBehaviour);
                }
            } else {
                Optimizer.checkMatchParent(this, system, widget);
                widget.addToSolver(system);
            }
        }
        if (this.mHorizontalChainsSize > 0) {
            Chain.applyChainConstraints(this, system, 0);
        }
        if (this.mVerticalChainsSize > 0) {
            Chain.applyChainConstraints(this, system, 1);
        }
        return USE_SNAPSHOT;
    }

    public void updateChildrenFromSolver(LinearSystem system, boolean[] flags) {
        flags[2] = false;
        updateFromSolver(system);
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            widget.updateFromSolver(system);
            if (widget.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_CONSTRAINT && widget.getWidth() < widget.getWrapWidth()) {
                flags[2] = USE_SNAPSHOT;
            }
            if (widget.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_CONSTRAINT && widget.getHeight() < widget.getWrapHeight()) {
                flags[2] = USE_SNAPSHOT;
            }
        }
    }

    public void setPadding(int left, int top, int right, int bottom) {
        this.mPaddingLeft = left;
        this.mPaddingTop = top;
        this.mPaddingRight = right;
        this.mPaddingBottom = bottom;
    }

    public void setRtl(boolean isRtl) {
        this.mIsRtl = isRtl;
    }

    public boolean isRtl() {
        return this.mIsRtl;
    }

    public void analyze(int optimizationLevel) {
        super.analyze(optimizationLevel);
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ((ConstraintWidget) this.mChildren.get(i)).analyze(optimizationLevel);
        }
    }

    /* JADX WARNING: type inference failed for: r17v2, types: [boolean] */
    /* JADX WARNING: type inference failed for: r0v39, types: [boolean[]] */
    /* JADX WARNING: type inference failed for: r17v3 */
    /* JADX WARNING: type inference failed for: r17v5 */
    /* JADX WARNING: type inference failed for: r17v6 */
    /* JADX WARNING: type inference failed for: r17v7 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=boolean[], code=null, for r0v39, types: [boolean[]] */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r17v6
  assigns: []
  uses: [?[int, short, byte, char], boolean]
  mth insns count: 376
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0292  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x02af  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x02be  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0315  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x019a  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01a4  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01f9  */
    /* JADX WARNING: Unknown variable types count: 4 */
    public void layout() {
        int groupSize;
        int i;
        int groupSize2;
        boolean wrap_override;
        int count;
        int maxX;
        int width;
        int height;
        int i2;
        int i3;
        int i4;
        ? r17;
        int prex = this.mX;
        int prey = this.mY;
        int prew = Math.max(0, getWidth());
        int preh = Math.max(0, getHeight());
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        if (this.mParent != null) {
            if (this.mSnapshot == null) {
                this.mSnapshot = new Snapshot(this);
            }
            this.mSnapshot.updateFrom(this);
            setX(this.mPaddingLeft);
            setY(this.mPaddingTop);
            resetAnchors();
            resetSolverVariables(this.mSystem.getCache());
        } else {
            this.mX = 0;
            this.mY = 0;
        }
        int i5 = 32;
        if (this.mOptimizationLevel != 0) {
            if (!optimizeFor(8)) {
                optimizeReset();
            }
            if (!optimizeFor(32)) {
                optimize();
            }
            this.mSystem.graphOptimizer = USE_SNAPSHOT;
        } else {
            this.mSystem.graphOptimizer = false;
        }
        boolean wrap_override2 = false;
        DimensionBehaviour originalVerticalDimensionBehaviour = this.mListDimensionBehaviors[1];
        DimensionBehaviour originalHorizontalDimensionBehaviour = this.mListDimensionBehaviors[0];
        resetChains();
        if (this.mWidgetGroups.size() == 0) {
            this.mWidgetGroups.clear();
            this.mWidgetGroups.add(0, new ConstraintWidgetGroup(this.mChildren));
        }
        int groupSize3 = this.mWidgetGroups.size();
        ArrayList arrayList = this.mChildren;
        boolean hasWrapContent = (getHorizontalDimensionBehaviour() == DimensionBehaviour.WRAP_CONTENT || getVerticalDimensionBehaviour() == DimensionBehaviour.WRAP_CONTENT) ? USE_SNAPSHOT : false;
        int groupIndex = 0;
        while (groupIndex < groupSize3 && !this.mSkipSolver) {
            if (((ConstraintWidgetGroup) this.mWidgetGroups.get(groupIndex)).mSkipSolver) {
                groupSize = groupSize3;
            } else {
                if (optimizeFor(i5)) {
                    if (getHorizontalDimensionBehaviour() == DimensionBehaviour.FIXED && getVerticalDimensionBehaviour() == DimensionBehaviour.FIXED) {
                        this.mChildren = (ArrayList) ((ConstraintWidgetGroup) this.mWidgetGroups.get(groupIndex)).getWidgetsToSolve();
                    } else {
                        this.mChildren = (ArrayList) ((ConstraintWidgetGroup) this.mWidgetGroups.get(groupIndex)).mConstrainedGroup;
                    }
                }
                resetChains();
                int count2 = this.mChildren.size();
                int countSolve = 0;
                int i6 = 0;
                while (i6 < count2) {
                    ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i6);
                    int countSolve2 = countSolve;
                    if ((widget instanceof WidgetContainer) != 0) {
                        ((WidgetContainer) widget).layout();
                    }
                    i6++;
                    countSolve = countSolve2;
                }
                int countSolve3 = countSolve;
                int height2 = 1;
                boolean wrap_override3 = wrap_override2;
                while (height2 != 0) {
                    int countSolve4 = countSolve3 + 1;
                    try {
                        this.mSystem.reset();
                        resetChains();
                        createObjectVariables(this.mSystem);
                        int i7 = 0;
                        while (i7 < count2) {
                            wrap_override = wrap_override3;
                            try {
                                i = height2;
                            } catch (Exception e) {
                                e = e;
                                int i8 = height2;
                                e.printStackTrace();
                                PrintStream printStream = System.out;
                                int i9 = height2;
                                StringBuilder sb = new StringBuilder();
                                groupSize2 = groupSize3;
                                sb.append("EXCEPTION : ");
                                sb.append(e);
                                printStream.println(sb.toString());
                                height2 = i9;
                                if (height2 != 0) {
                                }
                                int i10 = 0;
                                if (hasWrapContent) {
                                }
                                i4 = 0;
                                count = count2;
                                maxX = i4;
                                wrap_override3 = wrap_override;
                                width = Math.max(this.mMinWidth, getWidth());
                                if (width > getWidth()) {
                                }
                                height = Math.max(this.mMinHeight, getHeight());
                                if (height > getHeight()) {
                                }
                                if (!wrap_override3) {
                                }
                                countSolve3 = countSolve4;
                                groupSize3 = groupSize2;
                                count2 = count;
                            }
                            try {
                                ((ConstraintWidget) this.mChildren.get(i7)).createObjectVariables(this.mSystem);
                                i7++;
                                wrap_override3 = wrap_override;
                                height2 = i;
                            } catch (Exception e2) {
                                e = e2;
                                height2 = i;
                                e.printStackTrace();
                                PrintStream printStream2 = System.out;
                                int i92 = height2;
                                StringBuilder sb2 = new StringBuilder();
                                groupSize2 = groupSize3;
                                sb2.append("EXCEPTION : ");
                                sb2.append(e);
                                printStream2.println(sb2.toString());
                                height2 = i92;
                                if (height2 != 0) {
                                }
                                int i102 = 0;
                                if (hasWrapContent) {
                                }
                                i4 = 0;
                                count = count2;
                                maxX = i4;
                                wrap_override3 = wrap_override;
                                width = Math.max(this.mMinWidth, getWidth());
                                if (width > getWidth()) {
                                }
                                height = Math.max(this.mMinHeight, getHeight());
                                if (height > getHeight()) {
                                }
                                if (!wrap_override3) {
                                }
                                countSolve3 = countSolve4;
                                groupSize3 = groupSize2;
                                count2 = count;
                            }
                        }
                        wrap_override = wrap_override3;
                        int i11 = height2;
                        height2 = addChildrenToSolver(this.mSystem);
                        if (height2 != 0) {
                            try {
                                this.mSystem.minimize();
                            } catch (Exception e3) {
                                e = e3;
                            }
                        }
                        groupSize2 = groupSize3;
                    } catch (Exception e4) {
                        e = e4;
                        wrap_override = wrap_override3;
                        int i12 = height2;
                        e.printStackTrace();
                        PrintStream printStream22 = System.out;
                        int i922 = height2;
                        StringBuilder sb22 = new StringBuilder();
                        groupSize2 = groupSize3;
                        sb22.append("EXCEPTION : ");
                        sb22.append(e);
                        printStream22.println(sb22.toString());
                        height2 = i922;
                        if (height2 != 0) {
                        }
                        int i1022 = 0;
                        if (hasWrapContent) {
                        }
                        i4 = 0;
                        count = count2;
                        maxX = i4;
                        wrap_override3 = wrap_override;
                        width = Math.max(this.mMinWidth, getWidth());
                        if (width > getWidth()) {
                        }
                        height = Math.max(this.mMinHeight, getHeight());
                        if (height > getHeight()) {
                        }
                        if (!wrap_override3) {
                        }
                        countSolve3 = countSolve4;
                        groupSize3 = groupSize2;
                        count2 = count;
                    }
                    if (height2 != 0) {
                        updateFromSolver(this.mSystem);
                        int i13 = 0;
                        while (true) {
                            if (i13 >= count2) {
                                i = height2;
                                break;
                            }
                            ConstraintWidget widget2 = (ConstraintWidget) this.mChildren.get(i13);
                            i = height2;
                            if (widget2.mListDimensionBehaviors[0] != DimensionBehaviour.MATCH_CONSTRAINT) {
                                r17 = 1;
                            } else if (widget2.getWidth() < widget2.getWrapWidth()) {
                                Optimizer.flags[2] = USE_SNAPSHOT;
                                break;
                            } else {
                                r17 = 1;
                            }
                            if (widget2.mListDimensionBehaviors[r17] == DimensionBehaviour.MATCH_CONSTRAINT && widget2.getHeight() < widget2.getWrapHeight()) {
                                Optimizer.flags[2] = r17;
                                break;
                            } else {
                                i13++;
                                height2 = i;
                            }
                        }
                    } else {
                        updateChildrenFromSolver(this.mSystem, Optimizer.flags);
                        i = height2;
                    }
                    int i10222 = 0;
                    if (hasWrapContent || countSolve4 >= 8 || !Optimizer.flags[2]) {
                        i4 = 0;
                        count = count2;
                    } else {
                        int maxX2 = 0;
                        int maxY = 0;
                        int i14 = 0;
                        while (i14 < count2) {
                            int i15 = i10222;
                            ConstraintWidget widget3 = (ConstraintWidget) this.mChildren.get(i14);
                            int count3 = count2;
                            maxX2 = Math.max(maxX2, widget3.mX + widget3.getWidth());
                            int i16 = widget3.mY;
                            i = widget3.getHeight();
                            maxY = Math.max(maxY, i16 + i);
                            i14++;
                            i10222 = i15;
                            count2 = count3;
                        }
                        i4 = i10222;
                        count = count2;
                        int maxX3 = Math.max(this.mMinWidth, maxX2);
                        int maxY2 = Math.max(this.mMinHeight, maxY);
                        if (originalHorizontalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT && getWidth() < maxX3) {
                            setWidth(maxX3);
                            this.mListDimensionBehaviors[0] = DimensionBehaviour.WRAP_CONTENT;
                            wrap_override = true;
                            i4 = 1;
                        }
                        if (originalVerticalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT && getHeight() < maxY2) {
                            setHeight(maxY2);
                            this.mListDimensionBehaviors[1] = DimensionBehaviour.WRAP_CONTENT;
                            wrap_override3 = true;
                            maxX = 1;
                            width = Math.max(this.mMinWidth, getWidth());
                            if (width > getWidth()) {
                                setWidth(width);
                                this.mListDimensionBehaviors[0] = DimensionBehaviour.FIXED;
                                wrap_override3 = USE_SNAPSHOT;
                                maxX = 1;
                            }
                            height = Math.max(this.mMinHeight, getHeight());
                            if (height > getHeight()) {
                                setHeight(height);
                                this.mListDimensionBehaviors[1] = DimensionBehaviour.FIXED;
                                wrap_override3 = USE_SNAPSHOT;
                                maxX = 1;
                            }
                            if (!wrap_override3) {
                                int i17 = maxX;
                                if (this.mListDimensionBehaviors[0] != DimensionBehaviour.WRAP_CONTENT || prew <= 0 || getWidth() <= prew) {
                                    i2 = i17;
                                } else {
                                    this.mWidthMeasuredTooSmall = USE_SNAPSHOT;
                                    wrap_override3 = USE_SNAPSHOT;
                                    this.mListDimensionBehaviors[0] = DimensionBehaviour.FIXED;
                                    setWidth(prew);
                                    i2 = 1;
                                }
                                boolean wrap_override4 = wrap_override3;
                                if (this.mListDimensionBehaviors[1] != DimensionBehaviour.WRAP_CONTENT || preh <= 0) {
                                    i3 = i2;
                                } else if (getHeight() > preh) {
                                    this.mHeightMeasuredTooSmall = USE_SNAPSHOT;
                                    int i18 = i2;
                                    this.mListDimensionBehaviors[1] = DimensionBehaviour.FIXED;
                                    setHeight(preh);
                                    height2 = 1;
                                    wrap_override3 = true;
                                } else {
                                    i3 = i2;
                                }
                                wrap_override3 = wrap_override4;
                                height2 = i3;
                            } else {
                                height2 = maxX;
                            }
                            countSolve3 = countSolve4;
                            groupSize3 = groupSize2;
                            count2 = count;
                        }
                    }
                    maxX = i4;
                    wrap_override3 = wrap_override;
                    width = Math.max(this.mMinWidth, getWidth());
                    if (width > getWidth()) {
                    }
                    height = Math.max(this.mMinHeight, getHeight());
                    if (height > getHeight()) {
                    }
                    if (!wrap_override3) {
                    }
                    countSolve3 = countSolve4;
                    groupSize3 = groupSize2;
                    count2 = count;
                }
                boolean wrap_override5 = wrap_override3;
                int i19 = count2;
                i = height2;
                groupSize = groupSize3;
                ((ConstraintWidgetGroup) this.mWidgetGroups.get(groupIndex)).updateUnresolvedWidgets();
                int i20 = countSolve3;
                wrap_override2 = wrap_override5;
            }
            groupIndex++;
            groupSize3 = groupSize;
            i5 = 32;
        }
        this.mChildren = arrayList;
        if (this.mParent != null) {
            int width2 = Math.max(this.mMinWidth, getWidth());
            int height3 = Math.max(this.mMinHeight, getHeight());
            this.mSnapshot.applyTo(this);
            setWidth(this.mPaddingLeft + width2 + this.mPaddingRight);
            setHeight(this.mPaddingTop + height3 + this.mPaddingBottom);
        } else {
            this.mX = prex;
            this.mY = prey;
        }
        if (wrap_override2) {
            this.mListDimensionBehaviors[0] = originalHorizontalDimensionBehaviour;
            this.mListDimensionBehaviors[1] = originalVerticalDimensionBehaviour;
        }
        resetSolverVariables(this.mSystem.getCache());
        if (this == getRootConstraintContainer()) {
            updateDrawPosition();
        }
    }

    public void preOptimize() {
        optimizeReset();
        analyze(this.mOptimizationLevel);
    }

    public void solveGraph() {
        ResolutionAnchor leftNode = getAnchor(Type.LEFT).getResolutionNode();
        ResolutionAnchor topNode = getAnchor(Type.TOP).getResolutionNode();
        leftNode.resolve(null, 0.0f);
        topNode.resolve(null, 0.0f);
    }

    public void resetGraph() {
        ResolutionAnchor leftNode = getAnchor(Type.LEFT).getResolutionNode();
        ResolutionAnchor topNode = getAnchor(Type.TOP).getResolutionNode();
        leftNode.invalidateAnchors();
        topNode.invalidateAnchors();
        leftNode.resolve(null, 0.0f);
        topNode.resolve(null, 0.0f);
    }

    public void optimizeForDimensions(int width, int height) {
        if (!(this.mListDimensionBehaviors[0] == DimensionBehaviour.WRAP_CONTENT || this.mResolutionWidth == null)) {
            this.mResolutionWidth.resolve(width);
        }
        if (this.mListDimensionBehaviors[1] != DimensionBehaviour.WRAP_CONTENT && this.mResolutionHeight != null) {
            this.mResolutionHeight.resolve(height);
        }
    }

    public void optimizeReset() {
        int count = this.mChildren.size();
        resetResolutionNodes();
        for (int i = 0; i < count; i++) {
            ((ConstraintWidget) this.mChildren.get(i)).resetResolutionNodes();
        }
    }

    public void optimize() {
        if (!optimizeFor(8)) {
            analyze(this.mOptimizationLevel);
        }
        solveGraph();
    }

    public boolean handlesInternalConstraints() {
        return false;
    }

    public ArrayList<Guideline> getVerticalGuidelines() {
        ArrayList<Guideline> guidelines = new ArrayList<>();
        int mChildrenSize = this.mChildren.size();
        for (int i = 0; i < mChildrenSize; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            if (widget instanceof Guideline) {
                Guideline guideline = (Guideline) widget;
                if (guideline.getOrientation() == 1) {
                    guidelines.add(guideline);
                }
            }
        }
        return guidelines;
    }

    public ArrayList<Guideline> getHorizontalGuidelines() {
        ArrayList<Guideline> guidelines = new ArrayList<>();
        int mChildrenSize = this.mChildren.size();
        for (int i = 0; i < mChildrenSize; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            if (widget instanceof Guideline) {
                Guideline guideline = (Guideline) widget;
                if (guideline.getOrientation() == 0) {
                    guidelines.add(guideline);
                }
            }
        }
        return guidelines;
    }

    public LinearSystem getSystem() {
        return this.mSystem;
    }

    private void resetChains() {
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
    }

    /* access modifiers changed from: 0000 */
    public void addChain(ConstraintWidget constraintWidget, int type) {
        ConstraintWidget widget = constraintWidget;
        if (type == 0) {
            addHorizontalChain(widget);
        } else if (type == 1) {
            addVerticalChain(widget);
        }
    }

    private void addHorizontalChain(ConstraintWidget widget) {
        int i = this.mHorizontalChainsSize + 1;
        ChainHead[] chainHeadArr = this.mHorizontalChainsArray;
        if (i >= chainHeadArr.length) {
            this.mHorizontalChainsArray = (ChainHead[]) Arrays.copyOf(chainHeadArr, chainHeadArr.length * 2);
        }
        this.mHorizontalChainsArray[this.mHorizontalChainsSize] = new ChainHead(widget, 0, isRtl());
        this.mHorizontalChainsSize++;
    }

    private void addVerticalChain(ConstraintWidget widget) {
        int i = this.mVerticalChainsSize + 1;
        ChainHead[] chainHeadArr = this.mVerticalChainsArray;
        if (i >= chainHeadArr.length) {
            this.mVerticalChainsArray = (ChainHead[]) Arrays.copyOf(chainHeadArr, chainHeadArr.length * 2);
        }
        this.mVerticalChainsArray[this.mVerticalChainsSize] = new ChainHead(widget, 1, isRtl());
        this.mVerticalChainsSize++;
    }

    public List<ConstraintWidgetGroup> getWidgetGroups() {
        return this.mWidgetGroups;
    }
}
