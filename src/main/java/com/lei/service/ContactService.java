package com.lei.service;

import com.lei.dao.ContactMapper;
import com.lei.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by John on 2017/6/14.
 */
@Service
public class ContactService {
    @Autowired
    ContactMapper contactMapper;


    public void save(List<Contact> contactBeanList, String uuid) {
        for (Contact contact : contactBeanList) {
            contact.setUuid(uuid);
            contactMapper.insert(contact);
        }
    }
}
