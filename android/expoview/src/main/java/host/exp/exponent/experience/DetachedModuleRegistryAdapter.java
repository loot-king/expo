package host.exp.exponent.experience;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;

import org.json.JSONObject;
import org.unimodules.adapters.react.ReactModuleRegistryProvider;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.core.interfaces.InternalModule;
import org.unimodules.core.interfaces.RegistryLifecycleListener;

import java.util.List;
import java.util.Map;

import expo.modules.notifications.notifications.scheduling.NotificationScheduler;
import host.exp.exponent.kernel.ExperienceId;
import host.exp.exponent.utils.ScopedContext;
import versioned.host.exp.exponent.modules.universal.ConstantsBinding;
import versioned.host.exp.exponent.modules.universal.ExpoModuleRegistryAdapter;
import versioned.host.exp.exponent.modules.universal.ScopedFileSystemModule;
import versioned.host.exp.exponent.modules.universal.ScopedUIManagerModuleWrapper;
import versioned.host.exp.exponent.modules.universal.UpdatesBinding;

public class DetachedModuleRegistryAdapter extends ExpoModuleRegistryAdapter {
  public DetachedModuleRegistryAdapter(ReactModuleRegistryProvider moduleRegistryProvider) {
    super(moduleRegistryProvider);
  }

  @Override
  public List<NativeModule> createNativeModules(final ScopedContext scopedContext, ExperienceId experienceId, Map<String, Object> experienceProperties, JSONObject manifest, List<NativeModule> otherModules) {
    ReactApplicationContext reactApplicationContext = (ReactApplicationContext) scopedContext.getContext();

    // We only use React application context, because we're detached -- no scopes
    ModuleRegistry moduleRegistry = mModuleRegistryProvider.get(scopedContext);

    moduleRegistry.registerInternalModule(new ConstantsBinding(scopedContext, experienceProperties, manifest));

    // Overriding expo-updates UpdatesService
    moduleRegistry.registerInternalModule(new UpdatesBinding(scopedContext, experienceProperties));

    // ReactAdapterPackage requires ReactContext
    ReactApplicationContext reactContext = (ReactApplicationContext) scopedContext.getContext();
    for (InternalModule internalModule : mReactAdapterPackage.createInternalModules(reactContext)) {
      moduleRegistry.registerInternalModule(internalModule);
    }

    // Overriding ScopedUIManagerModuleWrapper from ReactAdapterPackage
    moduleRegistry.registerInternalModule(new ScopedUIManagerModuleWrapper(reactContext));

    // Overriding expo-file-system FileSystemModule
    moduleRegistry.registerExportedModule(new ScopedFileSystemModule(scopedContext));

    // `NotificationScheduler` needs to share `SharedPreferences` object with the notifications services, we don't want to use scoped context.
    moduleRegistry.registerExportedModule(new NotificationScheduler(scopedContext.getBaseContext()));

    // Adding other modules (not universal) to module registry as consumers.
    // It allows these modules to refer to universal modules.
    for (NativeModule otherModule : otherModules) {
      if (otherModule instanceof RegistryLifecycleListener) {
        moduleRegistry.registerExtraListener((RegistryLifecycleListener) otherModule);
      }
    }

    configureModuleRegistry(moduleRegistry, reactApplicationContext);

    return getNativeModulesFromModuleRegistry(reactApplicationContext, moduleRegistry);
  }

  protected void configureModuleRegistry(ModuleRegistry moduleRegistry, ReactApplicationContext reactContext) {
    // Subclasses may add more modules here.
  }
}
