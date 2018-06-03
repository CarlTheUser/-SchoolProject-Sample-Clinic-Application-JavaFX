package data.querying;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public abstract class Query<TResult, TFilter extends Query.Filter>{

    protected List<Filter<TResult>> filters = new ArrayList<>();

    public abstract AbstractList<TResult> execute();

    Query filter(Filter<TResult> filter){
        filters.add(filter);
        return this;
    }

    interface Filter<TParameter>{

        boolean check(TParameter parameter);

    }


}
