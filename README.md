# vocabulary2_android
对vocabulary_android改版，结构更合理。

2016.10.12
<p>增加"本地词汇分类上传"功能</p>
<p>对Category进行数据交换存在的bug调整</p>
<p>对NetworkRequestByPost不能获取返回数据的bug进行调整</p>
<p>实现"取消收藏"功能</p>
<p>对Repository进行调整，添加query方法，使查询功能更加通用</p>

2016.10.13
<p>增加"词汇上传"、"词汇保存到本地"功能</p>
<p>调整"取消收藏"功能，原来是直接把本地库数据favorite置为false,现改为直接从本地库删除</p>
<p>对LocalDataSourceImpl, VocabularyDbHelper调整，使数据库访问方法独立出来，减少代码冗余，使方法功能更单一</p>
<p>对Vocabulary表结构变更，增加upload字段</p>