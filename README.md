# MultiImageSelector 图片选择组件
图片选择组件，模仿微信图片选择样式

## 效果展示
加载所有图片，按照选择顺序标识图片
![all image list](/screenshot/image_list.png)
可选择切换不同文件夹下的图片
![image dir list](/screenshot/image_dir_list.png)
预览选中的图片
![preview selected images](/screenshot/selected_image_preview.png)
预览所有图片，底部为选中的图片列表，也可以在该预览页面进行图片的选择和取消选择操作
![preview all images](/screenshot/all_image_preivew.png)
选择结果返回展示
![selected result](/screenshot/select_result.png)
## 集成方式
将library作为项目的module导入自己的项目，或者将源码导入，暂时不提供gradle依赖导入
## 配置内容
只需要简单通过MultiImageSelector.Builder构建一个选择器即可启动图片选择页面，通过startActivityForResult来启动图片选择页面，相应地需要通过启动页面在onActivityResult里接收处理返回结果，示例如下：
MultiImageSelector.with(MultiImageSelector.MULTI_MODE)
                        .maxImageSize(9)
                        .selectedImageList(selectedImageList)
                        .build()
                        .startActivityForResult(MainActivity.this, 1);
## 版本更新记录
### v1.0.0
完成图片选择及预览和排序基础功能