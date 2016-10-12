package com.ldb.vocabulary2.android.data;

import android.accounts.Account;

import com.ldb.vocabulary2.android.model.Category;
import com.ldb.vocabulary2.android.model.Vocabulary;

import java.util.List;

/**
 * Presenter --- RequestXXXCallback --> Repository --- Callback --> DataSource
 * Presenter <-- onSuccess(message, ...) / onError(message, ...) -- Repository <--- onResult(isOk, message) -- DataSource
 * Created by lsp on 2016/9/17.
 */
public interface Callback {

    void onResult(boolean isOk, String response);

    public interface RequestErrorCallback{
        /**
         * 失败
         * @param error 错误信息
         */
        void onError(String error);
    }

    /**
     * 获取短信验证码回调接口
     */
    public interface RequestCheckCodeCallback extends RequestErrorCallback{
        /**
         * 成功
         * @param message
         * @param checkCode 验证码
         */
        void onSuccess(String message, String checkCode);

    }

    /**
     * 登录回调接口
     */
    public interface RequestLoginCallback extends RequestErrorCallback{
        /**
         * 成功
         * @param message
         * @param account 帐户信息
         */
        void onSuccess(String message, Account account);

//        /**
//         * 失败
//         * @param error 错误信息
//         */
//        void onError(String error);
    }

    /**
     * 词汇分类列表回调借口
     */
    public interface RequestCategoryListCallback extends RequestErrorCallback{

        void onSuccess(String message, List<Category> categoryList);
    }

    /**
     * 词汇列表回调接口
     */
    public interface RequestVocabularyListCallback extends RequestErrorCallback{
        void onSuccess(String message, List<Vocabulary> vocabularyList);
    }

    public interface PostCategoryCallback extends RequestErrorCallback{
        void onSuccess(String message, Category category);
    }
}
