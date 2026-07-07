package shade.utility.interfaces;

import net.minecraft.util.math.BlockPos;
import shade.features.modules.render.Trails;

import java.util.List;

public interface IEntity {
    List<Trails.Trail> getTrails();

    BlockPos exoHack_Recode$getVelocityBP();
}
