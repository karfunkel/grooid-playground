package groovy.android.factory

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.LayoutAnimationController
import groovy.android.AndroidBuilder

class LayoutFactory extends AndroidFactory {
    def contextProps
    public static final String CURRENT_LAYOUT = "__CURRENT_LAYOUT_"
    public static final String CURRENT_LAYOUT_FACTORY = "__CURRENT_LAYOUT_FACTORY_"

    boolean supportsMarginLayoutParams = false

    LayoutFactory(Class beanClass, Closure newInstance = null) {
        this(beanClass, false, newInstance)
    }

    LayoutFactory(Class cls, boolean leaf, Closure newInstance = null) {
        super(cls, leaf, newInstance)
        if (!ViewGroup.isAssignableFrom(cls))
            throw new IllegalArgumentException("Layouts have to be a child of $ViewGroup.canonicalName")
    }

    @Override
    def defaultNewInstance(AndroidBuilder builder, Context context, Object name, Object value, Map attributes) {
        def instance = super.defaultNewInstance(builder, context, name, value, attributes)
        addContextProperties(builder.context, ViewGroup.LayoutParams)
        builder.context.put(CURRENT_LAYOUT_FACTORY, this)
        builder.context.put(CURRENT_LAYOUT, instance)
        return instance
    }

    static layoutParamsAttributeDelegate(AndroidBuilder builder, def node, Map<String, Object> attributes) {
        if (node instanceof View) {
            def width = attributes.remove('width')
            def height = attributes.remove('height')
            def size = attributes.remove('size')
            if (size instanceof List && size.size() == 2) {
                width = size[0]
                height = size[1]
            }

            LayoutFactory factory = builder.findPropertyInContextStack(CURRENT_LAYOUT_FACTORY)

            ViewGroup.LayoutParams params = node.layoutParams
            if (params) {
                if (width != null)
                    params.width = (int) width
                if (height != null)
                    params.height = (int) height
            } else {
                node.layoutParams = factory.createLayoutParams(width ?: ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, node, attributes)
            }

            factory.handleLayoutParams(builder, node, attributes)
        }
    }

    ViewGroup.LayoutParams createLayoutParams(int width, int height, def node, Map<String, Object> attributes) {
        return new ViewGroup.LayoutParams(width, height)
    }

    void handleLayoutParams(AndroidBuilder builder, View node, Map<String, Object> attributes) {
        def layoutParams = node.layoutParams
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            setCheckedProperty(Number, attributes, 'layoutDirection', layoutParams, { it.intValue() })
            setCheckedProperty(LayoutAnimationController.AnimationParameters, attributes, 'layoutAnimationParameters', layoutParams)

            List margin = attributes.remove('margin') ?: [attributes.remove('marginStart') ?: attributes.remove('marginLeft') ?: 0, attributes.remove('marginTop') ?: 0, attributes.remove('marginEnd') ?: attributes.remove('marginRight') ?: 0, attributes.remove('marginBottom') ?: 0]
            if (margin?.size() > 0) {
                if (margin.size() == 1)
                    margin = margin * 4
                else if (margin.size() == 2)
                    margin = margin * 2
                else if (margin.size() == 3)
                    margin << margin[1]
                if (node.layoutDirection == View.LAYOUT_DIRECTION_RTL)
                    margin = [margin[2], margin[1], margin[0], margin[3]]
                layoutParams.setMargins(*margin)
            }
        }
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        ViewGroup instance = node
        if (supportsMarginLayoutParams) {
            instance.getLayoutParams()
        }
        return super.onHandleNodeAttributes(builder, node, attributes)
    }

    @Override
    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (parent instanceof ViewGroup && child instanceof View) {
            parent.addView(child)
        }
    }
}
