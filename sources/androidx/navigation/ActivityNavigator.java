package androidx.navigation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.navigation.Navigator.Name;
import com.example.javasecondapp.BuildConfig;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Name("activity")
public class ActivityNavigator extends Navigator<Destination> {
    private static final String EXTRA_NAV_CURRENT = "android-support-navigation:ActivityNavigator:current";
    private static final String EXTRA_NAV_SOURCE = "android-support-navigation:ActivityNavigator:source";
    private static final String EXTRA_POP_ENTER_ANIM = "android-support-navigation:ActivityNavigator:popEnterAnim";
    private static final String EXTRA_POP_EXIT_ANIM = "android-support-navigation:ActivityNavigator:popExitAnim";
    private Context mContext;
    private Activity mHostActivity;

    public static class Destination extends NavDestination {
        private String mDataPattern;
        private Intent mIntent;

        public Destination(NavigatorProvider navigatorProvider) {
            this(navigatorProvider.getNavigator(ActivityNavigator.class));
        }

        public Destination(Navigator<? extends Destination> activityNavigator) {
            super(activityNavigator);
        }

        public void onInflate(Context context, AttributeSet attrs) {
            super.onInflate(context, attrs);
            TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.ActivityNavigator);
            String className = a.getString(R.styleable.ActivityNavigator_android_name);
            if (className != null) {
                setComponentName(new ComponentName(context, parseClassFromName(context, className, Activity.class)));
            }
            setAction(a.getString(R.styleable.ActivityNavigator_action));
            String data = a.getString(R.styleable.ActivityNavigator_data);
            if (data != null) {
                setData(Uri.parse(data));
            }
            setDataPattern(a.getString(R.styleable.ActivityNavigator_dataPattern));
            a.recycle();
        }

        public final Destination setIntent(Intent intent) {
            this.mIntent = intent;
            return this;
        }

        public final Intent getIntent() {
            return this.mIntent;
        }

        public final Destination setComponentName(ComponentName name) {
            if (this.mIntent == null) {
                this.mIntent = new Intent();
            }
            this.mIntent.setComponent(name);
            return this;
        }

        public final ComponentName getComponent() {
            Intent intent = this.mIntent;
            if (intent == null) {
                return null;
            }
            return intent.getComponent();
        }

        public final Destination setAction(String action) {
            if (this.mIntent == null) {
                this.mIntent = new Intent();
            }
            this.mIntent.setAction(action);
            return this;
        }

        public final String getAction() {
            Intent intent = this.mIntent;
            if (intent == null) {
                return null;
            }
            return intent.getAction();
        }

        public final Destination setData(Uri data) {
            if (this.mIntent == null) {
                this.mIntent = new Intent();
            }
            this.mIntent.setData(data);
            return this;
        }

        public final Uri getData() {
            Intent intent = this.mIntent;
            if (intent == null) {
                return null;
            }
            return intent.getData();
        }

        public final Destination setDataPattern(String dataPattern) {
            this.mDataPattern = dataPattern;
            return this;
        }

        public final String getDataPattern() {
            return this.mDataPattern;
        }

