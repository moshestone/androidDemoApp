package androidx.navigation;

import androidx.navigation.Navigator.Name;
import java.util.HashMap;
import java.util.Map;

public class NavigatorProvider {
    private static final HashMap<Class, String> sAnnotationNames = new HashMap<>();
    private final HashMap<String, Navigator<? extends NavDestination>> mNavigators = new HashMap<>();

    private static boolean validateName(String name) {
        return name != null && !name.isEmpty();
    }

    static String getNameForNavigator(Class<? extends Navigator> navigatorClass) {
        String name = (String) sAnnotationNames.get(navigatorClass);
        if (name == null) {
            Name annotation = (Name) navigatorClass.getAnnotation(Name.class);
            name = annotation != null ? annotation.value() : null;
            if (validateName(name)) {
                sAnnotationNames.put(navigatorClass, name);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("No @Navigator.Name annotation found for ");
                sb.append(navigatorClass.getSimpleName());
                throw new IllegalArgumentException(sb.toString());
            }
        }
        return name;
    }

    public final <T extends Navigator<?>> T getNavigator(Class<T> navigatorClass) {
        return getNavigator(getNameForNavigator(navigatorClass));
    }

    public <T extends Navigator<?>> T getNavigator(String name) {
        if (validateName(name)) {
            Navigator<? extends NavDestination> navigator = (Navigator) this.mNavigators.get(name);
            if (navigator != null) {
                return navigator;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Could not find Navigator with name \"");
            sb.append(name);
            sb.append("\". You must call NavController.addNavigator() for each navigation type.");
            throw new IllegalStateException(sb.toString());
        }
        throw new IllegalArgumentException("navigator name cannot be an empty string");
    }

    public final Navigator<? extends NavDestination> addNavigator(Navigator<? extends NavDestination> navigator) {
        return addNavigator(getNameForNavigator(navigator.getClass()), navigator);
    }

    public Navigator<? extends NavDestination> addNavigator(String name, Navigator<? extends NavDestination> navigator) {
        if (validateName(name)) {
            return (Navigator) this.mNavigators.put(name, navigator);
        }
        throw new IllegalArgumentException("navigator name cannot be an empty string");
    }

    /* access modifiers changed from: 0000 */
    public Map<String, Navigator<? extends NavDestination>> getNavigators() {
        return this.mNavigators;
    }
}
