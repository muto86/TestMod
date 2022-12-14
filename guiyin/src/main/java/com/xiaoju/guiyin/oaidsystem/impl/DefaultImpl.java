/*
 * Copyright (c) 2016-present 贵州纳雍穿青人李裕江<1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.xiaoju.guiyin.oaidsystem.impl;


import com.xiaoju.guiyin.oaidsystem.IGetter;
import com.xiaoju.guiyin.oaidsystem.IOAID;
import com.xiaoju.guiyin.oaidsystem.OAIDException;

class DefaultImpl implements IOAID {

    @Override
    public boolean supported() {
        return false;
    }

    @Override
    public void doGet(final IGetter getter) {
        if (getter == null) {
            return;
        }
        getter.onOAIDGetError(new OAIDException("Unsupported"));
    }

}
