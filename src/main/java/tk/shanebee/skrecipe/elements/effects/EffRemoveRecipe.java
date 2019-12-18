package tk.shanebee.skrecipe.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import tk.shanebee.skrecipe.Config;
import tk.shanebee.skrecipe.SkRecipe;
import tk.shanebee.skrecipe.recipe.Remover;

@Name("Recipe - Remove")
@Description({"Remove a vanilla Minecraft recipe from your server. Recipes can be removed at any time ",
        "but it is best to do so during a server load event. If a recipe is removed whilst a player is online ",
        "it will still show up in their recipe book, but they will not be able to craft it. If need be, you can get ",
        "a list of all recipes by simply typing \"/minecraft:recipe give YourName \" in game."})
@Examples({"remove mc recipe \"acacia_boat\"", "remove minecraft recipe \"cooked_chicken_from_campfire_cooking\""})
@Since("1.3.0")
public class EffRemoveRecipe extends Effect {

    private static Remover REMOVER = null;
    private Config config = SkRecipe.getInstance().getPluginConfig();

    static {
        try {
            REMOVER = new Remover();
            Skript.registerEffect(EffRemoveRecipe.class,
                    "remove (mc|minecraft) recipe[s] %strings%");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            Skript.warning("[SkRecipe] - Recipe remover failed to load!");
        }
    }

    @SuppressWarnings("null")
    private Expression<String> recipes;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        recipes = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        String[] recipes = this.recipes.getAll(event);
        if (recipes == null) return;

        for (String recipe : recipes) {
            if (recipe.startsWith("minecraft:")) {
                recipe = recipe.replace("minecraft:", "");
            }
            REMOVER.removeRecipeByKey(recipe);
            if (config.DEBUG) {
                SkRecipe.log("&aRemoving recipe: minecraft:" + recipe);
            }
        }
    }

    @Override
    public String toString(Event e, boolean d) {
        return "remove minecraft recipes " + recipes.toString(e, d);
    }

}