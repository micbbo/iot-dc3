/*
 * Copyright 2016-2021 Pnoker. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dc3.api.center.auth.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dc3.api.center.auth.hystrix.TenantClientHystrix;
import com.dc3.common.bean.R;
import com.dc3.common.constant.Common;
import com.dc3.common.dto.TenantDto;
import com.dc3.common.model.Tenant;
import com.dc3.common.valid.Insert;
import com.dc3.common.valid.Update;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotNull;

/**
 * 租户 FeignClient
 *
 * @author pnoker
 */
@FeignClient(path = Common.Service.DC3_AUTH_TENANT_URL_PREFIX, name = Common.Service.DC3_AUTH_SERVICE_NAME, fallbackFactory = TenantClientHystrix.class)
public interface TenantClient {

    /**
     * 新增 Tenant
     *
     * @param tenant Tenant
     * @return Tenant
     */
    @PostMapping("/add")
    R<Tenant> add(@Validated(Insert.class) @RequestBody Tenant tenant);

    /**
     * 根据 ID 删除 Tenant
     *
     * @param id Tenant Id
     * @return Boolean
     */
    @PostMapping("/delete/{id}")
    R<Boolean> delete(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 修改 Tenant
     * <p>
     * 支  持: Enable
     * 不支持: Name
     *
     * @param tenant Tenant
     * @return Tenant
     */
    @PostMapping("/update")
    R<Tenant> update(@Validated(Update.class) @RequestBody Tenant tenant);

    /**
     * 根据 ID 查询 Tenant
     *
     * @param id Tenant Id
     * @return Tenant
     */
    @GetMapping("/id/{id}")
    R<Tenant> selectById(@NotNull @PathVariable(value = "id") Long id);

    /**
     * 根据 Name 查询 Tenant
     *
     * @param name Tenant Name
     * @return Tenant
     */
    @GetMapping("/name/{name}")
    R<Tenant> selectByName(@NotNull @PathVariable(value = "name") String name);

    /**
     * 分页查询 Tenant
     *
     * @param tenantDto Dto
     * @return Page<Tenant>
     */
    @PostMapping("/list")
    R<Page<Tenant>> list(@RequestBody(required = false) TenantDto tenantDto);

}
