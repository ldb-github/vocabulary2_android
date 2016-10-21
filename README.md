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

2016.10.14
<p>增加"本地未上传词汇上传"、"删除未上传词汇"功能</p>
<p>修改item_vocabulary.xml，使其更符合material design规范</p>
<p>对Vocabulary表结构变更，增加cid_local字段</p>

2016.10.16
<p>增加"本地词汇分类上传后自动上传此分类未上传的词汇"功能</p>

2016.10.21
<p>增加"收藏"功能之下载词汇到本地，目前只支持下载一页词汇（纠结是下载一页呢还是全部下载？？！！）</>
<p>增加"取消收藏"功能之删除本地词汇</p>
<p>终于，基本功能算是拼凑完成了，艰巨的任务才刚刚开始：
    1、代码结构优化。目前自认为是MVC，可总是别扭的很，Fragment与Adapter之间的关联，异步数据上传、下载与界面之间的交互，等等，
    现在的代码让我感觉怪怪的，需要从哪里去学习下处理方式才好。哪里呢？？
    2、数据新增或删除后，返回主界面如何进行数据同步呢？ 现在是手动刷新，暂时使用这个方法。等有了能够进行数据同步的方法，再增加吧！！
    3、对于Adapter与ViewHolder之间的关系还是要调整下的，打算参考《Android Programming: The Big Nerd Ranch Guide(2nd Edition)》中
    第9章“RecyclerView,Adapter,and ViewHolder”小节描述的三者职能，来调整目前的代码。
    4、对Repository模式的使用表示怀疑，不是真正的repository，特别是几乎所有方法都有Context入参，感觉这个很不好！！
</p>
