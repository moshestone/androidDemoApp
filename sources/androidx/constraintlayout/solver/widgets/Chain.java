package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.ArrayRow;
import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.SolverVariable;
import androidx.constraintlayout.solver.widgets.ConstraintWidget.DimensionBehaviour;
import java.util.ArrayList;

class Chain {
    private static final boolean DEBUG = false;

    Chain() {
    }

    static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem system, int orientation) {
        ChainHead[] chainsArray;
        int chainsSize;
        int offset;
        if (orientation == 0) {
            offset = 0;
            chainsSize = constraintWidgetContainer.mHorizontalChainsSize;
            chainsArray = constraintWidgetContainer.mHorizontalChainsArray;
        } else {
            offset = 2;
            chainsSize = constraintWidgetContainer.mVerticalChainsSize;
            chainsArray = constraintWidgetContainer.mVerticalChainsArray;
        }
        for (int i = 0; i < chainsSize; i++) {
            ChainHead first = chainsArray[i];
            first.define();
            if (!constraintWidgetContainer.optimizeFor(4)) {
                applyChainConstraints(constraintWidgetContainer, system, orientation, offset, first);
            } else if (!Optimizer.applyChainOptimized(constraintWidgetContainer, system, orientation, offset, first)) {
                applyChainConstraints(constraintWidgetContainer, system, orientation, offset, first);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:286:0x0636 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x0648  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x064d  */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0654  */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x0659  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x065c  */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x0670  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x0674  */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0680  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0683 A[ADDED_TO_REGION] */
    static void applyChainConstraints(ConstraintWidgetContainer container, LinearSystem system, int orientation, int offset, ChainHead chainHead) {
        boolean isChainPacked;
        boolean isChainSpread;
        boolean done;
        boolean isChainSpreadInside;
        ConstraintWidget widget;
        ConstraintWidget previousMatchConstraintsWidget;
        ConstraintWidget widget2;
        ArrayList<ConstraintWidget> listMatchConstraints;
        SolverVariable beginTarget;
        SolverVariable endTarget;
        ConstraintAnchor end;
        ConstraintAnchor end2;
        ConstraintAnchor endTarget2;
        ConstraintWidget previousVisibleWidget;
        ConstraintWidget widget3;
        ConstraintWidget next;
        ConstraintWidget next2;
        SolverVariable beginNext;
        SolverVariable beginNextTarget;
        ConstraintAnchor beginNextAnchor;
        int strength;
        ConstraintWidget next3;
        ConstraintWidget previousVisibleWidget2;
        ConstraintWidget widget4;
        ConstraintWidget next4;
        int nextMargin;
        SolverVariable beginTarget2;
        SolverVariable beginNextTarget2;
        SolverVariable beginNext2;
        ConstraintAnchor beginNextAnchor2;
        int margin1;
        int margin2;
        int strength2;
        ConstraintAnchor begin;
        ConstraintAnchor end3;
        float bias;
        float totalWeights;
        ConstraintWidget previousMatchConstraintsWidget2;
        ConstraintWidget widget5;
        ArrayList<ConstraintWidget> listMatchConstraints2;
        ConstraintWidget firstMatchConstraintsWidget;
        int margin;
        int strength3;
        float totalWeights2;
        int strength4;
        ConstraintWidget next5;
        ConstraintWidgetContainer constraintWidgetContainer = container;
        LinearSystem linearSystem = system;
        ChainHead chainHead2 = chainHead;
        ConstraintWidget first = chainHead2.mFirst;
        ConstraintWidget last = chainHead2.mLast;
        ConstraintWidget firstVisibleWidget = chainHead2.mFirstVisibleWidget;
        ConstraintWidget lastVisibleWidget = chainHead2.mLastVisibleWidget;
        ConstraintWidget head = chainHead2.mHead;
        ConstraintWidget widget6 = first;
        float totalWeights3 = chainHead2.mTotalWeight;
        ConstraintWidget firstMatchConstraintsWidget2 = chainHead2.mFirstMatchConstraintWidget;
        ConstraintWidget previousMatchConstraintsWidget3 = chainHead2.mLastMatchConstraintWidget;
        ConstraintWidget widget7 = widget6;
        boolean isWrapContent = constraintWidgetContainer.mListDimensionBehaviors[orientation] == DimensionBehaviour.WRAP_CONTENT;
        if (orientation == 0) {
            isChainSpread = head.mHorizontalChainStyle == 0;
            done = false;
            isChainPacked = head.mHorizontalChainStyle == 2;
            widget = widget7;
            isChainSpreadInside = head.mHorizontalChainStyle == 1;
        } else {
            isChainSpread = head.mVerticalChainStyle == 0;
            done = false;
            isChainPacked = head.mVerticalChainStyle == 2;
            widget = widget7;
            isChainSpreadInside = head.mVerticalChainStyle == 1;
        }
        while (!done) {
            ConstraintAnchor begin2 = widget.mListAnchors[offset];
            int strength5 = 4;
            if (isWrapContent || isChainPacked) {
                strength5 = 1;
            }
            int margin3 = begin2.getMargin();
            if (begin2.mTarget == null || widget == first) {
                margin = margin3;
            } else {
                margin = margin3 + begin2.mTarget.getMargin();
            }
            if (isChainPacked && widget != first && widget != firstVisibleWidget) {
                strength3 = 6;
            } else if (!isChainSpread || !isWrapContent) {
                strength3 = strength5;
            } else {
                strength3 = 4;
            }
            if (begin2.mTarget != null) {
                if (widget == firstVisibleWidget) {
                    totalWeights2 = totalWeights3;
                    linearSystem.addGreaterThan(begin2.mSolverVariable, begin2.mTarget.mSolverVariable, margin, 5);
                } else {
                    totalWeights2 = totalWeights3;
                    linearSystem.addGreaterThan(begin2.mSolverVariable, begin2.mTarget.mSolverVariable, margin, 6);
                }
                strength4 = strength3;
                linearSystem.addEquality(begin2.mSolverVariable, begin2.mTarget.mSolverVariable, margin, strength4);
            } else {
                totalWeights2 = totalWeights3;
                strength4 = strength3;
            }
            if (isWrapContent) {
                if (widget.getVisibility() == 8 || widget.mListDimensionBehaviors[orientation] != DimensionBehaviour.MATCH_CONSTRAINT) {
                    int i = strength4;
                } else {
                    ConstraintAnchor constraintAnchor = begin2;
                    int i2 = strength4;
                    linearSystem.addGreaterThan(widget.mListAnchors[offset + 1].mSolverVariable, widget.mListAnchors[offset].mSolverVariable, 0, 5);
                }
                linearSystem.addGreaterThan(widget.mListAnchors[offset].mSolverVariable, constraintWidgetContainer.mListAnchors[offset].mSolverVariable, 0, 6);
            } else {
                int i3 = strength4;
            }
            ConstraintAnchor nextAnchor = widget.mListAnchors[offset + 1].mTarget;
            if (nextAnchor != null) {
                ConstraintWidget next6 = nextAnchor.mOwner;
                if (next6.mListAnchors[offset].mTarget == null || next6.mListAnchors[offset].mTarget.mOwner != widget) {
                    next5 = null;
                } else {
                    next5 = next6;
                }
            } else {
                next5 = null;
            }
            if (next5 != null) {
                widget = next5;
            } else {
                done = true;
            }
            totalWeights3 = totalWeights2;
        }
        float totalWeights4 = totalWeights3;
        if (lastVisibleWidget != null && last.mListAnchors[offset + 1].mTarget != null) {
            ConstraintAnchor end4 = lastVisibleWidget.mListAnchors[offset + 1];
            linearSystem.addLowerThan(end4.mSolverVariable, last.mListAnchors[offset + 1].mTarget.mSolverVariable, -end4.getMargin(), 5);
        }
        if (isWrapContent) {
            linearSystem.addGreaterThan(constraintWidgetContainer.mListAnchors[offset + 1].mSolverVariable, last.mListAnchors[offset + 1].mSolverVariable, last.mListAnchors[offset + 1].getMargin(), 6);
        }
        ArrayList<ConstraintWidget> listMatchConstraints3 = chainHead2.mWeightedMatchConstraintsWidgets;
        if (listMatchConstraints3 != null) {
            int count = listMatchConstraints3.size();
            if (count > 1) {
                ConstraintWidget lastMatch = null;
                float lastWeight = 0.0f;
                if (!chainHead2.mHasUndefinedWeights || chainHead2.mHasComplexMatchWeights) {
                    totalWeights = totalWeights4;
                } else {
                    totalWeights = (float) chainHead2.mWidgetsMatchCount;
                }
                int i4 = 0;
                while (i4 < count) {
                    ConstraintWidget match = (ConstraintWidget) listMatchConstraints3.get(i4);
                    int count2 = count;
                    float currentWeight = match.mWeight[orientation];
                    if (currentWeight < 0.0f) {
                        float f = currentWeight;
                        if (chainHead2.mHasComplexMatchWeights) {
                            listMatchConstraints2 = listMatchConstraints3;
                            widget5 = widget;
                            previousMatchConstraintsWidget2 = previousMatchConstraintsWidget3;
                            linearSystem.addEquality(match.mListAnchors[offset + 1].mSolverVariable, match.mListAnchors[offset].mSolverVariable, 0, 4);
                            firstMatchConstraintsWidget = firstMatchConstraintsWidget2;
                            i4++;
                            ConstraintWidgetContainer constraintWidgetContainer2 = container;
                            firstMatchConstraintsWidget2 = firstMatchConstraintsWidget;
                            count = count2;
                            listMatchConstraints3 = listMatchConstraints2;
                            widget = widget5;
                            previousMatchConstraintsWidget3 = previousMatchConstraintsWidget2;
                        } else {
                            listMatchConstraints2 = listMatchConstraints3;
                            widget5 = widget;
                            previousMatchConstraintsWidget2 = previousMatchConstraintsWidget3;
                            currentWeight = 1.0f;
                        }
                    } else {
                        float f2 = currentWeight;
                        listMatchConstraints2 = listMatchConstraints3;
                        widget5 = widget;
                        previousMatchConstraintsWidget2 = previousMatchConstraintsWidget3;
                    }
                    if (currentWeight == 0.0f) {
                        firstMatchConstraintsWidget = firstMatchConstraintsWidget2;
                        linearSystem.addEquality(match.mListAnchors[offset + 1].mSolverVariable, match.mListAnchors[offset].mSolverVariable, 0, 6);
                    } else {
                        firstMatchConstraintsWidget = firstMatchConstraintsWidget2;
                        if (lastMatch != null) {
                            SolverVariable begin3 = lastMatch.mListAnchors[offset].mSolverVariable;
                            SolverVariable end5 = lastMatch.mListAnchors[offset + 1].mSolverVariable;
                            SolverVariable nextBegin = match.mListAnchors[offset].mSolverVariable;
                            SolverVariable nextEnd = match.mListAnchors[offset + 1].mSolverVariable;
                            ConstraintWidget constraintWidget = lastMatch;
                            ArrayRow row = system.createRow();
                            row.createRowEqualMatchDimensions(lastWeight, totalWeights, currentWeight, begin3, end5, nextBegin, nextEnd);
                            linearSystem.addConstraint(row);
                        }
                        lastMatch = match;
                        lastWeight = currentWeight;
                    }
                    i4++;
                    ConstraintWidgetContainer constraintWidgetContainer22 = container;
                    firstMatchConstraintsWidget2 = firstMatchConstraintsWidget;
                    count = count2;
                    listMatchConstraints3 = listMatchConstraints2;
                    widget = widget5;
                    previousMatchConstraintsWidget3 = previousMatchConstraintsWidget2;
                }
                ConstraintWidget constraintWidget2 = lastMatch;
                listMatchConstraints = listMatchConstraints3;
                widget2 = widget;
                previousMatchConstraintsWidget = previousMatchConstraintsWidget3;
                ConstraintWidget constraintWidget3 = firstMatchConstraintsWidget2;
                float f3 = totalWeights;
            } else {
                listMatchConstraints = listMatchConstraints3;
                widget2 = widget;
                previousMatchConstraintsWidget = previousMatchConstraintsWidget3;
                ConstraintWidget constraintWidget4 = firstMatchConstraintsWidget2;
            }
        } else {
            listMatchConstraints = listMatchConstraints3;
            widget2 = widget;
            previousMatchConstraintsWidget = previousMatchConstraintsWidget3;
            ConstraintWidget constraintWidget5 = firstMatchConstraintsWidget2;
        }
        if (firstVisibleWidget == null) {
            ArrayList<ConstraintWidget> arrayList = listMatchConstraints;
            ConstraintWidget constraintWidget6 = widget2;
            ConstraintWidget constraintWidget7 = previousMatchConstraintsWidget;
        } else if (firstVisibleWidget == lastVisibleWidget || isChainPacked) {
            ConstraintAnchor begin4 = first.mListAnchors[offset];
            ConstraintAnchor end6 = last.mListAnchors[offset + 1];
            SolverVariable beginTarget3 = first.mListAnchors[offset].mTarget != null ? first.mListAnchors[offset].mTarget.mSolverVariable : null;
            SolverVariable endTarget3 = last.mListAnchors[offset + 1].mTarget != null ? last.mListAnchors[offset + 1].mTarget.mSolverVariable : null;
            if (firstVisibleWidget == lastVisibleWidget) {
                begin = firstVisibleWidget.mListAnchors[offset];
                end3 = firstVisibleWidget.mListAnchors[offset + 1];
            } else {
                begin = begin4;
                end3 = end6;
            }
            if (beginTarget3 == null || endTarget3 == null) {
                ConstraintAnchor constraintAnchor2 = begin;
                ConstraintWidget constraintWidget8 = head;
                ArrayList<ConstraintWidget> arrayList2 = listMatchConstraints;
                ConstraintWidget constraintWidget9 = widget2;
                ConstraintWidget constraintWidget10 = previousMatchConstraintsWidget;
                if ((!isChainSpread || isChainSpreadInside) && firstVisibleWidget != null) {
                    ConstraintAnchor begin5 = firstVisibleWidget.mListAnchors[offset];
                    ConstraintAnchor end7 = lastVisibleWidget.mListAnchors[offset + 1];
                    beginTarget = begin5.mTarget == null ? begin5.mTarget.mSolverVariable : null;
                    SolverVariable endTarget4 = end7.mTarget == null ? end7.mTarget.mSolverVariable : null;
                    if (last == lastVisibleWidget) {
                        ConstraintAnchor realEnd = last.mListAnchors[offset + 1];
                        endTarget = realEnd.mTarget != null ? realEnd.mTarget.mSolverVariable : null;
                    } else {
                        endTarget = endTarget4;
                    }
                    if (firstVisibleWidget != lastVisibleWidget) {
                        begin5 = firstVisibleWidget.mListAnchors[offset];
                        end = firstVisibleWidget.mListAnchors[offset + 1];
                    } else {
                        end = end7;
                    }
                    if (beginTarget != null || endTarget == null) {
                    }
                    int beginMargin = begin5.getMargin();
                    if (lastVisibleWidget == null) {
                        lastVisibleWidget = last;
                    }
                    ConstraintAnchor constraintAnchor3 = end;
                    system.addCentering(begin5.mSolverVariable, beginTarget, beginMargin, 0.5f, endTarget, end.mSolverVariable, lastVisibleWidget.mListAnchors[offset + 1].getMargin(), 5);
                    return;
                }
                return;
            }
            if (orientation == 0) {
                bias = head.mHorizontalBiasPercent;
            } else {
                bias = head.mVerticalBiasPercent;
            }
            ArrayList<ConstraintWidget> arrayList3 = listMatchConstraints;
            ConstraintWidget constraintWidget11 = widget2;
            ConstraintAnchor constraintAnchor4 = end3;
            ConstraintWidget constraintWidget12 = previousMatchConstraintsWidget;
            ConstraintAnchor constraintAnchor5 = begin;
            ConstraintWidget constraintWidget13 = head;
            system.addCentering(begin.mSolverVariable, beginTarget3, begin.getMargin(), bias, endTarget3, end3.mSolverVariable, end3.getMargin(), 5);
            if (!isChainSpread) {
            }
            ConstraintAnchor begin52 = firstVisibleWidget.mListAnchors[offset];
            ConstraintAnchor end72 = lastVisibleWidget.mListAnchors[offset + 1];
            beginTarget = begin52.mTarget == null ? begin52.mTarget.mSolverVariable : null;
            if (end72.mTarget == null) {
            }
            if (last == lastVisibleWidget) {
            }
            if (firstVisibleWidget != lastVisibleWidget) {
            }
            if (beginTarget != null) {
            }
        } else {
            ConstraintWidget constraintWidget14 = head;
            ArrayList<ConstraintWidget> arrayList4 = listMatchConstraints;
            ConstraintWidget constraintWidget15 = widget2;
            ConstraintWidget constraintWidget16 = previousMatchConstraintsWidget;
        }
        if (!isChainSpread || firstVisibleWidget == null) {
            int i5 = 8;
            if (isChainSpreadInside && firstVisibleWidget != null) {
                boolean applyFixedEquality = chainHead2.mWidgetsMatchCount > 0 && chainHead2.mWidgetsCount == chainHead2.mWidgetsMatchCount;
                ConstraintWidget widget8 = firstVisibleWidget;
                ConstraintWidget previousVisibleWidget3 = firstVisibleWidget;
                while (widget8 != null) {
                    ConstraintWidget next7 = widget8.mNextChainWidget[orientation];
                    while (next7 != null && next7.getVisibility() == i5) {
                        next7 = next7.mNextChainWidget[orientation];
                    }
                    if (widget8 == firstVisibleWidget || widget8 == lastVisibleWidget || next7 == null) {
                        widget3 = widget8;
                        previousVisibleWidget = previousVisibleWidget3;
                        next = next7;
                    } else {
                        if (next7 == lastVisibleWidget) {
                            next2 = null;
                        } else {
                            next2 = next7;
                        }
                        ConstraintAnchor beginAnchor = widget8.mListAnchors[offset];
                        SolverVariable begin6 = beginAnchor.mSolverVariable;
                        if (beginAnchor.mTarget != null) {
                            SolverVariable solverVariable = beginAnchor.mTarget.mSolverVariable;
                        }
                        SolverVariable beginTarget4 = previousVisibleWidget3.mListAnchors[offset + 1].mSolverVariable;
                        SolverVariable beginNext3 = null;
                        int beginMargin2 = beginAnchor.getMargin();
                        int nextMargin2 = widget8.mListAnchors[offset + 1].getMargin();
                        if (next2 != null) {
                            ConstraintAnchor beginNextAnchor3 = next2.mListAnchors[offset];
                            beginNextTarget = beginNextAnchor3.mTarget != null ? beginNextAnchor3.mTarget.mSolverVariable : null;
                            beginNext = beginNextAnchor3.mSolverVariable;
                            beginNextAnchor = beginNextAnchor3;
                        } else {
                            ConstraintAnchor beginNextAnchor4 = widget8.mListAnchors[offset + 1].mTarget;
                            if (beginNextAnchor4 != null) {
                                beginNext3 = beginNextAnchor4.mSolverVariable;
                            }
                            beginNextAnchor = beginNextAnchor4;
                            beginNextTarget = widget8.mListAnchors[offset + 1].mSolverVariable;
                            beginNext = beginNext3;
                        }
                        if (beginNextAnchor != null) {
                            nextMargin2 += beginNextAnchor.getMargin();
                        }
                        if (previousVisibleWidget3 != null) {
                            beginMargin2 += previousVisibleWidget3.mListAnchors[offset + 1].getMargin();
                        }
                        if (applyFixedEquality) {
                            strength = 6;
                        } else {
                            strength = 4;
                        }
                        if (begin6 == null || beginTarget4 == null || beginNext == null || beginNextTarget == null) {
                            SolverVariable solverVariable2 = begin6;
                            ConstraintAnchor constraintAnchor6 = beginAnchor;
                            next3 = next2;
                            widget3 = widget8;
                            previousVisibleWidget = previousVisibleWidget3;
                        } else {
                            SolverVariable solverVariable3 = beginTarget4;
                            SolverVariable solverVariable4 = begin6;
                            ConstraintAnchor constraintAnchor7 = beginAnchor;
                            next3 = next2;
                            widget3 = widget8;
                            previousVisibleWidget = previousVisibleWidget3;
                            system.addCentering(begin6, beginTarget4, beginMargin2, 0.5f, beginNext, beginNextTarget, nextMargin2, strength);
                        }
                        next = next3;
                    }
                    if (widget3.getVisibility() != 8) {
                        previousVisibleWidget3 = widget3;
                    } else {
                        previousVisibleWidget3 = previousVisibleWidget;
                    }
                    widget8 = next;
                    i5 = 8;
                }
                ConstraintWidget widget9 = widget8;
                ConstraintWidget constraintWidget17 = previousVisibleWidget3;
                ConstraintAnchor begin7 = firstVisibleWidget.mListAnchors[offset];
                ConstraintAnchor beginTarget5 = first.mListAnchors[offset].mTarget;
                ConstraintAnchor end8 = lastVisibleWidget.mListAnchors[offset + 1];
                ConstraintAnchor endTarget5 = last.mListAnchors[offset + 1].mTarget;
                if (beginTarget5 == null) {
                    endTarget2 = endTarget5;
                    end2 = end8;
                    ConstraintAnchor constraintAnchor8 = beginTarget5;
                } else if (firstVisibleWidget != lastVisibleWidget) {
                    linearSystem.addEquality(begin7.mSolverVariable, beginTarget5.mSolverVariable, begin7.getMargin(), 5);
                    endTarget2 = endTarget5;
                    end2 = end8;
                    ConstraintAnchor constraintAnchor9 = beginTarget5;
                } else if (endTarget5 != null) {
                    endTarget2 = endTarget5;
                    end2 = end8;
                    ConstraintAnchor constraintAnchor10 = beginTarget5;
                    system.addCentering(begin7.mSolverVariable, beginTarget5.mSolverVariable, begin7.getMargin(), 0.5f, end8.mSolverVariable, endTarget5.mSolverVariable, end8.getMargin(), 5);
                } else {
                    endTarget2 = endTarget5;
                    end2 = end8;
                    ConstraintAnchor constraintAnchor11 = beginTarget5;
                }
                ConstraintAnchor endTarget6 = endTarget2;
                if (endTarget6 == null || firstVisibleWidget == lastVisibleWidget) {
                } else {
                    ConstraintAnchor end9 = end2;
                    linearSystem.addEquality(end9.mSolverVariable, endTarget6.mSolverVariable, -end9.getMargin(), 5);
                }
                ConstraintWidget constraintWidget18 = widget9;
            }
            if (!isChainSpread) {
            }
            ConstraintAnchor begin522 = firstVisibleWidget.mListAnchors[offset];
            ConstraintAnchor end722 = lastVisibleWidget.mListAnchors[offset + 1];
            beginTarget = begin522.mTarget == null ? begin522.mTarget.mSolverVariable : null;
            if (end722.mTarget == null) {
            }
            if (last == lastVisibleWidget) {
            }
            if (firstVisibleWidget != lastVisibleWidget) {
            }
            if (beginTarget != null) {
            }
        }
        boolean applyFixedEquality2 = chainHead2.mWidgetsMatchCount > 0 && chainHead2.mWidgetsCount == chainHead2.mWidgetsMatchCount;
        ConstraintWidget widget10 = firstVisibleWidget;
        ConstraintWidget previousVisibleWidget4 = firstVisibleWidget;
        while (widget10 != null) {
            ConstraintWidget next8 = widget10.mNextChainWidget[orientation];
            while (true) {
                if (next8 != null) {
                    if (next8.getVisibility() != 8) {
                        break;
                    }
                    next8 = next8.mNextChainWidget[orientation];
                } else {
                    break;
                }
            }
            if (next8 != null || widget10 == lastVisibleWidget) {
                ConstraintAnchor beginAnchor2 = widget10.mListAnchors[offset];
                SolverVariable begin8 = beginAnchor2.mSolverVariable;
                SolverVariable beginTarget6 = beginAnchor2.mTarget != null ? beginAnchor2.mTarget.mSolverVariable : null;
                if (previousVisibleWidget4 != widget10) {
                    beginTarget2 = previousVisibleWidget4.mListAnchors[offset + 1].mSolverVariable;
                } else if (widget10 == firstVisibleWidget && previousVisibleWidget4 == widget10) {
                    beginTarget2 = first.mListAnchors[offset].mTarget != null ? first.mListAnchors[offset].mTarget.mSolverVariable : null;
                } else {
                    beginTarget2 = beginTarget6;
                }
                SolverVariable beginNext4 = null;
                int beginMargin3 = beginAnchor2.getMargin();
                int nextMargin3 = widget10.mListAnchors[offset + 1].getMargin();
                if (next8 != null) {
                    ConstraintAnchor beginNextAnchor5 = next8.mListAnchors[offset];
                    beginNextAnchor2 = beginNextAnchor5;
                    beginNext2 = beginNextAnchor5.mSolverVariable;
                    beginNextTarget2 = widget10.mListAnchors[offset + 1].mSolverVariable;
                } else {
                    ConstraintAnchor beginNextAnchor6 = last.mListAnchors[offset + 1].mTarget;
                    if (beginNextAnchor6 != null) {
                        beginNext4 = beginNextAnchor6.mSolverVariable;
                    }
                    beginNextAnchor2 = beginNextAnchor6;
                    beginNext2 = beginNext4;
                    beginNextTarget2 = widget10.mListAnchors[offset + 1].mSolverVariable;
                }
                if (beginNextAnchor2 != null) {
                    nextMargin3 += beginNextAnchor2.getMargin();
                }
                if (previousVisibleWidget4 != null) {
                    beginMargin3 += previousVisibleWidget4.mListAnchors[offset + 1].getMargin();
                }
                if (begin8 == null || beginTarget2 == null || beginNext2 == null || beginNextTarget2 == null) {
                    SolverVariable solverVariable5 = begin8;
                    ConstraintAnchor constraintAnchor12 = beginAnchor2;
                    next4 = next8;
                    widget4 = widget10;
                    previousVisibleWidget2 = previousVisibleWidget4;
                    nextMargin = 8;
                } else {
                    int margin12 = beginMargin3;
                    if (widget10 == firstVisibleWidget) {
                        margin1 = firstVisibleWidget.mListAnchors[offset].getMargin();
                    } else {
                        margin1 = margin12;
                    }
                    int margin13 = nextMargin3;
                    if (widget10 == lastVisibleWidget) {
                        margin2 = lastVisibleWidget.mListAnchors[offset + 1].getMargin();
                    } else {
                        margin2 = margin13;
                    }
                    if (applyFixedEquality2) {
                        strength2 = 6;
                    } else {
                        strength2 = 4;
                    }
                    SolverVariable solverVariable6 = begin8;
                    int i6 = nextMargin3;
                    nextMargin = 8;
                    ConstraintAnchor constraintAnchor13 = beginAnchor2;
                    next4 = next8;
                    widget4 = widget10;
                    previousVisibleWidget2 = previousVisibleWidget4;
                    system.addCentering(begin8, beginTarget2, margin1, 0.5f, beginNext2, beginNextTarget2, margin2, strength2);
                }
            } else {
                next4 = next8;
                widget4 = widget10;
                previousVisibleWidget2 = previousVisibleWidget4;
                nextMargin = 8;
            }
            if (widget4.getVisibility() != nextMargin) {
                previousVisibleWidget4 = widget4;
            } else {
                previousVisibleWidget4 = previousVisibleWidget2;
            }
            widget10 = next4;
            ConstraintWidget constraintWidget19 = next4;
        }
        ConstraintWidget constraintWidget20 = previousVisibleWidget4;
        ConstraintWidget constraintWidget21 = widget10;
        if (!isChainSpread) {
        }
        ConstraintAnchor begin5222 = firstVisibleWidget.mListAnchors[offset];
        ConstraintAnchor end7222 = lastVisibleWidget.mListAnchors[offset + 1];
        beginTarget = begin5222.mTarget == null ? begin5222.mTarget.mSolverVariable : null;
        if (end7222.mTarget == null) {
        }
        if (last == lastVisibleWidget) {
        }
        if (firstVisibleWidget != lastVisibleWidget) {
        }
        if (beginTarget != null) {
        }
    }
}