        /* access modifiers changed from: 0000 */
        public boolean supportsActions() {
            return false;
        }
    }

    public static final class Extras implements androidx.navigation.Navigator.Extras {
        private final ActivityOptionsCompat mActivityOptions;
        private final int mFlags;

        public static final class Builder {
            private ActivityOptionsCompat mActivityOptions;
            private int mFlags;

            public Builder addFlags(int flags) {
                this.mFlags |= flags;
                return this;
            }

            public Builder setActivityOptions(ActivityOptionsCompat activityOptions) {
                this.mActivityOptions = activityOptions;
                return this;
            }

            public Extras build() {
                return new Extras(this.mFlags, this.mActivityOptions);
            }
        }

        Extras(int flags, ActivityOptionsCompat activityOptions) {
            this.mFlags = flags;
            this.mActivityOptions = activityOptions;
        }

        public int getFlags() {
            return this.mFlags;
        }

        public ActivityOptionsCompat getActivityOptions() {
            return this.mActivityOptions;
        }
    }

    public ActivityNavigator(Context context) {
        this.mContext = context;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                this.mHostActivity = (Activity) context;
                return;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
    }

    public static void applyPopAnimationsToPendingTransition(Activity activity) {
        Intent intent = activity.getIntent();
        if (intent != null) {
            int popEnterAnim = intent.getIntExtra(EXTRA_POP_ENTER_ANIM, -1);
            int popExitAnim = intent.getIntExtra(EXTRA_POP_EXIT_ANIM, -1);
            if (!(popEnterAnim == -1 && popExitAnim == -1)) {
                int i = 0;
                int popEnterAnim2 = popEnterAnim != -1 ? popEnterAnim : 0;
                if (popExitAnim != -1) {
                    i = popExitAnim;
                }
                activity.overridePendingTransition(popEnterAnim2, i);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public final Context getContext() {
        return this.mContext;
    }

    public Destination createDestination() {
        return new Destination((Navigator<? extends Destination>) this);
    }

    public boolean popBackStack() {
        Activity activity = this.mHostActivity;
        if (activity == null) {
            return false;
        }
        activity.finish();
        return true;
    }

    public NavDestination navigate(Destination destination, Bundle args, NavOptions navOptions, androidx.navigation.Navigator.Extras navigatorExtras) {
        if (destination.getIntent() != null) {
            Intent intent = new Intent(destination.getIntent());
            if (args != null) {
                intent.putExtras(args);
                String dataPattern = destination.getDataPattern();
                if (!TextUtils.isEmpty(dataPattern)) {
                    StringBuffer data = new StringBuffer();
                    Matcher matcher = Pattern.compile("\\{(.+?)\\}").matcher(dataPattern);
                    while (matcher.find()) {
                        String argName = matcher.group(1);
                        if (args.containsKey(argName)) {
                            matcher.appendReplacement(data, BuildConfig.FLAVOR);
                            data.append(Uri.encode(args.get(argName).toString()));
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Could not find ");
                            sb.append(argName);
                            sb.append(" in ");
                            sb.append(args);
                            sb.append(" to fill data pattern ");
                            sb.append(dataPattern);
                            throw new IllegalArgumentException(sb.toString());
                        }
                    }
                    matcher.appendTail(data);
                    intent.setData(Uri.parse(data.toString()));
                }
            }
            if (navigatorExtras instanceof Extras) {
                intent.addFlags(((Extras) navigatorExtras).getFlags());
            }
            if (!(this.mContext instanceof Activity)) {
                intent.addFlags(268435456);
            }
            if (navOptions != null && navOptions.shouldLaunchSingleTop()) {
                intent.addFlags(536870912);
            }
            Activity activity = this.mHostActivity;
            String str = EXTRA_NAV_CURRENT;
            int exitAnim = 0;
            if (activity != null) {
                Intent hostIntent = activity.getIntent();
                if (hostIntent != null) {
                    int hostCurrentId = hostIntent.getIntExtra(str, 0);
                    if (hostCurrentId != 0) {
                        intent.putExtra(EXTRA_NAV_SOURCE, hostCurrentId);
                    }
                }
            }
            intent.putExtra(str, destination.getId());
            if (navOptions != null) {
                intent.putExtra(EXTRA_POP_ENTER_ANIM, navOptions.getPopEnterAnim());
                intent.putExtra(EXTRA_POP_EXIT_ANIM, navOptions.getPopExitAnim());
            }
            if (navigatorExtras instanceof Extras) {
                ActivityOptionsCompat activityOptions = ((Extras) navigatorExtras).getActivityOptions();
                if (activityOptions != null) {
                    ActivityCompat.startActivity(this.mContext, intent, activityOptions.toBundle());
                } else {
                    this.mContext.startActivity(intent);
                }
            } else {
                this.mContext.startActivity(intent);
            }
            if (!(navOptions == null || this.mHostActivity == null)) {
                int enterAnim = navOptions.getEnterAnim();
                int exitAnim2 = navOptions.getExitAnim();
                if (!(enterAnim == -1 && exitAnim2 == -1)) {
                    int enterAnim2 = enterAnim != -1 ? enterAnim : 0;
                    if (exitAnim2 != -1) {
                        exitAnim = exitAnim2;
                    }
                    this.mHostActivity.overridePendingTransition(enterAnim2, exitAnim);
                }
            }
            return null;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Destination ");
        sb2.append(destination.getId());
        sb2.append(" does not have an Intent set.");
        throw new IllegalStateException(sb2.toString());
    }
}
