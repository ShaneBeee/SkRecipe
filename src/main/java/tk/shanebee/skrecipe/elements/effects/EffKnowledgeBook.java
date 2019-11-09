package tk.shanebee.skrecipe.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import tk.shanebee.skrecipe.SkRecipe;

import java.util.ArrayList;
import java.util.List;

@Name("Recipe - Knowledge Book")
@Description("Add/Remove custom or minecraft recipes to/from a knowledge book item.")
@Examples({"add custom recipe \"my_recipe\" to player's tool",
        "add minecraft recipe \"cooked_cod_from_campfire_cooking\" to {_book}"})
@Since("1.1.0")
public class EffKnowledgeBook extends Effect {

    static {
        Skript.registerEffect(EffKnowledgeBook.class,
                "add [(custom|1¦(mc|minecraft))] [recipe[s]] [with id[s]] %strings% to %itemtype%",
                "remove [(custom|1¦(mc|minecraft)] [recipe[s]] [with id[s]] %strings% from %itemtype%");
    }

    private Expression<String> recipes;
    private Expression<ItemType> book;
    private boolean minecraft;
    private boolean add;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int pattern, Kleenean kleenean, ParseResult parseResult) {
        recipes = (Expression<String>) exprs[0];
        book = (Expression<ItemType>) exprs[1];
        minecraft = parseResult.mark == 1;
        add = pattern != 1;
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (book.getSingle(event).getMaterial() != Material.KNOWLEDGE_BOOK)
            return;

        ItemType book = this.book.getSingle(event);
        String[] recipes = this.recipes.getAll(event);
        KnowledgeBookMeta meta = ((KnowledgeBookMeta) book.getItemMeta());

        List<NamespacedKey> allRecipes = new ArrayList<>(meta.getRecipes());
        for (String recipe : recipes) {
            NamespacedKey key;
            if (minecraft)
                key = NamespacedKey.minecraft(recipe);
            else
                key = new NamespacedKey(SkRecipe.getInstance(), recipe);

            if (add)
                allRecipes.add(key);
            else
                allRecipes.remove(key);
        }
        meta.setRecipes(allRecipes);
        book.setItemMeta(meta);
    }

    @Override
    public String toString(Event e, boolean d) {
        return (add ? "add" : "remove") + (minecraft ? " minecraft" : "custom") + " recipe(s) " + recipes.toString(e, d) +
                (add ? " to " : " from ") + book.toString(e, d);
    }

}
