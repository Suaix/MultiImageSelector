# MultiImageSelector 图片选择组件
图片选择组件，模仿微信图片选择样式

## 效果展示
<img src='/screenshot/image_list.png' width=200 height=356><img src='/screenshot/image_dir_list.png' width=200 height=356><img src='/screenshot/selected_image_preview.png' width=200 height=356>
<img src='/screenshot/all_image_preivew.png' width=200 height=356><img src='/screenshot/select_result.png' width=200 height=356>
## 集成方式
将library作为项目的module导入自己的项目，或者将源码导入，暂时不提供gradle依赖导入
## 配置内容
通过MultiImageSelector.Builder来配置相关参数，通过调用`show(int requestCode)`来启动图片选择页面（即通过`startActivityForResult`），如下：
```
MultiImageSelector.with(MainActivity.this, MultiImageSelector.MULTI_MODE) //多图选择模式
                        .maxImageSize(9) //最大图片数量
                        .selectedImageList(selectedImageList) //已选择图片列表
                        .registerImageLoader(new GlideImageLoader()) //注册自己的图片加载器
                        .build()
                        .show(MULTI_IMAGE_CODE);
```
在启动页面的`onActivityResult`中使用以下方法获取图片选择结果
```
        if (requestCode == MULTI_IMAGE_CODE && resultCode == RESULT_OK) {
            // 多图选择模式返回图片
            Bundle extras = data.getExtras();
            selectedImageList = extras.getParcelableArrayList(MultiImageSelector.RESULT_MULTI_DATA);
            // do something
        } else if (requestCode == SINGLE_IMAGE_CODE && resultCode == RESULT_OK) {
            // 单图选择模式
            Bundle extras = data.getExtras();
            ImageInfo imageInfo = extras.getParcelable(MultiImageSelector.RESULT_SINGLE_DATA);
            // do something
        }
```
## 版本更新记录
### v1.0.0
1. 图片单选和多选
2. 图片预览和拖动排序
3. 图片预览手势缩放