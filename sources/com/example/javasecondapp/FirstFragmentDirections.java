package com.example.javasecondapp;

import android.os.Bundle;
import androidx.navigation.NavDirections;
import java.util.HashMap;

public class FirstFragmentDirections {

    public static class ActionFirstFragmentToSecondFragment implements NavDirections {
        private final HashMap arguments;

        private ActionFirstFragmentToSecondFragment(int myArg) {
            HashMap hashMap = new HashMap();
            this.arguments = hashMap;
            hashMap.put("myArg", Integer.valueOf(myArg));
        }

        public ActionFirstFragmentToSecondFragment setMyArg(int myArg) {
            this.arguments.put("myArg", Integer.valueOf(myArg));
            return this;
        }

        public Bundle getArguments() {
            Bundle __result = new Bundle();
            String str = "myArg";
            if (this.arguments.containsKey(str)) {
                __result.putInt(str, ((Integer) this.arguments.get(str)).intValue());
            }
            return __result;
        }

        public int getActionId() {
            return R.id.action_FirstFragment_to_SecondFragment;
        }

        public int getMyArg() {
            return ((Integer) this.arguments.get("myArg")).intValue();
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            ActionFirstFragmentToSecondFragment that = (ActionFirstFragmentToSecondFragment) object;
            String str = "myArg";
            if (this.arguments.containsKey(str) == that.arguments.containsKey(str) && getMyArg() == that.getMyArg() && getActionId() == that.getActionId()) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (((1 * 31) + getMyArg()) * 31) + getActionId();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ActionFirstFragmentToSecondFragment(actionId=");
            sb.append(getActionId());
            sb.append("){myArg=");
            sb.append(getMyArg());
            sb.append("}");
            return sb.toString();
        }
    }

    private FirstFragmentDirections() {
    }

    public static ActionFirstFragmentToSecondFragment actionFirstFragmentToSecondFragment(int myArg) {
        return new ActionFirstFragmentToSecondFragment(myArg);
    }
}
