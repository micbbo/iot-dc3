/*
 * Copyright 2019 Pnoker. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pnoker.center.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pnoker.center.auth.mapper.UserMapper;
import com.pnoker.center.auth.service.UserService;
import com.pnoker.common.constant.Common;
import com.pnoker.common.dto.UserDto;
import com.pnoker.common.exception.ServiceException;
import com.pnoker.common.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 用户服务接口实现类
 *
 * @author pnoker
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.USER_ID, key = "#user.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.USER_NAME, key = "#user.name", condition = "#result!=null")
            },
            evict = {@CacheEvict(value = Common.Cache.USER_LIST, allEntries = true, condition = "#result!=null")}
    )
    public User add(User user) {
        User select = selectByName(user.getName());
        if (null != select) {
            throw new ServiceException("user already exists");
        }
        if (userMapper.insert(user) > 0) {
            return userMapper.selectById(user.getId());
        }
        return null;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = Common.Cache.USER_ID, key = "#id", condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.USER_NAME, allEntries = true, condition = "#result==true"),
                    @CacheEvict(value = Common.Cache.USER_LIST, allEntries = true, condition = "#result==true")
            }
    )
    public boolean delete(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = Common.Cache.USER_ID, key = "#user.id", condition = "#result!=null"),
                    @CachePut(value = Common.Cache.USER_NAME, key = "#user.name", condition = "#result!=null")
            },
            evict = {
                    @CacheEvict(value = Common.Cache.USER_LIST, allEntries = true, condition = "#result!=null")
            }
    )
    public User update(User user) {
        user.setUpdateTime(null);
        if (userMapper.updateById(user) > 0) {
            User select = selectById(user.getId());
            user.setName(select.getName());
            return select;
        }
        return null;
    }

    @Override
    @Cacheable(value = Common.Cache.USER_ID, key = "#id", unless = "#result==null")
    public User selectById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    @Cacheable(value = Common.Cache.USER_NAME, key = "#name", unless = "#result==null")
    public User selectByName(String name) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda()
                .eq(User::getName, name)
                .eq(User::getEnable, true);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    @Cacheable(value = Common.Cache.USER_LIST, keyGenerator = "commonKeyGenerator", unless = "#result==null")
    public Page<User> list(UserDto userDto) {
        return userMapper.selectPage(userDto.getPage().convert(), fuzzyQuery(userDto));
    }

    @Override
    public boolean checkUserValid(String name) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda()
                .eq(User::getName, name)
                .eq(User::getEnable, true);
        Integer count = userMapper.selectCount(queryWrapper);
        return count > 0;
    }

    @Override
    public boolean restPassword(Long id) {
        User user = selectById(id);
        if (null != user) {
            user.setPassword(Common.DEFAULT_PASSWORD);
            return null != update(user);
        }
        return false;
    }

    @Override
    public LambdaQueryWrapper<User> fuzzyQuery(UserDto userDto) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>query().lambda();
        Optional.ofNullable(userDto).ifPresent(dto -> {
            if (StringUtils.isNotBlank(dto.getName())) {
                queryWrapper.like(User::getName, dto.getName());
            }
        });
        return queryWrapper;
    }

}
