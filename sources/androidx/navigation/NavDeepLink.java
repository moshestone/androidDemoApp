package androidx.navigation;

import android.net.Uri;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NavDeepLink {
    private static final Pattern SCHEME_PATTERN = Pattern.compile("^[a-zA-Z]+[+\\w\\-.]*:");
    private final ArrayList<String> mArguments = new ArrayList<>();
    private final boolean mExactDeepLink;
    private final Pattern mPattern;

    NavDeepLink(String uri) {
        StringBuilder uriRegex = new StringBuilder("^");
        if (!SCHEME_PATTERN.matcher(uri).find()) {
            uriRegex.append("http[s]?://");
        }
        Matcher matcher = Pattern.compile("\\{(.+?)\\}").matcher(uri);
        int appendPos = 0;
        String str = ".*";
        boolean exactDeepLink = !uri.contains(str);
        while (matcher.find()) {
            this.mArguments.add(matcher.group(1));
            uriRegex.append(Pattern.quote(uri.substring(appendPos, matcher.start())));
            uriRegex.append("(.+?)");
            appendPos = matcher.end();
            exactDeepLink = false;
        }
        if (appendPos < uri.length()) {
            uriRegex.append(Pattern.quote(uri.substring(appendPos)));
        }
        this.mPattern = Pattern.compile(uriRegex.toString().replace(str, "\\E.*\\Q"));
        this.mExactDeepLink = exactDeepLink;
    }

    /* access modifiers changed from: 0000 */
    public boolean matches(Uri deepLink) {
        return this.mPattern.matcher(deepLink.toString()).matches();
    }

    /* access modifiers changed from: 0000 */
    public boolean isExactDeepLink() {
        return this.mExactDeepLink;
    }

    /* access modifiers changed from: 0000 */
    public Bundle getMatchingArguments(Uri deepLink, Map<String, NavArgument> arguments) {
        Matcher matcher = this.mPattern.matcher(deepLink.toString());
        if (!matcher.matches()) {
            return null;
        }
        Bundle bundle = new Bundle();
        int size = this.mArguments.size();
        for (int index = 0; index < size; index++) {
            String argumentName = (String) this.mArguments.get(index);
            String value = Uri.decode(matcher.group(index + 1));
            NavArgument argument = (NavArgument) arguments.get(argumentName);
            if (argument != null) {
                try {
                    argument.getType().parseAndPut(bundle, argumentName, value);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            } else {
                bundle.putString(argumentName, value);
            }
        }
        return bundle;
    }
}
