package com.jmp.basicstructureproject.presentation.web;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jmp.basicstructureproject.BaseActivity;
import com.jmp.basicstructureproject.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import kotlin.jvm.functions.Function0;

public class HybridWebViewChromeClient extends WebChromeClient {

    private View mCustomView;
    private CustomViewCallback mCustomViewCallback;
    private int mOriginalOrientation;
    private FrameLayout mFullscreenContainer;
    private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    private Context mContext;

    public HybridWebViewChromeClient(){}

    public ValueCallback<Uri[]> filePathCallbackLollipop;
    public final static int FILECHOOSER_LOLLIPOP_REQ_CODE = 2002;

    private Uri mCameraImageUri = null;

    private String mActionCmd = null;

    private OnProgressChanged mOnProgressChanged = null;

    /**
     * 한글만 찾아서 url 를 인코딩 해주는 함수
     * @param url
     * @return 한글만 URLEncode 된 url
     */
    public static String getChangeHangulToUrlEncode(String url){
        char[] txtChar = url.toCharArray();
        for (int j = 0; j < txtChar.length; j++) {
            if (txtChar[j] >= '\uAC00' && txtChar[j] <= '\uD7A3') {
                String targetText = String.valueOf(txtChar[j]);
                try {
                    url = url.replace(targetText, URLEncoder.encode(targetText, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return url;
    }

    public HybridWebViewChromeClient(Context context, String actionCmd, OnProgressChanged onProgressChanged){
        mContext = context;
        mActionCmd = actionCmd;
        mOnProgressChanged = onProgressChanged;

        if(mContext instanceof BaseActivity){
            BaseActivity activity = ((BaseActivity)mContext);

            activity.addOnActivityResultListener((requestCode, resultCode, data) -> {
                switch (requestCode){

                    case FILECHOOSER_LOLLIPOP_REQ_CODE:

                        if (resultCode == Activity.RESULT_OK) {
                            if (filePathCallbackLollipop == null) return;

                            if(data == null){
                                data = new Intent();
                                if (data.getData() == null)
                                    data.setData(mCameraImageUri);

                                filePathCallbackLollipop.onReceiveValue(FileChooserParams.parseResult(resultCode, data));
                            }else if(data.getClipData() != null){
                                int count = data.getClipData().getItemCount();
                                Uri[] uris = new Uri[count];
                                for(int i = 0 ; i < count ; i++){
                                    uris[i] = data.getClipData().getItemAt(i).getUri();
                                }

                                filePathCallbackLollipop.onReceiveValue(uris);
                            }else if (data.getData() != null){
                                filePathCallbackLollipop.onReceiveValue(new Uri[]{data.getData()});
                            }
                            filePathCallbackLollipop = null;

                        }else{
                            if (filePathCallbackLollipop != null){   //  resultCode에 RESULT_OK가 들어오지 않으면 null 처리하지 한다.(이렇게 하지 않으면 다음부터 input 태그를 클릭해도 반응하지 않음)
                                filePathCallbackLollipop.onReceiveValue(null);
                                filePathCallbackLollipop = null;
                            }
                        }
                        break;
                    default:

                        break;
                }
            });
        }
    }

    private boolean isYoutubeUrlCheck(String url) {
        return url.contains("youtu.be") || url.contains("youtube");
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

        Message href = view.getHandler().obtainMessage();
        view.requestFocusNodeHref(href);

        String url = href.getData().getString("url");
        if(url != null && isYoutubeUrlCheck(url)){
            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        }

        // Dialog Create Code
        WebView newWebView = new WebView(view.getContext());
        WebSettings webSettings = newWebView.getSettings();

        //웹뷰의 설정을 다음과 같이 맞춰주시기 바랍니다.
        webSettings.setJavaScriptEnabled(true);	//필수설정(true)
        webSettings.setDomStorageEnabled(true);		//필수설정(true)
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);	//필수설정(true)

        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);

        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(newWebView);

        HybridWebViewExtensionsKt.downloadFileListener(newWebView);

        ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
        dialog.show();
        newWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onCloseWindow(WebView window) {
                dialog.dismiss();
            }
        });

        HybridWebViewClient hybridWebViewClient = new HybridWebViewClient();
        newWebView.setWebViewClient(hybridWebViewClient);

        ((WebView.WebViewTransport)resultMsg.obj).setWebView(newWebView);
        resultMsg.sendToTarget();
        return true;

    }

    // For Android 5.0+ 카메라 - input type="file" 태그를 선택했을 때 반응
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(
            WebView webView, ValueCallback<Uri[]> filePathCallback,
            FileChooserParams fileChooserParams) {

        final int REQUEST_CODE_EXTERNAL_STORAGE_READ = 2004;

        // Callback 초기화 (중요!)
        if (filePathCallbackLollipop != null) {
            filePathCallbackLollipop.onReceiveValue(null);
            filePathCallbackLollipop = null;
        }
        filePathCallbackLollipop = filePathCallback;
        boolean isCapture = fileChooserParams.isCaptureEnabled();

        if(mContext instanceof BaseActivity) {
            BaseActivity activity = ((BaseActivity) mContext);

            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                mCameraImageUri = activity.runCamera(isCapture, FILECHOOSER_LOLLIPOP_REQ_CODE);
            }else{
                activity.addPermissionCallback(REQUEST_CODE_EXTERNAL_STORAGE_READ, (requestCode, permissions1, grantResults) -> {
                    if(requestCode == REQUEST_CODE_EXTERNAL_STORAGE_READ){
                        if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                            mCameraImageUri = activity.runCamera(isCapture, FILECHOOSER_LOLLIPOP_REQ_CODE);
                        }else{
                            // TODO Dialog 만들기
//                            FragmentManagerExKt.showChoiceDialog(activity.getSupportFragmentManager(),
//                                    "", activity.getString(R.string.failed_permission_move_system_settings),
//                                    "아니요", "예", (Function0) () -> null, () -> {
//                                        moveSystemSettings();
//                                        return null;
//                                    }
//                            );
                        }
                    }
                });

                String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(
                        activity,
                        permissions,
                        REQUEST_CODE_EXTERNAL_STORAGE_READ
                );

                if (filePathCallbackLollipop != null) {
                    filePathCallbackLollipop.onReceiveValue(null);
                    filePathCallbackLollipop = null;
                }
            }
        }
        return true;
    }

    private void moveSystemSettings(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ mContext.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }

            mOriginalOrientation = ((Activity)mContext).getRequestedOrientation();
            FrameLayout decor = (FrameLayout) ((Activity)mContext).getWindow().getDecorView();
            mFullscreenContainer = new FullscreenHolder(mContext);
            mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
            decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
            mCustomView = view;
            setFullscreen(true);
            mCustomViewCallback = callback;
