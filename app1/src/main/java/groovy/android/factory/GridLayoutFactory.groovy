package groovy.android.factory

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import groovy.android.AndroidBuilder

class GridLayoutFactory extends LayoutFactory {
    // TODO: dividerDrawable
    GridLayoutFactory() {
        super(GridLayout, { AndroidBuilder builder, Context context, Object name, Object value, Map attributes ->
            def instance = builder.currentFactory.defaultNewInstance(builder, context, name, value, attributes)
            addContextProperties(builder.context, Gravity)
            return instance
        })
        supportsMarginLayoutParams = true
    }

    ViewGroup.LayoutParams createLayoutParams(int width, int height, def node, Map<String, Object> attributes) {
        def column = attributes.column ?: attributes.col
        def row = attributes.row
        def colSpan = attributes.columnSpan ?: attributes.colSpan ?: 1
        def rowSpan = attributes.rowSpan ?: 1
        def colAlignment = attributes.columnAlignment ?: attributes.colAlignment ?: GridLayout.UNDEFINED
        def rowAlignment = attributes.rowAlignment ?: GridLayout.UNDEFINED

        GridLayout.Spec colSpec = GridLayout.spec(GridLayout.UNDEFINED)
        GridLayout.Spec rowSpec = GridLayout.spec(GridLayout.UNDEFINED)

        if (column) {
            checkType(Number, column, 'column')
            checkType(Number, colSpan, 'columnSpan')
            checkType(GridLayout.Alignment, colAlignment, 'columnAlignment')
            colSpec = GridLayout.spec(column.intValue(), colSpan.intValue(), colAlignment)
        }
        if (row) {
            checkType(Number, row, 'row')
            checkType(Number, rowSpan, 'rowSpan')
            checkType(GridLayout.Alignment, rowAlignment, 'rowAlignment')
            colSpec = GridLayout.spec(row.intValue(), rowSpan.intValue(), rowAlignment)
        }
        return new GridLayout.LayoutParams(rowSpec, colSpec)
    }

    void checkType(Class type, def value, String property) {
        if (!type.isInstance(value))
            throw new IllegalArgumentException("$property has to be of type $type.canonicalName")
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        return super.onHandleNodeAttributes(builder, node, attributes)
    }

    @Override
    void handleLayoutParams(AndroidBuilder builder, View node, Map<String, Object> attributes) {
        super.handleLayoutParams(builder, node, attributes)
        def layoutParams = node.layoutParams
        if (layoutParams instanceof GridLayout.LayoutParams) {
            setCheckedProperty(Number, attributes, 'gravity', layoutParams, { it.intValue() })
        }
    }
}
