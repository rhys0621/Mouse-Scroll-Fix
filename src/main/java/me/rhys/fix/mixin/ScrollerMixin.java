package me.rhys.fix.mixin;

import me.rhys.fix.Mod;
import net.minecraft.client.input.Scroller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Scroller.class)
public class ScrollerMixin {
    @Unique
    private static long lastScroll = 0;

    @Unique
    private static int lastIndex = -1;

    @Inject(method = "scrollCycling", at = @At("RETURN"), cancellable = true)
    private static void fixDoubleScroll(double amount, int selectedIndex,
                                        int total, CallbackInfoReturnable<Integer> cir) {
        long timestamp = System.currentTimeMillis();

        // the issue always returns less than 1 millisecond
        // we set the current scroll wheel index to the last recorded index
        if ((timestamp - lastScroll) <= 1L && lastIndex != -1) {
            Mod.LOGGER.info("Invalid skip detected # attempted: {} fixed: {}", selectedIndex, lastIndex);
            cir.setReturnValue(lastIndex);
            return;
        }

        lastIndex = selectedIndex;
        lastScroll = timestamp;
    }
}
