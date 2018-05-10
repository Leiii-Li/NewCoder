package com.lei.util;

import com.lei.service.SensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

/**
 * Created by John on 2017/5/23.
 */
@Component
public class FilterContent {

    @Autowired
    private SensitiveService sensitiveService;

    public String filter(String txt) {
//        txt = HtmlUtils.htmlEscape(txt);
        txt = sensitiveService.match(txt);
        return txt;
    }
}
