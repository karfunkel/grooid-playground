package groovy.android

import android.content.Context
import android.util.TypedValue
import android.view.View
import groovy.android.factory.AndroidFactory
import groovy.android.factory.CollectionFactory
import groovy.android.factory.FrameLayoutFactory
import groovy.android.factory.GridLayoutFactory
import groovy.android.factory.LayoutFactory
import groovy.android.factory.LinearLayoutFactory
import groovy.android.factory.RelativeLayoutFactory
import groovy.android.factory.TableLayoutFactory
import groovy.android.factory.TableRowFactory
import groovy.android.factory.TextViewFactory
import org.codehaus.groovy.control.customizers.builder.PostCompletionFactory

import java.util.logging.Logger

class AndroidBuilder extends FactoryBuilderSupport {
    private static final Logger LOG = Logger.getLogger(AndroidBuilder.name)
    public static final String DELEGATE_PROPERTY_OBJECT_ID = "_delegateProperty:id"
    public static final String DEFAULT_DELEGATE_PROPERTY_OBJECT_ID = "id"
    public static final String ANDROID_CONTEXT = "__ANDROID_CONTEXT_"
    public static final String ANDROID_PARENT_CONTEXT = "__ANDROID_PARENT_CONTEXT_"

    protected Context androidContext

    AndroidBuilder(boolean init = true) {
        super(init)
        this[DELEGATE_PROPERTY_OBJECT_ID] = DEFAULT_DELEGATE_PROPERTY_OBJECT_ID
    }

    def registerNodes() {
        registerFactory 'noparent', new CollectionFactory()

        registerFactory 'textView', new TextViewFactory()
    }

    def registerAttributeDelegates() {
        //object id delegate, for propertyNotFound
        addAttributeDelegate(AndroidBuilder.&objectIDAttributeDelegate)

        // listener delegate
        addAttributeDelegate(AndroidFactory.&listenersAttributeDelegate)

        // layoutParams delegate
        addAttributeDelegate(LayoutFactory.&layoutParamsAttributeDelegate)

    }

    def registerLayouts() {
        registerFactory 'relativeLayout', new RelativeLayoutFactory()
        registerFactory 'linearLayout', new LinearLayoutFactory()
        registerFactory 'tableLayout', new TableLayoutFactory()
        registerFactory 'tableRow', new TableRowFactory()
        registerFactory 'frameLayout', new FrameLayoutFactory()
        registerFactory 'gridLayout', new GridLayoutFactory()

    }

    def registerUnitConversion() {
        registerExplicitMethod('dp', 'conversion', this.&convertFrom.curry(TypedValue.COMPLEX_UNIT_DIP))
        registerExplicitMethod('sp', 'conversion', this.&convertFrom.curry(TypedValue.COMPLEX_UNIT_SP))
        registerExplicitMethod('pt', 'conversion', this.&convertFrom.curry(TypedValue.COMPLEX_UNIT_PT))
        registerExplicitMethod('mm', 'conversion', this.&convertFrom.curry(TypedValue.COMPLEX_UNIT_MM))
        registerExplicitMethod('in', 'conversion', this.&convertFrom.curry(TypedValue.COMPLEX_UNIT_IN))
    }

    public Object build(Context androidContext, Closure c) {
        this.androidContext = androidContext
        c.setDelegate(this)
        return c.call()
    }

    static objectIDAttributeDelegate(def builder, def node, def attributes) {
        def idAttr = builder.getAt(DELEGATE_PROPERTY_OBJECT_ID) ?: DEFAULT_DELEGATE_PROPERTY_OBJECT_ID
        def theID = attributes.remove(idAttr)
        if (theID) {
            builder.setVariable(theID, node)
            if (node) {
                try {
                    if (!node.id) node.id = theID
                } catch (MissingPropertyException mpe) {
                    // ignore
                }
            }
        }
    }

    Object findPropertyInContextStack(String property) {
        Map<String, Object> context = getContext()
        while (context) {
            if (context.containsKey(property)) {
                return context.get(property)
            } else {
                context = context.get(PARENT_CONTEXT)
            }
        }
        if(property == ANDROID_CONTEXT)
            return androidContext
        return null
    }

    @Override
    protected Object postNodeCompletion(Object parent, Object node) {
        Object result = super.postNodeCompletion(parent, node)
        Object factory = getContextAttribute(CURRENT_FACTORY)
        if (factory instanceof AndroidFactory) {
            return factory.postCompleteNode(this, parent, result)
        }
        return result
    }

    int convertFrom(int unit, Number value) {
        Context context = findPropertyInContextStack(ANDROID_CONTEXT)
        return TypedValue.applyDimension(unit, value.floatValue(), context.resources.displayMetrics).intValue()
    }
}
