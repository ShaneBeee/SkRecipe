package tk.shanebee.skrecipe.elements.effects;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.*;
import ch.njol.skript.events.bukkit.SkriptStartEvent;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.*;
import tk.shanebee.skrecipe.SkRecipe;

@Name("Recipe - Cooking")
@Description("Register new cooking recipes. " +
        "On 1.13+ you can register recipes for furnaces. " +
        "On 1.14+ you can also register recipes for smokers, blast furnaces and campfires. " +
        "The ID will be the name given to this recipe. Used for recipe discovery/unlocking recipes for players. " +
        "You may also include an optional group for recipes. These will group the recipes together in the recipe book. " +
        "Recipes must be registered in a <b>Skript load event</b>")
@Examples({"on skript load:",
        "\tregister new furnace recipe for diamond using dirt with id \"furnace_diamond\"",
        "\tregister new blasting recipe for emerald using dirt with id \"blasting_emerald\"",
        "\tregister new smoking recipe for cooked cod named \"Hot Cod\" using puffer fish with id \"smoking_cod\""})
@RequiredPlugins("1.13+ for furnaces. 1.14+ for smokers, blast furnaces and campfires.")
@Since("1.0.0")
public class EffCookingRecipe extends Effect {

    private static final boolean HAS_BLASTING = Skript.isRunningMinecraft(1, 14);

    static {
        if (!HAS_BLASTING) {
            Skript.registerEffect(EffCookingRecipe.class,
                    "register [new] furnace recipe for %itemtype% " +
                            "(using|with ingredient) %itemtype% with id %string% [[and ]with exp[erience] %-number%] " +
                            "[[and ]with cook[ ]time %-timespan%] [in group %-string%]");
        } else {
            Skript.registerEffect(EffCookingRecipe.class,
                    "register [new] (0¦furnace|1¦(blast furnance|blasting)|2¦smok(er|ing)|3¦campfire) recipe for %itemtype% " +
                            "(using|with ingredient) %itemtype% with id %string% [[and ]with exp[erience] %-number%] " +
                            "[[and ]with cook[ ]time %-timespan%] [in group %-string%]");
        }
    }

    @SuppressWarnings("null")
    private Expression<ItemType> item;
    private Expression<ItemType> ingredient;
    private Expression<String> key;
    private Expression<Number> experience;
    private Expression<Timespan> cookTime;
    private Expression<String> group;
    private int recipeType;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(SkriptStartEvent.class)) {
            Skript.error("Recipes can only be registered during a Skript load event!");
            return false;
        }
        item = (Expression<ItemType>) exprs[0];
        ingredient = (Expression<ItemType>) exprs[1];
        key = (Expression<String>) exprs[2];
        experience = (Expression<Number>) exprs[3];
        cookTime = (Expression<Timespan>) exprs[4];
        group = (Expression<String>) exprs[5];

        recipeType = !HAS_BLASTING ? 0 : parseResult.mark;
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void execute(Event event) {
        ItemType res = this.item.getSingle(event);
        ItemType ing = this.ingredient.getSingle(event);
        if (res == null) {
            Skript.error("Error registering cooking recipe - result is null");
            Skript.error("Current Item: §6" + this.toString(event, true));
            return;
        }
        if (ing == null) {
            Skript.error("Error registering cooking recipe - ingredient is null");
            Skript.error("Current Item: §6" + this.toString(event, true));
            return;
        }

        ItemStack result = res.getRandom();

        NamespacedKey key = new NamespacedKey(SkRecipe.getInstance(), this.key.getSingle(event));

        CookingRecipe recipe;
        float xp = experience != null ? experience.getSingle(event).floatValue() : 0;
        int cookTime;

        RecipeChoice.ExactChoice ingredient = new RecipeChoice.ExactChoice(ing.getRandom());
        String group = this.group != null ? this.group.getSingle(event) : "";

        switch (recipeType) {
            case 1: // BLASTING
                cookTime = this.cookTime != null ? ((int) this.cookTime.getSingle(event).getTicks_i()) : 100;
                recipe = new BlastingRecipe(key, result, ingredient, xp, cookTime);
                break;
            case 2: // SMOKING
                cookTime = this.cookTime != null ? ((int) this.cookTime.getSingle(event).getTicks_i()) : 100;
                recipe = new SmokingRecipe(key, result, ingredient, xp, cookTime);
                break;
            case 3: // CAMPFIRE
                cookTime = this.cookTime != null ? ((int) this.cookTime.getSingle(event).getTicks_i()) : 600;
                recipe = new CampfireRecipe(key, result, ingredient, xp, cookTime);
                break;
            default: // FURNACE
                cookTime = this.cookTime != null ? ((int) this.cookTime.getSingle(event).getTicks_i()) : 200;
                recipe = new FurnaceRecipe(key, result, ingredient, xp, cookTime);
        }

        recipe.setGroup(group);
        Bukkit.addRecipe(recipe);
    }

    @Override
    public String toString(Event e, boolean d) {
        String type;
        switch (recipeType) {
            case 1:
                type = "blasting";
                break;
            case 2:
                type = "smoking";
                break;
            case 3:
                type = "campfire";
                break;
            default:
                type = "furnace";
        }
        String xp = experience != null ? " and with xp " + experience.toString(e, d) : "";
        String cook = cookTime != null ? " and with cooktime " + cookTime.toString(e, d) : "";
        return "register new " + type + " recipe for " + item.toString(e, d) + " using " + ingredient.toString(e, d) +
                " with id " + key.toString(e, d) + xp + cook;
    }

}
