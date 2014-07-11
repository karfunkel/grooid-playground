package groovy.android.factory

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import groovy.android.AndroidBuilder

class TableRowFactory extends LayoutFactory {
    TableRowFactory() {
        super(TableRow, { AndroidBuilder builder, Context context, Object name, Object value, Map attributes ->
            def instance = builder.currentFactory.defaultNewInstance(builder, context, name, value, attributes)
            return instance
        })
        supportsMarginLayoutParams = true
    }

    ViewGroup.LayoutParams createLayoutParams(int width, int height, def node, Map<String, Object> attributes) {
        def initWeight = attributes.initWeight
        if (initWeight) {
            if (initWeight instanceof Number)
                return new TableRow.LayoutParams(width, height, initWeight.floatValue())
            else
                throw new IllegalArgumentException("initWeight has to be a numeric value")
        } else
            return new TableRow.LayoutParams(width, height)

    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        return super.onHandleNodeAttributes(builder, node, attributes)
    }

    @Override
    void handleLayoutParams(AndroidBuilder builder, View node, Map<String, Object> attributes) {
        super.handleLayoutParams(builder, node, attributes)

        def layoutParams = node.layoutParams
        if (layoutParams instanceof TableRow.LayoutParams) {
            setCheckedProperty(Number, attributes, 'column', layoutParams, {it.intValue()})
            setCheckedProperty(Number, attributes, 'span', layoutParams, {it.intValue()})
        }
    }
}
