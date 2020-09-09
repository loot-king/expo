#pragma once

#include "ABI39_0_0SharedParent.h"
#include "ABI39_0_0MutableValueSetterProxy.h"
#include <mutex>
#include <ABI39_0_0jsi/ABI39_0_0jsi.h>
#include <map>

using namespace ABI39_0_0facebook;

namespace ABI39_0_0reanimated {

class MutableValue : public jsi::HostObject, public std::enable_shared_from_this<MutableValue> {
  private:
  friend MutableValueSetterProxy;
  NativeReanimatedModule *module;
  std::mutex readWriteMutex;
  std::shared_ptr<ShareableValue> value;
  jsi::Value setter;
  jsi::Value animation;
  std::map<unsigned long, std::function<void()>> listeners;

  void setValue(jsi::Runtime &rt, const jsi::Value &newValue);
  jsi::Value getValue(jsi::Runtime &rt);

  public:
  MutableValue(jsi::Runtime &rt, const jsi::Value &initial, NativeReanimatedModule *module);

  public:
  void set(jsi::Runtime &rt, const jsi::PropNameID &name, const jsi::Value &value);
  jsi::Value get(jsi::Runtime &rt, const jsi::PropNameID &name);
  std::vector<jsi::PropNameID> getPropertyNames(jsi::Runtime &rt);
  unsigned long addListener(unsigned long listenerId, std::function<void()> listener);
  void removeListener(unsigned long listenerId);
};

}
