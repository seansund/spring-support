package com.ibm.garage_catalyst.logging.support;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public interface SimpleFilter extends Filter {
    @Override
    default void init(FilterConfig filterConfig) {
    }

    @Override
    default void destroy() {
    }
}
