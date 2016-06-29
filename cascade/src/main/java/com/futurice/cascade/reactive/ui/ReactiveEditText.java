/*
This file is part of Reactive Cascade which is released under The MIT License.
See license.txt or http://reactivecascade.com for details.
This is open source for the common good. Please contribute improvements by pull request or contact paulirotta@gmail.com
*/
package com.futurice.cascade.reactive.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.annotation.UiThread;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.futurice.cascade.functional.ImmutableValue;
import com.futurice.cascade.i.IAsyncOrigin;
import com.futurice.cascade.i.INamed;
import com.futurice.cascade.i.NotCallOrigin;
import com.futurice.cascade.reactive.ReactiveValue;
import com.futurice.cascade.util.AssertUtil;
import com.futurice.cascade.util.RCLog;

import static com.futurice.cascade.Async.UI;

/**
 * An {@link EditText} which can be manipulated and which in turn manipulates a supporting
 * {@link ReactiveValue<String>} which reflects the current on-screen from
 * <p>
 * Created by Paul Houghton on 12-03-2015.
 */
@NotCallOrigin
public class ReactiveEditText extends EditText implements INamed, IAsyncOrigin {
    @NonNull
    private final ImmutableValue<String> mOrigin = isInEditMode() ? RCLog.DEFAULT_ORIGIN : RCLog.originAsync();
    public volatile ReactiveValue<String> mReactiveValue;
    private TextWatcher mTextWatcher;

//TODO Constructors which support a string validator (example: trim whitespace or fix capitalization as you type)

    public ReactiveEditText(@NonNull Context context) {
        super(context);

        mReactiveValue = new ReactiveValue<>(getName(), getText().toString());
    }

    public ReactiveEditText(
            @NonNull Context context,
            @NonNull ReactiveValue<String> reactiveValue) {
        super(context);

        this.mReactiveValue = reactiveValue;
    }

    public ReactiveEditText(
            @NonNull Context context,
            @NonNull AttributeSet attrs) {
        super(context, attrs);

        mReactiveValue = new ReactiveValue<>(getName(), getText().toString());
    }

    public ReactiveEditText(
            @NonNull Context context,
            @NonNull AttributeSet attrs,
            @NonNull ReactiveValue<String> reactiveValue) {
        super(context, attrs);

        this.mReactiveValue = reactiveValue;
    }

    public ReactiveEditText(
            @NonNull Context context,
            @NonNull AttributeSet attrs,
            @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mReactiveValue = new ReactiveValue<>(getName(), getText().toString());
    }

    public ReactiveEditText(
            @NonNull Context context,
            @NonNull AttributeSet attrs,
            @AttrRes int defStyleAttr,
            @NonNull ReactiveValue<String> reactiveValue) {
        super(context, attrs, defStyleAttr);

        this.mReactiveValue = reactiveValue;
    }

    @TargetApi(21)
    public ReactiveEditText(
            @NonNull String name,
            @NonNull Context context,
            @NonNull AttributeSet attrs,
            @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mReactiveValue = new ReactiveValue<>(getName(), getText().toString());
    }

    @TargetApi(21)
    public ReactiveEditText(
            @NonNull String name,
            @NonNull Context context,
            @NonNull AttributeSet attrs,
            @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes,
            @NonNull ReactiveValue<String> reactiveValue) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.mReactiveValue = reactiveValue;
    }

    /**
     * Set the reactive view model associated with this view
     *
     * @param reactiveValue
     * @param fire
     * @return
     */
    @NonNull
    public ReactiveValue<String> setReactiveValue(@NonNull ReactiveValue<String> reactiveValue,
                                                  final boolean fire) {
        UI.run(() -> {
            this.mReactiveValue = reactiveValue;
            if (fire) {
                reactiveValue.fire();
            }
        });

        return reactiveValue;
    }

    @Override // INamed
    @NonNull
    public String getName() {
        return "ReactiveEditText" + getId();
    }

    @Override // View
    @UiThread
    public void onAttachedToWindow() {
        final String currentText = mReactiveValue.get();
        setText(currentText);

        RCLog.v(this, "onAttachedToWindow " + getName() + ", from=" + currentText);

        mReactiveValue.subscribe(UI, this::setText);

        if (mTextWatcher == null) {
            mTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(@NonNull final CharSequence s,
                                              final int start,
                                              final int count,
                                              final int after) {
                }

                @Override
                public void onTextChanged(@NonNull final CharSequence s,
                                          final int start,
                                          final int before,
                                          final int count) {
                }

                @Override
                public void afterTextChanged(@NonNull final Editable s) {
                    setSelection(s.length());
                    mReactiveValue.set(s.toString());
                }
            };
            this.addTextChangedListener(mTextWatcher);
        }

        super.onAttachedToWindow();
    }

    @Override // View
    @UiThread
    public void onDetachedFromWindow() {
        AssertUtil.assertNotNull(mOrigin);
        RCLog.v(this, "onDetachedFromWindow " + getName() + ", current from=" + getText());
        mReactiveValue.unsubscribeAll("onDetachedFromWindow");

        super.onDetachedFromWindow();
    }

    @NonNull
    @Override
    public ImmutableValue<String> getOrigin() {
        return mOrigin;
    }
}