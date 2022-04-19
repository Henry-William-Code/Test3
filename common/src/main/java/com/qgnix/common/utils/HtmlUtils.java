package com.qgnix.common.utils;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AlignmentSpan;
import android.text.style.BulletSpan;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.xml.sax.XMLReader;

import java.util.LinkedList;

/**
 * html帮助类
 */
public class HtmlUtils {

    private HtmlUtils() {
    }

    /**
     * 设置Html文本数据
     *
     * @param txtStr 带有html格式的文本
     * @param tv     tv控件
     */
    public static void setTxtData(String txtStr, TextView tv) {
        if (null == tv) return;
        if (!TextUtils.isEmpty(txtStr)) {
            Html.ImageGetter imgGetter = new HtmlImgGetter(tv);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // flags
                // FROM_HTML_MODE_COMPACT：html块元素之间使用一个换行符分隔
                // FROM_HTML_MODE_LEGACY：html块元素之间使用两个换行符分隔
                tv.setText(Html.fromHtml(txtStr, Html.FROM_HTML_MODE_COMPACT, imgGetter, new HtmlTagHandler()));
            } else {
                tv.setText(Html.fromHtml(txtStr, imgGetter, new HtmlTagHandler()));
            }
            tv.setClickable(true);
            // 设置超链接可点击
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            // 设置可滚动
            tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        } else {
            tv.setText("");
        }
    }


    private static class HtmlTagHandler implements Html.TagHandler {

        private int mListItemCount = 0;
        private final LinkedList<String> mListParents = new LinkedList<>();

        private interface Code {
        }

        private interface Center {
        }

        @Override
        public void handleTag(final boolean opening, final String tag, Editable output, final XMLReader xmlReader) {
            boolean listFlag = "ul".equalsIgnoreCase(tag) || "ol".equalsIgnoreCase(tag)
                    || "dd".equalsIgnoreCase(tag);
            if (opening) {
                // opening tag
                if (listFlag) {
                    mListParents.add(tag);
                    mListItemCount = 0;
                } else if ("code".equalsIgnoreCase(tag)) {
                    start(output, new Object());
                } else if ("center".equalsIgnoreCase(tag)) {
                    start(output, new Object());
                }
            } else {
                // closing tag
                if (listFlag) {
                    mListParents.remove(tag);
                    mListItemCount = 0;
                } else if ("li".equalsIgnoreCase(tag)) {
                    handleListTag(output);
                } else if ("code".equalsIgnoreCase(tag)) {
                    end(output, HtmlTagHandler.Code.class, new TypefaceSpan("monospace"), false);
                } else if ("center".equalsIgnoreCase(tag)) {
                    end(output, HtmlTagHandler.Center.class,
                            new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), true);
                }
            }
            //解析img标签
            if ("img".equalsIgnoreCase(tag.toLowerCase())) {
                int len = output.length();
                ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
                String imgUrl = images[0].getSource();
                //添加点击事件
                output.setSpan(new ImageClickSpan(imgUrl), len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }

        /**
         * Mark the opening tag by using private classes
         *
         * @param output
         * @param mark
         */
        private void start(android.text.Editable output, Object mark) {
            int len = output.length();
            output.setSpan(mark, len, len, Spanned.SPAN_MARK_MARK);
        }

        private void end(android.text.Editable output, Class<?> kind, Object repl, boolean paragraphStyle) {
            Object obj = getLast(output, kind);
            // start of the tag
            int where = output.getSpanStart(obj);
            // end of the tag
            int len = output.length();

            output.removeSpan(obj);

            if (where != len) {
                // paragraph styles like AlignmentSpan need to end with a new line!
                if (paragraphStyle) {
                    output.append("\n");
                    len++;
                }

                output.setSpan(repl, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        /**
         * Get last marked position of a specific tag kind (private class)
         *
         * @param text
         * @param kind
         * @return
         */
        private Object getLast(android.text.Editable text, Class<?> kind) {
            Object[] objs = text.getSpans(0, text.length(), kind);
            if (objs.length == 0) {
                return null;
            } else {
                for (int i = objs.length; i > 0; i--) {
                    if (text.getSpanFlags(objs[i - 1]) == Spanned.SPAN_MARK_MARK) {
                        return objs[i - 1];
                    }
                }
                return null;
            }
        }

        private void handleListTag(android.text.Editable output) {
            if ("ul".equalsIgnoreCase(mListParents.getLast())) {
                output.append("\n");
                String[] split = output.toString().split("\n");

                int lastIndex = split.length - 1;
                int start = output.length() - split[lastIndex].length() - 1;
                output.setSpan(new BulletSpan(15 * mListParents.size()), start, output.length(), 0);
            } else if ("ol".equalsIgnoreCase(mListParents.getLast())) {
                mListItemCount++;
                output.append("\n");
                String[] split = output.toString().split("\n");

                int lastIndex = split.length - 1;
                int start = output.length() - split[lastIndex].length() - 1;
                output.insert(start, mListItemCount + ". ");
                output.setSpan(new LeadingMarginSpan.Standard(15 * mListParents.size()), start,
                        output.length(), 0);
            }
        }


        /**
         * 设置img标签的点击事件
         */
        static class ImageClickSpan extends ClickableSpan {

            private String url;

            public ImageClickSpan(String url) {
                this.url = url;
            }

            /**
             * Performs the click action associated with this span.
             *
             * @param widget
             */
            @Override
            public void onClick(View widget) {
                Log.e("===点击了图片==url=", url);
            }
        }
    }

}
