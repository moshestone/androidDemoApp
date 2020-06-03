package androidx.navigation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import androidx.navigation.NavArgument.Builder;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public final class NavInflater {
    private static final String APPLICATION_ID_PLACEHOLDER = "${applicationId}";
    private static final String TAG_ACTION = "action";
    private static final String TAG_ARGUMENT = "argument";
    private static final String TAG_DEEP_LINK = "deepLink";
    private static final String TAG_INCLUDE = "include";
    private static final ThreadLocal<TypedValue> sTmpValue = new ThreadLocal<>();
    private Context mContext;
    private NavigatorProvider mNavigatorProvider;

    public NavInflater(Context context, NavigatorProvider navigatorProvider) {
        this.mContext = context;
        this.mNavigatorProvider = navigatorProvider;
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x004b A[Catch:{ Exception -> 0x0055, all -> 0x0053 }] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x001c A[Catch:{ Exception -> 0x0055, all -> 0x0053 }] */
    public NavGraph inflate(int graphResId) {
        int type;
        Resources res = this.mContext.getResources();
        XmlResourceParser parser = res.getXml(graphResId);
        AttributeSet attrs = Xml.asAttributeSet(parser);
        while (true) {
            try {
                int next = parser.next();
                type = next;
                if (next == 2 || type == 1) {
                    if (type != 2) {
                        String rootElement = parser.getName();
                        NavDestination destination = inflate(res, parser, attrs, graphResId);
                        if (destination instanceof NavGraph) {
                            NavGraph navGraph = (NavGraph) destination;
                            parser.close();
                            return navGraph;
                        }
                        StringBuilder sb = new StringBuilder();
                        sb.append("Root element <");
                        sb.append(rootElement);
                        sb.append("> did not inflate into a NavGraph");
                        throw new IllegalArgumentException(sb.toString());
                    }
                    throw new XmlPullParserException("No start tag found");
                }
            } catch (Exception e) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Exception inflating ");
                sb2.append(res.getResourceName(graphResId));
                sb2.append(" line ");
                sb2.append(parser.getLineNumber());
                throw new RuntimeException(sb2.toString(), e);
            } catch (Throwable th) {
                parser.close();
                throw th;
            }
        }
        if (type != 2) {
        }
    }

    private NavDestination inflate(Resources res, XmlResourceParser parser, AttributeSet attrs, int graphResId) throws XmlPullParserException, IOException {
        Resources resources = res;
        AttributeSet attributeSet = attrs;
        NavDestination dest = this.mNavigatorProvider.getNavigator(parser.getName()).createDestination();
        dest.onInflate(this.mContext, attributeSet);
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                break;
            }
            int depth = parser.getDepth();
            int depth2 = depth;
            if (depth < innerDepth && type == 3) {
                break;
            } else if (type == 2 && depth2 <= innerDepth) {
                String name = parser.getName();
                if (TAG_ARGUMENT.equals(name)) {
                    inflateArgumentForDestination(resources, dest, attributeSet, graphResId);
                } else {
                    int i = graphResId;
                    if (TAG_DEEP_LINK.equals(name)) {
                        inflateDeepLink(resources, dest, attributeSet);
                    } else if (TAG_ACTION.equals(name)) {
                        inflateAction(res, dest, attrs, parser, graphResId);
                    } else if (TAG_INCLUDE.equals(name) && (dest instanceof NavGraph)) {
                        TypedArray a = resources.obtainAttributes(attributeSet, R.styleable.NavInclude);
                        ((NavGraph) dest).addDestination(inflate(a.getResourceId(R.styleable.NavInclude_graph, 0)));
                        a.recycle();
                    } else if (dest instanceof NavGraph) {
                        ((NavGraph) dest).addDestination(inflate(res, parser, attrs, graphResId));
                    }
                }
            }
        }
        return dest;
    }

    private void inflateArgumentForDestination(Resources res, NavDestination dest, AttributeSet attrs, int graphResId) throws XmlPullParserException {
        TypedArray a = res.obtainAttributes(attrs, R.styleable.NavArgument);
        String name = a.getString(R.styleable.NavArgument_android_name);
        if (name != null) {
            dest.addArgument(name, inflateArgument(a, res, graphResId));
            a.recycle();
            return;
        }
        throw new XmlPullParserException("Arguments must have a name");
    }

    private void inflateArgumentForBundle(Resources res, Bundle bundle, AttributeSet attrs, int graphResId) throws XmlPullParserException {
        TypedArray a = res.obtainAttributes(attrs, R.styleable.NavArgument);
        String name = a.getString(R.styleable.NavArgument_android_name);
        if (name != null) {
            NavArgument argument = inflateArgument(a, res, graphResId);
            if (argument.isDefaultValuePresent()) {
                argument.putDefaultValue(name, bundle);
            }
            a.recycle();
            return;
        }
        throw new XmlPullParserException("Arguments must have a name");
    }

    private NavArgument inflateArgument(TypedArray a, Resources res, int graphResId) throws XmlPullParserException {
        Builder argumentBuilder = new Builder();
        boolean z = false;
        argumentBuilder.setIsNullable(a.getBoolean(R.styleable.NavArgument_nullable, false));
        TypedValue value = (TypedValue) sTmpValue.get();
        if (value == null) {
            value = new TypedValue();
            sTmpValue.set(value);
        }
        Object defaultValue = null;
        NavType navType = null;
        String argType = a.getString(R.styleable.NavArgument_argType);
        if (argType != null) {
            navType = NavType.fromArgType(argType, res.getResourcePackageName(graphResId));
        }
        if (a.getValue(R.styleable.NavArgument_android_defaultValue, value)) {
            String str = "' for ";
            String str2 = "unsupported value '";
            if (navType == NavType.ReferenceType) {
                if (value.resourceId != 0) {
                    defaultValue = Integer.valueOf(value.resourceId);
                } else if (value.type == 16 && value.data == 0) {
                    defaultValue = Integer.valueOf(0);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(str2);
                    sb.append(value.string);
                    sb.append(str);
                    sb.append(navType.getName());
                    sb.append(". Must be a reference to a resource.");
                    throw new XmlPullParserException(sb.toString());
                }
            } else if (value.resourceId != 0) {
                if (navType == null) {
                    navType = NavType.ReferenceType;
                    defaultValue = Integer.valueOf(value.resourceId);
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str2);
                    sb2.append(value.string);
                    sb2.append(str);
                    sb2.append(navType.getName());
                    sb2.append(". You must use a \"");
                    sb2.append(NavType.ReferenceType.getName());
                    sb2.append("\" type to reference other resources.");
                    throw new XmlPullParserException(sb2.toString());
                }
            } else if (navType == NavType.StringType) {
                defaultValue = a.getString(R.styleable.NavArgument_android_defaultValue);
            } else {
                int i = value.type;
                if (i == 3) {
                    String stringValue = value.string.toString();
                    if (navType == null) {
                        navType = NavType.inferFromValue(stringValue);
                    }
                    defaultValue = navType.parseValue(stringValue);
                } else if (i == 4) {
                    navType = checkNavType(value, navType, NavType.FloatType, argType, "float");
                    defaultValue = Float.valueOf(value.getFloat());
                } else if (i == 5) {
                    navType = checkNavType(value, navType, NavType.IntType, argType, "dimension");
                    defaultValue = Integer.valueOf((int) value.getDimension(res.getDisplayMetrics()));
                } else if (i == 18) {
                    navType = checkNavType(value, navType, NavType.BoolType, argType, "boolean");
                    if (value.data != 0) {
                        z = true;
                    }
                    defaultValue = Boolean.valueOf(z);
                } else if (value.type < 16 || value.type > 31) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("unsupported argument type ");
                    sb3.append(value.type);
                    throw new XmlPullParserException(sb3.toString());
                } else {
                    navType = checkNavType(value, navType, NavType.IntType, argType, "integer");
                    defaultValue = Integer.valueOf(value.data);
                }
            }
        }
        if (defaultValue != null) {
            argumentBuilder.setDefaultValue(defaultValue);
        }
        if (navType != null) {
            argumentBuilder.setType(navType);
        }
        return argumentBuilder.build();
    }

    private static NavType checkNavType(TypedValue value, NavType navType, NavType expectedNavType, String argType, String foundType) throws XmlPullParserException {
        if (navType == null || navType == expectedNavType) {
            return navType != null ? navType : expectedNavType;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Type is ");
        sb.append(argType);
        sb.append(" but found ");
        sb.append(foundType);
        sb.append(": ");
        sb.append(value.data);
        throw new XmlPullParserException(sb.toString());
    }

    private void inflateDeepLink(Resources res, NavDestination dest, AttributeSet attrs) {
        TypedArray a = res.obtainAttributes(attrs, R.styleable.NavDeepLink);
        String uri = a.getString(R.styleable.NavDeepLink_uri);
        if (!TextUtils.isEmpty(uri)) {
            dest.addDeepLink(uri.replace(APPLICATION_ID_PLACEHOLDER, this.mContext.getPackageName()));
            a.recycle();
            return;
        }
        throw new IllegalArgumentException("Every <deepLink> must include an app:uri");
    }

    private void inflateAction(Resources res, NavDestination dest, AttributeSet attrs, XmlResourceParser parser, int graphResId) throws IOException, XmlPullParserException {
        Resources resources = res;
        AttributeSet attributeSet = attrs;
        TypedArray a = resources.obtainAttributes(attributeSet, R.styleable.NavAction);
        int id = a.getResourceId(R.styleable.NavAction_android_id, 0);
        NavAction action = new NavAction(a.getResourceId(R.styleable.NavAction_destination, 0));
        NavOptions.Builder builder = new NavOptions.Builder();
        builder.setLaunchSingleTop(a.getBoolean(R.styleable.NavAction_launchSingleTop, false));
        builder.setPopUpTo(a.getResourceId(R.styleable.NavAction_popUpTo, -1), a.getBoolean(R.styleable.NavAction_popUpToInclusive, false));
        builder.setEnterAnim(a.getResourceId(R.styleable.NavAction_enterAnim, -1));
        builder.setExitAnim(a.getResourceId(R.styleable.NavAction_exitAnim, -1));
        builder.setPopEnterAnim(a.getResourceId(R.styleable.NavAction_popEnterAnim, -1));
        builder.setPopExitAnim(a.getResourceId(R.styleable.NavAction_popExitAnim, -1));
        action.setNavOptions(builder.build());
        Bundle args = new Bundle();
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                int i = graphResId;
                break;
            }
            int depth = parser.getDepth();
            int depth2 = depth;
            if (depth < innerDepth && type == 3) {
                int i2 = graphResId;
                break;
            } else if (type == 2 && depth2 <= innerDepth) {
                if (TAG_ARGUMENT.equals(parser.getName())) {
                    inflateArgumentForBundle(resources, args, attributeSet, graphResId);
                } else {
                    int i3 = graphResId;
                }
            }
        }
        if (!args.isEmpty()) {
            action.setDefaultArguments(args);
        }
        dest.putAction(id, action);
        a.recycle();
    }
}
