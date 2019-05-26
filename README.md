# MultiImageSelector 图片选择组件
图片选择组件，模仿微信图片选择样式

## 效果展示
<img src='/screenshot/image_list.png' width=200 height=356><img src='/screenshot/image_dir_list.png' width=200 height=356><img src='/screenshot/selected_image_preview.png' width=200 height=356>
<img src='/screenshot/all_image_preivew.png' width=200 height=356><img src='/screenshot/select_result.png' width=200 height=356>
## 集成方式
将library作为项目的module导入自己的项目，或者将源码导入，暂时不提供gradle依赖导入
## 配置内容
只需要简单通过MultiImageSelector.Builder构建一个选择器即可启动图片选择页面，通过startActivityForResult来启动图片选择页面，相应地需要通过启动页面在onActivityResult里接收处理返回结果，示例如下：
```
MultiImageSelector.with(MainActivity.this, MultiImageSelector.MULTI_MODE) //多图选择模式
                        .maxImageSize(9) //最大图片数量
                        .selectedImageList(selectedImageList) //已选择图片列表
                        .registerImageLoader(new GlideImageLoader()) //注册自己的图片加载器
                        .build()
                        .show(1);

```
## 版本更新记录
### v1.0.0
完成图片选择及预览和排序基础功能