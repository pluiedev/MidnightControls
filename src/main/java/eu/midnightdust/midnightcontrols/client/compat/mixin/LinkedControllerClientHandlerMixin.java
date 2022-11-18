package eu.midnightdust.midnightcontrols.client.compat.mixin;

import com.simibubi.create.content.logistics.item.LinkedControllerClientHandler;
import eu.midnightdust.midnightcontrols.client.compat.CreateCompat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Vector;

@Mixin(LinkedControllerClientHandler.class)
@Environment(EnvType.CLIENT)
public abstract class LinkedControllerClientHandlerMixin {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/HashSet;<init>(Ljava/util/Collection;)V",
            ordinal = 0
        ),
        remap = false,
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void injectControllerInput(
            CallbackInfo ci, MinecraftClient mc, ClientPlayerEntity player, ItemStack heldItem, Vector controls, Collection pressedKeys
    ) {
        CreateCompat.PRESSED_BUTTONS.stream().map(Enum::ordinal).forEach(pressedKeys::add);
    }
}
