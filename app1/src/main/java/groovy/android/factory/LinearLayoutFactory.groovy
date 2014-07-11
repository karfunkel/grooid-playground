package groovy.android.factory

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TableLayout
import groovy.android.AndroidBuilder

class LinearLayoutFactory extends LayoutFactory {
    // TODO: dividerDrawable
    LinearLayoutFactory() {
        super(LinearLayout, { AndroidBuilder builder, Context context, Object name, Object value, Map attributes ->
            def instance = builder.currentFactory.defaultNewInstance(builder, context, name, value, attributes)
            addContextProperties(builder.context, Gravity)
            return instance
        })
        supportsMarginLayoutParams = true
    }

    ViewGroup.LayoutParams createLayoutParams(int width, int height, def node, Map<String, Object> attributes) {
        def weight = attributes.weight
        if (weight) {
            if (weight instanceof Number)
                return new LinearLayout.LayoutParams(width, height, weight.floatValue())
            else
                throw new IllegalArgumentException("weight has to be a numeric value")
        } else
            return new LinearLayout.LayoutParams(width, height)

    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        return super.onHandleNodeAttributes(builder, node, attributes)
    }

    @Override
    void handleLayoutParams(AndroidBuilder builder, View node, Map<String, Object> attributes) {
        super.handleLayoutParams(builder, node, attributes)
        def layoutParams = node.layoutParams
        if (layoutParams instanceof LinearLayout.LayoutParams) {
            setCheckedProperty(Number, attributes, 'gravity', layoutParams, { it.intValue() })
        }
    }
}
