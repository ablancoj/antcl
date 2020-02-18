package cat.urv.gfa.loyalty.utils;

import cat.urv.gfa.loyalty.model.Token;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import com.walmart.openapi.responses.Item;
import com.walmart.openapi.SearchApi;
import java.util.List;

public class CategoryPath
{
    public static final String ROOT = "Product";
    public static final String API_KEY = "kau3ahj4btyewmhmucx2mkmk";
    
    private CategoryPath() {
    }
    
    public static List<String> getPath(final String product) throws IOException, ExecutionException, InterruptedException {
        final SearchApi search = new SearchApi("kau3ahj4btyewmhmucx2mkmk");
        final Item i = search.getSearchResponse(product).getItems().get(0);
        final List<String> categories = new ArrayList<String>();
        categories.addAll(Arrays.asList(i.getCategoryPath().split("/")));
        categories.add(0, "Product");
        categories.add(i.getName());
        return categories;
    }
    
    public static List<Token> chooseLevel(final List<Token> tokens, final int level) {
        if (level >= tokens.size()) {
            return tokens;
        }
        return tokens.subList(0, level);
    }
}
