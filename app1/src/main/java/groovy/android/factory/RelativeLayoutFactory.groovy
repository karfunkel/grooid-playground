package groovy.android.factory

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import groovy.android.AndroidBuilder

class RelativeLayoutFactory extends LayoutFactory {
    RelativeLayoutFactory() {
        super(RelativeLayout, { AndroidBuilder builder, Context context, Object name, Object value, Map attributes ->
            def instance = defaultNewInstance(builder, context, name, value, attributes)
            addContextProperties(builder.context, RelativeLayout.LayoutParams)
            addContextProperties(builder.context, Gravity)
            return instance
        })
        supportsMarginLayoutParams = true
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        asId(attributes, 'ignoreGravity')
        return super.onHandleNodeAttributes(builder, node, attributes)
    }

    ViewGroup.LayoutParams createLayoutParams(int width, int height, def node, Map<String, Object> attributes) {
        return new RelativeLayout.LayoutParams(width, height)
    }

    @Override
    void handleLayoutParams(AndroidBuilder builder, View node, Map<String, Object> attributes) {
        super.handleLayoutParams(builder, node, attributes)

        def layoutParams = node.layoutParams
        if (layoutParams instanceof RelativeLayout.LayoutParams) {
            addRule(attributes, 'layoutAbove', RelativeLayout.ABOVE, true, layoutParams)
            addRule(attributes, 'layoutBelow', RelativeLayout.BELOW, true, layoutParams)
            addRule(attributes, 'layoutAlignTop', RelativeLayout.ALIGN_RIGHT, true, layoutParams)
            addRule(attributes, 'layoutAlignRight', RelativeLayout.ALIGN_START, true, layoutParams)
            addRule(attributes, 'layoutAlignBottom', RelativeLayout.ALIGN_BOTTOM, true, layoutParams)
            addRule(attributes, 'layoutAlignLeft', RelativeLayout.ALIGN_LEFT, true, layoutParams)
            addRule(attributes, 'layoutAlignStart', RelativeLayout.ALIGN_START, true, layoutParams)
            addRule(attributes, 'layoutAlignEnd', RelativeLayout.ALIGN_END, true, layoutParams)
            addRule(attributes, 'layoutAlignParentTop', RelativeLayout.ALIGN_PARENT_TOP, false, layoutParams)
            addRule(attributes, 'layoutAlignParentRight', RelativeLayout.ALIGN_PARENT_RIGHT, false, layoutParams)
            addRule(attributes, 'layoutAlignParentBottom', RelativeLayout.ALIGN_PARENT_BOTTOM, false, layoutParams)
            addRule(attributes, 'layoutAlignParentLeft', RelativeLayout.ALIGN_PARENT_LEFT, false, layoutParams)
            addRule(attributes, 'layoutAlignParentStart', RelativeLayout.ALIGN_PARENT_START, false, layoutParams)
            addRule(attributes, 'layoutAlignParentEnd', RelativeLayout.ALIGN_PARENT_END, false, layoutParams)
            addRule(attributes, 'layoutCenterHorizontal', RelativeLayout.CENTER_HORIZONTAL, false, layoutParams)
            addRule(attributes, 'layoutCenterInParent', RelativeLayout.CENTER_IN_PARENT, false, layoutParams)
            addRule(attributes, 'layoutCenterVertical', RelativeLayout.CENTER_VERTICAL, false, layoutParams)
            addRule(attributes, 'layoutToEndOf', RelativeLayout.END_OF, true, layoutParams)
            addRule(attributes, 'layoutToLeftOf', RelativeLayout.LEFT_OF, true, layoutParams)
            addRule(attributes, 'layoutToRightOf', RelativeLayout.RIGHT_OF, true, layoutParams)
            addRule(attributes, 'layoutToStartOf', RelativeLayout.START_OF, true, layoutParams)
        }
    }

    void addRule(Map<String, Object> attributes, String property, int rule, boolean view, RelativeLayout.LayoutParams target) {
        def value = attributes.remove(property)
        if (view) {
            if (value instanceof View)
                target.addRule(rule, view.id)
            else if (value instanceof Integer)
                target.addRule(rule, value)
            else if (value != null)
                throw new IllegalArgumentException("$property must be a View or a View-reference")
        } else {
            if (value instanceof Boolean) {
                if (value)
                    target.addRule(rule, RelativeLayout.TRUE)
                else
                    target.addRule(rule, 0)
            } else if (value != null)
                throw new IllegalArgumentException("$property must be of type boolean")
        }
    }
}
