/*
 * Copyright (C) 2013, 2014 Brett Wooldridge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaxxer.hikari.pool;

import java.util.concurrent.ScheduledExecutorService;

/**
 * A factory for {@link ProxyLeakTask} Runnables that are scheduled in the future to report leaks.
 * 一个用于将来调用报告更多任务的{@link ProxyLeakTask} Runnable的工厂。
 *
 * @author Brett Wooldridge
 * @author Andreas Brenk
 */
class ProxyLeakTaskFactory
{
   private ScheduledExecutorService executorService;
   private long leakDetectionThreshold;

   ProxyLeakTaskFactory(final long leakDetectionThreshold, final ScheduledExecutorService executorService)
   {
      this.executorService = executorService;
      this.leakDetectionThreshold = leakDetectionThreshold;
   }

   /**
    * 如果leakDetectionThreshold=0，即禁用连接泄露检测，schedule返回的是ProxyLeakTask.NO_LEAK，否则则新建一个ProxyLeakTask，在leakDetectionThreshold时间后触发
    * @param poolEntry
    * @return
    */
   ProxyLeakTask schedule(final PoolEntry poolEntry)
   {
      return (leakDetectionThreshold == 0) ? ProxyLeakTask.NO_LEAK : scheduleNewTask(poolEntry);
   }

   void updateLeakDetectionThreshold(final long leakDetectionThreshold)
   {
      this.leakDetectionThreshold = leakDetectionThreshold;
   }

   private ProxyLeakTask scheduleNewTask(PoolEntry poolEntry) {
      ProxyLeakTask task = new ProxyLeakTask(poolEntry);
      task.schedule(executorService, leakDetectionThreshold);

      return task;
   }
}
