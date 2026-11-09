papackage com.example.addon;

import com.example.addon.modules.MeteorBaritoneBridge;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class MyCustomClientInitializer extends MeteorAddon {
    public static final Category CATEGORY = new Category("Fused-Client");

    @Override
    public void onInitialize() {
        // Register modules into the Meteor module system
        Modules.get().add(new MeteorBaritoneBridge());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
  }
}￼Enter
