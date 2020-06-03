package com.example.javasecondapp;

import android.os.Bundle;
import androidx.navigation.NavArgs;
import java.util.HashMap;

public class SecondFragmentArgs implements NavArgs {
    /* access modifiers changed from: private */
    public final HashMap arguments;

    public static class Builder {
        private final HashMap arguments;

        public Builder(SecondFragmentArgs original) {
            HashMap hashMap = new HashMap();
            this.arguments = hashMap;
            hashMap.putAll(original.arguments);
        }

        public Builder(int myArg) {
            HashMap hashMap = new HashMap();
            this.arguments = hashMap;
            hashMap.put("myArg", Integer.valueOf(myArg));
        }

        public SecondFragmentArgs build() {
            return new SecondFragmentArgs(this.arguments);
        }

        public Builder setMyArg(int myArg) {
            this.arguments.put("myArg", Integer.valueOf(myArg));
            return this;
        }

        public int getMyArg() {
            return ((Integer) this.arguments.get("myArg")).intValue();
        }
    }

    private SecondFragmentArgs() {
        this.arguments = new HashMap();
    }

    private SecondFragmentArgs(HashMap argumentsMap) {
        HashMap hashMap = new HashMap();
        this.arguments = hashMap;
        hashMap.putAll(argumentsMap);
    }

    public static SecondFragmentArgs fromBundle(Bundle bundle) {
        SecondFragmentArgs __result = new SecondFragmentArgs();
        bundle.setClassLoader(SecondFragmentArgs.class.getClassLoader());
        String str = "myArg";
        if (bundle.containsKey(str)) {
            __result.arguments.put(str, Integer.valueOf(bundle.getInt(str)));
            return __result;
        }
        throw new IllegalArgumentException("Required argument \"myArg\" is missing and does not have an android:defaultValue");
    }

    public int getMyArg() {
        return ((Integer) this.arguments.get("myArg")).intValue();
    }

    public Bundle toBundle() {
        Bundle __result = new Bundle();
        String str = "myArg";
        if (this.arguments.containsKey(str)) {
            __result.putInt(str, ((Integer) this.arguments.get(str)).intValue());
        }
        return __result;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        SecondFragmentArgs that = (SecondFragmentArgs) object;
        String str = "myArg";
        if (this.arguments.containsKey(str) == that.arguments.containsKey(str) && getMyArg() == that.getMyArg()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (1 * 31) + getMyArg();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SecondFragmentArgs{myArg=");
        sb.append(getMyArg());
        sb.append("}");
        return sb.toString();
    }
}
