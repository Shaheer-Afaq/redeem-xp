package redeemxp;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.PredicateConstraint;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.ui.parsing.UIModel;

@Modmenu(modId = "redeemxp")
@Config(name = "redeemxp", wrapperName = "MyConfig")
public class ConfigModel {
//    @RangeConstraint(min = 1, max = 1000000, decimalPlaces = 0)
    @PredicateConstraint("isInRange")
    public int max_xp = 1395;

//    @RangeConstraint(min = 1, max = 1000000, decimalPlaces = 0)
    @PredicateConstraint("isInRange")
    public int xp_rate = 10;

    public static boolean isInRange(int value) {
        return value > 0 && value <= 1000000;
    }
}
