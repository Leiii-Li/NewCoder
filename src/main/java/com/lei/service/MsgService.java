package com.lei.service;

import com.lei.dao.MsgMapper;
import com.lei.model.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by John on 2017/6/14.
 */
@Service
public class MsgService {
    @Autowired
    MsgMapper msgMapper;

    public void save(Msg msgBean, String uuid) {
        msgBean.setUuid(uuid);
        msgMapper.insert(msgBean);
    }

    public void saveList(List<Msg> msgBean, String uuid) {
        for (Msg msg : msgBean) {
            msg.setUuid(uuid);
            msgMapper.insert(msg);
        }
    }
}
