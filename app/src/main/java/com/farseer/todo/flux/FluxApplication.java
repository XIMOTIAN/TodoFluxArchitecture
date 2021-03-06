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

package com.farseer.todo.flux;

import android.app.Application;
import com.farseer.todo.flux.action.creator.ActionCreator;
import com.farseer.todo.flux.di.component.ApplicationComponent;
import com.farseer.todo.flux.dispatcher.Dispatcher;
import com.farseer.todo.flux.store.Store;
import com.farseer.todo.flux.tool.ProcessTool;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * Flux Application
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 2016-04-18
 */
public class FluxApplication extends Application {
    @Inject
    Store todoStore;

    @Inject
    @Named("dataDispatcher")
    Dispatcher dataDispatcher;

    @Inject
    @Named("actionDispatcher")
    Dispatcher actionDispatcher;

    @Inject
    ActionCreator actionCreator;


    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = ProcessTool.getProcessName(getApplicationContext());
        if (!BuildConfig.APPLICATION_ID.equals(processName)) {
            return;
        }
        initApplication();
    }

    //初始化Application
    private void initApplication() {
        initInjector();
    }

    private void initInjector() {
        component = ApplicationComponent.Initializer.init(this);
        component.inject(this);
    }

    /**
     * 获得ApplicationComponent.
     *
     * @return component
     */
    public ApplicationComponent getComponent() {
        return component;
    }
}