package groovy.android.factory

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import groovy.android.AndroidBuilder

class FrameLayoutFactory extends LayoutFactory {
    // TODO: dividerDrawable
    FrameLayoutFactory() {
        super(FrameLayout, { AndroidBuilder builder, Context context, Object name, Object value, Map attributes ->
            def instance = builder.currentFactory.defaultNewInstance(builder, context, name, value, attributes)
            addContextProperties(builder.context, Gravity)
            return instance
        })
        supportsMarginLayoutParams = true
    }

    ViewGroup.LayoutParams createLayoutParams(int width, int height, def node, Map<String, Object> attributes) {
        def gravity = attributes.gravity
        if (gravity) {
            if (gravity instanceof Number)
                return new FrameLayout.LayoutParams(width, height, gravity.intValue())
            else if (gravity != null)
                throw new IllegalArgumentException("gravity has to be a numeric value")
        } else
            return new FrameLayout.LayoutParams(width, height)

    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        return super.onHandleNodeAttributes(builder, node, attributes)
    }

    @Override
    void handleLayoutParams(AndroidBuilder builder, View node, Map<String, Object> attributes) {
        super.handleLayoutParams(builder, node, attributes)
    }
}
