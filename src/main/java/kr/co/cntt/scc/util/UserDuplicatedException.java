package kr.co.cntt.scc.util;

/**
 * Created by jslivane on 2016. 6. 3..
 */
public class UserDuplicatedException extends RuntimeException {

    String name;

    public UserDuplicatedException(String name) { this.name = name; }

    public String getName() { return name; }

}
