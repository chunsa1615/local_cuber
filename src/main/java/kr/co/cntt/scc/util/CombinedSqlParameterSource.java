package kr.co.cntt.scc.util;

import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Created by jslivane on 2016. 4. 19..
 *
 * http://stackoverflow.com/a/13341287/3614964
 */
public class CombinedSqlParameterSource extends AbstractSqlParameterSource {
    private MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    private BeanPropertySqlParameterSource beanPropertySqlParameterSource;

    public CombinedSqlParameterSource(Object object) {
        this.beanPropertySqlParameterSource = new BeanPropertySqlParameterSource(object);
    }

    public void addValue(String paramName, Object value) {
        mapSqlParameterSource.addValue(paramName, value);
    }

    @Override
    public boolean hasValue(String paramName) {
        return beanPropertySqlParameterSource.hasValue(paramName) || mapSqlParameterSource.hasValue(paramName);
    }

    @Override
    public Object getValue(String paramName) {
        return mapSqlParameterSource.hasValue(paramName) ? mapSqlParameterSource.getValue(paramName) : beanPropertySqlParameterSource.getValue(paramName);
    }

    @Override
    public int getSqlType(String paramName) {
        return mapSqlParameterSource.hasValue(paramName) ? mapSqlParameterSource.getSqlType(paramName) : beanPropertySqlParameterSource.getSqlType(paramName);
    }
}