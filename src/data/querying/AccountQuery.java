package data.querying;

import data.model.Account;

import java.util.AbstractList;

public abstract class AccountQuery extends Query<Account, AccountQuery.Filter> {

    interface Filter extends Query.Filter<Account> {

    }
}
