package tk.shanebee.skrecipe.elements.effects;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.*;
import ch.njol.skript.events.bukkit.SkriptStartEvent;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import tk.shanebee.skrecipe.SkRecipe;

@Name("Recipe - StoneCutting")
@Description("Register a new stone cutting recipe. " +
        "The ID will be the name given to this recipe. Used for recipe discovery/unlocking recipes for players. " +
        "You may also include an optional group for recipes. These will group the recipes together in the recipe book. " +
        "Recipes must be registered in a <b>Skript load event</b>")
@Examples("register new stone cutting recipe for diamond using diamond ore with id \"cutting_diamond\"")
@RequiredPlugins("1.14+")
@Since("1.0.0")
public class EffStonecuttingRecipe extends Effect {

    static {
        if (Skript.isRunningMinecraft(1, 14)) {
            Skript.registerEffect(EffStonecuttingRecipe.class,
                    "register [new] stone cutt(ing|er) recipe for %itemtype% (using|with ingredient) %itemtype% with id %string% [in group %-string%]");
        }
    }

    @SuppressWarnings("null")
    private Expression<ItemType> item;
    private Expression<ItemType> ingredient;
    private Expression<String> key;
    private Expression<String> group;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(SkriptStartEvent.class)) {
            Skript.error("Recipes can only be registered during a Skript load event!");
            return false;
        }
        item = (Expression<ItemType>) exprs[0];
        ingredient = (Expression<ItemType>) exprs[1];
        key = (Expression<String>) exprs[2];
        group = (Expression<String>) exprs[3];
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void execute(Event event) {
        ItemType item = this.item.getSingle(event);
        ItemType ingredient = this.ingredient.getSingle(event);

        if (item == null) {
            Skript.error("Error registering stonecutting recipe - result is null");
            Skript.error("Current Item: §6" + this.toString(event, true));
            return;
        }
        if (ingredient == null) {
            Skript.error("Error registering stonecutting recipe - ingredient is null");
            Skript.error("Current Item: §6" + this.toString(event, true));
            return;
        }

        String group = this.group != null ? this.group.getSingle(event) : "";

        NamespacedKey key = new NamespacedKey(SkRecipe.getInstance(), this.key.getSingle(event));

        RecipeChoice.ExactChoice choice = new RecipeChoice.ExactChoice(ingredient.getRandom());
        StonecuttingRecipe recipe = new StonecuttingRecipe(key, item.getRandom(), choice);
        recipe.setGroup(group);

        Bukkit.addRecipe(recipe);
    }

    @Override
    public String toString(Event e, boolean d) {
        String group = this.group != null ? " in group " + this.group.toString(e, d) : "";
        return "register new stone cutting recipe for " + item.toString(e, d) + " using " + ingredient.toString(e, d) + group;
    }

}
