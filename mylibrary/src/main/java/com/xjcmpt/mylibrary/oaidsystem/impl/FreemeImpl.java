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

package com.xjcmpt.mylibrary.oaidsystem.impl;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.os.RemoteException;


import com.xjcmpt.mylibrary.oaidsystem.IGetter;
import com.xjcmpt.mylibrary.oaidsystem.IOAID;
import com.xjcmpt.mylibrary.oaidsystem.OAIDException;
import com.xjcmpt.mylibrary.oaidsystem.OAIDLog;

import repeackage.com.android.creator.IdsSupplier;


public class FreemeImpl implements IOAID {
    private final Context context;

    public FreemeImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.android.creator", 0);
            return pi != null;
        } catch (Exception e) {
            OAIDLog.print(e);
            return false;
        }
    }

    @Override
    public void doGet(final IGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        Intent intent = new Intent("android.service.action.msa");
        intent.setPackage("com.android.creator");
        OAIDService.bind(context, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                IdsSupplier anInterface = IdsSupplier.Stub.asInterface(service);
                if (anInterface == null) {
                    throw new OAIDException("IdsSupplier is null");
                }
                return anInterface.getOAID();
            }
        });
    }

}
