package androidx.transition;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.ViewGroup;
import androidx.collection.ArrayMap;
import androidx.core.content.res.TypedArrayUtils;
import java.io.IOException;
import java.lang.reflect.Constructor;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class TransitionInflater {
    private static final ArrayMap<String, Constructor> CONSTRUCTORS = new ArrayMap<>();
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE = {Context.class, AttributeSet.class};
    private final Context mContext;

    private TransitionInflater(Context context) {
        this.mContext = context;
    }

    public static TransitionInflater from(Context context) {
        return new TransitionInflater(context);
    }

    public Transition inflateTransition(int resource) {
        XmlResourceParser parser = this.mContext.getResources().getXml(resource);
        try {
            Transition createTransitionFromXml = createTransitionFromXml(parser, Xml.asAttributeSet(parser), null);
            parser.close();
            return createTransitionFromXml;
        } catch (XmlPullParserException e) {
            throw new InflateException(e.getMessage(), e);
        } catch (IOException e2) {
            StringBuilder sb = new StringBuilder();
            sb.append(parser.getPositionDescription());
            sb.append(": ");
            sb.append(e2.getMessage());
            throw new InflateException(sb.toString(), e2);
        } catch (Throwable th) {
            parser.close();
            throw th;
        }
    }

    public TransitionManager inflateTransitionManager(int resource, ViewGroup sceneRoot) {
        XmlResourceParser parser = this.mContext.getResources().getXml(resource);
        try {
            TransitionManager createTransitionManagerFromXml = createTransitionManagerFromXml(parser, Xml.asAttributeSet(parser), sceneRoot);
            parser.close();
            return createTransitionManagerFromXml;
        } catch (XmlPullParserException e) {
            InflateException ex = new InflateException(e.getMessage());
            ex.initCause(e);
            throw ex;
        } catch (IOException e2) {
            StringBuilder sb = new StringBuilder();
            sb.append(parser.getPositionDescription());
            sb.append(": ");
            sb.append(e2.getMessage());
            InflateException ex2 = new InflateException(sb.toString());
            ex2.initCause(e2);
            throw ex2;
        } catch (Throwable th) {
            parser.close();
            throw th;
        }
    }

    private Transition createTransitionFromXml(XmlPullParser parser, AttributeSet attrs, Transition parent) throws XmlPullParserException, IOException {
        Transition transition = null;
        int depth = parser.getDepth();
        TransitionSet transitionSet = parent instanceof TransitionSet ? (TransitionSet) parent : null;
        while (true) {
            int next = parser.next();
            int type = next;
            if ((next != 3 || parser.getDepth() > depth) && type != 1) {
                if (type == 2) {
                    String name = parser.getName();
                    if ("fade".equals(name)) {
                        transition = new Fade(this.mContext, attrs);
                    } else if ("changeBounds".equals(name)) {
                        transition = new ChangeBounds(this.mContext, attrs);
                    } else if ("slide".equals(name)) {
                        transition = new Slide(this.mContext, attrs);
                    } else if ("explode".equals(name)) {
                        transition = new Explode(this.mContext, attrs);
                    } else if ("changeImageTransform".equals(name)) {
                        transition = new ChangeImageTransform(this.mContext, attrs);
                    } else if ("changeTransform".equals(name)) {
                        transition = new ChangeTransform(this.mContext, attrs);
                    } else if ("changeClipBounds".equals(name)) {
                        transition = new ChangeClipBounds(this.mContext, attrs);
                    } else if ("autoTransition".equals(name)) {
                        transition = new AutoTransition(this.mContext, attrs);
                    } else if ("changeScroll".equals(name)) {
                        transition = new ChangeScroll(this.mContext, attrs);
                    } else if ("transitionSet".equals(name)) {
                        transition = new TransitionSet(this.mContext, attrs);
                    } else {
                        String str = "transition";
                        if (str.equals(name)) {
                            transition = (Transition) createCustom(attrs, Transition.class, str);
                        } else if ("targets".equals(name)) {
                            getTargetIds(parser, attrs, parent);
                        } else if (!"arcMotion".equals(name)) {
                            String str2 = "pathMotion";
                            if (str2.equals(name)) {
                                if (parent != null) {
                                    parent.setPathMotion((PathMotion) createCustom(attrs, PathMotion.class, str2));
                                } else {
                                    throw new RuntimeException("Invalid use of pathMotion element");
                                }
                            } else if (!"patternPathMotion".equals(name)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Unknown scene name: ");
                                sb.append(parser.getName());
                                throw new RuntimeException(sb.toString());
                            } else if (parent != null) {
                                parent.setPathMotion(new PatternPathMotion(this.mContext, attrs));
                            } else {
                                throw new RuntimeException("Invalid use of patternPathMotion element");
                            }
                        } else if (parent != null) {
                            parent.setPathMotion(new ArcMotion(this.mContext, attrs));
                        } else {
                            throw new RuntimeException("Invalid use of arcMotion element");
                        }
                    }
                    if (transition == null) {
                        continue;
                    } else {
                        if (!parser.isEmptyElementTag()) {
                            createTransitionFromXml(parser, attrs, transition);
                        }
                        if (transitionSet != null) {
                            transitionSet.addTransition(transition);
                            transition = null;
                        } else if (parent != null) {
                            throw new InflateException("Could not add transition to another transition.");
                        }
                    }
                }
            }
        }
        return transition;
    }

    private Object createCustom(AttributeSet attrs, Class expectedType, String tag) {
        Object newInstance;
        String className = attrs.getAttributeValue(null, "class");
        if (className != null) {
            try {
                synchronized (CONSTRUCTORS) {
                    Constructor constructor = (Constructor) CONSTRUCTORS.get(className);
                    if (constructor == null) {
                        Class<?> c = this.mContext.getClassLoader().loadClass(className).asSubclass(expectedType);
                        if (c != null) {
                            constructor = c.getConstructor(CONSTRUCTOR_SIGNATURE);
                            constructor.setAccessible(true);
                            CONSTRUCTORS.put(className, constructor);
                        }
                    }
                    newInstance = constructor.newInstance(new Object[]{this.mContext, attrs});
                }
                return newInstance;
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Could not instantiate ");
                sb.append(expectedType);
                sb.append(" class ");
                sb.append(className);
                throw new InflateException(sb.toString(), e);
            }
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(tag);
            sb2.append(" tag must have a 'class' attribute");
            throw new InflateException(sb2.toString());
        }
    }

    private void getTargetIds(XmlPullParser parser, AttributeSet attrs, Transition transition) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if ((next == 3 && parser.getDepth() <= depth) || type == 1) {
                return;
            }
            if (type == 2) {
                if (parser.getName().equals("target")) {
                    TypedArray a = this.mContext.obtainStyledAttributes(attrs, Styleable.TRANSITION_TARGET);
                    int id = TypedArrayUtils.getNamedResourceId(a, parser, "targetId", 1, 0);
                    if (id != 0) {
                        transition.addTarget(id);
                    } else {
                        int namedResourceId = TypedArrayUtils.getNamedResourceId(a, parser, "excludeId", 2, 0);
                        int id2 = namedResourceId;
                        if (namedResourceId != 0) {
                            transition.excludeTarget(id2, true);
                        } else {
                            String namedString = TypedArrayUtils.getNamedString(a, parser, "targetName", 4);
                            String transitionName = namedString;
                            if (namedString != null) {
                                transition.addTarget(transitionName);
                            } else {
                                String namedString2 = TypedArrayUtils.getNamedString(a, parser, "excludeName", 5);
                                String transitionName2 = namedString2;
                                if (namedString2 != null) {
                                    transition.excludeTarget(transitionName2, true);
                                } else {
                                    String className = TypedArrayUtils.getNamedString(a, parser, "excludeClass", 3);
                                    if (className != null) {
                                        try {
                                            transition.excludeTarget(Class.forName(className), true);
                                        } catch (ClassNotFoundException e) {
                                            a.recycle();
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("Could not create ");
                                            sb.append(className);
                                            throw new RuntimeException(sb.toString(), e);
                                        }
                                    } else {
                                        String namedString3 = TypedArrayUtils.getNamedString(a, parser, "targetClass", 0);
                                        String className2 = namedString3;
                                        if (namedString3 != null) {
                                            transition.addTarget(Class.forName(className2));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    a.recycle();
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Unknown scene name: ");
                    sb2.append(parser.getName());
                    throw new RuntimeException(sb2.toString());
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0056, code lost:
        return r1;
     */
    private TransitionManager createTransitionManagerFromXml(XmlPullParser parser, AttributeSet attrs, ViewGroup sceneRoot) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();
        TransitionManager transitionManager = null;
        while (true) {
            int next = parser.next();
            int type = next;
            if ((next != 3 || parser.getDepth() > depth) && type != 1) {
                if (type == 2) {
                    String name = parser.getName();
                    if (name.equals("transitionManager")) {
                        transitionManager = new TransitionManager();
                    } else if (!name.equals("transition") || transitionManager == null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Unknown scene name: ");
                        sb.append(parser.getName());
                    } else {
                        loadTransition(attrs, parser, sceneRoot, transitionManager);
                    }
                }
            }
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Unknown scene name: ");
        sb2.append(parser.getName());
        throw new RuntimeException(sb2.toString());
    }

    private void loadTransition(AttributeSet attrs, XmlPullParser parser, ViewGroup sceneRoot, TransitionManager transitionManager) throws NotFoundException {
        TypedArray a = this.mContext.obtainStyledAttributes(attrs, Styleable.TRANSITION_MANAGER);
        int transitionId = TypedArrayUtils.getNamedResourceId(a, parser, "transition", 2, -1);
        int fromId = TypedArrayUtils.getNamedResourceId(a, parser, "fromScene", 0, -1);
        Scene toScene = null;
        Scene fromScene = fromId < 0 ? null : Scene.getSceneForLayout(sceneRoot, fromId, this.mContext);
        int toId = TypedArrayUtils.getNamedResourceId(a, parser, "toScene", 1, -1);
        if (toId >= 0) {
            toScene = Scene.getSceneForLayout(sceneRoot, toId, this.mContext);
        }
        if (transitionId >= 0) {
            Transition transition = inflateTransition(transitionId);
            if (transition != null) {
                if (toScene == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("No toScene for transition ID ");
                    sb.append(transitionId);
                    throw new RuntimeException(sb.toString());
                } else if (fromScene == null) {
                    transitionManager.setTransition(toScene, transition);
                } else {
                    transitionManager.setTransition(fromScene, toScene, transition);
                }
            }
        }
        a.recycle();
    }
}
