package com.micai.fox.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.micai.fox.R;
import com.micai.fox.activity.ModifyNiChengActivity;
import com.micai.fox.activity.MyReportActivity;
import com.micai.fox.activity.MyZhongChouActivity;
import com.micai.fox.activity.NotificationActivity;
import com.micai.fox.activity.SettingActivity;
import com.micai.fox.app.Config;
import com.micai.fox.util.Bimp;
import com.micai.fox.util.LogUtil;
import com.micai.fox.util.Tools;
import com.micai.fox.view.ChooseDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.micai.fox.R.id.iv_mine_head;
import static com.micai.fox.R.id.tv_mine_nicheng;

/**
 * Created by lq on 2017/12/19.
 */
/*我的*/
public class MineFragmnet extends Fragment {
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.rl)
    RelativeLayout rl;
    @Bind(R.id.tv_notify)
    TextView tvNotify;
    @Bind(iv_mine_head)
    CircleImageView ivMineHead;
    @Bind(R.id.tv_mine_nicheng)
    TextView tvMineNicheng;
    @Bind(R.id.iv_notify_point)
    ImageView ivNotifyPoint;
    @Bind(R.id.ll_mine_zhongchou)
    LinearLayout llMineZhongchou;
    @Bind(R.id.ll_mine_report)
    LinearLayout llMineReport;
    @Bind(R.id.ll_mine_set)
    LinearLayout llMineSet;
    @Bind(R.id.tv_back)
    TextView tvBack;


    private File file;
    private Uri photoUri = null;
    private static String fileName;
    private final int REQUEST_NICHENG = 3;//修改昵称
    private final String isimg = "file:///sdcard/temm.jpg";

    private final int REQUEST_CODE_TAKE_PHOTO = 0; // 拍照

    private final int REQUEST_CODE_CLIP_PHOTO = 1;  // 截取照片

    private final int REQUEST_CODE_SELECT_PHOTO = 2; // 从相册选择

    File photo = null;
    private Uri uri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);
        rl.setVisibility(View.VISIBLE);
        tvNotify.setVisibility(View.VISIBLE);
        tvTitle.setText("我的");
        ivNotifyPoint.setVisibility(View.VISIBLE);
        Drawable drawable = getResources().getDrawable(R.mipmap.message);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
        // param 左上右下
        tvNotify.setCompoundDrawables(null, null, drawable, null);

        if (savedInstanceState == null) {
            Log.e("YJL", "personactivity第一次创建");
        } else {
            photo = (File) savedInstanceState.getSerializable("PHOTOFILE");
            Log.e("YJL", "personactivity恢复");
        }
        if (null != Config.getInstance().getPhotoUrl() || !TextUtils.isEmpty(Config.getInstance().getPhotoUrl())) {
            Glide.with(this).load(Config.getInstance().getPhotoUrl()).asBitmap().placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).into(ivMineHead);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.tv_notify, iv_mine_head, R.id.tv_mine_nicheng, R.id.ll_mine_zhongchou, R.id.ll_mine_report, R.id.ll_mine_set})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_notify:
                //消息通知
                intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
                break;
            case iv_mine_head:
                //修改头像
                showDialog(getActivity());
                break;
            case R.id.tv_mine_nicheng:
                //修改昵称
                intent = new Intent(getActivity(), ModifyNiChengActivity.class);
                intent.putExtra("NAME", "" + tvMineNicheng.getText().toString().trim());
                startActivityForResult(intent, REQUEST_NICHENG);
                break;
            case R.id.ll_mine_zhongchou:
                //我的众筹
                intent = new Intent(getActivity(), MyZhongChouActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_mine_report:
                //我的报告
                intent = new Intent(getActivity(), MyReportActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_mine_set:
                //设置
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void showDialog(final Context context) {
        new ChooseDialog(context, new ChooseDialog.ChooseListener() {
            Intent intent;

            @Override
            public void choosePhoto() {
                selectphoto();
            }

            @Override
            public void chooseCamer() {
                //相机
                if (hasCarema() == false) {
                    return;
                }
                takePhoto();
            }
        }).show();
    }

    /* 选择照片 */
    private void selectphoto() {
        //  Toast.makeText(ChangePhotosActivity.this, "点击了选择相册", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO);
    }

    /* 相机权限 */
    private boolean hasCarema() {
        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            //    Toast.makeText(this, "请您开启相机权限", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "请您开启相机权限", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String imageName;
    private String fileDir;

    /* 拍照 */
    private void takePhoto() {
        Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(isimg));
        getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        startActivityForResult(getImageByCamera, REQUEST_CODE_TAKE_PHOTO);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_NICHENG:
                if (null != data)
                    tvMineNicheng.setText(data.getStringExtra("NAME"));
                break;
            case REQUEST_CODE_TAKE_PHOTO:
                onTakePhotoFinished(resultCode, data);
                break;
            case REQUEST_CODE_CLIP_PHOTO:
                if (null != data)
                    setPicToView(data);
                break;
            case REQUEST_CODE_SELECT_PHOTO:
                if (data != null) {
                    if (null != data.getData()) {           // 非空判断
                        onSelectPhotoFinish(resultCode, data);
                    }
                }
                break;
        }
    }

    /* 拍照图片结果 */
    private void onTakePhotoFinished(int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        } else if (resultCode != RESULT_OK) {
        } else {
            startActivityForResult(cropp(Uri.parse(isimg), 240, 300, 4, 5), REQUEST_CODE_CLIP_PHOTO);
        }
    }

    /* 选择照片结果 */
    private void onSelectPhotoFinish(int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        } else if (resultCode != RESULT_OK) {
        } else {
            if (data != null) {
                uri = data.getData();
               /* if (Build.VERSION.SDK_INT <17) {
                    uri = dealUri(ChangePhotosActivity.this, uri);//适配4.4系统
                    startActivityForResult(cropp(uri, 240, 300, 4, 5), REQUEST_CODE_CLIP_PHOTO);
                } else {
                    startActivityForResult(cropp(uri, 240, 300, 4, 5), REQUEST_CODE_CLIP_PHOTO);
                }*/
                startActivityForResult(cropp(uri, 240, 300, 4, 5), REQUEST_CODE_CLIP_PHOTO);
            }

        }
    }

    // 将进行剪裁后的图片显示到UI界面上
    private void setPicToView(Intent intent) {
        LogUtil.e("YJL", "" + photoUri);
        fileName = getActivity().getExternalFilesDir(null) + "/micaifox/camera";
        File folderName = new File(fileName);
        if (!folderName.exists()) {
            LogUtil.e("YJL", "" + folderName.exists());
            folderName.mkdirs();
        }
        fileName = folderName + File.separator + System.currentTimeMillis() + ".jpg";
        LogUtil.e("YJL", "" + fileName);
        file = new File(fileName);
        FileOutputStream fos = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            if (null != intent) {
                Bitmap photo = null;
                LogUtil.e("YJL", "uri===" + intent.getData());
                if (null != intent.getData()) {
//                    photoUri=intent.getData();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 2;
                    photo = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(photoUri), null, options);
                    fos = new FileOutputStream(file);
                    photo.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                    if (photo != null) {
                        saveBitmap(file, photo);
                        return;
                    }
                }
                if (null != intent.getParcelableExtra("data")) {
                    photo = intent.getParcelableExtra("data");
                    fos = new FileOutputStream(file);
                    photo.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                    fos.flush();
                    fos.close();
                    if (null != photo) {
                        saveBitmap(file, photo);
                        return;
                    }
                }
                if (null != intent.getExtras()) {
                    Bundle bundle = intent.getExtras();
                    if (null != bundle.getParcelable("data")) {
                        photo = bundle.getParcelable("data");
                        fos = new FileOutputStream(file);
                        photo.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                        fos.flush();
                        fos.close();
                        if (null != photo) {
                            saveBitmap(file, photo);
                            return;
                        }
                    }
                }
            }
           /* if (null!=bitmap){
                saveBitmap(file);
            }*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtil.e("YJL", "FileNotFoundException" + e.getMessage());

        } catch (IOException e) {
            LogUtil.e("YJL", "IOException-----" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /* 保存照片 */
    private void saveBitmap(File file, final Bitmap headPhoto) {
        //            photo = new File(new URI(isimg.toString()));
//            String path = photo.getAbsolutePath();
        LogUtil.e("YJL", "保存上传图片");
        String path = file.getAbsolutePath();
        LogUtil.e("YJL", "" + path);
        String name = path.substring(path.lastIndexOf("/") + 1);
        LogUtil.e("YJL", "" + name);
        ivMineHead.setImageBitmap(headPhoto);
/*        OkHttpUtils.post().addFile("mFile", name, file)
                .url(String.format(Url.WEB_UPLOAD, SharedPreferencesUtils.getValue("SESSIONID", getApplicationContext())))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e(TAG, "e ==  " + e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) throws Exception {
                if (HomeFunctionUtils.isNetworkAvailable(ChangePhotosActivity.this)) {
                    if (Tools.isGoodJson(response)) {
                        UploadResult uploadResult = new Gson().fromJson(response, UploadResult.class);
                        if (uploadResult.isExecResult()) {
                            LogUtils.e(TAG, response + "response");
                            //    Toast.makeText(ChangePhotosActivity.this, "上传头像成功", Toast.LENGTH_SHORT).show(); // 上传头像
                            ToolsC.CenterToast(ChangePhotosActivity.this, "上传头像成功");
                            String url = uploadResult.getExecDatas().getUrl();
                            LogUtils.e(TAG, "url :" + url);    // /website/userfiles/images/20170110/test.jpg}
                            downLoadBitmap(url, headPhoto);
                        }
                    }
                }
            }
        });*/
    }
    /**
     * 裁剪，例如：输出100*100大小的图片，宽高比例是1:1
     *
     * @param w       输出宽
     * @param h       输出高
     * @param aspectX 宽比例
     * @param aspectY 高比例
     */
    public Intent cropp(Uri uri, int w, int h, int aspectX, int aspectY) {
        if (w == 0 && h == 0) {
            w = h = 480;
        }
        if (aspectX == 0 && aspectY == 0) {
            aspectX = aspectY = 1;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 照片URL地址
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", w);
        intent.putExtra("outputY", h);
        // 输出路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 输出格式
        intent.putExtra("outputFormat", "JPEG");
        // 不启用人脸识别
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        return intent;
    }
}
