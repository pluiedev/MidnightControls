/*
 * Copyright © 2022 Motschen <motschen@midnightdust.eu>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.compat;

import com.simibubi.create.content.logistics.item.LinkedControllerClientHandler;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.ButtonCategory;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import static eu.midnightdust.midnightcontrols.client.controller.ButtonBinding.axisAsButton;

/**
 * Handler for compatibility with Create.
 * <p>
 * Currently registers button bindings to Create's Linked Controllers.
 *
 * @author pluiedev
 * @version 1.7.0
 * @since 1.7.0
 */
public class CreateCompat implements CompatHandler {

    private static boolean IS_CREATE_PRESENT;

    public static EnumSet<LinkedControllerButton> PRESSED_BUTTONS = EnumSet.noneOf(LinkedControllerButton.class);
    public static List<ButtonBinding> BINDINGS;

    public CreateCompat() {
        IS_CREATE_PRESENT = true;
    }

    @Override
    public void handle(@NotNull MidnightControlsClient mod) {
        ButtonCategory category = new ButtonCategory(new Identifier("midnightcontrols","category.create"), 101);
        InputManager.registerCategory(category);

        BINDINGS = Arrays.stream(LinkedControllerButton.values())
                .map(button -> button.registerButtonBinding(category))
                .collect(Collectors.toList());
    }

    public static boolean isOverriddenByController(MinecraftClient client, ButtonBinding binding) {
        System.out.println(binding);
        BINDINGS.forEach(System.out::println);

        return IS_CREATE_PRESENT &&
                (LinkedControllerClientHandler.MODE == LinkedControllerClientHandler.Mode.ACTIVE) &&
                BINDINGS.contains(binding);
    }

    public enum LinkedControllerButton {
        UP("up", axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y, true)),
        DOWN("down", axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y, false)),
        LEFT("left", axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X, false)),
        RIGHT("right", axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y, true)),
        JUMP("jump", GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB),
        SNEAK("sneak", GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB);

        private final String name;
        private final int[] button;

        LinkedControllerButton(String name, int... button) {
            this.name = name;
            this.button = button;
        }

        public ButtonBinding registerButtonBinding(ButtonCategory category) {
            return new ButtonBinding.Builder("create_linked_controller_" + name)
                .buttons(button)
                .category(category)
                .action((client, button, value, action) -> {
                    if (action.isPressed())
                        PRESSED_BUTTONS.add(this);
                    else
                        PRESSED_BUTTONS.remove(this);
                    return true;
                })
                .cooldown()
                .filter(CreateCompat::isOverriddenByController)
                .register();
        }
    }

    static {
        IS_CREATE_PRESENT = FabricLoader.getInstance().isModLoaded("create");
    }
}
