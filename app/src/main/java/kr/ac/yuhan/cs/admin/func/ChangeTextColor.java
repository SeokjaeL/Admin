package kr.ac.yuhan.cs.admin.func;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChangeTextColor {
    public static void changeDarkTextColor(View view, int color) {
        if (view instanceof TextView) {
            // 만약 뷰가 TextView라면 색상 변경
            ((TextView) view).setTextColor(color);
        } else if (view instanceof ViewGroup) {
            // 만약 뷰가 ViewGroup이라면 해당 ViewGroup 안에 있는 모든 뷰에 대해 재귀적으로 색상 변경
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                changeDarkTextColor(viewGroup.getChildAt(i), color);
            }
        }
    }
    public static void changeLightTextColor(View view, int color) {
        if (view instanceof TextView) {
            // 만약 뷰가 TextView라면 색상 변경
            ((TextView) view).setTextColor(color);
        } else if (view instanceof ViewGroup) {
            // 만약 뷰가 ViewGroup이라면 해당 ViewGroup 안에 있는 모든 뷰에 대해 재귀적으로 색상 변경
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                changeLightTextColor(viewGroup.getChildAt(i), color);
            }
        }
    }
}
