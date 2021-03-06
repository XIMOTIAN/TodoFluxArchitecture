/*
 *
 *    Copyright 2016 zhaosc
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.farseer.todo.flux.store;

import com.farseer.todo.flux.action.TodoItemAction;
import com.farseer.todo.flux.action.TodoListAction;
import com.farseer.todo.flux.action.base.DataBundle;
import com.farseer.todo.flux.di.ForApplication;
import com.farseer.todo.flux.dispatcher.Dispatcher;
import com.farseer.todo.flux.model.TodoListModel;
import com.farseer.todo.flux.pojo.TodoItem;
import com.farseer.todo.flux.tool.LogTool;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Todo Store
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 2016-04-19
 */
@ForApplication
public class TodoStore implements Store {

    private Map<Long, TodoItem> todoItemHashMap = new HashMap<>();
    private TodoListModel.Filter filter = TodoListModel.Filter.ALL;

    private Dispatcher dataDispatcher;
    private Dispatcher actionDispatcher;

    /**
     * 构造TodoStore.
     *
     * @param dataDispatcher   Data Dispatcher
     * @param actionDispatcher Action Dispatcher
     */
    @Inject
    public TodoStore(@Named("dataDispatcher") Dispatcher dataDispatcher,
                     @Named("actionDispatcher") Dispatcher actionDispatcher) {
        LogTool.debug("构造 TodoStore");
        this.dataDispatcher = dataDispatcher;
        this.actionDispatcher = actionDispatcher;
    }

    @Override
    public void register() {
        actionDispatcher.register(this);
    }

    @Override
    public void unregister() {
        actionDispatcher.unregister(this);
    }

    /**
     * 订阅TodoItemAction事件.
     *
     * @param todoItemAction 单个todo事项事件
     */
    @Subscribe
    public final void subscribeItemAction(TodoItemAction todoItemAction) {
        LogTool.debug(todoItemAction.toString());
        DataBundle<TodoItemAction.Key> data = todoItemAction.getDataBundle();
        Long id = (Long) data.get(TodoItemAction.Key.ID, -1L);
        TodoItem item = (TodoItem) data.get(TodoItemAction.Key.ITEM, null);
        switch ((TodoItemAction.Type) todoItemAction.getType()) {
            case NEW:
                if (item != null) {
                    todoItemHashMap.put(item.getId(), item);
                    notifyTodoListModelChanged();
                }
                break;
            case EDIT:
                if (item != null) {
                    todoItemHashMap.put(item.getId(), item);
                    notifyTodoListModelChanged();
                }
                break;
            case DELETE:
                todoItemHashMap.remove(id);
                notifyTodoListModelChanged();
                break;
            default:
                break;
        }
    }

    /**
     * 订阅TodoListAction事件.
     *
     * @param todoListAction todo事项列表事件
     */
    @Subscribe
    public final void subscribeListAction(TodoListAction todoListAction) {
        LogTool.debug(todoListAction.toString());
        DataBundle<TodoListAction.Key> data = todoListAction.getDataBundle();
        switch ((TodoListAction.Type) todoListAction.getType()) {
            case LOAD: {
                List<TodoItem> list = (List<TodoItem>) data.get(TodoListAction.Key.LIST, null);
                if (list != null) {
                    todoItemHashMap.clear();
                    for (TodoItem item : list) {
                        todoItemHashMap.put(item.getId(), item);
                    }
                }
                filter = TodoListModel.Filter.ALL;
                break;
            }
            case SHOW_ALL:
                filter = TodoListModel.Filter.ALL;
                break;
            case SHOW_COMPLETED:
                filter = TodoListModel.Filter.COMPLETED;
                break;
            case SHOW_STARED:
                filter = TodoListModel.Filter.STARED;
                break;
            default:
                break;
        }

        notifyTodoListModelChanged();
    }

    private void notifyTodoListModelChanged() {
        List<TodoItem> todoItemList = new ArrayList<>();
        todoItemList.addAll(todoItemHashMap.values());
        TodoListModel todoListModel = new TodoListModel(todoItemList, filter);
        dataDispatcher.post(todoListModel);
    }

}