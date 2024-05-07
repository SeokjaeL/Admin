package kr.ac.yuhan.cs.admin.util;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

public class ChangeMode {
    public static void applyMainTheme(View rootView, int mode) {
        int textColor;
        int buttonColor;

        if (mode == 0) {
            // 다크 모드
            textColor = Color.WHITE;
            buttonColor = Color.BLACK;
        } else {
            // 라이트 모드
            textColor = Color.rgb(0, 105, 97);
            buttonColor = Color.rgb(0, 174, 142);
        }

        setTextColor(rootView, textColor);
        setButtonColor(rootView, buttonColor);
    }
    public static void applySubTheme(View rootView, int mode) {
        int textColor;
        int buttonColor;

        if (mode == 1) {
            // 다크 모드
            textColor = Color.WHITE;
            buttonColor = Color.BLACK;
        } else {
            // 라이트 모드
            textColor = Color.rgb(0, 105, 97);
            buttonColor = Color.rgb(0, 174, 142);
        }

        setTextColor(rootView, textColor);
        setButtonColor(rootView, buttonColor);
    }

    private static void setTextColor(View view, int color) {
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(color);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setTextColor(viewGroup.getChildAt(i), color);
            }
        }
    }

    private static void setButtonColor(View view, int backgroundColor) {
        if (view instanceof Button) {
            Button button = (Button) view;
            button.setBackgroundColor(backgroundColor);
            ColorStateList colorStateList = ColorStateList.valueOf(Color.WHITE);
            button.setTextColor(colorStateList); // 버튼의 글자색을 항상 흰색으로 설정
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setButtonColor(viewGroup.getChildAt(i), backgroundColor);
            }
        }
    }

    public static void setDarkShadowCardView(View view) {
        if (view instanceof NeumorphCardView) {
            NeumorphCardView nCardView = (NeumorphCardView) view;
            nCardView.setShadowColorLight(Color.GRAY);
            nCardView.setShadowColorDark(Color.BLACK);
        }

        if (view instanceof NeumorphImageView) {
            NeumorphImageView nImageView = (NeumorphImageView) view;
            nImageView.setShadowColorLight(Color.GRAY);
            nImageView.setShadowColorDark(Color.BLACK);
        }

        if (view instanceof NeumorphButton) {
            NeumorphButton nButtonView = (NeumorphButton) view;
            nButtonView.setShadowColorLight(Color.GRAY);
            nButtonView.setShadowColorDark(Color.BLACK);
        }
    }

    public static void setLightShadowCardView(View view) {
        if (view instanceof NeumorphCardView) {
            NeumorphCardView nCardView = (NeumorphCardView) view;
            nCardView.setShadowColorLight(Color.WHITE);
            nCardView.setShadowColorDark(Color.rgb(217, 217, 217));
        }

        if (view instanceof NeumorphImageView) {
            NeumorphImageView nImageView = (NeumorphImageView) view;
            nImageView.setShadowColorLight(Color.WHITE);
            nImageView.setShadowColorDark(Color.rgb(217, 217, 217));
        }

        if (view instanceof NeumorphButton) {
            NeumorphButton nButtonView = (NeumorphButton) view;
            nButtonView.setShadowColorLight(Color.WHITE);
            nButtonView.setShadowColorDark(Color.rgb(217, 217, 217));
        }
    }

    public static void setColorFilterDark(View view) {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            imageView.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }
        if (view instanceof NeumorphImageView) {
            NeumorphImageView nImageView = (NeumorphImageView) view;
            nImageView.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }
    }

    public static void setColorFilterLight(View view) {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            imageView.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }
        if (view instanceof NeumorphImageView) {
            NeumorphImageView nImageView = (NeumorphImageView) view;
            nImageView.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }
    }
}
