package groovy.android.factory

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.support.v4.view.PagerTitleStripIcs
import android.support.v7.internal.view.menu.ActionMenuItemView
import android.support.v7.internal.widget.CompatTextView
import android.text.InputType
import android.text.method.ArrowKeyMovementMethod
import android.text.method.BaseMovementMethod
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.ReplacementTransformationMethod
import android.text.method.ScrollingMovementMethod
import android.text.method.SingleLineTransformationMethod
import android.text.method.TransformationMethod
import android.text.util.Linkify
import android.view.ActionMode
import android.view.Gravity
import android.view.View
import android.widget.TextView
import groovy.android.AndroidBuilder

//TODO: setCompoundDrawables...

class TextViewFactory extends AndroidFactory {
    TextViewFactory() {
        super(TextView, true)
        { AndroidBuilder builder, Context context, Object name, Object value, Map attributes ->
            def instance = builder.currentFactory.defaultNewInstance(builder, context, name, value, attributes)
            addContextProperties(builder.context, Linkify)
            addContextProperties(builder.context, Gravity)
            addContextProperties(builder.context, Paint)
            addContextProperties(builder.context, InputType)
            addContextProperties(builder.context, Typeface)
            addContextEnums(builder.context, TextView.BufferType)
            builder.context.ARROW_KEY = ArrowKeyMovementMethod.instance
            builder.context.LINK = LinkMovementMethod.instance
            builder.context.SCROLLING = ScrollingMovementMethod.instance

            builder.context.HIDE_RETURNS = HideReturnsTransformationMethod.instance
            builder.context.PASSWORD = PasswordTransformationMethod.instance
            builder.context.SINGLE_LINE = SingleLineTransformationMethod.instance

            return instance
        }
        addListenerMethod('setCustomSelectionActionModeCallback', ActionMode.Callback)
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        if (node instanceof TextView) {
            def hint = attributes.remove('hint')
            if (hint != null)
                node.setHint(hint)

            def lineSpacing = attributes.remove('lineSpacing')
            if (lineSpacing instanceof Collection)
                node.setLineSpacing(*lineSpacing)
            else if (lineSpacing instanceof Number)
                node.setLineSpacing(lineSpacing.floatValue(), 1)
            else if (lineSpacing != null)
                throw new IllegalArgumentException("lineSpacing has to be of type $Collection.canonicalName or $Number.canonicalName")

            def shadowLayer = attributes.remove('shadowLayer')
            if (shadowLayer instanceof Collection && shadowLayer.size() == 4) {
                if (shadowLayer[3] instanceof String) {
                    // TODO: Colorhandling like the others
                } else
                    node.setShadowLayer(*shadowLayer)
            } else if (shadowLayer != null)
                throw new IllegalArgumentException("shadowLayer has to be a Collection in the format [radius, dx, dy, color]")

            def text = attributes.remove('text')
            if (text != null) {
                def textBufferType = attributes.remove('textBufferType')
                if (textBufferType instanceof TextView.BufferType)
                    node.setText(text, textBufferType)
                else
                    node.setText(text)
            }

            def textAppearance = attributes.remove('textAppearance')
            if (textAppearance != null)
                node.setTextAppearance(builder.context, textAppearance)

            def textLocale = attributes.remove('textLocale')
            if (textLocale instanceof Locale)
                node.setTextLocale(textLocale)
            else if (textLocale instanceof String)
                node.setTextLocale(toLocale(textLocale))
            else if (textLocale != null)
                throw new IllegalArgumentException("textLocale has to be of type $String.canonicalName or $Locale.canonicalName")

            def typeFace = attributes.remove('typeFace')
            def typeStyle = attributes.remove('typeStyle')
            if (typeFace instanceof Typeface) {
                if (typeStyle instanceof Integer)
                    node.setTypeface(typeFace, typeStyle)
                else if (typeStyle == null)
                    node.setTypeface(typeFace)
                else
                    throw new IllegalArgumentException("typeStyle has to be of type $Integer.canonicalName")
            } else if (typeFace == null) {
                if (typeStyle instanceof Integer)
                    node.setTypeface(null, typeStyle)
                else if (typeStyle == null)
                    node.setTypeface(Typeface.DEFAULT)
                else
                    throw new IllegalArgumentException("typeStyle has to be of type $Integer.canonicalName")
            } else
                throw new IllegalArgumentException("typeFace has to be of type $Typeface.canonicalName")
        }
    }

    Locale toLocale(String code) {
        def parts = code.split('_')
        if (parts.size() <= 3)
            return new Locale(*parts)
        throw new IllegalArgumentException('Only max. 3 parts in Locale-codes supported')
    }

}