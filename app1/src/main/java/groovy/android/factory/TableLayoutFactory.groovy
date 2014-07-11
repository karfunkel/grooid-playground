package groovy.android.factory

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import groovy.android.AndroidBuilder

class TableLayoutFactory extends LayoutFactory {
    TableLayoutFactory() {
        super(TableLayout, { AndroidBuilder builder, Context context, Object name, Object value, Map attributes ->
            def instance = builder.currentFactory.defaultNewInstance(builder, context, name, value, attributes)
            builder.context.ALL = 'ALL'
            builder.context.NONE = 'NONE'
            return instance
        })
        supportsMarginLayoutParams = true
    }

    ViewGroup.LayoutParams createLayoutParams(int width, int height, def node, Map<String, Object> attributes) {
        def initWeight = attributes.initWeight
        if (initWeight) {
            if (initWeight instanceof Number)
                return new TableLayout.LayoutParams(width, height, initWeight.floatValue())
            else
                throw new IllegalArgumentException("initWeight has to be a numeric value")
        } else
            return new TableLayout.LayoutParams(width, height)

    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        if (node instanceof TableLayout) {
            handleColumns('collapseColumns', 'setColumnCollapsed', node, attributes, null)
            handleColumns('shrinkColumns', 'setColumnShrinkable', node, attributes, 'shrinkAllColumns')
            handleColumns('stretchColumns', 'setColumnStretchable', node, attributes, 'stretchAllColumns')
        }
        return super.onHandleNodeAttributes(builder, node, attributes)
    }

    private void handleColumns(String property, String setter, def node, Map attributes, String allHandler) {
        def columns = attributes.remove(property)
        if (allHandler && columns instanceof String) {
            if (columns == 'ALL')
                node."$allHandler" = true
            else if (columns == 'NONE')
                node."$allHandler" = true
            else
                throw new IllegalArgumentException("$property has to be a numeric value, ALL or NONE")
        }
        columns.each { col ->
            if (col instanceof Number)
                node.invokeMethod(setter, [col.intValue(), true])
            else
                throw new IllegalArgumentException("$property values have to be numeric")
        }
    }

    @Override
    void handleLayoutParams(AndroidBuilder builder, View node, Map<String, Object> attributes) {
        super.handleLayoutParams(builder, node, attributes)
    }
}
