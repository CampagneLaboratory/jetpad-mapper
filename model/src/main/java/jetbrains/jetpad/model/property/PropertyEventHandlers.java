/*
 * Copyright 2012-2016 JetBrains s.r.o
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.jetpad.model.property;

import jetbrains.jetpad.model.event.EventHandler;


public class PropertyEventHandlers {
  public static class CountingHandler<ItemT> implements EventHandler<PropertyChangeEvent<ItemT>> {
    private int counter = 0;

    @Override
    public void onEvent(PropertyChangeEvent<ItemT> event) {
      counter += 1;
    }

    public int getCounter() {
      return counter;
    }
  }

  public static class RecordingHandler<ItemT> implements EventHandler<PropertyChangeEvent<ItemT>> {
    private ItemT oldValue, newValue;

    @Override
    public void onEvent(PropertyChangeEvent<ItemT> event) {
      oldValue = event.getOldValue();
      newValue = event.getNewValue();
    }

    public ItemT getOldValue() {
      return oldValue;
    }

    public ItemT getNewValue() {
      return newValue;
    }
  }
}