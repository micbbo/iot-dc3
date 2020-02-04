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

package com.pnoker.api.device.manager.hystrix;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pnoker.api.device.manager.feign.DicClient;
import com.pnoker.common.bean.R;
import com.pnoker.common.dto.DicDto;
import com.pnoker.common.model.Dic;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>字典 FeignHystrix
 *
 * @author pnoker
 */
@Slf4j
@Component
public class DicClientHystrix implements FallbackFactory<DicClient> {

    @Override
    public DicClient create(Throwable throwable) {
        String message = throwable.getMessage() == null ? "No available server for client: DC3-MANAGER" : throwable.getMessage();
        log.error("DicClientHystrix:{},hystrix服务降级处理", message, throwable);

        return new DicClient() {

            @Override
            public R<Dic> add(Dic dic) {
                return R.fail(message);
            }

            @Override
            public R<Boolean> delete(Long id) {
                return R.fail(message);
            }

            @Override
            public R<Dic> update(Dic dic) {
                return R.fail(message);
            }

            @Override
            public R<Dic> selectById(Long id) {
                return R.fail(message);
            }

            @Override
            public R<Dic> selectByLabelAndType(String label, String type) {
                return R.fail(message);
            }

            @Override
            public R<Page<Dic>> list(DicDto dicDto) {
                return R.fail(message);
            }
        };
    }
}