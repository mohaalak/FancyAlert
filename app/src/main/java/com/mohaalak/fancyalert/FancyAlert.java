package com.mohaalak.fancyalert;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by alibilly on 7/6/17.
 */

public class FancyAlert {

    public static final  int ERROR = 0;
    public static final  int WARNING = 1;
    public static final  int SUCCESS = 2;

    //region Private Variables
    private OnDismissListener onDismissListener;
    private int duration = 0;
    private int height;
    private WeakReference<Activity> activity;
    Handler handler = new Handler();
    //endregion

    //region Private Views
    private View view;
    private TextView textView;
    private FancyButton button;
    private ImageView icon;
    private LinearLayout container;
    //endregion

    FancyAlert(Activity activity) {
        this.activity = new WeakReference<Activity>(activity);
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        view = layoutInflater.inflate(R.layout.alert, (ViewGroup) activity.getWindow().getDecorView(), false);

        textView = (TextView) view.findViewById(R.id.text);
        button = (FancyButton) view.findViewById(R.id.button);
        icon = (ImageView) view.findViewById(R.id.icon);
        container = (LinearLayout) view.findViewById(R.id.container);
        button.setVisibility(View.GONE);
    }

    private Context getContext() {
        return this.activity.get().getApplicationContext();
    }

    private void setButtonText(String buttonText) {
        button.setVisibility(View.VISIBLE);
        button.setText(buttonText);
    }

    private void setButtonClickListener(View.OnClickListener onClickListener) {
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(onClickListener);
    }

    private void setButton(String buttonText, View.OnClickListener onClickListener) {
        setButtonText(buttonText);
        setButtonClickListener(onClickListener);
    }

    private void setText(String message) {
        textView.setText(message);
    }

    private void setTextTypeFace(Typeface tf) {
        textView.setTypeface(tf);
    }

    private void setBackgroundColor(@ColorInt int color) {
        ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{color});

        ViewCompat.setBackgroundTintList(container, csl);
        ViewCompat.setBackgroundTintMode(container, PorterDuff.Mode.MULTIPLY);
    }

    private void setDuration(int duration) {
        this.duration = duration;
    }

    private void setIcon(Drawable drawable) {
        icon.setImageDrawable(drawable);
    }

    private void setOnDismissListener(OnDismissListener listener) {
        onDismissListener = listener;
    }

    private void setMode(int mode) {
        Drawable drawable = null;
        switch (mode) {
            case SUCCESS:
                setBackgroundColor(Color.parseColor("#27ae60"));
                drawable = ContextCompat.getDrawable(activity.get(), R.drawable.success);
                setIcon(drawable);
                break;
            case WARNING:
                setBackgroundColor(Color.parseColor("#f39c12"));
                drawable = ContextCompat.getDrawable(activity.get(), R.drawable.warning);
                setIcon(drawable);
                break;
            default:
                setBackgroundColor(Color.parseColor("#e74c3c"));
                drawable = ContextCompat.getDrawable(activity.get(), R.drawable.error);
                setIcon(drawable);
                break;

        }
    }
    public void show() {
        if (activity.get() == null) {
            destroy();
            return;
        }

        view.setTranslationY(0);
        ((ViewGroup) activity.get().getWindow().getDecorView()).addView(view);
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                double aspect = 16.0/9.0;
                height = view.getHeight();
                int containerHeight = (int) (container.getWidth() / aspect);
                if (containerHeight > container.getHeight()) {
                    RelativeLayout.LayoutParams lp =new RelativeLayout.LayoutParams(container.getWidth(), containerHeight);
                    container.setLayoutParams(lp);
                }

                view.setTranslationY(height * -1);
                view.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(800);
                return false;
            }
        });
        if (duration > 0) {
            handler.removeCallbacks(hideRunnable);
            handler.postDelayed(hideRunnable, duration);
        } else {
            view.setBackgroundColor(Color.parseColor("#00000036"));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hide();
                }
            });
        }
    }

    public void destroy() {
        onDismissListener = null;
        activity = null;
        textView = null;
        container = null;
        icon = null;
        button.setOnClickListener(null);
        button = null;
    }

    public void hide() {
        handler.removeCallbacks(hideRunnable);
        view.animate().translationY(-1 * height)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(800)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (activity.get() == null) {
                            destroy();
                            return;
                        }
                        if (onDismissListener != null) {
                            onDismissListener.dismiss();
                        }
                        ((ViewGroup) activity.get().getWindow().getDecorView()).removeView(view);
                        activity = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };


    public static class FancyAlertBuilder {


        private String message;
        private String buttonText;
        private Typeface typeface;
        private View.OnClickListener onClickListener;
        private OnDismissListener onDismissListener;
        private int mode = FancyAlert.ERROR;
        private Drawable icon;
        private @ColorInt int color = -1;
        private int duration = -1;

        public FancyAlertBuilder setButton(String text, View.OnClickListener onClickListener) {
            buttonText = text;
            this.onClickListener = onClickListener;
            return this;
        }

        public FancyAlertBuilder setButtonText(String text) {
            buttonText = text;
            return this;
        }

        public FancyAlertBuilder setButtonClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        public FancyAlertBuilder setIcon(Drawable drawable) {
            icon = drawable;
            return this;
        }

        public FancyAlertBuilder setBackgroundColor(@ColorInt int color) {
            this.color = color;
            return this;
        }

        public FancyAlertBuilder setMessage(String s) {
            this.message = s;
            return this;
        }

        public FancyAlertBuilder setDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public FancyAlertBuilder setTypeface(Typeface tf) {
            this.typeface = tf;
            return this;
        }

        public FancyAlertBuilder setOnDismissListener(OnDismissListener onDismissListener) {
            this.onDismissListener = onDismissListener;
            return this;
        }


        public FancyAlertBuilder setMode(int mode) {
            this.mode = mode;
            return this;
        }

        public FancyAlert build(Activity activity) {
            FancyAlert fancyAlert = new FancyAlert(activity);
            fancyAlert.setMode(mode);
            if (color != -1)
                fancyAlert.setBackgroundColor(color);
            if (message != null)
                fancyAlert.setText(message);
            if (onClickListener != null)
                fancyAlert.setButtonClickListener(onClickListener);
            if (buttonText != null)
                fancyAlert.setButtonText(buttonText);
            if (typeface != null)
                fancyAlert.setTextTypeFace(typeface);
            if (icon != null)
                fancyAlert.setIcon(icon);
            if (duration != -1)
                fancyAlert.setDuration(duration);
            if (onDismissListener != null)
                fancyAlert.setOnDismissListener(onDismissListener);
            return fancyAlert;
        }

    }

    public interface OnDismissListener {
        void dismiss();
    }
}

