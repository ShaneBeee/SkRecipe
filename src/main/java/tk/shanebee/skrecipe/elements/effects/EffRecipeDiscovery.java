package tk.shanebee.skrecipe.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import tk.shanebee.skrecipe.SkRecipe;

@Name("Recipe - Discovery")
@Description("Lock/Unlock recipes for players. This uses the IDs we created earlier when registering recipes, " +
        "you can also lock/unlock minecraft recipes.")
@Examples({"unlock recipe \"smoking_cod\" for all players",
        "on pickup of diamonds:",
        "\tdiscover recipe \"fancy_diamonds\" for player"})
public class EffRecipeDiscovery extends Effect {

    private static final SkRecipe plugin = SkRecipe.getInstance();

    static {
        Skript.registerEffect(EffRecipeDiscovery.class,
                "(discover|unlock) [(custom|1¦(mc|minecraft)] recipe[s] [with id[s]] %strings% for %players%",
                "(undiscover|lock) [(custom|1¦(mc|minecraft)] recipe[s] [with id[s]] %strings% for %players%");
    }

    @SuppressWarnings("null")
    private Expression<String> recipes;
    private Expression<Player> players;
    private boolean discover;
    private boolean minecraft;

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int pattern, Kleenean kleenean, ParseResult parseResult) {
        recipes = (Expression<String>) exprs[0];
        players = (Expression<Player>) exprs[1];
        discover =  pattern == 0;
        minecraft = parseResult.mark == 1;
        return true;
    }

    @Override
    protected void execute(Event event) {
        Player[] players = this.players.getAll(event);
        String[] recipes = this.recipes.getAll(event);
        for (Player player : players) {
            for (String recipe : recipes) {
                NamespacedKey key;
                if (minecraft)
                    key = NamespacedKey.minecraft(recipe);
                else
                    key = new NamespacedKey(plugin, recipe);

                if (discover)
                    player.discoverRecipe(key);
                else
                    player.undiscoverRecipe(key);
            }
        }
    }

    @Override
    public String toString(Event e, boolean d) {
        String disc = discover ? "discover" : "undiscover";
        return disc + " recipe[s] " + recipes.toString(e, d) + " for " + players.toString(e, d);
    }

}
