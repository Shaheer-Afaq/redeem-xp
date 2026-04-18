package redeemxp;
import blue.endless.jankson.Comment;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.PredicateConstraint;
import io.wispforest.owo.config.annotation.RangeConstraint;

@Modmenu(modId = "redeemxp")
@Config(name = "redeemxp", wrapperName = "MyConfig")
public class ConfigModel {

    @Comment("Maximum xp a bottle can hold")
    @PredicateConstraint("isInRange")
    public int max_xp = 1395;

    @Comment("How much xp is thrown at each use")
    @PredicateConstraint("isInRange")
    public int xp_rate = 20;

    @Comment("How much xp is conserved in a bottle on death")
    @RangeConstraint(min = 0, max = 100)
    public int xp_percentage_on_death = 50;

    public static boolean isInRange(int value) {
        return value > 0 && value <= 10000000;
    }
}
