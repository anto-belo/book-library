package com.itechart.lab.service;

import com.itechart.lab.dao.EnumDaoFactory;
import com.itechart.lab.entity.EnumEntity;

public class PublisherServiceImpl extends AbstractEnumEntityService<EnumEntity, String> {
    private static final PublisherServiceImpl instance = new PublisherServiceImpl();

    private PublisherServiceImpl() {
        super(EnumDaoFactory.getPublisherDao());
    }

    public static PublisherServiceImpl getInstance() {
        return instance;
    }
}
