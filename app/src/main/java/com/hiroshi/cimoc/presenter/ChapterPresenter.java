package com.hiroshi.cimoc.presenter;

import android.content.ContentResolver;
import android.support.v4.provider.DocumentFile;

import com.hiroshi.cimoc.core.Download;
import com.hiroshi.cimoc.core.manager.ComicManager;
import com.hiroshi.cimoc.core.manager.TaskManager;
import com.hiroshi.cimoc.model.Chapter;
import com.hiroshi.cimoc.model.Comic;
import com.hiroshi.cimoc.model.MiniComic;
import com.hiroshi.cimoc.model.Task;
import com.hiroshi.cimoc.rx.RxBus;
import com.hiroshi.cimoc.rx.RxEvent;
import com.hiroshi.cimoc.ui.view.ChapterView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Hiroshi on 2016/11/14.
 */

public class ChapterPresenter extends BasePresenter<ChapterView> {

    private ComicManager mComicManager;
    private TaskManager mTaskManager;
    private Comic mComic;

    public ChapterPresenter() {
        mComicManager = ComicManager.getInstance();
        mTaskManager = TaskManager.getInstance();
    }

    public void load(long id) {
        mComic = mComicManager.load(id);
    }

    /**
     * 添加任务到数据库
     * @param cList 所有章节列表，用于写索引文件
     * @param dList 下载章节列表
     */
    public void addTask(ContentResolver resolver, DocumentFile root, List<Chapter> cList, final List<Chapter> dList) {
        mCompositeSubscription.add(Download.updateComicIndex(resolver, root, cList, mComic)
                .flatMap(new Func1<Void, Observable<ArrayList<Task>>>() {
                    @Override
                    public Observable<ArrayList<Task>> call(Void v) {
                        return mComicManager.callInRx(new Callable<ArrayList<Task>>() {
                            @Override
                            public ArrayList<Task> call() throws Exception {
                                Long key = mComic.getId();
                                mComic.setDownload(System.currentTimeMillis());
                                mComicManager.updateOrInsert(mComic);
                                ArrayList<Task> result = new ArrayList<>();
                                for (Chapter chapter : dList) {
                                    Task task = new Task(null, key, chapter.getPath(), chapter.getTitle(), 0, 0, null);
                                    long id = mTaskManager.insert(task);
                                    task.setId(id);
                                    task.setSource(mComic.getSource());
                                    task.setCid(mComic.getCid());
                                    task.setState(Task.STATE_WAIT);
                                    result.add(task);
                                }
                                return result;
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<Task>>() {
                    @Override
                    public void call(ArrayList<Task> list) {
                        RxBus.getInstance().post(new RxEvent(RxEvent.EVENT_TASK_INSERT, new MiniComic(mComic), list));
                        mBaseView.onTaskAddSuccess(list);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mBaseView.onTaskAddFail();
                    }
                }));
    }

}