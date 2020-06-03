package androidx.navigation;

import android.os.Bundle;

public final class ActionOnlyNavDirections implements NavDirections {
    private final int mActionId;

    public ActionOnlyNavDirections(int actionId) {
        this.mActionId = actionId;
    }

    public int getActionId() {
        return this.mActionId;
    }

    public Bundle getArguments() {
        return new Bundle();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        if (getActionId() != ((ActionOnlyNavDirections) object).getActionId()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (1 * 31) + getActionId();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ActionOnlyNavDirections(actionId=");
        sb.append(getActionId());
        sb.append(")");
        return sb.toString();
    }
}
