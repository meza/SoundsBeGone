/*? if fabric {*/
package gg.meza.soundsbegone.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import gg.meza.soundsbegone.client.ConfigScreen;


public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::getConfigeScreen;
    }
}
/*?}*/
