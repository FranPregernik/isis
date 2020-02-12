/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.extensions.secman.model.dom.role;

import java.util.Collection;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.extensions.secman.api.role.ApplicationRole;
import org.apache.isis.extensions.secman.api.role.ApplicationRole.RemoveUserDomainEvent;
import org.apache.isis.extensions.secman.api.role.ApplicationRoleRepository;
import org.apache.isis.extensions.secman.api.user.ApplicationUser;
import org.apache.isis.extensions.secman.api.user.ApplicationUserRepository;

import lombok.RequiredArgsConstructor;

@Action(domainEvent = RemoveUserDomainEvent.class, associateWith = "users")
@ActionLayout(named="Remove")
@RequiredArgsConstructor
public class ApplicationRole_removeUser {
    
    @Inject private ApplicationRoleRepository applicationRoleRepository;
    @Inject private ApplicationUserRepository applicationUserRepository;
    
    private final ApplicationRole holder;

    @MemberOrder(sequence = "2")
    public ApplicationRole act(final ApplicationUser applicationUser) {
        applicationRoleRepository.removeRoleFromUser(holder, applicationUser);
        return holder;
    }

    public Collection<ApplicationUser> choices0Act() {
        return applicationRoleRepository.getUsers(holder);
    }

    public String validateAct(final ApplicationUser applicationUser) {
        if(applicationUserRepository.isAdminUser(applicationUser) 
                && applicationRoleRepository.isAdminRole(holder)) {
            return "Cannot remove admin user from the admin role.";
        }
        return null;
    }
}