//          mActivity.setRequestedOrientation(requestedOrientation);

        }

        super.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        if (mCustomView == null) {
            return;
        }

        setFullscreen(false);
        FrameLayout decor = (FrameLayout) ((Activity)mContext).getWindow().getDecorView();
        decor.removeView(mFullscreenContainer);
        mFullscreenContainer = null;
        mCustomView = null;
        mCustomViewCallback.onCustomViewHidden();
        ((Activity)mContext).setRequestedOrientation(mOriginalOrientation);

    }

    private void setFullscreen(boolean enabled) {

        Window win = ((Activity)mContext).getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (enabled) {
            winParams.flags |= bits;
            if (mCustomView != null) {
                mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE|
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        } else {
            winParams.flags &= ~bits;
            if (mCustomView != null) {
                mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);

        if(mOnProgressChanged != null){
            mOnProgressChanged.onProgressChanged(view, newProgress);
        }

        Log.d("jmp_web","url : "+view.getUrl()+"  , newProgress : "+newProgress);

        if(newProgress >= 100){

            if(mActionCmd != null){
                view.loadUrl(mActionCmd);
                mActionCmd = null;
            }
        }
    }

    private static class FullscreenHolder extends FrameLayout {
        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    public interface OnProgressChanged{
        void onProgressChanged(WebView view, int newProgress);
    }

}
